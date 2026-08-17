package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.capabilities.users.UsersRequirement;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.UserRestControllerV1;
import org.springframework.samples.petclinic.rest.dto.RoleDto;
import org.springframework.samples.petclinic.rest.dto.UserDto;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.users.UsersRequirement.Rn.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class UserRestControllerV1Tests {

    @Autowired
    private UserRestControllerV1 userRestControllerV1;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userRestControllerV1)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("addUserCases")
    @WithMockUser(roles = "ADMIN")
    void addUser(UsersRequirement.Rn requirement, UserDto user, int expectedStatus) throws Exception {
        var result = this.mockMvc.perform(post("/api/users")
                .content(objectMapper.writeValueAsString(user))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus));

        if (expectedStatus == 201) {
            result.andExpect(jsonPath("$.username").value("username"));
            verify(userService).saveUser(any());
        }
        else {
            verify(userService, never()).saveUser(any());
        }
    }

    static Stream<Arguments> addUserCases() {
        return Stream.of(
            Arguments.of(R1_1, validUser(), 201),
            Arguments.of(R1_2, validUser().username(null), 400),
            Arguments.of(R1_2, validUser().username(""), 400),
            Arguments.of(R1_2, validUser().username("u".repeat(81)), 400),
            Arguments.of(R1_2, validUser().password(""), 400),
            Arguments.of(R1_2, validUser().password("p".repeat(81)), 400),
            Arguments.of(R1_2, validUser().roles(java.util.List.of(new RoleDto())), 400),
            Arguments.of(R1_2, validUser().roles(java.util.List.of(new RoleDto().name(""))), 400),
            Arguments.of(R1_2, validUser().roles(java.util.List.of(new RoleDto().name("r".repeat(81)))), 400),
            Arguments.of(R1_3, validUser().roles(null), 400),
            Arguments.of(R1_3, validUser().roles(java.util.List.of()), 400)
        );
    }

    private static UserDto validUser() {
        return new UserDto().username("username").password("password").enabled(true)
            .roles(java.util.List.of(new RoleDto().name("OWNER_ADMIN")));
    }
}
