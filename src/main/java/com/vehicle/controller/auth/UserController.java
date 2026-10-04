package com.vehicle.controller.auth;

import com.vehicle.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

/**
 * UserController
 * Handles: /user/** routes — all require USER role in session
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private VehicleService vehicleService;

    /* ─── Auth Guard ─────────────────────────────────── */
    private boolean isUser(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "USER".equals(role) || "ADMIN".equals(role); // admins can also view
    }

    /* ─── Dashboard ──────────────────────────────────── */

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isUser(session)) return "redirect:/login/user";

        return "redirect:/app";
    }

    /* ─── My Vehicles ────────────────────────────────── */

    @GetMapping("/vehicles")
    public String myVehicles(HttpSession session, Model model) {
        if (!isUser(session)) return "redirect:/login/user";

        return "redirect:/index.html#vehicles";
    }

    /* ─── Service History ────────────────────────────── */

    @GetMapping("/service-history")
    public String serviceHistory(HttpSession session, Model model) {
        if (!isUser(session)) return "redirect:/login/user";

        return "redirect:/index.html#vehicles";
    }

    /* ─── Profile ────────────────────────────────────── */

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        if (!isUser(session)) return "redirect:/login/user";

        return "redirect:/app";
    }

    /* ─── Workshops ──────────────────────────────────── */

    @GetMapping("/workshops")
    public String workshops(HttpSession session, Model model) {
        if (!isUser(session)) return "redirect:/login/user";

        return "redirect:/app";
    }
}
