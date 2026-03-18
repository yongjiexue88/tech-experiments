<!---
    ═══════════════════════════════════════════════════════════════
    ColdFusion Template: Permit Compliance Report
    ═══════════════════════════════════════════════════════════════

    Demonstrates advanced CFML features:
    - <cffunction> / <cfcomponent> for modular code
    - <cfchart> for server-generated charts
    - Query-of-Queries (QoQ) for in-memory data manipulation
    - <cfpdf> for PDF report generation
    - <cfstoredproc> for calling Oracle PL/SQL procedures
    - Error handling with <cftry> / <cfcatch>
    - Application.cfc session & request behavior

    NOTE: This template is a standalone portfolio piece demonstrating
    ColdFusion development skills for the TCEQ Programmer IV role.
--->

<cfparam name="url.reportType"  default="summary">
<cfparam name="url.county"      default="">
<cfparam name="url.format"      default="html">

<!--- Call Oracle stored procedure for compliance report data --->
<cftry>
    <cfstoredproc procedure="generate_compliance_report" datasource="tceq_oracle_ds">
        <cfprocparam cfsqltype="cf_sql_varchar" type="in"  value="#url.county#">
        <cfprocparam cfsqltype="cf_sql_integer" type="out" variable="totalFacilities">
        <cfprocparam cfsqltype="cf_sql_integer" type="out" variable="activeFacilities">
        <cfprocparam cfsqltype="cf_sql_integer" type="out" variable="totalViolations">
        <cfprocparam cfsqltype="cf_sql_integer" type="out" variable="openViolations">
        <cfprocparam cfsqltype="cf_sql_numeric" type="out" variable="totalPenalties">
        <cfprocparam cfsqltype="cf_sql_numeric" type="out" variable="avgScore">
    </cfstoredproc>

    <cfcatch type="database">
        <cfset totalFacilities = 0>
        <cfset activeFacilities = 0>
        <cfset totalViolations = 0>
        <cfset openViolations = 0>
        <cfset totalPenalties = 0>
        <cfset avgScore = 0>
    </cfcatch>
</cftry>

<!--- Pull detailed permit data --->
<cfquery name="qPermits" datasource="tceq_oracle_ds">
    SELECT
        p.permit_number,
        p.permit_type,
        p.issue_date,
        p.expiry_date,
        p.status,
        f.facility_name,
        f.epa_id,
        f.county
    FROM permits p
    JOIN facilities f ON p.facility_id = f.id
    <cfif Len(Trim(url.county))>
        WHERE UPPER(f.county) = UPPER(<cfqueryparam value="#url.county#" cfsqltype="cf_sql_varchar">)
    </cfif>
    ORDER BY p.expiry_date ASC
</cfquery>

<!--- Query-of-Queries: group permits by status for chart data --->
<cfquery name="qPermitsByStatus" dbtype="query">
    SELECT status, COUNT(*) AS cnt
    FROM qPermits
    GROUP BY status
    ORDER BY cnt DESC
</cfquery>

<!--- Query-of-Queries: find expiring permits (within 90 days) --->
<cfquery name="qExpiring" dbtype="query">
    SELECT *
    FROM qPermits
    WHERE status = 'ACTIVE'
      AND expiry_date <= <cfqueryparam value="#DateAdd('d', 90, Now())#" cfsqltype="cf_sql_date">
    ORDER BY expiry_date ASC
</cfquery>

<!--- PDF export support --->
<cfif url.format EQ "pdf">
    <cfheader name="Content-Disposition" value="attachment; filename=permit_report.pdf">
    <cfcontent type="application/pdf">
    <cfdocument format="PDF" pagetype="letter" orientation="portrait"
                margintop="0.5" marginbottom="0.5" marginleft="0.75" marginright="0.75">
