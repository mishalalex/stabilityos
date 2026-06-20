# StabilityOS

StabilityOS is a self-hosted executive-function operating system built with Spring Boot and PostgreSQL.

It is designed to help capture inputs, maintain structured personal context, protect attention, reduce cognitive debt, and convert insights into action.

The target user is a high-curiosity systems thinker who is strong at synthesis and mental-model building, but vulnerable to fragmented attention, too many open loops, and excessive input accumulation.

## Current Scope

Implemented backend modules:

- `finance`: basic financial signal capture
- `health`: basic physical state tracking
- `planning`: daily, evening, and weekly correction loops
- `assistant`: deterministic assistant responses with persona support
- `memory`: seeded assistant memory and personal context foundation
- `persona`: deterministic persona composition for assistant output
- `scheduler`: scheduled prompts and brief delivery
- `delivery`: Telegram delivery adapter with log fallback
- `news`: manual news capture and digest generation; intentionally constrained
- `input`: raw input inbox for text/media metadata ingestion
- `telegram`: inbound Telegram text receiver
- `draft`: action draft and confirmation workflow

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

**Phase 12.5: Product Realignment and Codebase Understanding**

This is not a feature-expansion phase. It is a consolidation phase to realign StabilityOS around executive function and to ensure the codebase is understood before more capabilities are added.

Current focus:

- development is paused briefly for understanding and alignment
- README and roadmap should reflect the executive-function thesis
- future phases should reduce fragmentation, not add more information streams

Current capabilities:

- expense capture and monthly summaries
- health logging and health summary
- planning summaries for daily brief, evening reflection, and weekly review
- deterministic assistant responses with persona layer
- seeded memory foundation
- scheduled daily brief
- Telegram outbound delivery
- manual news capture and scheduled news digest delivery
- input inbox storage
- Telegram inbound text receiver
- action-draft creation from inputs
- pending, confirmed, and rejected draft workflow

Current constraints:

- no provider-backed AI generation yet
- no advanced memory retrieval yet
- no public Telegram webhook exposure yet unless configured separately
- no media ingestion yet
- no local OCR yet
- no voice transcription yet
- no food logging core yet
- no cognitive debt manager yet
- no attention governor yet
- no commitment ledger yet
- no automatic execution of confirmed drafts into downstream modules yet
- news ingestion is manual and should remain constrained until attention governance exists

## API Surface

Core:

```http
GET /api/health
POST /api/assistant/respond
GET /api/assistant/persona
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

Telegram:

```http
POST /api/telegram/webhook
```

Drafts / Action Drafts:

```http
POST /api/action-drafts/from-input/{inputItemId}
GET /api/action-drafts
GET /api/action-drafts?status=pending
POST /api/action-drafts/{id}/confirm
POST /api/action-drafts/{id}/reject
```

Notes:

- `/api/health` is public health check endpoint.
- `/api/telegram/webhook` bypasses API-key filter but still requires `X-Telegram-Bot-Api-Secret-Token`.
- other `/api/**` routes are protected by API key filter
- memory, scheduler, and delivery behavior are configuration-driven rather than exposed as admin endpoints

## Tech Stack

- Java 21
- Spring Boot
- PostgreSQL
- Flyway
- Docker Compose
- Maven Wrapper
- Telegram Bot API for optional delivery and inbound text capture

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
- when news digest has zero items, Telegram delivery is skipped

## Local Run

Prerequisites:

- Java 21
- Docker and Docker Compose
- PostgreSQL, if running outside Docker Compose

Run with Docker Compose:

```bash
docker compose up -d --build
curl http://127.0.0.1:8080/api/health
```

Run backend directly:

```bash
./backend/mvnw spring-boot:run
```

Run tests:

```bash
./backend/mvnw test
```

Create local backup artifacts:

```bash
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

1. Foundation
1.5 Config hardening
2. Finance Core
2.5 API key protection
3. Health Core
4. Planning and Review Engine
5. Assistant Brain Foundation
6. Memory and Personality Foundation
7. Scheduled Daily Brief
7.5 Telegram Delivery
8. News Digest Foundation
8.5 News Digest Telegram Delivery
9. Spring Security Hardening
10. Input Inbox Foundation
10.5 Telegram Inbound Text Receiver
11. Assistant Persona Layer
12. Draft Confirmation Workflow

Current:

12.5 Product Realignment and Codebase Understanding

- update product thesis
- understand current codebase before continuing development
- ensure future phases improve executive function rather than curiosity-driven collection

Recommended upcoming roadmap:

13. Cognitive Debt Foundation
14. Attention Governor
15. Commitment Ledger
16. Focus Session Tracker
17. Insight-to-Action Converter
18. Food Logging Core
19. Local Screenshot OCR
20. Local Voice Transcription
21. AI Provider Interface
22. Paid AI Food Photo Extraction
23. Token, Cost, and Failure Fallback
24. Memory Retrieval Upgrade
25. Weekly Operating Review v2
26. Automated News Ingestion, delayed and constrained
27. News Relevance and Deduplication
28. AI-Assisted News Summaries
29. Obsidian / Markdown Export
30. Paperclip Evaluation
31. Hermes Evaluation
32. Dashboard Foundation
33. Agent Experiments
34. Appearance and Confidence Intelligence

Automated news, Paperclip, Hermes, agents, dashboard polish, and appearance intelligence are intentionally delayed because they may satisfy curiosity without improving execution unless executive-function rails are built first.

## Design Principles

- Spring Boot remains the system of record.
- PostgreSQL holds structured state.
- Capture should lead to triage, closure, or action.
- The system should reduce cognitive fragmentation, not increase it.
- The assistant should protect attention, not merely provide more information.
- Drafts and confirmation are required before uncertain inputs mutate final records.
- News and knowledge capture must be constrained.
- User understanding of the codebase is part of the product goal.
- Local development and VPS deployment must stay portable.

## Direction

The target end state is a private executive-function OS that can:

- ingest personal inputs
- classify and store them safely
- identify open loops and cognitive debt
- convert insights into decisions, commitments, habits, or parked ideas
- protect daily attention
- synthesize daily and weekly correction loops
- deliver concise, useful guidance through Telegram
- avoid becoming a high-quality distraction engine
