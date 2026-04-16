# Employee Management System Backend

A Spring Boot backend for employee management with JWT authentication, role-based access, DTOs, validation, MySQL persistence, Swagger documentation, logging, tests, and Docker support.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA / Hibernate
- Spring Security with JWT
- MySQL
- Maven
- Lombok
- Flyway
- Springdoc OpenAPI
- JUnit 5 / Mockito

## Folder Structure

```text
employee-management-system
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/example/employeemanagement
    │   │   ├── bootstrap
    │   │   ├── config
    │   │   ├── controller
    │   │   ├── dto
    │   │   ├── entity
    │   │   ├── exception
    │   │   ├── mapper
    │   │   ├── repository
    │   │   ├── security
    │   │   └── service
    │   └── resources/application.properties
    └── test/java/com/example/employeemanagement
```

## Roles and Security

- `USER`: Can read and search employees.
- `ADMIN`: Can create, update, delete, read, and search employees.
- Public registration creates `USER` accounts only.
- Use bootstrap admin properties or `docker-compose.yml` to create the first admin account.

## Run Locally

Prerequisites:

- Java 17
- Maven 3.9+
- MySQL 8+

Create a database:

```sql
CREATE DATABASE employee_management;
```

Run the app:

```bash
cd employee-management-system
mvn clean test
mvn spring-boot:run
```

Default configuration expects:

```text
DB_URL=jdbc:mysql://localhost:3306/employee_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=change-this-secret-key-to-at-least-32-characters
JPA_DDL_AUTO=validate
```

To create a bootstrap admin on startup:

```bash
APP_BOOTSTRAP_ADMIN_ENABLED=true
APP_BOOTSTRAP_ADMIN_EMAIL=admin@example.com
APP_BOOTSTRAP_ADMIN_PASSWORD=AdminPassword123
APP_BOOTSTRAP_ADMIN_NAME="System Admin"
```

## Run With Docker Compose

```bash
cd employee-management-system
docker compose up --build
```

The compose setup creates:

- MySQL on `localhost:3306`
- API on `http://localhost:8080`
- Bootstrap admin: `admin@example.com` / `AdminPassword123`

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Sample API Requests

### Register User

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Jane User",
  "email": "jane@example.com",
  "password": "Password123"
}
```

Sample response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 2,
    "name": "Jane User",
    "email": "jane@example.com",
    "role": "USER",
    "createdAt": "2026-04-15T10:30:00"
  }
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "AdminPassword123"
}
```

Use the returned token:

```http
Authorization: Bearer <token>
```

### Create Employee

Requires `ADMIN`.

```http
POST /api/employees
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada.lovelace@example.com",
  "phoneNumber": "+1 555 0100",
  "department": "Engineering",
  "jobTitle": "Principal Engineer",
  "salary": 150000.00,
  "dateOfJoining": "2024-01-15"
}
```

Sample response:

```json
{
  "id": 1,
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada.lovelace@example.com",
  "phoneNumber": "+1 555 0100",
  "department": "Engineering",
  "jobTitle": "Principal Engineer",
  "salary": 150000.00,
  "dateOfJoining": "2024-01-15",
  "createdByEmail": "admin@example.com",
  "createdAt": "2026-04-15T10:35:00",
  "updatedAt": "2026-04-15T10:35:00"
}
```

### Get Employees With Pagination, Sorting, and Search

Requires `ADMIN` or `USER`.

```http
GET /api/employees?page=0&size=10&sortBy=lastName&sortDir=asc&name=ada&department=engineering
Authorization: Bearer <token>
```

Sample response:

```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Ada",
      "lastName": "Lovelace",
      "email": "ada.lovelace@example.com",
      "phoneNumber": "+1 555 0100",
      "department": "Engineering",
      "jobTitle": "Principal Engineer",
      "salary": 150000.00,
      "dateOfJoining": "2024-01-15",
      "createdByEmail": "admin@example.com",
      "createdAt": "2026-04-15T10:35:00",
      "updatedAt": "2026-04-15T10:35:00"
    }
  ],
  "pageNo": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### Update Employee

Requires `ADMIN`.

```http
PUT /api/employees/1
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada.lovelace@example.com",
  "phoneNumber": "+1 555 0199",
  "department": "Research",
  "jobTitle": "Chief Scientist",
  "salary": 175000.00,
  "dateOfJoining": "2024-01-15"
}
```

### Delete Employee

Requires `ADMIN`.

```http
DELETE /api/employees/1
Authorization: Bearer <admin-token>
```

Returns `204 No Content`.

## Useful Endpoints

| Method | Endpoint | Access |
| --- | --- | --- |
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| POST | `/api/employees` | ADMIN |
| GET | `/api/employees` | ADMIN, USER |
| GET | `/api/employees/{id}` | ADMIN, USER |
| PUT | `/api/employees/{id}` | ADMIN |
| DELETE | `/api/employees/{id}` | ADMIN |

## Production Notes

- Override `JWT_SECRET` with a strong secret.
- Database schema is managed by Flyway in `src/main/resources/db/migration`.
- Keep `JPA_DDL_AUTO=validate` in production.
- Store database credentials in a secret manager, not in plain text.
- Use HTTPS in front of the API.
