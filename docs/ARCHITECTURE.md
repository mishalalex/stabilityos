# StabilityOS Architecture

## Core Runtime

Telegram
-> Hermes
-> Spring Boot StabilityOS API
-> PostgreSQL

## Spring Boot Responsibilities

- Finance logic
- Health logic
- Planning/review logic
- Assistant context building
- Memory
- AI provider abstraction
- News digest later

## Hermes Responsibilities

- Telegram messaging
- Scheduled prompts
- Calls Spring Boot backend

Hermes is not the brain.
Spring Boot owns the brain.
