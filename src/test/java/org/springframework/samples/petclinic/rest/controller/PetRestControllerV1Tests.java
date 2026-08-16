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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.capabilities.pets.Requirement.Rn;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.PetRestControllerV1;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.pets.Requirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirement tests for {@link PetRestControllerV1}.
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class PetRestControllerV1Tests {

    @Autowired
    private PetRestControllerV1 petRestControllerV1;

    @Autowired
    private PetMapper petMapper;

    @MockitoBean
    private ClinicService clinicService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private List<PetDto> pets;

    @BeforeEach
    void initPets() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(petRestControllerV1)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
        pets = new ArrayList<>();

        OwnerDto owner = new OwnerDto();
        owner.id(1).firstName("Eduardo")
            .lastName("Rodriquez")
            .address("2693 Commerce St.")
            .city("McFarland")
            .telephone("6085558763");

        PetTypeDto petType = new PetTypeDto();
        petType.id(2)
            .name("dog");

        PetDto pet = new PetDto();
        pets.add(pet.id(3)
            .name("Rosy")
            .birthDate(LocalDate.now())
            .type(petType));

        pet = new PetDto();
        pets.add(pet.id(4)
            .name("Jewel")
            .birthDate(LocalDate.now())
            .type(petType));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listPetCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void listPets(Rn requirement, boolean hasPets, int expectedStatus) throws Exception {
        given(this.clinicService.findAllPets()).willReturn(hasPets ? petMapper.toPets(pets) : List.of());

        ResultActions result = this.mockMvc.perform(get("/api/pets").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (hasPets) {
            result.andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("Rosy"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].name").value("Jewel"));
        }
    }

    static Stream<Arguments> listPetCases() {
        return Stream.of(Arguments.of(R1_1, true, 200), Arguments.of(R1_2, false, 404));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getPetCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void getPet(Rn requirement, int petId, boolean found, int expectedStatus) throws Exception {
        given(this.clinicService.findPetById(petId)).willReturn(found ? petMapper.toPet(pets.get(0)) : null);

        ResultActions result = this.mockMvc.perform(get("/api/pets/{petId}", petId)
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found) {
            result.andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Rosy"));
        }
    }

    static Stream<Arguments> getPetCases() {
        return Stream.of(Arguments.of(R2_1, 3, true, 200), Arguments.of(R2_2, 999, false, 404));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updatePetCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void updatePet(Rn requirement, int petId, boolean found, String name, int expectedStatus,
                   boolean expectSave) throws Exception {
        given(this.clinicService.findPetById(petId)).willReturn(found ? petMapper.toPet(pets.get(0)) : null);

        PetDto newPet = pets.get(0);
        newPet.id(petId).name(name);
        String body = objectMapper.writeValueAsString(newPet);

        ResultActions result = this.mockMvc.perform(put("/api/pets/{petId}", petId)
            .content(body).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectSave) {
            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(this.clinicService).savePet(petCaptor.capture());
            assertThat(petCaptor.getValue().getId()).isEqualTo(petId);
            assertThat(petCaptor.getValue().getName()).isEqualTo(name);
        } else {
            verify(this.clinicService, never()).savePet(any(Pet.class));
        }
    }

    static Stream<Arguments> updatePetCases() {
        return Stream.of(
            Arguments.of(R3_1, 3, true, "Rosy I", 204, true),
            Arguments.of(R3_2, 999, false, "Rosy", 404, false),
            Arguments.of(R3_3, 3, true, null, 400, false));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deletePetCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void deletePet(Rn requirement, int petId, boolean found, int expectedStatus) throws Exception {
        given(this.clinicService.findPetById(petId)).willReturn(found ? petMapper.toPet(pets.get(0)) : null);

        ResultActions result = this.mockMvc.perform(delete("/api/pets/{petId}", petId)
            .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found) {
            verify(this.clinicService).deletePet(any(Pet.class));
        } else {
            verify(this.clinicService, never()).deletePet(any(Pet.class));
        }
    }

    static Stream<Arguments> deletePetCases() {
        return Stream.of(Arguments.of(R4_1, 3, true, 204), Arguments.of(R4_2, 999, false, 404));
    }

}
