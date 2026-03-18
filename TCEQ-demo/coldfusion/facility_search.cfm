<!---
    ═══════════════════════════════════════════════════════════════
    ColdFusion Template: Facility Search
    ═══════════════════════════════════════════════════════════════

    Demonstrates CFML proficiency with:
    - <cfparam> for request parameter defaults
    - <cfquery> with Oracle datasource & parameterized queries
    - <cfoutput> / <cfloop> for dynamic HTML generation
    - <cfif> conditional logic
    - ColdFusion functions (Len, HTMLEditFormat, URLEncodedFormat)

    NOTE: This template is a standalone portfolio piece. It does not
    run within the Spring Boot application but demonstrates ColdFusion
    development skills expected for the TCEQ Programmer IV role.
--->

<!--- Default search parameters --->
<cfparam name="url.searchTerm"    default="">
<cfparam name="url.facilityType"  default="">
<cfparam name="url.county"        default="">
<cfparam name="url.status"        default="ACTIVE">
<cfparam name="url.pageNum"       default="1">
<cfset   pageSize = 25>
<cfset   startRow = ((url.pageNum - 1) * pageSize) + 1>

<!--- Query the database for matching facilities --->
<cfquery name="qFacilities" datasource="tceq_oracle_ds" maxrows="500">
    SELECT
        f.id,
        f.facility_name,
        f.epa_id,
        f.facility_type,
        f.address,
        f.city,
        f.county,
        f.state_code,
        f.zip_code,
        f.status,
        f.registration_date,
        (SELECT COUNT(*) FROM permits p WHERE p.facility_id = f.id) AS permit_count,
        (SELECT COUNT(*) FROM violations v WHERE v.facility_id = f.id AND v.status = 'OPEN') AS open_violations
    FROM facilities f
    WHERE 1 = 1

    <!--- Dynamic search conditions --->
    <cfif Len(Trim(url.searchTerm))>
        AND (
            UPPER(f.facility_name) LIKE UPPER(<cfqueryparam value="%#url.searchTerm#%" cfsqltype="cf_sql_varchar">)
            OR UPPER(f.epa_id) LIKE UPPER(<cfqueryparam value="%#url.searchTerm#%" cfsqltype="cf_sql_varchar">)
        )
    </cfif>

    <cfif Len(Trim(url.facilityType))>
        AND f.facility_type = <cfqueryparam value="#url.facilityType#" cfsqltype="cf_sql_varchar">
    </cfif>

    <cfif Len(Trim(url.county))>
        AND UPPER(f.county) = UPPER(<cfqueryparam value="#url.county#" cfsqltype="cf_sql_varchar">)
    </cfif>

    <cfif Len(Trim(url.status))>
        AND f.status = <cfqueryparam value="#url.status#" cfsqltype="cf_sql_varchar">
    </cfif>

    ORDER BY f.facility_name ASC
</cfquery>

<!--- Get distinct values for filter dropdowns --->
<cfquery name="qFacilityTypes" datasource="tceq_oracle_ds">
    SELECT DISTINCT facility_type
    FROM facilities
    WHERE facility_type IS NOT NULL
    ORDER BY facility_type
</cfquery>

<cfquery name="qCounties" datasource="tceq_oracle_ds">
    SELECT DISTINCT county
    FROM facilities
    WHERE county IS NOT NULL
    ORDER BY county
