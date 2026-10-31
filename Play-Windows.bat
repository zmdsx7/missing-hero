@echo off
cd /d "%~dp0"
java -jar MissingHero.jar
if errorlevel 1 pause