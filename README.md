# StabilityOS

StabilityOS is a self-hosted personal operating system for life management, built as a serious Spring Boot portfolio project.

The long-term goal is a private assistant that can track what happened, explain what matters, and suggest what to do next across finance, health, planning, and personal memory.

## What Exists Today

StabilityOS already has working backend modules for:

- Finance tracking
- Health logging
- Planning and review summaries
- Assistant-style text responses over the current data
- Seeded assistant memory stored in PostgreSQL
- Optional scheduled daily brief generation
- API key protection, Flyway migrations, Docker Compose, and backup scripts

Current backend flow:

```text
Spring Boot API
  ├─ Finance
  ├─ Health
  ├─ Planning
  └─ Assistant
       ├─ uses planning summaries and deterministic intent matching
       ├─ reads seeded memory/personality context
       └─ can be triggered by scheduler
            ↓
        PostgreSQL
```

## Current Status

**Current phase:** Phase 8, news digest foundation

Completed:

- Phase 1: Foundation
- Phase 1.5: Config hardening
- Phase 2: Finance core
- Phase 2.5: API security
- Phase 3: Health core
- Phase 4: Planning and review engine
- Phase 5: Assistant package foundation
- Phase 6: Assistant memory and personality foundation
- Phase 7: Scheduled daily brief foundation
- Phase 7.5: Telegram delivery foundation

Right now the assistant:

- matches a small set of intents
- pulls from deterministic planning outputs
- stores seeded assistant memory in PostgreSQL
- uses a concise personality line in the daily brief
- can generate a scheduled daily brief when scheduling is enabled
- avoids exposing raw memory directly in user-facing responses
- Scheduled daily brief can be delivered to Telegram when Telegram delivery is enabled
- Manual news item capture
- Daily news digest generated from stored news items

It does not yet have:

- provider-backed AI generation
- advanced memory retrieval
- broader reasoning across the full system

Telegram delivery is configuration-driven. The backend can still run with Telegram disabled, in which case scheduled output falls back to logs.

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

No public memory or scheduler endpoints exist yet. Memory is seeded on startup and scheduling is driven by application configuration.

Example request:

```json
{
  "message": "What should I do today?"
}
```

News:

```http
POST /api/news/items
GET /api/news/items
GET /api/news/daily-digest
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
6. Memory and personality foundation
   Seeded assistant memory, startup initialization, and personality-aware briefing.

### Current Phase

Phase 8 adds the news digest foundation.

Right now the news module:

- stores manually captured news items
- groups news by date and region
- generates a simple daily digest from stored items
- prepares the system for future automated news ingestion

It does not yet have:

- automatic news fetching
- source ranking
- AI-generated summaries
- scheduled Telegram delivery of news digest

### Future Phases

7. Delivery completion
   Hermes bridge and Telegram delivery on top of the scheduler groundwork already in this branch.
8. News digest
   Personal daily news summaries.
9. Obsidian export
   Markdown output for long-term review and archives.
10. Agent experiments
   Orchestration only after the core assistant is reliable.
11. Appearance and confidence intelligence
   Longer-horizon self-improvement tracking.
12. Appearance and confidence intelligence
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
