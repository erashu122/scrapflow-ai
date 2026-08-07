# 1. Software Requirements Specification

## Purpose
ScrapFlow AI lets verified business buyers maintain their compliance profile, discover managed scrap catalogues, and register material interest. Administrators verify documents, curate catalogues, and track marketplace intelligence.

## Roles
| Role | Capabilities |
| --- | --- |
| Buyer | Registration, profile, document upload, catalogue browsing, favourites, material interest, notifications |
| Admin | Buyer/document verification, catalogue/material/category management, reports, audit trail, settings |

## Functional scope
Authentication supports email/password access, short-lived access JWTs and rotating refresh tokens. Buyer onboarding captures company, owner, statutory identifiers, addresses, monthly demand and material preferences. Uploaded documents move through `PENDING`, `VERIFIED`, or `REJECTED` with reviewer context. Catalogues are curated inventory, not public bidding; buyers may only express interest.

## Non-functional requirements
- Tenant-safe role authorization and immutable audit events.
- P95 API target under 300 ms for cached read requests; paginated queries everywhere.
- WCAG 2.1 AA responsive interface with dark/light themes.
- Files are stored outside MongoDB (object storage); MongoDB stores metadata only.
- AI is isolated behind interfaces so features can be enabled without rewriting domain flows.

## Out of scope
Payments, bidding, auctions, seller self-service, and autonomous AI decisions.
