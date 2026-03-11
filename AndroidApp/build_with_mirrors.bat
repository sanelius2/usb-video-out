@echo off
echo Building APK with Aliyun mirrors...
echo.

set JAVA_HOME=C:\Java\jdk-11.0.2
set PATH=%PATH%;C:\Java\jdk-11.0.2\bin;C:\Users\sanelius\AppData\Local\Android\Sdk\platform-tools

cd /d C:\Users\sanelius\Desktop\安卓转c\scrcpy\AndroidApp

gradlew.bat assembleDebug --init-script=init.gradle --no-daemon

if %ERRORLEVEL% EQU 0 (
    echo.
    echo BUILD SUCCESS!
    echo APK Location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo BUILD FAILED!
    echo.
    echo Try option 1: Run without init script
    echo   gradlew.bat assembleDebug --no-daemon
    echo.
    echo Try option 2: Clean and rebuild
    echo   gradlew.bat clean --init-script=init.gradle
    echo   gradlew.bat assembleDebug --init-script=init.gradle
)

pause
