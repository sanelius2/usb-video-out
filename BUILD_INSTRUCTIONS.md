# Android USB视频输出应用 - 编译说明

## 前置要求

### 必需软件
1. **Java Development Kit (JDK) 11或更高版本**
   - 下载地址: https://www.oracle.com/java/technologies/downloads/
   - 配置环境变量 JAVA_HOME

2. **Android Studio** (可选，但推荐)
   - 下载地址: https://developer.android.com/studio
   - 或者使用命令行工具

3. **Android SDK**
   - 最小API Level: 21 (Android 5.0)
   - 推荐API Level: 34 (Android 14)
   - 包含: Android SDK Build-Tools, Android SDK Platform

### 验证环境
打开命令行，运行以下命令验证：
```bash
java -version
```

## 编译步骤

### 方法一：使用Android Studio（推荐）

1. **打开项目**
   ```
   启动 Android Studio
   选择 "Open an Existing Project"
   选择 scrcpy/AndroidApp 目录
   ```

2. **同步Gradle**
   - 等待Gradle同步完成
   - 如有错误，点击 "Sync Now"

3. **构建APK**
   - 菜单: Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 或直接点击工具栏的运行按钮

4. **查找APK**
   - 构建完成后，点击弹窗中的 "locate"
   - APK位于: `AndroidApp/app/build/outputs/apk/debug/app-debug.apk`

### 方法二：命令行编译

1. **进入项目目录**
   ```bash
   cd c:\Users\sanelius\Desktop\安卓转c\scrcpy\AndroidApp
   ```

2. **下载Gradle Wrapper**（首次）
   ```bash
   # Windows
   gradlew wrapper
   ```

3. **构建Debug APK**
   ```bash
   # Windows
   gradlew.bat assembleDebug
   
   # 或
   gradlew assembleDebug
   ```

4. **构建Release APK**
   ```bash
   # 首先需要配置签名密钥
   # 然后运行
   gradlew.bat assembleRelease
   ```

5. **查找生成的APK**
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release.apk`

## 安装到手机

### 方法一：ADB安装
```bash
adb devices
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 方法二：直接传输
1. 将APK文件复制到手机存储
2. 在手机上点击APK文件安装
3. 如提示"禁止安装未知应用"，在设置中允许安装

## 使用说明

### 硬件要求
- Android 5.0 (API 21) 或更高版本
- 支持USB OTG功能的手机
- USB转HDMI转换器（兼容UVC Video Class）

### 软件要求
- 启用开发者选项
- 启用USB调试

### 使用步骤
1. **连接设备**
   - 将USB转HDMI转换器连接到手机
   - 将HDMI连接到显示器

2. **打开应用**
   - 启动"USB视频输出"应用
   - 确认USB设备已连接

3. **授权屏幕录制**
   - 点击"开始输出"按钮
   - 系统弹出授权对话框
   - 选择"立即开始"
   - 勾选"不再询问"

4. **查看输出**
   - 屏幕内容将通过USB传输到HDMI显示器
   - 应用显示状态为"正在输出..."

5. **停止输出**
   - 点击"停止输出"按钮

## 故障排除

### 问题1：未检测到USB设备
**解决方案：**
- 确认手机支持USB OTG
- 检查USB线缆是否正常
- 尝试更换USB转换器

### 问题2：无法获取屏幕录制权限
**解决方案：**
- 在设置→应用权限中授予屏幕录制权限
- 重启应用

### 问题3：视频输出无画面
**解决方案：**
- 确认显示器输入源正确
- 检查HDMI线缆连接
- 尝试调整分辨率和码率

### 问题4：应用崩溃
**解决方案：**
- 查看Logcat日志
- 提交Issue到GitHub

## 代码签名（Release版本）

### 生成签名密钥
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000
```

### 配置签名
在 `app/build.gradle` 中添加：
```gradle
android {
    signingConfigs {
        release {
            storeFile file("my-release-key.jks")
            storePassword "your-password"
            keyAlias "my-key-alias"
            keyPassword "your-key-password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

## 高级配置

### 修改视频参数
编辑 `VideoOutputService.java`：
```java
private static final int VIDEO_WIDTH = 1920;      // 分辨率宽度
private static final int VIDEO_HEIGHT = 1080;     // 分辨率高度
private static final int VIDEO_BITRATE = 8000000; // 码率 8 Mbps
private static final int VIDEO_FPS = 30;          // 帧率
```

### 支持更多USB设备
编辑 `res/xml/usb_device_filter.xml`，添加设备的VID/PID

## 技术支持

- GitHub Issues: （待创建）
- 技术文档: scrcpy/README.md

## 开源协议

MIT License

## 更新日志

### v1.0.0 (2024)
- 初始版本
- 基本的屏幕捕获功能
- H.264编码
- USB视频输出
