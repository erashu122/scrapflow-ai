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
cd backend && ./mvnw spring-boot:run
cd frontend && npm install && npm run dev
```

Copy `backend/.env.example` to `backend/.env` before production deployment and provide a long random JWT secret.
