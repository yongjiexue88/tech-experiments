package com.interview.hibernate.service;

import com.interview.hibernate.model.*;
import com.interview.hibernate.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ 面试要点: Spring @Service 与 @Transactional 的魔法 ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ 1. @Service 告诉 Spring 的 IoC 容器管理这个类的生命周期 (单例)。 ║
 * ║ ║
 * ║ 2. AOP 代理模式机制: ║
 * ║ 当你在方法上加上 @Transactional，Spring 并不会直接给你这个 Service， ║
 * ║ 而是动态生成一个 "代理对象 (Proxy)"。当 Controller 调用此服务时， ║
 * ║ 它是与代理对象通信。代理对象负责: ║
 * ║ - (前端切面) session.beginTransaction() ║
 * ║ - 实际执行你的 Java 逻辑代码 ║
 * ║ - (后端切面) 如果没抛错，执行 tx.commit()，否则 tx.rollback() ║
 * ║ ║
 * ║ 面试必考: @Transactional 传播机制 (Propagation) 和 隔离级别 (Isolation) ║
 * ║ ───────────────────────────────────────────────────────────────────── ║
 * ║ - REQUIRED (默认): 必须有事务。如果调用方已有事务，则加入；否则新建。 ║
 * ║ - REQUIRES_NEW: 挂起当前事务，强行开启一个全新的独立事务。 ║
 * ║ - SUPPORTS: 支持事务。如果有事务就用，没有就非事务执行。 ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;

    // 构造器注入 (Best Practice)
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            ProjectRepository projectRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * 在一个事务中创建部门及其员工。
     *
     * 面试要点: Cascade.ALL 的魔力。因为 Department 配置了级联保存，
     * 所以在存储 Department 时，Spring Data JPA (底层 Hibernate)
     * 会自动为所有 Employees 执行 INSERT。
     */
    @Transactional
    public Department createDepartmentWithEmployees(String deptName,
            String description,
            List<Employee> employees) {
        // 创建实体 (现在的状态是 TRANSIENT，即瞬时态)
        Department dept = new Department(deptName, description);

        for (Employee emp : employees) {
            dept.addEmployee(emp);
        }

        // JPA 帮我们持久化对象并生成 ID。
        // 方法结束时，Spring AOP 会自动提交事务。
        return departmentRepository.save(dept);
    }

    /**
     * 将员工调到另一个部门。
     *
     * 面试要点: 脏检查机制 (Dirty Checking)。
     * 注意这里没有任何 "employeeRepository.save(employee);" 代码！
     * 在 @Transactional 方法内获取的实体属于 MANAGED (托管) 状态。
     * 事务提交时，Hibernate 会比较老快照，检测到字段改变，自动发送 UPDATE。
     */
    @Transactional
    public void transferEmployee(Long employeeId, Long newDepartmentId) {
        // 从数据库中加载员工和部门
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("员工未找到: " + employeeId));

        // getReferenceById 对应 EntityManager.getReference()，
        // 只返回一个延迟加载的代理 Proxy，避免了一次不必要的 DB 查询。
        Department newDept = departmentRepository.getReferenceById(newDepartmentId);

        // 仅仅是修改内存里的字段
        employee.setDepartment(newDept);

        // 事务结束，自动生成并执行 UPDATE SQL!
    }

    /**
     * 将员工分配到项目 (ManyToMany 关系操作)。
     */
    @Transactional
    public void assignEmployeeToProject(Long employeeId, Long projectId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("员工未找到"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("项目未找到"));

        // 更新中间表数据。同样基于脏检查机制，无需显式 save。
        employee.assignToProject(project);
    }

    /**
     * 给部门所有员工加薪 (批量 UPDATE)。
     *
     * 面试要点: 对于批量操作，使用带有 @Modifying 的 @Query。
     * 这比循环 find() 然后 save() 要快无数倍，因为它在数据库层面只执行一条 SQL。
     */
    @Transactional
    public int giveRaiseToDepartment(Long departmentId, BigDecimal raisePercent) {
        BigDecimal multiplier = BigDecimal.ONE.add(raisePercent.divide(BigDecimal.valueOf(100)));
        // 直接委派给 Spring Data Repository 的自定义方法
        return employeeRepository.giveRaiseToDepartment(departmentId, multiplier);
    }

    /**
     * 演示脱管实体和 save (merge) 操作。
     *
     * 面试要点: 在 Spring Data JPA 中，save() 方法有两个作用:
     * 1. 如果实体没有 ID (或使用 isNew() 为 true)，底层调用 EntityManager.persist()，执行 INSERT。
     * 2. 如果实体有 ID，底层调用 EntityManager.merge()，执行 UPDATE，并返回新的 MANAGED 对象。
     */
    @Transactional
    public Employee updateDetachedEmployee(Employee detachedEmployee) {
        // save() 方法会返回一个新的与当前 Hibernate Session 绑定的托管对象。
        return employeeRepository.save(detachedEmployee);
    }

    /**
     * ═══════════════════════════════════════════════════════════════════
     * 面试要点: Open Session in View (OSIV) 及 N+1 的解决方案
     * ─────────────────────────────────────────────────────────────────
     * 默认情况下 Spring Boot 启用了 spring.jpa.open-in-view=true。
     * 也就是说由于事务(Session)没关，Controller层直接访问关联关系也不会报错。
     * 
     * 但在性能攸关的服务中，我们应当明确规定加载的数据深度 (JOIN FETCH) 并关掉 OSIV。
     * ═══════════════════════════════════════════════════════════════════
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findEmployeeFullyLoaded(Long id) {
        // 直接使用 Repository 里手写的 JPQL "JOIN FETCH" 查询，
        // 这样一条 SQL 查回所有数据并组装，彻底避免 N+1 问题。
        return employeeRepository.findEmployeeFullyLoaded(id);
    }
}
