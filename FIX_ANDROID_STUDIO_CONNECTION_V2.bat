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
    echo 如果你还没安装，请先安装Android Studio:
    echo https://developer.android.com/studio
    echo.
    echo 如果已安装，请手动选择安装位置
    echo.
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
echo   [3] 清除Gradle缓存
echo       - 解决缓存问题
echo.
echo   [4] 配置Gradle优化
echo       - 加速编译
echo.
echo   [5] 配置HTTP代理（需要VPN）
echo.
echo   [0] 退出
echo.
echo ════════════════════════════════════════════════════════
echo.

:menu
set /p choice="请选择 (0-5): "

if "%choice%"=="1" goto tencent
if "%choice%"=="2" goto tsinghua
if "%choice%"=="3" goto clean
if "%choice%"=="4" goto gradle
if "%choice%"=="5" goto proxy
if "%choice%"=="0" goto exit
if "%choice%"=="" goto menu

echo.
echo ❌ 无效选择，请重新输入
goto menu

:tencent
echo.
echo ════════════════════════════════════════════════════════
echo   配置腾讯云镜像
echo ════════════════════════════════════════════════════════
echo.

echo 创建Android配置目录...
if not exist "%USERPROFILE%\.android" (
    mkdir "%USERPROFILE%\.android"
    echo ✅ 已创建配置目录
) else (
    echo ✅ 配置目录已存在
)

echo.
echo 配置SDK镜像源...
(
echo ### Android SDK Repositories - Tencent Cloud
echo repo.url.1=https://mirrors.cloud.tencent.com/AndroidSDK/
echo repo.name.1=Tencent Cloud Mirror
echo repo.count=1
) > "%USERPROFILE%\.android\repositories.cfg"

if %errorlevel% equ 0 (
    echo ✅ SDK镜像配置成功
) else (
    echo ❌ SDK镜像配置失败
    goto pause_exit
)

echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo ✅ 配置完成！
echo.
echo 下一步操作：
echo   1. 打开 Android Studio
echo   2. File → Settings (Windows) / Preferences (Mac)
echo   3. Appearance ^& Behavior → System Settings → Android SDK
echo   4. 点击 "SDK Update Sites" 标签
echo   5. 取消勾选所有默认源
echo   6. 添加腾讯云镜像:
echo      URL: https://mirrors.cloud.tencent.com/AndroidSDK/
echo   7. 点击 "Apply" → "OK"
echo   8. 重启 Android Studio
echo   9. 重新打开项目，等待 Gradle 同步
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo 详细教程请查看: FIX_ANDROID_STUDIO_CONNECTION.md
echo.
goto pause_exit

:tsinghua
echo.
echo ════════════════════════════════════════════════════════
echo   配置清华镜像
echo ════════════════════════════════════════════════════════
echo.

echo 创建Android配置目录...
if not exist "%USERPROFILE%\.android" (
    mkdir "%USERPROFILE%\.android"
    echo ✅ 已创建配置目录
)

echo.
echo 配置SDK镜像源...
(
echo ### Android SDK Repositories - Tsinghua University
echo repo.url.1=https://mirrors.tuna.tsinghua.edu.cn/android/android-sdk/
echo repo.name.1=Tsinghua Mirror
echo repo.count=1
) > "%USERPROFILE%\.android\repositories.cfg"

if %errorlevel% equ 0 (
    echo ✅ SDK镜像配置成功
) else (
    echo ❌ SDK镜像配置失败
    goto pause_exit
)

echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo ✅ 配置完成！
echo.
echo 下一步操作：
echo   1. 打开 Android Studio
echo   2. File → Settings → Android SDK
echo   3. 点击 "SDK Update Sites" 标签
echo   4. 添加清华镜像:
echo      URL: https://mirrors.tuna.tsinghua.edu.cn/android/android-sdk/
echo   5. Apply → OK
echo   6. 重启 Android Studio
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
goto pause_exit

:clean
echo.
echo ════════════════════════════════════════════════════════
echo   清除缓存
echo ════════════════════════════════════════════════════════
echo.

cd /d "%~dp0AndroidApp"

echo 正在清除缓存...

