@echo off
chcp 65001 >nul
echo ========================================
echo   Android USB视频输出 - 离线编译
echo ========================================
echo.

cd /d "%~dp0AndroidApp"

echo [1/4] 检查Java...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Java
    echo.
    echo 请先安装JDK 11:
    echo https://adoptium.net/temurin/releases/?version=11
    echo.
    pause
    exit /b 1
)
echo ✅ Java已就绪
java -version
echo.

echo [2/4] 配置Gradle...
echo org.gradle.offline=true > gradle.properties
echo org.gradle.daemon=false >> gradle.properties
echo org.gradle.configureondemand=true >> gradle.properties
echo ✅ 已配置离线模式
echo.

echo [3/4] 清理旧构建...
if exist ".gradle" rmdir /s /q ".gradle" >nul 2>&1
if exist "build" rmdir /s /q "build" >nul 2>&1
if exist "app\build" rmdir /s /q "app\build" >nul 2>&1
echo ✅ 清理完成
echo.

echo [4/4] 开始编译（离线模式）...
echo.
echo ℹ️ 注意：首次离线编译可能会失败
echo    如果失败，请使用在线编译或GitHub Actions
echo.
echo    查看在线编译方法：GITHUB_UPLOAD_GUIDE.md
echo.

call gradlew.bat assembleDebug --offline --stacktrace

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   ✅ 编译成功！
    echo ========================================
    echo.
    echo APK位置:
    echo   app\build\outputs\apk\debug\app-debug.apk
    echo.

    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo 打开文件所在目录...
        explorer /select,"app\build\outputs\apk\debug\app-debug.apk"
    )
) else (
    echo.
    echo ========================================
    echo   ❌ 编译失败
    echo ========================================
    echo.
    echo 可能的原因：
    echo   1. 缺少Android SDK依赖
    echo   2. 离线模式无法下载必要组件
    echo.
    echo 推荐解决方案：
    echo   1. 运行 fix_sdk_error.bat 修复SDK问题
    echo   2. 使用GitHub Actions在线编译（推荐）
    echo   3. 查看 ANDROID_SDK_ERROR_FIX.md 了解更多
    echo.
    echo 查看: GITHUB_UPLOAD_GUIDE.md
)

echo.
pause
