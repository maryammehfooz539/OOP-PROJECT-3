@echo off
REM start-gemora.bat - starts Gemora without the EXE (needs Java 11 or newer). Open http://localhost:8080/
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
    echo Java was not found. Please install Java 11 or newer (https://adoptium.net/), then start again.
    pause
    exit /b 1
)
java -jar gemora.jar
pause
