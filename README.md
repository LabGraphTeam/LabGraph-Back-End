# QualityLab Pro-API RESTful for internal laboratory quality control.

<p align="center">
<img src="https://img.shields.io/static/v1?label=STATUS&message=In%20progress&color=RED&style=for-the-badge" alt="Em desenvolvimento"/>
</p>

## Technologies

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

## Description
The RESTful API for Laboratory Internal Quality Control is designed to assist clinical and research laboratories in monitoring and controlling the quality of their processes. This API provides endpoints to manage information related to control standards, test results, statistical analyses, and other activities essential for ensuring the accuracy and reliability of laboratory data.

## Implemented functionalities
The API efficiently manages package insert values and test values, offering simple and intuitive CRUD operations.

## Project Structure
```
.
├── .github/workflows     # GitHub Actions workflow configurations
├── src
│   ├── main
│   │   ├── java/leonardo/labutilities/qualitylabpro
│   │   │   ├── configs           # Application configurations
│   │   │   │   ├── date         # Date/time configurations
│   │   │   │   ├── docs         # API documentation configs
│   │   │   │   ├── rest         # REST configurations
│   │   │   │   └── security     # Security configurations
│   │   │   ├── constants        # Constant definitions
│   │   │   ├── controllers      # REST controllers
│   │   │   ├── dtos            # Data Transfer Objects
│   │   │   ├── entities        # Domain entities
│   │   │   ├── enums           # Enumerations
│   │   │   ├── repositories    # Data access layer
│   │   │   ├── services       # Business logic
│   │   │   └── utils          # Utility classes
│   │   └── resources
│   │       ├── db/migration    # Flyway migrations
│   │       └── application*.properties
│   └── test
│       └── java               # Test classes
├── database                   # Database scripts
├── nginx                      # Nginx configurations
└── docker-compose*.yml        # Docker compose files
```

### Key Components:
- `configs/`: Configuration classes for security, documentation, and more
- `controllers/`: REST API endpoints organized by domain
- `services/`: Business logic implementations
- `dtos/`: Data Transfer Objects for API requests/responses
- `entities/`: Domain model classes
- `repositories/`: Database access layer
- `utils/`: Helper classes and utilities
- `.github/workflows/`: CI/CD pipeline configurations

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

## Installation

#### Step 1. Clone the repository
Run the command below in Git Bash or Terminal to clone the repository:
```
git clone https://github.com/LeonardoMeireles55/QualityLabPro.git
```

#### Step 2. Running with docker-compose
In the project root directory, run the command:
```
docker-compose up or docker compose up
```

## Usage

#### Step 3. Access API documentation
```
http://localhost:8080/swagger-ui.html
```

## Services

### CoagulationAnalyticsService
Handles analytics related to coagulation tests.

### BiochemistryAnalyticsService
Handles analytics related to biochemistry tests.

### HematologyAnalyticsService
Handles analytics related to hematology tests.

## Controllers

### CoagulationAnalyticsController
Manages endpoints for coagulation analytics.

### BiochemistryAnalyticsController
Manages endpoints for biochemistry analytics.

### HematologyAnalyticsController
Manages endpoints for hematology analytics.

## React Recharts.js Front-end
<img width="1470" alt="Screenshot 2024-12-06 at 18 01 35" src="https://github.com/user-attachments/assets/4fca9580-c012-48ef-a3d7-bf264593ccf2">

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.

## License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0) - see the [LICENSE](LICENSE) file for details.

The GPL-3.0 ensures that:
- The software remains open source
- Any modifications or derived works must also be released under GPL-3.0
- Source code must be made available when distributing the software
