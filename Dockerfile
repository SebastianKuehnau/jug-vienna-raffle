# Allows you to run this app easily as a docker container.
# See README.md for more details.
#
# 1. Build the image with: docker build --no-cache -t test/app:latest .
# 2. Run the image with: docker run --rm -ti -p8080:8080 test/app
#
# Uses Docker Multi-stage builds: https://docs.docker.com/build/building/multi-stage/

# The "Build" stage. Copies the entire project into the container, into the /app/ folder, and builds it.
FROM eclipse-temurin:17 AS build
COPY . /app/
WORKDIR /app/
RUN --mount=type=cache,target=/root/.m2 --mount=type=cache,target=/root/.vaadin ./mvnw -C -e clean package -Pproduction
# At this point, we have the app (executable jar file):  /app/target/app.jar


# The "Run" stage. Start with a clean image, and copy over just the app itself, omitting gradle, npm and any intermediate build files.
FROM eclipse-temurin:17
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
