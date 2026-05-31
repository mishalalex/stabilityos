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
- `persona`: deterministic assistant persona composed from seeded memory
- `scheduler`: scheduled daily brief and news digest jobs
- `delivery`: Telegram delivery adapter with log fallback
- `telegram`: webhook ingress for inbound Telegram messages
- `news`: manual news capture and daily digest generation
- `input`: raw input inbox for text/media metadata ingestion
- `draft`: action-draft creation and explicit confirm/reject workflow over captured inputs

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

**Phase 12: Draft and Confirmation Workflow Foundation**

This branch builds on the input inbox, persona, and Telegram ingress work by adding a reviewable draft layer for captured inputs before downstream action.

Current capabilities:

- deterministic assistant responses via `POST /api/assistant/respond`
- seeded assistant memory via Flyway-backed persistence
- scheduled daily brief generation
- optional Telegram delivery for scheduled outputs
- manual news-item capture and daily digest generation
- scheduled news-digest delivery when items exist
- input inbox storage with source, input type, Telegram metadata, status, and detected domain
- Telegram webhook ingestion for inbound text messages
- automatic input-inbox persistence for inbound Telegram text
- acknowledgment delivery after successful inbound capture
- reusable deterministic assistant persona derived from seeded memory
- protected persona inspection endpoint via `GET /api/assistant/persona`
- action-draft generation from captured input items
- draft typing based on detected input domain
- explicit draft confirmation and rejection with optional decision notes
- persistent draft audit fields including status, created time, decision time, and decision note

Current constraints:

- no provider-backed AI generation
- no advanced memory retrieval
- no automatic execution of confirmed drafts into downstream modules yet
- no input-processing pipeline beyond storage, lightweight domain detection, and manual draft generation
- no Hermes bridge yet

## API Surface

Core:

```http
GET /api/health
POST /api/assistant/respond
GET /api/assistant/persona
POST /api/telegram/webhook
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

Drafts:

```http
POST /api/action-drafts/from-input/{inputItemId}
GET /api/action-drafts
GET /api/action-drafts?status=pending
POST /api/action-drafts/{id}/confirm
POST /api/action-drafts/{id}/reject
```

Notes:

- `/api/health` is the public health check.
- `/api/telegram/webhook` is public at the API-key layer, but requires the Telegram webhook secret header.
- Other `/api/**` routes are protected by the API key filter.
- Memory, scheduler, and delivery features are configuration-driven, not exposed as public admin endpoints.

## Configuration

Key environment variables:

- `STABILITYOS_API_KEY`
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `STABILITYOS_TELEGRAM_WEBHOOK_SECRET`
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
9. Spring Security Hardening
10. Input Inbox Foundation
10.5 Telegram Inbound Text Foundation
11. Assistant Persona Layer
12. Draft and Confirmation Workflow Foundation

Upcoming roadmap items:

13. Food Logging Core
14. Local Screenshot OCR
15. Local Voice Transcription
16. AI Provider Interface
17. Paid AI Food Photo Extraction
18. Token, Cost, and Failure Fallback
19. Memory Retrieval Upgrade
20. Weekly Operating Review v2
21. Automated News Ingestion
22. News Relevance and Deduplication
23. AI-Assisted News Summaries
24. Obsidian / Markdown Export
25. Paperclip Evaluation
26. Hermes Evaluation
27. Dashboard Foundation
28. Agent Experiments
29. Appearance and Confidence Intelligence

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
