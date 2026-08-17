/*
 * Copyright 2026 the original author or authors.
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.OwnerMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.capabilities.owners.OwnersRequirement;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.OwnerRestControllerV1;
import org.springframework.samples.petclinic.rest.controller.v2.OwnerRestControllerV2;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.VisitCreateFieldsDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.owners.OwnersRequirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirement tests for the {@code owners} capability, realized through
 * {@link OwnerRestControllerV1} and {@link OwnerRestControllerV2}.
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class OwnerRestControllerRequirementTests {

    @Autowired
    private OwnerRestControllerV1 ownerRestControllerV1;

    @Autowired
    private OwnerRestControllerV2 ownerRestControllerV2;

    @Autowired
    private OwnerMapper ownerMapper;

    @MockitoBean
    private ClinicService clinicService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private List<OwnerDto> owners;

    @BeforeEach
    void initOwners() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(ownerRestControllerV1, ownerRestControllerV2)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        owners = new ArrayList<>();
        owners.add(new OwnerDto().id(1).firstName("George").lastName("Franklin")
            .address("110 W. Liberty St.").city("Madison").telephone("6085551023"));
        owners.add(new OwnerDto().id(2).firstName("Betty").lastName("Davis")
            .address("638 Cardinal Ave.").city("Sun Prairie").telephone("6085551749"));
        owners.add(new OwnerDto().id(3).firstName("Eduardo").lastName("Rodriquez")
            .address("2693 Commerce St.").city("McFarland").telephone("6085558763"));
        owners.add(new OwnerDto().id(4).firstName("Harold").lastName("Davis")
            .address("563 Friendly St.").city("Windsor").telephone("6085553198"));
    }

    private String ownerJson(OwnerFieldsDto dto) throws Exception {
        return objectMapper.writeValueAsString(dto);
    }

    private String json(Object dto) throws Exception {
        return objectMapper.writeValueAsString(dto);
    }

    private static OwnerFieldsDto validOwnerFields() {
        return new OwnerFieldsDto()
            .firstName("George").lastName("Franklin")
            .address("110 W. Liberty St.").city("Madison").telephone("6085551023");
    }

    private Owner ownerWithPet() {
        VisitDto visit = new VisitDto().id(1).date(LocalDate.now()).description("checkup");
        PetDto pet = new PetDto().id(1).name("Rosy").birthDate(LocalDate.now())
            .type(new PetTypeDto().id(2).name("dog")).addVisitsItem(visit);
        OwnerDto owner = owners.get(0).addPetsItem(pet);
        return ownerMapper.toOwner(owner);
    }

    private static PetFieldsDto validPetFields() {
        return new PetFieldsDto().name("Rosy").birthDate(LocalDate.now().minusYears(2))
            .type(new PetTypeDto().id(2).name("dog"));
    }

    private static VisitCreateFieldsDto validVisitFields() {
        return new VisitCreateFieldsDto().date(LocalDate.now()).description("checkup");
    }

    private static Pet existingPet(int petId) {
        Pet pet = new Pet();
        pet.setId(petId);
        pet.setName("Rosy");
        pet.setBirthDate(LocalDate.now().minusYears(2));
        PetType type = new PetType();
        type.setId(2);
        type.setName("dog");
        pet.setType(type);
        return pet;
    }

    // ---------------------------------------------------------------- R1
    @ParameterizedTest(name = "{0}")
    @MethodSource("createOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void createOwner(OwnersRequirement.Rn requirement, OwnerFieldsDto body, boolean valid, int expectedStatus)
        throws Exception {
        if (valid) {
            willAnswer(invocation -> {
                Owner owner = invocation.getArgument(0);
                owner.setId(999);
                return null;
            }).given(this.clinicService).saveOwner(any(Owner.class));
        }

        ResultActions result = this.mockMvc.perform(post("/api/owners")
            .content(ownerJson(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));

        if (valid) {
            ArgumentCaptor<Owner> saved = ArgumentCaptor.forClass(Owner.class);
            verify(this.clinicService).saveOwner(saved.capture());
            result.andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.firstName").value("George"))
                .andExpect(header().string("Location", "/api/owners/999"));
        }
        else {
            verify(this.clinicService, never()).saveOwner(any(Owner.class));
        }
    }

    static Stream<Arguments> createOwnerCases() {
        return Stream.of(
            Arguments.of(R1_1, validOwnerFields(), true, 201),
            Arguments.of(R1_2, validOwnerFields().firstName(null), false, 400),
            Arguments.of(R1_2, validOwnerFields().lastName(null), false, 400),
            Arguments.of(R1_2, validOwnerFields().address(null), false, 400),
            Arguments.of(R1_2, validOwnerFields().city(null), false, 400),
            Arguments.of(R1_2, validOwnerFields().telephone(null), false, 400),
            Arguments.of(R1_2, validOwnerFields().firstName(""), false, 400),
            Arguments.of(R1_2, validOwnerFields().lastName(""), false, 400),
            Arguments.of(R1_2, validOwnerFields().address(""), false, 400),
            Arguments.of(R1_2, validOwnerFields().city(""), false, 400),
            Arguments.of(R1_2, validOwnerFields().telephone(""), false, 400),
            Arguments.of(R1_2, validOwnerFields().firstName("A".repeat(31)), false, 400),
            Arguments.of(R1_2, validOwnerFields().lastName("A".repeat(31)), false, 400),
            Arguments.of(R1_2, validOwnerFields().address("A".repeat(256)), false, 400),
            Arguments.of(R1_2, validOwnerFields().city("A".repeat(81)), false, 400),
            Arguments.of(R1_2, validOwnerFields().telephone("1".repeat(21)), false, 400),
            Arguments.of(R1_3, validOwnerFields().firstName("George1"), false, 400),
            Arguments.of(R1_3, validOwnerFields().lastName("Franklin1"), false, 400),
            Arguments.of(R1_4, validOwnerFields().telephone("60855abc"), false, 400)
        );
    }

    // ---------------------------------------------------------------- R2
    @ParameterizedTest(name = "{0}")
    @MethodSource("listOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void listOwners(OwnersRequirement.Rn requirement, String lastName, boolean empty, int expectedStatus) throws Exception {
        List<OwnerDto> resultOwners = empty ? List.of()
            : lastName == null ? owners : List.of(owners.get(1), owners.get(3));
        if (lastName != null) {
            given(this.clinicService.findOwnerByLastName(lastName))
                .willReturn(ownerMapper.toOwners(resultOwners));
        }
        else {
            given(this.clinicService.findAllOwners())
                .willReturn(ownerMapper.toOwners(resultOwners));
        }

        MockHttpServletRequestBuilder request = lastName != null
            ? get("/api/owners").param("lastName", lastName)
            : get("/api/owners");
        ResultActions result = this.mockMvc.perform(request.accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (!empty) {
            result.andExpect(jsonPath("$.length()").value(lastName == null ? 4 : 2))
                .andExpect(jsonPath("$.[0].id").value(lastName == null ? 1 : 2))
                .andExpect(jsonPath("$.[0].firstName").value(lastName == null ? "George" : "Betty"));
            if (lastName != null) {
                result.andExpect(jsonPath("$.[0].lastName").value("Davis"))
                    .andExpect(jsonPath("$.[1].lastName").value("Davis"));
            }
        }
    }

    static Stream<Arguments> listOwnerCases() {
        return Stream.of(
            Arguments.of(R2_1, null, false, 200),
            Arguments.of(R2_2, "Dav", false, 200),
            Arguments.of(R2_3, "Unknown", true, 404),
            Arguments.of(R2_3, null, true, 404)
        );
    }

    // ---------------------------------------------------------------- R3
    @ParameterizedTest(name = "{0}")
    @MethodSource("listOwnerPageCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void listOwnersPage(OwnersRequirement.Rn requirement, String lastName, Integer page, Integer size,
                        boolean empty, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            int expectedPage = page == null ? 0 : page;
            int expectedSize = size == null ? 20 : size;
            PageRequest pageRequest = PageRequest.of(expectedPage, expectedSize, Sort.by("id"));
            List<OwnerDto> matchingOwners = empty ? List.of()
                : lastName == null ? owners : List.of(owners.get(1), owners.get(3));
            int contentSize = Math.min(expectedSize, matchingOwners.size());
            var pageOwners = ownerMapper.toOwners(matchingOwners.subList(0, contentSize)).stream().toList();
            given(this.clinicService.findOwners(eq(lastName), eq(pageRequest)))
                .willReturn(new PageImpl<>(pageOwners, pageRequest, matchingOwners.size()));
        }

        MockHttpServletRequestBuilder request = get("/api/v2/owners").accept(MediaType.APPLICATION_JSON);
        if (lastName != null) {
            request.param("lastName", lastName);
        }
        if (page != null) {
            request.param("page", String.valueOf(page));
        }
        if (size != null) {
            request.param("size", String.valueOf(size));
        }
        ResultActions result = this.mockMvc.perform(request);

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 200) {
            int expectedPage = page == null ? 0 : page;
            int expectedSize = size == null ? 20 : size;
            int expectedTotal = empty ? 0 : lastName == null ? 4 : 2;
            int expectedPages = expectedTotal == 0 ? 0 : (int) Math.ceil((double) expectedTotal / expectedSize);
            result.andExpect(jsonPath("$.page").value(expectedPage))
                .andExpect(jsonPath("$.size").value(expectedSize))
                .andExpect(jsonPath("$.totalElements").value(expectedTotal))
                .andExpect(jsonPath("$.totalPages").value(expectedPages));
            if (empty) {
                result.andExpect(jsonPath("$.content").isEmpty());
            }
            else {
                result.andExpect(jsonPath("$.content[0].id").value(lastName == null ? 1 : 2));
            }
        }
    }

    static Stream<Arguments> listOwnerPageCases() {
        return Stream.of(
            Arguments.of(R3_1, null, 0, 2, false, 200),
            Arguments.of(R3_1, "Dav", 0, 2, false, 200),
            Arguments.of(R3_1, null, null, null, false, 200),
            Arguments.of(R3_2, null, -1, 2, false, 400),
            Arguments.of(R3_2, null, 0, 0, false, 400),
            Arguments.of(R3_2, null, 0, 101, false, 400),
            Arguments.of(R3_3, "Unknown", 0, 20, true, 200)
        );
    }

    // ---------------------------------------------------------------- R4
    @ParameterizedTest(name = "{0}")
    @MethodSource("getOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void getOwner(OwnersRequirement.Rn requirement, int ownerId, boolean found, int expectedStatus) throws Exception {
        given(this.clinicService.findOwnerById(ownerId)).willReturn(found ? ownerWithPet() : null);

        ResultActions result = this.mockMvc.perform(get("/api/owners/{ownerId}", ownerId)
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found) {
            result.andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("George"))
                .andExpect(jsonPath("$.pets[0].name").value("Rosy"))
                .andExpect(jsonPath("$.pets[0].visits[0].description").value("checkup"));
        }
    }

    static Stream<Arguments> getOwnerCases() {
        return Stream.of(
            Arguments.of(R4_1, 1, true, 200),
            Arguments.of(R4_2, 999, false, 404),
            Arguments.of(R4_3, -1, false, 400)
        );
    }

    // ---------------------------------------------------------------- R5
    @ParameterizedTest(name = "{0}")
    @MethodSource("updateOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void updateOwner(OwnersRequirement.Rn requirement, int ownerId, boolean found, OwnerFieldsDto body,
                     int expectedStatus) throws Exception {
        given(this.clinicService.findOwnerById(ownerId)).willReturn(found ? ownerMapper.toOwner(owners.get(0)) : null);

        ResultActions result = this.mockMvc.perform(put("/api/owners/{ownerId}", ownerId)
            .content(ownerJson(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found && expectedStatus == 204) {
            ArgumentCaptor<Owner> updated = ArgumentCaptor.forClass(Owner.class);
            verify(this.clinicService).saveOwner(updated.capture());
            assertThat(updated.getValue().getId()).isEqualTo(ownerId);
            assertThat(updated.getValue().getFirstName()).isEqualTo(body.getFirstName());
        }
        else {
            verify(this.clinicService, never()).saveOwner(any(Owner.class));
        }
    }

    static Stream<Arguments> updateOwnerCases() {
        return Stream.of(
            Arguments.of(R5_1, 1, true, validOwnerFields().firstName("GeorgeI"), 204),
            Arguments.of(R5_2, 999, false, validOwnerFields(), 404),
            Arguments.of(R5_3, 1, true, validOwnerFields().firstName(null), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().firstName(""), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().firstName("A".repeat(31)), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().lastName(null), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().lastName(""), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().lastName("A".repeat(31)), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().address(null), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().address(""), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().address("A".repeat(256)), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().city(null), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().city(""), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().city("A".repeat(81)), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().telephone(null), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().telephone(""), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().telephone("1".repeat(21)), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().firstName("George1"), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().lastName("Franklin1"), 400),
            Arguments.of(R5_3, 1, true, validOwnerFields().telephone("60855abc"), 400),
            Arguments.of(R5_4, -1, false, validOwnerFields(), 400)
        );
    }

    // ---------------------------------------------------------------- R6
    @ParameterizedTest(name = "{0}")
    @MethodSource("deleteOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void deleteOwner(OwnersRequirement.Rn requirement, int ownerId, boolean found, int expectedStatus) throws Exception {
        given(this.clinicService.findOwnerById(ownerId)).willReturn(found ? ownerMapper.toOwner(owners.get(0)) : null);

        ResultActions result = this.mockMvc.perform(delete("/api/owners/{ownerId}", ownerId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found) {
            verify(this.clinicService).deleteOwner(any(Owner.class));
        }
        else {
            verify(this.clinicService, never()).deleteOwner(any(Owner.class));
        }
    }

    static Stream<Arguments> deleteOwnerCases() {
        return Stream.of(
            Arguments.of(R6_1, 1, true, 204),
            Arguments.of(R6_2, 999, false, 404),
            Arguments.of(R6_3, -1, false, 400)
        );
    }

    // ---------------------------------------------------------------- R7
    @ParameterizedTest(name = "{0}")
    @MethodSource("addPetToOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void addPetToOwner(OwnersRequirement.Rn requirement, int ownerId, boolean ownerExists,
                       PetFieldsDto body, int expectedStatus) throws Exception {
        given(this.clinicService.findOwnerById(ownerId))
            .willReturn(ownerExists ? ownerMapper.toOwner(owners.get(0)) : null);
        if (expectedStatus == 201) {
            willAnswer(invocation -> {
                Pet pet = invocation.getArgument(0);
                pet.setId(999);
                return null;
            }).given(this.clinicService).savePet(any(Pet.class));
        }

        ResultActions result = this.mockMvc.perform(post("/api/owners/{ownerId}/pets", ownerId)
            .content(json(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 201) {
            ArgumentCaptor<Pet> saved = ArgumentCaptor.forClass(Pet.class);
            verify(this.clinicService).savePet(saved.capture());
            assertThat(saved.getValue().getOwner().getId()).isEqualTo(ownerId);
            assertThat(saved.getValue().getType().getId()).isEqualTo(2);
            result.andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(header().string("Location", "/api/pets/999"));
        }
        else {
            verify(this.clinicService, never()).savePet(any(Pet.class));
        }
    }

    static Stream<Arguments> addPetToOwnerCases() {
        return Stream.of(
            Arguments.of(R7_1, 1, true, validPetFields(), 201),
            Arguments.of(R7_2, 999, false, validPetFields(), 404),
            Arguments.of(R7_3, 1, true, validPetFields().name(null), 400),
            Arguments.of(R7_3, 1, true, validPetFields().name("A".repeat(31)), 400),
            Arguments.of(R7_3, 1, true, validPetFields().birthDate(null), 400),
            Arguments.of(R7_3, 1, true, validPetFields().birthDate(LocalDate.now().plusDays(1)), 400),
            Arguments.of(R7_3, 1, true, validPetFields().birthDate(LocalDate.now().minusYears(50).minusDays(1)), 400),
            Arguments.of(R7_3, 1, true, validPetFields().type(null), 400),
            Arguments.of(R7_3, 1, true, validPetFields().type(new PetTypeDto().name("dog")), 400),
            Arguments.of(R7_3, 1, true, validPetFields().type(new PetTypeDto().id(-1).name("dog")), 400),
            Arguments.of(R7_3, 1, true, validPetFields().type(new PetTypeDto().id(2)), 400),
            Arguments.of(R7_3, 1, true, validPetFields().type(new PetTypeDto().id(2).name("")), 400),
            Arguments.of(R7_3, 1, true,
                validPetFields().type(new PetTypeDto().id(2).name("A".repeat(81))), 400),
            Arguments.of(R7_4, -1, false, validPetFields(), 400)
        );
    }

    // ---------------------------------------------------------------- R8
    @ParameterizedTest(name = "{0}")
    @MethodSource("getOwnerPetCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void getOwnerPet(OwnersRequirement.Rn requirement, int ownerId, int petId,
                     boolean ownerExists, boolean petExists, int expectedStatus) throws Exception {
        Owner owner = petExists ? ownerWithPet() : ownerMapper.toOwner(owners.get(0));
        given(this.clinicService.findOwnerById(ownerId)).willReturn(ownerExists ? owner : null);

        ResultActions result = this.mockMvc.perform(get("/api/owners/{ownerId}/pets/{petId}", ownerId, petId)
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 200) {
            result.andExpect(jsonPath("$.id").value(petId))
                .andExpect(jsonPath("$.name").value("Rosy"))
                .andExpect(jsonPath("$.visits[0].description").value("checkup"));
        }
    }

    static Stream<Arguments> getOwnerPetCases() {
        return Stream.of(
            Arguments.of(R8_1, 1, 1, true, true, 200),
            Arguments.of(R8_2, 999, 1, false, false, 404),
            Arguments.of(R8_2, 1, 999, true, false, 404),
            Arguments.of(R8_3, -1, 1, false, false, 400),
            Arguments.of(R8_3, 1, -1, false, false, 400)
        );
    }

    // ---------------------------------------------------------------- R9
    @ParameterizedTest(name = "{0}")
    @MethodSource("updateOwnerPetCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void updateOwnerPet(OwnersRequirement.Rn requirement, int ownerId, int petId,
                        boolean ownerExists, boolean petExists, PetFieldsDto body, int expectedStatus) throws Exception {
        given(this.clinicService.findOwnerById(ownerId))
            .willReturn(ownerExists ? ownerMapper.toOwner(owners.get(0)) : null);
        given(this.clinicService.findPetById(petId)).willReturn(petExists ? existingPet(petId) : null);

        ResultActions result = this.mockMvc.perform(put("/api/owners/{ownerId}/pets/{petId}", ownerId, petId)
            .content(json(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 204) {
            ArgumentCaptor<Pet> updated = ArgumentCaptor.forClass(Pet.class);
            verify(this.clinicService).savePet(updated.capture());
            assertThat(updated.getValue().getId()).isEqualTo(petId);
            assertThat(updated.getValue().getName()).isEqualTo(body.getName());
            assertThat(updated.getValue().getBirthDate()).isEqualTo(body.getBirthDate());
        }
        else {
            verify(this.clinicService, never()).savePet(any(Pet.class));
        }
    }

    static Stream<Arguments> updateOwnerPetCases() {
        return Stream.of(
            Arguments.of(R9_1, 1, 1, true, true, validPetFields().name("Rex"), 204),
            Arguments.of(R9_2, 999, 1, false, true, validPetFields(), 404),
            Arguments.of(R9_2, 1, 999, true, false, validPetFields(), 404),
            Arguments.of(R9_3, 1, 1, true, true, validPetFields().name(null), 400),
            Arguments.of(R9_3, 1, 1, true, true, validPetFields().name("A".repeat(31)), 400),
            Arguments.of(R9_3, 1, 1, true, true, validPetFields().birthDate(null), 400),
            Arguments.of(R9_3, 1, 1, true, true, validPetFields().birthDate(LocalDate.now().plusDays(1)), 400),
            Arguments.of(R9_3, 1, 1, true, true,
                validPetFields().birthDate(LocalDate.now().minusYears(50).minusDays(1)), 400),
            Arguments.of(R9_3, 1, 1, true, true, validPetFields().type(null), 400),
            Arguments.of(R9_3, 1, 1, true, true,
                validPetFields().type(new PetTypeDto().name("dog")), 400),
            Arguments.of(R9_3, 1, 1, true, true,
                validPetFields().type(new PetTypeDto().id(-1).name("dog")), 400),
            Arguments.of(R9_3, 1, 1, true, true,
                validPetFields().type(new PetTypeDto().id(2)), 400),
            Arguments.of(R9_3, 1, 1, true, true,
                validPetFields().type(new PetTypeDto().id(2).name("")), 400),
            Arguments.of(R9_3, 1, 1, true, true,
                validPetFields().type(new PetTypeDto().id(2).name("A".repeat(81))), 400),
            Arguments.of(R9_4, -1, 1, false, false, validPetFields(), 400),
            Arguments.of(R9_4, 1, -1, false, false, validPetFields(), 400)
        );
    }

    // ---------------------------------------------------------------- R10
    @ParameterizedTest(name = "{0}")
    @MethodSource("addVisitToOwnerPetCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void addVisitToOwnerPet(OwnersRequirement.Rn requirement, int ownerId, int petId,
                            VisitCreateFieldsDto body, boolean persistenceRejects, int expectedStatus) throws Exception {
        if (expectedStatus == 201) {
            willAnswer(invocation -> {
                Visit visit = invocation.getArgument(0);
                visit.setId(999);
                return null;
            }).given(this.clinicService).saveVisit(any(Visit.class));
        }
        else if (persistenceRejects) {
            doThrow(new DataIntegrityViolationException("constraint fk_visit_pet"))
                .when(this.clinicService).saveVisit(any(Visit.class));
        }

        ResultActions result = this.mockMvc.perform(post("/api/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
            .content(json(body))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 201) {
            ArgumentCaptor<Visit> saved = ArgumentCaptor.forClass(Visit.class);
            verify(this.clinicService).saveVisit(saved.capture());
            assertThat(saved.getValue().getPet().getId()).isEqualTo(petId);
            assertThat(saved.getValue().getDate()).isEqualTo(body.getDate() == null ? LocalDate.now() : body.getDate());
            result.andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.petId").value(petId))
                .andExpect(header().string("Location", "/api/visits/999"));
        }
        else if (expectedStatus == 400) {
            verify(this.clinicService, never()).saveVisit(any(Visit.class));
        }
        else {
            result.andExpect(jsonPath("$.detail")
                .value("The requested resource could not be processed due to a data constraint violation"));
        }
    }

    static Stream<Arguments> addVisitToOwnerPetCases() {
        return Stream.of(
            Arguments.of(R10_1, 1, 1, validVisitFields(), false, 201),
            Arguments.of(R10_1, 1, 1, validVisitFields().date(LocalDate.now().plusDays(1)), false, 201),
            Arguments.of(R10_2, 1, 1, validVisitFields().date(null), false, 201),
            Arguments.of(R10_3, 1, 1, validVisitFields().description(null), false, 400),
            Arguments.of(R10_3, 1, 1, validVisitFields().description(""), false, 400),
            Arguments.of(R10_3, 1, 1, validVisitFields().description("A".repeat(256)), false, 400),
            Arguments.of(R10_3, 1, 1, validVisitFields().date(LocalDate.now().minusDays(1)), false, 400),
            Arguments.of(R10_4, -1, 1, validVisitFields(), false, 400),
            Arguments.of(R10_4, 1, -1, validVisitFields(), false, 400),
            Arguments.of(R10_5, 1, 999, validVisitFields(), true, 404)
        );
    }
}
