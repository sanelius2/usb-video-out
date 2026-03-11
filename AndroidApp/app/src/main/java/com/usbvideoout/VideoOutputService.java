package com.usbvideoout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.hardware.usb.UsbConstants;
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
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

    private Socket networkSocket;
    private OutputStream networkOutput;

    // 配置
    private String serverHost = "127.0.0.1";
    private int serverPort = 5555;
    private boolean useNetworkOutput = false; // 默认使用USB，可切换到网络

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

    public void setNetworkOutput(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
        this.useNetworkOutput = true;
        Log.d(TAG, "已设置为网络输出模式: " + host + ":" + port);
    }

    public void setUsbOutput() {
        this.useNetworkOutput = false;
        Log.d(TAG, "已设置为USB输出模式");
    }

    public void start(Context context, Intent projectionData, StreamCallback callback) {
        this.callback = callback;

        try {
            // 获取MediaProjection
            mediaProjection = projectionManager.getMediaProjection(projectionData.getIntExtra("resultCode", -1), projectionData.getParcelableExtra("data"));

            // 如果是网络输出模式，先建立连接
            if (useNetworkOutput) {
                if (!connectToServer()) {
                    throw new RuntimeException("无法连接到服务器: " + serverHost + ":" + serverPort);
                }
            }

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

    private boolean connectToServer() {
        try {
            Log.d(TAG, "正在连接到服务器: " + serverHost + ":" + serverPort);
            networkSocket = new Socket(InetAddress.getByName(serverHost), serverPort);
            networkOutput = networkSocket.getOutputStream();
            Log.d(TAG, "已连接到服务器");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "连接服务器失败", e);
            return false;
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
        String mode = useNetworkOutput ? "网络模式" : "USB模式";
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("USB视频输出")
                .setContentText("正在输出视频... (" + mode + ")")
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
                flags,
                surface
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

                            // 根据模式发送数据
                            if (useNetworkOutput) {
                                sendToNetwork(data, bufferInfo.presentationTimeUs);
                            } else {
                                sendToUsb(data);
                            }
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

    private void sendToNetwork(byte[] data, long timestamp) {
        try {
            if (networkOutput != null) {
                // 发送长度前缀
                DataOutputStream dos = new DataOutputStream(networkOutput);
                dos.writeInt(data.length);
                dos.writeLong(timestamp);
                dos.write(data);
                dos.flush();

                Log.d(TAG, "已发送网络数据: " + data.length + " bytes");
            }
        } catch (IOException e) {
            Log.e(TAG, "网络传输错误", e);
            if (callback != null) {
                callback.onStreamError("网络传输错误: " + e.getMessage());
            }
        }
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
                            if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
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

        if (networkOutput != null) {
            try {
                networkOutput.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭网络输出失败", e);
            }
            networkOutput = null;
        }

        if (networkSocket != null) {
            try {
                networkSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭Socket失败", e);
            }
            networkSocket = null;
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
                                     int density, int flags, Surface surface) {
            virtualDisplay = projection.createVirtualDisplay(name, width, height, density, flags,
                    surface, null, null);
        }

        public void release() {
            if (virtualDisplay != null) {
                virtualDisplay.release();
            }
        }
    }
}
