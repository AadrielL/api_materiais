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

## ⚙️ Variáveis de Ambiente (Environment Variables)

A API de Materiais lê configurações sensíveis exclusivamente por variáveis de ambiente.

| Variável | Obrigatória? | Descrição | Exemplo Local |
|---|---|---|---|
| `JWT_SECRET` | **Sim** | Segredo para validação dos tokens JWT gerados pela `api_auth`. **Deve ser idêntico ao da api_auth**. | `sua_chave_secreta_jwt_deve_ter_pelo_menos_32_caracteres` |
| `DB_URL` | Não | JDBC URL do PostgreSQL (default: `jdbc:postgresql://localhost:5432/postgres`) | `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USERNAME` | Não | Usuário do PostgreSQL (default: `postgres`) | `postgres` |
| `DB_PASSWORD` | Não | Senha do PostgreSQL (default: `1234`) | `1234` |
| `JPA_DDL` | Não | Estratégia de DDL do Hibernate (default: `update`) | `update` ou `validate` (em prod) |

---

### 💻 Executando em Ambiente Local

1. **Via PowerShell (Windows):**
   ```powershell
   $env:JWT_SECRET="sua_chave_secreta_jwt_deve_ter_pelo_menos_32_caracteres"
   ./mvnw.cmd spring-boot:run
   ```

2. **Via Bash (Linux / Mac):**
   ```bash
   export JWT_SECRET="sua_chave_secreta_jwt_deve_ter_pelo_menos_32_caracteres"
   ./mvnw spring-boot:run
   ```

---

### 🚀 Configuração em Produção (Render / Railway / Docker)

Defina as variáveis no painel da plataforma de deploy (aba *Environment Variables*):
- `JWT_SECRET`: *[Mesma chave configurada na api_auth]*
- `DB_URL`: `jdbc:postgresql://<host>:<port>/<database>`
- `DB_USERNAME`: `<usuario>`
- `DB_PASSWORD`: `<senha>`
- `JPA_DDL`: `update`

---

## Conclusion
This API serves as a robust solution for engineers and architects to calculate material requirements efficiently, ensuring compliance with NBR 5410 standards.