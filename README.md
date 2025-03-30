# Spring Boot Product Management API

A RESTful API for managing products built with Spring Boot 3.x.

## Features

- CRUD operations for products
- Input validation
- Exception handling
- Caching support
- API documentation with OpenAPI/Swagger
- Security with Spring Security
- H2 database for development
- Actuator endpoints for monitoring

## Prerequisites

- Java 17 or later
- Maven 3.6 or later

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/product-management-api.git
cd product-management-api
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring:boot run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/api-docs`

## H2 Console

Access the H2 database console at:
`http://localhost:8080/api/h2-console`

Default credentials:
- JDBC URL: `jdbc:h2:mem:demodb`
- Username: `sa`
- Password: `password`

## API Endpoints

### Products

- `POST /api/v1/products` - Create a new product
- `GET /api/v1/products` - Get all products
- `GET /api/v1/products/{id}` - Get product by ID
- `GET /api/v1/products/search` - Search products by criteria
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Delete product

## Security

The API is secured with basic authentication:
- Username: `admin`
- Password: `admin`

## Actuator Endpoints

The following actuator endpoints are available:
- `/api/actuator/health` - Application health
- `/api/actuator/info` - Application information
- `/api/actuator/metrics` - Application metrics

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 