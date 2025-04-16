# Java + Spring Boot Style Guide

## General Principles

- Use Spring Boot annotations and conventions to minimize boilerplate code
- Prefer Java 17+ features and syntax for modern, clean code
- Follow SOLID principles and clean architecture patterns
- Keep classes focused and single-purpose
- Use dependency injection rather than direct instantiation
- Favor composition over inheritance
- Use consistent naming and coding conventions

## Project Structure

- Follow standard Maven/Gradle project structure
- Organize code in packages by feature/domain rather than technical layers
- Use meaningful package names (`com.company.project.feature`)
- Separate business logic from infrastructure concerns

## Classes

- Use PascalCase for class names
- One class per file
- Follow standard naming conventions:
  - Service classes end with `Service`
  - Repository classes end with `Repository`
  - Controller classes end with `Controller`
  - Entity classes represent domain objects without suffixes

## Spring Components

- Use constructor injection instead of field injection
- Annotate classes appropriately: `@Service`, `@Repository`, `@Controller`, etc.
- Keep controllers thin - delegate business logic to services
- Use Spring's stereotypes consistently

## REST Controllers

- Use RESTful conventions for endpoint URLs
- Group related endpoints in the same controller
- Use appropriate HTTP methods (GET, POST, PUT, DELETE)
- Return proper HTTP status codes
- Validate request parameters and bodies
- Document APIs with OpenAPI/Swagger annotations

## Data Layer

- Use Spring Data repositories for database access
- Define clear entity relationships
- Use appropriate JPA annotations
- Create DTOs for transferring data between layers
- Avoid exposing entities directly to the web layer

## Exception Handling

- Create custom exceptions for business-specific errors
- Use `@ControllerAdvice` for global exception handling
- Return consistent error responses
- Log exceptions appropriately

## Configuration

- Use application.properties/application.yml for configuration
- Create separate config files for different environments
- Externalize sensitive information (credentials, API keys)
- Use Spring profiles for environment-specific configuration

## Testing

- Write unit tests for service and utility classes
- Write integration tests for repositories and controllers
- Use MockMvc for testing REST endpoints
- Use appropriate testing annotations (`@SpringBootTest`, `@WebMvcTest`, etc.)
- Organize test classes to mirror the structure of production code

## Lombok

- Use Lombok to reduce boilerplate code
- Prefer `@Getter`/`@Setter` over manually written methods
- Use `@Builder` for complex object construction
- Use `@RequiredArgsConstructor` for constructor injection
- Be consistent with Lombok usage throughout the codebase

## Code Quality

- Use static analysis tools (SonarQube, Checkstyle)
- Follow consistent code formatting
- Keep methods small and focused
- Document public APIs with JavaDoc
- Use meaningful variable and method names

## Performance Considerations

- Use caching where appropriate
- Be aware of N+1 query problems
- Use pagination for large result sets
- Consider async processing for long-running operations

## Security

- Use Spring Security for authentication and authorization
- Never store sensitive information in plain text
- Validate and sanitize all user inputs
- Protect against common vulnerabilities (XSS, CSRF, SQL injection)

## Logging

- Use SLF4J for logging
- Define appropriate log levels
- Include relevant context in log messages
- Avoid logging sensitive information


