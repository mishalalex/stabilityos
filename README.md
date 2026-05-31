# StabilityOS

StabilityOS is a self-hosted personal operations backend built with Spring Boot and PostgreSQL.

The target system is a private assistant that can ingest inputs, maintain structured personal context, generate deterministic guidance, and later deliver that guidance through external channels.

## Current Scope

Implemented backend modules:

- `finance`: expense capture and monthly summary
- `health`: sleep, water, weight, mood logging and summary
- `planning`: daily brief, evening reflection, weekly review
- `assistant`: intent-based natural-language responses over planning outputs
- `memory`: seeded assistant memory stored in PostgreSQL
- `scheduler`: scheduled daily brief and news digest jobs
- `delivery`: Telegram delivery adapter with log fallback
- `news`: manual news capture and daily digest generation
- `input`: raw input inbox for text/media metadata ingestion

Runtime shape:

```text
Spring Boot API
  ├─ domain modules
  ├─ assistant orchestration
  ├─ schedulers
  └─ delivery adapters
       ↓
    PostgreSQL
```

## Current Phase

**Phase 10: Input Inbox Foundation**

This branch adds an input-ingestion substrate on top of the earlier assistant, scheduling, Telegram delivery, and news-digest work.

Current capabilities:

- deterministic assistant responses via `POST /api/assistant/respond`
- seeded assistant memory via Flyway-backed persistence
- scheduled daily brief generation
- optional Telegram delivery for scheduled outputs
- manual news-item capture and daily digest generation
- scheduled news-digest delivery when items exist
- input inbox storage with source, input type, Telegram metadata, status, and detected domain

Current constraints:

- no provider-backed AI generation
- no advanced memory retrieval
- no input-processing pipeline beyond storage and lightweight domain detection
- no Hermes bridge yet

## API Surface

Core:

```http
GET /api/health
POST /api/assistant/respond
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

News:

```http
POST /api/news/items
GET /api/news/items
GET /api/news/daily-digest
```

Input Inbox:

```http
POST /api/input-items
GET /api/input-items
GET /api/input-items?status=received
```

Notes:

- `/api/health` is the public health check.
- Other `/api/**` routes are protected by the API key filter.
- Memory, scheduler, and delivery features are configuration-driven, not exposed as public admin endpoints.

## Configuration

Key environment variables:

- `STABILITYOS_API_KEY`
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `STABILITYOS_SCHEDULER_ENABLED`
- `STABILITYOS_DAILY_BRIEF_CRON`
- `STABILITYOS_NEWS_DIGEST_ENABLED`
- `STABILITYOS_NEWS_DIGEST_CRON`
- `STABILITYOS_TIMEZONE`
- `STABILITYOS_TELEGRAM_ENABLED`
- `STABILITYOS_TELEGRAM_BOT_TOKEN`
- `STABILITYOS_TELEGRAM_CHAT_ID`

Behavior:

- when Telegram delivery is disabled, scheduled output is logged
- when the news digest has zero items, Telegram delivery is skipped

## Local Run

```bash
docker compose up -d --build
curl http://127.0.0.1:8080/api/health
./backend/mvnw test
./ops/backup.sh
```

## Repo Layout

```text
stabilityos/
  backend/     Spring Boot application
  docs/        architecture and planning notes
  ops/         operational scripts
  hermes/      reserved for future bridge integration
  backups/     local backup artifacts
```

## Phase Roadmap

Completed:

1. Foundation: Spring Boot, PostgreSQL, Docker Compose, Flyway, health check
2. Finance Core
3. Health Core
4. Planning and Review
5. Assistant Brain Foundation
6. Memory and Personality Foundation
7. Scheduled Delivery Foundation
7.5 Telegram Delivery Foundation
8. News Digest Foundation
8.5 News Digest Delivery Foundation

In progress:

10. Input Inbox Foundation
   Raw input capture is implemented; processing and routing are still ahead.

Upcoming roadmap items:

Phase numbering is preserved from the project plan.

9. Obsidian Export
11. Agent Experiments
12. Appearance and Confidence Intelligence

## Design Principles

- Spring Boot is the system of record.
- PostgreSQL holds structured state.
- Core reasoning stays deterministic before heavier AI orchestration.
- Delivery adapters stay replaceable.
- Scheduler and transport concerns remain outside the core domain logic.
- Local development and VPS deployment must stay portable.

## Direction

The target end state is a private assistant that can:

- ingest personal inputs from multiple channels
- classify and store them safely
- synthesize daily and weekly guidance
- deliver useful output without requiring manual backfilling
