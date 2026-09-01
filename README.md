# 📦 Product Management API

A secure and scalable RESTful API for managing products and their items — built with Spring Boot, Spring Security, and JWT-based authentication.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Maven](https://img.shields.io/badge/Build-Maven-blue)
![License](https://img.shields.io/badge/License-Private-lightgrey)

The application provides JWT-based authentication, role-based authorization, product and item management, pagination, sorting, searching, filtering, request validation, API documentation, and comprehensive automated testing.

---

## 📑 Table of Contents

- [Features](#-features)
- [User Roles](#-user-roles)
- [Architecture](#️-architecture)
- [Technology Stack](#️-technology-stack)
- [Authentication Flow](#-authentication-flow)
- [Authorization Rules](#-authorization-rules)
- [API Endpoints](#-api-endpoints)
- [Pagination](#-pagination)
- [Sorting](#-sorting)
- [JWT Authentication](#️-jwt-authentication)
- [Refresh Token Rotation](#-refresh-token-rotation)
- [Logout](#-logout)
- [Validation](#-validation)
- [Exception Handling](#️-exception-handling)
- [API Documentation](#-api-documentation)
- [Using JWT with Swagger](#-using-jwt-with-swagger)
- [Testing](#-testing)
- [Application Setup](#️-application-setup)
- [Docker Support](#-docker-support)
- [Database Design](#️-database-design)
- [Entity Relationships](#-entity-relationships)
- [Security Architecture](#️-security-architecture)
- [Project Structure](#-project-structure)
- [Key Design Decisions](#-key-design-decisions)
- [Future Improvements](#-future-improvements)
- [Author](#-author)
- [License](#-license)

---

## 🚀 Features

### 🔐 Authentication & Security
- User Registration
- User Login
- JWT Access Token Authentication
- Refresh Token Authentication
- Refresh Token Rotation
- Logout with Refresh Token Revocation
- Role-Based Access Control (RBAC)
- Stateless Authentication
- BCrypt Password Encryption

### 📦 Product Management
- Create Product
- Get Product by ID
- Get All Products
- Update Product
- Delete Product
- Search Products by Name
- Filter Products by Minimum Item Quantity
- Filter Products by Item Quantity Range
- Pagination Support
- Sorting Support

### 📋 Item Management
- Create Item for a Product
- Get All Items for a Product
- Item Quantity Validation
- Product-Item Relationship Management

### 🛡️ Validation & Error Handling
- Request Validation using Jakarta Validation
- Global Exception Handling
- Resource Not Found Handling
- Username Already Exists Handling
- Invalid JWT Token Handling
- Expired JWT Token Handling
- Invalid Refresh Token Handling
- Unauthorized Access Handling

---

## 👥 User Roles

| Role  | Permissions                                     |
|-------|--------------------------------------------------|
| ADMIN | Create, Update, Delete Products and Create Items  |
| USER  | View Products and Items                           |

---

## 🏗️ Architecture

The project follows a layered architecture:

```
Client
   │
   ▼
Controller Layer
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
Database
```

### Project Architecture

```
src/main/java
└── com.zestindia.productmanagement
    │
    ├── config
    │   ├── OpenApiConfig
    │   └── SecurityConfig
    │
    ├── controller
    │   ├── AuthController
    │   └── ProductController
    │
    ├── dto
    │   ├── request
    │   └── response
    │
    ├── entity
    │   ├── User
    │   ├── Product
    │   ├── Item
    │   └── RefreshToken
    │
    ├── enums
    │   └── Role
    │
    ├── exception
    │   └── GlobalExceptionHandler
    │
    ├── repository
    │   ├── UserRepository
    │   ├── ProductRepository
    │   ├── ItemRepository
    │   └── RefreshTokenRepository
    │
    ├── security
    │   ├── JwtService
    │   ├── JwtAuthenticationFilter
    │   ├── JwtAuthenticationEntryPoint
    │   └── CustomUserDetailsService
    │
    └── service
        ├── AuthService
        ├── ProductService
        ├── ItemService
        └── impl
```

---

## 🛠️ Technology Stack

| Technology         | Purpose                        |
|---------------------|---------------------------------|
| Java 21             | Programming Language           |
| Spring Boot         | Backend Framework              |
| Spring Security     | Authentication & Authorization |
| Spring Data JPA     | Database Access                |
| Hibernate           | ORM                             |
| JWT                 | Stateless Authentication       |
| Refresh Token       | Token Renewal                  |
| Maven               | Dependency Management          |
| JUnit 5             | Unit & Integration Testing     |
| Mockito             | Mocking Framework              |
| Swagger / OpenAPI   | API Documentation              |
| Jakarta Validation  | Request Validation             |
| Lombok              | Boilerplate Reduction          |

---

## 🔐 Authentication Flow

The application uses JWT-based authentication.

```
User
 │
 │ Login
 ▼
Authentication Manager
 │
 ▼
User Validation
 │
 ├───────────────┐
 ▼               ▼
Access Token    Refresh Token
 │               │
 ▼               ▼
API Request     Token Refresh
```

### Authentication Process

1. User registers an account.
2. User logs into the application.
3. The server generates:
   - JWT Access Token
   - Refresh Token
4. The Access Token is used to access protected APIs.
5. When the Access Token expires, the Refresh Token can generate a new Access Token.
6. Refresh Token Rotation generates a new Refresh Token.
7. Logout revokes the Refresh Token.

---

## 👮 Authorization Rules

### Public APIs
```
/api/v1/auth/**
/swagger-ui/**
/v3/api-docs/**
```

### ADMIN Permissions
```
POST   /api/v1/products
PUT    /api/v1/products/**
DELETE /api/v1/products/**
POST   /api/v1/products/*/items
```

### ADMIN and USER Permissions
```
GET /api/v1/products/**
```

---

## 📡 API Endpoints

### 🔐 Authentication APIs

#### Register User
`POST /api/v1/auth/register`

**Request Body**
```json
{
  "username": "admin",
  "password": "Admin@123",
  "role": "ADMIN"
}
```

#### Login
`POST /api/v1/auth/login`

**Request Body**
```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

**Response**
```json
{
  "accessToken": "JWT_ACCESS_TOKEN",
  "refreshToken": "REFRESH_TOKEN",
  "tokenType": "Bearer",
  "accessTokenExpiresIn": 900000
}
```

#### Refresh Access Token
`POST /api/v1/auth/refresh`

**Request Body**
```json
{
  "refreshToken": "YOUR_REFRESH_TOKEN"
}
```

#### Logout
`POST /api/v1/auth/logout`

**Request Body**
```json
{
  "refreshToken": "YOUR_REFRESH_TOKEN"
}
```

---

### 📦 Product APIs

#### Create Product
`POST /api/v1/products`

**Authorization:** `ADMIN`

**Request Body**
```json
{
  "productName": "Laptop",
  "items": [
    { "quantity": 10 },
    { "quantity": 20 }
  ]
}
```

#### Get Product by ID
`GET /api/v1/products/{id}`

**Authorization:** `ADMIN` or `USER`

#### Get All Products
`GET /api/v1/products`

**Query Parameters**

| Parameter      | Default Value | Description         |
|----------------|----------------|----------------------|
| page           | 0              | Page Number          |
| size           | 10             | Number of Records    |
| sortBy         | id             | Field for Sorting    |
| sortDirection  | asc            | Sort Direction        |

**Example**
```
GET /api/v1/products?page=0&size=10&sortBy=productName&sortDirection=asc
```

#### Update Product
`PUT /api/v1/products/{id}`

**Authorization:** `ADMIN`

**Request Body**
```json
{
  "productName": "Updated Laptop",
  "items": [
    { "quantity": 15 }
  ]
}
```

#### Delete Product
`DELETE /api/v1/products/{id}`

**Authorization:** `ADMIN`

---

### 🔍 Product Search

#### Search Products by Name
`GET /api/v1/products/search`

**Example**
```
GET /api/v1/products/search?keyword=laptop&page=0&size=10
```

---

### 📊 Product Filtering

#### Filter Products by Minimum Quantity
`GET /api/v1/products/filter`

**Example**
```
GET /api/v1/products/filter?minQuantity=10&page=0&size=10
```
Returns products containing at least one item with a quantity greater than or equal to the specified value.

#### Filter Products by Quantity Range
`GET /api/v1/products/filter/range`

**Example**
```
GET /api/v1/products/filter/range?minQuantity=10&maxQuantity=100&page=0&size=10
```
Returns products containing at least one item with a quantity within the specified range.

---

### 📋 Item APIs

#### Create Item
`POST /api/v1/products/{productId}/items`

**Authorization:** `ADMIN`

**Request Body**
```json
{
  "quantity": 10
}
```

#### Get Items by Product
`GET /api/v1/products/{productId}/items`

**Authorization:** `ADMIN` or `USER`

---

## 📄 Pagination

The Product API supports pagination.

**Example**
```
GET /api/v1/products?page=0&size=10
```

**Parameters**
- `page` → Page number (starts from 0)
- `size` → Number of records per page

---

## 🔃 Sorting

The Product API supports sorting.

**Example**
```
GET /api/v1/products?sortBy=productName&sortDirection=asc
```

**Supported directions:** `asc`, `desc`

---

## 🛡️ JWT Authentication

Protected APIs require a JWT Access Token.

Include the token in the request header:

```
Authorization: Bearer YOUR_ACCESS_TOKEN
```

**Example**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🔄 Refresh Token Rotation

The application implements Refresh Token Rotation. When a refresh request is successful:

1. The existing Refresh Token is replaced.
2. A new Access Token is generated.
3. A new Refresh Token value is generated.
4. The previous Refresh Token value becomes invalid.

This improves security by preventing long-term reuse of refresh tokens.

---

## 🚪 Logout

When a user logs out:

```
Refresh Token
      │
      ▼
Token Revoked
      │
      ▼
Cannot be used again
```

The Refresh Token is marked as revoked and cannot be used to generate new Access Tokens.

---

## ❗ Validation

The application uses Jakarta Bean Validation.

**Product Validation — Product Name**
- ✓ Cannot be empty
- ✓ Cannot be blank

**Item Validation — Quantity**
- ✓ Required
- ✓ Must be greater than 0

---

## ⚠️ Exception Handling

The application uses centralized exception handling. Handled exceptions include:

- `ResourceNotFoundException`
- `UsernameAlreadyExistsException`
- `InvalidTokenException`
- `TokenExpiredException`
- `InvalidRefreshTokenException`
- Validation Exceptions
- Authentication Exceptions
- Authorization Exceptions

---

## 📚 API Documentation

The application uses Swagger/OpenAPI for API documentation.

After starting the application, access Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

Swagger provides:
- API Endpoint Documentation
- Request Models
- Response Models
- JWT Authorization Support
- Interactive API Testing

---

## 🔒 Using JWT with Swagger

1. **Register a User** — `POST /api/v1/auth/register`
2. **Login** — `POST /api/v1/auth/login`
3. **Copy Access Token** — copy the value from `accessToken`
4. **Click Authorize** — click **Authorize 🔒** and enter:
   ```
   Bearer YOUR_ACCESS_TOKEN
   ```

You can now test protected endpoints.

---

## 🧪 Testing

The project includes comprehensive automated testing.

### Test Categories

```
Repository Tests
        │
        ▼
Service Tests
        │
        ▼
Controller Tests
        │
        ▼
API Integration Tests
        │
        ▼
Security Integration Tests
```

#### Repository Tests
- `ItemRepositoryTest`
- `ProductRepositoryTest`
- `RefreshTokenRepositoryTest`
- `UserRepositoryTest`

#### Service Tests
- `AuthServiceTest`
- `ProductServiceImplTest`
- `ItemServiceImplTest`

#### Controller Tests
- `AuthControllerTest`
- `ProductControllerTest`
- `ItemControllerTest`

#### API Integration Tests
- `ProductApiIntegrationTest`
- `AuthApiIntegrationTest`
- `ItemApiIntegrationTest`

These tests validate the complete flow:

```
HTTP Request
     │
     ▼
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
Database
```

#### Security Integration Tests

Security integration testing verifies:
- ✓ Public API Access
- ✓ Unauthorized Request Handling
- ✓ ADMIN Authorization
- ✓ USER Authorization
- ✓ Protected Product APIs
- ✓ JWT Authentication

### ▶️ Running Tests

Run all tests using Maven:
```bash
mvn test
```

Or using Spring Tool Suite / Eclipse:
1. Right-click the project
2. **Run As**
3. **JUnit Test**

---

## ⚙️ Application Setup

### Prerequisites

Make sure you have installed:
- Java 21
- Maven
- Git
- Database configured for the active Spring profile

### Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/product-management-api.git
cd product-management-api
```

### Configure Application Properties

The application uses Spring Profiles.

**Main configuration**
```yaml
spring:
  application:
    name: product-management-api

  profiles:
    active: dev
```

Configure the database and JWT properties in your active profile configuration.

**Example required JWT properties**
```yaml
jwt:
  secret: YOUR_SECURE_SECRET_KEY
  access-token-expiration: 900000
```

> **Important:** Do not commit real production secrets or credentials to a public GitHub repository.

### Run the Application

Using Maven:
```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class directly from your IDE.

### 🌐 Application URLs

| Resource               | URL                                              |
|-------------------------|--------------------------------------------------|
| Application             | http://localhost:8080                            |
| Swagger UI               | http://localhost:8080/swagger-ui/index.html      |
| OpenAPI Documentation    | http://localhost:8080/v3/api-docs                |

---

## 🐳 Docker Support

This project is intended to be containerized using:
- `Dockerfile`
- `docker-compose.yml`

The Docker configuration enables the application and its required services to be started consistently across different environments.

### Build Docker Image
```bash
docker build -t product-management-api .
```

### Run Using Docker Compose
```bash
docker-compose up --build
```

Ensure the Docker configuration and environment variables match your database configuration.

---

## 🗂️ Database Design

The application consists primarily of the following entities:

```
User
 │
 ├──── RefreshToken
 │
 └──── Role


Product
 │
 └──── Item
```

### User
- id
- username
- password
- role
- enabled
- createdOn

### Refresh Token
- id
- token
- expiryDate
- revoked
- user
- createdOn

### Product
- id
- productName
- createdBy
- createdOn
- modifiedBy
- modifiedOn

### Item
- id
- quantity
- product

---

## 🔗 Entity Relationships

```
Product
   │
   │ One-to-Many
   ▼
Item


User
   │
   │ One-to-One / Token Ownership
   ▼
RefreshToken
```

---

## 🏛️ Security Architecture

```
Client Request
      │
      ▼
JWT Authentication Filter
      │
      ▼
Extract JWT Token
      │
      ▼
Validate Token
      │
      ▼
Load User Details
      │
      ▼
Security Context
      │
      ▼
Role-Based Authorization
      │
      ▼
Protected API
```

---

## 📁 Project Structure

```
product-management-api
│
├── src
│   │
│   ├── main
│   │   ├── java
│   │   │   └── com.zestindia.productmanagement
│   │   │
│   │   └── resources
│   │       ├── application.yml
│   │       └── application-*.yml
│   │
│   └── test
│       └── java
│           └── com.zestindia.productmanagement
│
├── Dockerfile
│
├── docker-compose.yml
│
├── pom.xml
│
└── README.md
```

---

## 🔧 Key Design Decisions

### Layered Architecture

The application separates responsibilities into:
- **Controller** → API Layer
- **Service** → Business Logic
- **Repository** → Database Operations
- **Entity** → Database Mapping
- **DTO** → Request / Response Data

This improves maintainability, testability, scalability, and separation of concerns.

### Stateless Authentication

JWT authentication is configured as stateless (`SessionCreationPolicy.STATELESS`). The server does not maintain HTTP session information.

### Role-Based Authorization

Spring Security controls access based on user roles:
- `ROLE_ADMIN`
- `ROLE_USER`

### DTO-Based API Design

Entities are not directly exposed through API responses:

```
Request DTO
      ↓
Service
      ↓
Entity
      ↓
Response DTO
```

This improves API security and separation between database models and API contracts.

---

## 🚧 Future Improvements

- Redis Token Blacklisting
- Rate Limiting
- Email Verification
- Password Reset
- Account Locking
- Audit Logging
- API Versioning Enhancements
- Docker Deployment
- CI/CD Pipeline
- Cloud Deployment
- Monitoring and Metrics

---

## 👨‍💻 Author

**Ajit Musale**
Java Full Stack Developer

---

## 📄 License

This project was developed as a private assignment project.
