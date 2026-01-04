# Credit Score Analyzer - Microservices Architecture

A robust, event-driven microservices application designed to calculate, analyze, and report credit scores based on financial data. This project demonstrates a modern, production-ready architecture using Spring Boot, Kafka, Redis, Docker, and the PLG (Promtail, Loki, Grafana) logging stack.

## Architecture Overview

The system is composed of **6 independent microservices** orchestrated via Docker Compose. It follows the **Event-Driven Architecture** pattern for asynchronous communication and high scalability.

### Core Services
1.  **User Service**: Manages user registration and authentication (JWT).
2.  **Data Collection Service**: Ingests financial data (transactions, loans) and publishes events to Kafka.
3.  **Credit Scoring Service**: Consumes data events, calculates credit scores using a custom algorithm, caches results in Redis, and publishes score events.
4.  **Report Service**: Consumes score events, generates detailed credit reports, and handles email notifications.

### Infrastructure Services
5.  **API Gateway**: Single entry point for all client requests (Spring Cloud Gateway).
6.  **Discovery Server**: Service registry and load balancing (Netflix Eureka).

------------

## Tech Stack

### Backend
*   **Java 17**: Core language.
*   **Spring Boot 3.x**: Framework for building microservices.
*   **Spring Cloud**: Gateway, Eureka, OpenFeign, Config.
*   **Spring Security + JWT**: Stateless authentication and authorization.
*   **Hibernate / JPA**: ORM for database interaction.

### Data & Messaging
*   **MySQL 8.0**: Primary relational database (per-service schema).
*   **Redis (Alpine)**: In-memory caching for high-speed credit score retrieval.
*   **Apache Kafka + Zookeeper**: Distributed event streaming for decoupling services.

### DevOps & Observability
*   **Docker & Docker Compose**: Containerization and orchestration.
*   **Grafana Loki**: Log aggregation system (PLG Stack).
*   **Promtail**: Log shipping agent.
*   **Grafana**: Visualization dashboard for logs.
*   **Maven**: Build automation.
