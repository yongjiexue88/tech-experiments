package com.tceq.compliance.repository;

import com.tceq.compliance.model.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Permit} entities.
 */
@Repository
public interface PermitRepository extends JpaRepository<Permit, Long> {

    List<Permit> findByFacility_Id(Long facilityId);

    List<Permit> findByStatus(String status);

    List<Permit> findByPermitType(String permitType);

    @Query("SELECT p FROM Permit p WHERE p.expiryDate < :date AND p.status = 'ACTIVE'")
    List<Permit> findExpiredPermits(@Param("date") LocalDate date);

    @Query("SELECT p FROM Permit p WHERE p.expiryDate BETWEEN :start AND :end")
    List<Permit> findPermitsExpiringSoon(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT p.status, COUNT(p) FROM Permit p GROUP BY p.status")
    List<Object[]> countByStatusGrouped();
}
