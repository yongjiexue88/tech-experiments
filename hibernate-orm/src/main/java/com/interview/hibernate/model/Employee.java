package com.interview.hibernate.model; // 包声明

import jakarta.persistence.*; // JPA 所有持久化注解
import java.math.BigDecimal; // 精确小数类型 — 金额/薪资必须用 BigDecimal, 不要用 double
import java.util.HashSet; // HashSet — 基于哈希表的集合，不允许重复
import java.util.Objects; // 工具类: 提供 hash() 和 equals() 辅助方法
import java.util.Set; // Set 接口 — 无序不重复集合

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ 面试要点: 核心实体 — 集中展示多个 Hibernate 概念 ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ 此实体涵盖: ║
 * ║ • @ManyToOne (与 Department 的双向关系的拥有端) ║
 * ║ • @ManyToMany 与显式 @JoinTable ║
 * ║ • @Enumerated(STRING) vs ORDINAL ║
 * ║ • @Version 乐观锁 ║
 * ║ • 延迟加载 vs 即时加载 ║
 * ║ • JPA 实体的 equals/hashCode 约定 ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Entity // 标记为 JPA 实体
@Table(name = "employees", // 映射到 "employees" 表
        indexes = {
                // ═══════════════════════════════════════════════════════════════════
                // 面试要点: @Table(indexes = ...)
                // Hibernate 在 hbm2ddl 运行时可以根据注解生成索引。
                // 生产环境中，应使用 Flyway/Liquibase 迁移来管理索引。
                // ═══════════════════════════════════════════════════════════════════
                @Index(name = "idx_employee_email", // 索引名称
                        columnList = "email", // 索引列
                        unique = true), // 唯一索引 — 保证 email 不重复
                @Index(name = "idx_employee_dept", // 外键列索引
                        columnList = "dept_id") // 加速按部门查询
        })
public class Employee {

    @Id // 主键标记
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id; // 员工 ID

    // 名字 — 不允许为空，最多50字符
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    // 姓氏 — 不允许为空，最多50字符
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    // 邮箱 — 不允许为空，唯一约束，最多150字符
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // 薪资 — 使用 BigDecimal 保证精度
    // precision=10: 总共10位数字, scale=2: 小数点后2位
    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: @Enumerated — STRING vs ORDINAL
    // ─────────────────────────────────────────────────────────────────
    // ORDINAL (默认) → 存储枚举的序号 (0, 1, 2...)
    // ⚠️ 危险: 重新排序枚举常量会改变数据库中的含义！
    // 比如原来 ACTIVE=0, INACTIVE=1，后来在位置1插入了 ON_LEAVE，
    // 所有 "INACTIVE" 的行会默默变成 "ON_LEAVE"。
    //
    // STRING → 存储枚举名称 ("ACTIVE", "INACTIVE")
    // ✅ 可以安全地重新排序，只有重命名才是破坏性变更。
    // 存储开销略大，但安全性值得。
    // ═══════════════════════════════════════════════════════════════════
    @Enumerated(EnumType.STRING) // 将枚举存储为字符串 (推荐)
    @Column(name = "status", nullable = false, length = 20) // 不允许为空
    private EmployeeStatus status = EmployeeStatus.ACTIVE; // 默认状态: 在职

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: @ManyToOne — 关系的拥有端
    // ─────────────────────────────────────────────────────────────────
    // 此端拥有实际的外键列 (dept_id)。
    // "拥有端"永远是有 @JoinColumn 的一端。
    //
    // FetchType.LAZY → 只在访问 department 属性时才加载
    // (通过一条单独的 SELECT 或代理对象初始化)
    // FetchType.EAGER → 立即通过 JOIN 加载
    //
    // ⚠️ @ManyToOne 默认是 EAGER！必须显式设置 LAZY。
    // EAGER 的 @ManyToOne 是 N+1 查询问题的头号原因。
    //
    // N+1 问题详解:
    // ───────────────────────
    // 查询1: SELECT * FROM employees (返回 N 行)
    // 查询2..N+1: SELECT * FROM departments (每个员工一条!)
    // 共计: N+1 条 SQL — 性能灾难
    //
    // 解决方案:
    // 1. HQL 中使用 JOIN FETCH: "FROM Employee e JOIN FETCH e.department"
    // 2. @BatchSize(size=10) → 批量加载 department
    // 3. @EntityGraph → 声明式加载计划
    // 4. Criteria API 中使用 fetch() JOIN
    // ═══════════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY) // 多对一关系，延迟加载
    @JoinColumn(name = "dept_id", // 外键列名
            foreignKey = @ForeignKey(name = "fk_emp_dept")) // 外键约束名称
    private Department department; // 员工所属部门

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: @ManyToMany 与 @JoinTable
    // ─────────────────────────────────────────────────────────────────
    // 多对多关系需要数据库中的一张中间表 (JOIN TABLE)。
    // 只有一端应该定义 @JoinTable (拥有端)。
    // 另一端使用 mappedBy (参见 Project.employees)。
    //
    // ⚠️ @ManyToMany 应使用 Set<>，不要用 List<>！
    // 使用 List 时，Hibernate 在任何变更时都会删除所有中间表行再重新插入。
    // 使用 Set 则允许精准的 INSERT/DELETE。
    //
    // 对于复杂的中间表 (有额外列)，应将中间表提升为 @Entity，
    // 使用两个 @ManyToOne 关系代替 @ManyToMany。
    // ═══════════════════════════════════════════════════════════════════
    @ManyToMany(fetch = FetchType.LAZY) // 多对多关系，延迟加载
    @JoinTable(name = "employee_projects", // 中间表名称
            joinColumns = @JoinColumn(name = "employee_id"), // 当前实体在中间表的外键列
            inverseJoinColumns = @JoinColumn(name = "project_id") // 对方实体在中间表的外键列
    )
    private Set<Project> projects = new HashSet<>(); // 使用 Set 而非 List (重要!)

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: @Version — 乐观锁 (Optimistic Locking)
    // ─────────────────────────────────────────────────────────────────
    // Hibernate 自动执行:
    // 1. 在 UPDATE 的 WHERE 子句中包含版本号:
    // UPDATE employees SET ..., version=2 WHERE id=1 AND version=1
    // 2. 如果没有行被更新 (版本不匹配)，抛出
    // OptimisticLockException → 表示发生了并发修改
    //
    // 乐观锁适合冲突较少的场景。
    // 当冲突频繁时 (如高并发的库存扣减)，应使用悲观锁
    // (SELECT ... FOR UPDATE)。
    //
    // 支持的类型: int, Integer, long, Long, short, Short,
    // Timestamp, Instant
    // ═══════════════════════════════════════════════════════════════════
    @Version // 标记为版本字段，启用乐观锁
    @Column(name = "version") // 映射到 version 列
    private Integer version; // 版本号 — Hibernate 自动维护，每次 UPDATE 自增

