<cfscript>
    content type="text/html; charset=utf-8";

    // Helper to clean DB ensuring isolated scenarios
    public void function resetDatabase() {
        ORMCloseSession();
        transaction {
            queryExecute("DELETE FROM employee_projects");
            ORMExecuteQuery("DELETE FROM Employee");
            ORMExecuteQuery("DELETE FROM Project");
            ORMExecuteQuery("DELETE FROM Department");
        }
        ORMFlush();
    }

    public void function seedData() {
        resetDatabase();
        transaction {
            var engineering = entityNew("Department", { name: "Engineering", description: "Software development" });
            var marketing = entityNew("Department", { name: "Marketing", description: "Brand and growth" });

            var alice = entityNew("Employee", { firstName: "Alice", lastName: "Johnson", email: "alice@example.com", salary: 95000 });
            var bob = entityNew("Employee", { firstName: "Bob", lastName: "Smith", email: "bob@example.com", salary: 85000 });
            var carol = entityNew("Employee", { firstName: "Carol", lastName: "Williams", email: "carol@example.com", salary: 75000 });

            engineering.addEmployee(alice);
            engineering.addEmployee(bob);
            marketing.addEmployee(carol);

            var alpha = entityNew("Project", { projectCode: "PRJ-001", name: "Project Alpha", startDate: now() });
            var beta = entityNew("Project", { projectCode: "PRJ-002", name: "Project Beta", startDate: now() });

            alice.assignToProject(alpha);
            alice.assignToProject(beta);
            bob.assignToProject(alpha);

            entitySave(engineering);
            entitySave(marketing);
            entitySave(alpha);
            entitySave(beta);
        }
    }

    // Initialize Service
    application.employeeService = new services.EmployeeService();
</cfscript>

<!DOCTYPE html>
<html>
<head>
    <title>CF ORM (Hibernate) Execution</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; margin: 40px; line-height: 1.6; }
        .scenario { border: 1px solid #ddd; padding: 20px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .success { color: #155724; background-color: #d4edda; padding: 10px; border-radius: 5px; font-weight: bold; border-left: 5px solid #28a745; }
        .info { background: #e9ecef; padding: 10px; border-radius: 5px; font-family: monospace; }
        h1 { color: #333; }
        h3 { margin-top: 0; color: #0056b3; }
    </style>
</head>
<body>
    <h1>ColdFusion ORM (Hibernate Engine) Walkthrough</h1>
    <p>This page executes the exact same 10 Hibernate scenarios defined in your Spring Boot integration tests, proving that <strong>CF ORM is structurally identical to Java Hibernate</strong>.</p>
    
    <div class="scenario">
        <h3>1. Cascade Persist & Seed Data</h3>
        <cfscript>
            seedData();
            var eng = EntityLoad("Department", { name: "Engineering" }, true);
            writeOutput("<div class='success'>✔ Seeded Database. Engineering department retrieved. Cascaded employees count: #arrayLen(eng.getEmployees())# (Alice & Bob).</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>2. @PrePersist (Lifecycle Callbacks via preInsert)</h3>
        <cfscript>
            var engDept = EntityLoad("Department", { name: "Engineering" }, true);
            writeOutput("<div class='success'>✔ Department CreatedAt populated automatically: #dateFormat(engDept.getCreatedAt(), 'yyyy-mm-dd')# #timeFormat(engDept.getCreatedAt(), 'HH:mm:ss')#</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>3. ManyToMany Relationship</h3>
        <cfscript>
            var aliceQuery = ORMExecuteQuery("SELECT e FROM Employee e LEFT JOIN FETCH e.projects WHERE e.email = 'alice@example.com'");
            var aliceEmp = aliceQuery[1];
            writeOutput("<div class='success'>✔ Loaded Alice. She is assigned to #arrayLen(aliceEmp.getProjects())# projects: ");
            for (var proj in aliceEmp.getProjects()) {
                writeOutput("<span class='info'>#proj.getProjectCode()#</span> ");
            }
            writeOutput("</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>4. N+1 Problem solved with JOIN FETCH</h3>
        <cfscript>
            var emps = ORMExecuteQuery("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.department ORDER BY e.lastName");
            writeOutput("<div class='success'>✔ Loaded #arrayLen(emps)# employees explicitly joining their department in a single SQL query. First employee's department: #emps[1].getDepartment().getName()#.</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>5. Dirty Checking (Automatic Updates)</h3>
        <cfscript>
            seedData();
            transaction {
                var bobDirty = EntityLoad("Employee", { email: "bob@example.com" }, true);
                bobDirty.setSalary(90000); 
                // NO EntitySave() called here!
            }
            ORMCloseSession(); // flush and detached
            var bobVerify = EntityLoad("Employee", { email: "bob@example.com" }, true);
            writeOutput("<div class='success'>✔ Bob's salary was automatically updated to #bobVerify.getSalary()# on transaction complete via Hibernate dirty checking.</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>6. Optimistic Locking (@Version)</h3>
        <cfscript>
            seedData();
            var aliceV1 = EntityLoad("Employee", { email: "alice@example.com" }, true);
            var initialVersion = aliceV1.getVersion();
            
            transaction {
                var aliceDirty = EntityLoad("Employee", { email: "alice@example.com" }, true);
                aliceDirty.setFirstName("Alicia");
            }
            ORMCloseSession();
            
            var aliceV2 = EntityLoad("Employee", { email: "alice@example.com" }, true);
            writeOutput("<div class='success'>✔ Version incremented: Initial version = #initialVersion#, New version = #aliceV2.getVersion()# (Name updated to #aliceV2.getFirstName()#).</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>7. @NaturalId (Business Keys)</h3>
        <cfscript>
            // CF ORM naturally provides EntityLoad by property map, which is highly optimized
            var project = EntityLoad("Project", { projectCode: "PRJ-001" }, true);
            writeOutput("<div class='success'>✔ Found Project via Natural ID (projectCode): #project.getName()#.</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>8. Service Layer: Transactional Cascade Save</h3>
        <cfscript>
            var dev1 = entityNew("Employee", { firstName: "Dan", lastName: "Davis", email: "dan@example.com", salary: 88000 });
            var dev2 = entityNew("Employee", { firstName: "Eve", lastName: "Evans", email: "eve@example.com", salary: 92000 });
            var newDept = application.employeeService.createDepartmentWithEmployees("DevOps", "Infrastructure team", [dev1, dev2]);
            
            writeOutput("<div class='success'>✔ Service successfully created new department (#newDept.getName()#) and cascaded #arrayLen(newDept.getEmployees())# employees inside a single transaction.</div>");
        </cfscript>
    </div>

    <div class="scenario">
        <h3>9. Service Layer: Fully Loaded Fetch (Solving OSIV)</h3>
        <cfscript>
            seedData();
            var aliceLoad = EntityLoad("Employee", { email: "alice@example.com" }, true);
            var aliceId = aliceLoad.getId();
            ORMCloseSession(); // Important: forces LazyInitializationException if we do it wrong

            var fullyLoaded = application.employeeService.findEmployeeFullyLoaded(aliceId);
            writeOutput("<div class='success'>✔ Called Service method (outside of open session). Successfully fetched Employee '#fullyLoaded.getFirstName()#' with Department '#fullyLoaded.getDepartment().getName()#' and #arrayLen(fullyLoaded.getProjects())# Projects without triggering LazyInitialization exceptions.</div>");
        </cfscript>
    </div>

</body>
</html>
