@echo off
REM build-jar.bat - makes gemora.jar from the source code (needed only after you change Java code).
REM Needs JDK 11 or newer. Pictures, pages (webapp) and data do NOT need a new jar.
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
    echo JDK was not found. Please install JDK 11 or newer, then run this file again.
    pause
    exit /b 1
)
if exist build rmdir /s /q build
mkdir build\classes
dir /s /b src\*.java > sources.txt
javac --release 11 -encoding UTF-8 -cp "lib/*" -d build\classes @sources.txt
del sources.txt
if errorlevel 1 (
    echo Compile error. Please check the messages above.
    pause
    exit /b 1
)
jar cfm gemora.jar manifest.txt -C build\classes .
rmdir /s /q build
echo gemora.jar is ready
pause
