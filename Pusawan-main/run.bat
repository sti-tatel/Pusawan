@echo off
cd /d "%~dp0"
javac -d Pusawan\class Pusawan\*.java
java -cp Pusawan\class Pusawan.Main
pause