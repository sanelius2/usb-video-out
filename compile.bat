@echo off
chcp 65001 >nul
echo ========================================
echo   Android USB视频输出 - 一键编译
echo ========================================
echo.

cd /d "%~dp0AndroidApp"

echo [1/5] 检查环境...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Java，请先安装JDK 11或更高版本
    echo    下载地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
java -version
echo.

echo [2/5] 检查Gradle Wrapper...
if not exist gradlew.bat (
    echo ❌ 错误: 未找到gradlew.bat
    pause
    exit /b 1
)
echo ✅ Gradle Wrapper已就绪
echo.

echo [3/5] 清理旧构建...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ⚠ 清理失败，但继续编译...
)
echo.

echo [4/5] 开始编译Debug APK...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ❌ 编译失败！
    pause
    exit /b 1
)
echo.

echo [5/5] 编译完成！
echo.
echo ========================================
echo   ✅ APK位置:
echo   %~dp0AndroidApp\app\build\outputs\apk\debug\app-debug.apk
echo ========================================
echo.

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo 📦 APK文件大小:
    for %%F in ("app\build\outputs\apk\debug\app-debug.apk") do echo    %%~zF 字节

    echo.
    echo 📲 安装方法:
    echo    方法1: adb install app\build\outputs\apk\debug\app-debug.apk
    echo    方法2: 将APK传输到手机，点击安装
    echo.
    explorer /select,"app\build\outputs\apk\debug\app-debug.apk"
)

pause
