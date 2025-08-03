#!/bin/bash
# Azure VM Deployment Script for SRE Demo
# This script prepares the Azure VM environment for the Spring Boot application

echo "🚀 Azure SRE Demo - VM Deployment Script"
echo "========================================"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Update system packages
echo "📦 Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install Java 21 if not present
if java -version 2>&1 | grep -q "21"; then
    echo "✅ Java 21 already installed"
else
    echo "☕ Installing Java 21..."
    sudo apt install openjdk-21-jdk -y
    
    # Verify installation
    if java -version 2>&1 | grep -q "21"; then
        echo "✅ Java 21 installed successfully"
    else
        echo "❌ Java 21 installation failed"
        exit 1
    fi
fi

# Install Maven if not present
if command_exists mvn; then
    echo "✅ Maven already installed"
else
    echo "🔧 Installing Maven..."
    sudo apt install maven -y
    
    if command_exists mvn; then
        echo "✅ Maven installed successfully"
    else
        echo "❌ Maven installation failed"
        exit 1
    fi
fi

# Install curl if not present (for downloading OpenTelemetry agent)
if command_exists curl; then
    echo "✅ curl already available"
else
    echo "🌐 Installing curl..."
    sudo apt install curl -y
fi

# Download OpenTelemetry agent if not present
if [ -f "opentelemetry-javaagent.jar" ]; then
    echo "✅ OpenTelemetry agent already present"
else
    echo "📥 Downloading OpenTelemetry Java agent..."
    curl -L -o opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
    
    if [ -f "opentelemetry-javaagent.jar" ]; then
        echo "✅ OpenTelemetry agent downloaded successfully"
    else
        echo "❌ Failed to download OpenTelemetry agent"
        exit 1
    fi
fi

# Create .env file from template if it doesn't exist
if [ -f ".env" ]; then
    echo "✅ .env file already exists"
else
    if [ -f ".env.template" ]; then
        echo "⚙️  Creating .env file from template..."
        cp .env.template .env
        echo ""
        echo "🔑 IMPORTANT: Please edit .env file with your actual Azure credentials:"
        echo "   nano .env"
        echo ""
        echo "Required values to update:"
        echo "- APPLICATIONINSIGHTS_CONNECTION_STRING (from your Application Insights)"
        echo "- LOG_ANALYTICS_WORKSPACE_ID (from your Log Analytics Workspace)"  
        echo "- LOG_ANALYTICS_WORKSPACE_KEY (from your Log Analytics Workspace)"
        echo ""
        echo "After updating .env, press Enter to continue..."
        read -p ""
    else
        echo "❌ .env.template not found. Please ensure it exists in the project."
        exit 1
    fi
fi

# Create logs directory
mkdir -p logs
echo "✅ Created logs directory"

# Make run script executable
if [ -f "run-azure-app.sh" ]; then
    chmod +x run-azure-app.sh
    echo "✅ Made run-azure-app.sh executable"
fi

# Display system information
echo ""
echo "🔧 System Information:"
echo "   Java Version: $(java -version 2>&1 | head -1)"
echo "   Maven Version: $(mvn -version 2>&1 | head -1)"
echo "   OS: $(lsb_release -d 2>/dev/null | cut -f2 || uname -s)"
echo ""

# Test Maven build
echo "🔨 Building Spring Boot application..."
if ./mvnw clean package -DskipTests; then
    echo "✅ Build successful!"
    echo ""
    echo "🎯 Azure SRE Demo is ready for deployment!"
    echo ""
    echo "Next steps:"
    echo "1. Ensure your .env file has correct Azure credentials"
    echo "2. Run: ./run-azure-app.sh"
    echo "3. Access your application at: http://localhost:8080/api/products"
    echo "4. Monitor in Azure Application Insights and Log Analytics Workspace"
    echo ""
else
    echo "❌ Build failed. Please check errors above."
    echo ""
    echo "Common issues:"
    echo "- Check internet connectivity for Maven dependencies"
    echo "- Ensure Java 21 is properly installed"
    echo "- Verify all source files are present"
    exit 1
fi

echo "🎉 Deployment preparation complete!"
echo "Run './run-azure-app.sh' to start the application"
