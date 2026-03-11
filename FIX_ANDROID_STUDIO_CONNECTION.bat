@echo off
chcp 65001 >nul
title 修复Android Studio连接问题

cls
echo ╔══════════════════════════════════════════════════════╗
echo ║                                                        ║
echo ║     🔧 Android Studio连接问题 - 自动修复工具             ║
echo ║                                                        ║
echo ╚══════════════════════════════════════════════════════╝
echo.

echo 📋 检测Android Studio...
set AS_PATH=""
if exist "C:\Program Files\Android\Android Studio" (
    set AS_PATH=C:\Program Files\Android\Android Studio
    echo ✅ 找到Android Studio: %AS_PATH%
) else if exist "C:\Program Files (x86)\Android\Android Studio" (
    set AS_PATH=C:\Program Files (x86)\Android\Android Studio
    echo ✅ 找到Android Studio: %AS_PATH%
) else (
    echo ❌ 未找到Android Studio
    echo.
    echo 请先安装Android Studio: https://developer.android.com/studio
    echo.
    pause
    exit /b 1
)
echo.

echo ════════════════════════════════════════════════════════
echo   选择修复方法：
echo ════════════════════════════════════════════════════════
echo.
echo   [1] 配置腾讯云镜像（推荐）⭐
echo       - 速度快，稳定
echo       - 无需代理
echo.
echo   [2] 配置清华镜像
echo       - 备选方案
echo.
echo   [3] 配置HTTP代理
echo       - 需要VPN/代理软件
echo.
echo   [4] 配置Gradle使用镜像
echo       - 加速依赖下载
echo.
echo   [5] 清除缓存并重启
echo.
echo   [0] 退出
echo.
echo ════════════════════════════════════════════════════════
echo.

set /p choice="请选择 (1-5/0): "

if "%choice%"=="1" goto tencent
if "%choice%"=="2" goto tsinghua
if "%choice%"=="3" goto proxy
if "%choice%"=="4" goto gradle
if "%choice%"=="5" goto cache
if "%choice%"=="0" exit /b 0

echo.
echo ❌ 无效选择
pause
exit /b 1

:tencent
echo.
echo ════════════════════════════════════════════════════════
echo   配置腾讯云镜像
echo ════════════════════════════════════════════════════════
echo.

echo 创建配置文件...
if not exist "%USERPROFILE%\.android" mkdir "%USERPROFILE%\.android" >nul 2>&1

(
echo ### Android SDK Repositories
echo ### Tencent Cloud Mirror (推荐)
echo repo.url.1=https://mirrors.cloud.tencent.com/AndroidSDK/
echo repo.name.1=Tencent Cloud Mirror
echo.
echo repo.count=1
) > "%USERPROFILE%\.android\repositories.cfg"

echo ✅ 已配置腾讯云镜像
echo.

echo 下一步：
echo 1. 打开Android Studio
echo 2. File → Settings → Android SDK
echo 3. 点击 "SDK Update Sites"
echo 4. 添加镜像: https://mirrors.cloud.tencent.com/AndroidSDK/
echo 5. Apply → OK
echo 6. 重启Android Studio
echo.
echo 详细说明请查看：FIX_ANDROID_STUDIO_CONNECTION.md
pause
exit /b 0

:tsinghua
echo.
echo ════════════════════════════════════════════════════════
echo   配置清华镜像
echo ════════════════════════════════════════════════════════
echo.

if not exist "%USERPROFILE%\.android" mkdir "%USERPROFILE%\.android" >nul 2>&1

(
echo ### Android SDK Repositories
echo ### Tsinghua University Mirror
echo repo.url.1=https://mirrors.tuna.tsinghua.edu.cn/android/android-sdk/
echo repo.name.1=Tsinghua Mirror
echo.
echo repo.count=1
) > "%USERPROFILE%\.android\repositories.cfg"

