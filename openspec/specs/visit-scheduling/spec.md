# visit-scheduling Specification

## Purpose

Defines the date boundary and observable API behavior for scheduling veterinary visits without recording appointments in the past.

## Requirements

### Requirement: Schedule visits for allowed dates
The system SHALL schedule a valid visit request when its visit date is the current date or a future date.

#### Scenario: Schedule a visit for today
- **WHEN** a client submits a valid visit request whose date is the current date
- **THEN** the system persists the visit and returns a successful creation response

#### Scenario: Schedule a future visit
- **WHEN** a client submits a valid visit request whose date is after the current date
- **THEN** the system persists the visit and returns a successful creation response

### Requirement: Reject visits in the past
The system MUST reject a visit request whose date is before the current date with `400 Bad Request`, MUST include the validation message `Visit date must be today or in the future.`, and MUST NOT persist the visit.

#### Scenario: Reject a past visit through the visits endpoint
- **WHEN** a client submits a visit request dated before the current date through the general visit-creation endpoint
- **THEN** the system returns `400 Bad Request` with the message `Visit date must be today or in the future.` and does not persist the visit

#### Scenario: Reject a past visit for an owner's pet
- **WHEN** a client submits a visit request dated before the current date through the owner and pet visit-creation endpoint
- **THEN** the system returns `400 Bad Request` with the message `Visit date must be today or in the future.` and does not persist the visit
