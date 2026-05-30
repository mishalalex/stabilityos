# StabilityOS

StabilityOS is a self-hosted personal operating system for daily life management.

The goal is to build a private assistant that helps track and reason about:

* Finance
* Health
* Daily planning
* Weekly reviews
* Personal memory
* News
* Long-term self-improvement

This project is also a serious Spring Boot portfolio project.

---

## Current Status

**Current phase:** Phase 2 — Finance Core

### Completed

* Phase 1: Portable Spring Boot foundation
* Local Docker setup
* VPS Docker deployment
* PostgreSQL container
* Spring Boot backend
* Flyway migration setup
* `/api/health`
* Backup script
* GitHub repo setup

### In Progress

* Phase 2: Finance Core

    * Expense logging
    * Expense listing
    * Monthly spending summary

---

## Architecture

```text
Telegram
  ↓
Hermes
  ↓
Spring Boot StabilityOS API
  ↓
PostgreSQL
```

Future assistant flow:

```text
Telegram
  ↓
Hermes
  ↓
POST /api/assistant/respond
  ↓
Spring Boot context builder + memory + rules + AI gateway
  ↓
Natural assistant response
```

---

## Core Design Principles

* Spring Boot is the source of truth.
* PostgreSQL stores structured data.
* Hermes is only the Telegram/scheduling bridge.
* Avoid OpenAI lock-in.
* Use a provider-agnostic AI gateway later.
* Keep core logic deterministic before adding agents.
* Docker Compose must make the system portable.
* VPS should be replaceable at any time.
* Develop locally, commit to GitHub, deploy to VPS.
* Each phase should be developed in its own Git branch.
* README should be updated at every phase.

---

## Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* Flyway
* Docker Compose
* GitHub
* Hermes later
* Spring AI / provider-agnostic AI gateway later

---

## Project Structure

```text
stabilityos/
  backend/              Spring Boot backend
  docs/                 Planning and architecture docs
  hermes/               Future Hermes integration
  ops/                  Operational scripts
  backups/              Local backup output
  docker-compose.yml    Local/VPS runtime setup
  README.md
```

---

## Current Endpoints

### Health

```http
GET /api/health
```

Response:

```json
{
  "status": "ok"
}
```

### Finance

Phase 2 target endpoints:

```http
POST /api/expenses
GET /api/expenses
GET /api/finance/monthly-summary
```

---

## Run Locally

From project root:

```bash
docker compose up -d --build
```

Check containers:

```bash
docker compose ps
```

Test backend:

```bash
curl http://127.0.0.1:8080/api/health
```

Expected:

```json
{"status":"ok"}
```

---

## Backup

Run:

```bash
./ops/backup.sh
```

This creates:

```text
backups/db_TIMESTAMP.sql
backups/stabilityos_project_TIMESTAMP.tar.gz
```

The `.sql` file backs up PostgreSQL data.

The `.tar.gz` file backs up the project snapshot.

---

## Deployment Model

Development happens locally.

```text
Local machine → GitHub → VPS
```

Workflow locally:

```bash
git add .
git commit -m "message"
git push
```

Workflow on VPS:

```bash
git pull
docker compose up -d --build
```

The VPS is only runtime.

GitHub + backups are the source of truth.

---

## Branch Strategy

`main` contains stable deployed code.

Each phase gets its own branch:

```text
phase-1-foundation
phase-2-finance-core
phase-3-health-core
phase-4-planning-review
phase-5-assistant-brain
phase-6-memory-personality
phase-7-hermes-integration
phase-8-news-digest
phase-9-obsidian-export
phase-10-agent-experiments
phase-11-appearance-confidence
```

Current branch:

```text
phase-2-finance-core
```

For fixes:

```text
fix/docker-port-conflict
fix/backup-script
```

For experiments:

```text
experiment/paperclip
experiment/openswarm
```

---

## Roadmap

### Phase 1 — Portable Foundation ✅

Built:

* Spring Boot backend
* PostgreSQL container
* Docker Compose setup
* Flyway baseline
* `/api/health`
* GitHub repo
* VPS deployment
* Backup script

Purpose:

Create a portable foundation that can be moved to a new VPS easily.

---

### Phase 2 — Finance Core 🚧

Build:

* Expense entity
* Expense repository
* Expense service
* Expense controller
* Expense DTOs
* Expense logging API
* Expense listing API
* Monthly summary API

Endpoints:

```http
POST /api/expenses
GET /api/expenses
GET /api/finance/monthly-summary
```

Purpose:

Start tracking real daily spending.

---

### Phase 3 — Health Core

Build:

* Sleep logging
* Water logging
* Weight logging
* Health summary

Purpose:

Give StabilityOS personal health context.

---

### Phase 4 — Planning and Review

Build:

* Daily brief
* Evening reflection
* Weekly review

Purpose:

Turn raw data into daily and weekly guidance.

---

### Phase 5 — Assistant Brain

Build:

```http
POST /api/assistant/respond
```

Responsibilities:

* Classify user intent
* Gather finance/health/planning context
* Apply deterministic rules
* Call AI provider through abstraction
* Return natural assistant response

Purpose:

Make StabilityOS feel like an assistant, not a set of cron jobs.

---

### Phase 6 — Memory and Personality

Build:

* Assistant memory
* User preferences
* Behavior observations
* Consistency patterns

Purpose:

Give StabilityOS continuity and personality.

---

### Phase 7 — Hermes Integration

Build:

* Telegram bridge
* Hermes plugin
* Scheduled morning/evening/weekly messages

Purpose:

Make StabilityOS usable through Telegram.

---

### Phase 8 — News Digest

Build:

* Local news digest
* Kottayam/Kerala/India/global sections
* Daily scheduled summary

Purpose:

Provide daily situational awareness.

---

### Phase 9 — Obsidian Export

Build:

* Markdown export
* Daily brief notes
* Weekly review notes
* News digest notes

Purpose:

Create readable long-term personal history.

---

### Phase 10 — Agent Experiments

Evaluate:

* Paperclip
* OpenSwarm
* MCP
* Multi-agent workflows

Purpose:

Add orchestration only after the core assistant works.

---

### Phase 11 — Appearance and Confidence Intelligence

Build later:

* Appearance tracking dashboard
* Weight vs facial aesthetics correlation
* Sleep vs skin quality
* Mood vs grooming
* Wardrobe intelligence
* Monthly glow-up reports
* Confidence tracking

Purpose:

Track slow compounding improvement over 1–2 years.

---

## Long-Term Vision

StabilityOS should become a private personal assistant that can say:

* What happened
* What matters
* What is slipping
* What to do today
* What to fix this week

The final goal is not just tracking.

The final goal is better daily stability, better decisions, and long-term personal compounding.