</cfquery>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TCEQ Facility Search</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }
        .header { background: #003366; color: white; padding: 15px 20px; margin: -20px -20px 20px; }
        .header h1 { margin: 0; font-size: 20px; }
        .search-form { background: white; padding: 20px; border: 1px solid #ddd; margin-bottom: 20px; }
        .form-row { display: flex; gap: 15px; align-items: flex-end; flex-wrap: wrap; }
        .form-group { display: flex; flex-direction: column; gap: 4px; }
        .form-group label { font-weight: bold; font-size: 13px; color: #333; }
        .form-group input, .form-group select {
            padding: 8px 12px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px;
        }
        .btn-search { background: #003366; color: white; padding: 8px 24px; border: none;
                       border-radius: 4px; cursor: pointer; font-size: 14px; }
        .btn-search:hover { background: #004488; }
        .results-info { margin-bottom: 10px; color: #666; font-size: 14px; }
        table { width: 100%; border-collapse: collapse; background: white; }
        th { background: #003366; color: white; padding: 10px; text-align: left; font-size: 13px; }
        td { padding: 8px 10px; border-bottom: 1px solid #eee; font-size: 13px; }
        tr:hover { background: #f0f7ff; }
        .status-active { color: #28a745; font-weight: bold; }
        .status-suspended { color: #ffc107; font-weight: bold; }
        .status-inactive { color: #dc3545; font-weight: bold; }
        .violations-alert { color: #dc3545; font-weight: bold; }
        a { color: #003366; }
    </style>
</head>
<body>
    <div class="header">
        <h1>TCEQ Facility Search</h1>
    </div>

    <!--- Search Form --->
    <div class="search-form">
        <form method="get" action="facility_search.cfm">
            <div class="form-row">
                <div class="form-group">
                    <label for="searchTerm">Search:</label>
                    <input type="text" id="searchTerm" name="searchTerm"
                           value="<cfoutput>#HTMLEditFormat(url.searchTerm)#</cfoutput>"
                           placeholder="Facility name or EPA ID">
                </div>

                <div class="form-group">
                    <label for="facilityType">Type:</label>
                    <select id="facilityType" name="facilityType">
                        <option value="">All Types</option>
                        <cfoutput query="qFacilityTypes">
                            <option value="#facility_type#"
                                <cfif url.facilityType EQ facility_type>selected</cfif>>
                                #HTMLEditFormat(facility_type)#
                            </option>
                        </cfoutput>
                    </select>
                </div>

                <div class="form-group">
                    <label for="county">County:</label>
                    <select id="county" name="county">
                        <option value="">All Counties</option>
                        <cfoutput query="qCounties">
                            <option value="#county#"
                                <cfif url.county EQ county>selected</cfif>>
                                #HTMLEditFormat(county)#
                            </option>
                        </cfoutput>
                    </select>
                </div>

                <div class="form-group">
                    <label for="status">Status:</label>
                    <select id="status" name="status">
                        <option value="">All Statuses</option>
                        <cfloop list="ACTIVE,INACTIVE,SUSPENDED,CLOSED" index="s">
                            <cfoutput>
                                <option value="#s#" <cfif url.status EQ s>selected</cfif>>
                                    #s#
                                </option>
                            </cfoutput>
                        </cfloop>
                    </select>
                </div>

                <div class="form-group">
                    <button type="submit" class="btn-search">Search</button>
                </div>
            </div>
        </form>
    </div>

    <!--- Results --->
    <div class="results-info">
        <cfoutput>Found <strong>#qFacilities.RecordCount#</strong> facilities</cfoutput>
        <cfif Len(Trim(url.searchTerm))>
            <cfoutput> matching "<em>#HTMLEditFormat(url.searchTerm)#</em>"</cfoutput>
        </cfif>
    </div>

    <table>
        <thead>
            <tr>
                <th>Facility Name</th>
                <th>EPA ID</th>
                <th>Type</th>
                <th>City</th>
                <th>County</th>
                <th>Status</th>
                <th>Permits</th>
                <th>Open Violations</th>
            </tr>
        </thead>
        <tbody>
            <cfoutput query="qFacilities" startrow="#startRow#" maxrows="#pageSize#">
                <tr>
                    <td>
                        <a href="facility_detail.cfm?id=#id#">
                            #HTMLEditFormat(facility_name)#
                        </a>
                    </td>
                    <td>#HTMLEditFormat(epa_id)#</td>
                    <td>#HTMLEditFormat(facility_type)#</td>
                    <td>#HTMLEditFormat(city)#</td>
                    <td>#HTMLEditFormat(county)#</td>
                    <td>
                        <span class="status-#LCase(status)#">
                            #HTMLEditFormat(status)#
                        </span>
                    </td>
                    <td>#permit_count#</td>
                    <td>
                        <cfif open_violations GT 0>
                            <span class="violations-alert">#open_violations#</span>
                        <cfelse>
                            0
                        </cfif>
                    </td>
                </tr>
            </cfoutput>
        </tbody>
    </table>

    <!--- Pagination --->
    <cfset totalPages = Ceiling(qFacilities.RecordCount / pageSize)>
    <cfif totalPages GT 1>
        <div style="margin-top: 15px; text-align: center;">
            <cfloop from="1" to="#totalPages#" index="pg">
                <cfoutput>
                    <cfif pg EQ url.pageNum>
                        <strong>[#pg#]</strong>
                    <cfelse>
                        <a href="facility_search.cfm?searchTerm=#URLEncodedFormat(url.searchTerm)#&facilityType=#URLEncodedFormat(url.facilityType)#&county=#URLEncodedFormat(url.county)#&status=#URLEncodedFormat(url.status)#&pageNum=#pg#">
                            [#pg#]
                        </a>
                    </cfif>
                </cfoutput>
            </cfloop>
        </div>
    </cfif>
</body>
</html>
