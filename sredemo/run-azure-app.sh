#!/bin/bash
# Azure SRE Demo - Application Runner with Monitoring

echo "🚀 Starting Azure SRE Demo Application with monitoring..."

# Load environment variables from .env file
if [ -f ".env" ]; then
    export $(cat .env | grep -v '^#' | xargs)
    echo "✅ Azure configuration loaded from .env"
else
    echo "❌ .env file not found!"
    echo "Please create .env file from .env.template with your Azure credentials"
    exit 1
fi

# Create logs directory
mkdir -p logs
echo "📁 Created logs directory for Azure Log Analytics integration"

# Download OpenTelemetry agent if not present
if [ ! -f "opentelemetry-javaagent.jar" ]; then
    echo "📥 Downloading OpenTelemetry Java agent..."
    wget -O opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
    if [ $? -ne 0 ]; then
        echo "❌ Failed to download OpenTelemetry agent"
        exit 1
    fi
    echo "✅ OpenTelemetry agent downloaded successfully"
else
    echo "✅ OpenTelemetry agent already present"
fi

echo ""
echo "📦 Building application..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
    echo ""
    echo "🌟 Starting Spring Boot application with Azure monitoring..."
    echo "   (OpenTelemetry agent will be automatically attached)"
    echo ""
    echo "🔗 Application endpoints will be available at:"
    echo "   - Application: http://localhost:$SERVER_PORT/api/products"
    echo "   - Health: http://localhost:$SERVER_PORT/actuator/health"
    echo "   - Info: http://localhost:$SERVER_PORT/actuator/info"
    echo "   - Metrics: http://localhost:$SERVER_PORT/actuator/metrics"
    echo ""
    
    # Start the application (agent will be automatically attached via JAVA_TOOL_OPTIONS)
    java -jar target/sredemo-0.0.1-SNAPSHOT.jar
else
    echo "❌ Build failed. Please check for errors and try again."
    exit 1
fi
