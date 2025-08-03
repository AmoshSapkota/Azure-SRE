# Azure SRE Demo - Spring Boot Application

A comprehensive Spring Boot application demonstrating Azure Site Reliability Engineering (SRE) practices with Application Insights and Log Analytics Workspace integration.

## 🎯 Features

- ✅ **Azure Application Insights** integration
- ✅ **Azure Log Analytics Workspace** integration  
- ✅ **OpenTelemetry** automatic instrumentation
- ✅ **Custom business metrics** and telemetry
- ✅ **Health endpoints** for monitoring
- ✅ **H2 database** with REST API

## 🚀 Quick Start

### Local Development (Windows)
```cmd
# Run the application with Azure monitoring
run-azure-app.bat
```

### Local Development (Linux/Mac)
```bash
# Make executable and run
chmod +x run-azure-app.sh
./run-azure-app.sh
```

### Azure VM Deployment
```bash
# 1. SSH into your Azure VM
# 2. Clone this repository
git clone https://github.com/AmoshSapkota/Azure-SRE.git
cd Azure-SRE/sredemo

# 3. Run deployment script
chmod +x deploy-to-vm.sh
./deploy-to-vm.sh

# 4. Configure your Azure credentials
nano .env

# 5. Start the application
./run-azure-app.sh
```

## ⚙️ Configuration

### Required Azure Resources
1. **Application Insights** instance
2. **Log Analytics Workspace** (linked to Application Insights)
3. **Azure VM** with Java 21

### Environment Variables
Copy `.env.template` to `.env` and fill in your Azure credentials:

```bash
# Azure Application Insights
APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=xxx...

# Azure Log Analytics Workspace  
LOG_ANALYTICS_WORKSPACE_ID=your-workspace-id
LOG_ANALYTICS_WORKSPACE_KEY=your-workspace-key
```

## 🌐 API Endpoints

Once running, access these endpoints:

| Endpoint | Purpose |
|----------|---------|
| `http://localhost:8080/api/products` | Product REST API |
| `http://localhost:8080/actuator/health` | Health check |
| `http://localhost:8080/actuator/metrics` | Application metrics |
| `http://localhost:8080/h2-console` | Database console |

## 📊 Azure Monitoring

The application automatically sends telemetry to:
- **Application Insights**: Live metrics, application map, performance
- **Log Analytics Workspace**: Structured logs, custom queries (KQL)

## 🏗️ Architecture

```
Spring Boot App → OpenTelemetry Agent → Application Insights → Log Analytics Workspace
```

## 🔒 Security

- Credentials are stored in `.env` file (excluded from Git)
- Use `.env.template` as a reference for required variables
- Never commit actual Azure credentials to source control

## 📋 Requirements

- Java 21+
- Maven 3.6+
- Azure subscription with Application Insights and Log Analytics Workspace
