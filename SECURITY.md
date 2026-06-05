# Security Implementation Documentation

## Overview
This document describes the comprehensive security measures implemented in the Notes API to address all identified vulnerabilities.

## Authentication and Authorization

### JWT-Based Authentication
- **Implementation**: JWT tokens with HS384 signing algorithm
- **Token Expiration**: 24 hours (configurable via `app.jwtExpirationMs`)
- **Secret Key**: Configurable via `app.jwtSecret` property
- **Storage**: Stateless - no server-side session storage

### User Management
- **Default Users**:
  - `user` / `password` (ROLE_USER)
  - `admin` / `admin` (ROLE_ADMIN, ROLE_USER)
- **Password Encoding**: BCrypt with default strength
- **User Store**: In-memory (suitable for demo - use database for production)

### Protected Endpoints
All `/notes/**` endpoints require authentication:
- `GET /notes` - List all notes
- `GET /notes/{id}` - Get specific note
- `POST /notes` - Create new note
- `PUT /notes/{id}` - Update note
- `DELETE /notes/{id}` - Delete note
- `GET /notes/label/{label}` - Search by label

### Public Endpoints
- `POST /auth/signin` - Authentication
- `POST /auth/signout` - Logout
- `/v3/api-docs/**` - OpenAPI documentation
- `/swagger-ui/**` - Swagger UI

## Input Validation and Sanitization

### Note Model Validation
```java
@NotBlank(message = "Title is required")
@Size(max = 200, message = "Title must not exceed 200 characters")
private String title;

@NotBlank(message = "Content is required") 
@Size(max = 5000, message = "Content must not exceed 5000 characters")
private String content;

@NotNull(message = "Labels cannot be null")
@Size(max = 10, message = "Maximum 10 labels allowed")
private List<@NotBlank @Size(max = 50) String> labels;

@NotNull(message = "URLs cannot be null")
@Size(max = 5, message = "Maximum 5 URLs allowed")
private List<@Pattern(regexp = "^https?://.*", message = "URLs must start with http:// or https://") String> urls;

@Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code (e.g., #FF0000)")
private String color;
```

### Path Parameter Validation
- ID parameters validated with `@Min(1)` annotation
- Label parameters sanitized to remove potentially dangerous characters
- Maximum length enforcement (50 characters)

### Global Exception Handling
- Structured error responses for validation failures
- No sensitive information leakage in error messages
- Consistent error format across all endpoints

## Rate Limiting

### Implementation
- **Library**: Bucket4j
- **Limit**: 100 requests per minute per IP address
- **Storage**: In-memory cache (ConcurrentHashMap)
- **Response**: HTTP 429 (Too Many Requests) when limit exceeded

### Configuration
```java
private static final int REQUESTS_PER_MINUTE = 100;
Bandwidth limit = Bandwidth.classic(REQUESTS_PER_MINUTE, 
    Refill.intervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
```

### Monitoring
- Rate limit violations logged with IP address
- Debug logging for bucket creation

## Security Logging and Monitoring

### Authentication Events
- Successful login attempts with username and IP
- Failed login attempts with username and IP  
- Logout events with username and IP

### API Access Logging
- All authenticated requests logged with:
  - Username
  - HTTP method and URI
  - IP address (including X-Forwarded-For support)

### Security Violations
- Unauthorized access attempts
- Rate limiting violations
- Authentication failures
- Input validation failures

### Log Levels
- **INFO**: Successful authentication, API access
- **WARN**: Failed authentication, rate limiting, validation failures
- **ERROR**: System errors, unexpected exceptions

## CORS Configuration

### Allowed Origins
- `http://localhost:*` - Development environments
- `https://localhost:*` - Secure development environments

### Allowed Methods
- GET, POST, PUT, DELETE, OPTIONS

### Allowed Headers
- All headers (`*`) - but restricted by origins

### Security Settings
- `allowCredentials: true` - Enables cookie/auth header support
- `maxAge: 3600` - Preflight cache duration (1 hour)

## Security Headers

### Implemented Headers
- **X-Frame-Options**: DENY - Prevents clickjacking
- **X-Content-Type-Options**: nosniff - Prevents MIME type sniffing
- **Cache-Control**: no-cache, no-store, max-age=0, must-revalidate
- **Pragma**: no-cache
- **Expires**: 0
- **Strict-Transport-Security**: max-age=31536000; includeSubDomains (HSTS)

### Additional Security Features
- CSRF protection disabled (stateless JWT approach)
- Session management set to STATELESS
- Secure frame options to prevent embedding

## Configuration Properties

### Security Settings
```properties
# JWT Configuration
app.jwtSecret=notesApiSecretKeyForJWTTokenGenerationAndValidation2024!
app.jwtExpirationMs=86400000

# Error Handling (Security)
server.error.include-message=never
server.error.include-binding-errors=never  
server.error.include-stacktrace=never
server.error.include-exception=false

# Logging
logging.level.com.rmarcello.note.security=INFO
logging.level.com.rmarcello.note.controller=INFO
logging.level.org.springframework.security=WARN
```

## Testing Security

### Manual Testing Commands
```bash
# Test unauthenticated access (should return 401)
curl http://localhost:8080/notes

# Authenticate and get token
curl -X POST http://localhost:8080/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "password"}'

# Use token for authenticated requests
curl -H "Authorization: Bearer <token>" http://localhost:8080/notes

# Test input validation (should return 400 with validation errors)
curl -X POST http://localhost:8080/notes \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title": "", "content": "", "urls": ["invalid-url"], "color": "invalid"}'

# Test CORS preflight
curl -X OPTIONS -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization" \
  http://localhost:8080/notes -I
```

## Production Considerations

### Recommendations for Production Deployment

1. **JWT Secret**: Use a strong, randomly generated secret key
2. **User Storage**: Replace in-memory users with database-backed storage
3. **Password Policy**: Implement strong password requirements
4. **Token Rotation**: Consider shorter expiration times with refresh tokens
5. **Rate Limiting**: Consider distributed rate limiting for multiple instances
6. **Logging**: Integrate with centralized logging system (ELK stack, etc.)
7. **Monitoring**: Set up alerts for security violations
8. **HTTPS**: Enforce HTTPS in production with proper TLS configuration
9. **Database Security**: Use parameterized queries, connection encryption
10. **Environment Variables**: Externalize all sensitive configuration

### Security Hardening
- Regular security dependency updates
- Penetration testing
- Security code reviews
- Vulnerability scanning
- Audit log monitoring
- Incident response procedures

## Compliance

### Security Standards Addressed
- OWASP Top 10 vulnerabilities
- Input validation and sanitization
- Authentication and session management
- Access control
- Security logging and monitoring
- Error handling and information disclosure
- Communication security (HTTPS readiness)

This implementation provides a solid foundation for secure API operations while maintaining usability and performance.