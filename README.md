# PayrollSystem — Spring Boot 3.5.5 (Java 21)
A Payroll system(React JS + SpringBoot) demonstrating a clean 3-layer architecture, database migrations, and comprehensive testing.

## Features
- **Entities:** Department, Employee, PayrollRun, PayrollItem.
- **Database Migrations:** Flyway scripts for schema, indexes, and a stored procedure `calc_payroll_for_period(date, bigint)`.
- **Stored Procedure:** Calculates gross/tax/net salaries and inserts payroll items per run.
- **Business Logic:** Guards against duplicate departments, duplicate employees (email), and multiple payroll runs for the same period.
- **Service Layer:** Transactional services with validation and exception handling.
- **Security:** Basic Authentication with roles `ADMIN` and `EMPLOYEE` (role-based access).
- **REST API:** Full CRUD plus pagination and search.
- **OpenAPI/Swagger UI:** Automatic API documentation (enabled only in dev).
- **Testing:**  
  - **Unit tests** with JUnit 5 & Mockito.  
  - **Integration tests** with Testcontainers & Flyway, covering concurrency, uniqueness, and edge cases.

## Tech Stack
- **Language:** Java 21  
- **Framework:** Spring Boot 3.3  
- **Libraries:** Spring Web, Spring Data JPA (Hibernate 6), Validation, Spring Security  
- **Database:** PostgreSQL with Flyway migrations  
- **DTO Mapping:** MapStruct  
- **Documentation:** springdoc-openapi (Swagger UI)  
- **Testing:** JUnit 5, Mockito, Testcontainers

## Running in Development
```bash
mvn clean package
java -jar target/hrpayroll-0.0.1-SNAPSHOT.jar
