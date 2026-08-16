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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.capabilities.vets.Requirement.Rn;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.VetRestControllerV1;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.vets.Requirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirement tests for {@link VetRestControllerV1}.
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class VetRestControllerV1Tests {

    @Autowired
    private VetRestControllerV1 vetRestController;

    @MockitoBean
    private ClinicService clinicService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private List<Vet> vets;

    @BeforeEach
    void initVets() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(vetRestController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        this.vets = new ArrayList<>();
        this.vets.add(vet(1, "James", "Carter"));
        this.vets.add(vet(2, "Helen", "Leary"));
        this.vets.add(vet(3, "Linda", "Douglas"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listVetCases")
    @WithMockUser(roles = "VET_ADMIN")
    void listVets(Rn requirement, boolean hasVets, int expectedStatus) throws Exception {
        given(this.clinicService.findAllVets()).willReturn(hasVets ? vets : List.of());

        ResultActions result = this.mockMvc.perform(get("/api/vets").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (hasVets) {
            result.andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[2].lastName").value("Douglas"));
        }
    }

    static Stream<Arguments> listVetCases() {
        return Stream.of(Arguments.of(R1_1, true, 200), Arguments.of(R1_2, false, 404));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getVetCases")
    @WithMockUser(roles = "VET_ADMIN")
    void getVet(Rn requirement, int vetId, boolean found, int expectedStatus) throws Exception {
        Vet vet = vets.getFirst();
        Specialty specialty = specialty("dentistry");
        vet.addSpecialty(specialty);
        given(this.clinicService.findVetById(vetId)).willReturn(found ? vet : null);

        ResultActions result = this.mockMvc.perform(get("/api/vets/{vetId}", vetId)
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found) {
            result.andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("James"))
                .andExpect(jsonPath("$.specialties[0].name").value("dentistry"));
        }
    }

    static Stream<Arguments> getVetCases() {
        return Stream.of(Arguments.of(R2_1, 1, true, 200), Arguments.of(R2_2, 999, false, 404));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("addVetCases")
    @WithMockUser(roles = "VET_ADMIN")
    void addVet(Rn requirement, Map<String, Object> body, boolean resolveSpecialties, int expectedStatus) throws Exception {
        Specialty existingSpecialty = specialty("dentistry");
        if (resolveSpecialties) {
            given(this.clinicService.findSpecialtiesByNameIn(java.util.Set.of("dentistry")))
                .willReturn(List.of(existingSpecialty));
        }
        willAnswer(invocation -> {
            invocation.<Vet>getArgument(0).setId(999);
            return null;
        }).given(this.clinicService).saveVet(any(Vet.class));

        ResultActions result = this.mockMvc.perform(post("/api/vets")
            .content(objectMapper.writeValueAsString(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 201) {
            ArgumentCaptor<Vet> savedVet = ArgumentCaptor.forClass(Vet.class);
            verify(this.clinicService).saveVet(savedVet.capture());
            assertThat(savedVet.getValue().getId()).isEqualTo(999);
            if (resolveSpecialties) {
                assertThat(savedVet.getValue().getSpecialties()).containsExactly(existingSpecialty);
            }
            result.andExpect(header().string("Location", "/api/vets/999"))
                .andExpect(jsonPath("$.id").value(999));
        }
        else {
            verify(this.clinicService, never()).saveVet(any(Vet.class));
        }
    }

    static Stream<Arguments> addVetCases() {
        return Stream.of(
            Arguments.of(R3_1, vetBody("James", "Carter", List.of()), false, 201),
            Arguments.of(R3_2, vetBody(null, "Carter", List.of()), false, 400),
            Arguments.of(R3_2, vetBody("", "Carter", List.of()), false, 400),
            Arguments.of(R3_2, vetBody("James1", "Carter", List.of()), false, 400),
            Arguments.of(R3_2, vetBody("J".repeat(31), "Carter", List.of()), false, 400),
            Arguments.of(R3_2, vetBody("James", null, List.of()), false, 400),
            Arguments.of(R3_2, vetBody("James", "", List.of()), false, 400),
            Arguments.of(R3_2, vetBody("James", "Carter1", List.of()), false, 400),
            Arguments.of(R3_2, vetBody("James", "C".repeat(31), List.of()), false, 400),
            Arguments.of(R3_2, vetBody("James", "Carter", null), false, 400),
            Arguments.of(R3_3, vetBody("James", "Carter", List.of(specialtyBody("dentistry"))), true, 201)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateVetCases")
    @WithMockUser(roles = "VET_ADMIN")
    void updateVet(Rn requirement, int vetId, Map<String, Object> body, boolean found, int expectedStatus) throws Exception {
        Vet existingVet = vets.getFirst();
        given(this.clinicService.findVetById(vetId)).willReturn(found ? existingVet : null);

        ResultActions result = this.mockMvc.perform(put("/api/vets/{vetId}", vetId)
            .content(objectMapper.writeValueAsString(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 204) {
            verify(this.clinicService).saveVet(existingVet);
            assertThat(existingVet.getId()).isEqualTo(1);
            assertThat(existingVet.getFirstName()).isEqualTo("Jane");
            assertThat(existingVet.getLastName()).isEqualTo("Doe");
        }
        else {
            verify(this.clinicService, never()).saveVet(any(Vet.class));
        }
    }

    static Stream<Arguments> updateVetCases() {
        return Stream.of(
            Arguments.of(R4_1, 1, vetBody("Jane", "Doe", List.of()), true, 204),
            Arguments.of(R4_2, 999, vetBody("Jane", "Doe", List.of()), false, 404),
            Arguments.of(R4_3, 1, vetBody(null, "Doe", List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("", "Doe", List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("Jane1", "Doe", List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("J".repeat(31), "Doe", List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("Jane", null, List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("Jane", "", List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("Jane", "Doe1", List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("Jane", "D".repeat(31), List.of()), true, 400),
            Arguments.of(R4_3, 1, vetBody("Jane", "Doe", null), true, 400)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deleteVetCases")
    @WithMockUser(roles = "VET_ADMIN")
    void deleteVet(Rn requirement, int vetId, boolean found, int expectedStatus) throws Exception {
        Vet vet = vets.getFirst();
        given(this.clinicService.findVetById(vetId)).willReturn(found ? vet : null);

        this.mockMvc.perform(delete("/api/vets/{vetId}", vetId).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));

        if (found) {
            verify(this.clinicService).deleteVet(vet);
        }
        else {
            verify(this.clinicService, never()).deleteVet(any(Vet.class));
        }
    }

    static Stream<Arguments> deleteVetCases() {
        return Stream.of(Arguments.of(R5_1, 1, true, 204), Arguments.of(R5_2, 999, false, 404));
    }

    private static Vet vet(int id, String firstName, String lastName) {
        Vet vet = new Vet();
        vet.setId(id);
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        return vet;
    }

    private static Specialty specialty(String name) {
        Specialty specialty = new Specialty();
        specialty.setName(name);
        return specialty;
    }

    private static Map<String, Object> vetBody(String firstName, String lastName, List<Map<String, Object>> specialties) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (firstName != null) {
            body.put("firstName", firstName);
        }
        if (lastName != null) {
            body.put("lastName", lastName);
        }
        if (specialties != null) {
            body.put("specialties", specialties);
        }
        return body;
    }

    private static Map<String, Object> specialtyBody(String name) {
        return Map.of("name", name);
    }
}
