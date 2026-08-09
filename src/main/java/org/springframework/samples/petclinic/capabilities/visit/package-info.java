/// # Visit Management
///
/// > Manages visits and their association with pets throughout the visit lifecycle.
///
/// ## Boundary
///
/// - `list-visits` - Lists all recorded visits.
/// - `get-visit` - Retrieves a visit by its identity.
/// - `list-pet-visits` - Lists the visits associated with a pet.
/// - `create-visit` - Records a new visit for a pet.
/// - `update-visit` - Changes an existing visit's editable details.
/// - `delete-visit` - Removes an existing visit.
///
/// ## Requirements
///
/// ### R1 Retrieve visits
///
/// - R1.1 - When visits are requested and at least one visit exists, the capability shall provide all recorded visits.
///
/// - R1.2 - When visits are requested and no visits exist, the capability shall report that the visit collection was not found.
///
/// - R1.3 - When an existing visit is requested by identity, the capability shall provide that visit and its pet association.
///
/// - R1.4 - When a visit is requested by an identity that does not identify a visit, the capability shall report that the visit was not found.
///
/// - R1.5 - When visits are requested for a pet, the capability shall provide the visits associated with that pet.
///
/// ### R2 Create a visit
///
/// - R2.1 - When a valid new visit with a pet association is submitted, the capability shall persist it, assign its identity, and provide the created visit.
///
/// - R2.2 - If a visit creation request has no non-empty description, then the capability shall reject the request.
///
/// ### R3 Update a visit
///
/// - R3.1 - When an existing visit is updated, the capability shall persist its new date and description while retaining its identity and pet association.
///
/// - R3.2 - If a visit update has no non-empty description, then the capability shall reject the update.
///
/// - R3.3 - When an update targets an identity that does not identify a visit, the capability shall report that the visit was not found.
///
/// ### R4 Delete a visit
///
/// - R4.1 - When deletion is requested for an existing visit, the capability shall remove that visit.
///
/// - R4.2 - When deletion is requested for an identity that does not identify a visit, the capability shall report that the visit was not found.
///
/// ## Entities
///
/// - Visit - A dated description of care associated with one pet and identified after persistence.
///
/// ## Out of scope
///
/// - Scheduling visits through owner operations and managing the pet lifecycle.
/// - Transport formats, persistence technology, and DTO conversion mechanics.
package org.springframework.samples.petclinic.capabilities.visit;
