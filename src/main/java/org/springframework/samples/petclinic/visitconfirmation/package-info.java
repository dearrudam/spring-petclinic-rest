/// # Visit Confirmation
///
/// > Records an authorized confirmation for an eligible visit.
///
/// ## Boundary
///
/// - `confirm-visit` - Confirms an existing visit and provides its confirmation record.
///
/// ## Requirements
///
/// ### R1 Confirm a visit
///
/// - R1.1 - When an owner administrator confirms an unconfirmed visit dated today or in the future, the capability shall record the visit identity and current time and provide the confirmation.
///
/// - R1.2 - When an owner administrator confirms an already-confirmed visit, the capability shall provide the existing confirmation unchanged.
///
/// - R1.3 - If confirmation is requested for an identity that does not identify a visit, then the capability shall report that the visit was not found.
///
/// - R1.4 - While a visit is dated before the current date, when confirmation is requested, the capability shall reject the request as a conflict because the visit is no longer eligible for confirmation.
///
/// - R1.5 - If a caller is not an owner administrator, then the capability shall reject the confirmation request as unauthorized.
///
/// ## Entities
///
/// - VisitConfirmation - The immutable first confirmation time associated with one visit identity.
///
/// ## Out of scope
///
/// - Creating, rescheduling, updating, or deleting visits.
/// - Changing or cancelling an existing confirmation.
/// - Transport formats and persistence technology.
package org.springframework.samples.petclinic.visitconfirmation;
