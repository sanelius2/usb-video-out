# 修复 "Unable to access Android SDK add-on list" 错误

## 错误原因
这个错误通常是因为：
1. 网络问题（无法访问Google服务器）
2. 防火墙/代理设置
3. SDK Manager配置问题

---

## ✅ 解决方案

### 方法1：使用国内镜像源（推荐）⭐

#### 步骤1：配置镜像源

**Android Studio用户：**
1. 打开Android Studio
2. File → Settings (Windows) 或 Android Studio → Preferences (Mac)
3. Appearance & Behavior → System Settings → Android SDK
4. 点击 "SDK Update Sites" 标签
5. 取消勾选所有默认源
6. 点击 "+" 添加以下国内镜像：

```
https://mirrors.cloud.tencent.com/AndroidSDK/
```

或

```
https://mirrors.tuna.tsinghua.edu.cn/android/android-sdk/
```

**命令行用户：**
创建或编辑 `~/.android/repositories.cfg` (Linux/Mac) 或 `%USERPROFILE%\.android\repositories.cfg` (Windows)

添加：
```
### Android SDK Repositories
### Tencent Mirror
repo.url.1=https://mirrors.cloud.tencent.com/AndroidSDK/
repo.name.1=Tencent Mirror

### Tsinghua Mirror
repo.url.2=https://mirrors.tuna.tsinghua.edu.cn/android/android-sdk/
repo.name.2=Tsinghua Mirror
```

#### 步骤2：清除缓存并重试

```bash
# 删除SDK缓存
rm -rf ~/.android/cache  # Linux/Mac
rmdir /s /q "%USERPROFILE%\.android\cache"  # Windows

# 重启Android Studio或重新运行命令
```

---

### 方法2：使用离线SDK

如果你已经下载过SDK，可以直接使用：

#### Windows用户：
1. 下载完整SDK命令行工具：
   - https://dl.google.com/android/repository/commandlinetools-win-9477386_latest.zip

2. 解压到：`C:\android-sdk`

3. 设置环境变量：
   ```batch
   set ANDROID_HOME=C:\android-sdk
   set PATH=%PATH%;%ANDROID_HOME%\cmdline-tools\latest\bin;%ANDROID_HOME%\platform-tools
   ```

4. 直接编译：
   ```batch
   cd scrcpy/AndroidApp
   gradlew.bat assembleDebug --offline
   ```

---

### 方法3：配置代理

如果需要通过代理访问：

#### Android Studio：
1. File → Settings → Appearance & Behavior → System Settings → HTTP Proxy
2. 选择 "Auto-detect proxy settings"
3. 或手动设置：
   - Host: 代理服务器地址
   - Port: 端口
4. 点击 "Check connection" 测试

#### 命令行：
```bash
# Windows
set HTTP_PROXY=http://proxy.example.com:port
set HTTPS_PROXY=http://proxy.example.com:port

# Linux/Mac
export HTTP_PROXY=http://proxy.example.com:port
export HTTPS_PROXY=http://proxy.example.com:port
```

---

### 方法4：忽略SDK更新（快速编译）

如果你只想编译项目，可以跳过SDK更新：

#### 修改 gradle.properties

编辑 `AndroidApp/gradle.properties`，添加：

```properties
# 禁用SDK更新检查
android.builder.sdkDownload=false

# 使用离线模式
org.gradle.offline=true
```

然后编译：
```bash
gradlew.bat assembleDebug --offline
```

#### 注意事项：
- ⚠️ 需要预先安装好SDK
- ⚠️ 离线模式无法下载新依赖

---

### 方法5：使用预配置的Gradle Wrapper

我已经为你配置好了Gradle Wrapper，可以直接编译：

#### 快速编译步骤：

1. **确保Java已安装**
```bash
java -version
```

如果未安装，下载JDK 11：
- https://adoptium.net/temurin/releases/?version=11

2. **直接编译（跳过SDK检查）**
```bash
cd scrcpy/AndroidApp
gradlew.bat assembleDebug --offline --stacktrace
```

3. **如果仍然报错，清理后重试**
```bash
gradlew.bat clean
gradlew.bat assembleDebug --offline
```

---

## 🔍 检查问题

### 验证Java安装
```bash
java -version
# 应该显示 Java 11 或更高版本
```

### 验证网络连接
```bash
ping dl.google.com
# 检查是否能连接Google服务器
```

### 查看详细错误
```bash
gradlew.bat assembleDebug --info --stacktrace
```

---

## 💡 推荐解决方案

### 如果你在国内（中国）：
→ **使用方法1（腾讯镜像）** - 最快最稳定

### 如果你有完整SDK：
→ **使用方法4（离线模式）** - 不需要网络

### 如果只想快速测试：
→ **使用GitHub Actions** - 完全不需要SDK

---

## 🚀 最简单的方案：使用GitHub Actions

**完全跳过SDK问题！**

查看：`GITHUB_UPLOAD_GUIDE.md`

5分钟搞定，无需安装任何东西！

---

## 📞 如果问题仍未解决

请提供以下信息：

1. **错误信息**（完整截图）
2. **你的位置**（国内/国外）
3. **使用的方法**（Android Studio / 命令行 / GitHub Actions）
4. **Java版本**：`java -version`
5. **网络状态**（是否使用代理/VPN）

---

## ✨ 快速修复命令（Windows）

将以下内容保存为 `fix_sdk_error.bat` 并运行：

```batch
@echo off
echo 修复Android SDK错误...
echo.

echo [1] 设置国内镜像源...
if not exist "%USERPROFILE%\.android" mkdir "%USERPROFILE%\.android"
(
echo ### Tencent Mirror
echo repo.url.1=https://mirrors.cloud.tencent.com/AndroidSDK/
echo repo.name.1=Tencent Mirror
) > "%USERPROFILE%\.android\repositories.cfg"

echo [2] 清除缓存...
if exist "%USERPROFILE%\.android\cache" rmdir /s /q "%USERPROFILE%\.android\cache"

echo [3] 清理项目...
cd /d "%~dp0AndroidApp"
call gradlew.bat clean --offline

echo.
echo ✅ 修复完成！
echo 现在运行: compile.bat
pause
```

---

**修复完成后，重新编译即可！** 🎉
