# Vehicle Identification System — Spring Boot

A full-stack Vehicle Identification System built with **Spring Boot 3** (backend REST API) and a **single-page HTML/JS frontend** served statically.

---

## Features

| Feature | Details |
|---|---|
| Owner Management | Register, view, update, delete owners |
| Vehicle Registration | Register vehicles linked to owners, auto-generated Reg No |
| Insurance | Add/view insurance policies per vehicle |
| Service Records | Track workshop visits and service history |
| Search | Search any vehicle by registration number |
| Activity Logs | File-based logging of all key events (`vehicle_data.txt`) |
| Persistence | H2 file-based database (data survives restarts) |

---

## Tech Stack

- **Backend**: Spring Boot 3.2, Spring Data JPA, H2 Database, Spring Validation
- **Frontend**: Pure HTML5 / CSS3 / Vanilla JavaScript (no npm needed)
- **Database**: H2 (file-based, auto-created at `./data/vehicledb`)
- **Build**: Maven 3.6+

---

## Requirements

- Java 17+
- Maven 3.6+

---

## Running the Application

```bash
# From the repository root:
cd vehicle-springboot-full\vehicle-springboot

# Build and run
mvn spring-boot:run
```

Then open your browser at:

```
http://localhost:8080
```

---

## API Endpoints

### Owners
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/owners` | Register new owner |
| GET | `/api/owners` | Get all owners |
| GET | `/api/owners/{id}` | Get owner by ID |
| PUT | `/api/owners/{id}` | Update owner |
| DELETE | `/api/owners/{id}` | Delete owner |

### Vehicles
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/vehicles/owner/{ownerId}` | Register vehicle for owner |
| GET | `/api/vehicles` | Get all vehicles |
| GET | `/api/vehicles/{id}` | Get vehicle by ID |
| GET | `/api/vehicles/search/{regNo}` | Search by registration number |
| POST | `/api/vehicles/{id}/insurance` | Add insurance to vehicle |
| POST | `/api/vehicles/{id}/service` | Add service record |
| DELETE | `/api/vehicles/{id}` | Delete vehicle |

### Logs
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/logs` | Get all activity log entries |

### H2 Console
Access the H2 database console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/vehicledb`
- Username: `sa`, Password: (empty)

---

## Project Structure

```
src/
├── main/
│   ├── java/com/vehicle/
│   │   ├── VehicleApplication.java
│   │   ├── model/          # JPA entities: Owner, Vehicle, Insurance, Workshop
│   │   ├── repository/     # Spring Data JPA repositories
│   │   ├── service/        # Business logic + FileHandlerService
│   │   ├── controller/     # REST controllers
│   │   └── config/         # GlobalExceptionHandler
│   └── resources/
│       ├── application.properties
│       └── static/
│           └── index.html  # Full SPA frontend
```

---

## From Original Console App

| Original Feature | Spring Boot Equivalent |
|---|---|
| `Vehicle`, `Owner`, `Insurance`, `Workshop` classes | JPA `@Entity` classes with relationships |
| `FileHandler.writeToFile / readFile` | `FileHandlerService` (with timestamps) |
| `VehicleRegistrationThread` | `VehicleService.registerVehicle()` (thread-safe via `@Transactional`) |
| `InsuranceThread` | `VehicleService.addInsurance()` |
| `ServiceThread` | `VehicleService.addServiceRecord()` |
| `Scanner` console menu | Full browser-based SPA dashboard |
| In-memory `List/Map` | H2 persistent database via JPA |
