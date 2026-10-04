package com.vehicle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

@Component
public class ApiAccessInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public ApiAccessInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        HttpSession session = request.getSession(false);
        Object roleValue = session == null ? null : session.getAttribute("role");
        if (!(roleValue instanceof String role)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Sign in to access this resource.");
            return false;
        }

        if ("ADMIN".equals(role)) return true;

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if ("USER".equals(role) && "GET".equalsIgnoreCase(request.getMethod())
            && ("/api/session".equals(path) || "/api/vehicles".equals(path)
                || path.matches("/api/vehicles/search/[^/]+"))) return true;

        writeError(response, HttpServletResponse.SC_FORBIDDEN, "Your account does not have permission for this action.");
        return false;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("error", message));
    }
}
