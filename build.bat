@echo off
echo Building XILLEN Security Plugin...
echo.

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven from: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo Maven found, starting build...
echo.

REM Clean and build
mvn clean package

if %errorlevel% equ 0 (
    echo.
    echo SUCCESS: Plugin built successfully!
    echo JAR file location: target\xillen-security-2.0.jar
    echo.
    echo Copy this JAR file to your server's plugins folder
    echo.
) else (
    echo.
    echo ERROR: Build failed!
    echo Check the error messages above
    echo.
)

pause
