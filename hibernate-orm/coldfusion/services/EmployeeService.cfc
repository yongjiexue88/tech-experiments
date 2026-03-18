// Equivalent to @Service, managing @Transactional logic manually using transaction {} blocks
component {

    /**
     * @Transactional
     * 1. Creates department
     * 2. Adds employees
     * 3. EntitySave cascades to insert all
     */
    public Department function createDepartmentWithEmployees(
        required string deptName, 
        required string description, 
        required array employees) 
    {
        var dept = "";
        
        // CFML's equivalent of @Transactional
        transaction {
            dept = entityNew("Department", { name: arguments.deptName, description: arguments.description });
            
            for (var emp in arguments.employees) {
                dept.addEmployee(emp);
            }
            // save() handles persistence and ID generation
            entitySave(dept);
            
            // transaction commits automatically at the end of the block
        }
        return dept;
    }

    /**
     * @Transactional
     * Dirty checking handles the UPDATE on commit automatically.
     */
    public void function transferEmployee(required numeric employeeId, required numeric newDepartmentId) {
        transaction {
            var employee = entityLoadByPK("Employee", arguments.employeeId);
            if (isNull(employee)) {
                throw(type="IllegalArgumentException", message="Employee not found: #arguments.employeeId#");
            }
            
            // CF ORM proxy loading (equivalent to getReferenceById)
            var newDept = entityLoadByPK("Department", arguments.newDepartmentId);
            
            // Setting the department marks the employee as DIRTY. 
            // At the end of the transaction{}, CF ORM automatically generates the UPDATE statement.
            employee.setDepartment(newDept);
        }
    }

    /**
     * @Transactional(readOnly=true)
     * Solves OSIV problem with JOIN FETCH via HQL.
     */
    public any function findEmployeeFullyLoaded(required numeric id) {
        var result = "";
        transaction {
            // HQL standard syntax works natively in ColdFusion ORMExecuteQuery
            var hql = "SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.projects WHERE e.id = :id";
            var queryResult = ORMExecuteQuery(hql, { id: arguments.id });
            
            if (arrayLen(queryResult)) {
                result = queryResult[1]; // Return the fully initialized entity
            }
        }
        return isNull(result) ? null : result;
    }
}
