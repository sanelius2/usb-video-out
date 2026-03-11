@echo off
chcp 65001 >nul
echo ========================================
echo   修复 Android SDK 错误
echo ========================================
echo.

echo [1/4] 检查Java环境...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未找到Java，请先安装JDK 11
    echo    下载: https://adoptium.net/temurin/releases/?version=11
    pause
    exit /b 1
)
echo ✅ Java已安装
java -version
echo.

echo [2/4] 配置国内镜像源...
if not exist "%USERPROFILE%\.android" mkdir "%USERPROFILE%\.android" >nul 2>&1
(
echo ### Android SDK Repositories - Tencent Mirror
echo repo.url.1=https://mirrors.cloud.tencent.com/AndroidSDK/
echo repo.name.1=Tencent Mirror
) > "%USERPROFILE%\.android\repositories.cfg"
echo ✅ 已配置腾讯云镜像源
echo.

echo [3/4] 清除缓存...
if exist "%USERPROFILE%\.android\cache" (
    rmdir /s /q "%USERPROFILE%\.android\cache" >nul 2>&1
    echo ✅ 已清除缓存
) else (
    echo ℹ️ 无需清除缓存
)
echo.

echo [4/4] 清理项目构建缓存...
cd /d "%~dp0AndroidApp"
if exist ".gradle" (
    rmdir /s /q ".gradle" >nul 2>&1
    echo ✅ 已清除Gradle缓存
)

echo.
echo ========================================
echo   ✅ 修复完成！
echo ========================================
echo.
echo 现在请运行以下命令编译：
echo    compile.bat
echo.
echo 或手动运行：
echo    cd AndroidApp
echo    gradlew.bat assembleDebug
echo.
pause
