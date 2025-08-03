@echo off
REM Azure SRE Demo - Windows Application Runner with Monitoring

echo Starting Azure SRE Demo Application with comprehensive monitoring...
echo.

REM Load environment variables from .env file
echo Loading Azure configuration from .env file...
for /f "usebackq tokens=1,* delims==" %%i in (".env") do (
    if not "%%i"=="" if not "%%i:~0,1%%"=="#" set %%i=%%j
)

REM Create logs directory
if not exist "logs" mkdir logs
echo Created logs directory for Azure Log Analytics integration

REM Download OpenTelemetry agent if not present
if not exist "opentelemetry-javaagent.jar" (
    echo Downloading OpenTelemetry Java agent...
    curl -L -o opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
    if errorlevel 1 (
        echo Failed to download OpenTelemetry agent
        pause
        exit /b 1
    )
    echo OpenTelemetry agent downloaded successfully
) else (
    echo OpenTelemetry agent already present
)

echo.
echo Azure monitoring environment configured successfully!
echo.
echo Your Spring Boot application will now automatically:
echo   - Send telemetry to Application Insights
echo   - Generate structured logs for Log Analytics
echo   - Expose health and metrics endpoints
echo   - Track custom application metrics
echo.

REM Build application
echo Building application...
call mvn clean package -DskipTests

if errorlevel 1 (
    echo Build failed. Please check for errors and try again.
    pause
    exit /b 1
)

echo Build successful
echo.
echo Starting Spring Boot application with Azure monitoring...
echo (OpenTelemetry agent will be automatically attached)
echo.
echo Application endpoints will be available at:
echo   - Application: http://localhost:%SERVER_PORT%/api/products
echo   - Health: http://localhost:%SERVER_PORT%/actuator/health
echo   - Info: http://localhost:%SERVER_PORT%/actuator/info
echo   - Metrics: http://localhost:%SERVER_PORT%/actuator/metrics
echo.

REM Start application (JAVA_TOOL_OPTIONS will be automatically applied)
java -jar target/sredemo-0.0.1-SNAPSHOT.jar

pause
