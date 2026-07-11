Big Picture

This repo is a Spring Boot backend for a self-hosted executive-function system.

Right now it is mostly a structured capture and review engine:

- receive API or Telegram input
- store structured records in PostgreSQL
- compute planning summaries from finance and health data
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

Important annotations:

- `@SpringBootApplication`: enables component scanning and Spring Boot auto-configuration
- `@EnableScheduling`: enables scheduled jobs

Startup flow is roughly:

1. Spring Boot loads configuration from `application.yml` and env vars.
2. DataSource is created for PostgreSQL.
3. Flyway runs migrations from `classpath:db/migration`.
4. JPA validates entities against schema because `ddl-auto: validate`.
5. Beans are created.
6. `StartupDataInitializer` seeds assistant memory if missing.
7. Scheduled beans become active if their properties enable them.
8. HTTP server starts on port `8080`.

How Normal Request Flows

Example: create expense.

1. `ExpenseController` receives `POST /api/expenses`.
2. Controller passes validated request DTO to `ExpenseService`.
3. Service creates `Expense` entity.
4. `ExpenseRepository` saves entity.
5. Spring Data JPA writes row into `expenses`.
6. Service maps saved entity to response DTO.
7. Controller returns JSON.

That same controller -> service -> repository pattern is used almost everywhere.

Main Runtime Packages

Under `backend/src/main/java/com/stabilityos/backend`:

- `assistant`: deterministic assistant responses for planning prompts
- `common`: shared lightweight endpoints like health check
- `config`: startup data seeding
- `delivery`: outbound notification abstraction and implementations
- `draft`: review/confirm/reject workflow for captured input
- `finance`: expense capture and monthly summary
- `health`: health logging and seven-day summary
- `input`: raw inbox for captured text and metadata
- `memory`: persisted assistant memory entries
- `news`: manual news capture and digest generation
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
- Builds:
  - daily brief
  - evening reflection
  - weekly review
- Logic is deterministic and threshold-based, not model-driven.
- Product thesis mostly lives here today.

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
- Domain detection is simple keyword heuristics:
  - `finance`
  - `health`
  - `food`
  - `news`
  - `planning`
  - `unknown`

`draft`

- Creates review drafts from captured input items.
- Draft title and type are derived from detected domain.
- Drafts can be listed, confirmed, or rejected.
- Confirming or rejecting only updates draft state and decision metadata.
- No downstream write into finance, health, news, or planning modules happens yet.
- Draft creation also does not currently mark the source input item as processed.

`telegram`

- Accepts `POST /api/telegram/webhook`.
- Validates `X-Telegram-Bot-Api-Secret-Token` against configured secret.
- Ignores updates without message text.
- Stores text messages as input inbox items.
- Sends acknowledgement through notification delivery service.

`delivery`

- Defines notification abstraction via `NotificationDeliveryService`.
- `TelegramNotificationDeliveryService` sends via Telegram Bot API when enabled.
- `LoggingNotificationDeliveryService` is fallback when Telegram delivery disabled or missing.

`scheduler`

- `ScheduledBriefService`: active only when `stabilityos.scheduler.enabled=true`
- `ScheduledNewsDigestService`: active only when `stabilityos.scheduler.news-digest-enabled=true`
- Both use cron plus configured timezone.
- Daily brief scheduler asks assistant: `"What should I do today?"`
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

Important detail: `app_metadata` exists from `V1__init.sql`, even though current business logic barely uses it.

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

What Planning Logic Really Is

Planning is not generic orchestration yet. It is explicit business logic over finance and health data:

- daily brief checks recent expense and health logging, sleep average, water average
- evening reflection counts whether expense and health data were logged today
- weekly review totals spending, finds top category, computes average sleep and water, and emits next-week priorities

This matters because current "assistant intelligence" is mostly deterministic planning logic plus formatting, not AI reasoning.

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
- almost no automated test coverage beyond context-load smoke test

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
- Maven Wrapper
- Docker Compose

Root `pom.xml` is only aggregator. Real application dependencies are in `backend/pom.xml`.

Testing State

There is currently only one test class:

- `backend/src/test/java/com/stabilityos/backend/BackendApplicationTests.java`

It only verifies Spring context loads. There are no focused unit tests or integration tests for finance, health, planning, input, draft, Telegram, or security flows yet.

Local Run Commands

Useful commands from repo docs:

- `docker compose up -d --build`
- `docker compose ps`
- `curl http://127.0.0.1:8080/api/health`
- `./backend/mvnw spring-boot:run`
- `./backend/mvnw test`
- `./ops/backup.sh`

One Mental Model

If you need one sentence:

This repo is a Spring Boot backend that captures structured personal signals, computes simple review loops, stores them in PostgreSQL, and optionally delivers scheduled summaries through Telegram.
