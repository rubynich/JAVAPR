```bat
@echo off
chcp 65001 >nul
cd /d "%~dp0"

if exist out rmdir /s /q out
if exist sources.txt del sources.txt
mkdir out

for /r src %%f in (*.java) do echo %%f>>sources.txt

javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt

java -Dfile.encoding=UTF-8 -cp out ru.edu.pr02.SelfCheck

echo.
echo Self-check finished. Exit code: %ERRORLEVEL%
pause
```