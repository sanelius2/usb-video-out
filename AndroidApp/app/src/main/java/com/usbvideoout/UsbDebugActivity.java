package com.usbvideoout;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class UsbDebugActivity extends Activity {
    private static final String TAG = "UsbDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setPadding(20, 20, 20, 20);
        textView.setTextSize(14);
        scrollView.addView(textView);
        setContentView(scrollView);

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("=== USB设备调试信息 ===\n\n");

        if (usbManager == null) {
            sb.append("错误: UsbManager为null\n");
        } else {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            sb.append("检测到的设备数量: ").append(deviceList.size()).append("\n\n");

            if (deviceList.isEmpty()) {
                sb.append("未检测到任何USB设备\n\n");
                sb.append("可能的原因:\n");
                sb.append("1. USB-C线缆未连接\n");
                sb.append("2. 外部设备未通电\n");
                sb.append("3. 手机不支持USB Host模式\n");
                sb.append("4. 设备未被识别\n");
            } else {
                for (Map.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
                    UsbDevice device = entry.getValue();
                    sb.append("--- USB设备 ---\n");
                    sb.append("设备名称: ").append(device.getDeviceName()).append("\n");
                    sb.append("设备ID: ").append(entry.getKey()).append("\n");
                    sb.append("厂商ID (VID): 0x").append(String.format("%04X", device.getVendorId())).append("\n");
                    sb.append("产品ID (PID): 0x").append(String.format("%04X", device.getProductId())).append("\n");
                    sb.append("类: ").append(device.getDeviceClass()).append("\n");
                    sb.append("子类: ").append(device.getDeviceSubclass()).append("\n");
                    sb.append("接口数: ").append(device.getInterfaceCount()).append("\n");

                    for (int i = 0; i < device.getInterfaceCount(); i++) {
                        sb.append("  接口 ").append(i).append(": ")
                          .append("class=").append(device.getInterface(i).getInterfaceClass())
                          .append(", subclass=").append(device.getInterface(i).getInterfaceSubclass())
                          .append(", protocol=").append(device.getInterface(i).getInterfaceProtocol())
                          .append(", endpoints=").append(device.getInterface(i).getEndpointCount())
                          .append("\n");
                    }

                    sb.append("有权限: ").append(usbManager.hasPermission(device)).append("\n\n");
                }
            }
        }

        textView.setText(sb.toString());
        Log.d(TAG, sb.toString());
    }
}
