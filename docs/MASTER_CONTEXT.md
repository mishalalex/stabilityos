# StabilityOS Master Context

StabilityOS is a self-hosted personal AI operating system.

Core stack:
- Java 21
- Spring Boot
- PostgreSQL
- Docker Compose
- Hermes for Telegram bridge
- Provider-agnostic AI gateway later

Main design:
- Spring Boot is the source of truth.
- PostgreSQL stores structured data.
- Hermes is only messaging + scheduling.
- Main future interface: POST /api/assistant/respond.
- Avoid direct OpenAI lock-in.
- Use AiGateway abstraction for model providers.
- Paperclip, OpenSwarm, MCP, Obsidian are later phases, not MVP.

Phase 1 goal:
Create portable Spring Boot + PostgreSQL foundation.
