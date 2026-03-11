package com.usbvideoout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class VideoOutputService extends Service {
    private static final String TAG = "VideoOutputService";
    private static final String CHANNEL_ID = "VideoOutputChannel";
    private static final int NOTIFICATION_ID = 1;
    
    // 视频编码参数
    private static final int VIDEO_WIDTH = 1920;
    private static final int VIDEO_HEIGHT = 1080;
    private static final int VIDEO_BITRATE = 8000000; // 8 Mbps
    private static final int VIDEO_FPS = 30;
    private static final int IFRAME_INTERVAL = 10;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaCodec encoder;
    private Surface surface;
    private VirtualDisplayWrapper virtualDisplay;

    private UsbManager usbManager;
    private UsbDeviceConnection usbConnection;
    private UsbEndpoint usbEndpoint;
    
    private Handler mainHandler;
    private boolean isRunning = false;
    private StreamCallback callback;

    public interface StreamCallback {
        void onStreamStarted();
        void onStreamError(String error);
        void onStreamStopped();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mainHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    public void start(Context context, Intent projectionData, StreamCallback callback) {
        this.callback = callback;
        
        try {
            // 获取MediaProjection
            mediaProjection = projectionManager.getMediaProjection(projectionData.getIntExtra("resultCode", -1), projectionData.getParcelableExtra("data"));
            
            // 创建视频编码器
            createEncoder();
            
            // 创建虚拟显示器
            createVirtualDisplay();
            
            // 启动前台服务
            startForeground(NOTIFICATION_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
            
            isRunning = true;
            startEncoding();
            
            if (callback != null) {
                callback.onStreamStarted();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "启动失败", e);
            if (callback != null) {
                callback.onStreamError(e.getMessage());
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "视频输出服务",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("USB视频输出")
                .setContentText("正在输出视频...")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build();
    }

    private void createEncoder() {
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, VIDEO_WIDTH, VIDEO_HEIGHT);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_BITRATE);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FPS);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
            
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            surface = encoder.createInputSurface();
            encoder.start();
            
        } catch (IOException e) {
            Log.e(TAG, "创建编码器失败", e);
            throw new RuntimeException("创建编码器失败: " + e.getMessage());
        }
    }

    private void createVirtualDisplay() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags |= DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
        }
        
        virtualDisplay = new VirtualDisplayWrapper(
                mediaProjection,
                "USBVideoOut",
                screenWidth,
                screenHeight,
                metrics.densityDpi,
                surface,
                flags
        );
    }

    private void startEncoding() {
        new Thread(() -> {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            
            while (isRunning) {
                try {
                    int outputBufferId = encoder.dequeueOutputBuffer(bufferInfo, 10000);
                    
                    if (outputBufferId >= 0) {
                        ByteBuffer outputBuffer = encoder.getOutputBuffer(outputBufferId);
                        
                        if (outputBuffer != null) {
                            outputBuffer.position(bufferInfo.offset);
                            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                            
                            byte[] data = new byte[bufferInfo.size];
                            outputBuffer.get(data);
                            
                            // 发送数据到USB设备
                            sendToUsb(data);
                        }
                        
                        encoder.releaseOutputBuffer(outputBufferId, false);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "编码错误", e);
                    if (callback != null) {
                        callback.onStreamError(e.getMessage());
                    }
                    break;
                }
            }
        }).start();
    }

    private void sendToUsb(byte[] data) {
        try {
            // 这里实现USB数据传输
            // 需要根据实际USB设备协议实现
            Log.d(TAG, "发送数据到USB: " + data.length + " bytes");
            
            // 示例：查找USB设备并发送数据
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            for (UsbDevice device : deviceList.values()) {
                if (usbConnection == null) {
                    usbConnection = usbManager.openDevice(device);
                    if (usbConnection != null) {
                        UsbInterface iface = device.getInterface(0);
                        usbConnection.claimInterface(iface, true);
                        
                        // 查找输出端点
                        for (int i = 0; i < iface.getEndpointCount(); i++) {
                            UsbEndpoint endpoint = iface.getEndpoint(i);
                            if (endpoint.getDirection() == UsbEndpoint.DIR_OUT) {
                                usbEndpoint = endpoint;
                                break;
                            }
                        }
                    }
                }
                
                if (usbConnection != null && usbEndpoint != null) {
                    // 分块发送数据
                    int maxPacketSize = usbEndpoint.getMaxPacketSize();
                    for (int offset = 0; offset < data.length; offset += maxPacketSize) {
                        int length = Math.min(maxPacketSize, data.length - offset);
                        usbConnection.bulkTransfer(usbEndpoint, data, offset, length, 1000);
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "USB传输错误", e);
        }
    }

    public void stop() {
        isRunning = false;
        
        if (encoder != null) {
            encoder.stop();
            encoder.release();
            encoder = null;
        }
        
        if (surface != null) {
            surface.release();
            surface = null;
        }
        
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        
        if (usbConnection != null) {
            usbConnection.close();
            usbConnection = null;
        }
        
        if (callback != null) {
            callback.onStreamStopped();
        }
        
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    // VirtualDisplay的包装类
    private static class VirtualDisplayWrapper {
        private VirtualDisplay virtualDisplay;
        
        public VirtualDisplayWrapper(MediaProjection projection, String name, int width, int height,
                                     int density, Surface surface, int flags) {
            virtualDisplay = projection.createVirtualDisplay(name, width, height, density,
                    surface, flags, null, null);
        }
        
        public void release() {
            if (virtualDisplay != null) {
                virtualDisplay.release();
            }
        }
    }
}
