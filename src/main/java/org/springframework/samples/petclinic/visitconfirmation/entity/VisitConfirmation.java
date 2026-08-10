package org.springframework.samples.petclinic.visitconfirmation.entity;

import java.time.Instant;

public record VisitConfirmation(int visitId, Instant confirmedAt) {
}
