## BCE Migration Plan

### Current Shape

The `visit` capability is currently distributed across technical layers:

- Contract: `src/main/java/org/springframework/samples/petclinic/capabilities/visit/package-info.java`
- REST entrypoint: `rest/controller/v1/VisitRestControllerV1.java`
- Indirect owner entrypoint: `OwnerRestControllerV1.addVisitToOwner(...)`
- Application facade: visit methods in `ClinicService` and `ClinicServiceImpl`
- Entity: `model/Visit.java`
- Mapping and validation: `mapper/VisitMapper.java` and `rest/validation/VisitDate*`
- Persistence port and adapters: `repository/VisitRepository.java` and the JDBC, JPA, and Spring Data JPA implementations
- Tests: `VisitRestControllerV1Tests` and visit cases in `AbstractClinicServiceTests`

The visit implementation depends on the legacy `Pet` model. Pet and owner API responses also include visits, and owner, pet, and pet-type persistence adapters delete visit rows during aggregate deletion. The full verification command is `./mvnw verify`.

### Proposed Business Components

| Component | Responsibility | Status | Boundary | Control | Entity | Source Evidence |
| --- | --- | --- | --- | --- | --- | --- |
| `visit` | Manage visits and their association with pets throughout the visit lifecycle | Completed | REST controller, public facade, DTO mapper, date validator, persistence adapters | Transactional visit use cases and persistence port | `Visit` | Existing visit capability spec, controller, service methods, entity, repository adapters, and traced tests |

Only `visit` will migrate to BCE. Owner, pet, pet type, vet, specialty, and shared technical concerns remain in the current package-by-layer architecture.

### Package Naming Decision

Use a top-level business package under the application base package as the BCE component root:

```text
org.springframework.samples.petclinic.visit
|-- package-info.java
|-- boundary
|   |-- VisitFacade.java
|   |-- VisitRestControllerV1.java
|   |-- VisitMapper.java
|   |-- validation
|   `-- persistence
|       |-- jdbc
|       |-- jpa
|       `-- springdatajpa
|-- control
|   |-- VisitControl.java
|   `-- VisitRepository.java
`-- entity
    `-- Visit.java
