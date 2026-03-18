package com.tceq.compliance.controller;

import com.tceq.compliance.model.*;
import com.tceq.compliance.service.ComplianceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API controller exposing JSON endpoints for the compliance dashboard.
 *
 * <p>
 * All endpoints are prefixed with {@code /api}.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final ComplianceService complianceService;

    public ApiController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    /* ── Dashboard Stats ──────────────────────────────────────── */

    @GetMapping("/dashboard/stats")
    public Map<String, Object> getDashboardStats() {
        return complianceService.getDashboardStats();
    }

    @GetMapping("/dashboard/violations-by-severity")
    public Map<String, Long> getViolationsBySeverity() {
        return complianceService.getViolationsBySeverity();
    }

    @GetMapping("/dashboard/permits-by-status")
    public Map<String, Long> getPermitsByStatus() {
        return complianceService.getPermitsByStatus();
    }

    @GetMapping("/dashboard/inspections-by-result")
    public Map<String, Long> getInspectionsByResult() {
        return complianceService.getInspectionsByResult();
    }

    /* ── Facilities ───────────────────────────────────────────── */

    @GetMapping("/facilities")
    public List<Map<String, Object>> getFacilities(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String county) {

        if (search != null && !search.isBlank()) {
            // Return simplified view when searching
            return complianceService.searchFacilities(search).stream()
                    .map(this::facilityToMap).toList();
        }
        if (county != null && !county.isBlank()) {
            return complianceService.getFacilitiesByCounty(county).stream()
                    .map(this::facilityToMap).toList();
        }
        return complianceService.getFacilitySummaries();
    }

    @GetMapping("/facilities/{id}")
    public ResponseEntity<Map<String, Object>> getFacility(@PathVariable Long id) {
        return complianceService.getFacilityById(id)
                .map(f -> {
                    Map<String, Object> data = facilityToMap(f);
                    data.put("complianceScore", complianceService.calculateComplianceScore(id));
                    return ResponseEntity.ok(data);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/facilities/types")
    public List<String> getFacilityTypes() {
        return complianceService.getDistinctFacilityTypes();
    }

    @GetMapping("/facilities/counties")
    public List<String> getCounties() {
        return complianceService.getDistinctCounties();
    }

    /* ── Helpers ───────────────────────────────────────────────── */

    private Map<String, Object> facilityToMap(Facility f) {
        return Map.of(
                "id", f.getId(),
                "facilityName", f.getFacilityName(),
                "epaId", f.getEpaId(),
                "facilityType", f.getFacilityType() != null ? f.getFacilityType() : "",
                "city", f.getCity() != null ? f.getCity() : "",
                "county", f.getCounty() != null ? f.getCounty() : "",
                "status", f.getStatus() != null ? f.getStatus() : "");
    }
}
