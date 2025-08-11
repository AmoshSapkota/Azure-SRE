# Azure SRE: Java Spring Boot Application with Comprehensive Monitoring & Testing

This repository contains a **production-ready Java Spring Boot application** deployed on Azure Virtual Machine (VM) demonstrating **Azure Site Reliability Engineering (SRE)** best practices with comprehensive monitoring, automated testing, and secure deployment workflows.

## 📋 **Project Overview**

A complete **Azure SRE demonstration** featuring a Spring Boot application with Product CRUD operations, integrated with **Application Insights**, **Log Analytics Workspace**, **OpenTelemetry instrumentation**, and **comprehensive testing suite** (65+ test cases with Mockito). Built for **Azure VM deployment** with automated setup scripts and secure credential management.

## 🎯 **Core Features**

### **Application Architecture**
- ✅ **Spring Boot 3.x** with Java 21 runtime
- ✅ **RESTful Product API** with full CRUD operations (`/api/products`)
- ✅ **PostgreSQL Database** with persistence storage
- ✅ **Spring Data JPA** for data access layer
- ✅ **Spring Boot Actuator** for health checks and metrics
- ✅ **Lombok** for clean entity models

### **Azure Monitoring Integration**
- ✅ **Application Insights** - Real-time application telemetry and live metrics
- ✅ **Log Analytics Workspace** - Centralized log management with KQL queries
- ✅ **OpenTelemetry Java Agent** - Zero-code automatic instrumentation
- ✅ **Custom Business Metrics** - Manual SDK integration (`products.retrieved`, `products.created`, `http.requests.total`)
- ✅ **Distributed Tracing** - End-to-end request flow visibility with custom spans
- ✅ **Structured Logging** - Business context logging for Azure Monitor

### **Comprehensive Testing Suite (65+ Test Cases)**
- ✅ **Unit Tests with Mockito**
  - `ProductTest.java` - Entity validation and Lombok integration (15+ tests)
  - `ProductServiceTest.java` - Business logic with mocked dependencies (18+ tests)
  - `ProductControllerTest.java` - REST API with MockMvc testing (20+ tests)
  - `ProductRepoTest.java` - JPA operations with H2 database (12+ tests)
- ✅ **Integration Tests with TestContainers**
  - `ProductIntegrationTest.java` - End-to-end workflow validation (8+ tests)
  - Real database interactions and HTTP request/response cycles
- ✅ **Test Coverage** across all application layers (Entity, Repository, Service, Controller)
- ✅ **Automated Test Execution** with cross-platform scripts

### **DevOps & Deployment Automation**
- ✅ **Cross-Platform Scripts** (Windows `.bat` & Linux `.sh`)
- ✅ **Azure VM Deployment Automation** (`deploy-to-vm.sh`)
- ✅ **Environment Configuration Management** with `.env.template`
- ✅ **Secure Credential Handling** - No secrets in source control
- ✅ **One-Click Local Development** setup
- ✅ **Git-Ready Structure** with comprehensive `.gitignore`

---

## 🏗️ **Technical Architecture**

```
┌─────────────────────┐    ┌──────────────────────┐    ┌─────────────────────┐
│   Spring Boot App   │───▶│  OpenTelemetry Agent │───▶│ Application Insights│
│   - REST API        │    │  - Auto Instrumentation │    │ - Live Metrics      │
│   - Business Logic  │    │  - Custom Metrics    │    │ - Application Map   │
│   - PostgreSQL Database     │    │  - Distributed Traces│    │ - Performance Data  │
└─────────────────────┘    └──────────────────────┘    └─────────────────────┘
                                                                │
                                                                ▼
                                                    ┌─────────────────────┐
                                                    │ Log Analytics       │
                                                    │ Workspace           │
                                                    │ - KQL Queries       │
                                                    │ - Custom Dashboards │
                                                    │ - Alert Rules       │
                                                    └─────────────────────┘
```

## 🛠️ **Technology Stack**

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Runtime** | Java 21, Spring Boot 3.x | Modern Java application platform |
| **Database** | PostgreSQL Database, Spring Data JPA | Lightweight persistence layer |
| **Testing** | JUnit 5, Mockito, TestContainers | Comprehensive test coverage |
| **Monitoring** | OpenTelemetry, Application Insights | Azure-native observability |
| **Build** | Maven 3.x, Spring Boot Maven Plugin | Build automation and packaging |
| **Deployment** | Bash/Batch Scripts, Azure VM | Cross-platform deployment |

## 📊 **Project Structure**

```
Azure-SRE/
├── sredemo/                           # Main Spring Boot application
│   ├── src/
│   │   ├── main/java/com/project/webapp/
│   │   │   ├── controller/            # REST API controllers
│   │   │   ├── service/               # Business logic with custom telemetry
│   │   │   ├── repository/            # Data access layer
│   │   │   ├── model/                 # Entity models
│   │   │   └── WebappApplication.java # Main application
│   │   ├── test/java/                 # Comprehensive test suite (65+ tests)
│   │   └── resources/
│   │       ├── application.properties # Production configuration
│   │       └── static/ & templates/   # Web assets
│   ├── .env.template                  # Secure credential template
│   ├── .gitignore                     # Security-focused git exclusions
│   ├── pom.xml                        # Maven dependencies + testing
│   ├── run-azure-app.bat              # Windows development runner
│   ├── run-azure-app.sh               # Linux/VM application runner
│   ├── deploy-to-vm.sh                # Complete VM deployment automation
│   ├── opentelemetry-javaagent.jar    # Auto-instrumentation agent
│   └── README.md                      # Detailed setup guide
└── README.md                          # This project overview
```

