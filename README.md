# Ballot Box API - Advanced Voting System

A robust, production-ready Voting System built with Java 21 and Spring Boot 3.4. This project is designed using Domain-Driven Design (DDD) and Clean Architecture principles to ensure scalability, testability, and clear separation of concerns.

## Key Features

**Multi-Election Management:** Create and manage multiple independent election instances (e.g., "Mayor Election 2025").

**Voter Governance:** Register voters with the ability to block or unblock them to manage eligibility.

**Single-Vote Constraint:** Strict business logic ensuring a voter can only cast one vote per election instance.

**Candidate Validation:** Each election instance has its own set of candidates; votes for non-existent candidates are rejected.

**Resilient Architecture:** Built-in global exception handling and data validation.

## Tech Stack

**Runtime:** Java 21

**Framework:** Spring Boot 3.4.1

## Database:

- PostgreSQL (Production/Docker)

- H2 (In-memory for local development/testing)

- **Documentation:** SpringDoc OpenAPI (Swagger UI)

- **Containerization:** Docker & Docker Compose

- **Testing:** JUnit 5, MockMvc, AssertJ

## Architecture Overview

The project follows a layered architecture inspired by DDD:

1. **Domain Layer** (io.github.ballotbox.api.domain):
- Contains pure Java Logic (Records).
- Encapsulates business rules (e.g., Election.castVote() logic).
- Zero dependencies on external frameworks.

2. **Application Layer** (io.github.ballotbox.api):
- Orchestrates use cases via VotingService.
- Manages transactions and coordinates between domain and persistence. 

3. **Infrastructure Layer** (io.github.ballotbox.api.infrastructure):
- **Web:** REST Controllers, DTOs, and Global Exception Handler.

- **Persistence:** JPA Entities and Repositories for PostgreSQL/H2 mapping.

## Getting Started

**Option 1: Docker Compose (Production Setup)**

This will start the API and a PostgreSQL database.

```docker-compose up --build```

Note: This automatically triggers the "prod" Spring profile.

**Option 2: Maven (Local Development)**

Run the application using the default "dev" profile (uses H2 in-memory database).

```mvn spring-boot:run```


## API Documentation & Testing

**Swagger UI:** http://localhost:8080/swagger-ui.html

**H2 Console:** http://localhost:8080/h2-console (Profile: dev only)

**Health Check:** curl http://localhost:8080/actuator/health

## CURL Commands (Test Scenario)

Follow these steps to test the full lifecycle of the application:

1. **Register a Voter**

```
curl -X POST http://localhost:8080/api/v1/voters \
-H "Content-Type: application/json" \
-d '{"name": "John Doe"}'
```

**Response:** Returns a UUID (Voter ID).

2. **Create an Election Instance**

```
curl -X POST http://localhost:8080/api/v1/elections \
-H "Content-Type: application/json" \
-d '{
"title": "Mayor Election 2025",
"candidates": ["Alice Smith", "Bob Wilson"]
}'
```


**Response:** Returns a UUID (Election ID).

3. **Cast a Successful Vote**

Replace {voterId} and {electionId} with values from previous steps.
```
curl -X POST http://localhost:8080/api/v1/elections/{electionId}/votes \
-H "Content-Type: application/json" \
-d '{
"voterId": "{voterId}",
"candidateName": "Alice Smith"
}'
```


4. **Test Constraints (Expected Failures)**

Double Voting (Returns 422 Unprocessable Entity):

```
curl -i -X POST http://localhost:8080/api/v1/elections/{electionId}/votes \
-H "Content-Type: application/json" \
-d '{
"voterId": "{voterId}",
"candidateName": "Bob Wilson"
}'
```


**Voting while Blocked:**

1. Block the voter: curl -X PATCH http://localhost:8080/api/v1/voters/{voterId}/block

2. Try to vote again in a new election instance.

## Running Tests

To execute the comprehensive TDD unit tests and API integration tests:

```mvn test```
