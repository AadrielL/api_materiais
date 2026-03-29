# NBR 5410 Engine Documentation

## Introduction
This document provides comprehensive information about the Materials Calculation API, developed according to the NBR 5410 standards. This API allows users to calculate various materials based on user input and predefined parameters.

## API Endpoints

### 1. Calculate Materials
- **Endpoint:** `/api/calculate`
- **Method:** `POST`
- **Description:** Calculates the required materials based on the input parameters.
- **Request Body:**
  ```json
  {
    "length": float,
    "width": float,
    "height": float,
    "materialType": string
  }
  ```
- **Response:**
  ```json
  {
    "totalMaterials": float,
    "unit": string,
    "breakdown": [
        { "material": string, "amount": float },
        ...
    ]
  }
  ```

### 2. Get Available Material Types
- **Endpoint:** `/api/materials`
- **Method:** `GET`
- **Description:** Retrieves a list of available material types.
- **Response:**
  ```json
  [
    { "materialType": string },
    ...
  ]
  ```

### 3. Get Calculator Configuration
- **Endpoint:** `/api/config`
- **Method:** `GET`
- **Description:** Returns configuration settings for the calculator.
- **Response:**
  ```json
  {
    "settings": { "maxLength": float, "maxWidth": float, ... }
  }
  ```

## Technical Features
- **Standards Compliance:** Adheres to NBR 5410 standards for electrical installations.
- **Response Time:** Average response time is under 200ms for well-structured requests.
- **Error Handling:** Implements standardized error responses with HTTP status codes.
- **Authentication:** API key-based authentication to secure your data.

## Architecture
- **Microservices Based:** The API is built on a microservices architecture that separates different functionalities into individual services.
- **Database:** Utilizes a NoSQL database to store material types and calculation settings.
- **Cloud Deployment:** Deployed on a cloud platform for scalability, availability, and performance.

## Conclusion
This API serves as a robust solution for engineers and architects to calculate material requirements efficiently, ensuring compliance with NBR 5410 standards.