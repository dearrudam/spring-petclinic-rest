package org.springframework.samples.petclinic.visitconfirmation.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationControl.VisitNotEligibleException;
import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationControl.VisitNotFoundException;
import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VisitConfirmationControlTests {

    private static final int VISIT_ID = 7;
    private static final Instant NOW = Instant.parse("2026-08-09T12:34:56.123456789Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private VisitConfirmationRepository repository;

    private VisitConfirmationControl control;

    @BeforeEach
    void setUp() {
        control = new VisitConfirmationControl(repository, CLOCK);
    }

    @ParameterizedTest(name = "R1.1 records a visit dated {0} day(s) from today")
    @ValueSource(ints = { 0, 1 })
    void recordsFirstConfirmationForEligibleVisit(int daysFromToday) {
        given(repository.lockVisitDate(VISIT_ID)).willReturn(Optional.of(LocalDate.now(CLOCK).plusDays(daysFromToday)));
        given(repository.findByVisitId(VISIT_ID)).willReturn(Optional.empty());

        VisitConfirmation confirmation = control.confirmVisit(VISIT_ID);

        assertThat(confirmation.visitId()).isEqualTo(VISIT_ID);
        assertThat(confirmation.confirmedAt()).isEqualTo(Instant.parse("2026-08-09T12:34:56.123456Z"));
        verify(repository).save(confirmation);
    }

    @Test
    @DisplayName("R1.2 provides an existing confirmation unchanged, even after the visit date")
    void returnsExistingConfirmationUnchanged() {
        VisitConfirmation existing = new VisitConfirmation(VISIT_ID, Instant.parse("2026-08-01T09:00:00Z"));
        given(repository.lockVisitDate(VISIT_ID)).willReturn(Optional.of(LocalDate.now(CLOCK).minusDays(1)));
        given(repository.findByVisitId(VISIT_ID)).willReturn(Optional.of(existing));

        assertThat(control.confirmVisit(VISIT_ID)).isSameAs(existing);
        verify(repository, never()).save(existing);
    }

    @Test
    @DisplayName("R1.3 reports an identity that does not identify a visit")
    void rejectsMissingVisit() {
        given(repository.lockVisitDate(VISIT_ID)).willReturn(Optional.empty());

        assertThrows(VisitNotFoundException.class, () -> control.confirmVisit(VISIT_ID));
        verify(repository, never()).findByVisitId(VISIT_ID);
    }

    @Test
    @DisplayName("R1.4 rejects an unconfirmed visit dated before today")
    void rejectsPastUnconfirmedVisit() {
        given(repository.lockVisitDate(VISIT_ID)).willReturn(Optional.of(LocalDate.now(CLOCK).minusDays(1)));
        given(repository.findByVisitId(VISIT_ID)).willReturn(Optional.empty());

        assertThrows(VisitNotEligibleException.class, () -> control.confirmVisit(VISIT_ID));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
