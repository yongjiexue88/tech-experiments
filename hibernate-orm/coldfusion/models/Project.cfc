// @Entity
// @Table(name="projects")
// @NaturalIdCache (CF ORM leverages Hibernate 2nd level cache natively if configured)
component persistent="true" table="projects" {

    // @Id
    property name="id" fieldtype="id" generator="native";

    // @NaturalId
    // @Column(name = "project_code", nullable = false, unique = true, updatable = false)
    property name="projectCode" column="project_code" type="string" length="50" notnull="true" unique="true" update="false";

    property name="name" type="string" length="100" notnull="true";
    
    property name="startDate" column="start_date" ormtype="date";
    property name="endDate" column="end_date" ormtype="date";
    
    property name="active" ormtype="boolean" default="true";

    // @ManyToMany(mappedBy = "projects")
    property name="employees" 
             fieldtype="many-to-many" 
             cfc="Employee" 
             linktable="employee_projects" 
             fkcolumn="project_id" 
             inversejoincolumn="employee_id" 
             inverse="true";

    public Project function init(string projectCode="", string name="", date startDate=now()) {
        variables.projectCode = arguments.projectCode;
        variables.name = arguments.name;
        variables.startDate = arguments.startDate;
        variables.active = true;
        return this;
    }
}
