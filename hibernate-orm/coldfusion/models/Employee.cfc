component persistent="true" table="employees" {

    // @Id
    property name="id" fieldtype="id" generator="native";

    // @Column(name = "first_name", nullable = false, length = 50)
    property name="firstName" column="first_name" type="string" length="50" notnull="true";

    // @Column(name = "last_name", nullable = false, length = 50)
    property name="lastName" column="last_name" type="string" length="50" notnull="true";

    // @Column(nullable = false, unique = true, length = 100)
    property name="email" type="string" length="100" notnull="true" unique="true";

    // @Column(precision = 10, scale = 2)
    property name="salary" ormtype="big_decimal";

    // @Enumerated(EnumType.STRING) - in CF ORM, simple strings for enums
    property name="status" type="string" length="20" default="ACTIVE";

    // @Version - Optimistic Locking
    property name="version" ormtype="long" version="true";

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "dept_id")
    property name="department" 
             fieldtype="many-to-one" 
             cfc="Department" 
             fkcolumn="dept_id" 
             lazy="true";

    // @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    // @JoinTable(...)
    property name="projects" 
             fieldtype="many-to-many" 
             cfc="Project" 
             linktable="employee_projects" 
             fkcolumn="employee_id" 
             inversejoincolumn="project_id" 
             cascade="save-update";

    public Employee function init(
        string firstName="", 
        string lastName="", 
        string email="", 
        numeric salary=0, 
        string status="ACTIVE"
    ) {
        variables.firstName = arguments.firstName;
        variables.lastName = arguments.lastName;
        variables.email = arguments.email;
        variables.salary = arguments.salary;
        variables.status = arguments.status;
        return this;
    }

    public void function assignToProject(required Project proj) {
        if (!hasProject(proj)) {
            arrayAppend(getProjects(), proj);
        }
        // CF ORM Many-To-Many management
        if (!proj.hasEmployee(this)) {
            arrayAppend(proj.getEmployees(), this);
        }
    }
}
