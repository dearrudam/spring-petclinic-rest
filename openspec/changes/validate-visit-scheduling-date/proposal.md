## Why

Visit creation currently accepts dates in the past, allowing invalid appointments to be recorded. Visit scheduling must enforce a clear temporal rule so that only appointments for today or a future date are persisted.

## What Changes

- Validate the requested date in every visit-creation flow before scheduling the visit.
- Continue scheduling valid visits whose date is today or in the future.
- Reject visit requests dated before the current date with `400 Bad Request` and the message `Visit date must be today or in the future.`
- Document the date constraint and error response in the REST API contract.

## Capabilities

### New Capabilities

- `visit-scheduling`: Defines successful visit scheduling and rejection of appointments requested for past dates.

### Modified Capabilities

None.

## Impact

- Affects the OpenAPI visit schemas and generated request DTO validation metadata.
- Affects both visit-creation endpoints implemented by `VisitRestControllerV1` and `OwnerRestControllerV1`.
- Requires controller tests for past, current, and future visit dates and verification that rejected visits are not persisted.
