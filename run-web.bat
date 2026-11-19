@echo off
title CampusHub ERP - Web Application
echo =================================================================
echo             CampusHub ERP - Spring Boot Web Server              
echo =================================================================
echo.
echo [1/3] Ensuring Port 8080 is clean...
powershell -NoProfile -Command "$p = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue; if ($p) { Stop-Process -Id $p.OwningProcess -Force -ErrorAction SilentlyContinue }"

echo [2/3] Starting Spring Boot application server...
echo Chrome will open AUTOMATICALLY once the server is ready!
echo.
echo -----------------------------------------------------------------
echo ALL USERS CAN LOG IN (287 Total Accounts Active!):
echo -----------------------------------------------------------------
echo   1. HOD / Admin:
echo      - Username: admin         Password: Admin@123
echo      - Username: hod_ece       Password: HOD@123
echo.
echo   2. Faculty Members (25 Professors):
echo      - Username: fac_cse_01 to fac_cse_10 (or FAC-CSE-01, etc.)
echo      - Username: fac_ece_01 to fac_ece_06
echo      - Username: fac_bbe_01 to fac_bbe_04
echo      - Username: fac_mca_01 to fac_mca_05
echo      - Password for ALL Faculty: Faculty@123
echo.
echo   3. Students (260 Enrolled Students):
echo      - Username: 25bcs0001 to 25bcs0080 (or 25BCS0001, etc.)
echo      - Username: 24bcs0001 to 24bcs0060
echo      - Username: 25bet0001 to 25bet0050
echo      - Username: 25bbe0001 to 25bbe0040
echo      - Username: 25mca0001 to 25mca0030
echo      - Password for ALL Students: Student@123
echo.
echo Tip: Click "Browse All 287 Users" on the login screen to log in as anyone!
echo Master credentials list saved in: CREDENTIALS_AND_PASSWORDS.txt
echo =================================================================
echo.

:: Launch background watcher that waits until http://localhost:8080 is actually responding before opening browser
start "" /b powershell -NoProfile -Command "for ($i=0; $i -lt 40; $i++) { Start-Sleep -Seconds 1; try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/login' -UseBasicParsing -TimeoutSec 1; if ($r.StatusCode -eq 200) { Start-Process 'http://localhost:8080'; break; } } catch {} }"

echo [3/3] Launching Tomcat... (Press Ctrl+C to stop)
cd /d "%~dp0"
"C:\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
pause
