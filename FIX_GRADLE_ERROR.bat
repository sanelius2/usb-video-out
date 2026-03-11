@echo off
chcp 65001 >nul
title 修复Gradle下载错误

cls
echo ╔══════════════════════════════════════════════════════╗
echo ║                                                        ║
echo ║     🔧 修复Gradle下载超时错误 - 自动修复工具              ║
echo ║                                                        ║
echo ╚══════════════════════════════════════════════════════╝
echo.

echo 📋 检测环境...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未检测到Java
    echo.
    echo 请先安装JDK 11:
    echo https://adoptium.net/temurin/releases/?version=11
    echo.
    pause
    exit /b 1
)
echo ✅ Java已安装
echo.

echo ════════════════════════════════════════════════════════
echo   选择修复方法：
echo ════════════════════════════════════════════════════════
echo.
echo   [1] 修改Gradle镜像源为腾讯云（推荐）⭐
echo       - 快速，稳定
echo       - 无需手动下载
echo.
echo   [2] 修改Gradle镜像源为阿里云
echo       - 备选方案
echo.
echo   [3] 手动下载Gradle（如果镜像也失败）
echo       - 会在浏览器打开下载页面
echo       - 需要手动放置文件
echo.
echo   [4] 配置Gradle使用代理
echo       - 需要VPN/代理
echo.
echo   [5] 配置HTTP代理加速
echo.
echo   [6] 清除Gradle缓存
echo       - 清理缓存后重试
echo.
echo   [0] 退出
echo.
echo ════════════════════════════════════════════════════════
echo.

:menu
set /p choice="请选择 (0-6): "

if "%choice%"=="1" goto tencent
if "%choice%"=="2" goto aliyun
if "%choice%"=="3" goto manual
if "%choice%"=="4" goto proxy
if "%choice%"=="5" goto http_proxy
if "%choice%"=="6" goto clean
if "%choice%"=="0" goto exit
if "%choice%"=="" goto menu

echo.
echo ❌ 无效选择，请重新输入
goto menu

:tencent
echo.
echo ════════════════════════════════════════════════════════
echo   配置腾讯云Gradle镜像
echo ════════════════════════════════════════════════════════
echo.

cd /d "%~dp0AndroidApp"

if not exist "gradle\wrapper" (
    echo ❌ 未找到 gradle\wrapper 目录
    goto pause_exit
)

if not exist "gradle\wrapper\gradle-wrapper.properties" (
    echo ❌ 未找到 gradle-wrapper.properties 文件
    goto pause_exit
)

echo 备份原文件...
copy "gradle\wrapper\gradle-wrapper.properties" "gradle\wrapper\gradle-wrapper.properties.bak" >nul 2>&1

echo 修改Gradle镜像源为腾讯云...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.0-bin.zip
echo networkTimeout=10000
echo validateDistributionUrl=true
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > "gradle\wrapper\gradle-wrapper.properties"

echo.
echo ✅ 已配置腾讯云Gradle镜像
echo.
echo 下一步操作：
echo   1. 打开CMD
echo   2. cd scrcpy/AndroidApp
echo   3. gradlew.bat clean
echo   4. gradlew.bat assembleDebug
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
goto pause_exit

:aliyun
echo.
echo ════════════════════════════════════════════════════════
echo   配置阿里云Gradle镜像
echo ════════════════════════════════════════════════════════
echo.

cd /d "%~dp0AndroidApp"

if not exist "gradle\wrapper\gradle-wrapper.properties" (
    echo ❌ 未找到 gradle-wrapper.properties 文件
    goto pause_exit
)

echo 备份原文件...
copy "gradle\wrapper\gradle-wrapper.properties" "gradle\wrapper\gradle-wrapper.properties.bak" >nul 2>&1

echo 修改Gradle镜像源为阿里云...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://mirrors.aliyun.com/gradle/gradle-8.0-bin.zip
echo networkTimeout=10000
echo validateDistributionUrl=true
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > "gradle\wrapper\gradle-wrapper.properties"

echo.
echo ✅ 已配置阿里云Gradle镜像
echo.
echo 下一步操作：
echo   1. cd scrcpy/AndroidApp
echo   2. gradlew.bat clean
echo   3. gradlew.bat assembleDebug
echo.
goto pause_exit

