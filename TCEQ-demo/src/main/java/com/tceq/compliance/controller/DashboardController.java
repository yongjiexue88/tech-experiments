package com.tceq.compliance.controller;

import com.tceq.compliance.service.ComplianceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Server-side MVC controller rendering Thymeleaf-based pages.
 *
 * <p>
 * Provides a server-rendered alternative to the static SPA dashboard,
 * demonstrating both approaches.
 */
@Controller
public class DashboardController {

    private final ComplianceService complianceService;

    public DashboardController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", complianceService.getDashboardStats());
        model.addAttribute("facilities", complianceService.getFacilitySummaries());
        model.addAttribute("violationsBySeverity", complianceService.getViolationsBySeverity());
        model.addAttribute("permitsByStatus", complianceService.getPermitsByStatus());
        return "dashboard";
    }
}