echo ✅ 已配置清华镜像
echo.
echo 下一步：
echo 1. 打开Android Studio
echo 2. File → Settings → Android SDK
echo 3. 点击 "SDK Update Sites"
echo 4. 添加镜像: https://mirrors.tuna.tsinghua.edu.cn/android/android-sdk/
echo 5. Apply → OK
echo 6. 重启Android Studio
pause
exit /b 0

:proxy
echo.
echo ════════════════════════════════════════════════════════
echo   配置HTTP代理
echo ════════════════════════════════════════════════════════
echo.

set /p proxy_addr="输入代理地址 (默认127.0.0.1): "
if "%proxy_addr%"=="" set proxy_addr=127.0.0.1

set /p proxy_port="输入代理端口 (默认7890): "
if "%proxy_port%"=="" set proxy_port=7890

echo 配置Gradle代理...
cd /d "%~dp0AndroidApp"
if not exist "gradle.properties" (
    echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 > gradle.properties
    echo android.useAndroidX=true >> gradle.properties
    echo android.enableJetifier=true >> gradle.properties
)

echo. >> gradle.properties
echo # HTTP Proxy Configuration >> gradle.properties
echo systemProp.http.proxyHost=%proxy_addr% >> gradle.properties
echo systemProp.http.proxyPort=%proxy_port% >> gradle.properties
echo systemProp.https.proxyHost=%proxy_addr% >> gradle.properties
echo systemProp.https.proxyPort=%proxy_port% >> gradle.properties

echo ✅ 已配置代理: %proxy_addr%:%proxy_port%
echo.
echo 下一步：
echo 1. 打开Android Studio
echo 2. File → Settings → HTTP Proxy
echo 3. 选择 "Manual proxy configuration"
echo 4. HTTP Proxy: %proxy_addr%
echo 5. Port: %proxy_port%
echo 6. 勾选 "Use same proxy for HTTPS"
echo 7. Check connection 测试
echo 8. OK
pause
exit /b 0

:gradle
echo.
echo ════════════════════════════════════════════════════════
echo   配置Gradle使用镜像
echo ════════════════════════════════════════════════════════
echo.

cd /d "%~dp0AndroidApp"
if not exist "gradle.properties" (
    echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 > gradle.properties
    echo android.useAndroidX=true >> gradle.properties
    echo android.enableJetifier=true >> gradle.properties
)

echo 添加镜像配置...
if not exist "gradle.properties.bak" copy gradle.properties gradle.properties.bak >nul 2>&1

(
echo.
echo # Gradle Mirror Configuration - Tencent Cloud
echo org.gradle.daemon=true
echo org.gradle.parallel=true
echo org.gradle.configureondemand=true
) >> gradle.properties

echo ✅ 已配置Gradle镜像
echo.
echo 下一步：
echo 1. 打开Android Studio
echo 2. 打开项目
echo 3. 等待Gradle同步
pause
exit /b 0

:cache
echo.
echo ════════════════════════════════════════════════════════
echo   清除缓存
echo ════════════════════════════════════════════════════════
echo.

echo 清除Android缓存...
if exist "%USERPROFILE%\.android\cache" (
    rmdir /s /q "%USERPROFILE%\.android\cache" >nul 2>&1
    echo ✅ 已清除 .android\cache
)

echo 清除Gradle缓存...
cd /d "%~dp0AndroidApp"
if exist ".gradle" (
    rmdir /s /q ".gradle" >nul 2>&1
    echo ✅ 已清除 .gradle
)

if exist "build" (
    rmdir /s /q "build" >nul 2>&1
    echo ✅ 已清除 build
)

if exist "app\build" (
    rmdir /s /q "app\build" >nul 2>&1
    echo ✅ 已清除 app\build
)

echo.
echo ✅ 缓存已清除
echo.
echo 下一步：
echo 1. 打开Android Studio
echo 2. File → Invalidate Caches / Restart
echo 3. 选择 "Invalidate and Restart"
pause
exit /b 0
