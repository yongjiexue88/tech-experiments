# Architecture Overview

## System Architecture

```
┌──────────────────────────────────────────────────────────┐
│                      Client Layer                        │
├────────────────────┬─────────────────────────────────────┤
│   SPA Dashboard    │   Server-Rendered Dashboard         │
│   (HTML/CSS/JS)    │   (Thymeleaf)                       │
│   Chart.js         │   /dashboard                        │
│   Fetch API        │                                     │
└────────┬───────────┴──────────────┬──────────────────────┘
         │ REST JSON                │ MVC Model
┌────────┴──────────────────────────┴──────────────────────┐
│                  Spring Boot 3.2                         │
├──────────────────────────────────────────────────────────┤
│  ApiController.java          DashboardController.java    │
│  /api/facilities             /dashboard                  │
│  /api/dashboard/stats                                    │
├──────────────────────────────────────────────────────────┤
│                ComplianceService.java                    │
│  - getDashboardStats()    - calculateComplianceScore()   │
│  - getFacilitySummaries() - getViolationsBySeverity()    │
├──────────────────────────────────────────────────────────┤
│          Spring Data JPA Repositories                    │
│  FacilityRepo  PermitRepo  InspectionRepo  ViolationRepo│
├──────────────────────────────────────────────────────────┤
│              Hibernate ORM (JPA + XML)                   │
│  @Entity annotations  +  Facility.hbm.xml               │
├──────────────────────────────────────────────────────────┤
│            H2 Database (MODE=Oracle)                     │
│  Sequences │ VARCHAR2 │ CHECK constraints │ PL/SQL       │
└──────────────────────────────────────────────────────────┘
```

## Data Model

```
┌─────────────────┐
│   FACILITIES    │
├─────────────────┤
│ id (PK, SEQ)    │
│ facility_name   │──┐
│ epa_id (UK)     │  │
│ facility_type   │  │     ┌──────────────────┐
│ city, county    │  ├────>│     PERMITS      │
│ status          │  │     ├──────────────────┤
│ lat, lon        │  │     │ permit_number    │
└─────────────────┘  │     │ permit_type      │
                     │     │ issue_date       │
                     │     │ expiry_date      │
                     │     │ status           │
                     │     └──────────────────┘
                     │
                     │     ┌──────────────────┐
                     ├────>│   INSPECTIONS    │
                     │     ├──────────────────┤
                     │     │ inspection_date  │
                     │     │ inspector_name   │
                     │     │ findings         │
                     │     │ result           │
                     │     └──────────────────┘
                     │
                     │     ┌──────────────────┐
                     └────>│   VIOLATIONS     │
                           ├──────────────────┤
                           │ violation_code   │
                           │ severity         │
                           │ penalty_amount   │
                           │ corrective_action│
                           └──────────────────┘
```

## Compliance Scoring Algorithm

Score starts at 100 and deducts points:

| Condition | Deduction |
|---|---|
| Each open MINOR violation | -5 |
| Each open MAJOR violation | -15 |
| Each open CRITICAL violation | -30 |
| Each expired active permit | -10 |
| Each overdue inspection | -5 |

Minimum score is clamped to 0.

## Technology Decisions

| Decision | Rationale |
|---|---|
| H2 in Oracle mode | Zero-setup local development. DDL uses Oracle syntax (VARCHAR2, NUMBER, sequences). |
| Both annotation + XML mappings | Demonstrates proficiency with modern and legacy Hibernate approaches. |
| SPA + SSR dashboards | Shows JavaScript frontend AND Thymeleaf server-rendering skills. |
| ColdFusion as standalone templates | Showcases CFML knowledge without requiring a CF server for the demo. |
