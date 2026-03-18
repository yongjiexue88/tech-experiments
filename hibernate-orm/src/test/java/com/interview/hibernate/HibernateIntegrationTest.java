package com.interview.hibernate;

import com.interview.hibernate.model.*;
import com.interview.hibernate.repository.DepartmentRepository;
import com.interview.hibernate.repository.EmployeeRepository;
import com.interview.hibernate.repository.ProjectRepository;
import com.interview.hibernate.service.EmployeeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Boot + JPA 集成测试。
 * 面试要点: 使用 @SpringBootTest，Spring 会自动启动完整的应用上下文，
 * 读取 application.yml 中的配置，连接到自动装配的 SQLite 内存数据库。
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HibernateIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EntityManager em; // 注入底层的 EntityManager 以做精确的生命周期测试
    @Autowired
    private TransactionTemplate txTemplate; // 用于手动控制事务边界

    @BeforeEach
    void cleanDatabase() {
        // 使用 Repository 删除数据。注意删除顺序以应对外键约束
        employeeRepository.deleteAll(); // 由于级联等设置，会级联删除中间表
        projectRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    private void seedData() {
        // 使用 TransactionTemplate 保证所有数据都在一个事务中提交
        txTemplate.executeWithoutResult(status -> {
            Department engineering = new Department("Engineering", "Software development");
            Department marketing = new Department("Marketing", "Brand and growth");

            Employee alice = new Employee("Alice", "Johnson", "alice@example.com", new BigDecimal("95000.00"),
                    EmployeeStatus.ACTIVE);
            Employee bob = new Employee("Bob", "Smith", "bob@example.com", new BigDecimal("85000.00"),
                    EmployeeStatus.ACTIVE);
            Employee carol = new Employee("Carol", "Williams", "carol@example.com", new BigDecimal("75000.00"),
                    EmployeeStatus.ON_LEAVE);

            engineering.addEmployee(alice);
            engineering.addEmployee(bob);
            marketing.addEmployee(carol);

            Project alpha = new Project("PRJ-001", "Project Alpha", LocalDate.of(2024, 1, 15));
            Project beta = new Project("PRJ-002", "Project Beta", LocalDate.of(2024, 6, 1));

            alice.assignToProject(alpha);
            alice.assignToProject(beta);
            bob.assignToProject(alpha);

            // JpaRepository.save 遇到没有 ID 的实体会调用 EntityManager.persist
            departmentRepository.save(engineering);
            departmentRepository.save(marketing);
            projectRepository.save(alpha);
            projectRepository.save(beta);
        });
    }

    @Test
    @Order(1)
    @DisplayName("1. 保存部门时应该级联保存其员工")
    void testCascadePersist() {
        seedData();
        List<Department> departments = departmentRepository.findAll();
        assertThat(departments).hasSize(2);

        Department eng = departments.stream().filter(d -> d.getName().equals("Engineering")).findFirst().orElseThrow();
        // 因为测试方法本身没有 @Transactional，访问 Lazy 集合如果触发了 LAZY-Loading 且没有 OSIV 会报错。
        // 但在这我们通过 txTemplate 控制边界确保正确加载。
        txTemplate.executeWithoutResult(status -> {
            Department txEng = departmentRepository.findById(eng.getId()).orElseThrow();
            assertThat(txEng.getEmployees()).hasSize(2);
        });
    }

    @Test
    @Order(2)
    @DisplayName("2. @PrePersist 应该自动填充 AuditInfo 时间戳")
    void testAuditInfoLifecycleCallback() {
        seedData();
        Department dept = departmentRepository.findAll().stream().filter(d -> d.getName().equals("Engineering"))
                .findFirst().orElseThrow();

        assertThat(dept.getAuditInfo()).isNotNull();
        assertThat(dept.getAuditInfo().getCreatedAt()).isNotNull();
        assertThat(dept.getAuditInfo().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("3. ManyToMany: 员工应该能被分配到多个项目")
    void testManyToManyRelationship() {
        seedData();
        // 使用我们定义的 JPQL FETCH 解决 N+1
        Employee alice = employeeRepository.findAllWithDepartmentAndProjects().stream()
                .filter(e -> e.getEmail().equals("alice@example.com")).findFirst().orElseThrow();

        assertThat(alice.getProjects()).hasSize(2);
        assertThat(alice.getProjects()).extracting(Project::getProjectCode).containsExactlyInAnyOrder("PRJ-001",
                "PRJ-002");
    }

    @Test
    @Order(4)
    @DisplayName("4. JOIN FETCH 应该在一条查询中加载员工及其部门")
    void testJoinFetch() {
        seedData();
        List<Employee> employees = employeeRepository.findAllWithDepartment();
        assertThat(employees).hasSize(3);

        for (Employee emp : employees) {
            assertThat(emp.getDepartment()).isNotNull();
            assertThat(emp.getDepartment().getName()).isNotEmpty();
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. 脏检查应该在提交时自动更新已修改的托管实体")
    void testDirtyChecking() {
        seedData();

        // 第一步: 事务内修改实例 (依赖底层 Transactional 的包裹)
        txTemplate.executeWithoutResult(status -> {
            Employee bob = employeeRepository.findAll().stream()
                    .filter(e -> e.getEmail().equals("bob@example.com")).findFirst().orElseThrow();
            bob.setSalary(new BigDecimal("90000.00"));
            // 事务结束时，自动执行 UPDATE! 毫无 save() 代码
        });

        // 验证已更新
        Employee bob = employeeRepository.findAll().stream().filter(e -> e.getEmail().equals("bob@example.com"))
                .findFirst().orElseThrow();
        assertThat(bob.getSalary()).isEqualByComparingTo(new BigDecimal("90000.00"));
    }

    @Test
    @Order(6)
    @DisplayName("6. @Version 应该在更新时自增")
    void testOptimisticLockingVersionIncrement() {
        seedData();

        Long aliceId = employeeRepository.findAll().stream()
                .filter(e -> e.getEmail().equals("alice@example.com")).findFirst().orElseThrow().getId();

        assertThat(employeeRepository.findById(aliceId).orElseThrow().getVersion()).isEqualTo(0);

        txTemplate.executeWithoutResult(status -> {
            Employee alice = employeeRepository.findById(aliceId).orElseThrow();
            alice.setFirstName("Alicia");
            // 脏检查发生 -> version++
        });

        assertThat(employeeRepository.findById(aliceId).orElseThrow().getVersion()).isEqualTo(1);
    }

    @Test
    @Order(7)
    @DisplayName("7. @NaturalId 应该支持按业务键查找 (使用 EntityManager)")
    void testNaturalIdLookup() {
        seedData();
        txTemplate.executeWithoutResult(status -> {
            // Spring Data JPA 默认不支持 NaturalId，必须调用底层 EntityManager
            Project project = em.unwrap(org.hibernate.Session.class)
                    .byNaturalId(Project.class)
                    .using("projectCode", "PRJ-001")
                    .load();
            assertThat(project).isNotNull();
            assertThat(project.getName()).isEqualTo("Project Alpha");
        });
    }

    @Test
    @Order(8)
    @DisplayName("8. @NamedQuery 应该能查找激活的项目 (使用 EntityManager)")
    void testNamedQuery() {
        seedData();
        txTemplate.executeWithoutResult(status -> {
            @SuppressWarnings("unchecked")
            List<Project> activeProjects = em.createNamedQuery("Project.findByStatus")
                    .setParameter("active", true)
                    .getResultList();
            assertThat(activeProjects).hasSize(2);
        });
    }

    @Test
    @Order(9)
    @DisplayName("9. EmployeeService 应该创建部门并级联保存员工")
    void testServiceCreateDepartment() {
        Employee dev1 = new Employee("Dan", "Davis", "dan@example.com", new BigDecimal("88000.00"),
                EmployeeStatus.ACTIVE);
        Employee dev2 = new Employee("Eve", "Evans", "eve@example.com", new BigDecimal("92000.00"),
                EmployeeStatus.ACTIVE);

        Department dept = employeeService.createDepartmentWithEmployees("DevOps", "Infrastructure team",
                List.of(dev1, dev2));
        assertThat(dept.getId()).isNotNull();

        txTemplate.executeWithoutResult(status -> {
            Department loaded = departmentRepository.findById(dept.getId()).orElseThrow();
            assertThat(loaded.getEmployees()).hasSize(2);
        });
    }

    @Test
    @Order(10)
    @DisplayName("10. findEmployeeFullyLoaded 应该急切加载部门和项目")
    void testServiceFullyLoadedEmployee() {
        seedData();
        Long aliceId = employeeRepository.findAll().stream()
                .filter(e -> e.getEmail().equals("alice@example.com")).findFirst().orElseThrow().getId();

        // Service 方法使用了 @Transactional(readOnly = true) 和 JOIN FETCH
        Optional<Employee> result = employeeService.findEmployeeFullyLoaded(aliceId);

        assertThat(result).isPresent();
        Employee alice = result.get();
        assertThat(alice.getDepartment().getName()).isEqualTo("Engineering");
        assertThat(alice.getProjects()).hasSize(2);
    }
}