:manual
echo.
echo ════════════════════════════════════════════════════════
echo   手动下载Gradle
echo ════════════════════════════════════════════════════════
echo.

echo 正在打开下载页面...
echo.

echo 请选择下载源（浏览器会自动打开）：
echo.
echo 腾讯云镜像（推荐）：
start https://mirrors.cloud.tencent.com/gradle/gradle-8.0-bin.zip
echo.

timeout /t 3 >nul

echo 阿里云镜像：
start https://mirrors.aliyun.com/gradle/gradle-8.0-bin.zip
echo.

timeout /t 3 >nul

echo 清华大学镜像：
start https://mirrors.tuna.tsinghua.edu.cn/gradle/gradle-8.0-bin.zip
echo.

echo.
echo ════════════════════════════════════════════════════════
echo   下载后的操作步骤：
echo ════════════════════════════════════════════════════════
echo.
echo 1. 下载完成后，找到 gradle-8.0-bin.zip 文件
echo.
echo 2. 创建文件夹（如果没有）：
echo    %USERPROFILE%\.gradle\wrapper\dists\gradle-8.0-bin\cache\
echo.
echo 3. 将 gradle-8.0-bin.zip 复制到上述文件夹
echo.
echo 4. 在该文件夹中解压 gradle-8.0-bin.zip
echo.
echo 5. 运行编译命令：
echo    cd scrcpy/AndroidApp
echo    gradlew.bat assembleDebug
echo.
echo ════════════════════════════════════════════════════════
echo.
goto pause_exit

:proxy
echo.
echo ════════════════════════════════════════════════════════
echo   配置Gradle代理
echo ════════════════════════════════════════════════════════
echo.

set /p proxy_host="代理地址 (默认127.0.0.1): "
if "%proxy_host%"=="" set proxy_host=127.0.0.1

set /p proxy_port="代理端口 (默认7890): "
if "%proxy_port%"=="" set proxy_port=7890

cd /d "%~dp0AndroidApp"

if not exist "gradle.properties" (
    echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 > gradle.properties
    echo android.useAndroidX=true >> gradle.properties
    echo android.enableJetifier=true >> gradle.properties
)

echo.
echo # HTTP Proxy for Gradle >> gradle.properties
echo systemProp.http.proxyHost=%proxy_host% >> gradle.properties
echo systemProp.http.proxyPort=%proxy_port% >> gradle.properties
echo systemProp.https.proxyHost=%proxy_host% >> gradle.properties
echo systemProp.https.proxyPort=%proxy_port% >> gradle.properties

echo.
echo ✅ 已配置Gradle代理
echo.
echo 代理信息:
echo   地址: %proxy_host%
echo   端口: %proxy_port%
echo.
echo 下一步操作：
echo   cd scrcpy/AndroidApp
echo   gradlew.bat assembleDebug
echo.
goto pause_exit

:http_proxy
echo.
echo ════════════════════════════════════════════════════════
echo   配置HTTP代理加速
echo ════════════════════════════════════════════════════════
echo.

set /p http_proxy="HTTP代理地址 (格式: http://127.0.0.1:7890): "
if "%http_proxy%"=="" goto pause_exit

cd /d "%~dp0AndroidApp"

echo 配置Gradle HTTP代理...
echo GRADLE_OPTS=-Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7890 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7890 > gradle.properties.bak2

echo.
echo ✅ 已配置HTTP代理
echo.
echo 下一步：
echo   set GRADLE_OPTS=-Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7890
echo   gradlew.bat assembleDebug
echo.
goto pause_exit

:clean
echo.
echo ════════════════════════════════════════════════════════
echo   清除Gradle缓存
echo ════════════════════════════════════════════════════════
echo.

cd /d "%~dp0AndroidApp"

echo 清理项目缓存...
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
echo 清理Gradle用户缓存...
if exist "%USERPROFILE%\.gradle\caches" (
    rmdir /s /q "%USERPROFILE%\.gradle\caches" >nul 2>&1
    echo ✅ 已清除 .gradle\caches
)

echo.
echo ✅ 缓存清理完成
echo.
echo 下一步操作：
echo   1. cd scrcpy/AndroidApp
echo   2. gradlew.bat clean
echo   3. gradlew.bat assembleDebug
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
