@echo off
echo =========================================================================
echo   Starting CampusHub - Smart Campus Management System
echo   Java OOP End-Semester Practical Project
echo =========================================================================

REM Add local maven to PATH if not already in system PATH
set "PATH=%PATH%;C:\apache-maven-3.9.6\bin"

echo Executing Maven exec:java ...
mvn exec:java -Dexec.mainClass="com.campushub.Main"
pause
