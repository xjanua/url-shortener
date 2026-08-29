# Frontend Architecture

## Goals

- Keep routing separate from product logic.
- Organize product behavior by business feature.
- Keep shared code small and domain-agnostic.
- Keep credentials and backend-only configuration out of the browser bundle.
- Allow features to evolve independently without creating circular imports.

## Source layout

```text
src/
|-- app/                 Next.js routes, layouts, metadata, and BFF handlers
|   |-- (marketing)/     Public product and acquisition routes
|   |-- (auth)/          Login and registration routes
|   |-- (dashboard)/     Authenticated SaaS routes
|   `-- api/             Reserved for Next-owned endpoints only, not Spring proxies
|-- components/
|   |-- ui/              Domain-free design-system primitives
|   |-- layout/          Shared page shells and navigation composition
|   |-- providers/       Client provider boundaries
|   `-- shared/          Reusable composed components without domain ownership
|-- features/
|   |-- auth/            Authentication and session-facing product logic
|   |-- links/           Short-link creation and management
|   `-- analytics/       Click metrics and reporting
|-- lib/
|   |-- api/             Shared HTTP transport and API error normalization
|   |-- auth/            Server-only cookie and session infrastructure
|   |-- env/             Environment parsing and validation
|   `-- query/           TanStack Query configuration
|-- styles/              Global design tokens added when visual design begins
`-- test/                Shared unit-test setup and helpers
```

## Dependency direction

```text
app -> features -> components/ui + lib
app -> components/layout + components/providers
components/ui -> no product feature
lib -> no route or product feature
```

Rules:

1. `app` composes routes. It should not contain reusable business logic.
2. A feature owns its API calls, schemas, hooks, types, and feature-specific components.
3. Features do not import from `app`.
4. Features should not import another feature directly. Compose them in `app`, or promote genuinely shared code to `components` or `lib`.
5. `components/ui` contains domain-free primitives only.
6. `lib` contains infrastructure only and must not know about product features.
7. Export a feature's public API from its root `index.ts`. Do not deep-import another feature's internal files.

## Server and client boundaries

- Components are Server Components by default.
- Add `"use client"` only to interactive leaf components or provider boundaries.
- The browser calls Spring Boot directly through the shared client in `lib/api/client.ts`.
- Every product feature uses the same `NEXT_PUBLIC_API_URL` base URL and sends credentials with requests.
- Spring Boot owns the HttpOnly authentication cookie and resolves JWTs from that cookie.
- Access tokens and refresh tokens must not be stored in browser storage or exposed through `NEXT_PUBLIC_*` variables.
- Only variables intentionally safe for browsers may use the `NEXT_PUBLIC_` prefix.

## Feature shape

Each feature starts with this internal structure and may omit folders it does not need:

```text
feature/
|-- api/                 Request functions and query keys
|-- components/          Feature-owned UI
|-- hooks/               Feature-owned client orchestration
|-- schemas/             Zod input and response schemas
|-- types/               Domain types not inferred from schemas
`-- index.ts             Explicit public exports
```

## Testing strategy

- Vitest and Testing Library for pure logic, hooks, and synchronous components.
- Playwright for authentication, async Server Components, link management, redirect handoff, and analytics flows.
- Tests should be colocated with feature code unless they cover multiple routes, in which case they belong under `tests/e2e`.

## Deferred decisions

- Analytics provider.
- Error monitoring provider.
- Deployment platform.

These stay deferred until implementation requirements are provided.

## Design system baseline

- Tailwind CSS v4 with semantic color tokens in `src/app/globals.css`.
- shadcn/ui using the Radix Nova style as a customizable primitive layer.
- Phosphor Icons is the only product icon family.
- Light and dark themes follow the operating-system color preference.
- Feature components compose primitives; they do not redefine global tokens.
