# IssueWatch API

IssueWatch API is a Spring Boot backend system for reporting, tracking, assigning, commenting on, and resolving technical issues or system downtime incidents.

The project demonstrates a real-world backend workflow using authentication, role-based access control, issue management, file uploads, email notifications, scheduled jobs, Swagger documentation, tests, Docker, and MySQL.

## Features

- User registration and login
- JWT access token authentication
- Refresh tokens and logout
- Role-based access control
- Default role seeding on application startup
- Issue creation and tracking
- Issue assignment to support users
- Issue status and priority workflow
- Advanced issue filtering and keyword search
- Pagination and sorting
- Issue comments
- Internal support/admin comments
- Issue attachment uploads and downloads
- Email notification flow
- Scheduled refresh token cleanup
- Scheduled unresolved issue reminders
- Swagger/OpenAPI documentation
- Unit tests for core services
- Dockerized API and MySQL setup

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- JWT
- Maven
- Docker
- Docker Compose
- Swagger / OpenAPI
- JUnit 5
- Mockito

## Roles

The system uses three roles:

| Role | Description |
|---|---|
| USER | Reports and tracks their own issues |
| SUPPORT | Works on assigned issues and adds support comments |
| ADMIN | Manages issues, assigns support users, and updates priorities |

Default roles are automatically seeded on startup:

```text
USER
SUPPORT
ADMIN