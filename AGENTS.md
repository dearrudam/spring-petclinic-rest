# AGENTS.md

## Commands

- Use the Maven wrapper: `./mvnw`, not system `mvn`; the build enforces Maven `3.9.9` and compiles with Java release `25`.
- Full verification: `./mvnw verify` runs OpenAPI generation, compilation, tests, JaCoCo report, and coverage checks.
- Focused Java test: `./mvnw -Dtest=OwnerRestControllerV1Tests test` or `./mvnw -Dtest=OwnerRestControllerV1Tests#testMethod test`.
- Run locally: `./mvnw spring-boot:run`; default URL is `http://localhost:9966/petclinic/`.
- Build container image with Jib only when needed: `./mvnw jib:dockerBuild` or `./mvnw jib:build`.

## Generated API Contract

- `src/main/resources/openapi.yml` is the source for generated REST API interfaces and DTOs.
- Generated code lives under `target/generated-sources/openapi/src/main/java` and is added during `generate-sources`; do not edit generated `rest/api` or `rest/dto` classes directly.
- Controllers in `src/main/java/.../rest/controller/v1` and `v2` implement generated interfaces from `org.springframework.samples.petclinic.rest.api`.
- MapStruct mappers in `src/main/java/.../mapper` convert between domain models and generated DTOs; annotation processing uses Spring components by default.

## API Validation

- `src/main/resources/openapi.yml` is the source of truth for REST request schemas and transport-level validation.
- When a requirement can be expressed through OpenAPI constraints or generated Jakarta Bean Validation annotations, update `openapi.yml` before adding manual validation to controllers or services.
- When create and update operations have different validation rules, use operation-specific request schemas instead of branching on entity identity such as `isNew()`.
- Use `x-field-extra-annotation` for Jakarta constraints not directly expressible by OpenAPI Schema, such as `@FutureOrPresent`.
- Keep response schemas separate from constrained request schemas when responses may legitimately contain values rejected for new requests.
- Defaults and state transitions that Bean Validation cannot perform remain application behavior.
- Do not duplicate generated DTO validation in the service unless the same invariant must also protect non-HTTP callers; when uncertain, ask before duplicating it.

## Runtime Profiles

- The app expects two active profiles: one database profile (`h2`, `hsqldb`, `mysql`, `postgres`) plus one repository profile (`jdbc`, `jpa`, `spring-data-jpa`).
- Default runtime profiles are `h2,spring-data-jpa`; default test profiles are `hsqldb,spring-data-jpa` unless a test overrides them with `@ActiveProfiles`.
- SQL init files are selected from `src/main/resources/db/${spring.sql.init.platform}/schema.sql` and `data.sql`; add schema/data changes for every supported database when behavior depends on them.
- Security is enabled by default through `petclinic.security.enable=true`; use `--spring-boot.run.arguments=--petclinic.security.enable=false` only for local/manual checks that need unauthenticated endpoints.

## Test Notes

- Controller tests are Spring Boot tests wired with `ApplicationTestConfig` and `@MockitoBean`, then exercised through standalone `MockMvc`; follow that pattern for new controller coverage.
- Service tests intentionally cover multiple repository/database profile combinations; choose the matching existing `ClinicService*Tests` or `UserService*Tests` class when narrowing failures.
- JaCoCo excludes generated OpenAPI `rest/api` and `rest/dto` packages and enforces `0.85` line and `0.66` branch coverage on `verify`.
- Postman regression tests live in `src/test/postman` and require the app running locally plus Node.js and `jq`; run with `zsh src/test/postman/postman-tests.sh` from the repo root if the script is not executable.
- JMeter performance tests live in `src/test/jmeter` and require the app running locally plus JMeter `5.6.3+`; keep generated results out of source changes unless explicitly requested.

## SDD4J

Spec source:
- format: `package-info.java`
- source root: `src/main/java`
- requirements style: EARS
- trace ids: `R<n>.<m>`

Spec language:
- default: `pt-BR`
- requirements: localized EARS

Architecture layout:
- skill: `sdd4j-package-by-layer`
- scope: primary project architecture
- base package: `org.springframework.samples.petclinic`
- spec package pattern: `org.springframework.samples.petclinic.capabilities.<capability>`

Layer packages:
- entrypoints: `org.springframework.samples.petclinic.rest.controller.v1`, `org.springframework.samples.petclinic.rest.controller.v2`
- application: `org.springframework.samples.petclinic.service`
- entities: `org.springframework.samples.petclinic.model`
- infrastructure: `org.springframework.samples.petclinic.mapper`, `org.springframework.samples.petclinic.repository`
- generated OpenAPI APIs and DTOs, configuration, security, validation, advice, utilities, and persistence implementations are excluded from capability drift unless explicitly declared

Capability mapping:
- strategy: resource stem
- classes whose resource stem matches the capability map to that capability
- methods in shared classes map by the resource named in the method signature
- controller operations involving multiple resources map to the capability owning the operation's primary resource
- `src/main/resources/openapi.yml` is contract-relevant for REST boundary operations
- generated API interfaces and DTOs are excluded from drift, but their OpenAPI source is not
- shared base models and generic support classes are excluded from entity drift unless explicitly declared
- tests may remain in existing layer-oriented test packages

Stack:
- skill: `spring-boot-server`
- build tool: Maven wrapper
- verification command: `./mvnw verify`

Traceability:
- requirement ids use exact runner-visible `Rn.m` display names or parameterized-case labels
- normalized Java identifiers are valid only when test infrastructure displays the exact dotted id
- JavaDoc and comments do not count
