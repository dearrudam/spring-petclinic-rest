/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.capabilities.visit.VisitRequirement;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.VisitRestControllerV1;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.visit.VisitRequirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirement tests for {@link VisitRestControllerV1}.
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class VisitRestControllerV1Tests {

    @Autowired
    private VisitRestControllerV1 visitRestController;

    @MockitoBean
    private ClinicService clinicService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private List<Visit> visits;

    @BeforeEach
    void initVisits() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(visitRestController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("Eduardo");
        owner.setLastName("Rodriquez");
        owner.setAddress("2693 Commerce St.");
        owner.setCity("McFarland");
        owner.setTelephone("6085558763");

        PetType petType = new PetType();
        petType.setId(2);
        petType.setName("dog");

        Pet pet = new Pet();
        pet.setId(8);
        pet.setName("Rosy");
        pet.setBirthDate(LocalDate.now());
        pet.setOwner(owner);
        pet.setType(petType);

        this.visits = new ArrayList<>();
        this.visits.add(visit(2, pet, LocalDate.now(), "rabies shot"));
        this.visits.add(visit(3, pet, LocalDate.now(), "neutered"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listVisitCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void listVisits(VisitRequirement.Rn requirement, boolean hasVisits, int expectedStatus) throws Exception {
        given(this.clinicService.findAllVisits()).willReturn(hasVisits ? visits : List.of());

        ResultActions result = this.mockMvc.perform(get("/api/visits").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (hasVisits) {
            result.andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3));
        }
    }

    static Stream<Arguments> listVisitCases() {
        return Stream.of(Arguments.of(R1_1, true, 200), Arguments.of(R1_2, false, 404));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getVisitCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void getVisit(VisitRequirement.Rn requirement, int visitId, boolean found, int expectedStatus) throws Exception {
        given(this.clinicService.findVisitById(visitId)).willReturn(found ? visits.getFirst() : null);

        ResultActions result = this.mockMvc.perform(get("/api/visits/{visitId}", visitId)
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found) {
            result.andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.description").value("rabies shot"))
                .andExpect(jsonPath("$.petId").value(8));
        }
    }

    static Stream<Arguments> getVisitCases() {
        return Stream.of(
            Arguments.of(R2_1, 2, true, 200),
            Arguments.of(R2_2, 999, false, 404),
            Arguments.of(R2_3, -1, false, 400)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("registerVisitCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void registerVisit(VisitRequirement.Rn requirement, LocalDate date, String description,
                       boolean includeDescription, int expectedStatus) throws Exception {
        willAnswer(invocation -> {
            Visit visit = invocation.getArgument(0);
            visit.setId(999);
            return null;
        }).given(this.clinicService).saveVisit(any(Visit.class));

        Map<String, Object> body = new LinkedHashMap<>();
        if (date != null) {
            body.put("date", date);
        }
        if (includeDescription) {
            body.put("description", description);
        }
        body.put("petId", requirement == R3_6 ? -1 : 8);

        ResultActions result = this.mockMvc.perform(post("/api/visits")
            .content(objectMapper.writeValueAsString(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 201) {
            LocalDate expectedDate = date == null ? LocalDate.now() : date;
            ArgumentCaptor<Visit> savedVisit = ArgumentCaptor.forClass(Visit.class);
            verify(this.clinicService).saveVisit(savedVisit.capture());
            assertThat(savedVisit.getValue().getId()).isEqualTo(999);
            assertThat(savedVisit.getValue().getPet().getId()).isEqualTo(8);
            assertThat(savedVisit.getValue().getDescription()).isEqualTo(description);
            assertThat(savedVisit.getValue().getDate()).isEqualTo(expectedDate);
            result.andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.petId").value(8))
                .andExpect(jsonPath("$.date").value(expectedDate.toString()));
        }
        else {
            verify(this.clinicService, never()).saveVisit(any(Visit.class));
        }
    }

    static Stream<Arguments> registerVisitCases() {
        return Stream.of(
            Arguments.of(R3_1, LocalDate.now(), "annual exam", true, 201),
            Arguments.of(R3_2, LocalDate.now(), null, false, 400),
            Arguments.of(R3_2, LocalDate.now(), "", true, 400),
            Arguments.of(R3_2, LocalDate.now(), "x".repeat(256), true, 400),
            Arguments.of(R3_3, null, "annual exam", true, 201),
            Arguments.of(R3_4, LocalDate.now(), "annual exam", true, 201),
            Arguments.of(R3_4, LocalDate.now().plusDays(1), "annual exam", true, 201),
            Arguments.of(R3_5, LocalDate.now().minusDays(1), "annual exam", true, 400),
            Arguments.of(R3_6, LocalDate.now(), "annual exam", true, 400)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateVisitCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void updateVisit(VisitRequirement.Rn requirement, int visitId, String description,
                     boolean includeDescription, int expectedStatus) throws Exception {
        Visit existingVisit = visits.getFirst();
        Pet associatedPet = existingVisit.getPet();
        given(this.clinicService.findVisitById(visitId)).willReturn(visitId == 2 ? existingVisit : null);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("date", LocalDate.now().plusDays(1));
        if (includeDescription) {
            body.put("description", description);
        }

        ResultActions result = this.mockMvc.perform(put("/api/visits/{visitId}", visitId)
            .content(objectMapper.writeValueAsString(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 204) {
            ArgumentCaptor<Visit> savedVisit = ArgumentCaptor.forClass(Visit.class);
            verify(this.clinicService).saveVisit(savedVisit.capture());
            assertThat(savedVisit.getValue().getId()).isEqualTo(2);
            assertThat(savedVisit.getValue().getPet()).isSameAs(associatedPet);
            assertThat(savedVisit.getValue().getDescription()).isEqualTo(description);
            assertThat(savedVisit.getValue().getDate()).isEqualTo(LocalDate.now().plusDays(1));
        }
        else {
            verify(this.clinicService, never()).saveVisit(any(Visit.class));
        }
    }

    static Stream<Arguments> updateVisitCases() {
        return Stream.of(
            Arguments.of(R4_1, 2, "follow-up", true, 204),
            Arguments.of(R4_2, 999, "follow-up", true, 404),
            Arguments.of(R4_3, 2, null, false, 400),
            Arguments.of(R4_3, 2, "", true, 400),
            Arguments.of(R4_3, 2, "x".repeat(256), true, 400),
            Arguments.of(R4_4, -1, "follow-up", true, 400)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deleteVisitCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void deleteVisit(VisitRequirement.Rn requirement, int visitId, boolean found, int expectedStatus) throws Exception {
        Visit visit = visits.getFirst();
        given(this.clinicService.findVisitById(visitId)).willReturn(found ? visit : null);

        this.mockMvc.perform(delete("/api/visits/{visitId}", visitId).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));

        if (found) {
            verify(this.clinicService).deleteVisit(visit);
        }
        else {
            verify(this.clinicService, never()).deleteVisit(any(Visit.class));
        }
    }

    static Stream<Arguments> deleteVisitCases() {
        return Stream.of(
            Arguments.of(R5_1, 2, true, 204),
            Arguments.of(R5_2, 999, false, 404),
            Arguments.of(R5_3, -1, false, 400)
        );
    }

    @Test
    @DisplayName("R6.1")
    @VisitRequirement(R6_1)
    @WithMockUser(roles = "OWNER_ADMIN")
    void listPetVisits() throws Exception {
        given(this.clinicService.findVisitsByPetId(8)).willReturn(visits);

        this.mockMvc.perform(get("/api/pets/8/visits").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(2))
            .andExpect(jsonPath("$[0].petId").value(8))
            .andExpect(jsonPath("$[1].id").value(3))
            .andExpect(jsonPath("$[1].petId").value(8));

        given(this.clinicService.findVisitsByPetId(8)).willReturn(List.of());
        this.mockMvc.perform(get("/api/pets/8/visits").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    private static Visit visit(int id, Pet pet, LocalDate date, String description) {
        Visit visit = new Visit();
        visit.setId(id);
        visit.setPet(pet);
        visit.setDate(date);
        visit.setDescription(description);
        return visit;
    }
}
