package com.vehicle.controller.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:login-web-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AuthControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void legacyLoginUrlRedirectsToCustomLoginPage() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/user?error"));
    }

    @Test
    void loginPageUsesCustomUiAndShowsInlineErrorAfterFailedLogin() throws Exception {
        MvcResult loginPage = mockMvc.perform(get("/login/user"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Welcome Back")))
                .andReturn();

        String csrfToken = extractCsrfToken(loginPage.getResponse().getContentAsString());
        mockMvc.perform(post("/login/user")
                        .session((MockHttpSession) loginPage.getRequest().getSession(false))
                        .param("username", "unknown-login-test-user")
                        .param("password", "incorrect-password")
                        .param("_csrf", csrfToken))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login/user?error"));

        mockMvc.perform(get("/login/user").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Invalid username or password. Please try again.")));
    }

    @Test
    void sessionEndpointProvidesCsrfTokenAndApiRejectsMissingToken() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("role", "ADMIN");
        session.setAttribute("username", "test-admin");

        mockMvc.perform(get("/api/session").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.csrfToken").isNotEmpty());

        mockMvc.perform(post("/api/owners")
                        .session(session)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    private String extractCsrfToken(String html) {
        Matcher matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(html);
        assertTrue(matcher.find(), "login form should contain a CSRF token");
        return matcher.group(1);
    }
}
