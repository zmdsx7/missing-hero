@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac --release 17 -encoding UTF-8 -d out src\main\java\com\missinghero\*.java
if errorlevel 1 exit /b 1
xcopy /E /I /Y src\main\resources\* out\ >nul
jar --create --file MissingHero.jar --main-class com.missinghero.Main -C out .
echo Built MissingHero.jar
