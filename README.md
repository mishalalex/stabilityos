# StabilityOS

StabilityOS is a self-hosted personal operating system for life management, built as a serious Spring Boot portfolio project.

The long-term goal is a private assistant that can track what happened, explain what matters, and suggest what to do next across finance, health, planning, and personal memory.

## What Exists Today

StabilityOS already has working backend modules for:

- Finance tracking
- Health logging
- Planning and review summaries
- Assistant-style text responses over the current data
- API key protection, Flyway migrations, Docker Compose, and backup scripts

Current backend flow:

```text
Spring Boot API
  ├─ Finance
  ├─ Health
  ├─ Planning
  └─ Assistant
       └─ uses planning summaries and deterministic intent matching
            ↓
        PostgreSQL
```

## Current Status

**Current phase:** Phase 5, early Assistant Brain

Completed:

- Phase 1: Foundation
- Phase 1.5: Config hardening
- Phase 2: Finance core
- Phase 2.5: API security
- Phase 3: Health core
- Phase 4: Planning and review engine
- Phase 5: Assistant package foundation

What the assistant package adds now:

- `POST /api/assistant/respond`
- Intent classification for daily brief, evening reflection, and weekly review prompts
- Natural-language responses generated from existing planning data
- A clean place to later add memory, rules, and provider-based AI

## Current API Surface

Health check:

```http
GET /api/health
```

Finance:

```http
POST /api/expenses
GET /api/expenses
GET /api/finance/monthly-summary
```

Health:

```http
POST /api/health/logs
GET /api/health/logs
GET /api/health/summary
```

Planning:

```http
GET /api/planning/daily-brief
GET /api/planning/evening-reflection
GET /api/planning/weekly-review
```

Assistant:

```http
POST /api/assistant/respond
```

Example request:

```json
{
  "message": "What should I do today?"
}
```

## Run Locally

Start the stack:

```bash
docker compose up -d --build
```

Smoke test:

```bash
curl http://127.0.0.1:8080/api/health
```

Run tests:

```bash
./backend/mvnw test
```

Create backups:

```bash
./ops/backup.sh
```

## Repo Layout

```text
stabilityos/
  backend/     Spring Boot application
  docs/        planning and architecture notes
  ops/         operational scripts
  hermes/      future Telegram bridge
  backups/     local backup artifacts
```

## Build Strategy

The repo is being built phase by phase so each step stays usable on its own.

### Completed Phases

1. Foundation
   Spring Boot, PostgreSQL, Docker Compose, Flyway, health check, VPS portability.
2. Finance Core
   Expense logging and monthly summaries.
3. Health Core
   Sleep, water, weight, and health summaries.
4. Planning and Review
   Daily brief, evening reflection, and weekly review endpoints.
5. Assistant Brain, first cut
   A backend assistant endpoint that turns user prompts into structured planning responses.

### Current Phase

Phase 5 is active, but still intentionally simple.

Right now the assistant:

- matches a small set of intents
- pulls from deterministic planning outputs
- returns plain natural-language summaries

It does not yet have:

- persistent memory
- provider-backed AI generation
- Telegram delivery
- broader reasoning across the full system

### Future Phases

6. Memory and personality
   Preferences, continuity, and longer-term behavioral context.
7. Hermes integration
   Telegram bridge and scheduled delivery.
8. News digest
   Personal daily news summaries.
9. Obsidian export
   Markdown output for long-term review and archives.
10. Agent experiments
   Orchestration only after the core assistant is reliable.
11. Appearance and confidence intelligence
   Longer-horizon self-improvement tracking.

## Design Principles

- Spring Boot is the source of truth.
- PostgreSQL stores structured personal data.
- Keep the core deterministic before adding heavier AI orchestration.
- Avoid provider lock-in.
- Keep local development and VPS deployment portable.
- Treat GitHub and backups as the durable source of truth.

## Long-Term Direction

The target experience is a private assistant that can answer questions like:

- What should I focus on today?
- What slipped this week?
- What patterns are improving or getting worse?
- What should I correct next?

The goal is not just tracking data. The goal is better decisions, better routines, and long-term personal stability.
