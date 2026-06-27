# Ionic Security Cookie

Starter application for cookie-based authentication with an Ionic/Angular client and a Spring Boot server.

## Features

- Spring Security form login with an HTTP session cookie
- Cross-origin cookie support for the local Ionic dev server
- Signup, login, logout, profile update, and password change
- Forgot-password flow with email delivery through the local Inbucket container
- Persistent remember-me sessions stored in PostgreSQL
- Remember-me session listing and deletion
- Admin user list with account unlock support
- Configurable lockout after repeated failed login attempts

## Stack

- Client: Angular 22, Ionic 8, standalone components
- Server: Spring Boot 4, Spring Security, JOOQ, Flyway
- Database: PostgreSQL 18 Alpine, started with Docker Compose
- Test mail server: Inbucket, started with Docker Compose

## Prerequisites

- Node.js and npm
- Java 25
- Docker or another PostgreSQL instance matching `server/src/main/resources/application.properties`

## Run Locally

Start PostgreSQL and Inbucket:

```sh
cd server
docker compose up -d
```

Start the Spring Boot server:

```sh
cd server
./mvnw spring-boot:run
```

On Windows PowerShell, use:

```powershell
cd server
.\mvnw.cmd spring-boot:run
```

Install and start the Ionic/Angular client:

```sh
cd client
npm install
npm start
```

Open the client at `http://localhost:4200`. The API runs on `http://localhost:8080`.

Inbucket is available at `http://localhost:9000`; password reset emails are sent there through SMTP port `2500`.

## Build

Build the client:

```sh
cd client
npm run lint
npm run build
```

Build the server:

```sh
cd server
./mvnw package
```

On Windows PowerShell:

```powershell
cd server
.\mvnw.cmd package
```

## Configuration

Important local settings live in `server/src/main/resources/application.properties`:

- `app.allow-origin` lists allowed client origins for credentialed CORS requests.
- `app.url` is used when generating password reset links.
- `app.rememberme-cookie-key` signs remember-me cookies and should be changed outside local development.
- `app.login-lock-attempts` and `app.login-lock-minutes` control account lockout behavior.

The client API URL is configured in `client/src/environments/environment.ts` and `environment.prod.ts`.

## Dependency Notes

The client uses npm overrides for transitive build-tool security fixes. After `npm install`, `npm audit` should report zero vulnerabilities.

Recent npm versions may warn about packages with install scripts that have not been approved. Review them with `npm approve-scripts` if your npm policy requires script approval.
