package org.springframework.samples.petclinic.visitconfirmation.control;

import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;

import java.time.LocalDate;
import java.util.Optional;

public interface VisitConfirmationRepository {

    Optional<LocalDate> lockVisitDate(int visitId);

    Optional<VisitConfirmation> findByVisitId(int visitId);

    void save(VisitConfirmation confirmation);
}
