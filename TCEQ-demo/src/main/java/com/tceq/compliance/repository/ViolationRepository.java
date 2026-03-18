package com.tceq.compliance.repository;

import com.tceq.compliance.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Violation} entities.
 */
@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

        List<Violation> findByFacility_Id(Long facilityId);

        List<Violation> findBySeverity(String severity);

        List<Violation> findByStatus(String status);

        @Query("SELECT v.severity, COUNT(v) FROM Violation v GROUP BY v.severity")
        List<Object[]> countBySeverityGrouped();

        @Query("SELECT v.status, COUNT(v) FROM Violation v GROUP BY v.status")
        List<Object[]> countByStatusGrouped();

        @Query("SELECT COALESCE(SUM(v.penaltyAmount), 0) FROM Violation v")
        BigDecimal totalPenalties();

        @Query("SELECT v FROM Violation v WHERE v.status = 'OPEN' ORDER BY " +
                        "CASE v.severity WHEN 'CRITICAL' THEN 1 WHEN 'MAJOR' THEN 2 ELSE 3 END")
        List<Violation> findOpenViolationsBySeverityPriority();

        @Query("SELECT v.facility.id, COUNT(v) FROM Violation v WHERE v.status = 'OPEN' " +
                        "GROUP BY v.facility.id HAVING COUNT(v) >= :minCount")
        List<Object[]> findFacilitiesWithMultipleOpenViolations(@Param("minCount") long minCount);
}
