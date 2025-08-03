# 🌱 AL Carbon Calculator

This project is a **backend for a carbon emission calculator**, developed using **Java + Spring Boot + MongoDB**.  
It was created as part of a **technical assessment** for **Action Labs**.

---

## 🚀 Technologies Used
- **Java 17**
- **Spring Boot 3**
- **Spring Data MongoDB**
- **Lombok**
- **Swagger/OpenAPI**
- **Docker Compose** (MongoDB)
- **Maven**

---

## 📌 Implemented Features

The application provides **3 main endpoints**:

1. **POST `/open/start-calc`**
    - Creates a new carbon emission calculation.
    - Receives basic user data (`name`, `email`, `phoneNumber`, `uf`).
    - Returns the calculation `id` to be used in the next steps.

2. **PUT `/open/info`**
    - Updates the information required for the carbon emission calculation:
        - Energy consumption (`energyConsumption`)
        - Transportation used (`type` and `monthlyDistance`)
        - Solid waste production (`solidWasteTotal` and `recyclePercentage`)
    - If called again with the same `id`, it overwrites the existing data.

3. **GET `/open/result/{id}`**
    - Calculates the user's **total carbon emission**:
        - Electric energy
        - Transportation
        - Solid waste
    - Returns a JSON with the total and a breakdown of each category.

---

## 🗄️ MongoDB Modeling

The project uses **MongoDB** for persistence.  
Main collections:

- `calculation` → stores user calculation data
- `energyEmissionFactor` → emission factors by state
- `transportationEmissionFactor` → emission factors by transportation type
- `solidWasteEmissionFactor` → emission factors for recyclable and non-recyclable waste

Example document in `calculation`:

```json
{
  "_id": "66aee019a28f4a3d8c7f9e23",
  "name": "João da Silva",
  "email": "joao@example.com",
  "phoneNumber": "11999999999",
  "uf": "SP",
  "energyConsumption": 300,
  "transportation": [
    { "type": "CAR", "monthlyDistance": 500 },
    { "type": "BICYCLE", "monthlyDistance": 200 }
  ],
  "solidWasteTotal": 60,
  "recyclePercentage": 0.3
}
```

---

## 🧮 Carbon Formulas

- **Energy**:  
  ```
  Carbon emission = energy consumption * emission factor
  ```

- **Transport**:  
  ```
  Carbon emission = distance * transportation type emission factor
  ```

- **Solid Waste**:  
  ```
  Carbon emission = solid waste production * emission factor
  ```

The **total** is the sum of the three emissions.

---

## ▶️ How to Run the Project

### 0️⃣ Clone the Repository
Clone the project to your local machine and enter the folder:
```bash
git clone https://github.com/maxzdosreis/challenge_actionlabs.git
cd challenge_actionlabs
```

### 1️⃣ Upload MongoDB with Docker
```bash
docker-compose up -d
```

> If you want to reset the data to the initial state:
```bash
docker-compose down -v
docker-compose up -d
```

---

### 2️⃣ Run the backend
In your IDE or Maven terminal:
```bash
mvn spring-boot:run
```

The application will run on
```
http://localhost:8085
```

---

### 3️⃣ Test on Swagger 
Access:
```
http://localhost:8085/swagger-ui.html
```

1. **POST** `/open/start-calc` → Creates the calculation and returns `id`  
2. **PUT** `/open/info` → Update data with `id`  
3. **GET** `/open/result/{id}` → Calculates emissions and returns total

---

## ✅ Test Flow Example

**1️⃣ Create calculation:**
```json
POST /open/start-calc
{
  "name": "João da Silva",
  "email": "joao@example.com",
  "uf": "SP",
  "phoneNumber": "11999999999"
}
```
Response:
```json
{ "id": "66aee019a28f4a3d8c7f9e23" }
```

**2️⃣ Update Information:**
```json
PUT /open/info
{
  "id": "66aee019a28f4a3d8c7f9e23",
  "energyConsumption": 300,
  "transportation": [
    { "type": "CAR", "monthlyDistance": 500 }
  ],
  "solidWasteTotal": 60,
  "recyclePercentage": 0.3
}
```
Response (example):
```json
{
   "success": true
}
```

**3️⃣ Get final result:**
```
GET /open/result/{id}
```
Response (example):
```json
{
   "energy": 126.0,
   "transportation": 95.0,
   "solidWaste": 49.68,
   "total": 270.68
}
```

---

## 📄 Project structure

```
challenge_actionlabs/src/main/java/br/com/actionlabs/carboncalc/
 ├── config/
 │    ├── Appconfig.java
 │    └── OpenRestController.java
 ├── dto/
 │    ├── CarbonCalculationResultDTO.java
 │    ├── ServerStatusDTO.java
 │    ├── StartCalcRequestDTO.java
 │    ├── StartCalcResponseDTO.java
 │    ├── TransportationDTO.java
 │    ├── UpdateCalcInfoRequestDTO.java
 │    └── UpdateCalcInfoResponseDTO.java
 ├── enums/
 │    └── TransportationType.java
 ├── model/
 │    ├── Calculation.java
 │    ├── EnergyEmissionFactor.java
 │    ├── SolidWasteEmissionFactor.java
 │    ├── Transportation.java
 │    └── TransportationEmissionFactor
 ├── repository/
 │    ├── CalculationRepository.java
 │    ├── EnergyEmissionFactorRepository.java
 │    ├── SolidWasteEmissionFactorRepository.java
 │    └── TransportationEmissionFactorRepository.java
 ├── rest/
 │    ├── StatusRestController.java 
 │    └── OpenRestController.java
 └── service/
      └── CalculationService.java
```

---

## 👨‍💻 Autor
Implemented by **Max Zimmermann dos Reis**  
As part of a technical test for **Action Labs**.
