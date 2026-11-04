# decoupledx-common

Shared seam library for the decoupledx applications (Reservation System,
Team Management): the stable, application-neutral infrastructure seams both
products use, with **one** source of truth instead of per-app copies.

A deliberately **narrow seam library** — never a dumping ground. A class is
promoted here only when it is stable, fully understood, and has a real second
consumer. Application-owned policy (security routing, domain models, domain
error codes) stays in the applications.

## Contents

| Package | Seams |
|---|---|
| `com.decoupledx.common.error` | `ErrorCode` **contract** + `BusinessException` + `GlobalExceptionHandler` (ProblemDetail, URN base configurable via `app.common.problem-urn-base`) |
| `com.decoupledx.common.event` | Domain-event contract (`DomainEvent`, `DomainEventPublisher`, `DomainEventListener`) + composed annotation `OnDomainEvent` + seam impl `SpringDomainEventPublisher` (Modulith conventions) |
| `com.decoupledx.common.transaction` | `TransactionRunner` port + `SpringTransactionRunner` (no-rollback on `DataIntegrityViolationException`) |
| `com.decoupledx.common.security` | JWT machinery: `JwtDecoderConfig` (+ validators/roles converter), `RealmRoleOidcUserService`, `PromptLoginAuthorizationRequestResolver`, `KeycloakLogoutSuccessHandler` |
| `com.decoupledx.common.config` | Fixed `Clock` (`app.clock.fixed-instant`), OpenAPI bearer scheme (conditional on `app.common.openapi.title`) |
| `com.decoupledx.common.autoconfigure` | Registers all of the above; every bean `@ConditionalOnMissingBean` |

**Not in here** (deliberately): `SecurityConfig` (routing policy per app),
Testcontainers base specs (`@SpringBootTest` properties are app-specific),
`JwtSupport`/`WebUserSupport` (test-framework dependency — kept per-app), all
domain models, per-app `ErrorCode` enums (each app implements the contract).

## Consumption

Apps declare:

```xml
<dependency>
  <groupId>com.decoupledx</groupId>
  <artifactId>decoupledx-common</artifactId>
  <version>0.1.0</version>
</dependency>
```

Local development: build + install here once after cloning:

```bash
source ~/tools/env.sh
./mvnw install
```

CI resolves from **GitHub Packages** (`maven.pkg.github.com/efthymiosD/decoupledx-common`);
the publish workflow deploys **on `v*` tag pushes** (versions are immutable —
a re-deploy 409s). Main pushes only run CI. Java 26, Spring Boot 4.1.1 /
Modulith 2.1.1 aligned, tests in Spock (Groovy 5.1).

## Releases

Released versions: **v0.1.0** (first release; both applications consume
`0.1.0`). Release flow: bump the pom version, commit, `tag vX.Y.Z`, push
`main` + the tag together (the tag triggers the publish workflow).

## Conventions

See `AGENTS.md` for the contribution rules and `docs/adr/` for decisions
(including the ErrorCode-contract design and what stays app-owned).
