package org.springframework.samples.petclinic.rest.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.capabilities.specialties.SpecialtiesRequirement.Rn;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.SpecialtyRestControllerV1;
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
import static org.springframework.samples.petclinic.capabilities.specialties.SpecialtiesRequirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class SpecialtiesRequirementTests {

    @Autowired
    private SpecialtyRestControllerV1 controller;

    @MockitoBean
    private ClinicService clinicService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice()).build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listCases")
    @WithMockUser(roles = "VET_ADMIN")
    void listSpecialties(Rn requirement, boolean present, int expectedStatus) throws Exception {
        given(clinicService.findAllSpecialties())
            .willReturn(present ? List.of(specialty(1, "radiology"), specialty(2, "surgery")) : List.of());
        ResultActions result = mockMvc.perform(get("/api/specialties").accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().is(expectedStatus));
        if (present) {
            result.andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("radiology"))
                .andExpect(jsonPath("$[1].name").value("surgery"));
        }
    }

    static Stream<Arguments> listCases() {
        return Stream.of(Arguments.of(R1_1, true, 200), Arguments.of(R1_2, false, 404));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getCases")
    @WithMockUser(roles = "VET_ADMIN")
    void getSpecialty(Rn requirement, int id, boolean found, int expectedStatus) throws Exception {
        given(clinicService.findSpecialtyById(id)).willReturn(found ? specialty(1, "radiology") : null);
        ResultActions result = mockMvc.perform(get("/api/specialties/{id}", id).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().is(expectedStatus));
        if (found) result.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("radiology"));
    }

    static Stream<Arguments> getCases() {
        return Stream.of(Arguments.of(R2_1, 1, true, 200), Arguments.of(R2_2, 999, false, 404), Arguments.of(R2_3, -1, false, 400));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("addCases")
    @WithMockUser(roles = "VET_ADMIN")
    void addSpecialty(Rn requirement, String name, boolean included, int expectedStatus) throws Exception {
        willAnswer(invocation -> {
            Specialty specialty = invocation.getArgument(0);
            assertThat(specialty.getName()).isEqualTo(name);
            specialty.setId(999);
            return null;
        }).given(clinicService).saveSpecialty(any(Specialty.class));
        Map<String, Object> body = included ? Map.of("name", name) : Map.of();
        ResultActions result = mockMvc.perform(post("/api/specialties").content(objectMapper.writeValueAsString(body))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 201) result.andExpect(header().string("Location", "/api/specialties/999")).andExpect(jsonPath("$.id").value(999));
        else verify(clinicService, never()).saveSpecialty(any(Specialty.class));
    }

    static Stream<Arguments> addCases() {
        return Stream.of(Arguments.of(R3_1, "radiology", true, 201), Arguments.of(R3_2, null, false, 400),
            Arguments.of(R3_2, "", true, 400), Arguments.of(R3_2, "x".repeat(81), true, 400));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateCases")
    @WithMockUser(roles = "VET_ADMIN")
    void updateSpecialty(Rn requirement, int id, String name, boolean included, boolean found, int expectedStatus) throws Exception {
        Specialty specialty = specialty(1, "radiology");
        given(clinicService.findSpecialtyById(id)).willReturn(found ? specialty : null);
        Map<String, Object> body = included ? Map.of("name", name) : Map.of();
        ResultActions result = mockMvc.perform(put("/api/specialties/{id}", id).content(objectMapper.writeValueAsString(body))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().is(expectedStatus));
        if (expectedStatus == 204) {
            verify(clinicService).saveSpecialty(specialty);
            assertThat(specialty.getId()).isEqualTo(id);
            assertThat(specialty.getName()).isEqualTo(name);
        }
        else {
            verify(clinicService, never()).saveSpecialty(any(Specialty.class));
        }
    }

    static Stream<Arguments> updateCases() {
        return Stream.of(Arguments.of(R4_1, 1, "surgery", true, true, 204), Arguments.of(R4_2, 999, "surgery", true, false, 404),
            Arguments.of(R4_3, 1, null, false, true, 400), Arguments.of(R4_3, 1, "", true, true, 400),
            Arguments.of(R4_3, 1, "x".repeat(81), true, true, 400), Arguments.of(R4_4, -1, "surgery", true, false, 400));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deleteCases")
    @WithMockUser(roles = "VET_ADMIN")
    void deleteSpecialty(Rn requirement, int id, boolean found, int expectedStatus) throws Exception {
        Specialty specialty = specialty(1, "radiology");
        given(clinicService.findSpecialtyById(id)).willReturn(found ? specialty : null);
        mockMvc.perform(delete("/api/specialties/{id}", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus));
        if (found) verify(clinicService).deleteSpecialty(specialty); else verify(clinicService, never()).deleteSpecialty(any(Specialty.class));
    }

    static Stream<Arguments> deleteCases() {
        return Stream.of(Arguments.of(R5_1, 1, true, 204), Arguments.of(R5_2, 999, false, 404), Arguments.of(R5_3, -1, false, 400));
    }

    private static Specialty specialty(int id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        return specialty;
    }
}
