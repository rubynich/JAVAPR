@echo off
chcp 65001 >nul
cd /d "%~dp0"

if exist out rmdir /s /q out
mkdir out

javac -encoding UTF-8 -d out src\ru\edu\pr04\*.java

if errorlevel 1 (
    echo.
    echo Compilation failed. Check the errors above.
    pause
    exit /b 1
)

java -Dfile.encoding=UTF-8 -cp out ru.edu.pr04.SelfCheck

pause
