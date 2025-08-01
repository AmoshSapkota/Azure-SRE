ssss# Azure SRE: Java Spring Boot Application with Monitoring, Logging, and Backup

This repository contains a Java Spring Boot application deployed on an Azure Virtual Machine (VM) to demonstrate Azure monitoring, logging, and backup capabilities as per the project requirements.

The project is divided into three parts, executed serially:
1. Azure Built-in Logs, Metrics, and Dashboards
2. Custom Logs, Metrics, and Traces
3. Azure Backup & Site Recovery

All parts use a single compute service (Azure VM) and a single Java Spring Boot application with CRUD operations.

---

## Table of Contents
- [Prerequisites](#prerequisites)
- [Part 1: Azure Built-in Logs, Metrics, and Dashboards](#part-1-azure-built-in-logs-metrics-and-dashboards)
- [Part 2: Custom Logs, Metrics, and Traces](#part-2-custom-logs-metrics-and-traces)
- [Part 3: Azure Backup & Site Recovery](#part-3-azure-backup--site-recovery)
- [Cleanup](#cleanup)
- [References](#references)

---

## Prerequisites

- **Azure Subscription**: Active subscription with permissions to create VMs, Log Analytics Workspace, Application Insights, Backup Vault, and Site Recovery Vault.

- **Development Environment**:
  - Java 17 or later
  - Maven
  - IDE (e.g., IntelliJ IDEA, Eclipse)
  - Git
  - Azure CLI or Azure Portal access

- **Tools**:
  - SSH client (e.g., PuTTY, OpenSSH)
  - PowerShell (for cleanup scripts)

- **Spring Boot Application**: Basic CRUD application (e.g., managing a `User` entity)

- **Regions**:
  - Primary: East US
  - Secondary: West US 3

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
