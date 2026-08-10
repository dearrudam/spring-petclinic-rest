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
- Controllers implement generated interfaces from `org.springframework.samples.petclinic.rest.api`; the visit controller is owned by its BCE boundary.
- MapStruct mappers convert between domain models and generated DTOs; visit mapping is owned by its BCE boundary and annotation processing uses Spring components by default.

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
- format: Markdown doc comments in `package-info.java`
- source root: `src/main/java`
- requirements style: EARS
- trace ids: `R<n>.<m>`

Spec language:
- default: `en`
- requirements: English EARS

Architecture layout:
- skill: `sdd4j-package-by-layer`
- scope: primary project architecture
- exception: `org.springframework.samples.petclinic.visit` uses `sdd4j-bce`
- base package: `org.springframework.samples.petclinic`
- spec package pattern: `org.springframework.samples.petclinic.capabilities.<capability>`
- exception spec: `org.springframework.samples.petclinic.visit.package-info.java`

Visit BCE layout:
- component package: `org.springframework.samples.petclinic.visit`
- boundary package: `org.springframework.samples.petclinic.visit.boundary`
- control package: `org.springframework.samples.petclinic.visit.control`
- entity package: `org.springframework.samples.petclinic.visit.entity`

Layer packages:
- entrypoints: `org.springframework.samples.petclinic.rest.controller.v1`, `org.springframework.samples.petclinic.rest.controller.v2`
- application: `org.springframework.samples.petclinic.service`
- entities: `org.springframework.samples.petclinic.model`
- infrastructure: `org.springframework.samples.petclinic.repository`, its subpackages, `org.springframework.samples.petclinic.mapper`, `org.springframework.samples.petclinic.config`, `org.springframework.samples.petclinic.security`, and `org.springframework.samples.petclinic.util`

Capability mapping:
- strategy: singular resource name; `<resource>` maps to controller classes whose resource stem matches, service methods whose domain noun matches, and the exact model class when declared
- all public generated-API override methods in a mapped controller belong to that controller's capability, including operations on child resources
- `ClinicService` is shared; its methods map by domain noun and do not make the whole service belong to one capability
- `BaseEntity`, `NamedEntity`, `Person`, and `Role` are shared models excluded from entity drift unless explicitly declared
- generated OpenAPI interfaces and DTOs, mappers, repositories, persistence implementations, configuration, validation plumbing, and transport error handling are implementation details unless explicitly declared
- structural drift is checked both ways between boundary operations and mapped controller/service methods, entities and mapped models, and requirement ids and tests

Stack:
- skill: `spring-boot-server`
- build tool: Maven wrapper
- verification command: `./mvnw verify`

Traceability:
- tests may remain in existing layer-based test packages
- each requirement id must appear in a test method name, display name, JavaDoc, or annotation
