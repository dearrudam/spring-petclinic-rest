package org.springframework.samples.petclinic.visitconfirmation.control;

import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class VisitConfirmationControl {

    private final VisitConfirmationRepository repository;
    private final Clock clock;

    public VisitConfirmationControl(VisitConfirmationRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public VisitConfirmation confirmVisit(int visitId) {
        LocalDate visitDate = repository.lockVisitDate(visitId)
            .orElseThrow(VisitNotFoundException::new);
        VisitConfirmation existingConfirmation = repository.findByVisitId(visitId).orElse(null);
        if (existingConfirmation != null) {
            return existingConfirmation;
        }
        if (visitDate.isBefore(LocalDate.now(clock))) {
            throw new VisitNotEligibleException();
        }

        Instant confirmedAt = clock.instant().truncatedTo(ChronoUnit.MICROS);
        VisitConfirmation confirmation = new VisitConfirmation(visitId, confirmedAt);
        repository.save(confirmation);
        return confirmation;
    }

    public static final class VisitNotFoundException extends RuntimeException {
    }

    public static final class VisitNotEligibleException extends RuntimeException {
    }
}
