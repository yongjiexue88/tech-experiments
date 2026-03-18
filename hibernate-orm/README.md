# Hibernate ORM + SQLite — Senior Interview Reference Project

> A **standalone Hibernate ORM project** (no Spring Boot) with a local SQLite database.  
> Every class is annotated with inline interview explanations — read the source code like a study guide.

---

## Quick Start

```bash
# Build and run all tests
mvn clean test

# Just compile (no tests)
mvn clean compile
```

**Requirements**: Java 17+, Maven 3.8+

The SQLite database file is auto-created at `data/interview_demo.db` when the app runs in file mode. Tests use an **in-memory** SQLite (`jdbc:sqlite::memory:`) for isolation.

---

## Project Structure

```
hibernate-orm/
├── pom.xml                          # Maven config (Hibernate 6.4, SQLite JDBC)
├── src/main/
│   ├── java/com/interview/hibernate/
│   │   ├── model/
│   │   │   ├── AuditInfo.java       # @Embeddable value object
│   │   │   ├── Department.java      # @OneToMany, cascade, lifecycle callbacks
│   │   │   ├── Employee.java        # @ManyToOne, @ManyToMany, @Version, N+1
│   │   │   ├── EmployeeStatus.java  # Enum for @Enumerated(STRING)
│   │   │   └── Project.java         # @NaturalId, @NamedQuery, inverse M:N
│   │   ├── dao/
│   │   │   ├── GenericDao.java      # Generic CRUD (persist vs save vs merge)
│   │   │   └── EmployeeDao.java     # HQL, Criteria API, Native SQL, JOIN FETCH
│   │   ├── service/
│   │   │   └── EmployeeService.java # Transactions, dirty checking, OSIV
│   │   └── util/
│   │       └── HibernateUtil.java   # SessionFactory singleton
│   └── resources/
│       ├── hibernate.cfg.xml        # Hibernate config for SQLite
│       └── logback.xml              # Logging config
└── src/test/
    └── java/com/interview/hibernate/
        └── HibernateIntegrationTest.java  # 10 integration tests
```

---

## Interview Topics → File Reference

| # | Interview Topic | File | Key Annotation/Code |
|---|----------------|------|-------------------|
| 1 | **Entity Mapping** | `Department.java`, `Employee.java` | `@Entity`, `@Table`, `@Column` |
| 2 | **ID Generation Strategies** | `Department.java` | `@GeneratedValue(IDENTITY)` — comments explain SEQUENCE vs TABLE vs AUTO |
| 3 | **Bidirectional @OneToMany** | `Department.java` ↔ `Employee.java` | `mappedBy`, owning vs inverse side |
| 4 | **@ManyToMany + @JoinTable** | `Employee.java` ↔ `Project.java` | Why `Set<>` not `List<>` for M:N |
| 5 | **Cascade Types** | `Department.java` | `CascadeType.ALL`, `orphanRemoval` |
| 6 | **@Embedded Value Objects** | `AuditInfo.java` + `Department.java` | `@Embeddable` vs `@Entity` |
| 7 | **Lifecycle Callbacks** | `Department.java` | `@PrePersist`, `@PreUpdate` |
| 8 | **@Enumerated STRING vs ORDINAL** | `Employee.java` | Why ORDINAL is dangerous |
| 9 | **@Version Optimistic Locking** | `Employee.java` | How Hibernate detects concurrent modifications |
| 10 | **Lazy vs Eager Fetching** | `Employee.java` | `FetchType.LAZY` on `@ManyToOne` (default is EAGER!) |
| 11 | **N+1 Query Problem** | `Employee.java`, `EmployeeDao.java` | Problem explanation + `JOIN FETCH` solution |
| 12 | **@NaturalId** | `Project.java` | Business key lookups with L2 cache |
| 13 | **@NamedQuery** | `Project.java` | Pre-compiled, validated-at-startup HQL |
| 14 | **equals/hashCode Contract** | `Employee.java`, `Project.java` | Never use `@GeneratedValue` ID! |
| 15 | **SessionFactory vs Session** | `HibernateUtil.java` | Heavyweight vs lightweight, thread safety |
| 16 | **persist vs save vs merge** | `GenericDao.java` | Detailed comparison of all 5 methods |
| 17 | **get() vs load()/getReference()** | `GenericDao.java` | Proxy behavior, when to use each |
| 18 | **Criteria API** | `EmployeeDao.java` | Dynamic queries with `CriteriaBuilder` |
| 19 | **HQL + Native SQL** | `EmployeeDao.java` | Three query approaches compared |
| 20 | **Pagination** | `EmployeeDao.java` | `setFirstResult/setMaxResults` |
| 21 | **Transaction Management** | `EmployeeService.java` | Programmatic vs declarative (`@Transactional`) |
| 22 | **Dirty Checking & Flush** | `EmployeeService.java` | Auto-UPDATE without explicit save |
| 23 | **Entity States** | `EmployeeService.java` | TRANSIENT → PERSISTENT → DETACHED → REMOVED |
| 24 | **Bulk HQL Updates** | `EmployeeService.java` | Bypass persistence context for performance |
| 25 | **Open Session in View** | `EmployeeService.java` | Why it's an anti-pattern |
| 26 | **hibernate.cfg.xml Config** | `hibernate.cfg.xml` | Dialect, hbm2ddl, batching, L2 cache |
| 27 | **Second-Level Cache** | `hibernate.cfg.xml` | L1 vs L2 vs query cache explained |

