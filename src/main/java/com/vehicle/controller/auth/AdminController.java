package com.vehicle.controller.auth;

import com.vehicle.service.ActivityLogService;
import com.vehicle.service.OwnerService;
import com.vehicle.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

/**
 * AdminController
 * Handles: /admin/** routes — all require ADMIN role in session
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private ActivityLogService activityLogService;

    /* ─── Auth Guard ─────────────────────────────────── */
    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    /* ─── Dashboard ──────────────────────────────────── */

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login/admin";
        return "redirect:/app";
    }

    /* ─── Vehicles ───────────────────────────────────── */

    @GetMapping("/vehicles")
    public String vehicles(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login/admin";

        return "redirect:/index.html#vehicles";
    }

    /* ─── Owners ─────────────────────────────────────── */

    @GetMapping("/owners")
    public String owners(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login/admin";

        return "redirect:/index.html#owners";
    }

    /* ─── Users ──────────────────────────────────────── */

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login/admin";

        return "redirect:/app";
    }

    /* ─── Reports ────────────────────────────────────── */

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login/admin";

        return "redirect:/index.html#logs";
    }

    /* ─── Workshops ──────────────────────────────────── */

    @GetMapping("/workshops")
    public String workshops(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login/admin";

        return "redirect:/index.html#service";
    }
}
