package com.tceq.compliance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Environmental compliance violation recorded against a facility.
 *
 * <p>
 * Violations track severity (MINOR, MAJOR, CRITICAL), corrective actions,
 * and any associated monetary penalties.
 */
@Entity
@Table(name = "VIOLATIONS", indexes = {
        @Index(name = "IDX_VIOLATION_SEVERITY", columnList = "severity"),
        @Index(name = "IDX_VIOLATION_STATUS", columnList = "status")
})
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "violation_seq")
    @SequenceGenerator(name = "violation_seq", sequenceName = "VIOLATION_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "VIOLATION_CODE", nullable = false, length = 30)
    private String violationCode;

    @Size(max = 500)
    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Size(max = 20)
    @Column(name = "SEVERITY", length = 20)
    private String severity;

    @Column(name = "VIOLATION_DATE")
    private LocalDate violationDate;

    @Size(max = 20)
    @Column(name = "STATUS", length = 20)
    private String status;

    @Size(max = 500)
    @Column(name = "CORRECTIVE_ACTION", length = 500)
    private String correctiveAction;

    @Column(name = "PENALTY_AMOUNT", precision = 12, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "RESOLUTION_DATE")
    private LocalDate resolutionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FACILITY_ID", nullable = false)
    @JsonIgnore
    private Facility facility;

    /* ── Constructors ─────────────────────────────────────────── */

    public Violation() {
    }

    public Violation(String violationCode, String severity, LocalDate violationDate,
            String status, String description) {
        this.violationCode = violationCode;
        this.severity = severity;
        this.violationDate = violationDate;
        this.status = status;
        this.description = description;
    }

    /* ── Getters & Setters ────────────────────────────────────── */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViolationCode() {
        return violationCode;
    }

    public void setViolationCode(String violationCode) {
        this.violationCode = violationCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDate getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(LocalDate violationDate) {
        this.violationDate = violationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDate resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Long getFacilityId() {
        return facility != null ? facility.getId() : null;
    }

    @Override
    public String toString() {
        return "Violation{id=" + id + ", code='" + violationCode + "', severity='" + severity + "'}";
    }
}
