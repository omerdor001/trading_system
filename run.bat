@echo off
echo Starting Maven project and Vue app...

:: Start the Maven project in a new command prompt window
start cmd /k "mvn spring-boot:run"

:: Navigate to the Vue app directory
cd /d "online-store"

:: Start the Vue app in a new command prompt window
start cmd /k "npm run serve"