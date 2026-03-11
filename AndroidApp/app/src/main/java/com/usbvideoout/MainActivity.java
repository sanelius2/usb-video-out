package com.usbvideoout;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int SCREEN_CAPTURE_REQUEST_CODE = 100;
    private static final int USB_PERMISSION_REQUEST_CODE = 101;

    private UsbManager usbManager;
    private MediaProjectionManager projectionManager;
    private VideoOutputService videoService;
    private boolean isStreaming = false;

    private TextView tvStatus;
    private TextView tvUsbStatus;
    private TextView tvInfo;
    private Button btnStart;
    private Button btnStop;

    private UsbDevice connectedDevice;
    private UsbPermissionReceiver usbPermissionReceiver;
    private IntentFilter usbPermissionFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);

            initViews();
            initManagers();

            if (usbManager == null) {
                Log.e(TAG, "UsbManager initialization failed");
                Toast.makeText(this, "USB功能不可用", Toast.LENGTH_LONG).show();
            }

            registerUsbPermissionReceiver();
            checkUsbDevices();
        } catch (Exception e) {
            Log.e(TAG, "onCreate error", e);
            Toast.makeText(this, "初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (usbPermissionReceiver != null) {
                unregisterReceiver(usbPermissionReceiver);
                usbPermissionReceiver = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "unregisterReceiver error", e);
        }
        stopStreaming();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvUsbStatus = findViewById(R.id.tvUsbStatus);
        tvInfo = findViewById(R.id.tvInfo);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(v -> requestScreenCapture());
        btnStop.setOnClickListener(v -> stopStreaming());
    }

    private void initManagers() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void registerUsbPermissionReceiver() {
        usbPermissionFilter = new IntentFilter("com.usbvideoout.USB_PERMISSION");
        usbPermissionReceiver = new UsbPermissionReceiver();
        // 使用带flags的方法注册，指定不导出
        registerReceiver(usbPermissionReceiver, usbPermissionFilter, Context.RECEIVER_NOT_EXPORTED);

        // 监听权限授予事件
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        checkUsbDevices(); // 权限授予后重新检查设备
                    }
                },
                new IntentFilter("com.usbvideoout.USB_PERMISSION_GRANTED")
        );
    }

    private void checkUsbDevices() {
        if (usbManager == null) {
            Log.e(TAG, "UsbManager is null");
            tvUsbStatus.setText("USB管理器初始化失败");
            return;
        }

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        Log.d(TAG, "USB设备列表大小: " + deviceList.size());

        if (deviceList.isEmpty()) {
            tvUsbStatus.setText(R.string.usb_not_found);
            tvInfo.setText("未检测到USB设备\n\n请检查:\n1. USB-C线缆是否已连接\n2. 手机是否支持USB Host模式\n3. USB设备是否已通电\n4. 是否已授予USB调试权限");
            connectedDevice = null;
            btnStart.setEnabled(false);
        } else {
            // 检测到设备，请求权限
            UsbDevice firstDevice = deviceList.values().iterator().next();
            connectedDevice = firstDevice;

            // 检查是否已有权限
            if (!usbManager.hasPermission(firstDevice)) {
                tvUsbStatus.setText("检测到设备，请求权限...");
                tvInfo.setText("检测到USB设备:\n");
                tvInfo.append(String.format("%s (VID:0x%04X PID:0x%04X)\n",
                        firstDevice.getDeviceName(),
                        firstDevice.getVendorId(),
                        firstDevice.getProductId()));
                tvInfo.append("\n等待授予USB权限...");
                requestUsbPermission(firstDevice);
            } else {
                tvUsbStatus.setText(R.string.usb_connected);
                tvInfo.setText("检测到USB设备:\n");

                for (Map.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
                    UsbDevice device = entry.getValue();
                    tvInfo.append(String.format("%s (VID:0x%04X PID:0x%04X)\n",
                            device.getDeviceName(),
                            device.getVendorId(),
                            device.getProductId()));
                }

                btnStart.setEnabled(true);
            }
        }
    }

    private void requestUsbPermission(UsbDevice device) {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent("com.usbvideoout.USB_PERMISSION"),
                PendingIntent.FLAG_IMMUTABLE
        );
        usbManager.requestPermission(device, permissionIntent);
    }

    private void requestScreenCapture() {
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, SCREEN_CAPTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCREEN_CAPTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startStreaming(data);
            } else {
                Toast.makeText(this, R.string.screen_permission_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUsbDevices(); // 每次返回时重新检查USB设备
    }

    private void startStreaming(Intent projectionData) {
        try {
            if (videoService == null) {
                videoService = new VideoOutputService();
            }
            
            videoService.start(this, projectionData, new VideoOutputService.StreamCallback() {
                @Override
                public void onStreamStarted() {
                    runOnUiThread(() -> {
                        isStreaming = true;
                        tvStatus.setText(R.string.status_streaming);
                        btnStart.setEnabled(false);
                        btnStop.setEnabled(true);
                        Toast.makeText(MainActivity.this, "视频输出已开始", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onStreamError(String error) {
                    runOnUiThread(() -> {
                        isStreaming = false;
                        tvStatus.setText(R.string.status_error);
                        tvStatus.setText("错误: " + error);
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                        Toast.makeText(MainActivity.this, "错误: " + error, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onStreamStopped() {
                    runOnUiThread(() -> {
                        isStreaming = false;
                        tvStatus.setText(R.string.status_stopped);
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                    });
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(this, "启动失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopStreaming() {
        if (videoService != null && isStreaming) {
            videoService.stop();
        }
    }
}
