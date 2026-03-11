@echo off
chcp 65001 >nul
title 获取APK - 快速指南

cls
echo ╔══════════════════════════════════════════════════════╗
echo ║                                                        ║
echo ║     🚀 Android USB视频输出 - 获取APK快速指南            ║
echo ║                                                        ║
echo ╚══════════════════════════════════════════════════════╝
echo.

echo 📋 检测环境...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo    ❌ 未检测到Java
) else (
    echo    ✅ 检测到Java
)
echo.

echo ══════════════════════════════════════════════════════════
echo   选择获取APK的方法：
echo ══════════════════════════════════════════════════════════
echo.
echo   [1] GitHub Actions（推荐）⭐
echo       - 无需安装任何软件
echo       - 5分钟搞定
echo       - 完全免费
echo.
echo   [2] 查看详细教程
echo       - 图文步骤说明
echo       - 适合新手
echo.
echo   [3] 安装Android Studio
echo       - 完整开发环境
echo       - 需要30分钟安装
echo.
echo   [0] 退出
echo.
echo ══════════════════════════════════════════════════════════
echo.

set /p choice="请选择 (1/2/3/0): "

if "%choice%"=="1" goto github
if "%choice%"=="2" goto guide
if "%choice%"=="3" goto android_studio
if "%choice%"=="0" exit /b 0

echo.
echo ❌ 无效选择，请重新运行
pause
exit /b 1

:github
echo.
echo ══════════════════════════════════════════════════════════
echo   🌐 GitHub Actions - 在线编译指南
echo ══════════════════════════════════════════════════════════
echo.
echo 步骤1：注册GitHub账号
echo   打开浏览器访问：https://github.com/signup
echo.
echo 步骤2：创建仓库
echo   - 登录后点击右上角 "+" → "New repository"
echo   - Repository name: usb-video-out
echo   - 选择 Public
echo   - 点击 Create repository
echo.
echo 步骤3：上传代码
echo   - 点击 "uploading an existing file"
echo   - 上传 AndroidApp 文件夹的所有文件
echo   - 点击 Commit changes
echo.
echo 步骤4：自动编译
echo   - 点击 Actions 标签
echo   - 等待 2-5 分钟
echo.
echo 步骤5：下载APK
echo   - 点击最新的工作流
echo   - 下载 app-debug-apk
echo.
echo.
echo 详细教程：打开 GITHUB_UPLOAD_GUIDE.md
echo 快速指南：打开 NO_JAVA_SOLUTION.md
echo.
pause

:guide
echo.
echo ══════════════════════════════════════════════════════════
echo   📚 打开详细教程
echo ══════════════════════════════════════════════════════════
echo.
if exist "GITHUB_UPLOAD_GUIDE.md" (
    start GITHUB_UPLOAD_GUIDE.md
    echo ✅ 已打开：GITHUB_UPLOAD_GUIDE.md
) else (
    echo ❌ 未找到：GITHUB_UPLOAD_GUIDE.md
)
echo.
if exist "NO_JAVA_SOLUTION.md" (
    start NO_JAVA_SOLUTION.md
    echo ✅ 已打开：NO_JAVA_SOLUTION.md
) else (
    echo ❌ 未找到：NO_JAVA_SOLUTION.md
)
echo.
pause

:android_studio
echo.
echo ══════════════════════════════════════════════════════════
echo   💻 安装Android Studio
echo ══════════════════════════════════════════════════════════
echo.
echo 步骤1：下载Android Studio
echo   打开浏览器访问：https://developer.android.com/studio
echo   点击 Download
echo   选择 Windows 版本下载（约 1GB）
echo.
echo 步骤2：安装
echo   - 运行下载的安装程序
echo   - 选择 Standard 安装
echo   - 等待安装完成（约 10-30 分钟）
echo.
echo 步骤3：首次启动
echo   - 接受许可协议
echo   - 等待下载SDK组件
echo   - 创建新项目或导入现有项目
echo.
echo 步骤4：打开项目
echo   - 打开 scrcpy/AndroidApp 文件夹
echo   - 等待 Gradle 同步
echo.
echo 步骤5：编译APK
echo   - Build → Build Bundle(s) / APK(s) → Build APK(s)
echo   - 等待编译完成
echo.
echo.
echo 详细教程：打开 BUILD_GUIDE.md
echo.
start https://developer.android.com/studio
pause
