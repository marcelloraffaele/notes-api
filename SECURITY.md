# Security Implementation

This document outlines the security enhancements implemented in the Notes API.

## Authentication

The API now requires basic HTTP authentication for all endpoints except:
- Swagger UI documentation (`/swagger-ui/**`)
- API documentation (`/v3/api-docs/**`)

### Default Credentials
- Username: `admin`
- Password: `password`

**Note**: These are default credentials for development/demo purposes. In production, use proper user management with secure password policies.

## Authorization

All authenticated users have access to all note operations. The current implementation uses role-based security with a single `USER` role.

## Input Validation

The API now validates all user inputs:

### Note Fields Validation
- **Title**: Required, max 255 characters
- **Content**: Required, max 5000 characters
- **Labels**: Optional, alphanumeric + hyphens/underscores only, max 50 characters each
- **URLs**: Optional, must be valid HTTP/HTTPS URLs
- **Color**: Optional, must be valid hex color code (e.g., #FF0000 or #F00)

### Path Variables Validation
- **ID**: Must be a positive integer (≥ 1)
- **Label**: Must contain only alphanumeric characters, hyphens, and underscores, max 50 characters

## Rate Limiting

Rate limiting is implemented per IP address:
- **Limit**: 100 requests per minute per IP (~1.67 requests/second)
- **Response**: HTTP 429 (Too Many Requests) when exceeded
- **Headers**: `X-Rate-Limit-Retry-After-Seconds` indicates retry delay

## CORS Policy

CORS is configured with restricted origins:
- **Allowed Origins**: `http://localhost:*`, `https://localhost:*`
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Credentials**: Allowed

## Security Logging

Security events are logged with the `SECURITY` logger:
- Authentication attempts
- API access by user
- Validation failures
- Constraint violations
- Unexpected errors

## Error Handling

The API returns structured error responses:
- Validation errors include field-specific details
- Generic errors hide internal system information
- Proper HTTP status codes are used

## Security Headers

The following security configurations are applied:
- Error responses don't expose internal details
- Stack traces are hidden from responses
- Binding errors are not included in responses

## Usage Examples

### Authentication Required
```bash
# This will return 401 Unauthorized
curl -X GET http://localhost:8080/notes

# This will work with authentication
curl -X GET http://localhost:8080/notes -u admin:password
```

### Input Validation
```bash
# Invalid note (missing title/content) will return 400 Bad Request
curl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -u admin:password \
  -d '{"title": "", "content": ""}'

# Valid note will be created
curl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -u admin:password \
  -d '{
    "title": "My Note",
    "content": "This is my note content",
    "labels": ["work", "important"],
    "urls": ["https://example.com"],
    "color": "#FF0000"
  }'
```

### Rate Limiting
Making too many requests quickly will result in:
```
HTTP/1.1 429 Too Many Requests
X-Rate-Limit-Retry-After-Seconds: 60
Content-Type: text/plain

Too many requests - rate limit exceeded
```

## Production Considerations

For production deployment, consider:

1. **Authentication**: Replace basic auth with JWT or OAuth2
2. **User Management**: Implement proper user registration and management
3. **Database**: Use database storage instead of in-memory lists
4. **Logging**: Configure log aggregation and monitoring
5. **Rate Limiting**: Adjust limits based on expected traffic
6. **CORS**: Configure specific allowed origins for your frontend
7. **HTTPS**: Enable HTTPS with proper certificates
8. **Security Headers**: Add additional security headers (HSTS, CSP, etc.)