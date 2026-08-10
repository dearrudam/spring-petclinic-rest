package org.springframework.samples.petclinic.visitconfirmation.control;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class VisitConfirmationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    Clock visitConfirmationClock() {
        return Clock.systemDefaultZone();
    }
}
