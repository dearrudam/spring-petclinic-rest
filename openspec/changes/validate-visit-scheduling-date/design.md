## Context

The API contract in `src/main/resources/openapi.yml` generates the request DTOs and API interfaces used by both visit-creation controllers. Bean Validation failures already flow through `ExceptionControllerAdvice` as `400 Bad Request` `ProblemDetail` responses with field-level messages. See `proposal.md` for the motivation and `specs/visit-scheduling/spec.md` for the behavioral contract.

## Goals / Non-Goals

**Goals:**

- Express the date rule once in the source OpenAPI contract and apply it consistently to both generated visit request types.
- Reuse the existing validation error response shape and controller validation lifecycle.
- Compare date-only values against the application server's current local date.

**Non-Goals:**

- Introduce appointment times, time zones, availability, or conflict detection.
- Change visit persistence or database schemas.
- Redesign the existing `ProblemDetail` validation response.

## Decisions

### Add declarative validation to the generated visit date field

Add a Bean Validation future-or-present constraint to the `VisitFields.date` property through the OpenAPI field annotation extension, with the exact message `Visit date must be today or in the future.` Both creation request models derive from this schema, so request validation runs before either controller calls the clinic service.

This is preferred over imperative checks in both controllers because it avoids duplicated boundary logic and uses the established generated-contract validation mechanism. A custom validator was considered but is unnecessary because the standard future-or-present constraint has the required date semantics.

### Return the message through the existing validation error representation

Allow `ExceptionControllerAdvice` to convert the constraint violation into the existing `400 Bad Request` `ProblemDetail`. The exact constraint message will be present in the corresponding field validation entry, preserving the API's established error envelope rather than adding a visit-specific exception path.

Returning an ad hoc response directly from each controller was rejected because it would duplicate response construction and diverge from other request validation failures.

### Use the server-local current date

Use Bean Validation's current-date resolution for `LocalDate`, which compares against the application server's clock and local date. This matches the application's existing date-only model and avoids introducing time-zone semantics that the API does not currently represent.

## Risks / Trade-offs

- [Generated validation annotation behavior changes with OpenAPI Generator upgrades] -> Cover both generated request shapes with controller tests and run the full Maven verification pipeline.
- [The date boundary follows the server's date rather than the caller's date] -> Document this behavior and keep time-zone support outside this change.
- [The shared `VisitFields` schema also supplies fields for visit updates] -> Treat past dates as invalid consistently whenever that shared request model is validated; no persisted historical records are modified by this change alone.