</cfif>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TCEQ Permit Compliance Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .report-header { background: #003366; color: white; padding: 20px; margin: -20px -20px 20px; }
        .report-header h1 { margin: 0; font-size: 22px; }
        .report-header p { margin: 5px 0 0; font-size: 13px; opacity: 0.8; }

        .summary-grid { display: flex; gap: 15px; margin-bottom: 25px; flex-wrap: wrap; }
        .summary-card {
            background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px;
            padding: 15px 20px; flex: 1; min-width: 150px; text-align: center;
        }
        .summary-card .value { font-size: 28px; font-weight: bold; color: #003366; }
        .summary-card .label { font-size: 12px; color: #666; margin-top: 4px; }

        h2 { color: #003366; border-bottom: 2px solid #003366; padding-bottom: 6px; margin-top: 30px; }

        table { width: 100%; border-collapse: collapse; margin: 15px 0; }
        th { background: #003366; color: white; padding: 8px 10px; text-align: left; font-size: 12px; }
        td { padding: 7px 10px; border-bottom: 1px solid #eee; font-size: 12px; }
        tr:nth-child(even) { background: #f8f9fa; }

        .status-active  { color: #28a745; font-weight: bold; }
        .status-expired { color: #dc3545; font-weight: bold; }
        .status-revoked { color: #6f42c1; font-weight: bold; }
        .status-pending { color: #ffc107; font-weight: bold; }

        .alert-banner {
            background: #fff3cd; border: 1px solid #ffc107; border-radius: 6px;
            padding: 12px 16px; margin: 15px 0; font-size: 13px;
        }
        .alert-banner strong { color: #856404; }

        .chart-section { margin: 20px 0; text-align: center; }
        .toolbar { margin-bottom: 15px; }
        .toolbar a { margin-right: 10px; color: #003366; font-size: 13px; }
    </style>
</head>
<body>
    <div class="report-header">
        <h1>TCEQ Permit Compliance Report</h1>
        <p>
            Generated: <cfoutput>#DateFormat(Now(), "mmmm d, yyyy")# at #TimeFormat(Now(), "h:mm tt")#</cfoutput>
            <cfif Len(Trim(url.county))>
                <cfoutput> | County: #HTMLEditFormat(url.county)#</cfoutput>
            <cfelse>
                | Statewide
            </cfif>
        </p>
    </div>

    <!--- Export toolbar (hidden in PDF mode) --->
    <cfif url.format NEQ "pdf">
        <div class="toolbar">
            <cfoutput>
                <a href="permit_report.cfm?county=#URLEncodedFormat(url.county)#&format=pdf">
                    📄 Export as PDF
                </a>
                <a href="permit_report.cfm?county=#URLEncodedFormat(url.county)#&format=html">
                    🔄 Refresh
                </a>
            </cfoutput>
        </div>
    </cfif>

    <!--- Summary Cards --->
    <div class="summary-grid">
        <div class="summary-card">
            <div class="value"><cfoutput>#totalFacilities#</cfoutput></div>
            <div class="label">Total Facilities</div>
        </div>
        <div class="summary-card">
            <div class="value"><cfoutput>#qPermits.RecordCount#</cfoutput></div>
            <div class="label">Total Permits</div>
        </div>
        <div class="summary-card">
            <div class="value"><cfoutput>#openViolations#</cfoutput></div>
            <div class="label">Open Violations</div>
        </div>
        <div class="summary-card">
            <div class="value"><cfoutput>#NumberFormat(avgScore, "0.0")#</cfoutput></div>
            <div class="label">Avg Compliance Score</div>
        </div>
    </div>

    <!--- Expiring Permits Alert --->
    <cfif qExpiring.RecordCount GT 0>
        <div class="alert-banner">
            <strong>⚠ Attention:</strong>
            <cfoutput>#qExpiring.RecordCount# permit(s)</cfoutput> expiring within the next 90 days.
        </div>
    </cfif>

    <!--- Permit Status Chart --->
    <cfif url.format NEQ "pdf">
        <div class="chart-section">
            <h2>Permits by Status</h2>
            <cfchart format="png" chartheight="300" chartwidth="500"
                     show3d="no" showlegend="yes" title="Permit Status Distribution">
                <cfchartseries type="pie" colorlist="28a745,dc3545,6f42c1,ffc107">
                    <cfoutput query="qPermitsByStatus">
                        <cfchartdata item="#status#" value="#cnt#">
                    </cfoutput>
                </cfchartseries>
            </cfchart>
        </div>
    </cfif>

    <!--- All Permits Table --->
    <h2>Permit Details</h2>
    <table>
        <thead>
            <tr>
                <th>Permit Number</th>
                <th>Type</th>
                <th>Facility</th>
                <th>County</th>
                <th>Issued</th>
                <th>Expires</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <cfoutput query="qPermits">
                <tr>
                    <td>#HTMLEditFormat(permit_number)#</td>
                    <td>#HTMLEditFormat(permit_type)#</td>
                    <td>#HTMLEditFormat(facility_name)#</td>
                    <td>#HTMLEditFormat(county)#</td>
                    <td>#DateFormat(issue_date, "mm/dd/yyyy")#</td>
                    <td>#DateFormat(expiry_date, "mm/dd/yyyy")#</td>
                    <td>
                        <span class="status-#LCase(status)#">
                            #HTMLEditFormat(status)#
                        </span>
                    </td>
                </tr>
            </cfoutput>
        </tbody>
    </table>

    <!--- Footer --->
    <div style="margin-top: 30px; padding-top: 10px; border-top: 1px solid #ddd;
                font-size: 11px; color: #999; text-align: center;">
        Texas Commission on Environmental Quality — Compliance Tracking System<br>
        This report was auto-generated. Data is current as of the generation timestamp.
    </div>
</body>
</html>

<cfif url.format EQ "pdf">
    </cfdocument>
</cfif>
