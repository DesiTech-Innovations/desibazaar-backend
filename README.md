
# VyaparSathi Backend

## Overview
This is the backend for VyaparSathi POS - an offline-first shop management application targeting Indian village and city level shops.

## Technologies
- Java 17
- Spring Boot 3.x
- SQLite
- JWT authentication
- OpenPDF for invoice PDF generation

## Running the project

### Prerequisites
- Java 17+
- Maven 3.8+

### Run locally
```bash
mvn clean spring-boot:run
```

### API Documentation
Once running, access Swagger UI at:  
http://localhost:8080/swagger-ui.html

---

## Project structure
- `auth` - authentication and user management
- `shop` - shop setup and configuration
- `catalog` - item management
- `billing` - sale creation, GST calculation, invoice generation

---

## Notes
- Database file: `./data/shop_data.db`
- Logs: `logs/vyaparsathi.log`
- JWT secret and expiration are configured in `application.properties`
