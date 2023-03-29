
# servap

A RSS reader using Java Servlets as backend and Preact + TailwindCSS for frontend

# Running Instructions

1. Copy `.env.example` to `.env`
2. Update `CONNECTION_STRING`, `DB_USER`, and `DB_PASSWORD`.
3. Create a database `servap` and create tables as described in `/init.sql`
4. Run `mvn jetty:run-war`
