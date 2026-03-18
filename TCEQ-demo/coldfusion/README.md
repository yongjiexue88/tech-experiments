# ColdFusion Templates — Portfolio Showcase

This directory contains ColdFusion Markup Language (CFML) templates that demonstrate proficiency with ColdFusion development — a key technology in the TCEQ Programmer IV tech stack.

## Templates

### `facility_search.cfm`
Interactive facility search page demonstrating:
- **`<cfquery>`** — Parameterized Oracle queries with dynamic WHERE clauses
- **`<cfqueryparam>`** — SQL injection prevention
- **`<cfparam>`** — Request parameter defaults and validation
- **`<cfoutput>` / `<cfloop>`** — Dynamic HTML generation
- **`HTMLEditFormat()`** — XSS prevention
- **Server-side pagination** — Row windowing with `startrow` / `maxrows`

### `permit_report.cfm`
Compliance report page demonstrating:
- **`<cfstoredproc>`** — Calling Oracle PL/SQL stored procedures with IN/OUT params
- **`<cfchart>` / `<cfchartseries>`** — Server-generated pie charts
- **Query-of-Queries (QoQ)** — In-memory SQL against existing recordsets
- **`<cfdocument>`** — PDF report generation
- **`<cftry>` / `<cfcatch>`** — Structured error handling
- **`DateFormat()` / `NumberFormat()`** — Data formatting functions

## Running These Templates

These templates require a CFML engine (Adobe ColdFusion or [Lucee](https://www.lucee.org/)) with an Oracle datasource configured as `tceq_oracle_ds`. To run locally:

```bash
# Option 1: Lucee via Docker
docker run -d -p 8888:8888 \
  -v $(pwd):/var/www/html \
  lucee/lucee:latest

# Option 2: CommandBox (CFML task runner)
box server start cfengine=lucee@5
```

In a production TCEQ environment, these would be deployed to the agency's ColdFusion application server with the Oracle datasource pre-configured in the ColdFusion Administrator.
