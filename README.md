# 🛒 Jumbotail — B2B E-Commerce Shipping Charge Estimator

A production-grade Spring Boot REST API for a B2B e-commerce marketplace that helps Kirana stores (small Indian retail shops) discover and order products. The system calculates shipping charges using real road distances via the **OSRM routing API**, with **PostGIS** for geospatial queries and **JWT** for authentication.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Database Setup](#-database-setup)
- [Running the Project](#-running-the-project)
- [API Reference](#-api-reference)
- [Shipping Calculation Logic](#-shipping-calculation-logic)
- [Design Patterns](#-design-patterns)
- [Caching](#-caching)
- [Running Tests](#-running-tests)

---

## ✨ Features

- ✅ **JWT Authentication** — Secure signup and login for Customers, Sellers, and Admins
- ✅ **Geospatial Nearest Warehouse** — PostGIS `ST_Distance` query finds the closest warehouse to any seller
- ✅ **Real Road Distance** — OSRM API calculates actual road distance (with Haversine fallback)
- ✅ **Strategy Pattern Pricing** — Standard and Express delivery strategies, easily extensible
- ✅ **Transport Mode Selection** — Auto-selects Mini Van / Truck / Aeroplane based on distance
- ✅ **In-Memory Caching** — Caffeine cache for nearest warehouse and shipping charge results
- ✅ **Global Exception Handling** — Consistent JSON error responses across all endpoints
- ✅ **Normalized Database** — Separate User, Customer, Seller, Product, Warehouse, Order tables
- ✅ **Input Validation** — Bean Validation (`@Valid`) on all request DTOs
- ✅ **Unit Tests** — JUnit 5 + Mockito tests for shipping logic and utility classes

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.3 |
| Language | Java 17 |
| Database | PostgreSQL + PostGIS |
| ORM | Hibernate 6 + Hibernate Spatial |
| Security | Spring Security + JWT (JJWT 0.12.3) |
| Caching | Caffeine (in-memory) |
| Distance API | OSRM (Open Source Routing Machine) |
| Geospatial | JTS (Java Topology Suite) |
| Mapping | ModelMapper 3.2 |
| Build | Maven |
| Testing | JUnit 5 + Mockito |
| Utilities | Lombok |

---

## 🏗 Architecture

```
HTTP Request
     │
     ▼
JwtAuthenticationFilter          ← Validates Bearer token on every request
     │
     ▼
Controller Layer                 ← AuthController, ShippingController, etc.
     │
     ▼
Service Layer (Interface + Impl) ← Business logic, caching, orchestration
     │
     ├── DeliveryStrategyFactory  ← Selects Standard / Express strategy
     │         │
     │    DeliveryStrategy        ← Strategy Pattern: calculates charge
     │
     ├── OSRMService              ← Road distance via OSRM API (+ Haversine fallback)
     │
     └── WarehouseService         ← PostGIS nearest-warehouse query
              │
              ▼
     Repository Layer             ← Spring Data JPA + native PostGIS queries
              │
              ▼
     PostgreSQL + PostGIS         ← geometry(Point, 4326) columns
```

---

## 📁 Project Structure

```
src/main/java/com/jayant/JTail/
├── JumbotailApplication.java
│
├── config/
│   ├── CacheConfig.java          # Caffeine cache (10 min TTL)
│   ├── ModelMapperConfig.java    # DTO ↔ Entity mapping (STRICT strategy)
│   ├── RestTemplateConfig.java   # HTTP client for OSRM with timeouts
│   └── WebSecurityConfig.java    # JWT + role-based security rules
│
├── controller/
│   ├── AuthController.java       # POST /auth/signup/customer, /signup/seller, /login
│   ├── CustomerController.java   # GET  /customers/me
│   ├── ProductController.java    # CRUD for products
│   ├── ShippingController.java   # The 3 core assignment APIs
│   └── WarehouseController.java  # GET /warehouse/nearest + CRUD
│
├── dto/
│   ├── PointDto.java             # {coordinates: [lng, lat]}
│   ├── request/                  # CustomerSignupRequest, LoginRequest, etc.
│   └── response/                 # ApiResponse<T>, AuthResponse, etc.
│
├── entity/
│   ├── User.java                 # Auth credentials (email + BCrypt password)
│   ├── Customer.java             # Kirana store profile + PostGIS location
│   ├── Seller.java               # Seller profile + PostGIS location
│   ├── Product.java              # Product with weight/dimension attributes
│   ├── Warehouse.java            # Warehouse with PostGIS location
│   └── Order.java                # Order tracking with shipping details
│
├── enums/
│   ├── UserRole.java             # CUSTOMER, SELLER, ADMIN
│   ├── DeliverySpeed.java        # STANDARD, EXPRESS
│   ├── TransportMode.java        # MINI_VAN, TRUCK, AEROPLANE (with rate + range)
│   └── OrderStatus.java          # PENDING → DELIVERED lifecycle
│
├── exception/
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice — all errors → JSON
│   ├── ResourceNotFoundException.java # 404
│   ├── DuplicateResourceException.java# 409
│   ├── InvalidRequestException.java   # 400
│   └── ShippingCalculationException.java # 500
│
├── repository/
│   ├── WarehouseRepository.java  # PostGIS ST_Distance native query
│   └── ...                       # Standard JPA repositories
│
├── security/
│   ├── JwtTokenProvider.java         # Token generation + validation
│   ├── JwtAuthenticationFilter.java  # Intercepts every request
│   ├── JwtAuthEntryPoint.java        # Returns 401 JSON (not HTML)
│   └── UserDetailsServiceImpl.java   # Loads user from DB for Spring Security
│
├── service/
│   ├── interfaces/               # Service contracts (interfaces)
│   └── impl/
│       ├── AuthServiceImpl.java      # Signup + login
│       ├── ShippingServiceImpl.java  # Core shipping calculation
│       ├── WarehouseServiceImpl.java # Nearest warehouse lookup
│       ├── OSRMServiceImpl.java      # Road distance via OSRM API
│       └── ProductServiceImpl.java   # Product CRUD
│
├── strategy/
│   ├── DeliveryStrategy.java           # Interface
│   ├── StandardDeliveryStrategy.java   # Rs 10 + (rate × km × kg)
│   ├── ExpressDeliveryStrategy.java    # + Rs 1.2/kg surcharge
│   └── DeliveryStrategyFactory.java    # Auto-discovers strategies via Spring DI
│
└── utils/
    └── GeometryUtil.java         # JTS Point creation + Haversine formula
```

---

## ✅ Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 13+** with **PostGIS extension**

---

## 🗄 Database Setup

**1. Create the database and enable PostGIS:**

```sql
CREATE DATABASE "JumboTail";
\c JumboTail
CREATE EXTENSION postgis;

-- Verify PostGIS is active
SELECT PostGIS_Version();
```

**2. Create the admin user** (after running the app once so tables are created):

```sql
-- Password: admin123
INSERT INTO users (email, password, role, active)
VALUES (
  'admin@jumbotail.com',
  '$2a$12$heLKXHzjXmXnHxZWBAW9yeXLGVEoaUDcCLnwKg1kwU4lpNU1wI0ru',
  'ADMIN',
  true
);
```

**3. Update `application.properties`** after the first run to preserve data:

```properties
# First run: create  →  After first run: change to update
spring.jpa.hibernate.ddl-auto=update
```

---

## 🚀 Running the Project

```bash
# 1. Clone the repo
git clone https://github.com/your-username/jumbotail-shipping.git
cd jumbotail-shipping

# 2. Build
mvn clean install -DskipTests

# 3. Run
mvn spring-boot:run
```

App starts at **http://localhost:8080**

---

## 📡 API Reference

### 🔐 Authentication (Public — No Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/signup/customer` | Register a Kirana store |
| POST | `/api/v1/auth/signup/seller` | Register a seller |
| POST | `/api/v1/auth/login` | Login (returns JWT) |

**Customer Signup:**
```json
POST /api/v1/auth/signup/customer
{
  "storeName": "Shree Kirana Store",
  "ownerName": "Raju Patel",
  "email": "raju@shreekirana.com",
  "password": "secret123",
  "phone": "9847123456",
  "address": "MG Road, Bengaluru",
  "city": "Bengaluru",
  "state": "Karnataka",
  "pincode": "560001",
  "location": { "coordinates": [77.5946, 12.9716] }
}
```

**Seller Signup:**
```json
POST /api/v1/auth/signup/seller
{
  "businessName": "Nestle Seller",
  "contactName": "Neha Sharma",
  "email": "neha@nestle.com",
  "password": "secret123",
  "phone": "9847654321",
  "address": "Whitefield, Bengaluru",
  "gstNumber": "29AADCB2230M1ZP",
  "location": { "coordinates": [77.7480, 12.9698] }
}
```

**Login:**
```json
POST /api/v1/auth/login
{
  "email": "raju@shreekirana.com",
  "password": "secret123"
}
```
Response includes `"token": "eyJ..."` — use this as `Authorization: Bearer <token>`.

---

### 🏭 Warehouse (Admin Token Required for POST)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/warehouses` | ADMIN | Create warehouse |
| GET | `/api/v1/warehouses` | ADMIN | List all warehouses |
| GET | `/api/v1/warehouses/{id}` | Any | Get warehouse by ID |
| GET | `/api/v1/warehouse/nearest?sellerId={id}` | SELLER | **API #1** — Nearest warehouse |

**Create Warehouse:**
```json
POST /api/v1/warehouses
Authorization: Bearer <admin-token>
{
  "name": "BLR_Warehouse",
  "address": "Electronic City, Bengaluru",
  "city": "Bengaluru",
  "state": "Karnataka",
  "pincode": "560100",
  "location": { "coordinates": [77.6733, 12.8458] }
}
```

---

### 📦 Products

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/products` | Public | Browse all active products |
| GET | `/api/v1/products/{id}` | Public | Get product by ID |
| GET | `/api/v1/products/seller/{sellerId}` | Public | Products by seller |
| POST | `/api/v1/products` | SELLER | Add new product |
| PUT | `/api/v1/products/{id}` | SELLER | Update product |
| DELETE | `/api/v1/products/{id}` | SELLER | Deactivate product |

**Add Product:**
```json
POST /api/v1/products
Authorization: Bearer <seller-token>
{
  "name": "Maggie 500g Packet",
  "sellingPrice": 10.00,
  "weightKg": 0.5,
  "lengthCm": 10, "widthCm": 10, "heightCm": 10,
  "minOrderQuantity": 12,
  "stockQuantity": 500,
  "category": "Grocery",
  "sku": "MAGGI-500G-001"
}
```

---

### 🚚 Shipping (Core Assignment APIs)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/warehouse/nearest?sellerId={id}` | SELLER | **API #1** Nearest warehouse |
| GET | `/api/v1/shipping-charge?warehouseId=&customerId=&productId=&deliverySpeed=` | Any | **API #2** Shipping charge |
| POST | `/api/v1/shipping-charge/calculate` | Any | **API #3** Combined calculation |

**API #2 — Get Shipping Charge:**
```
GET /api/v1/shipping-charge?warehouseId=1&customerId=1&productId=1&deliverySpeed=STANDARD
Authorization: Bearer <token>
```

Response:
```json
{
  "success": true,
  "data": {
    "shippingCharge": 95.50,
    "distanceKm": 28.5,
    "transportMode": "MINI_VAN",
    "deliverySpeed": "STANDARD",
    "baseCourierCharge": 10.00,
    "expressSurcharge": null,
    "distanceBasedCharge": 85.50
  }
}
```

**API #3 — Combined Calculation:**
```json
POST /api/v1/shipping-charge/calculate
Authorization: Bearer <token>
{
  "sellerId": 1,
  "customerId": 1,
  "productId": 1,
  "deliverySpeed": "EXPRESS"
}
```

Response:
```json
{
  "success": true,
  "data": {
    "shippingCharge": 96.10,
    "nearestWarehouse": {
      "warehouseId": 1,
      "name": "BLR_Warehouse",
      "warehouseLocation": { "lat": 12.8458, "lng": 77.6733 }
    },
    "chargeBreakdown": {
      "transportMode": "MINI_VAN",
      "baseCourierCharge": 10.00,
      "expressSurcharge": 0.60,
      "distanceBasedCharge": 85.50
    }
  }
}
```

---

## 💰 Shipping Calculation Logic

### Transport Mode (auto-selected by distance)

| Distance | Mode | Rate |
|----------|------|------|
| 0 – 99 km | 🚐 Mini Van | Rs 3 / km / kg |
| 100 – 499 km | 🚛 Truck | Rs 2 / km / kg |
| 500+ km | ✈️ Aeroplane | Rs 1 / km / kg |

### Delivery Speed

| Speed | Formula |
|-------|---------|
| **STANDARD** | Rs 10 (base) + (rate × distance × weight) |
| **EXPRESS** | Rs 10 (base) + Rs 1.2/kg (surcharge) + (rate × distance × weight) |

### Example Calculation
> Seller in Bengaluru → BLR_Warehouse (28 km) → Customer in Bengaluru (28 km)
> Product: 0.5 kg, Delivery: EXPRESS
>
> Transport: Mini Van (< 100 km) → Rs 3/km/kg
> Distance charge: 3 × 28 × 0.5 = **Rs 42**
> Express surcharge: 1.2 × 0.5 = **Rs 0.60**
> Base: **Rs 10**
> **Total: Rs 52.60**

---

## 🎨 Design Patterns

### Strategy Pattern — Delivery Pricing
Adding a new delivery speed (e.g., `SAME_DAY`) requires **zero changes** to existing code:

```java
// Just create a new @Component class:
@Component
public class SameDayDeliveryStrategy implements DeliveryStrategy {
    @Override
    public DeliverySpeed getDeliverySpeed() { return DeliverySpeed.SAME_DAY; }

    @Override
    public ShippingChargeResponse calculateCharge(double distanceKm, double weightKg) {
        // custom formula
    }
}
// Spring auto-discovers it and DeliveryStrategyFactory maps it automatically.
```

### Factory Pattern — Strategy Resolution
`DeliveryStrategyFactory` uses Spring DI to auto-collect all `DeliveryStrategy` beans and maps them by `DeliverySpeed` enum key. No if-else chains.

### Repository Pattern
All data access is behind Spring Data JPA repository interfaces. Services never use `EntityManager` directly.

---

## 💾 Caching

Uses **Caffeine** (in-memory, no external service needed). Three caches:

| Cache Name | Key | TTL | Purpose |
|------------|-----|-----|---------|
| `nearestWarehouse` | `sellerId` | 10 min | PostGIS query result |
| `shippingCharge` | `warehouseId-customerId-productId-speed` | 10 min | OSRM + charge calculation |
| `products` | `all-active` | 10 min | Product list |

**To upgrade to Redis** (for multi-server deployment):
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
```properties
# application.properties
spring.cache.type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```
Then delete `CacheConfig.java` — Redis auto-configures itself.

---

## 🧪 Running Tests

```bash
mvn test
```

| Test Class | Tests | What's Covered |
|---|---|---|
| `ShippingServiceTest` | 8 | Shipping calculation with mocked DB + OSRM |
| `DeliveryStrategyTest` | 12 | Standard/Express formula correctness, transport mode boundaries |
| `GeometryUtilTest` | 4 | JTS Point creation, Haversine distance accuracy |

---

## 🗺 Sequential API Testing Guide

Run these in order in Postman:

```
1.  POST  /api/v1/auth/login             (admin@jumbotail.com / admin123)
2.  POST  /api/v1/warehouses             (ADMIN token) → create BLR_Warehouse
3.  POST  /api/v1/warehouses             (ADMIN token) → create MUMB_Warehouse
4.  POST  /api/v1/auth/signup/seller     → register Nestle Seller
5.  POST  /api/v1/auth/signup/customer   → register Shree Kirana Store
6.  POST  /api/v1/auth/login             (seller email) → get SELLER token
7.  POST  /api/v1/products               (SELLER token) → add Maggie 500g
8.  GET   /api/v1/warehouse/nearest      (SELLER token, sellerId=1)  ← API #1
9.  GET   /api/v1/shipping-charge        (any token)                 ← API #2
10. POST  /api/v1/shipping-charge/calculate (any token)              ← API #3
```

---

## 📝 Notes

- `coordinates` array follows **GeoJSON convention**: `[longitude, latitude]` (longitude first)
- All monetary values are in **Indian Rupees (₹)**
- OSRM uses the public demo server (`router.project-osrm.org`) — replace with a self-hosted instance for production
- Password hashing uses **BCrypt with cost factor 12**
- JWT tokens expire after **24 hours** (configurable via `jwt.expiration-ms`)
