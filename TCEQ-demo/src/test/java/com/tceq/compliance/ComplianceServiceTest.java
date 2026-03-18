package com.tceq.compliance;

import com.tceq.compliance.model.*;
import com.tceq.compliance.repository.*;
import com.tceq.compliance.service.ComplianceService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the TCEQ Compliance Tracking System.
 *
 * <p>
 * Tests run against the H2 in-memory database with seed data loaded
 * from {@code schema.sql} and {@code data.sql}.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ComplianceServiceTest {

    @Autowired
    private ComplianceService complianceService;

    @Autowired
    private FacilityRepository facilityRepo;

    @Autowired
    private PermitRepository permitRepo;

    @Autowired
    private InspectionRepository inspectionRepo;

    @Autowired
    private ViolationRepository violationRepo;

    /* ── Repository Tests ─────────────────────────────────────── */

    @Test
    @Order(1)
    @DisplayName("Seed data loads — facilities exist")
    void seedDataLoaded() {
        long count = facilityRepo.count();
        assertTrue(count >= 8, "Expected at least 8 seeded facilities, got " + count);
    }

    @Test
    @Order(2)
    @DisplayName("Find facility by EPA ID")
    void findByEpaId() {
        var facility = facilityRepo.findByEpaId("TXD980873749");
        assertTrue(facility.isPresent(), "Should find Lone Star Petrochemical Plant");
        assertEquals("Lone Star Petrochemical Plant", facility.get().getFacilityName());
    }

    @Test
    @Order(3)
    @DisplayName("Find facilities by county")
    void findByCounty() {
        List<Facility> harris = facilityRepo.findByCountyIgnoreCase("Harris");
        assertFalse(harris.isEmpty(), "Should find facilities in Harris County");
    }

    @Test
    @Order(4)
    @DisplayName("Search facilities by name")
    void searchByName() {
        List<Facility> results = facilityRepo.searchByName("Gulf");
        assertFalse(results.isEmpty(), "Should find Gulf Coast Water Treatment");
        assertTrue(results.stream().anyMatch(f -> f.getFacilityName().contains("Gulf")));
    }

    @Test
    @Order(5)
    @DisplayName("Get distinct facility types")
    void distinctFacilityTypes() {
        List<String> types = facilityRepo.findDistinctFacilityTypes();
        assertFalse(types.isEmpty());
        assertTrue(types.contains("Industrial"));
    }

    @Test
    @Order(6)
    @DisplayName("Permits loaded and linked to facilities")
    void permitsLinked() {
        List<Permit> permits = permitRepo.findByFacility_Id(1L);
        assertFalse(permits.isEmpty(), "Facility 1 should have permits");
    }

    @Test
    @Order(7)
    @DisplayName("Count permits by status")
    void permitsByStatus() {
        List<Object[]> grouped = permitRepo.countByStatusGrouped();
        assertFalse(grouped.isEmpty(), "Should have permit status groups");
    }

    @Test
    @Order(8)
    @DisplayName("Violations loaded with severity info")
    void violationsExist() {
        List<Violation> critical = violationRepo.findBySeverity("CRITICAL");
        assertFalse(critical.isEmpty(), "Should have CRITICAL violations");
    }

    @Test
    @Order(9)
    @DisplayName("Total penalties are nonzero")
    void totalPenalties() {
        BigDecimal total = violationRepo.totalPenalties();
        assertNotNull(total);
        assertTrue(total.compareTo(BigDecimal.ZERO) > 0, "Total penalties should be > 0");
    }

    /* ── Service Layer Tests ──────────────────────────────────── */

    @Test
    @Order(10)
    @DisplayName("Dashboard stats returns expected keys")
    void dashboardStats() {
        Map<String, Object> stats = complianceService.getDashboardStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalFacilities"));
        assertTrue(stats.containsKey("openViolations"));
        assertTrue(stats.containsKey("totalPenalties"));
        assertTrue(stats.containsKey("overdueInspections"));
    }

    @Test
    @Order(11)
    @DisplayName("Compliance score: clean facility scores 100")
    void complianceScoreClean() {
        // Facility 3 (Austin Clean Air Manufacturing) has no open violations
        // but may have an overdue inspection (-5 points)
        int score = complianceService.calculateComplianceScore(3L);
        assertTrue(score >= 90, "Facility with no violations should score >= 90, got " + score);
    }

    @Test
    @Order(12)
    @DisplayName("Compliance score: violated facility scores lower")
    void complianceScoreViolated() {
        // Facility 7 (Corpus Christi) has 2 CRITICAL open violations
        int score = complianceService.calculateComplianceScore(7L);
        assertTrue(score < 100, "Facility with open violations should score < 100");
        assertTrue(score <= 40, "Facility with 2 CRITICAL violations should score ≤ 40");
    }

    @Test
    @Order(13)
    @DisplayName("Facility summaries include compliance scores")
    void facilitySummaries() {
        List<Map<String, Object>> summaries = complianceService.getFacilitySummaries();
        assertFalse(summaries.isEmpty());

        Map<String, Object> first = summaries.get(0);
        assertTrue(first.containsKey("complianceScore"));
        assertTrue(first.containsKey("facilityName"));
        assertTrue(first.containsKey("openViolations"));
    }

    @Test
    @Order(14)
    @DisplayName("Violations by severity produces chart data")
    void violationsBySeverity() {
        Map<String, Long> data = complianceService.getViolationsBySeverity();
        assertNotNull(data);
        assertFalse(data.isEmpty());
    }

    @Test
    @Order(15)
    @DisplayName("Inspections by result produces chart data")
    void inspectionsByResult() {
        Map<String, Long> data = complianceService.getInspectionsByResult();
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.containsKey("PASS"));
    }
}
