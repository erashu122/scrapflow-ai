# 8. Backend

The initial Spring Boot service has a health endpoint and AI document-intelligence boundary under `backend/src/main/java`. Extend it module-by-module:

1. Identity: User aggregate, password hashing, JWT issue/refresh/revoke, brute-force protection.
2. Buyer: profile command/query services, uniqueness validation for GSTIN and ownership checks.
3. Documents: presigned object-storage uploads, MIME/magic-byte and size validation, malware-scan event, immutable review history.
4. Catalogues: admin-curated publication state, searchable material filtering and interest capture.
5. Shared: RFC 9457 error handler, `@CurrentUser`, audit-event publisher, cursor pagination and request IDs.

Each module uses controller → application service → repository/port. DTO validation is required at every inbound boundary. Keep document content out of the API service filesystem.
