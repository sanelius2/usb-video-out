# APK编译指南

## 快速编译（使用Android Studio）

### 步骤1：安装Android Studio
1. 下载Android Studio: https://developer.android.com/studio
2. 安装并启动
3. 首次启动时会自动安装SDK和Build Tools

### 步骤2：打开项目
1. 启动Android Studio
2. 选择 "Open an Existing Project"
3. 选择 `scrcpy/AndroidApp` 目录
4. 等待Gradle同步完成（可能需要几分钟）

### 步骤3：构建APK
1. 菜单: Build → Build Bundle(s) / APK(s) → Build APK(s)
2. 等待编译完成
3. 编译成功后会弹出通知，点击 "locate"

### 步骤4：查找APK
APK文件位置：`AndroidApp/app/build/outputs/apk/debug/app-debug.apk`

## 命令行编译（需要配置环境）

### 前置条件
1. 安装JDK 11或更高版本
2. 设置JAVA_HOME环境变量
3. 安装Android SDK
4. 设置ANDROID_HOME环境变量
5. 将 `$ANDROID_HOME/tools` 和 `$ANDROID_HOME/platform-tools` 添加到PATH

### 编译命令

```bash
# 进入项目目录
cd scrcpy/AndroidApp

# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

### 查找生成的APK
- Debug版本: `app/build/outputs/apk/debug/app-debug.apk`
- Release版本: `app/build/outputs/apk/release/app-release.apk`

## 安装到手机

### 方法1：ADB安装
```bash
# 1. 启用USB调试（手机设置 → 开发者选项）
# 2. 连接手机到电脑
adb devices

# 3. 安装APK
adb install app-debug.apk
```

### 方法2：直接安装
1. 将APK文件复制到手机
2. 在手机文件管理器中点击APK
3. 允许安装未知来源应用
4. 完成安装

## 使用方法

### 硬件准备
- USB转HDMI转换器（支持UVC协议）
- HDMI线
- 显示器

### 使用步骤
1. 将USB转HDMI转换器连接到手机（可能需要OTG转接线）
2. 将HDMI线连接到显示器
3. 打开"USB视频输出"应用
4. 点击"开始输出"
5. 授予屏幕录制权限
6. 查看显示器上的画面

### 停止输出
点击应用中的"停止输出"按钮

## 常见问题

### Q: 编译失败？
A: 检查JDK版本和Android SDK配置，确保已安装必要的Build Tools

### Q: USB设备检测不到？
A: 确认手机支持USB OTG，尝试更换转换器

### Q: 没有画面？
A: 检查HDMI连接，确认显示器选择了正确的输入源

### Q: 视频卡顿？
A: 降低分辨率或码率（修改VideoOutputService.java中的参数）

## 代码签名（Release版本）

如需发布到应用商店，需要签名APK：

1. 生成签名密钥：
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000
```

2. 在 `app/build.gradle` 中添加签名配置：
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

3. 构建Release APK：
```bash
gradlew assembleRelease
```
