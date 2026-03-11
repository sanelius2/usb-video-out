@echo off
echo ========================================
echo Building APK locally
echo ========================================
echo.

REM 设置环境变量
set JAVA_HOME=C:\Java\jdk-11.0.2
set ANDROID_HOME=C:\Users\sanelius\AppData\Local\Android\Sdk
set PATH=%PATH%;%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools

cd /d C:\Users\sanelius\Desktop\安卓转c\scrcpy\AndroidApp

echo Checking environment...
echo JAVA_HOME: %JAVA_HOME%
echo ANDROID_HOME: %ANDROID_HOME%
echo.

echo Building Debug APK...
.\gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESS!
    echo ========================================
    echo.
    echo APK location:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.

    if exist app\build\outputs\apk\debug\app-debug.apk (
        echo File size:
        dir app\build\outputs\apk\debug\app-debug.apk | find "app-debug.apk"
        echo.
        echo You can now install this APK on your phone!
    )
) else (
    echo.
    echo ========================================
    echo BUILD FAILED!
    echo ========================================
)

echo.
pause
