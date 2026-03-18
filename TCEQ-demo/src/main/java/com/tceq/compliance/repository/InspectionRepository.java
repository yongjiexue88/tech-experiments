package com.tceq.compliance.repository;

import com.tceq.compliance.model.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Inspection} entities.
 */
@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    List<Inspection> findByFacility_Id(Long facilityId);

    List<Inspection> findByResult(String result);

    @Query("SELECT i FROM Inspection i WHERE i.nextInspectionDue < :date")
    List<Inspection> findOverdueInspections(@Param("date") LocalDate date);

    @Query("SELECT i FROM Inspection i WHERE i.inspectionDate BETWEEN :start AND :end ORDER BY i.inspectionDate DESC")
    List<Inspection> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT i.result, COUNT(i) FROM Inspection i GROUP BY i.result")
    List<Object[]> countByResultGrouped();
}
