# LabGraph-API: Advanced Internal Laboratory Quality Control System

[![Docker Image CI/CD](https://github.com/LabGraphTeam/LabGraph-Back-End/actions/workflows/backend-deploy.yml/badge.svg?branch=main)](https://github.com/LabGraphTeam/LabGraph-Back-End/actions/workflows/backend-deploy.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Technologies

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

## Overview

LabGraph-API is a comprehensive RESTful API designed to revolutionize internal quality control processes in clinical and research laboratories. Built with modern technologies and best practices, this system helps laboratories monitor, analyze, and ensure the reliability of their analytical processes through robust statistical analysis and intuitive data visualization.

### Key Features

- **Comprehensive Control Standard Management**: Track and manage control materials with detailed metadata
- **Statistical Analysis Engine**: Perform Westgard rule evaluations, trend analysis, and variance monitoring
- **Multi-level User Management**: Role-based access control with customizable permissions
- **Real-time Monitoring**: Get instant alerts when quality issues are detected
- **Interactive Data Visualization**: View quality metrics through intuitive charts and graphs
- **Audit Trail**: Complete history of all quality control activities
- **Scalable Architecture**: Designed to handle laboratories of any size

## Architecture

LabGraph-API follows a modular architecture:

```
├── .github/workflows      # GitHub Actions workflow configurations
├── src/
│   ├── main/
│   │   ├── java/leonardo/labutilities/qualitylabpro/
│   │   │   ├── ControlApplication.java
│   │   │   ├── configs/            # Application configurations
│   │   │   │   ├── constants/     # System constants
│   │   │   │   ├── database/      # Database configurations
│   │   │   │   ├── date/          # Date/time configurations
│   │   │   │   ├── docs/          # API documentation
│   │   │   │   ├── rest/          # REST configurations 
│   │   │   │   └── security/      # Security settings
│   │   │   └── domains/           # Domain-driven modules
│   │   │       ├── analytics/     # Analytics domain
│   │   │       │   ├── controllers/
│   │   │       │   ├── dtos/
│   │   │       │   ├── models/
│   │   │       │   ├── repositories/
│   │   │       │   └── services/
│   │   │       ├── shared/        # Shared components
│   │   │       │   ├── enums/
│   │   │       │   ├── exceptions/
│   │   │       │   └── utils/
│   │   │       └── users/         # User management domain
│   │   │           ├── controllers/
│   │   │           ├── dtos/
│   │   │           ├── models/
│   │   │           ├── repositories/
│   │   │           └── services/
│   │   └── resources/
│   │       ├── db/migration/     # Flyway migrations
│   │       └── application.properties
│   └── test/
│       └── java/                # Test classes
├── database/                    # Database scripts
├── docker/                      # Docker configurations
│   ├── dev/                    # Development environment
│   └── prod/                   # Production environment
├── nginx/                      # Nginx configurations
├── docker-compose.yml          # Docker compose files
└── pom.xml                     # Maven configuration
```

### Architectural Highlights

- `configs/`: Configuration classes organized by purpose
  - `constants/`: System-wide constants
  - `database/`: Database configurations
  - `security/`: Security settings and configurations
- `domains/`: Domain modules
  - `analytics/`: Analytics domain components
  - `shared/`: Cross-cutting concerns and utilities
  - `users/`: User management domain
- Each domain contains:
  - `controllers/`: REST endpoints
  - `dtos/`: Data Transfer Objects
  - `models/`: Domain entities and components
  - `repositories/`: Data access layer
  - `services/`: Business logic
- `docker/`: Environment-specific Docker configurations

## Getting Started

### Prerequisites

* [Java 21](https://www.oracle.com/br/java/technologies/javase/jdk21-archive-downloads.html)
* [MariaDB 11.2](https://mariadb.org/download/?t=mariadb&p=mariadb&r=11.7.2)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/get-started/)
* [Git](https://git-scm.com/)

### Installation Options

#### Option 1: Docker Development Environment (Recommended)

This method provides a complete development environment with live reload capabilities.

1. **Clone the repository**
   ```bash
   git clone https://github.com/LabGraphTeam/LabGraph-Back-End.git
   cd LabGraph-Back-End
   ```

2. **Configure environment variables**

   ```bash
   DB_USER=root
   DB_ROOT_PASSWORD=root
   DB_DATABASE=lab_api_alpha
   DB_DATABASE_TEST=lab_api_test
   DB_LOCAL_PORT=3306
   DB_DOCKER_PORT=3306
   SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/lab_api_test
   API_SECURITY_TOKEN_SECRET=your_secret_token_here_123456789
   API_SECURITY_ISSUER=example-api-issuer
   SERVER_LOCAL_PORT=8080
   SERVER_DOCKER_PORT=8080
   SPRING_MAIL_USERNAME=example.email@example.com
   SPRING_MAIL_PASSWORD=examplePassword123!
   EMAIL_TO_SEND_LIST=recipient@example.com
   ```
   ```bash
   cp .env.example .env
   # Edit .env with your preferred settings
   ```

4. **Start the development stack**
   ```bash
   docker compose --profile dev up --build
   ```

   This will start:
   - MariaDB database (accessible at localhost:3306)
   - Spring Boot application with live reload (accessible at localhost:8080)

#### Option 2: Local Maven Execution

For development without Docker:

1. **Clone and configure**
   ```bash
   git clone https://github.com/LabGraphTeam/LabGraph-Back-End.git
   cd LabGraph-Back-End
   cp .env.example .env
   # Edit .env with your preferred settings
   ```

2. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

   Note: You'll need to have a MariaDB instance running separately.

#### Option 3: Production-like Environment

To test in a production-like environment:

```bash
docker compose --profile prod up --build
```

This starts a fully optimized stack with Nginx as reverse proxy.

## Development Workflow

### Live Reload

The development environment supports hot-reloading:

- Source code is mounted as a volume in the container
- Changes to Java files trigger automatic recompilation
- Application restarts automatically with changes

### API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

Use this interface to explore endpoints, models, and test API calls directly.

### Database Management

The database is accessible at:
- Host: localhost
- Port: 3306
- Credentials: As configured in your .env file

Database migrations are managed through Flyway and run automatically on startup.

## Testing

### Running Unit Tests

```bash
# Standard test execution
./mvnw test

# With detailed logging
./mvnw test -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG

# With extra debugging information
./mvnw test -X
```

### Integration Tests

```bash
./mvnw verify
```

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and delivery:

1. **Build & Test**: Runs on every pull request and push to main
2. **Code Quality**: Static analysis with SonarQube
3. **Docker Build**: Creates optimized Docker images
4. **Deployment**: Automatic deployment to staging/production environments

Details are available in the `.github/workflows` directory.

## Front-end Integration

LabGraph includes a React-based front-end with Recharts.js for visualization:

![demo2](https://github.com/user-attachments/assets/453af7d7-91b7-4b4c-b9d3-17915f9fe760)

The front-end repository is available at [LabGraphTeam/LabGraph-Front-End](https://github.com/LabGraphTeam/LabGraph-Front-End).

## Contributing

We welcome contributions from the community! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Implement your changes**
4. **Run tests**
   ```bash
   ./mvnw test
   ```
5. **Commit your changes**
   ```bash
   git commit -m "Add your detailed commit message"
   ```
6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Open a pull request**

### Contribution Guidelines

- Follow the existing code style
- Write unit tests for new features
- Update documentation for API changes
- Reference any related issues in your PR

## Deployment

### Environment Configuration

Production deployments should include:

- Proper database credentials in .env
- Configured SSL certificates for HTTPS
- Adjusted memory limits for JVM
- Database backup strategy

### Docker Deployment

```bash
 # Pull the latest image
docker pull leonardomeireles55/quality-lab-pro-back-end:latest

# Run with your environment variables
docker run -d -p 8080:8080 --env-file .env leonardomeireles55/quality-lab-pro-back-end:latest
```

### Cloud Hosting

LabGraph is optimized for deployment on DigitalOcean:

[![DigitalOcean Referral Badge](https://web-platforms.sfo2.cdn.digitaloceanspaces.com/WWW/Badge%203.svg)](https://www.digitalocean.com/?refcode=c961dfd401d8&utm_campaign=Referral_Invite&utm_medium=Referral_Program&utm_source=badge)

## Support

For questions, issues, or support:
- Open an issue on GitHub
- Contact the development team at labgraph.suport2025@gmail.com

## License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0) - see the [LICENSE](LICENSE) file for details.

The GPL-3.0 ensures that:

- The software remains open source
- Any modifications or derived works must also be released under GPL-3.0
- Source code must be made available when distributing the software

---

© 2025 LabGraph Team. All rights reserved.
