@echo off
REM Batch script to compile all Java files for the Chat Server System

echo ========================================
echo   Compiling Chat Server System
echo ========================================
echo.

REM Check if javac is available
where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: javac not found. Please ensure JDK is installed and in PATH.
    pause
    exit /b 1
)

REM Compile all Java files
echo Compiling Java files...
javac *.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   Compilation Successful!
    echo ========================================
    echo.
    echo To run the application:
    echo   1. Server:  java ChatServer
    echo   2. Client:  java ChatClientGUI
    echo.
) else (
    echo.
    echo ========================================
    echo   Compilation Failed!
    echo ========================================
    echo Please check the error messages above.
    echo.
)

pause
