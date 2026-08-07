# 11. Docker

Local compose provisions MongoDB with authentication and the API. Production builds should be multi-stage, run as a non-root user, receive secrets only through the platform secret manager, and publish immutable image tags.
