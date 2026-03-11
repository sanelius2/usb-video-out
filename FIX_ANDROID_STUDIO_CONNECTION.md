# 🔧 修复Android Studio连接Google问题

## 问题原因
Android Studio需要访问Google服务器下载SDK和依赖，但在国内无法直接访问。

---

## ✅ 解决方案（3选1）

### 方案1：配置腾讯云镜像（最简单）⭐⭐⭐⭐⭐

#### 步骤1：打开Android Studio
双击Android Studio图标启动

#### 步骤2：打开设置
- Windows: `File` → `Settings`
- Mac: `Android Studio` → `Preferences`

#### 步骤3：找到SDK设置
左侧菜单：
```
Appearance & Behavior
  └─ System Settings
      └─ Android SDK
```

#### 步骤4：配置镜像源
点击顶部的 **"SDK Update Sites"** 标签

**取消勾选**所有默认源（打钩的）

**点击 "+"** 添加以下镜像：

```
名称：腾讯云
URL：https://mirrors.cloud.tencent.com/AndroidSDK/
```

或者使用清华镜像：

```
名称：清华大学
URL：https://mirrors.tuna.tsinghua.edu.cn/android/android-sdk/
```

#### 步骤5：应用设置
点击 "Apply" → "OK"

#### 步骤6：重启Android Studio
File → Invalidate Caches / Restart → Restart

#### 步骤7：测试连接
回到设置：
- Android SDK → SDK Platforms
- 点击 "Refresh"
- 如果能显示版本列表，说明成功！

---

### 方案2：使用HTTP代理（如果有VPN）

#### 步骤1：找到你的VPN/代理
- 确认代理端口（通常是 7890, 1080, 10808等）
- 确认协议（HTTP/HTTPS/SOCKS5）

#### 步骤2：配置Android Studio代理

File → Settings → Appearance & Behavior → System Settings → HTTP Proxy

**自动检测：**
- 点击 "Auto-detect proxy settings"
- 如果自动检测成功，点击 "OK"

**手动设置：**
- 选择 "Manual proxy configuration"
- HTTP Proxy: 127.0.0.1
- Port: 你的代理端口（例如7890）
- 勾选 "Use same proxy for HTTPS and FTP"
- 点击 "Check connection" 测试
- 如果显示成功，点击 "OK"

#### 步骤3：在Gradle中也配置代理

编辑 `AndroidApp/gradle.properties`，添加：

```properties
# HTTP Proxy
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=7890
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7890
```

#### 步骤4：重启并测试
重启Android Studio，尝试下载SDK

---

### 方案3：离线安装SDK

如果网络问题严重，可以离线安装：

#### 步骤1：下载完整SDK

使用下载工具下载：
```
https://dl.google.com/android/repository/commandlinetools-win-9477386_latest.zip
```

#### 步骤2：解压SDK
解压到：`C:\android-sdk`

#### 步骤3：手动下载必需组件

从镜像站下载：
- platform-tools
- platforms;android-34
- build-tools;34.0.0

#### 步骤4：配置Android Studio
File → Settings → Appearance & Behavior → System Settings → Android SDK

- Android SDK Location: `C:\android-sdk`
- 点击 "Next" → "Finish"

---

## 🔍 快速测试连接

### 测试方法1：在Android Studio中测试
1. File → Settings → Android SDK
2. 点击 "SDK Platforms" 标签
3. 点击 "Refresh"
4. 如果能显示Android版本列表，说明连接成功

### 测试方法2：命令行测试
打开CMD：
```cmd
ping dl.google.com
```
- 如果能ping通（有响应时间），说明网络正常
- 如果请求超时，说明无法访问

---

## 🚀 验证修复是否成功

### 方法1：尝试编译
1. 打开项目
2. Gradle同步
3. 如果同步成功，说明问题解决

### 方法2：查看日志
在Gradle Console中：
- 如果看到 "BUILD SUCCESSFUL"，说明成功
- 如果看到网络错误，说明问题未解决

---

## 💡 推荐配置（复制粘贴）

### 腾讯云镜像配置（推荐）

在SDK Update Sites中添加：
```
名称：Tencent Cloud
URL：https://mirrors.cloud.tencent.com/AndroidSDK/
```

### 完整的gradle.properties配置

编辑 `AndroidApp/gradle.properties`：

```properties
# org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true

# 使用国内镜像
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true

# 如果有代理，取消注释以下内容
# systemProp.http.proxyHost=127.0.0.1
# systemProp.http.proxyPort=7890
# systemProp.https.proxyHost=127.0.0.1
# systemProp.https.proxyPort=7890
```

---

## ❓ 常见问题

### Q1：配置后仍然无法下载？
**A：**
1. 清除缓存：File → Invalidate Caches / Restart
2. 删除 `.gradle` 文件夹
3. 重新同步项目

### Q2：速度很慢？
**A：**
1. 使用腾讯云或清华镜像
2. 避免高峰时段下载
3. 使用HTTP代理

### Q3：某些组件下载失败？
**A：**
1. 单独下载失败的组件
2. 使用命令行：sdkmanager "组件名"
3. 离线安装组件

### Q4：Gradle同步失败？
**A：**
1. 检查网络连接
2. 配置镜像源
3. 如果有VPN，配置HTTP代理

---

## 🎯 完整操作流程（推荐）

```
1. 打开Android Studio
2. File → Settings → Android SDK
3. 点击 "SDK Update Sites"
4. 取消所有默认源
5. 添加腾讯云镜像：
   https://mirrors.cloud.tencent.com/AndroidSDK/
6. Apply → OK
7. 重启Android Studio
8. 打开项目
9. 等待Gradle同步
10. 编译APK
```

---

## 📞 如果问题仍未解决

请提供以下信息：

1. **错误截图**
2. **使用的方案**（镜像/代理/离线）
3. **错误日志**（Help → Show Log in Explorer）

---

## ⚡ 一键修复脚本（自动配置）

我为你准备了自动配置脚本，双击即可修复！

运行：`FIX_ANDROID_STUDIO_CONNECTION.bat`

---

## ✅ 验证成功标志

配置成功后，你应该看到：

- ✅ Gradle同步成功
- ✅ SDK更新列表能正常显示
- ✅ 可以下载新组件
- ✅ 编译不报网络错误
- ✅ Build输出显示 "BUILD SUCCESSFUL"

---

**配置成功后，就可以编译APK了！** 🎉
