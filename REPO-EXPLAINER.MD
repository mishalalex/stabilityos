Big Picture

This repo is a Spring Boot backend for a self-hosted executive-function system.

Right now it is a structured capture, review, and follow-through engine:

- receive API or Telegram input
- store structured records in PostgreSQL
- compute planning summaries from finance and health data
- turn captured mental load into drafts, burdens, open loops, and commitments
- produce deterministic assistant-style responses
- optionally send scheduled output through Telegram

It is not an AI-heavy system yet. There is no provider-backed LLM integration, no automatic tool execution, and no frontend.

ELI5 Model

- controllers receive HTTP requests
- services apply rules and build responses
- entities map Java objects to database rows
- repositories read and write those rows
- Flyway migrations define database schema history
- schedulers run timed jobs
- delivery adapters send outbound notifications
- security filter guards protected endpoints

Top-Level Repo Shape

- `backend/`: actual Spring Boot application
- `docs/`: architecture and roadmap notes
- `ops/`: helper scripts like backups
- `backups/`: generated backup artifacts
- `hermes/`: reserved for future bridge layer; not active in runtime today
- `pom.xml`: root aggregator for `backend`

What Starts App

Main entrypoint is `backend/src/main/java/com/stabilityos/backend/BackendApplication.java`.

Startup flow is roughly:

1. Spring Boot loads configuration from `application.yml` and env vars.
2. DataSource is created for PostgreSQL.
3. Flyway runs migrations from `classpath:db/migration`.
4. JPA validates entities against schema because `ddl-auto: validate`.
5. Beans are created.
6. `StartupDataInitializer` seeds assistant memory if missing.
7. Scheduled beans become active if their properties enable them.
8. HTTP server starts on port `8080`.

Main Runtime Packages

Under `backend/src/main/java/com/stabilityos/backend`:

- `assistant`: deterministic assistant responses for planning prompts
- `burden`: cognitive burden ledger for unresolved mental load
- `common`: shared lightweight endpoints like health check
- `commitment`: commitment ledger for explicit promises and obligations
- `config`: startup data seeding
- `delivery`: outbound notification abstraction and implementations
- `draft`: review/confirm/reject workflow for captured input
- `finance`: expense capture and monthly summary
- `health`: health logging and seven-day summary
- `input`: raw inbox for captured text and metadata
- `memory`: persisted assistant memory entries
- `news`: manual news capture and digest generation
- `openloop`: open loop capture and closure workflow
- `persona`: persona composition for assistant output
- `planning`: daily brief, evening reflection, weekly review
- `scheduler`: timed daily brief and news digest jobs
- `security`: API key authentication and filter chain
- `telegram`: inbound webhook handling and Telegram DTOs

What Each Module Actually Does

`finance`

- Stores expenses with amount, category, note, entry date, created time.
- Lists expenses.
- Computes monthly total and category breakdown.

`health`

- Stores health logs with sleep, water, weight, mood, notes, entry date.
- Lists health logs.
- Computes summary over recent records.

`planning`

- Pulls finance and health data directly from repositories.
- Builds daily brief, evening reflection, and weekly review.
- Logic is deterministic and threshold-based, not model-driven.

`assistant`

- Classifies user message by simple keyword matching.
- Routes to daily brief, evening reflection, weekly review, or fallback guidance.
- Formats those responses as assistant text.
- Uses persona service for tone line and fallback guidance.

`memory`

- Persists assistant memory rows in `assistant_memory`.
- Seeds defaults on startup if missing.
- Exposes top memories to persona layer.
- This is not retrieval-heavy memory; it is a small seeded context store.

`persona`

- Builds current persona response from memory.
- Returns static persona metadata plus principles loaded from memory.
- Supplies one-line persona guidance used by assistant replies.

`news`

- Stores manually entered news items.
- Lists stored items sorted by date and importance.
- Builds daily digest for a specific date.
- Scheduled digest sends only when at least one item exists for that date.

`input`

- Stores raw captured items in `input_items`.
- Supports API-created input and Telegram-created text input.
- Saves source, input type, raw text, file path metadata, Telegram message id, status, detected domain, error message, timestamps.
- Domain detection is simple keyword heuristics: `finance`, `health`, `food`, `news`, `planning`, `unknown`.

`draft`

