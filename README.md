# Android USB视频输出应用

让不支持原生C口视频输出的Android手机实现视频输出到显示器。

## 项目简介

本项目旨在为没有原生DP（DisplayPort）视频输出功能的Android手机提供视频输出能力。通过捕获手机屏幕内容，使用H.264编码压缩，并通过USB传输到HDMI显示器，实现屏幕镜像功能。

## 功能特性

- ✅ 屏幕内容捕获
- ✅ H.264硬件编码
- ✅ USB视频输出
- ✅ 支持多种分辨率（720p/1080p）
- ✅ 可调节码率和帧率
- ✅ 兼容UVC Video Class设备

## 技术方案

### 核心技术
1. **屏幕捕获**: 使用 `MediaProjectionManager` API
2. **视频编码**: 使用 `MediaCodec` H.264硬件编码
3. **USB通信**: 使用 `UsbManager` 和USB Host API
4. **显示输出**: 通过USB转HDMI转换器（UVC协议）

### 系统要求
- **最低版本**: Android 5.0 (API 21)
- **推荐版本**: Android 10 (API 29) 或更高
- **硬件**: 支持USB OTG
- **外设**: USB转HDMI转换器（兼容UVC）

## 快速开始

### 1. 编译应用
```bash
cd AndroidApp
gradlew assembleDebug
```

详细编译说明请参考 [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)

### 2. 安装APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. 连接设备
1. 将USB转HDMI转换器连接到手机
2. 将HDMI线连接到显示器
3. 打开应用

### 4. 开始使用
1. 点击"开始输出"
2. 授权屏幕录制
3. 查看显示器上的画面

## 项目结构

```
scrcpy/
├── README.md                    # 本文件
├── BUILD_INSTRUCTIONS.md        # 编译说明
├── AndroidApp/                  # Android应用源码
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/usbvideoout/
│   │   │   │   ├── MainActivity.java       # 主界面
│   │   │   │   ├── VideoOutputService.java # 视频输出服务
│   │   │   │   └── UsbDeviceActivity.java # USB设备管理
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── values/
│   │   │   │   └── xml/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   ├── build.gradle
│   └── settings.gradle
└── Docs/                       # 文档目录
```

## 核心模块说明

### MainActivity.java
主界面活动，负责：
- UI初始化和交互
- USB设备检测
- 请求屏幕捕获权限
- 启动/停止视频输出服务

### VideoOutputService.java
视频输出服务，负责：
- 创建MediaProjection（屏幕捕获）
- 创建MediaCodec（H.264编码）
- 创建VirtualDisplay（虚拟显示器）
- USB数据传输

### UsbDeviceActivity.java
USB设备活动，负责：
- USB设备连接处理
- USB权限请求
- 设备枚举

## 配置说明

### 视频参数配置
在 `VideoOutputService.java` 中修改：
```java
private static final int VIDEO_WIDTH = 1920;      // 分辨率宽度
private static final int VIDEO_HEIGHT = 1080;     // 分辨率高度
private static final int VIDEO_BITRATE = 8000000; // 码率 8 Mbps
private static final int VIDEO_FPS = 30;          // 帧率
```

## 技术原理

### 工作流程
```
手机屏幕 → MediaProjection → VirtualDisplay → Surface
                                                      ↓
                                               MediaCodec (H.264编码)
                                                      ↓
                                                     H.264数据流
                                                      ↓
                                                USB传输
                                                      ↓
                                             USB转HDMI转换器
                                                      ↓
                                                   HDMI显示器
```

## 兼容的USB设备

### 推荐的USB转HDMI转换器
- DisplayLink芯片转换器
- UVC Video Class兼容设备
- Silicon Image芯片转换器

### 查询设备信息
连接设备后，应用会显示USB设备的VID/PID，可以用来配置过滤器。

## 使用说明

### 首次使用
1. 开启手机的开发者选项
2. 启用USB调试
3. 安装APK
4. 连接USB转HDMI转换器
5. 打开应用并授权

### 常见问题

**Q: 检测不到USB设备？**
A: 检查手机是否支持USB OTG，尝试更换转换器或线缆。

**Q: 视频卡顿？**
A: 降低码率或分辨率，关闭其他后台应用。

**Q: 没有画面？**
A: 检查HDMI线连接，确认显示器输入源正确。

## 开源协议

MIT License

## 参考项目

- [scrcpy](https://github.com/Genymobile/scrcpy) - Android屏幕镜像工具
- [AndroidUSBCamera](https://github.com/wechat-AmosCC/AndroidUSBCamera) - USB摄像头库

## 更新日志

### v1.0.0
- 初始版本发布
- 基本的屏幕捕获和输出功能
- H.264编码
- USB视频传输
