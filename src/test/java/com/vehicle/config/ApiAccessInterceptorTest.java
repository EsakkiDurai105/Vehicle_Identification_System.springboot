package com.vehicle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiAccessInterceptorTest {

    private final ApiAccessInterceptor interceptor = new ApiAccessInterceptor(new ObjectMapper());

    @Test
    void anonymousApiRequestIsUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vehicles");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(401, response.getStatus());
    }

    @Test
    void userCanReadVehicleListButCannotReadOwnersOrWrite() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("role", "USER");

        assertTrue(request("GET", "/api/vehicles", session).allowed());
        assertEquals(403, request("GET", "/api/owners", session).response().getStatus());
        assertEquals(403, request("POST", "/api/vehicles", session).response().getStatus());
        assertEquals(403, request("GET", "/api/logs", session).response().getStatus());
    }

    @Test
    void adminCanManageApiResources() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("role", "ADMIN");

        assertTrue(request("GET", "/api/owners", session).allowed());
        assertTrue(request("DELETE", "/api/vehicles/1", session).allowed());
    }

    private AccessResult request(String method, String path, MockHttpSession session) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean allowed = interceptor.preHandle(request, response, new Object());
        return new AccessResult(allowed, response);
    }

    private record AccessResult(boolean allowed, MockHttpServletResponse response) {}
}