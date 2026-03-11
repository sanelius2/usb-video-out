package com.usbvideoout;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

public class UsbPermissionReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbPermissionReceiver";
    private static final String ACTION_USB_PERMISSION = "com.usbvideoout.USB_PERMISSION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
            synchronized (this) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Log.d(TAG, "USB权限已授予: " + device.getDeviceName());
                        Toast.makeText(context, "USB设备已连接", Toast.LENGTH_SHORT).show();

                        // 通知主界面更新状态
                        Intent updateIntent = new Intent("com.usbvideoout.USB_PERMISSION_GRANTED");
                        context.sendBroadcast(updateIntent);
                    } else {
                        Log.d(TAG, "USB权限被拒绝");
                        Toast.makeText(context, "需要USB权限才能使用此功能", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
