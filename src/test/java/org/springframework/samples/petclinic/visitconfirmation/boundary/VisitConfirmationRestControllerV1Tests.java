package org.springframework.samples.petclinic.visitconfirmation.boundary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationControl.VisitNotEligibleException;
import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationControl.VisitNotFoundException;
import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class VisitConfirmationRestControllerV1Tests {

    @Autowired
    private VisitConfirmationRestControllerV1 controller;

    @MockitoBean
    private VisitConfirmationFacade facade;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    @DisplayName("R1.1 provides the recorded visit identity and confirmation time")
    @WithMockUser(roles = "OWNER_ADMIN")
    void confirmsVisit() throws Exception {
        given(facade.confirmVisit(7)).willReturn(
            new VisitConfirmation(7, Instant.parse("2026-08-09T12:34:56.123456Z")));

        mockMvc.perform(put("/api/visits/7/confirmation").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.visitId").value(7))
            .andExpect(jsonPath("$.confirmedAt").value("2026-08-09T12:34:56.123456Z"));
    }

    @Test
    @DisplayName("R1.3 reports a missing visit as not found")
    @WithMockUser(roles = "OWNER_ADMIN")
    void reportsMissingVisit() throws Exception {
        given(facade.confirmVisit(999)).willThrow(new VisitNotFoundException());

        mockMvc.perform(put("/api/visits/999/confirmation"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("R1.4 reports an ineligible visit as a conflict")
    @WithMockUser(roles = "OWNER_ADMIN")
    void reportsIneligibleVisit() throws Exception {
        given(facade.confirmVisit(7)).willThrow(new VisitNotEligibleException());

        mockMvc.perform(put("/api/visits/7/confirmation"))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("R1.5 rejects a caller who is not an owner administrator")
    @WithMockUser(roles = "VET_ADMIN")
    void requiresOwnerAdministrator() {
        assertThrows(AccessDeniedException.class, () -> controller.confirmVisit(7));
    }
}