## 🚀 **Quick Start**

### **Local Development**
```bash
# Windows
cd sredemo
run-azure-app.bat

# Linux/Mac
cd sredemo
chmod +x run-azure-app.sh
./run-azure-app.sh
```

### **Azure VM Deployment**
```bash
# 1. SSH into your Azure VM
# 2. Clone this repository
git clone https://github.com/AmoshSapkota/Azure-SRE.git
cd Azure-SRE/sredemo

# 3. Run automated deployment
chmod +x deploy-to-vm.sh
./deploy-to-vm.sh

# 4. Configure Azure credentials
nano .env

# 5. Start the application
./run-azure-app.sh
```

## ⚙️ **Configuration Requirements**

### **Required Azure Resources**
1. **Application Insights** instance with connection string
2. **Log Analytics Workspace** (linked to Application Insights)
3. **Azure VM** with Java 21 and Maven

### **Environment Variables**
Copy `.env.template` to `.env` and configure:

```bash
# Azure Application Insights
APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=xxx...

# Azure Log Analytics Workspace  
LOG_ANALYTICS_WORKSPACE_ID=your-workspace-id
LOG_ANALYTICS_WORKSPACE_KEY=your-workspace-key

# Application Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
```

## 🧪 **Testing Excellence**

This project features a **comprehensive 65+ test suite** implementing industry best practices:

### **Testing Architecture**
```
Test Coverage Strategy
├── Unit Tests (45+ tests)
│   ├── Entity Layer: ProductTest.java (15+ tests)
│   ├── Service Layer: ProductServiceTest.java (18+ tests) 
│   ├── Controller Layer: ProductControllerTest.java (20+ tests)
│   └── Repository Layer: ProductRepoTest.java (12+ tests)
└── Integration Tests (8+ tests)
    └── End-to-End: ProductIntegrationTest.java
```

### **Testing Technologies**
- **JUnit 5**: Modern testing framework with parameterized tests
- **Mockito**: Mock objects for isolated unit testing
- **TestContainers**: Real database integration testing
- **MockMvc**: REST API testing with HTTP validation
- **Spring Boot Test**: Application context and web layer testing

### **Key Testing Features**
- **Mock Dependency Injection**: Service layer tests with `@Mock` and `@InjectMocks`
- **Database Testing**: Real PostgreSQL database integration with `@DataJpaTest`
- **REST API Validation**: Complete HTTP endpoint testing with MockMvc
- **Exception Handling**: Error scenario coverage and boundary testing
- **Cross-Platform Execution**: Windows (`run-tests.bat`) and Linux (`run-tests.sh`) scripts

### **Run Test Suite**
```bash
# Run all tests with coverage
mvn clean test

# Run specific test categories
mvn test -Dtest=*Test         # Unit tests
mvn test -Dtest=*Integration* # Integration tests

# Cross-platform test execution
# Windows: run-tests.bat
# Linux: ./run-tests.sh
```

## 📈 **Azure Monitoring & Observability**

### **OpenTelemetry Integration**
- **Hybrid Approach**: Combines automatic Java Agent instrumentation with manual SDK
- **Custom Telemetry**: Business metrics tracked in service layer methods
- **Distributed Tracing**: Full request lifecycle visibility across all application layers
- **Performance Metrics**: Response times, throughput, error rates automatically collected

### **Application Insights Features**
- **Real-time Monitoring**: Live application performance metrics
- **Application Map**: Visual service dependency mapping
- **Custom Dashboards**: KQL-powered analytics and alerting
- **Exception Tracking**: Automatic error detection and stack trace collection

### **Monitoring Setup**
1. **Application Insights** provides APM capabilities
2. **Log Analytics Workspace** enables custom KQL queries  
3. **OpenTelemetry Agent** handles automatic instrumentation
4. **Custom Metrics** track business KPIs via manual SDK

## 🔧 **DevOps & Deployment**

### **Cross-Platform Support**
- **Windows Development**: `run-azure-app.bat` for local testing
- **Linux Production**: `run-azure-app.sh` for VM deployment
- **Automated VM Setup**: `deploy-to-vm.sh` handles complete environment configuration

### **Security Features**
- **Environment Templates**: `.env.template` prevents credential exposure
- **Git Security**: `.gitignore` excludes sensitive files and dependencies
- **Secure Deployment**: Credentials injected at runtime, never committed

### **Deployment Automation**
```bash
# Complete VM deployment pipeline
./deploy-to-vm.sh
├── Java 21 installation
├── Maven setup  
├── Application compilation
├── OpenTelemetry agent configuration
├── Environment variable setup
└── Application startup
```

---

## 📚 **Detailed Implementation Guide**

---

## Part 1: Azure Built-in Logs, Metrics, and Dashboards

### Objective
Deploy a Java Spring Boot application on an Azure VM, configure diagnostic settings to collect logs, monitor metrics, create a custom dashboard, and set up metric-based and log-query-based alerts.

### Steps

1. **Provision Azure VM**:
   - Create VM in East US (Ubuntu 22.04, Standard_D2s_v3).
   - Enable public IP, open ports 80 (HTTP), 8080 (Spring Boot).
   - _Screenshot: VM creation confirmation._

2. **Deploy Spring Boot Application**:
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk maven -y
   git clone <repository-url>
   cd <repository-folder>
   mvn clean package
   java -jar target/<app-name>.jar
