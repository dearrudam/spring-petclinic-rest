## 1. API Contract

- [ ] 1.1 Add a future-or-present Bean Validation annotation with the exact message `Visit date must be today or in the future.` to the `VisitFields.date` property in `src/main/resources/openapi.yml`.
- [ ] 1.2 Regenerate the OpenAPI sources and confirm both visit request DTO shapes carry the date constraint without editing generated files directly.

## 2. Visit Scheduling Tests

- [ ] 2.1 Extend `VisitRestControllerV1Tests` to cover successful scheduling for today and a future date, plus `400 Bad Request` with the exact field validation message for a past date.
- [ ] 2.2 Extend `OwnerRestControllerV1Tests` to reject a past-dated owner/pet visit with the same status and message.
- [ ] 2.3 Verify in both rejection tests that `ClinicService.saveVisit` is never called, proving invalid visits are not persisted.

## 3. Verification

- [ ] 3.1 Run the focused visit and owner controller test classes with the Maven wrapper and resolve any failures.
- [ ] 3.2 Run `./mvnw verify` to validate OpenAPI generation, compilation, tests, and coverage checks.
