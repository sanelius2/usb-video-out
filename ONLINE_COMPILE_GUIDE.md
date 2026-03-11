# 📱 在线编译APK - 指南

由于本地环境可能没有配置Android开发环境，推荐使用以下在线编译平台：

## 🔥 推荐方案1：GitHub Actions（免费）

### 步骤1：上传到GitHub

1. 创建GitHub账号：https://github.com/signup
2. 创建新仓库，命名为 `usb-video-out`
3. 上传 `AndroidApp` 文件夹内容到GitHub

### 步骤2：创建GitHub Actions工作流

在仓库中创建 `.github/workflows/build.yml`：

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build with Gradle
      run: ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### 步骤3：触发编译

- 推送代码到GitHub，会自动编译
- 或在Actions页面点击 "Run workflow"
- 编译完成后下载APK

---

## 🚀 方案2：使用现成的在线编译服务

### Option A: CI/CD 平台

**1. GitLab CI (免费)**
- 将代码上传到GitLab
- 创建 `.gitlab-ci.yml` 文件
- 自动编译并下载APK

**2. Bitbucket Pipelines**
- 免费额度：每月500分钟
- 上传代码自动编译

### Option B: Android在线编译器

**注意：** 大多数在线Android IDE需要复杂配置，不推荐。

---

## 💻 方案3：本地快速安装（推荐）

### Windows 快速安装指南

#### 1. 安装JDK（5分钟）
```
下载: https://adoptium.net/temurin/releases/?version=11
选择: Windows x64 JDK
安装: 直接安装，记住安装路径
```

#### 2. 安装Android SDK命令行工具（10分钟）
```bash
# 下载SDK命令行工具
# https://developer.android.com/studio#command-tools

# 解压到 C:\android-sdk
# 设置环境变量 ANDROID_HOME=C:\android-sdk

# 安装必要组件
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
sdkmanager "platform-tools"
```

#### 3. 双击运行编译脚本
```batch
# 进入项目目录
cd scrcpy

# 双击运行
compile.bat
```

---

## 🌐 方案4：使用Docker（技术用户）

### 使用Docker编译Android APK

```bash
# 拉取Android构建镜像
docker pull openjdk:11-jdk-slim

# 运行编译容器
docker run -it --rm \
  -v %cd%/AndroidApp:/workspace \
  -w /workspace \
  openjdk:11-jdk-slim \
  bash -c "apt-get update && apt-get install -y wget unzip && \
  wget https://dl.google.com/android/repository/commandlinetools-win-9477386_latest.zip && \
  unzip commandlinetools-win-9477386_latest.zip -d cmdline-tools && \
  mkdir -p cmdline-tools/latest && \
  mv cmdline-tools/* cmdline-tools/latest/ && \
  export ANDROID_HOME=$PWD/android-sdk && \
  mkdir -p \$ANDROID_HOME/cmdline-tools/latest && \
  export PATH=\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$PATH && \
  sdkmanager \"platforms;android-34\" \"build-tools;34.0.0\" && \
  chmod +x gradlew && \
  ./gradlew assembleDebug"
```

---

## 📊 方案对比

| 方案 | 优点 | 缺点 | 时间 | 成本 |
|------|------|------|------|------|
| GitHub Actions | 免费、自动、稳定 | 需要GitHub账号 | 5-10分钟 | 免费 |
| Android Studio | 完整环境、调试方便 | 安装耗时（1-2小时） | 首次长 | 免费 |
| Docker命令行 | 快速、无污染 | 需要Docker知识 | 15-30分钟 | 免费 |
| 在线IDE | 无需安装 | 功能限制、不稳定 | 不稳定 | 免费/付费 |

---

## 🎯 推荐流程

### 如果你**第一次**编译：
1. **最简单**：使用 GitHub Actions（免费、自动）
2. **最快**：找有Android开发环境的电脑编译
3. **最完整**：安装Android Studio

### 如果**经常**编译：
1. 安装Android Studio（一次配置，永久使用）
2. 使用 `compile.bat` 一键编译

---

## 📦 编译后验证

编译完成后，检查以下几点：

```bash
# 1. 验证APK文件存在
ls -lh app/build/outputs/apk/debug/app-debug.apk

# 2. 验证APK大小（应该在5-10MB）
# 3. 使用aapt查看APK信息（可选）
aapt dump badging app-debug.apk | grep package

# 4. 安装测试
adb install app-debug.apk
```

---

## ❓ 常见问题

**Q: GitHub Actions编译失败？**
A: 检查build.yml格式，确保GitHub仓库公开或设置Secrets

**Q: 本地编译提示找不到Java？**
A: 安装JDK 11并设置JAVA_HOME环境变量

**Q: 编译很慢？**
A: 第一次编译需要下载依赖，后续会快很多

**Q: APK太大？**
A: Debug APK包含调试信息，Release版会更小

---

## 📞 需要帮助？

如果遇到编译问题，请提供：
1. 错误截图
2. 使用的编译方法
3. 操作系统版本
4. 错误日志

---

## 🎉 快速开始

**最快速的方法（5分钟）：**

1. 上传代码到GitHub
2. 创建GitHub Actions工作流
3. 点击"Run workflow"
4. 等待编译完成
5. 下载APK

就这么简单！🚀