---

## Top Interview Questions & Where to Find the Answer

### 1. "What is the N+1 problem and how do you solve it?"
→ **`Employee.java`** (line comments) + **`EmployeeDao.findAllWithDepartment()`** (JOIN FETCH solution)

### 2. "What's the difference between `persist()`, `save()`, `merge()`, and `update()`?"
→ **`GenericDao.java`** — full comparison with when to use each

### 3. "Explain entity states in Hibernate."
→ **`EmployeeService.updateDetachedEmployee()`** — TRANSIENT → PERSISTENT → DETACHED → REMOVED

### 4. "What is dirty checking?"
→ **`EmployeeService.transferEmployee()`** — no save() call needed, Hibernate auto-generates UPDATE

### 5. "Explain optimistic vs pessimistic locking."
→ **`Employee.java`** `@Version` field — optimistic locking with version column

### 6. "What's the difference between `get()` and `load()`?"
→ **`GenericDao.java`** — proxy behavior, when to use getReference

### 7. "When would you use the Criteria API vs HQL?"
→ **`EmployeeDao.java`** — HQL for static, Criteria for dynamic queries with optional filters

### 8. "What is the Open Session in View pattern?"
→ **`EmployeeService.findEmployeeFullyLoaded()`** — explains why OSIV is an anti-pattern

### 9. "How do you implement equals/hashCode for JPA entities?"
→ **`Employee.java`** and **`Project.java`** — natural key approach, never use generated ID

### 10. "What cascade type would you use and when?"
→ **`Department.java`** — explains PERSIST, MERGE, REMOVE, ALL, orphanRemoval

---

## Design Decisions

| Decision | Rationale |
|----------|-----------|
| **No Spring Boot** | Shows you understand raw Hibernate, not just Spring auto-configuration |
| **SQLite** | Zero-setup local database; portable; great for demos |
| **Hibernate 6.4** | Latest major version with Jakarta Persistence 3.1 |
| **In-memory SQLite for tests** | Fast, isolated, no cleanup needed |
| **Set for @ManyToMany** | Avoids Hibernate's inefficient List behavior (delete-all-reinsert) |

---

## Entity Relationship Diagram

```
┌─────────────────┐        ┌─────────────────────┐        ┌──────────────────┐
│   Department    │        │      Employee        │        │     Project      │
├─────────────────┤        ├─────────────────────┤        ├──────────────────┤
│ id         (PK) │◄──────┐│ id            (PK)  │┌──────►│ id          (PK) │
│ name            │  1:N  ││ first_name          ││  M:N  │ project_code(NK) │
│ description     │       ││ last_name           ││       │ name             │
│ created_at  (E) │       ││ email         (UK)  ││       │ start_date       │
│ updated_at  (E) │       ││ salary              ││       │ end_date         │
└─────────────────┘       ││ status        (ENUM)││       │ active           │
                          ││ dept_id       (FK)──┘│       └──────────────────┘
                          ││ version       (OL)  ││
                          │└─────────────────────┘│
                          │                       │
                          │  employee_projects    │
                          │  ┌──────────────────┐ │
                          └──│ employee_id (FK) │ │
                             │ project_id  (FK) │─┘
                             └──────────────────┘

(E) = @Embedded column    (NK) = @NaturalId
(UK) = Unique key          (OL) = Optimistic Lock @Version
```
