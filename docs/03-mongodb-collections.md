# 3. MongoDB Collections

| Collection | Key fields | Indexes |
| --- | --- | --- |
| users | email, passwordHash, role, refreshTokenHashes | unique email |
| buyers | userId, legalName, gstin, verificationStatus | unique userId, sparse unique gstin, status |
| documents | buyerId, type, storageKey, status, checksum | buyerId+status, checksum |
| catalogs | title, status, materialIds, publishedAt | status+publishedAt |
| materials | name, categoryId, active | unique normalizedName, categoryId |
| categories | name, slug | unique slug |
| notifications | userId, readAt, createdAt | userId+createdAt |
| activity_logs | actorId, action, createdAt | actorId+createdAt |
| audit_logs | actorId, entityType, entityId, createdAt | entityType+entityId+createdAt |
| settings | scope, key, value | unique scope+key |
| ai_history | operation, entityId, model, confidence, createdAt | entityId+createdAt |

Encrypt sensitive document metadata at rest where required. Retention and deletion jobs must meet the organisation's legal policy.
