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
├── .github/workflows     # GitHub Actions workflow configurations
├── src/
│   ├── main/
│   │   ├── java/leonardo/labutilities/qualitylabpro/
│   │   │   ├── configs/           # Application configurations
│   │   │   │   ├── date/         # Date/time configurations
│   │   │   │   ├── docs/         # API documentation
│   │   │   │   ├── rest/         # REST configurations 
│   │   │   │   └── security/     # Security settings
│   │   │   ├── controllers/      # REST endpoints
│   │   │   ├── dtos/            # Data Transfer Objects
│   │   │   ├── entities/        # Domain entities
│   │   │   ├── repositories/    # Data access layer
│   │   │   ├── services/       # Business logic
│   │   │   └── utils/          # Helper classes
│   │   └── resources/
│   │       ├── db/migration/    # Flyway migrations
│   │       └── application.properties
│   └── test/
│       └── java/               # Test classes
├── database/                   # Database scripts
├── nginx/                      # Nginx configurations
├── docker-compose.yml          # Docker compose files
└── pom.xml                     # Maven configuration
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
git clone https://github.com/LabGraphTeam/LabGraph-Back-End.git
```

#### Step 2. Running with docker-compose

In the project root directory, run the command:

```
docker compose -f docker-compose-dev.yml up --build    
```

## Usage

#### Step 3. Access API documentation

```
http://localhost:8080/swagger-ui.html
```

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

<img width="1470" alt="Screenshot 2024-12-06 at 18 01 35" src="https://github.com/user-attachments/assets/4fca9580-c012-48ef-a3d7-bf264593ccf2">

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
