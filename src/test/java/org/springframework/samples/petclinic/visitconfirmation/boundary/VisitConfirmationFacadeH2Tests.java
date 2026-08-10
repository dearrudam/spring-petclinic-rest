package org.springframework.samples.petclinic.visitconfirmation.boundary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:visit-confirmation;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
@ActiveProfiles({ "h2", "spring-data-jpa" })
class VisitConfirmationFacadeH2Tests {

    private static final Instant NOW = Instant.parse("2013-01-01T12:34:56.123456Z");

    @Autowired
    private VisitConfirmationFacade facade;

    @Test
    @Transactional
    @DisplayName("R1.1 and R1.2 persist one immutable first confirmation")
    void persistsAndReusesFirstConfirmation() {
        VisitConfirmation first = facade.confirmVisit(1);
        VisitConfirmation repeated = facade.confirmVisit(1);

        assertThat(first.visitId()).isEqualTo(1);
        assertThat(first.confirmedAt()).isEqualTo(NOW);
        assertThat(repeated).isEqualTo(first);
    }

    @TestConfiguration
    static class FixedClockConfiguration {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(NOW, ZoneOffset.UTC);
        }
    }
}
