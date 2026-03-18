package com.interview.hibernate.model; // 包声明

import jakarta.persistence.*; // JPA 持久化注解
import java.time.LocalDate; // 日期类 (不含时间部分)
import java.util.HashSet; // 哈希集合
import java.util.Objects; // equals/hashCode 辅助工具
import java.util.Set; // Set 接口

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ 面试要点: @NaturalId 和 @NamedQuery ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ @NaturalId — 标记业务键 (Business Key)，唯一且不可变。 ║
 * ║ Hibernate 提供 Session.byNaturalId() 进行优化查找， ║
 * ║ 启用 L2 NaturalId 缓存时效果更佳。 ║
 * ║ ║
 * ║ @NamedQuery — 预编译的 HQL 查询，定义在实体类级别。 ║
 * ║ • 在应用启动时验证 (HQL 不合法则快速失败) ║
 * ║ • 可被 Hibernate 的查询计划缓存缓存 ║
 * ║ • 集中定义查询 (便于找到和审计) ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Entity // 标记为 JPA 实体
@Table(name = "projects") // 映射到 "projects" 表
@NamedQuery( // 预定义的命名查询1: 按状态查找项目
        name = "Project.findByStatus", // 查询名称 — 使用 "实体名.方法名" 的命名惯例
        query = "SELECT p FROM Project p WHERE p.active = :active" // HQL 查询语句，:active 是命名参数
)
@NamedQuery( // 预定义的命名查询2: 带员工的项目查询 (使用 JOIN FETCH)
        name = "Project.findWithEmployees", query = "SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.employees WHERE p.id = :id"
// DISTINCT 避免因 JOIN 产生的重复行
// LEFT JOIN FETCH 在一条 SQL 中加载关联的员工集合
)
public class Project {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增策略
    private Long id; // 项目 ID

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: @NaturalId — 自然主键 / 业务键
    // ─────────────────────────────────────────────────────────────────
    // 自然 ID 是有业务含义的键 — 在数据库外也有意义。
    // 例如: 项目代码 "PRJ-2024-001", 身份证号, ISBN 等。
    //
    // 使用方法:
    // session.byNaturalId(Project.class)
    // .using("projectCode", "PRJ-2024-001")
    // .load();
    //
    // 优势:
    // • Hibernate 在 L2 缓存中缓存 自然ID→主键 的映射
    // • 避免按业务键查找时的全表扫描
    // ═══════════════════════════════════════════════════════════════════
    @org.hibernate.annotations.NaturalId // Hibernate 专属注解: 标记为自然 ID
    @Column(name = "project_code", // 列名
            nullable = false, // 不允许为空
            unique = true, // 唯一约束
            length = 20) // 最大20字符
    private String projectCode; // 项目代码 — 业务键

    // 项目名称 — 不允许为空
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    // 项目开始日期
    @Column(name = "start_date")
    private LocalDate startDate;

    // 项目结束日期 (可为空 — 进行中的项目还没有结束日期)
    @Column(name = "end_date")
    private LocalDate endDate;

    // 项目是否激活 — 默认为 true
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // ── ManyToMany 的反转端 ──
    // mappedBy = "projects" → 由 Employee.projects 字段拥有 @JoinTable
    // 即 Employee 端控制中间表 employee_projects
    @ManyToMany(mappedBy = "projects", // 反转端: Employee.projects 拥有外键
            fetch = FetchType.LAZY) // 延迟加载
    private Set<Employee> employees = new HashSet<>(); // 参与此项目的员工集合

    // ── 构造函数 ──

    // JPA 规范要求的无参构造函数
    public Project() {
    }

    // 便捷构造函数
    public Project(String projectCode, String name, LocalDate startDate) {
        this.projectCode = projectCode; // 设置项目代码
        this.name = name; // 设置项目名称
        this.startDate = startDate; // 设置开始日期
    }

    // ── Getter 和 Setter 方法 ──

    public Long getId() {
        return id;
    } // 获取主键

    public String getProjectCode() {
        return projectCode;
    } // 获取项目代码

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    } // 设置项目代码

    public String getName() {
        return name;
    } // 获取项目名称

    public void setName(String name) {
        this.name = name;
    } // 设置项目名称

    public LocalDate getStartDate() {
        return startDate;
    } // 获取开始日期

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    } // 设置开始日期

    public LocalDate getEndDate() {
        return endDate;
    } // 获取结束日期

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    } // 设置结束日期

    public boolean isActive() {
        return active;
    } // 项目是否激活

    public void setActive(boolean active) {
        this.active = active;
    } // 设置激活状态

    public Set<Employee> getEmployees() {
        return employees;
    } // 获取参与的员工集合

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: 使用 @NaturalId 实现 equals/hashCode
    // ─────────────────────────────────────────────────────────────────
    // 当实体有 @NaturalId 时，用它来实现 equals/hashCode。
    // 这是最干净的方式，因为自然 ID:
    // • 在 persist 之前就已设置 (没有 null ID 问题)
    // • 不可变 (哈希值不会改变)
    // • 唯一 (真正的业务标识)
    // ═══════════════════════════════════════════════════════════════════
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; // 同一对象 → 相等
        if (o == null || getClass() != o.getClass())
            return false; // null 或不同类 → 不等
        Project project = (Project) o; // 类型转换
        return Objects.equals(projectCode, project.projectCode); // 基于项目代码比较
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectCode); // 基于项目代码计算哈希
    }

    @Override
    public String toString() {
        // 字符串表示 — 不包含 employees 集合，避免懒加载和循环引用
        return "Project{id=" + id + ", code='" + projectCode + "', name='" + name + "'}";
    }
}
