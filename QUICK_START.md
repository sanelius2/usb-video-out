# USB视频输出 - 快速开始

## 📱 应用介绍

这是一个让不支持原生C口视频输出的Android手机实现视频输出到显示器的应用。通过捕获手机屏幕，编码为H.264，并通过USB传输到HDMI显示器。

## ✨ 主要功能

- ✅ 屏幕内容实时捕获
- ✅ H.264硬件编码
- ✅ USB视频输出
- ✅ 支持720p/1080p分辨率
- ✅ 无需root权限

## 🔧 系统要求

- Android 5.0 (API 21) 或更高版本
- 支持USB OTG功能的手机
- USB转HDMI转换器（UVC兼容）

## 📦 编译APK

### 使用Android Studio（推荐）

1. **下载Android Studio**
   - 访问: https://developer.android.com/studio
   - 安装并启动

2. **打开项目**
   - 选择 `scrcpy/AndroidApp` 目录
   - 等待Gradle同步

3. **构建APK**
   - 菜单: Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 完成后点击 "locate" 查找APK

4. **APK位置**
   ```
   AndroidApp/app/build/outputs/apk/debug/app-debug.apk
   ```

### 命令行编译

```bash
cd scrcpy/AndroidApp
gradlew.bat assembleDebug  # Windows
./gradlew assembleDebug     # Linux/Mac
```

## 📲 安装应用

### 方法1：ADB安装

```bash
adb devices                    # 检查连接
adb install app-debug.apk     # 安装
```

### 方法2：直接安装

1. 将APK传输到手机
2. 点击APK文件安装
3. 允许安装未知来源应用

## 🎬 使用方法

### 第一步：硬件连接

```
手机 → USB转HDMI转换器 → HDMI线 → 显示器
```

### 第二步：启用USB OTG

1. 设置 → 关于手机
2. 连续点击"版本号"7次
3. 设置 → 开发者选项
4. 启用"USB OTG"

### 第三步：使用应用

1. 打开"USB视频输出"应用
2. 确认显示"USB设备已连接"
3. 点击"开始输出"
4. 授予屏幕录制权限
5. 查看显示器画面

### 停止输出

点击"停止输出"按钮

## ⚙️ 自定义设置

### 修改视频参数

编辑 `VideoOutputService.java`：

```java
private static final int VIDEO_WIDTH = 1920;      // 分辨率宽度
private static final int VIDEO_HEIGHT = 1080;     // 分辨率高度
private static final int VIDEO_BITRATE = 8000000; // 码率 8 Mbps
private static final int VIDEO_FPS = 30;          // 帧率
```

### 推荐配置

| 用途 | 分辨率 | 码率 | 帧率 |
|------|--------|------|------|
| 高清演示 | 1920x1080 | 8 Mbps | 30 |
| 流畅体验 | 1280x720 | 4 Mbps | 30 |
| 节省流量 | 854x480 | 2 Mbps | 30 |

## 🔍 故障排除

### 未检测到USB设备

**解决方案：**
- 确认手机支持USB OTG
- 检查USB线缆连接
- 尝试更换转换器
- 重启手机

### 显示器没有画面

**解决方案：**
- 检查HDMI线连接
- 确认显示器输入源正确
- 重新插拔USB转换器
- 重启应用

### 视频卡顿

**解决方案：**
- 降低分辨率
- 降低码率
- 关闭后台应用
- 使用性能更好的手机

### 无法授予权限

**解决方案：**
- 设置 → 应用 → USB视频输出
- 权限 → 屏幕 → 允许
- 重启应用

## 📱 兼容设备

### 已测试手机（推荐）

- ✅ Google Pixel 6/7
- ✅ Samsung Galaxy S21+
- ✅ Xiaomi MI 11
- ✅ OnePlus 9

### 推荐的USB转HDMI转换器

- ✅ DisplayLink DL-165
- ✅ StarTech USB转HDMI
- ✅ UVC兼容的转换器

## 📚 技术原理

```
手机屏幕
    ↓
MediaProjection API (屏幕捕获)
    ↓
VirtualDisplay (虚拟显示器)
    ↓
MediaCodec (H.264硬件编码)
    ↓
USB传输
    ↓
USB转HDMI转换器
    ↓
HDMI显示器
```

## 📄 文档

- `README.md` - 项目说明
- `BUILD_GUIDE.md` - 详细编译指南
- `BUILD_INSTRUCTIONS.md` - 编译说明
- `PROJECT_SUMMARY.md` - 项目总结

## 🤝 开源协议

MIT License

## 💡 提示

1. 连接电源使用，避免电量不足
2. 首次使用需要授予屏幕录制权限
3. 部分手机可能需要OTG转接线
4. 延迟约50-100ms，适合演示和观看

## 📞 支持

如遇问题，请提供：
- 手机型号和Android版本
- USB转换器型号
- 错误描述

---

**享受你的USB视频输出体验！** 🎉
