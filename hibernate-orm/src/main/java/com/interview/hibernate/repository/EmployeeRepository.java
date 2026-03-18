package com.interview.hibernate.repository;

import com.interview.hibernate.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ 面试要点: Spring Data JPA Repository 模式 ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ 仅仅通过继承 JpaRepository<Employee, Long>，Spring 就会在运行时自动生成 ║
 * ║ 一个代理类，提供所有的 CRUD 方法 (save, findById, delete 等)。 ║
 * ║ 你不需要再写 session.save() 或新建 GenericDao 了。 ║
 * ║ ║
 * ║ JpaSpecificationExecutor<Employee> 的作用: ║
 * ║ 允许执行基于 JPA Criteria API 的动态查询 (通过 Specification 接口)。 ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: Derived Query Methods (派生查询)
    // ─────────────────────────────────────────────────────────────────
    // Spring 会解析方法名 "findBy" + "DepartmentName"，自动生成 HQL：
    // SELECT e FROM Employee e WHERE e.department.name = ?
    // ═══════════════════════════════════════════════════════════════════
    List<Employee> findByDepartmentName(String deptName);

    // 等同于: SELECT e FROM Employee e WHERE e.salary > ? ORDER BY e.salary DESC
    List<Employee> findBySalaryGreaterThanOrderBySalaryDesc(BigDecimal minSalary);

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: @Query 注解自定义 HQL / 解决 N+1 问题
    // ─────────────────────────────────────────────────────────────────
    // 使用 JOIN FETCH 在一条查询中急切加载关联数据。
    // ═══════════════════════════════════════════════════════════════════
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.department ORDER BY e.lastName")
    List<Employee> findAllWithDepartment();

    // 同时抓取部门和项目
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.projects ORDER BY e.lastName")
    List<Employee> findAllWithDepartmentAndProjects();

    // 按 ID 查询并同时抓取部门和项目 (解决 OSIV 的方法)
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.projects WHERE e.id = :id")
    Optional<Employee> findEmployeeFullyLoaded(@Param("id") Long id);

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: 批量 @Modifying 更新
    // ─────────────────────────────────────────────────────────────────
    // 当你要执行 INSERT、UPDATE 或 DELETE 时，必须加上 @Modifying。
    // clearAutomatically = true 会在更新后清空持久化上下文，确保后续查询
    // 不会读到过期的缓存数据。
    // ═══════════════════════════════════════════════════════════════════
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Employee e SET e.salary = e.salary * :multiplier WHERE e.department.id = :deptId")
    int giveRaiseToDepartment(@Param("deptId") Long deptId, @Param("multiplier") BigDecimal multiplier);

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: 原生 SQL 查询
    // ─────────────────────────────────────────────────────────────────
    // nativeQuery = true 告诉 Spring 这是纯 SQL，不是 HQL。
    // ═══════════════════════════════════════════════════════════════════
    @Query(value = "SELECT d.name AS dept_name, COUNT(e.id) AS emp_count " +
            "FROM departments d " +
            "LEFT JOIN employees e ON e.dept_id = d.id " +
            "GROUP BY d.name " +
            "ORDER BY emp_count DESC", nativeQuery = true)
    List<Object[]> countEmployeesPerDepartment();
}
