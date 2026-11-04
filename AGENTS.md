# decoupledx-common — Agent Instructions

## Delivery gate (user-directed rule)

Do NOT commit, push, or open a PR until the user says **"go-live"**. Until
then: prepare changes in the working tree, validate locally, present.
On "go-live": commit → push → PR → merge.

## Purpose and guardrail

One source of truth for the stable, app-neutral seams of the decoupledx
applications. **Narrow seam library**: a class moves here only when stable,
completely understood, and consumed by 2+ applications. Never add: domain
models, routing policy (SecurityConfig), per-app configuration defaults,
application test scaffolding, or speculative abstractions.

## Status

Bootstrap complete: error contract (ErrorCode interface + BusinessException +
GlobalExceptionHandler), event contract (ADR-0004 semantics), TransactionRunner
seam, JWT machinery, fixed-clock + OpenAPI config, Boot auto-configuration
(all beans @ConditionalOnMissingBean). Spock specs green.

## Conventions

- Java 26, Spring Boot 4.1.1 / Modulith 2.1.1 via BOM imports (the consumer's
  Boot dependency management wins over what is managed here).
- Spock is the sole test-authoring framework (`src/test/groovy`,
  `**/*Spec.java` surefire includes, gmavenplus 5.1.0, Groovy 5.1.3,
  targetBytecode 25 — the plugin's enum tops out there; classes run on JDK 26).
- `ErrorCode` is an INTERFACE contract here; every application owns its own
  enum implementing it (codes stay tied to real domain rules). Enums satisfy
  `name()` automatically.
- Auto-configuration only: every seam bean is registered in
  `DecoupledxCommonAutoConfiguration` with `@ConditionalOnMissingBean`;
  no component scanning of application packages ever.
- Property vocabulary shared with the apps: `app.clock.fixed-instant`,
  `app.security.*` (issuer-uri, jwk-set-uri, audience, dev-public-key,
  allow-dev-key, end-session-uri), `app.common.problem-urn-base`,
  `app.common.openapi.title|description|version`.
- Records in their own top-level files; Spotless (Java) after edits:
  `./mvnw spotless:apply`.
- Versioning: `0.1.0-SNAPSHOT`; release by dropping the suffix and deploying
  to GitHub Packages from `main`.

## Known quirks

- `GlobalExceptionHandler` is scoped to @RestControllerAdvice(@RestController)
  — HTML/Thymeleaf error flows intentionally bypass it.
- The OpenAPI bean is conditional: apps that don't set
  `app.common.openapi.title` get springdoc defaults instead.
- `JwtDecoderConfig` falls back to the dev public key only when
  `app.security.issuer-uri` is empty/absent and `allow-dev-key` is true
  (prod profiles must set `allow-dev-key: false`).