    // ── 构造函数 ──

    // JPA 规范要求的无参构造函数
    public Employee() {
    }

    // 全参构造函数 — 方便创建实例
    public Employee(String firstName, String lastName, String email,
            BigDecimal salary, EmployeeStatus status) {
        this.firstName = firstName; // 设置名字
        this.lastName = lastName; // 设置姓氏
        this.email = email; // 设置邮箱
        this.salary = salary; // 设置薪资
        this.status = status; // 设置状态
    }

    // ── 项目关系辅助方法 — 保持 ManyToMany 双向同步 ──

    /**
     * 将此员工分配到一个项目 — 同步双向关系
     * 
     * @param project 要分配的项目
     */
    public void assignToProject(Project project) {
        this.projects.add(project); // 将项目添加到员工的项目集合
        project.getEmployees().add(this); // 同时将员工添加到项目的员工集合
    }

    /**
     * 将此员工从一个项目中移除 — 同步双向关系
     * 
     * @param project 要移除的项目
     */
    public void removeFromProject(Project project) {
        this.projects.remove(project); // 从员工的项目集合中移除
        project.getEmployees().remove(this); // 同时从项目的员工集合中移除
    }

    // ── Getter 和 Setter 方法 ──

    public Long getId() {
        return id;
    } // 获取主键

    public String getFirstName() {
        return firstName;
    } // 获取名字

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    } // 设置名字

    public String getLastName() {
        return lastName;
    } // 获取姓氏

    public void setLastName(String lastName) {
        this.lastName = lastName;
    } // 设置姓氏

    public String getEmail() {
        return email;
    } // 获取邮箱

    public void setEmail(String email) {
        this.email = email;
    } // 设置邮箱

    public BigDecimal getSalary() {
        return salary;
    } // 获取薪资

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    } // 设置薪资

    public EmployeeStatus getStatus() {
        return status;
    } // 获取状态

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    } // 设置状态

    public Department getDepartment() {
        return department;
    } // 获取所属部门

    public void setDepartment(Department department) {
        this.department = department;
    } // 设置部门

    public Set<Project> getProjects() {
        return projects;
    } // 获取参与的项目集合

    public Integer getVersion() {
        return version;
    } // 获取乐观锁版本号

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: JPA 实体的 equals/hashCode 约定
    // ─────────────────────────────────────────────────────────────────
    // 规则: 绝对不要在 equals/hashCode 中使用 @GeneratedValue 的 ID！
    //
    // 原因: 在 persist() 之前，ID 是 null。如果你把实体放入 HashSet，
    // persist() 后 hashCode 会变化 → 实体在 Set 中"丢失"了
    // (按照新的 hash 找不到它了)。
    //
    // 最佳实践:
    // 1. 使用自然键 (Natural Key)，如 email — 这里的做法
    // 2. 使用在构造时分配的 UUID
    // 3. 使用默认的 Object identity (对脱管实体有风险)
    //
    // getClass() vs instanceof:
    // 使用 getClass() 来防止 Hibernate 代理对象的问题，
    // 除非你确信所有子类共享相同的身份逻辑。
    // ═══════════════════════════════════════════════════════════════════
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; // 同一个对象引用 → 相等
        if (o == null || getClass() != o.getClass())
            return false; // null 或不同类型 → 不等
        Employee employee = (Employee) o; // 安全类型转换
        return Objects.equals(email, employee.email); // 基于 email (自然键) 比较
    }

    @Override
    public int hashCode() {
        return Objects.hash(email); // 基于 email 计算哈希值
    }

    @Override
    public String toString() {
        // 返回员工的字符串表示 — 不包含关联实体，避免触发懒加载和循环引用
        return "Employee{id=" + id + ", name='" + firstName + " " + lastName +
                "', email='" + email + "', status=" + status + "}";
    }
}
