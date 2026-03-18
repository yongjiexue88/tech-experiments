component {
    this.name = "HibernateInterviewCF";
    
    // Define an in-memory H2 datasource natively in Application.cfc (supported by Lucee)
    this.datasources["hibernate_cf"] = {
        class: "org.h2.Driver",
        connectionString: "jdbc:h2:mem:hibernate_cf;DB_CLOSE_DELAY=-1" // Keeps DB alive in memory
    };
    this.defaultDatasource = "hibernate_cf";

    // ColdFusion ORM capabilities
    this.ormEnabled = true;
    this.ormSettings = {
        dialect = "H2",
        dbcreate = "dropcreate",     // Recreates tables on app restart (hibernate.hbm2ddl.auto="create-drop")
        logSQL = true,               // hibernate.show_sql="true"
        cfcLocation = "./models",    // Where our @Entity classes live
        eventhandling = true,        // Enables @PrePersist / preInsert()
        flushAtRequestEnd = false,   // We manage our own transactions and flushes
        autoManageSession = false    // No implicit ORM session magic
    };

    function onApplicationStart() {
        ORMReload(); // Force Hibernate to rebuild the SessionFactory
        return true;
    }

    function onRequestStart(requestPage) {
        // Allow reloading ORM dynamically via ?reload=true
        if (structKeyExists(url, "reload")) {
            ORMReload();
        }
    }
}
