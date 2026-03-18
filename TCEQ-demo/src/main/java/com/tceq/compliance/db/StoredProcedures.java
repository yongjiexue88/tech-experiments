package com.tceq.compliance.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Java implementations of PL/SQL stored procedures for use with H2's
 * {@code CREATE ALIAS} mechanism.
 *
 * <p>
 * These mirror the Oracle PL/SQL procedures defined in
 * {@code procedures.sql} and allow the demo to run without Oracle.
 */
public class StoredProcedures {

    /**
     * Calculates a compliance score (0–100) for a facility.
     *
     * @param conn       JDBC connection (injected by H2)
     * @param facilityId the facility ID
     * @return compliance score
     */
    public static int calculateComplianceScore(Connection conn, long facilityId)
            throws SQLException {
        int score = 100;

        // Count open violations by severity
        String violationSql = """
                SELECT severity, COUNT(*) AS cnt
                FROM violations
                WHERE facility_id = ? AND status = 'OPEN'
                GROUP BY severity
                """;

        try (PreparedStatement ps = conn.prepareStatement(violationSql)) {
            ps.setLong(1, facilityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String severity = rs.getString("severity");
                    int count = rs.getInt("cnt");
                    score -= switch (severity) {
                        case "CRITICAL" -> count * 30;
                        case "MAJOR" -> count * 15;
                        default -> count * 5;
                    };
                }
            }
        }

        // Count expired permits
        String permitSql = """
                SELECT COUNT(*) AS cnt FROM permits
                WHERE facility_id = ? AND status = 'ACTIVE' AND expiry_date < CURRENT_DATE
                """;

        try (PreparedStatement ps = conn.prepareStatement(permitSql)) {
            ps.setLong(1, facilityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    score -= rs.getInt("cnt") * 10;
                }
            }
        }

        // Count overdue inspections
        String inspectionSql = """
                SELECT COUNT(*) AS cnt FROM inspections
                WHERE facility_id = ? AND next_inspection_due < CURRENT_DATE
                """;

        try (PreparedStatement ps = conn.prepareStatement(inspectionSql)) {
            ps.setLong(1, facilityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    score -= rs.getInt("cnt") * 5;
                }
            }
        }

        return Math.max(0, score);
    }
}
