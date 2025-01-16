# Pokedex

This service has been written using **Java 21** and leverages the **Jetty** server for handling HTTP requests.

---

## Prerequisites
- JDK 21
- Java IDE
- [Maven](https://maven.apache.org/) (for building the project)
- [Docker](https://docs.docker.com/get-docker/)

---

## Build and Test

### **Maven**

Build the project and package it into a fat JAR:
```sh
mvn clean package
```

Run the service locally:

```sh
java -jar target/pokedex-challenge.jar
```


Run tests:

```sh
mvn test
```


### Docker
Build the Docker Image
```sh
docker build -t pokedex .
```
Run the Docker Container
```sh
docker run --rm -ti -p 8001:8001 pokedex
```
The service will be accessible on port 8001.

## Endpoints

```sh
GET api/v1/pokemon/{name}: Fetches a Pokemon for a given name.
GET api/v1/pokemon/translated/{name}: Fetches a Pokemon with a translated description for a given name.
GET /livez: Returns a 200 response if the service is alive.
```

## Future Improvements
Due to time constraints, the following improvements could be considered for future iterations:

- CI/CD Pipeline: Automate the build, test, and deployment process using a CI/CD pipeline.
- Caching: Important for handling rate limits imposed by third-party services.
- Extended Error Handling: Add support for more detailed error responses and codes.
- Tracing & Metrics: Enhanced observability through distributed tracing and metrics to monitor service health and performance effectively.