```

Move the existing capability contract from `org.springframework.samples.petclinic.capabilities.visit` to the new component root. This keeps `package-info.java` as the single capability contract and avoids creating a parallel specification. The project remains package-by-layer by default, with an explicit `sdd4j-bce` routing exception for `org.springframework.samples.petclinic.visit`.

Generated OpenAPI interfaces and DTOs remain in their current shared packages under `org.springframework.samples.petclinic.rest.api` and `org.springframework.samples.petclinic.rest.dto`. The plugin already uses `interfaceOnly=true`, so `VisitRestControllerV1` is a handwritten implementation and can move to the BCE boundary while continuing to implement the generated `VisitsApi` interface by import.

The OpenAPI Generator Maven plugin, its single execution, `apiPackage`, `modelPackage`, output directory, and source-root registration remain unchanged. Generated contracts are treated as shared transport details outside the business component. Shared error handling, security, configuration, and `BaseEntity` also remain outside the component.

### Public Contract Baseline

The migration must preserve:

- All paths and schemas in `src/main/resources/openapi.yml`.
- `GET /api/visits`, including `404` for an empty collection.
- `GET /api/visits/{visitId}`.
- `POST /api/visits`, including `201` and the `Location` header.
- `PUT /api/visits/{visitId}`, including `204`.
- `DELETE /api/visits/{visitId}`, including `204`.
- `POST /api/owners/{ownerId}/pets/{petId}/visits`.
- The `OWNER_ADMIN` authorization requirement.
- `VisitDto` and `VisitFieldsDto` JSON shapes and validation responses.
- The exact invalid-date validation message.
- Database schemas, persisted data, profile selection, and repository semantics.
- Requirement trace IDs `R1.1` through `R4.2`.

The migration must not silently correct existing behavior, including OpenAPI/runtime status differences, ignored `ownerId` in nested visit creation, date-validation differences between operations, caller-provided IDs on `POST /visits`, schema nullability differences, or unspecified list ordering.

### Migration Strategy

Use a strangler-style migration. Introduce a visit-owned facade and control while the legacy model and repositories are still in place, redirect callers through that boundary, and then move the entity and persistence adapters in independently verifiable slices.

The owner nested route remains owned by the owner controller because the visit specification declares scheduling through owner operations out of scope. Its only architectural change is to call the public visit boundary rather than the shared service facade.

The direct `Visit` to `Pet` association remains during this migration. Removing that coupling would require migration or redesign of the pet capability and is outside scope.

### Steps

1. Establish a green baseline.
   - Run `./mvnw verify` before implementation.
   - Record focused results for visit REST, owner nested creation, and all repository profiles.
   - Add characterization tests only where observable behavior is not currently protected, especially authorization, nested-route response details, owner/pet mismatch behavior, caller-provided create IDs, and operation-specific date validation.

2. Declare transitional architecture routing.
   - Keep `sdd4j-package-by-layer` as the project default.
   - Route only `org.springframework.samples.petclinic.visit` through `sdd4j-bce`.
   - Move the existing visit `package-info.java` to `org.springframework.samples.petclinic.visit`, changing only its package declaration and preserving all contract content and requirement IDs.

3. Introduce visit control without moving persistence.
   - Add `VisitControl` over the existing `VisitRepository` and `Visit` types.
   - Transfer visit transaction boundaries and not-found normalization from `ClinicServiceImpl` without changing semantics.
   - Add `VisitFacade` exposing the six operations declared by the specification: list, get, list by pet, create, update, and delete.
   - Keep visit methods in `ClinicService` and make `ClinicServiceImpl` delegate to the new control during the transition.

4. Redirect existing entrypoints.
   - Change `VisitRestControllerV1` to use `VisitFacade`.
   - Change only `OwnerRestControllerV1.addVisitToOwner(...)` to persist through `VisitFacade`.
   - Do not add owner lookup, pet lookup, ownership validation, or broader date validation.

5. Move the visit boundary.
   - Move `VisitRestControllerV1`, `VisitMapper`, `VisitDateValidation`, and `VisitDateValidator` under `org.springframework.samples.petclinic.visit.boundary`.
   - Keep the controller implementing the generated `org.springframework.samples.petclinic.rest.api.VisitsApi` interface.
   - Update the OpenAPI validator annotation reference without changing the API schema.
   - Update `PetMapper`, owner integration, and test imports.
   - Regenerate OpenAPI sources and compare generated visit DTO validation behavior.

6. Move the entity and persistence port.
   - Move `model.Visit` to `org.springframework.samples.petclinic.visit.entity.Visit`.
   - Move the persistence port to `org.springframework.samples.petclinic.visit.control.VisitRepository`.
   - Update imports in `Pet`, mappers, services, deletion adapters, tests, and fixtures.
   - Preserve all JPA mappings, table and column names, identity behavior, relationships, constructor defaults, and validation annotations.

7. Move persistence adapters one profile at a time.
   - Move and verify the plain JPA adapter.
   - Move and verify the Spring Data JPA adapter and overrides.
   - Move and verify the JDBC adapter, row mapper, and directly associated extractor.
   - Keep SQL and query behavior unchanged.
   - Treat owner use of `JdbcPetVisitExtractor` as an explicit integration with the visit persistence boundary rather than migrating owner.

8. Converge tests and remove obsolete shells.
   - Move `VisitRestControllerV1Tests` to the matching BCE test package without changing scenarios or trace IDs.
   - Add focused `VisitControl` tests only where extraction creates behavior not adequately covered by the existing integration matrix.
   - Keep visit scenarios in `AbstractClinicServiceTests`; project guidance explicitly permits tests to remain in existing layer-based packages.
   - Decide whether public Java compatibility requires retaining visit methods in `ClinicService`. Retain delegators if those methods are an external contract.
   - Identify source and test directories emptied by the migration, check them again immediately before removal, and remove only confirmed-empty directories.
   - Run `./mvnw verify` as the final gate.

### Verification Gates

Focused REST and owner integration:

```bash
./mvnw -Dtest=VisitRestControllerV1Tests,OwnerRestControllerV1Tests test
```

Indirect pet and owner representations:

```bash
./mvnw -Dtest=PetRestControllerV1Tests,V2RestControllersTests test
```

Persistence matrix:

```bash
./mvnw -Dtest=ClinicServiceSpringDataJpaTests,ClinicServiceJpaTests,ClinicServiceH2JdbcTests,ClinicServiceHsqlJdbcTests test
```

Final verification:

```bash
./mvnw verify
```

Postman and JMeter suites are not initial migration gates because their checked-in visit dates are already in the past. Changing those dates or extending date validation would be separate work.

### Risks And Checks

| Risk | Check |
| --- | --- |
| Structural work changes observable behavior | Characterize current behavior first and compare HTTP responses after every boundary step |
| Owner or pet responses lose visit data | Run owner, pet, and v2 controller tests after mapper or entity moves |
| Repository profile selection breaks | Move one adapter at a time and run its matching integration suite |
| Transaction semantics change | Transfer annotations with the use case and preserve read-only/write boundaries |
| Pet, owner, or pet-type deletion stops removing visits | Preserve deletion queries and run the complete persistence matrix |
| Nested owner route is accidentally corrected | Preserve its current lack of owner and ownership checks |
| Generated validation changes | Regenerate from OpenAPI and compare generated annotations and REST tests |
| Legacy code bypasses the visit component control | Route application calls through `VisitFacade`; document persistence-level cascade integrations |
| Mixed SDD4J adapters become implicit | Declare a package-specific BCE routing exception before moving the specification mapping |
| Java consumers depend on old packages or `ClinicService` | Confirm Java API scope before removing compatibility delegators; HTTP/OpenAPI remains frozen |
| Test traceability is lost during relocation | Keep every `R1.1` through `R4.2` ID grep-visible in the corresponding tests |

### Completion Criteria

The migration is complete when:

- The single visit specification resides at `org.springframework.samples.petclinic.visit/package-info.java`.
- Generated API interfaces and DTOs retain their existing shared packages and generation configuration.
- Visit production code is owned by the component's `boundary`, `control`, and `entity` packages.
- External entrypoints access visit use cases through `VisitFacade`.
- All persistence profiles retain their current behavior and selection rules.
- REST paths, DTOs, status codes, headers, validation, security, schemas, and persisted data are unchanged.
- Requirement IDs `R1.1` through `R4.2` remain traceable.
- Other capabilities remain in the package-by-layer architecture.
- Obsolete empty visit source and test directories have been safely removed.
- `./mvnw verify` passes.

### Recommended First Step

Completed on 2026-08-09. The baseline and final verification were green, behavior characterization was extended, and the visit capability now owns its BCE boundary, control, entity, persistence adapters, specification, and directly associated tests.
