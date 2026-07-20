# StabilityOS

StabilityOS is a self-hosted executive-function backend built with Spring Boot and PostgreSQL.

Its purpose is simple: reduce the mental effort required to capture, decide, remember, prioritize, follow through, and recover from daily life demands.

It should help Mishal run life with fewer open loops, fewer repeated decisions, fewer forgotten commitments, fewer context switches, and less dependence on willpower. It should not become another information collector or productivity distraction.

## Current Shape

StabilityOS is currently a backend-only system. It has no frontend and no provider-backed LLM integration yet.

Implemented areas:

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
- `burden`: cognitive burden ledger for open loops, decisions, reminders, worries, and unresolved mental load
- `openloop`: open loop capture and closure workflow for unresolved decisions, reminders, tasks, and worries
- `commitment`: commitment ledger for explicit promises, obligations, and follow-through items
- `attention`: deterministic attention checks for allowing, deferring, or blocking proposed activities

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

## Current Phase

**Phase 16: Attention Governor**

This phase adds a deterministic attention gate that decides whether a proposed activity should be allowed now, deferred, or blocked. It helps StabilityOS protect attention instead of merely capturing more information.

Current focus:

- make the core philosophy explicit: reduce the executive-function burden required to run Mishal’s life
- verify the actual backend state before planning new phases
- ensure upcoming phases reduce life-management load instead of adding more capture surfaces
- keep StabilityOS biased toward closure, decision support, reminders, recovery, and execution

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
- manual cognitive burden creation
- cognitive burden creation from captured input items
- open, parked, and closed burden states
- burden score from 1 to 5
- next-action field for reducing vague mental load
- Lombok-backed constructor injection for Spring services/controllers/config classes
- Lombok-backed getters and protected no-arg constructors for JPA entities
- DTO records retained as records
- explicit constructors retained where Spring `@Value` injection or domain-controlled construction is clearer
- manual open loop creation
- open loop creation from captured input items
- open loop creation from cognitive burdens
- open, parked, and closed loop states
- closure condition and next-action fields
- due review endpoint for open loops
- unit tests for input classification
- unit tests for open loop creation and conversion behavior
- unit tests for API-key filter behavior
- unit tests for news digest formatting
- test structure follows Maven/Spring Boot conventions under `src/test/java`
- manual commitment creation
- commitment creation from open loops
- open, completed, and dropped commitment states
- priority and due-date fields
- due commitment endpoint
- unit tests for commitment service behavior
- controller delegation tests for commitment endpoints
- attention check creation
- allowed_now, deferred, and blocked attention decisions
- decision reason and recommended action fields
- commitment-linked attention checks
- list attention checks by decision
- unit tests for attention decision behavior
- controller delegation tests for attention endpoints

## Testing Strategy

Tests live under `backend/src/test/java`.

Package structure mirrors production code:

- production: `src/main/java/com/stabilityos/backend/openloop/OpenLoopService.java`
- test: `src/test/java/com/stabilityos/backend/openloop/OpenLoopServiceTest.java`

Current testing policy:

- unit tests cover deterministic service rules and domain transitions
- security tests cover API-key filter behavior
- integration tests will be added selectively for database-backed API flows
- E2E tests will be added later when Telegram-to-action workflows stabilize
- every future feature phase should include relevant tests in the same PR

Current constraints:

- no provider-backed AI generation yet
- no advanced memory retrieval yet
- no public Telegram webhook exposure yet unless configured separately
- no media ingestion yet
- no local OCR yet
- no voice transcription yet
- no food logging core yet
- no cognitive debt manager yet
- no automatic execution of confirmed drafts into downstream modules yet
- news ingestion is manual and should remain constrained until attention governance exists
- no browser/app-level blocking yet
- no automatic attention enforcement yet

## API Surface

Core:

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

Attention:
```http
POST /api/attention/checks
GET /api/attention/checks
GET /api/attention/checks?decision=blocked
```

Notes:

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
12.6 Core Philosophy Realignment
13. Cognitive Burden Ledger
13.5 Lombok Codebase Simplification
14. Open Loop Capture and Closure
14.5 Testing Foundation
15. Commitment Ledger

Current:
16. Attention Governor

Recommended upcoming roadmap:
17. Daily Load Planner
18. Focus Session Tracker
19. Insight-to-Action Converter
20. Food Logging Core
21. Local Screenshot OCR
22. Local Voice Transcription
23. AI Provider Interface
24. Paid AI Food Photo Extraction
25. Token, Cost, and Failure Fallback
26. Memory Retrieval Upgrade
27. Weekly Operating Review v2
28. Automated News Ingestion, delayed and constrained
29. News Relevance and Deduplication
30. AI-Assisted News Summaries
31. Obsidian / Markdown Export
32. Paperclip Evaluation
33. Hermes Evaluation
34. Dashboard Foundation
35. Agent Experiments
36. Appearance and Confidence Intelligence

Automated news, Paperclip, Hermes, agents, dashboard polish, and appearance intelligence are intentionally delayed because they may satisfy curiosity without improving execution unless executive-function rails are built first.

## Design Principles

- Reduce executive-function burden.
- Spring Boot remains the system of record.
- PostgreSQL holds structured state.
- Capture must lead to triage, closure, scheduling, delegation, or action.
- Drafts and confirmation are required before uncertain inputs mutate final records.
- The assistant should protect attention, not create more information intake.
- Every phase must answer: does this reduce Mishal's life-management burden?
