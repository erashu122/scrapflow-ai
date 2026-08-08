# ScrapFlow AI

Enterprise buyer intelligence platform for industrial scrap procurement.

## Workspace

- `docs/` — product, architecture, data model, API, UI, delivery specifications
- `backend/` — Java 21 / Spring Boot modular backend
- `frontend/` — React + TypeScript + Vite premium web app
- `docker-compose.yml` — local MongoDB and application services

## Run locally

```bash
docker compose up --build
```

Or start services independently:

```bash
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

Copy `backend/.env.example` to `backend/.env` before production deployment and provide a long random JWT secret.

## Authentication email

Local Docker Compose includes Mailpit. Account-verification and password-reset emails are available at `http://localhost:8025`. For any shared or production environment, set the SMTP and `APP_WEB_URL` values from `backend/.env.example`; the API returns `503 Service Unavailable` if email delivery cannot be completed, rather than issuing an unusable verification or reset flow.
