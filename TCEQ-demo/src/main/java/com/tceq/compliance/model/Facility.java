package com.tceq.compliance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a regulated environmental facility in Texas.
 *
 * <p>
 * Each facility has a unique EPA-assigned identifier and is subject to
 * environmental permits, inspections, and potential violations tracked
 * by the TCEQ.
 */
@Entity
@Table(name = "FACILITIES", indexes = {
        @Index(name = "IDX_FACILITY_EPA_ID", columnList = "epaId", unique = true),
        @Index(name = "IDX_FACILITY_COUNTY", columnList = "county")
})
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facility_seq")
    @SequenceGenerator(name = "facility_seq", sequenceName = "FACILITY_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "FACILITY_NAME", nullable = false, length = 200)
    private String facilityName;

    @NotBlank
    @Size(max = 20)
    @Column(name = "EPA_ID", nullable = false, unique = true, length = 20)
    private String epaId;

    @Size(max = 50)
    @Column(name = "FACILITY_TYPE", length = 50)
    private String facilityType;

    @Size(max = 300)
    @Column(name = "ADDRESS", length = 300)
    private String address;

    @Size(max = 100)
    @Column(name = "CITY", length = 100)
    private String city;

    @Size(max = 100)
    @Column(name = "COUNTY", length = 100)
    private String county;

    @Size(max = 2)
    @Column(name = "STATE_CODE", length = 2)
    private String stateCode;

    @Size(max = 10)
    @Column(name = "ZIP_CODE", length = 10)
    private String zipCode;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Size(max = 20)
    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "REGISTRATION_DATE")
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Permit> permits = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inspection> inspections = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Violation> violations = new ArrayList<>();

    /* ── Constructors ─────────────────────────────────────────── */

    public Facility() {
    }

    public Facility(String facilityName, String epaId, String facilityType,
            String city, String county) {
        this.facilityName = facilityName;
        this.epaId = epaId;
        this.facilityType = facilityType;
        this.city = city;
        this.county = county;
        this.stateCode = "TX";
        this.status = "ACTIVE";
    }

    /* ── Getters & Setters ────────────────────────────────────── */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getEpaId() {
        return epaId;
    }

    public void setEpaId(String epaId) {
        this.epaId = epaId;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<Permit> getPermits() {
        return permits;
    }

    public void setPermits(List<Permit> permits) {
        this.permits = permits;
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public void setInspections(List<Inspection> inspections) {
        this.inspections = inspections;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    /* ── Helpers ───────────────────────────────────────────────── */

    public void addPermit(Permit permit) {
        permits.add(permit);
        permit.setFacility(this);
    }

    public void addInspection(Inspection inspection) {
        inspections.add(inspection);
        inspection.setFacility(this);
    }

    public void addViolation(Violation violation) {
        violations.add(violation);
        violation.setFacility(this);
    }

    @Override
    public String toString() {
        return "Facility{id=" + id + ", name='" + facilityName + "', epaId='" + epaId + "'}";
    }
}
