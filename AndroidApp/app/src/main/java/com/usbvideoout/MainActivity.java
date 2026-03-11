package com.usbvideoout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initManagers();
        checkUsbDevices();
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

    private void checkUsbDevices() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        
        if (deviceList.isEmpty()) {
            tvUsbStatus.setText(R.string.usb_not_found);
            btnStart.setEnabled(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStreaming();
    }
}
