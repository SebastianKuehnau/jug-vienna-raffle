# JUG Vienna Raffle

A web application for managing and conducting raffles at Java User Group Vienna meetups. The application integrates with Meetup.com to fetch event information and attendees, allowing organizers to create raffles with prizes and conduct them using an interactive spinning wheel.

## Features

- **Meetup Integration**: Fetches events and attendee lists directly from Meetup.com
- **Prize Management**: Add and manage prizes for each raffle
- **Spin Wheel**: Interactive spin wheel to randomly select winners
- **Admin Interface**: Dedicated admin area to manage raffles
- **Winner Management**: Track prize winners for each event

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.4.4, Spring Security with OAuth2
- **Frontend**: Vaadin 24.7.2, React (for the Spin Wheel component)
- **Database**: PostgreSQL
- **Authentication**: Keycloak integration

## Running the Application

### Development Mode

1. Clone the repository
2. Start PostgreSQL and run the application:
   ```
   ./run-with-postgres.sh
   ```
   Or start PostgreSQL manually:
   ```
   docker-compose up -d postgres
   ./mvnw spring-boot:run
   ```
3. Initialize sample data (after the application starts):
   ```
   ./create-sample-data.sh
   ```
   Or using curl directly:
   ```
   curl -X POST http://localhost:8080/api/data/init
   ```
4. Open [http://localhost:8080](http://localhost:8080) in your browser

For hot reload with code changes, first ensure PostgreSQL is running, then run the application from your IDE using the "Debug using HotswapAgent" option with the Vaadin plugin.

### Production Mode

To build a production version:

```
./mvnw clean package -Pproduction
```

Run the built JAR:

```
java -jar target/jug-vienna-raffle-1.0-SNAPSHOT.jar
```

### Docker Deployment

Deploy the complete application stack using Docker Compose:

```
# Build the application first
./01_package_production.sh
./02_build_docker.sh

# Start the entire stack (PostgreSQL and application)
docker-compose up -d

# Initialize sample data (after the application starts)
./create-sample-data.sh
```

For manual deployment without Docker Compose:

```
# Start PostgreSQL
docker run -d --name postgres -e POSTGRES_DB=jugviennaraffle -e POSTGRES_USER=juguser -e POSTGRES_PASSWORD=jugpassword -p 5432:5432 postgres:16-alpine

# Run the application
docker run -p 8080:8080 --link postgres:postgres -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/jugviennaraffle -e SPRING_DATASOURCE_USERNAME=juguser -e SPRING_DATASOURCE_PASSWORD=jugpassword jug-vienna-raffle:latest
```

## Project Structure

- `src/main/java/com/vaadin/demo/application/`
  - `Application.java`: Main entry point
  - `data/`: Entity classes (Raffle, Prize, etc.)
  - `repository/`: Data access repositories
  - `services/`: Business logic services
    - `meetup/`: Meetup.com integration services
  - `views/`: UI components
    - `admin/`: Admin interface views
    - `spinwheel/`: Spin wheel implementation

- `src/main/frontend/`
  - `components/react-spin-wheel/`: React implementation of spin wheel
  - `themes/`: CSS and theming files

## Configuration

Configure the application in `application.properties`.

Key settings:
- **Database:**
  - `spring.datasource.url`: PostgreSQL connection URL
  - `spring.datasource.username`: Database username
  - `spring.datasource.password`: Database password

- **Keycloak:**
  - `keycloak.host`: Keycloak host 
  - `keycloak.realm`: Keycloak realm
  - `spring.security.oauth2.client.registration.keycloak.client-id`: Client ID for authentication
  - `spring.security.oauth2.client.registration.keycloak.client-secret`: Client secret (set via environment variable)

## Running Tests

Run all tests:
```
mvn test
```

Run a specific test:
```
mvn test -Dtest=TestClassName
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

See the [LICENSE.md](LICENSE.md) file for details.