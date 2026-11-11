@echo off
REM start.bat - starts Gemora by double-click (Windows). Needs JDK 17 or newer (javac and java in PATH).
REM It compiles the project and runs Main. Then open http://localhost:8080/
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
    echo Java JDK was not found. Please install JDK 17 or newer, then double-click start.bat again.
    pause
    exit /b 1
)
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib/*" -d out @sources.txt
del sources.txt
if errorlevel 1 (
    echo Compile error. Please check the messages above.
    pause
    exit /b 1
)
java -cp "out;lib/*" com.jewellery.Main
pause
