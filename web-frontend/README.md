# URL Shortener Web

Next.js frontend for the URL Shortener SaaS product.

## Requirements

- Node.js 22 or newer
- pnpm 11 or newer

## Commands

```bash
pnpm install
pnpm dev
pnpm check
pnpm build
```

Copy `.env.example` to `.env.local` before connecting the frontend to Spring Boot.

The browser calls Spring Boot directly. Configure the public API origin in
`.env.local`:

```dotenv
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Spring Boot must allow the frontend origin and own the authentication cookie:

```dotenv
FRONTEND_ALLOWED_ORIGINS=http://localhost:3000
AUTH_COOKIE_SECURE=false
```

All feature API modules must use `src/lib/api/client.ts`. Do not create Next.js
proxy Route Handlers for Spring endpoints.

## Architecture

See [ARCHITECTURE.md](./ARCHITECTURE.md) for module boundaries, folder ownership, and import rules.
