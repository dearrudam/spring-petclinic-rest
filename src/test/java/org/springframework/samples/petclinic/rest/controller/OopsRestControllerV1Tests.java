package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.capabilities.oops.OopsRequirement;
import org.springframework.samples.petclinic.capabilities.oops.OopsRequirement.Rn;
import org.springframework.samples.petclinic.rest.controller.v1.OopsRestControllerV1;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OopsRestControllerV1Tests {

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OopsRestControllerV1()).build();

    @ParameterizedTest(name = "{0}")
    @EnumSource(value = OopsRequirement.Rn.class, names = "R1_1")
    void returnsAProblemResponseInsteadOfSuccess(Rn requirement) throws Exception {
        this.mockMvc.perform(get("/api/oops").accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type").value("http://localhost/api/oops"))
            .andExpect(jsonPath("$.title").value("Sample error"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("A sample error was requested."))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.schemaValidationErrors").isEmpty())
            .andExpect(jsonPath("$.exception").doesNotExist())
            .andExpect(jsonPath("$.trace").doesNotExist());
    }
}
