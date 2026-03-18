-- ═══════════════════════════════════════════════════════════════
-- TCEQ Environmental Compliance Tracker — PL/SQL Procedures
-- Oracle-compatible stored procedures and functions
-- NOTE: These are written in Oracle PL/SQL syntax to demonstrate
--       proficiency. They run on H2 in Oracle compatibility mode.
-- ═══════════════════════════════════════════════════════════════

-- ──────────────────────────────────────────────────────────────
-- PROCEDURE: calculate_compliance_score
-- Computes a compliance score (0-100) for a given facility based
-- on open violations, expired permits, and overdue inspections.
-- ──────────────────────────────────────────────────────────────
CREATE ALIAS IF NOT EXISTS CALCULATE_COMPLIANCE_SCORE FOR "com.tceq.compliance.db.StoredProcedures.calculateComplianceScore";

-- ──────────────────────────────────────────────────────────────
-- The following blocks show the ORACLE PL/SQL equivalents of the
-- stored procedures that would be used in a production Oracle DB.
-- They are preserved here as a reference / portfolio showcase.
-- ──────────────────────────────────────────────────────────────

/*
-- Oracle PL/SQL version of calculate_compliance_score
CREATE OR REPLACE FUNCTION calculate_compliance_score(
    p_facility_id IN NUMBER
) RETURN NUMBER
IS
    v_score       NUMBER := 100;
    v_minor_ct    NUMBER := 0;
    v_major_ct    NUMBER := 0;
    v_critical_ct NUMBER := 0;
    v_expired_ct  NUMBER := 0;
    v_overdue_ct  NUMBER := 0;
BEGIN
    -- Count open violations by severity
    SELECT
        NVL(SUM(CASE WHEN severity = 'MINOR'    THEN 1 ELSE 0 END), 0),
        NVL(SUM(CASE WHEN severity = 'MAJOR'    THEN 1 ELSE 0 END), 0),
        NVL(SUM(CASE WHEN severity = 'CRITICAL' THEN 1 ELSE 0 END), 0)
    INTO v_minor_ct, v_major_ct, v_critical_ct
    FROM violations
    WHERE facility_id = p_facility_id
      AND status = 'OPEN';

    -- Deduct points by severity
    v_score := v_score - (v_minor_ct * 5) - (v_major_ct * 15) - (v_critical_ct * 30);

    -- Count expired active permits
    SELECT COUNT(*) INTO v_expired_ct
    FROM permits
    WHERE facility_id = p_facility_id
      AND status = 'ACTIVE'
      AND expiry_date < SYSDATE;

    v_score := v_score - (v_expired_ct * 10);

    -- Count overdue inspections
    SELECT COUNT(*) INTO v_overdue_ct
    FROM inspections
    WHERE facility_id = p_facility_id
      AND next_inspection_due < SYSDATE;

    v_score := v_score - (v_overdue_ct * 5);

    -- Clamp to minimum of 0
    IF v_score < 0 THEN
        v_score := 0;
    END IF;

    RETURN v_score;
END calculate_compliance_score;
/

-- ──────────────────────────────────────────────────────────────
-- PROCEDURE: get_overdue_inspections
-- Returns a cursor of facilities that are past their next
-- inspection due date, ordered by how overdue they are.
-- ──────────────────────────────────────────────────────────────
CREATE OR REPLACE PROCEDURE get_overdue_inspections(
    p_results OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_results FOR
        SELECT
            f.id              AS facility_id,
            f.facility_name,
            f.epa_id,
            i.inspection_date AS last_inspection,
            i.next_inspection_due,
            TRUNC(SYSDATE - i.next_inspection_due) AS days_overdue
        FROM facilities f
        JOIN inspections i ON f.id = i.facility_id
        WHERE i.next_inspection_due < SYSDATE
        ORDER BY i.next_inspection_due ASC;
END get_overdue_inspections;
/

-- ──────────────────────────────────────────────────────────────
-- PROCEDURE: update_permit_status
-- Batch updates permits whose expiry date has passed from ACTIVE
-- to EXPIRED.  Returns the number of permits updated.
-- ──────────────────────────────────────────────────────────────
CREATE OR REPLACE PROCEDURE update_permit_status(
    p_updated_count OUT NUMBER
)
IS
BEGIN
    UPDATE permits
    SET    status = 'EXPIRED'
    WHERE  status = 'ACTIVE'
      AND  expiry_date < SYSDATE;

    p_updated_count := SQL%ROWCOUNT;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_permit_status;
/

-- ──────────────────────────────────────────────────────────────
-- PROCEDURE: generate_compliance_report
-- Generates a compliance summary for a given county, producing
-- aggregate metrics that would be used in management reports.
-- ──────────────────────────────────────────────────────────────
CREATE OR REPLACE PROCEDURE generate_compliance_report(
    p_county       IN  VARCHAR2,
    p_total_fac    OUT NUMBER,
    p_active_fac   OUT NUMBER,
    p_total_viol   OUT NUMBER,
    p_open_viol    OUT NUMBER,
    p_total_pen    OUT NUMBER,
    p_avg_score    OUT NUMBER
)
IS
BEGIN
    -- Facility counts
    SELECT COUNT(*),
           SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END)
    INTO   p_total_fac, p_active_fac
    FROM   facilities
    WHERE  UPPER(county) = UPPER(p_county);

    -- Violation counts
    SELECT COUNT(*),
           SUM(CASE WHEN v.status = 'OPEN' THEN 1 ELSE 0 END),
           NVL(SUM(v.penalty_amount), 0)
    INTO   p_total_viol, p_open_viol, p_total_pen
    FROM   violations v
    JOIN   facilities f ON v.facility_id = f.id
    WHERE  UPPER(f.county) = UPPER(p_county);

    -- Average compliance score across county facilities
    SELECT NVL(AVG(calculate_compliance_score(id)), 100)
    INTO   p_avg_score
    FROM   facilities
    WHERE  UPPER(county) = UPPER(p_county);
END generate_compliance_report;
/
*/