- Creates review drafts from captured input items.
- Draft title and type are derived from detected domain.
- Drafts can be listed, confirmed, or rejected.
- Confirming or rejecting only updates draft state and decision metadata.
- No downstream write into finance, health, news, planning, burden, open loop, or commitment modules happens yet.

`burden`

- Stores cognitive burdens for decisions, reminders, worries, and unresolved mental load.
- Can create burdens manually or from input items.
- Supports listing all burdens or filtering by status.
- Supports closing and parking burdens with an optional note.

`openloop`

- Stores unresolved loops with title, description, type, closure condition, next action, and next review date.
- Can create open loops manually, from input items, or from cognitive burdens.
- Supports open, closed, and parked states.
- Provides a due-review endpoint based on `next_review_date`.

`commitment`

- Stores explicit promises, obligations, and follow-through items.
- Can create commitments manually or from open loops.
- Tracks status, priority, due date, completion/drop timestamps, and outcome note.
- Provides a due-commitments endpoint based on `due_date`.
- Completing or dropping a commitment updates state but does not currently mutate the source open loop.

`telegram`

- Accepts `POST /api/telegram/webhook`.
- Validates `X-Telegram-Bot-Api-Secret-Token` against configured secret.
- Ignores updates without message text.
- Stores text messages as input inbox items.
- Sends acknowledgement through notification delivery service.

`delivery`

- Defines notification abstraction via `NotificationDeliveryService`.
- `TelegramNotificationDeliveryService` sends via Telegram Bot API when enabled.
- `LoggingNotificationDeliveryService` is fallback when Telegram delivery is disabled or missing.

`scheduler`

- `ScheduledBriefService`: active only when `stabilityos.scheduler.enabled=true`.
- `ScheduledNewsDigestService`: active only when `stabilityos.scheduler.news-digest-enabled=true`.
- Both use cron plus configured timezone.
- Daily brief scheduler asks assistant: `"What should I do today?"`.
- News digest scheduler formats digest for `LocalDate.now()` and skips delivery when item count is zero.

`security`

- Uses stateless Spring Security filter chain.
- API key auth is implemented by custom `ApiKeyAuthenticationFilter`.
- Protected clients must send header `X-StabilityOS-Key`.

Important Endpoints

Public:

- `GET /api/health`
- `GET /actuator/health/**`
- `POST /api/telegram/webhook` bypasses API-key filter, but controller still enforces Telegram secret header

Protected by API key:

- `POST /api/assistant/respond`
- `GET /api/assistant/persona`
- `POST /api/expenses`
- `GET /api/expenses`
- `GET /api/finance/monthly-summary`
- `POST /api/health/logs`
- `GET /api/health/logs`
- `GET /api/health/summary`
- `GET /api/planning/daily-brief`
- `GET /api/planning/evening-reflection`
- `GET /api/planning/weekly-review`
- `POST /api/news/items`
- `GET /api/news/items`
- `GET /api/news/daily-digest`
- `POST /api/input-items`
- `GET /api/input-items`
- `POST /api/action-drafts/from-input/{inputItemId}`
- `GET /api/action-drafts`
- `POST /api/action-drafts/{id}/confirm`
- `POST /api/action-drafts/{id}/reject`
- `POST /api/cognitive-burdens`
- `POST /api/cognitive-burdens/from-input/{inputItemId}`
- `GET /api/cognitive-burdens`
- `POST /api/cognitive-burdens/{id}/close`
- `POST /api/cognitive-burdens/{id}/park`
- `POST /api/open-loops`
- `POST /api/open-loops/from-input/{inputItemId}`
- `POST /api/open-loops/from-burden/{cognitiveBurdenId}`
- `GET /api/open-loops`
- `GET /api/open-loops/due`
- `POST /api/open-loops/{id}/close`
- `POST /api/open-loops/{id}/park`
- `POST /api/commitments`
- `POST /api/commitments/from-open-loop/{openLoopId}`
- `GET /api/commitments`
- `GET /api/commitments/due`
- `POST /api/commitments/{id}/complete`
- `POST /api/commitments/{id}/drop`

Security Flow

How API key auth works:

1. Filter skips public endpoints.
2. Filter reads `X-StabilityOS-Key`.
3. If configured API key is missing, response is `500`.
4. If provided key is missing or wrong, response is `401`.
5. If valid, request gets role `ROLE_API_CLIENT`.
6. Spring Security allows handler execution.

