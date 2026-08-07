# 4. API Design

All endpoints are versioned under `/api/v1`, return RFC 9457 Problem Details for errors, and use cursor or page pagination. OpenAPI is served at `/swagger-ui.html`.

| Area | Endpoints |
| --- | --- |
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout` |
| Profile | `GET/PATCH /buyers/me`, `GET /buyers` (admin), `GET /buyers/{id}` (admin) |
| Documents | `POST /documents/upload-url`, `POST /documents`, `GET /documents`, `PATCH /documents/{id}/review` (admin) |
| Catalogues | `GET /catalogs`, `GET /catalogs/{id}`, `POST/PATCH /catalogs` (admin) |
| Interests | `POST /catalogs/{id}/interest`, `GET /interests` |
| Intelligence | `GET /analytics/overview` (admin), `GET /notifications`, `PATCH /notifications/{id}/read` |

Protected mutations require a bearer token, authorization by role/ownership, request validation, and an audit event.