## Structure
```
└── 📁src
    └── 📁main
        └── 📁java
            └── 📁leonardo
                └── 📁labutilities
                    └── 📁qualitylabpro
                        └── 📁configs
                            └── 📁constants
                                └── ApiEndpoints.java
                            └── 📁database
                                └── FlywayConfig.java
                            └── 📁docs
                                └── SpringDocConfiguration.java
                            └── 📁pagination
                                └── CustomPageableResolver.java
                            └── 📁rest
                                └── WebConfig.java
                            └── 📁security
                                └── CorsConfig.java
                                └── SecurityConfiguration.java
                                └── SecurityFilter.java
                        └── ControlApplication.java
                        └── 📁domains
                            └── 📁analytics
                                └── 📁components
                                    └── AnalyticFailedNotificationComponent.java
                                    └── AnalyticObjectValidationComponent.java
                                    └── RulesProviderComponent.java
                                    └── SpecsValidatorComponent.java
                                    └── StatisticsCalculatorComponent.java
                                └── 📁constants
                                    └── AvailableAnalyticsNames.java
                                    └── AvailableBiochemistryAnalytics.java
                                    └── AvailableCoagulationAnalytics.java
                                    └── AvailableHematologyAnalytics.java
                                    └── ThresholdAnalyticsRules.java
                                    └── ValidationAnalyticsDescriptions.java
                                └── 📁controllers
                                    └── AnalyticsController.java
                                    └── AnalyticsHelperController.java
                                    └── BiochemistryAnalyticsController.java
                                    └── CoagulationAnalyticsController.java
                                    └── ControlLotController.java
                                    └── EquipmentController.java
                                    └── HematologyAnalyticsController.java
                                └── 📁dtos
                                    └── 📁requests
                                        └── AnalyticsDateRangeParamsDTO.java
                                        └── AnalyticsDTO.java
                                        └── AnalyticsFiltersParamsDTO.java
                                        └── AnalyticsLevelDateRangeParamsDTO.java
                                        └── AnalyticsNameAndLevelDateRangeParamsDTO.java
                                        └── ComparativeErrorStatisticsParamsDTO.java
                                        └── ControlLotDTO.java
                                        └── EquipmentDTO.java
                                        └── UpdateAnalyticsMeanDTO.java
                                        └── UpdateEquipmentDTO.java
                                    └── 📁responses
                                        └── AnalyticsWithCalcDTO.java
                                        └── ComparativeErrorStatisticsDTO.java
                                        └── ErrorStatisticsDTO.java
                                        └── GroupedMeanAndStdByLevelDTO.java
                                        └── GroupedResultsByLevelDTO.java
                                        └── GroupedValuesByLevelDTO.java
                                        └── MeanAndStdDeviationDTO.java
                                └── 📁enums
                                    └── WorkSectorEnum.java
                                └── 📁helpers
                                    └── AnalyticsHelperUtility.java
                                └── 📁models
                                    └── Analytic.java
                                    └── ControlLot.java
                                    └── Equipment.java
                                └── 📁repositories
                                    └── AnalyticsRepository.java
                                    └── ControlLotRepository.java
                                    └── EquipmentRepository.java
                                └── 📁services
                                    └── AnalyticHelperService.java
                                    └── AnalyticsStatisticsService.java
                                    └── AnalyticsValidationService.java
                                    └── BiochemistryAnalyticService.java
                                    └── CoagulationAnalyticService.java
                                    └── ControlLotService.java
                                    └── EquipmentService.java
                                    └── HematologyAnalyticService.java
                                    └── IAnalyticHelperService.java
                                    └── IAnalyticsStatisticsService.java
                                    └── IAnalyticsValidationService.java
                                └── 📁utils
                                    └── MergeEquipmentsObjects.java
                            └── 📁shared
                                └── 📁authentication
                                    └── AuthenticationService.java
                                    └── 📁dtos
                                        └── 📁responses
                                            └── TokenJwtDTO.java
                                    └── TokenService.java
                                └── 📁blacklist
                                    └── AnalyticsBlackList.java
                                └── 📁components
                                    └── StringToLocalDateTimeConverter.java
                                └── 📁email
                                    └── 📁constants
                                        └── EmailTemplate.java
                                    └── 📁dto
                                        └── 📁requests
                                            └── EmailDTO.java
                                            └── RecoveryEmailDTO.java
                                    └── EmailService.java
                                └── 📁exception
                                    └── ApiError.java
                                    └── CustomGlobalErrorHandling.java
                                └── 📁mappers
                                    └── AnalyticMapper.java
                                    └── ControlLotMapper.java
                                    └── EquipmentMapper.java
                            └── 📁users
                                └── 📁components
                                    └── BCryptEncoderComponent.java
                                    └── PasswordRecoveryTokenManager.java
                                └── 📁controllers
                                    └── UsersController.java
                                └── 📁dtos
                                    └── 📁requests
                                        └── ForgotPasswordDTO.java
                                        └── RecoverPasswordDTO.java
                                        └── SignInUserDTO.java
                                        └── SignUpUsersDTO.java
                                        └── UpdatePasswordDTO.java
                                    └── 📁responses
                                        └── UsersDTO.java
                                └── 📁enums
                                    └── ChartType.java
                                    └── ExportFormat.java
                                    └── QualityControlRulesApplied.java
                                    └── Theme.java
                                    └── UserRoles.java
                                └── 📁models
                                    └── User.java
                                    └── UserConfig.java
                                └── 📁repositories
                                    └── UserRepository.java
                                └── 📁services
                                    └── UserService.java
        └── 📁resources
            └── application-dev.properties
            └── application-local.properties
            └── application-prod.properties
            └── application-test.properties
            └── application.properties
            └── 📁db
                └── 📁migration
                    └── V1__create-table-lot.sql
                    └── V10__alter-table-users.sql
                    └── V11__drop-tables.sql
                    └── V12__create-table-hematology.sql
                    └── V13__alter_table-hematology.sql
                    └── V14__create-index-on-generic_analytics.sql
                    └── V15__delete_table_hematology.sql
                    └── V16__alter-table-generic_analytics.sql
                    └── V17__alter-table-users.sql
                    └── V18__alter-table-users.sql
                    └── V19__alter-table-users.sql
                    └── V2__create-table-users.sql
                    └── V20__alter_table-generic_analytics.sql
                    └── V21__alter_table-generic_analytics.sql
                    └── V22__alter-table-generic_analytics.sql
                    └── V23__create-table-user_configs.sql
                    └── V24__alter-table-users.sql
                    └── V25__alter-table-generic_analytics.sql
                    └── V26__alter-table-analytics.sql
                    └── V27__alter-table-analytics.sql
                    └── V28__alter-analytics-index-order.sql
                    └── V29__alter-table-analytics.sql
                    └── V3__create-table-defaultvalues.sql
                    └── V30__alter-table-analytics.sql
                    └── V31__create-table-control_lots.sql
                    └── V32__create-table-equipments.sql
                    └── V33__alter-table-analytics.sql
                    └── V34__add-equipment-to-analytics.sql
                    └── V35__alter-table-equipments.sql
                    └── V36__alter-table-equipments.sql
                    └── V4__create-table-analytics.sql
                    └── V5__alter-table-users.sql
                    └── V6__alter-table-analytics.sql
                    └── V7__create-table-generic_analytics.sql
                    └── V8__create-index-on-generic_analytics.sql
                    └── V9__alter-table-generic_analytics.sql
            └── 📁META-INF
                └── additional-spring-configuration-metadata.json
                └── spring.factories
            └── 📁static
                └── favicon.ico
    └── 📁test
        └── 📁java
            └── 📁leonardo
                └── 📁labutilities
                    └── 📁qualitylabpro
                        └── 📁configs
                            └── TestSecurityConfig.java
                        └── ControlApplicationTests.java
                        └── 📁domains
                            └── 📁analytic
                                └── 📁components
                                    └── AnalyticFailedNotificationComponentTests.java
                                    └── AnalyticObjectValidationComponentTests.java
                                    └── RulesProviderComponentTests.java
                                    └── SpecsValidatorComponentTests.java
                                └── 📁controllers
                                    └── BiochemistryAnalyticControllerTests.java
                                    └── CoagulationAnalyticControllerTests.java
                                    └── HematologyAnalyticControllerTests.java
                                └── 📁models
                                    └── AnalyticTests.java
                                └── 📁repositories
                                    └── AnalyticRepositoryTests.java
                                └── 📁services
                                    └── AnalyticHelperServiceTests.java
                                    └── AnalyticServiceTests.java
                                    └── AnalyticsValidationServiceTests.java
                                    └── BiochemistryAnalyticServiceTests.java
                                    └── CoagulationAnalyticServiceTests.java
                                    └── HematologyAnalyticServiceTests.java
                            └── 📁shared
                                └── 📁authentication
                                    └── AuthenticationServiceTests.java
                                    └── TokenServiceTests.java
                                └── 📁email
                                    └── EmailServiceTests.java
                            └── 📁user
                                └── 📁components
                                    └── PasswordRecoveryTokenManagerTests.java
                                └── 📁controllers
                                    └── UsersControllerTests.java
                                └── 📁models
                                    └── UserTests.java
                                └── 📁repositories
                                    └── UserRepositoryTests.java
                                └── 📁services
                                    └── UserServiceTests.java
                        └── 📁utils
                            └── AnalyticsHelperMocks.java
```