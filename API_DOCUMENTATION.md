# Genesis Fitness Auth Server API Documentation

## Base URL
```
https://app.gfproto.xyz
```

## Authentication

### Login
- **POST** `/auth/login`
- **Description:** Authenticate a user and receive a JWT.
- **Request Body:**  
  ```json
  {
    "email": "user@example.com",
    "password": "user_password"
  }
  ```
- **Response:**  
  Returns a `ReqResDTO` containing authentication details (typically a JWT).

---

### Refresh Token
- **POST** `/auth/refresh`
- **Description:** Refresh an authentication token.
- **Request Body:**  
  ```json
  {
    "refreshToken": "your_refresh_token"
  }
  ```
- **Response:**  
  Returns a `ReqResDTO` with a new token.

---

## Registration

### Register New User (Admin Only)
- **POST** `/admin/register`
- **Description:** Register a new user (admin access required).
- **Request Body:**  
  ```json
  {
    "email": "newuser@example.com",
    "password": "new_password",
    "role": "CLIENT|TRAINER|ADMIN"
  }
  ```
- **Response:**  
  Returns a `ReqResDTO` with registration status.

---

## User Management

### Get All Users (Admin Only)
- **GET** `/admin/user/get/all`
- **Description:** Retrieve all users.
- **Response:**  
  Returns a `ReqResDTO` with user list.

---

### Delete User (Admin Only)
- **POST** `/admin/user/delete`
- **Description:** Delete a user by email.
- **Request Body:**  
  ```json
  {
    "email": "user@example.com"
  }
  ```
- **Response:**  
  Returns a `ReqResDTO` with deletion status.

---

### Update Trainer User Password (Trainer or Admin)
- **POST** `/trainer/user/update/password`
- **Description:** Change a trainer user's password.
- **Request Body:**  
  ```json
  {
    "email": "user@example.com",
    "password": "current_password",
    "newPassword": "new_password"
  }
  ```
- **Response:**  
  Returns `true` if successful, `false` or `403` if credentials are invalid.

---

## Token Validation

### Validate Admin Token
- **GET** `/auth/token/validate/admin`
- **Description:** Check if the token is valid for admin.
- **Headers:**  
  `Authorization: Bearer <token>`
- **Response:**  
  `true` if valid.

---

### Validate Trainer Token
- **GET** `/auth/token/validate/trainer`
- **Description:** Check if the token is valid for trainer.
- **Headers:**  
  `Authorization: Bearer <token>`
- **Response:**  
  `true` if valid.

---

### Validate Client Token
- **GET** `/auth/token/validate/client`
- **Description:** Check if the token is valid for client.
- **Headers:**  
  `Authorization: Bearer <token>`
- **Response:**  
  `true` if valid.

---

## Health Check

### Ping
- **GET** `/ping`
- **Description:** Health check endpoint.
- **Response:**  
  `"Genesis Fitness User Authentication Server is HEALTHY.\n"`

---

## Security & Access Control

### Public Endpoints (No Authentication Required)
- `/auth/login`
- `/ping`
- `/public/**`

### Role-Based Access
- **ADMIN endpoints** (`/admin/**`): Require `ADMIN` authority
- **TRAINER endpoints** (`/trainer/**`): Require `TRAINER` or `ADMIN` authority  
- **CLIENT endpoints** (`/client/**`): Require `CLIENT` or `ADMIN` authority
- **Token validation endpoints**: Require corresponding role authority

### Authentication
- JWT-based authentication
- Stateless session management
- CSRF protection disabled
- CORS enabled with defaults

---

## Error Responses

### Common HTTP Status Codes
- **200 OK**: Request successful
- **400 Bad Request**: Invalid request format
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Endpoint does not exist
- **500 Internal Server Error**: Server error

### Example Error Response
```json
{
  "error": "Failed login attempt",
  "message": "Invalid credentials",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## Data Transfer Objects (DTOs)

### ReqResDTO
Used for most request/response operations. Fields may vary based on endpoint:
- `email`: User email address
- `password`: User password
- `newPassword`: New password (for password updates)
- `role`: User role (CLIENT, TRAINER, ADMIN)
- `refreshToken`: JWT refresh token
- `user`: User entity data
- `users`: List of user entities

---

## Development & Testing

### Local Development
```bash
# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Build with Maven
mvn clean package
```

### Docker
```bash
# Build Docker image
docker build -t gf-auth-server:latest .

# Run container
docker run -p 8080:8080 gf-auth-server:latest
```

---

## Notes
- All endpoints return JSON responses
- Passwords are encrypted using BCrypt
- JWT tokens should be included in Authorization header as `Bearer <token>`
- The server uses Spring Security with custom JWT authentication 