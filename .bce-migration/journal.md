## 2026-08-09 - Planning: Visit Capability Migration

- Status: Completed
- Changes made: Created the migration plan and journal only; no application source, tests, configuration, API contract, generated code, or database files were changed.
- Tests run: None. This was a planning-only activity.
- Issues encountered: The visit capability is coupled to the legacy pet model, owner nested visit creation, indirect owner/pet DTO mapping, and cross-capability deletion behavior.
- Decisions made: Migrate only `visit`; keep the existing capability spec as the BCE component root; preserve all observable behavior; leave the nested route owned by owner and integrate it through the visit boundary; retain package-by-layer as the default architecture with a visit-only BCE routing exception.
- Next steps: Run the baseline verification and add any missing behavior-characterization tests before implementing Step 2 of the plan.

## 2026-08-09 - Planning Correction: Generated Visit Boundary

- Status: Completed
- Changes made: Corrected the migration plan to account for the execution-wide `apiPackage` setting in `openapi-generator-maven-plugin`; no application source, tests, plugin configuration, API contract, or generated files were changed.
- Tests run: None. The OpenAPI Generator 7.23.0 Maven plugin parameters and Spring generator options were reviewed against the current `pom.xml` and generated `VisitsApi`.
- Issues encountered: The Spring generator has no supported per-tag API package option in one execution, selective generation is inclusion-based, and generated default interface methods require package-local `ApiUtil` support.
- Decisions made: Use two selective plugin executions with isolated outputs; generate shared DTOs and non-visit APIs in the legacy execution; generate only `VisitsApi` and its support in `capabilities.visit.boundary.api`; keep `interfaceOnly=true`; keep the handwritten controller in `capabilities.visit.boundary.web`.
- Next steps: Establish a green baseline, then implement and verify the generator split before moving handwritten visit code.

## 2026-08-09 - Planning Simplification: Shared Generated Contracts

- Status: Completed
- Changes made: Restored the simpler migration plan; no application source, tests, plugin configuration, API contract, or generated files were changed.
- Tests run: None. This was a planning-only decision.
- Issues encountered: Splitting OpenAPI generation would add execution, output, supporting-file, and tag-selection complexity without being required to move the handwritten controller.
- Decisions made: Keep the existing single OpenAPI Generator execution and shared generated packages unchanged. Treat generated APIs and DTOs as transport details outside the BCE component. Move only the handwritten `VisitRestControllerV1` into the visit boundary while continuing to implement `org.springframework.samples.petclinic.rest.api.VisitsApi`.
- Next steps: Establish a green baseline and proceed with the simplified incremental migration when implementation is approved.

## 2026-08-09 - Package Decision: Top-Level Visit Component

- Status: Completed
- Changes made: Updated the migration plan's target package only; no application source, tests, plugin configuration, API contract, generated files, or existing capability specification were moved.
- Tests run: None. This was a planning-only naming decision.
- Issues encountered: The current specification is under `capabilities.visit`, so implementation must move it rather than create a second specification.
- Decisions made: Use `org.springframework.samples.petclinic.visit` as the BCE component root, with `boundary`, `control`, and `entity` beneath it. Preserve the existing `package-info.java` content and requirement IDs when moving its package declaration.
- Next steps: Establish a green baseline and use the new package root for every visit migration slice.
