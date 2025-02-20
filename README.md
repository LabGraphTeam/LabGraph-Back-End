# LabGraph-API for internal laboratory quality control.

[![Docker Image CI/CD](https://github.com/LabGraphTeam/LabGraph-Back-End/actions/workflows/backend-deploy.yml/badge.svg?branch=main)](https://github.com/LabGraphTeam/LabGraph-Back-End/actions/workflows/backend-deploy.yml)

## Technologies

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

## Description

The RESTful API for Laboratory Internal Quality Control is designed to assist clinical and research laboratories in
monitoring and controlling the quality of their processes. This API provides endpoints to manage information related to
control standards, test results, statistical analyses, and other activities essential for ensuring the accuracy and
reliability of laboratory data.

## Project Structure

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

### Key Components:

- `configs/`: Configuration classes organized by purpose
  - `constants/`: System-wide constants
  - `database/`: Database configurations
  - `security/`: Security settings and configurations
- `domains/`: Domain-driven design modules
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

## Continuous Integration

This project uses GitHub Actions for automated testing and deployment. Our CI/CD pipeline includes:

- Automated build and test execution
- Code quality checks
- Docker image building
- Automated deployment to staging/production environments

You can view the workflow configurations in the `.github/workflows` directory.

## Requirements

* [Java 21](https://www.oracle.com/br/java/technologies/javase/jdk21-archive-downloads.html)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/get-started/)
* [Git](https://git-scm.com/)

## Installation & Local Development

### Running Locally with Docker (Recommended)

#### 1. Clone the repository
```bash
git clone https://github.com/LabGraphTeam/LabGraph-Back-End.git
cd LabGraph-Back-End
```

#### 2. Environment Setup
Create a `.env` file in the root directory with the required environment variables (see `.env.example`).

#### 3. Start Development Environment
Run the development stack with live reload support:
```bash
docker compose --profile dev up --build
```

This will start:
- MariaDB database (accessible at localhost:3306)
- Spring Boot application with live reload (accessible at localhost:8080)

The application will automatically reload when you make changes to the source code.

### Alternative Methods

#### Running with Maven (without Docker)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Running Production Environment Locally
```bash
docker compose --profile prod up --build
```

## Development

### Live Reload
The development environment is configured with live reload support:
- Source code is mounted as a volume in the container
- Maven is configured to detect changes and reload the application
- Changes to Java files will trigger automatic recompilation

### Accessing Services
- API Documentation: http://localhost:8080/swagger-ui.html
- Database: localhost:3306 (credentials in .env file)

## Running Tests

### With Maven

To run tests with detailed output:

```bash
./mvnw test -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG
```

For even more detailed test output:

```bash
./mvnw test -X
```

## React Recharts.js Front-end
![demo2](https://github.com/user-attachments/assets/453af7d7-91b7-4b4c-b9d3-17915f9fe760)

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature/branch`).
6. Open a pull request.

## Misc
[![DigitalOcean Referral Badge](https://web-platforms.sfo2.cdn.digitaloceanspaces.com/WWW/Badge%203.svg)](https://www.digitalocean.com/?refcode=c961dfd401d8&utm_campaign=Referral_Invite&utm_medium=Referral_Program&utm_source=badge)

## License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0) - see the [LICENSE](LICENSE) file for
details.

The GPL-3.0 ensures that:

- The software remains open source
- Any modifications or derived works must also be released under GPL-3.0
- Source code must be made available when distributing the software
