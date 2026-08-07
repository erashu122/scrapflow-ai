# 9. Frontend

The Vite app includes a responsive premium buyer-dashboard foundation in `frontend/src/main.tsx`. Its composition prioritises hierarchy over dashboard density: a clear hero, operational metrics, curated inventory, compliance progress and a purposeful search call-to-action.

Add routes by feature (`auth`, `buyer`, `catalogue`, `admin`), use React Query for API caching, React Hook Form with Zod for forms, and lazy-load each route. Integrate the existing visual tokens into Tailwind when Tailwind configuration is introduced; use the same semantic token names across light and dark themes.
