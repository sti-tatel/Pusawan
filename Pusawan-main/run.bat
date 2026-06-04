@echo off
cd /d "%~dp0"
if not exist Pusawan\class mkdir Pusawan\class
javac -d Pusawan\class Pusawan\*.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
java -cp Pusawan\class Pusawan.Main
pause