if exist ".gradle" (
    rmdir /s /q ".gradle" >nul 2>&1
    echo ✅ 已清除 .gradle 缓存
) else (
    echo ℹ️ .gradle 缓存不存在
)

if exist "build" (
    rmdir /s /q "build" >nul 2>&1
    echo ✅ 已清除 build 缓存
) else (
    echo ℹ️ build 缓存不存在
)

if exist "app\build" (
    rmdir /s /q "app\build" >nul 2>&1
    echo ✅ 已清除 app\build 缓存
) else (
    echo ℹ️ app\build 缓存不存在
)

if exist "%USERPROFILE%\.android\cache" (
    rmdir /s /q "%USERPROFILE%\.android\cache" >nul 2>&1
    echo ✅ 已清除 .android\cache
)

echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo ✅ 缓存清除完成！
echo.
echo 下一步操作：
echo   1. 打开 Android Studio
echo   2. File → Invalidate Caches / Restart
echo   3. 选择 "Invalidate and Restart"
echo   4. 重新打开项目
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
goto pause_exit

:gradle
echo.
echo ════════════════════════════════════════════════════════
echo   配置Gradle优化
echo ════════════════════════════════════════════════════════
echo.

cd /d "%~dp0AndroidApp"

echo 配置gradle.properties...
if not exist "gradle.properties" (
    echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 > gradle.properties
    echo android.useAndroidX=true >> gradle.properties
    echo android.enableJetifier=true >> gradle.properties
)

echo 添加优化配置...
(
echo.
echo # Gradle Performance Optimization
echo org.gradle.daemon=true
echo org.gradle.parallel=true
echo org.gradle.configureondemand=true
echo org.gradle.caching=true
) >> gradle.properties

echo ✅ Gradle优化配置完成
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo ✅ 配置完成！
echo.
echo 已启用以下优化：
echo   - Gradle Daemon（守护进程）
echo   - 并行编译
echo   - 按需配置
echo   - 缓存
echo.
echo 下一步操作：
echo   1. 打开 Android Studio
echo   2. 打开项目
echo   3. 等待 Gradle 同步
echo   4. Build → Build APK(s)
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
goto pause_exit

:proxy
echo.
echo ════════════════════════════════════════════════════════
echo   配置HTTP代理
echo ════════════════════════════════════════════════════════
echo.

echo 请输入你的代理信息:
echo (如果你没有代理/VPN，请选择其他选项)
echo.

set /p proxy_host="代理地址 (默认127.0.0.1): "
if "%proxy_host%"=="" set proxy_host=127.0.0.1

set /p proxy_port="代理端口 (默认7890): "
if "%proxy_port%"=="" set proxy_port=7890

echo.
echo 配置Gradle代理...
cd /d "%~dp0AndroidApp"

if not exist "gradle.properties" (
    echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 > gradle.properties
    echo android.useAndroidX=true >> gradle.properties
    echo android.enableJetifier=true >> gradle.properties
)

echo.
echo # HTTP Proxy Configuration >> gradle.properties
echo systemProp.http.proxyHost=%proxy_host% >> gradle.properties
echo systemProp.http.proxyPort=%proxy_port% >> gradle.properties
echo systemProp.https.proxyHost=%proxy_host% >> gradle.properties
echo systemProp.https.proxyPort=%proxy_port% >> gradle.properties

echo.
echo ✅ 代理配置完成
echo.
echo 配置信息:
echo   代理地址: %proxy_host%
echo   代理端口: %proxy_port%
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo 下一步操作：
echo   1. 打开 Android Studio
echo   2. File → Settings → HTTP Proxy
echo   3. 选择 "Manual proxy configuration"
echo   4. HTTP Proxy: %proxy_host%
echo   5. Port: %proxy_port%
echo   6. 勾选 "Use same proxy for HTTPS"
echo   7. 点击 "Check connection" 测试
echo   8. 如果测试成功，点击 "OK"
echo   9. 重启 Android Studio
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
goto pause_exit

:pause_exit
echo.
echo 按任意键退出...
pause >nul
exit /b 0

:exit
echo.
echo 再见！
timeout /t 2 >nul
exit /b 0
