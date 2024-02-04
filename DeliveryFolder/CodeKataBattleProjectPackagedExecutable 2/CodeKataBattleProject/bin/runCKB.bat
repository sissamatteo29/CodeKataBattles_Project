@echo off
setlocal enabledelayedexpansion

REM Check if the absolute path parameter is provided
if "%1"=="" (
    echo Usage: ^<script^> ^<abs_path^> ^<github_token^> ^<sonarQube_token^>
    exit /b 1
)

if "%2"=="" (
    echo Usage: ^<script^> ^<abs_path^> ^<github_token^> ^<sonarQube_token^>
    exit /b 1
)

if "%3"=="" (
    echo Usage: ^<script^> ^<abs_path^> ^<github_token^> ^<sonarQube_token^>
    exit /b 1
)



REM Set the absolute path provided as "app.base.dir" Java property
set "ABSOLUTE_PATH=%1"

REM Set the root directory of the project
set "PROJECT_ROOT=%~dp0\.."


echo Launching gateway_microservice
start "gateway_microservice" cmd /c "java -Dapp.base.dir=!ABSOLUTE_PATH! -jar !PROJECT_ROOT!\gateway_microservice\gateway_microservice.jar"
 


echo Launching tournament_microservice
start "tournament_microservice" cmd /c "java -Dapp.base.dir=!ABSOLUTE_PATH! -jar !PROJECT_ROOT!\tournament_microservice\tournament_microservice.jar"



echo Launching score_computation_microservice
start "score_computation_microservice" cmd /c "java -Dapp.base.dir=!ABSOLUTE_PATH! -jar !PROJECT_ROOT!\score_computation_microservice\score_computation_microservice.jar --githubDownloader.PersonalAccessToken=%2 --sonarQube.accessToken=%3"



echo Launching user_microservice
start "user_microservice" cmd /c "java -Dapp.base.dir=!ABSOLUTE_PATH! -jar !PROJECT_ROOT!\user_microservice\user_microservice.jar"



echo Launching notification_microservice
start "notification_microservice" cmd /c "java -Dapp.base.dir=!ABSOLUTE_PATH! -jar !PROJECT_ROOT!\notification_microservice\notification_microservice.jar"
 


echo Launching github_integration_microservice
start "github_integration_microservice" cmd /c "java -Dapp.base.dir=!ABSOLUTE_PATH! -jar !PROJECT_ROOT!\github_integration_microservice\github_integration_microservice.jar --githubDownloader.PersonalAccessToken=%2"
REM path to configuration file: --spring.config.location=file:!PROJECT_ROOT!\github_integration_microservice\application.yaml



echo Launching battle_microservice
start "battle_microservice" cmd /c "java -Dapp.base.dir=!ABSOLUTE_PATH! -jar !PROJECT_ROOT!\battle_microservice\battle_microservice.jar"



echo All microservices launched successfully.
exit /b 0