This is machine-to-machine protection, not end-user login.

Configuration That Matters

Main config lives in `backend/src/main/resources/application.yml`.

Key variables:

- `STABILITYOS_API_KEY`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `STABILITYOS_TELEGRAM_WEBHOOK_SECRET`
- `STABILITYOS_SCHEDULER_ENABLED`
- `STABILITYOS_DAILY_BRIEF_CRON`
- `STABILITYOS_NEWS_DIGEST_ENABLED`
- `STABILITYOS_NEWS_DIGEST_CRON`
- `STABILITYOS_TIMEZONE`
- `STABILITYOS_TELEGRAM_ENABLED`
- `STABILITYOS_TELEGRAM_BOT_TOKEN`
- `STABILITYOS_TELEGRAM_CHAT_ID`

Defaults worth knowing:

- server port: `8080`
- timezone default: `Asia/Kolkata`
- daily brief cron default: `0 30 8 * * *`
- news digest cron default: `0 0 9 * * *`
- both schedulers disabled by default
- Telegram delivery disabled by default

What Database Exists Today

Flyway migrations create these tables:

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

Important details:

- `app_metadata` exists from `V1__init.sql`, even though current business logic barely uses it.
- `attention_checks` currently exists as a migration, but no Java runtime package or endpoints use it yet.

Why Flyway Matters Here

Migrations are versioned schema changes.

Without Flyway:

- different machines can drift to different schemas
- startup can fail because JPA validation expects missing tables or columns
- you lose reliable schema history

Current pattern is:

- Flyway changes schema
- JPA validates schema
- application code assumes schema exists

`ddl-auto: validate` means Hibernate checks mapping compatibility but does not create or alter tables.

What Telegram Flow Really Does

Inbound:

1. Telegram sends webhook payload.
2. Controller validates secret header.
3. Service checks message presence and text presence.
4. Text becomes `CreateInputItemRequest("telegram", "text", ...)`.
5. Input item is stored with detected domain.
6. Notification service sends acknowledgement.

Outbound:

- scheduled daily brief can go to Telegram
- scheduled news digest can go to Telegram
- inbound acknowledgement can go to Telegram
- if Telegram delivery disabled, outbound messages are logged instead

What Repo Is Not Yet

- no provider-backed AI generation
- no LLM memory retrieval pipeline
- no automatic conversion from confirmed draft into final structured records
- no frontend
- no OCR pipeline
- no voice transcription pipeline
- no media ingestion processing
- no workflow engine
- no user accounts or multi-user auth
- no runtime attention governor service yet

Build And Runtime Shape

Tech stack in actual code:

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Actuator
- Flyway
- PostgreSQL
- Testcontainers for integration tests
- Maven
- Docker Compose

Root `pom.xml` is only aggregator. Real application dependencies are in `backend/pom.xml`.

Testing State

Unit tests run during the normal Maven test phase through Surefire:

- `ApiKeyAuthenticationFilterTest`
- `InputItemServiceTest`
- `OpenLoopServiceTest`
- `NewsDigestFormatterTest`
- `CommitmentServiceTest`
- `CommitmentControllerTest`

Integration tests are named `*IT.java` and run through Failsafe only when the `integration` profile is enabled:

- `BackendApplicationIT`

`BackendApplicationIT` starts a temporary PostgreSQL 16 container with Testcontainers, wires Spring to that database, runs Flyway, and verifies the application context starts.

Local Run Commands

Useful commands:

- `docker compose up -d --build`
- `docker compose ps`
- `curl http://127.0.0.1:8080/api/health`
- `mvn test`
- `mvn clean install`
- `mvn verify -Pintegration`
- `cat backend/target/surefire-reports/*.txt`
- `cat backend/target/failsafe-reports/*.txt`
- `./ops/backup.sh`

Maven wrapper note:

- Installed `mvn` works from the repo root and uses the root aggregator `pom.xml`.
- `backend/mvnw` exists for backend-local Maven runs.
- There is no root-level `./mvnw` yet; best long-term shape is a root-level Maven wrapper for the multi-module repo.

One Mental Model

If you need one sentence:

This repo is a Spring Boot backend that captures structured personal signals, turns mental load into reviewable records and commitments, stores them in PostgreSQL, and optionally delivers scheduled summaries through Telegram.
