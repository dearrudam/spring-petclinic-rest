/// # PetClinic System
///
/// ## Charter
///
/// PetClinic manages veterinary care information and the workflows around owners, pets, veterinarians, and visits.
///
/// ## Components
///
/// - Visit Confirmation depends on Visit Management to obtain a visit's identity and date before recording confirmation.
///
/// ## Stack
///
/// - Architecture: package-by-layer with declared BCE component exceptions.
/// - Stack: Spring Boot server.
/// - Source root: `src/main/java`.
/// - Base package: `org.springframework.samples.petclinic`.
package org.springframework.samples.petclinic;
