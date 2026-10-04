package com.vehicle.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import com.vehicle.service.UserAccountService;
import org.springframework.security.web.csrf.CsrfToken;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * AuthController
 * Handles: /, /select-role, /login/admin, /login/user
 */
@Controller
public class AuthController {

    @Value("${app.auth.admin.username}")
    private String adminUsername;

    @Value("${app.auth.admin.password}")
    private String adminPassword;

    @Value("${app.auth.user.username}")
    private String userUsername;

    @Value("${app.auth.user.password}")
    private String userPassword;

    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /* ─── Home Page ─────────────────────────────────── */

    @GetMapping("/")
    public String home() {
        return "index";                          // → templates/index.html
    }

    @GetMapping("/app")
    public String app(HttpSession session) {
        if (session.getAttribute("role") == null) return "redirect:/select-role";
        return "redirect:/index.html#dashboard";
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false) String error) {
        return error == null ? "redirect:/select-role" : "redirect:/login/user?error";
    }

    @GetMapping("/api/session")
    @org.springframework.web.bind.annotation.ResponseBody
    public Map<String, String> sessionInfo(
            HttpSession session,
            @org.springframework.web.bind.annotation.RequestAttribute("_csrf") CsrfToken csrfToken) {
        return Map.of(
                "username", (String) session.getAttribute("username"),
                "role", (String) session.getAttribute("role"),
                "csrfToken", csrfToken.getToken());
    }

    /* ─── Role Selection ─────────────────────────────── */

    @GetMapping("/select-role")
    public String selectRole() {
        return "auth/select-role";               // → templates/auth/select-role.html
    }

    /* ─── Admin Login ────────────────────────────────── */

    @GetMapping("/login/admin")
    public String adminLoginPage() {
        return "auth/login-admin";               // → templates/auth/login-admin.html
    }

    /**
     * Admin Login POST
     * NOTE: If Spring Security is active, this endpoint is handled by the security filter.
     * This manual method is used only WITHOUT Spring Security.
     */
    @PostMapping("/login/admin")
    public String adminLoginSubmit(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            HttpServletRequest request,
            Model model) {

        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            request.changeSessionId();
            session.setAttribute("role", "ADMIN");
            session.setAttribute("username", username);
            return "redirect:/app";
        }

        return "redirect:/login/admin?error";
    }

    /* ─── User Login ─────────────────────────────────── */

    @GetMapping("/login/user")
    public String userLoginPage() {
        return "auth/login-user";                // → templates/auth/login-user.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session,
            HttpServletRequest request,
            Model model) {

        String normalizedUsername = username.trim();
        if (!normalizedUsername.matches("[A-Za-z0-9._-]{3,32}")) {
            model.addAttribute("registrationError", "Use 3 to 32 letters, numbers, dots, underscores, or hyphens.");
            model.addAttribute("username", normalizedUsername);
            return "auth/register";
        }
        if (normalizedUsername.equalsIgnoreCase(adminUsername)
                || normalizedUsername.equalsIgnoreCase(userUsername)) {
            model.addAttribute("registrationError", "That username is reserved. Choose another one.");
            model.addAttribute("username", normalizedUsername);
            return "auth/register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("registrationError", "The passwords do not match.");
            model.addAttribute("username", normalizedUsername);
            return "auth/register";
        }

        try {
            userAccountService.register(normalizedUsername, password);
            request.changeSessionId();
            session.setAttribute("role", "USER");
            session.setAttribute("username", normalizedUsername);
            return "redirect:/app";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            model.addAttribute("username", normalizedUsername);
            return "auth/register";
        }
    }

    /**
     * User Login POST
     * NOTE: If Spring Security is active, this endpoint is handled by the security filter.
     * This manual method is used only WITHOUT Spring Security.
     */
    @PostMapping("/login/user")
    public String userLoginSubmit(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            HttpServletRequest request,
            Model model) {

        if ((userUsername.equals(username) && userPassword.equals(password))
            || userAccountService.authenticate(username, password)) {
            request.changeSessionId();
            session.setAttribute("role", "USER");
            session.setAttribute("username", username);
            return "redirect:/app";
        }

        return "redirect:/login/user?error";
    }

    /* ─── Logout ─────────────────────────────────────── */

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
