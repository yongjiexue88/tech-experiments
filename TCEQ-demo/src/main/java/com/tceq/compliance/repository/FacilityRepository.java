package com.tceq.compliance.repository;

import com.tceq.compliance.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Facility} entities.
 */
@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    Optional<Facility> findByEpaId(String epaId);

    List<Facility> findByStatus(String status);

    List<Facility> findByCountyIgnoreCase(String county);

    List<Facility> findByFacilityType(String facilityType);

    @Query(value = "SELECT * FROM FACILITIES f WHERE LOWER(f.FACILITY_NAME) LIKE LOWER('%' || :name || '%')", nativeQuery = true)
    List<Facility> searchByName(@Param("name") String name);

    @Query("SELECT COUNT(f) FROM Facility f WHERE f.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT DISTINCT f.facilityType FROM Facility f WHERE f.facilityType IS NOT NULL ORDER BY f.facilityType")
    List<String> findDistinctFacilityTypes();

    @Query("SELECT DISTINCT f.county FROM Facility f WHERE f.county IS NOT NULL ORDER BY f.county")
    List<String> findDistinctCounties();
}
