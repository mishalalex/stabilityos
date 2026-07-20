# StabilityOS

StabilityOS is a self-hosted executive-function backend built with Spring Boot and PostgreSQL.

Its purpose is simple: reduce the mental effort required to capture, decide, remember, prioritize, follow through, and recover from daily life demands.

It should help Mishal run life with fewer open loops, fewer repeated decisions, fewer forgotten commitments, fewer context switches, and less dependence on willpower. It should not become another information collector or productivity distraction.

## Current Shape

StabilityOS is currently a backend-only system. It has no frontend and no provider-backed LLM integration yet.

Implemented areas:

- Finance and health logging
- Daily, evening, and weekly planning summaries
- Deterministic assistant and persona responses
- Seeded assistant memory
- Manual news capture and digest delivery
- Input inbox and Telegram text ingestion
- Draft review workflow
- Cognitive burden ledger
- Open loop capture and closure
- Commitment ledger
- Attention checks for allowing, deferring, or blocking proposed activities
- Scheduled Telegram delivery with logging fallback

Runtime shape:

```text
HTTP / Telegram input
        |
Spring Boot API
        |
Controllers -> Services -> Repositories
        |
PostgreSQL + Flyway migrations
```

## Modules

Production code lives under `backend/src/main/java/com/stabilityos/backend`.

- `finance`: expenses and monthly summaries
- `health`: sleep, water, weight, mood, and health summaries
- `planning`: daily brief, evening reflection, weekly review
- `assistant`, `persona`, `memory`: deterministic assistant behavior and stored context
- `input`, `telegram`: raw input capture and Telegram inbound text
- `draft`: review/confirm/reject workflow for captured input
- `burden`: unresolved mental load, worries, reminders, and decisions
- `openloop`: unresolved loops with closure conditions and review dates
- `commitment`: promises, obligations, due dates, completion, and drops
- `attention`: activity checks with `allowed_now`, `deferred`, or `blocked` decisions
- `news`: manual news items and daily digest
- `scheduler`, `delivery`: scheduled output and Telegram/log delivery
- `security`: stateless API-key protection

## Quick Start

Prerequisites:

- Java 21
- Maven
- Docker Desktop, for Docker Compose and integration tests

Run the app with Docker Compose:

```bash
docker compose up -d --build
curl http://127.0.0.1:8080/api/health
```

Run the backend directly from the repo root:

```bash
mvn -pl backend spring-boot:run
```

Run unit tests:

```bash
mvn test
```

Run integration tests with Testcontainers PostgreSQL:

```bash
mvn verify -Pintegration
```

Show test summaries after a run:

```bash
cat backend/target/surefire-reports/*.txt
cat backend/target/failsafe-reports/*.txt
```

Create local backup artifacts:

```bash
./ops/backup.sh
```

## Maven Wrapper

The repo currently has a backend-local Maven wrapper:

```bash
cd backend
./mvnw test
```

There is no root-level `./mvnw` yet. From the repo root, use installed Maven:

```bash
mvn test
```

## Configuration

Main config lives in `backend/src/main/resources/application.yml`.

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

Defaults:

- Server port: `8080`
- Timezone: `Asia/Kolkata`
- Schedulers disabled by default
- Telegram delivery disabled by default

## API Overview

Public:

```http
GET /api/health
GET /actuator/health
POST /api/telegram/webhook
```

`/api/telegram/webhook` bypasses the API-key filter but still requires `X-Telegram-Bot-Api-Secret-Token`.

Protected routes require:

```http
X-StabilityOS-Key: <api-key>
```

Core protected endpoints:

```http
POST /api/assistant/respond
GET  /api/assistant/persona

POST /api/expenses
GET  /api/expenses
GET  /api/finance/monthly-summary

POST /api/health/logs
GET  /api/health/logs
GET  /api/health/summary

GET  /api/planning/daily-brief
GET  /api/planning/evening-reflection
GET  /api/planning/weekly-review

POST /api/news/items
GET  /api/news/items
GET  /api/news/daily-digest

POST /api/input-items
GET  /api/input-items

POST /api/action-drafts/from-input/{inputItemId}
GET  /api/action-drafts
POST /api/action-drafts/{id}/confirm
POST /api/action-drafts/{id}/reject

POST /api/cognitive-burdens
POST /api/cognitive-burdens/from-input/{inputItemId}
GET  /api/cognitive-burdens
POST /api/cognitive-burdens/{id}/close
POST /api/cognitive-burdens/{id}/park

POST /api/open-loops
POST /api/open-loops/from-input/{inputItemId}
POST /api/open-loops/from-burden/{cognitiveBurdenId}
GET  /api/open-loops
GET  /api/open-loops/due
POST /api/open-loops/{id}/close
POST /api/open-loops/{id}/park

POST /api/commitments
POST /api/commitments/from-open-loop/{openLoopId}
GET  /api/commitments
GET  /api/commitments/due
POST /api/commitments/{id}/complete
POST /api/commitments/{id}/drop

POST /api/attention/checks
GET  /api/attention/checks
```

## Database

Flyway migrations live in `backend/src/main/resources/db/migration`.

Current tables:

- `app_metadata`
- `expenses`
- `health_logs`
- `assistant_memory`
- `news_items`
- `input_items`
- `action_drafts`
- `cognitive_burdens`
- `open_loops`
- `commitments`
- `attention_checks`

Hibernate uses `ddl-auto: validate`, so Flyway owns schema changes.

## Testing

Unit tests run with Maven Surefire during:

```bash
mvn test
```

Current unit coverage includes input classification, API-key filtering, news digest formatting, open loop behavior, commitment behavior, and attention governor behavior.

Integration tests are named `*IT.java` and run with Maven Failsafe only when requested:

```bash
mvn verify -Pintegration
```

`BackendApplicationIT` starts a temporary PostgreSQL 16 container with Testcontainers, runs Flyway, and verifies that the Spring context starts against a real database.

## Repo Layout

```text
stabilityos/
  backend/     Spring Boot application
  docs/        architecture and planning notes
  ops/         operational scripts
  hermes/      reserved for future bridge integration
  backups/     local backup artifacts
```

## Roadmap

Completed:

- Foundation, config hardening, API security
- Finance, health, planning, assistant, memory, persona
- Scheduled daily brief and Telegram delivery
- Manual news digest and Telegram news delivery
- Input inbox and Telegram inbound text
- Draft confirmation workflow
- Cognitive burden ledger
- Open loop capture and closure
- Testing foundation
- Commitment ledger

Current:

- Attention Governor

Likely next:

- Daily load planner
- Focus session tracker
- Insight-to-action converter
- Food logging core
- Local OCR and voice transcription
- AI provider interface
- Memory retrieval upgrade
- Weekly operating review v2
- Dashboard foundation

Automated news, broad agent workflows, and appearance/confidence features stay delayed unless they clearly reduce life-management burden.

## Design Principles

- Reduce executive-function burden.
- Spring Boot remains the system of record.
- PostgreSQL holds structured state.
- Capture must lead to triage, closure, scheduling, delegation, or action.
- Drafts and confirmation are required before uncertain inputs mutate final records.
- The assistant should protect attention, not create more information intake.
- Every phase must answer: does this reduce Mishal's life-management burden?
