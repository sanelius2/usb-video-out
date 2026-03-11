package com.usbvideoout;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UsbDeviceActivity extends AppCompatActivity {
    private static final String TAG = "UsbDeviceActivity";
    private static final String ACTION_USB_PERMISSION = "com.usbvideoout.USB_PERMISSION";
    
    private UsbManager usbManager;
    private PendingIntent permissionIntent;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.d(TAG, "USB权限已授予: " + device.getDeviceName());
                            Toast.makeText(context, "USB设备已连接", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.d(TAG, "USB权限被拒绝");
                        Toast.makeText(context, "需要USB权限才能使用", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        permissionIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE);
        
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        
        handleUsbIntent();
    }

    private void handleUsbIntent() {
        UsbDevice device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device != null) {
            if (usbManager.hasPermission(device)) {
                Log.d(TAG, "已拥有USB权限");
                Toast.makeText(this, "USB设备已连接", Toast.LENGTH_SHORT).show();
            } else {
                usbManager.requestPermission(device, permissionIntent);
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }
}
