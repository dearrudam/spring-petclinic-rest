package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.samples.petclinic.capabilities.root.RootRequirement;
import org.springframework.samples.petclinic.rest.controller.v1.RootRestControllerV1;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RootRestControllerRequirementTests {

    @ParameterizedTest(name = "{0}")
    @EnumSource(value = RootRequirement.Rn.class, names = "R1_1")
    void redirectsRootToInteractiveDocumentationInTheApplicationContext(RootRequirement.Rn requirement) throws Exception {
        RootRestControllerV1 controller = new RootRestControllerV1();
        ReflectionTestUtils.setField(controller, "servletContextPath", "/petclinic");
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/petclinic/swagger-ui/index.html"));
    }
}
