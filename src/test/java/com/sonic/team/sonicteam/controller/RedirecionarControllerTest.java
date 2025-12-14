package com.sonic.team.sonicteam.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirecionarController.class)
class RedirecionarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ==================== TESTES redirecionarParaSwagger ====================

    @Test
    void redirecionarParaSwagger_DeveRedirecionarParaSwaggerUI() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }
}
