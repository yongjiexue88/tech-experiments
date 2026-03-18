// @Entity equivalent
// @Table(name="departments")
component persistent="true" table="departments" {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    property name="id" fieldtype="id" generator="native";

    // @Column(nullable = false, unique = true)
    property name="name" type="string" length="100" notnull="true" unique="true";
    property name="description" type="string" length="255";

    // Equivalent to @Embedded AuditInfo
    property name="createdAt" ormtype="timestamp";
    property name="updatedAt" ormtype="timestamp";

    // @OneToMany(mappedBy = "department", cascade = "all", orphanRemoval = true)
    property name="employees" 
             fieldtype="one-to-many" 
             cfc="Employee" 
             fkcolumn="dept_id" 
             inverse="true" 
             cascade="all-delete-orphan";

    // Constructor equivalent
    public Department function init(string name="", string description="") {
        variables.name = arguments.name;
        variables.description = arguments.description;
        return this;
    }

    // Helper method for Bidirectional sync
    public void function addEmployee(required Employee emp) {
        if (!hasEmployee(emp)) {
            arrayAppend(getEmployees(), emp);
        }
        // Set the inverse side
        emp.setDepartment(this);
    }

    // @PrePersist and @PreUpdate equivalent mapping via event handling
    public void function preInsert() {
        var now = now();
        setCreatedAt(now);
        setUpdatedAt(now);
    }

    public void function preUpdate(struct oldData) {
        setUpdatedAt(now());
    }
}
