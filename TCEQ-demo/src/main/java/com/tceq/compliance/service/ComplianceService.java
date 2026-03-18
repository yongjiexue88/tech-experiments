package com.tceq.compliance.service;

import com.tceq.compliance.model.*;
import com.tceq.compliance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Business-logic layer for the TCEQ compliance dashboard.
 *
 * <p>
 * Aggregates data across facilities, permits, inspections, and violations
 * to produce dashboard statistics and compliance insights.
 */
@Service
@Transactional(readOnly = true)
public class ComplianceService {

    private final FacilityRepository facilityRepo;
    private final PermitRepository permitRepo;
    private final InspectionRepository inspectionRepo;
    private final ViolationRepository violationRepo;

    public ComplianceService(FacilityRepository facilityRepo,
            PermitRepository permitRepo,
            InspectionRepository inspectionRepo,
            ViolationRepository violationRepo) {
        this.facilityRepo = facilityRepo;
        this.permitRepo = permitRepo;
        this.inspectionRepo = inspectionRepo;
        this.violationRepo = violationRepo;
    }

    /* ── Dashboard Aggregate Stats ────────────────────────────── */

    /**
     * Returns a summary map for the dashboard header cards.
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalFacilities", facilityRepo.count());
        stats.put("activeFacilities", facilityRepo.countByStatus("ACTIVE"));
        stats.put("totalPermits", permitRepo.count());
        stats.put("totalInspections", inspectionRepo.count());
        stats.put("totalViolations", violationRepo.count());
        stats.put("openViolations", violationRepo.findByStatus("OPEN").size());
        stats.put("totalPenalties", violationRepo.totalPenalties());
        stats.put("overdueInspections", inspectionRepo.findOverdueInspections(LocalDate.now()).size());
        stats.put("expiringPermits", permitRepo.findPermitsExpiringSoon(
                LocalDate.now(), LocalDate.now().plusDays(90)).size());
        return stats;
    }

    /**
     * Returns violation counts grouped by severity for chart rendering.
     */
    public Map<String, Long> getViolationsBySeverity() {
        return violationRepo.countBySeverityGrouped().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    /**
     * Returns permit counts grouped by status for chart rendering.
     */
    public Map<String, Long> getPermitsByStatus() {
        return permitRepo.countByStatusGrouped().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    /**
     * Returns inspection counts grouped by result for chart rendering.
     */
    public Map<String, Long> getInspectionsByResult() {
        return inspectionRepo.countByResultGrouped().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    /* ── Facility Views ───────────────────────────────────────── */

    public List<Facility> getAllFacilities() {
        return facilityRepo.findAll();
    }

    public Optional<Facility> getFacilityById(Long id) {
        return facilityRepo.findById(id);
    }

    public List<Facility> searchFacilities(String name) {
        return facilityRepo.searchByName(name);
    }

    public List<Facility> getFacilitiesByCounty(String county) {
        return facilityRepo.findByCountyIgnoreCase(county);
    }

    public List<String> getDistinctFacilityTypes() {
        return facilityRepo.findDistinctFacilityTypes();
    }

    public List<String> getDistinctCounties() {
        return facilityRepo.findDistinctCounties();
    }

    /* ── Compliance Scoring ───────────────────────────────────── */

    /**
     * Calculates a simple compliance score (0-100) for a facility.
     *
     * <p>
     * Scoring:
     * <ul>
     * <li>Start at 100</li>
     * <li>-5 per open MINOR violation</li>
     * <li>-15 per open MAJOR violation</li>
     * <li>-30 per open CRITICAL violation</li>
     * <li>-10 if any permit is expired</li>
     * <li>-5 if inspection is overdue</li>
     * </ul>
     */
    public int calculateComplianceScore(Long facilityId) {
        int score = 100;

        List<Violation> openViolations = violationRepo.findByFacility_Id(facilityId)
                .stream().filter(v -> "OPEN".equals(v.getStatus())).toList();

        for (Violation v : openViolations) {
            score -= switch (v.getSeverity()) {
                case "CRITICAL" -> 30;
                case "MAJOR" -> 15;
                default -> 5;
            };
        }

        long expiredPermits = permitRepo.findByFacility_Id(facilityId).stream()
                .filter(p -> "ACTIVE".equals(p.getStatus())
                        && p.getExpiryDate() != null
                        && p.getExpiryDate().isBefore(LocalDate.now()))
                .count();
        score -= (int) (expiredPermits * 10);

        long overdueInspections = inspectionRepo.findByFacility_Id(facilityId).stream()
                .filter(i -> i.getNextInspectionDue() != null
                        && i.getNextInspectionDue().isBefore(LocalDate.now()))
                .count();
        score -= (int) (overdueInspections * 5);

        return Math.max(0, score);
    }

    /**
     * Returns a list of facility summaries with compliance scores, suitable
     * for the main dashboard table.
     */
    public List<Map<String, Object>> getFacilitySummaries() {
        List<Map<String, Object>> summaries = new ArrayList<>();
        for (Facility f : facilityRepo.findAll()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", f.getId());
            row.put("facilityName", f.getFacilityName());
            row.put("epaId", f.getEpaId());
            row.put("facilityType", f.getFacilityType());
            row.put("city", f.getCity());
            row.put("county", f.getCounty());
            row.put("status", f.getStatus());
            row.put("permits", f.getPermits().size());
            row.put("violations", f.getViolations().size());
            row.put("openViolations", f.getViolations().stream()
                    .filter(v -> "OPEN".equals(v.getStatus())).count());
            row.put("complianceScore", calculateComplianceScore(f.getId()));
            summaries.add(row);
        }
        return summaries;
    }
}
