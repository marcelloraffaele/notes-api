# Note-API

## Introduction

This project is a demo application built using Spring Boot. It provides a secure RESTful API for managing notes with comprehensive security features including JWT authentication, input validation, rate limiting, and security monitoring.

## Security Features

### 🔐 Authentication & Authorization
- JWT-based authentication with Bearer tokens
- Role-based access control (USER, ADMIN roles)
- 24-hour token expiration (configurable)
- Secure password encoding with BCrypt

### ✅ Input Validation & Sanitization
- Comprehensive Bean Validation on all inputs
- Title/content length limits and requirements
- URL format validation (must start with http/https)
- Hex color code validation
- Label and URL count restrictions
- Path parameter sanitization

### 🚦 Rate Limiting & Throttling
- 100 requests per minute per IP address
- HTTP 429 responses for violations
- In-memory rate limiting with Bucket4j
- Configurable rate limits

### 📊 Security Logging & Monitoring
- Authentication success/failure logging
- API access logging with user context
- Rate limiting violation tracking
- IP address monitoring (X-Forwarded-For support)
- Structured security event logging

### 🛡️ Additional Security Measures
- Secure CORS configuration (localhost only)
- Security headers (X-Frame-Options, HSTS, etc.)
- Input sanitization for XSS prevention
- Structured error responses without information leakage
- CSRF protection (stateless JWT approach)

## Default Credentials

For testing purposes, the application includes these default users:
- **Username**: `user`, **Password**: `password` (ROLE_USER)
- **Username**: `admin`, **Password**: `admin` (ROLE_ADMIN, ROLE_USER)

⚠️ **Important**: Change these credentials before production deployment!

## API Authentication

### 1. Login to get JWT token:
```bash
curl -X POST http://localhost:8080/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "password"}'
```

### 2. Use the token in subsequent requests:
```bash
curl -H "Authorization: Bearer <your-jwt-token>" \
  http://localhost:8080/notes
```

## How to run it using command line

To run the application from the command line, follow these steps:

1. Clone the repository from GitHub:
   ```sh
   git clone https://github.com/marcelloraffaele/notes-api.git
   cd notes-api
   ```

2. Build the project using Maven:
   ```sh
   ./mvn clean install
   ```

3. Run the Spring Boot application:
   ```sh
   ./mvn spring-boot:run
   ```

The application will start and be accessible at `http://localhost:8080`.

## How to change properties

To change the properties of the application, you can modify the `application.properties` file located in the `src/main/resources` directory. 

### Security Configuration
```properties
# JWT Configuration
app.jwtSecret=your-secret-key-here
app.jwtExpirationMs=86400000

# Server Configuration
server.port=9090
```

### Other Properties
For example, to change the server port, you can add the following line to the `application.properties` file:

```properties
server.port=9090
```

This will change the server port to `9090`. You can also add other properties as needed.

## Note Fields

Each note has the following fields:
- `id`: The unique identifier of the note.
- `title`: The title of the note (required, max 200 characters).
- `content`: The content of the note (required, max 5000 characters).
- `labels`: A list of labels associated with the note (max 10 labels, each max 50 characters).
- `urls`: A list of URLs associated with the note (max 5 URLs, must start with http/https).
- `color` (optional): The color of the note, which must be a valid HTML hex color code.

### Examples of valid HTML hex color codes:
- Black: `#000000`
- White: `#FFFFFF`
- Red: `#FF0000`
- Green: `#00FF00`

## Use it from docker

To run the application from docker, follow these steps:
```sh
docker pull ghcr.io/marcelloraffaele/notes-api:latest
docker run -p 8080:8080 ghcr.io/marcelloraffaele/notes-api:latest
```

The application will start and be accessible at `http://localhost:8080`.

### API Documentation
The swagger UI will be accessible at `http://localhost:8080/swagger-ui/index.html`

### Security Testing
See `client.rest` for authenticated API examples and `SECURITY.md` for comprehensive security documentation.

## Security Documentation

For detailed security implementation information, see [SECURITY.md](SECURITY.md).
