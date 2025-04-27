# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands
- Run application: `mvn spring-boot:run`
- Run with production profile: `mvn -Pproduction spring-boot:run`
- Run single test: `mvn test -Dtest=TestClassName`
- Run specific test method: `mvn test -Dtest=TestClassName#methodName`
- Package application: `mvn package`
- Build Docker image: `./02_build_docker.sh`
- Run in Docker: `./03_run_docker.sh`

## Code Style Guidelines
- Java version: 17
- Framework: Spring Boot with Vaadin
- Entity pattern: Extend AbstractEntity for JPA entities
- Tests: JUnit 5 with descriptive method names
- Imports: Organized by package, no wildcard imports
- Naming: CamelCase for classes, camelCase for methods/variables
- Error handling: Use appropriate exceptions with descriptive messages
- TypeScript/React used for frontend components
- Lombok annotations preferred for boilerplate reduction
- Repository pattern for data access

## Architecture Guidelines
- Follow hexagonal architecture (ports and adapters pattern)
- Domain models should be immutable Java records
- UI layer must not directly use JPA entities
- Use application services to coordinate between UI and domain
- Adapters should translate between JPA entities and domain records
- Only create UI-specific form records when requirements differ from domain models

## Workflow Guidelines
- Regularly create commits for significant changes
- Each commit should represent a logical unit of work
- Include descriptive commit messages
- Run tests before committing to ensure code is working correctly