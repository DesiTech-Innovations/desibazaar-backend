VyaparSathi Backend
===================

### Overview

This is the backend for **VyaparSathi**, a powerful, offline-first shop management application. It is designed to serve as the core business logic for shops in India, particularly in village and city environments. The system handles all critical aspects of retail business management, including sales, inventory, customer relationships, and financial reporting.

### Features

*   **Offline-First Architecture**: Uses a local SQLite database for uninterrupted operation, with future plans for data synchronization.

*   **Secure Authentication**: Implements a robust JSON Web Token (**JWT**) based authentication system to secure API endpoints.

*   **Comprehensive Inventory Management**: Tracks items, their variants, and stock levels, including purchase orders and supplier information.

*   **Sales and Billing**: Facilitates sales transactions, calculates GST, and generates professional PDF invoices using **OpenPDF**.

*   **Customer & Supplier Management**: Manages detailed information for both customers and suppliers.

*   **Financial Tracking**: Records and manages business expenses and customer credit balances through a dedicated ledger. **NEW**: Enhanced with proper separation between operational expenses and inventory purchases, plus COGS calculation for accurate profit reporting.

*   **Advanced Financial Reports**: Provides comprehensive reports including sales summaries, daily reports, and GST breakdowns. **NEW**: Features corrected net revenue calculation, COGS tracking, and outstanding receivables with clear distinction between inventory costs and operational expenses.

*   **API Documentation**: Provides clear and interactive API documentation with **Swagger UI**.

*   **Database Migrations**: Manages database schema evolution using **Flyway**.


### Technologies

*   **Java 17**: The primary programming language.

*   **Spring Boot 3.x**: The application framework for building the backend services.

*   **SQLite**: The embedded database for offline-first data storage.

*   **Spring Data JPA & Hibernate**: For database interaction and object-relational mapping.

*   **Spring Security**: Handles user authentication and authorization.

*   **JWT (jjwt)**: For creating and verifying secure access tokens.

*   **Flyway**: Manages database schema versions and migrations.

*   **OpenPDF**: For generating professional PDF invoices.

*   **Springdoc OpenAPI**: Automatically generates and serves API documentation.

*   **Lombok**: Reduces boilerplate code in Java classes.

*   **MapStruct**: A code generator for creating type-safe mappers.


### Running the Project

#### Prerequisites

*   Java Development Kit (JDK) 17 or higher

*   Maven 3.8+


#### Run Locally

1.  Clone the repository.

2.  Navigate to the project root directory in your terminal.

3.  mvn clean spring-boot:run


#### API Documentation

Once the application is running, you can access the API documentation:

*   **Swagger UI**: http://localhost:8080/swagger-ui.html

*   **OpenAPI JSON**: http://localhost:8080/v3/api-docs


### Project Structure

The project is organized into logical modules, each handling a specific business function.

*   auth: Manages user authentication, roles, and password reset tokens.

*   shop: Handles shop-related data and configuration.

*   catalog: Deals with item and product variant definitions.

*   inventory: Manages suppliers, purchase orders, and stock entries.

*   customer: Manages customer information and their financial ledger.

*   sales: Handles sales transactions and billing, including invoice generation.

*   expense: Tracks and manages operational business expenses. **IMPORTANT**: Now includes validation to prevent inventory/stock purchases from being recorded as expenses - use Purchase Orders instead.

*   reports: **NEW**: Enhanced financial reporting with COGS calculation, corrected net profit calculations, and clear distinction between operational expenses and inventory costs.

*   changelog: Provides an audit trail for all data modifications.


### Database & Logging

*   **Database File**: The SQLite database file is located at ./data/shop\_data.db.

*   **Logs**: Application logs are written to logs/vyaparsathi.log.


### Configuration

*   **JWT**: The JWT secret key and expiration time are configured in src/main/resources/application.properties.

*   **Flyway**: Migration scripts are located in src/main/resources/db/migration and are executed automatically on application startup.

### Financial Reporting Improvements

This version includes significant improvements to financial reporting and business logic:

#### Key Changes

*   **Expense Validation**: The system now prevents inventory/stock purchases from being recorded as business expenses. Only operational expenses (rent, utilities, salary, marketing, etc.) can be recorded in the expense module.

*   **COGS Calculation**: Implemented Cost of Goods Sold (COGS) calculation using average cost method based on actual purchase costs from purchase orders.

*   **Corrected Net Profit**: Net profit is now calculated as: `Total Sales - COGS - Operational Expenses` (excludes inventory purchases).

*   **Outstanding Receivables**: Added proper calculation of outstanding receivables as `Total Sales - Total Paid`.

*   **Enhanced Reports**: All financial reports now include COGS, corrected net profit calculations, and clear field descriptions in API documentation.

#### Important Notes

*   **Inventory Purchases**: Must be recorded through Purchase Orders in the inventory module, not as expenses.
*   **Operational Expenses**: Only true business operational costs should be recorded in the expense module.
*   **Backward Compatibility**: Existing data remains unaffected; validation applies only to new expense entries.

#### API Changes

*   Expense creation/update endpoints now validate expense types
*   Report endpoints return additional fields: `totalCOGS`, corrected `netProfit`, and `outstandingReceivable`
*   Enhanced Swagger documentation explains the distinction between different financial metrics