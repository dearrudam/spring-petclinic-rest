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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.OwnerMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.capabilities.owners.OwnersRequirement;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.OwnerRestControllerV1;
import org.springframework.samples.petclinic.rest.controller.v2.OwnerRestControllerV2;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.owners.OwnersRequirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private static OwnerFieldsDto validOwnerFields() {
        return new OwnerFieldsDto()
            .firstName("George").lastName("Franklin")
            .address("110 W. Liberty St.").city("Madison").telephone("6085551023");
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
                .andExpect(jsonPath("$.firstName").value("George"));
        }
        else {
            verify(this.clinicService, never()).saveOwner(any(Owner.class));
        }
    }

    static Stream<Arguments> createOwnerCases() {
        return Stream.of(
            Arguments.of(R1_1, validOwnerFields(), true, 201),
            Arguments.of(R1_2, validOwnerFields().firstName(null), false, 400),
            Arguments.of(R1_3, validOwnerFields().firstName("George1"), false, 400),
            Arguments.of(R1_4, validOwnerFields().telephone("60855abc"), false, 400)
        );
    }

    // ---------------------------------------------------------------- R2
    @ParameterizedTest(name = "{0}")
    @MethodSource("listOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void listOwners(OwnersRequirement.Rn requirement, boolean filter, boolean empty, int expectedStatus) throws Exception {
        List<OwnerDto> resultOwners = empty ? List.of() : owners.subList(1, 3);
        if (filter) {
            given(this.clinicService.findOwnerByLastName("Davis"))
                .willReturn(ownerMapper.toOwners(resultOwners));
        }
        else {
            given(this.clinicService.findAllOwners())
                .willReturn(ownerMapper.toOwners(resultOwners));
        }

        MockHttpServletRequestBuilder request = filter
            ? get("/api/owners").param("lastName", "Davis")
            : get("/api/owners");
        ResultActions result = this.mockMvc.perform(request.accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (!empty) {
            result.andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].firstName").value("Betty"));
        }
    }

    static Stream<Arguments> listOwnerCases() {
        return Stream.of(
            Arguments.of(R2_1, false, false, 200),
            Arguments.of(R2_2, true, false, 200),
            Arguments.of(R2_3, true, true, 404)
        );
    }

    // ---------------------------------------------------------------- R3
    @ParameterizedTest(name = "{0}")
    @MethodSource("listOwnerPageCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void listOwnersPage(OwnersRequirement.Rn requirement, int page, int size, int expectedStatus) throws Exception {
        if (expectedStatus == 200) {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));
            var pageOwners = ownerMapper.toOwners(owners.subList(0, 2)).stream().toList();
            given(this.clinicService.findOwners(eq(null), eq(pageRequest)))
                .willReturn(new PageImpl<>(pageOwners, pageRequest, owners.size()));
        }

        ResultActions result = this.mockMvc.perform(get("/api/v2/owners")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size))
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 200) {
            result.andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1));
        }
    }

    static Stream<Arguments> listOwnerPageCases() {
        return Stream.of(
            Arguments.of(R3_1, 0, 2, 200),
            Arguments.of(R3_2, -1, 2, 400),
            Arguments.of(R3_2, 0, 0, 400),
            Arguments.of(R3_2, 0, 101, 400)
        );
    }

    // ---------------------------------------------------------------- R4
    @ParameterizedTest(name = "{0}")
    @MethodSource("getOwnerCases")
    @WithMockUser(roles = "OWNER_ADMIN")
    void getOwner(OwnersRequirement.Rn requirement, int ownerId, boolean found, int expectedStatus) throws Exception {
        given(this.clinicService.findOwnerById(ownerId)).willReturn(found ? ownerMapper.toOwner(owners.get(0)) : null);

        ResultActions result = this.mockMvc.perform(get("/api/owners/{ownerId}", ownerId)
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is(expectedStatus));
        if (found) {
            result.andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("George"));
        }
    }

    static Stream<Arguments> getOwnerCases() {
        return Stream.of(
            Arguments.of(R4_1, 1, true, 200),
            Arguments.of(R4_2, 999, false, 404)
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
            verify(this.clinicService).saveOwner(any(Owner.class));
        }
        else {
            verify(this.clinicService, never()).saveOwner(any(Owner.class));
        }
    }

    static Stream<Arguments> updateOwnerCases() {
        return Stream.of(
            Arguments.of(R5_1, 1, true, validOwnerFields().firstName("GeorgeI"), 204),
            Arguments.of(R5_2, 999, false, validOwnerFields(), 404),
            Arguments.of(R5_3, 1, true, validOwnerFields().firstName(""), 400)
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
            Arguments.of(R6_2, 999, false, 404)
        );
    }
}
