@echo off
cd /d "%~dp0"
javac Pusawan/*.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)
java Pusawan.Main
pause
