# TCEQ Environmental Compliance Tracking System

A full-stack demo application for tracking environmental permits, inspections, and compliance violations across Texas facilities — built for the **TCEQ Programmer IV** tech stack.

## Tech Stack Mapping

| Job Requirement | Implementation |
|---|---|
| **Java** | Spring Boot 3.2 backend, service layer, REST controllers |
| **Hibernate** | JPA entities with annotations + XML mapping (`Facility.hbm.xml`) |
| **Oracle / SQL** | H2 in Oracle mode — sequences, VARCHAR2, CHECK constraints |
| **PL/SQL** | Stored procedures for compliance scoring & batch updates |
| **JavaScript** | Dynamic dashboard with Chart.js, fetch API, DOM manipulation |
| **CSS** | TCEQ-branded responsive dark-theme design with animations |
| **HTML** | SPA dashboard + Thymeleaf server-rendered pages |
| **XML** | `hibernate.cfg.xml`, `Facility.hbm.xml`, `pom.xml` |
| **ColdFusion** | `cfquery`, `cfstoredproc`, `cfchart`, QoQ, PDF export |

## Quick Start

```bash
# Prerequisites: Java 17+
# No database setup needed — uses H2 in-memory

# 1. Build
./mvnw clean compile

# 2. Run tests
./mvnw test

# 3. Start the application
./mvnw spring-boot:run

# 4. Open browser
open http://localhost:8080
```

## Application URLs

| URL | Description |
|---|---|
| `http://localhost:8080` | SPA Dashboard (JavaScript) |
| `http://localhost:8080/dashboard` | Server-rendered Dashboard (Thymeleaf) |
| `http://localhost:8080/api/facilities` | REST API — Facilities |
| `http://localhost:8080/api/dashboard/stats` | REST API — Dashboard Stats |
| `http://localhost:8080/h2-console` | H2 Database Console |

> **H2 Console connection:** JDBC URL = `jdbc:h2:mem:tceqdb;MODE=Oracle`, User = `tceq_admin`, Password = `tceq_pass`

## Project Structure

```
TCEQ-demo/
├── pom.xml                              # Maven build
├── src/main/java/com/tceq/compliance/
│   ├── TceqApplication.java            # Spring Boot entry
│   ├── model/                           # JPA/Hibernate entities
│   │   ├── Facility.java               #   → Regulated site
│   │   ├── Permit.java                 #   → Environmental permit
│   │   ├── Inspection.java             #   → Inspection record
│   │   └── Violation.java              #   → Compliance violation
│   ├── repository/                      # Spring Data JPA repos
│   ├── service/ComplianceService.java   # Business logic + scoring
│   ├── controller/
│   │   ├── ApiController.java          # REST API endpoints
│   │   └── DashboardController.java    # Thymeleaf MVC
│   └── db/StoredProcedures.java        # H2 alias for PL/SQL
├── src/main/resources/
│   ├── application.properties           # Config
│   ├── hibernate.cfg.xml                # Traditional Hibernate XML
│   ├── orm/Facility.hbm.xml            # XML entity mapping
│   ├── db/
│   │   ├── schema.sql                  # Oracle-style DDL
│   │   ├── data.sql                    # Seed data
│   │   └── procedures.sql             # PL/SQL procedures
│   ├── static/                          # Frontend (HTML/CSS/JS)
│   └── templates/dashboard.html         # Thymeleaf template
├── src/test/                            # JUnit 5 tests
├── coldfusion/                          # CFML portfolio samples
│   ├── facility_search.cfm
│   └── permit_report.cfm
└── docs/architecture.md
```

## Key Features

- **Compliance Dashboard** — Real-time statistics, Chart.js visualizations, searchable facility table
- **Compliance Scoring** — Automated 0-100 scoring based on violations, expired permits, overdue inspections
- **Oracle-Compatible SQL** — DDL with sequences, constraints, and PL/SQL stored procedures
- **Dual Rendering** — SPA (JavaScript) + SSR (Thymeleaf) dashboards
- **ColdFusion Portfolio** — Production-quality CFML templates with cfquery, cfstoredproc, cfchart

## Database Schema

```
FACILITIES ──┬── PERMITS (1:N)
              ├── INSPECTIONS (1:N)
              └── VIOLATIONS (1:N)
```

All tables use Oracle-compatible syntax: `CREATE SEQUENCE`, `VARCHAR2`, `NUMBER`, `DATE`, and `CHECK` constraints.
