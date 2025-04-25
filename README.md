# JUG Vienna Raffle

A web application for managing and conducting raffles at Java User Group Vienna meetups. 
The application integrates with Meetup.com to fetch event information and attendees, 
allowing organizers to create raffles with prizes and conduct them using an interactive spinning wheel.

## Features

- **Meetup Integration**: Fetches events and attendee lists directly from Meetup.com
- **Prize Management**: Add and manage prizes for each raffle
- **Spin Wheel**: Interactive spin wheel to randomly select winners
- **Admin Interface**: Dedicated admin area to manage raffles
- **Winner Management**: Track prize winners for each event

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.4.4, Spring Security with OAuth2
- **Frontend**: Vaadin 24.7.2, React (for the Spin Wheel component)
- **Database**: H2 Database (in-memory)
- **Authentication**: Keycloak integration

## Running the Application

### Development Mode

1. Clone the repository
2. Run the application using Maven:
   ```
   ./mvnw spring-boot:run
   ```
   Or use the provided script:
   ```
   ./05_run_mvn.sh
   ```
3. Open [http://localhost:8080](http://localhost:8080) in your browser

For hot reload with code changes, run the application from your IDE using the "Debug using HotswapAgent" option with the Vaadin plugin.

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

Build Docker image:

```
./01_package_production.sh
./02_build_docker.sh
```

Run Docker container:

```
./03_run_docker.sh
```

Or manually:

```
docker run -p 8080:8080 jug-vienna-raffle:latest
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

Configure Meetup.com integration and Keycloak authentication in `application.properties`.

Key settings:
- `keycloak.host`: Keycloak host 
- `keycloak.realm`: Keycloak realm
- `spring.security.oauth2.client.registration.keycloak.client-id`: Client ID for authentication

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