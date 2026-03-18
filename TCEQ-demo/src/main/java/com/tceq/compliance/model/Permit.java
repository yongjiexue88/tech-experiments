package com.tceq.compliance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Environmental permit issued to a facility by TCEQ.
 *
 * <p>
 * Permit types include Air Quality, Water Quality, Waste Management,
 * and others regulated under Texas environmental law.
 */
@Entity
@Table(name = "PERMITS", indexes = {
        @Index(name = "IDX_PERMIT_NUMBER", columnList = "permitNumber", unique = true),
        @Index(name = "IDX_PERMIT_STATUS", columnList = "status")
})
public class Permit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permit_seq")
    @SequenceGenerator(name = "permit_seq", sequenceName = "PERMIT_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "PERMIT_NUMBER", nullable = false, unique = true, length = 30)
    private String permitNumber;

    @Size(max = 50)
    @Column(name = "PERMIT_TYPE", length = 50)
    private String permitType;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Size(max = 20)
    @Column(name = "STATUS", length = 20)
    private String status;

    @Size(max = 500)
    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FACILITY_ID", nullable = false)
    @JsonIgnore
    private Facility facility;

    /* ── Constructors ─────────────────────────────────────────── */

    public Permit() {
    }

    public Permit(String permitNumber, String permitType, LocalDate issueDate,
            LocalDate expiryDate, String status) {
        this.permitNumber = permitNumber;
        this.permitType = permitType;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    /* ── Getters & Setters ────────────────────────────────────── */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public String getPermitType() {
        return permitType;
    }

    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Convenience: get the parent facility's ID without loading the full entity.
     */
    public Long getFacilityId() {
        return facility != null ? facility.getId() : null;
    }

    @Override
    public String toString() {
        return "Permit{id=" + id + ", number='" + permitNumber + "', type='" + permitType + "'}";
    }
}
