package org.springframework.samples.petclinic.rest.controller;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.capabilities.pettypes.PettypesRequirement.Rn;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.PetTypeRestControllerV1;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.pettypes.PettypesRequirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class PettypesRequirementTests {

    @Autowired
    private PetTypeRestControllerV1 controller;

    @MockitoBean
    private ClinicService clinicService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice()).build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listCases")
    @WithMockUser(roles = "VET_ADMIN")
    void listPetTypes(Rn requirement, boolean present, int expectedStatus) throws Exception {
        List<PetType> petTypes = present ? List.of(petType(1, "cat"), petType(2, "dog")) : List.of();
        given(clinicService.findAllPetTypes()).willReturn(petTypes);

        ResultActions result = mockMvc.perform(get("/api/pettypes").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));
        if (present) {
            result.andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("cat"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("dog"));
        }
    }

    static Stream<Arguments> listCases() {
        return Stream.of(Arguments.of(R1_1, true, 200), Arguments.of(R1_2, false, 404));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getCases")
    @WithMockUser(roles = "VET_ADMIN")
    void getPetType(Rn requirement, int id, boolean found, int expectedStatus) throws Exception {
        given(clinicService.findPetTypeById(id)).willReturn(found ? petType(id, "cat") : null);

        ResultActions result = mockMvc.perform(get("/api/pettypes/{id}", id).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));
        if (found) {
            result.andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.name").value("cat"));
        }
    }

    static Stream<Arguments> getCases() {
        return Stream.of(Arguments.of(R2_1, 1, true, 200), Arguments.of(R2_2, 999, false, 404),
            Arguments.of(R2_3, -1, false, 400));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("addCases")
    @WithMockUser(roles = "VET_ADMIN")
    void addPetType(Rn requirement, String body, int expectedStatus) throws Exception {
        willAnswer(invocation -> {
            invocation.<PetType>getArgument(0).setId(99);
            return null;
        }).given(clinicService).savePetType(any(PetType.class));

        ResultActions result = mockMvc.perform(post("/api/pettypes").content(body)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));
        if (expectedStatus == 201) {
            result.andExpect(header().string("Location", "/api/pettypes/99"))
                .andExpect(jsonPath("$.id").value(99)).andExpect(jsonPath("$.name").value("cat"));
            verify(clinicService).savePetType(any(PetType.class));
        }
        else {
            verify(clinicService, never()).savePetType(any(PetType.class));
        }
    }

    static Stream<Arguments> addCases() {
        return Stream.of(Arguments.of(R3_1, "{\"name\":\"cat\"}", 201),
            Arguments.of(R3_2, "{}", 400), Arguments.of(R3_2, "{\"name\":\"\"}", 400),
            Arguments.of(R3_2, "{\"name\":\"" + "x".repeat(81) + "\"}", 400));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateCases")
    @WithMockUser(roles = "VET_ADMIN")
    void updatePetType(Rn requirement, int id, String body, boolean found, int expectedStatus) throws Exception {
        PetType existing = petType(id, "cat");
        given(clinicService.findPetTypeById(id)).willReturn(found ? existing : null);

        mockMvc.perform(put("/api/pettypes/{id}", id).content(body)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));
        if (expectedStatus == 204) {
            verify(clinicService).savePetType(existing);
            assertThat(existing.getId()).isEqualTo(id);
            assertThat(existing.getName()).isEqualTo("dog");
        }
        else {
            verify(clinicService, never()).savePetType(any(PetType.class));
        }
    }

    static Stream<Arguments> updateCases() {
        return Stream.of(Arguments.of(R4_1, 1, "{\"id\":999,\"name\":\"dog\"}", true, 204),
            Arguments.of(R4_2, 999, "{\"id\":999,\"name\":\"dog\"}", false, 404),
            Arguments.of(R4_3, 1, "{\"id\":1}", true, 400),
            Arguments.of(R4_3, 1, "{\"id\":1,\"name\":\"\"}", true, 400),
            Arguments.of(R4_3, 1, "{\"id\":1,\"name\":\"" + "x".repeat(81) + "\"}", true, 400),
            Arguments.of(R4_4, -1, "{\"id\":1,\"name\":\"dog\"}", false, 400));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deleteCases")
    @WithMockUser(roles = "VET_ADMIN")
    void deletePetType(Rn requirement, int id, boolean found, int expectedStatus) throws Exception {
        PetType existing = petType(id, "cat");
        given(clinicService.findPetTypeById(id)).willReturn(found ? existing : null);

        mockMvc.perform(delete("/api/pettypes/{id}", id).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));
        if (found) {
            verify(clinicService).deletePetType(existing);
        }
        else {
            verify(clinicService, never()).deletePetType(any(PetType.class));
        }
    }

    static Stream<Arguments> deleteCases() {
        return Stream.of(Arguments.of(R5_1, 1, true, 204), Arguments.of(R5_2, 999, false, 404),
            Arguments.of(R5_3, -1, false, 400));
    }

    private static PetType petType(int id, String name) {
        PetType petType = new PetType();
        petType.setId(id);
        petType.setName(name);
        return petType;
    }

}
