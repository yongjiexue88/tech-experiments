package com.tceq.compliance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Record of a compliance inspection conducted at a facility.
 *
 * <p>
 * Inspections may be routine, complaint-driven, or follow-up actions.
 * Results are categorized as PASS, FAIL, or PENDING.
 */
@Entity
@Table(name = "INSPECTIONS", indexes = {
        @Index(name = "IDX_INSPECTION_DATE", columnList = "inspectionDate"),
        @Index(name = "IDX_INSPECTION_RESULT", columnList = "result")
})
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inspection_seq")
    @SequenceGenerator(name = "inspection_seq", sequenceName = "INSPECTION_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "INSPECTION_DATE", nullable = false)
    private LocalDate inspectionDate;

    @Size(max = 30)
    @Column(name = "INSPECTION_TYPE", length = 30)
    private String inspectionType;

    @NotBlank
    @Size(max = 100)
    @Column(name = "INSPECTOR_NAME", nullable = false, length = 100)
    private String inspectorName;

    @Size(max = 1000)
    @Column(name = "FINDINGS", length = 1000)
    private String findings;

    @Size(max = 20)
    @Column(name = "RESULT", length = 20)
    private String result;

    @Column(name = "NEXT_INSPECTION_DUE")
    private LocalDate nextInspectionDue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FACILITY_ID", nullable = false)
    @JsonIgnore
    private Facility facility;

    /* ── Constructors ─────────────────────────────────────────── */

    public Inspection() {
    }

    public Inspection(LocalDate inspectionDate, String inspectionType,
            String inspectorName, String result) {
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.inspectorName = inspectorName;
        this.result = result;
    }

    /* ── Getters & Setters ────────────────────────────────────── */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDate getNextInspectionDue() {
        return nextInspectionDue;
    }

    public void setNextInspectionDue(LocalDate nextInspectionDue) {
        this.nextInspectionDue = nextInspectionDue;
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
        return "Inspection{id=" + id + ", date=" + inspectionDate + ", result='" + result + "'}";
    }
}
