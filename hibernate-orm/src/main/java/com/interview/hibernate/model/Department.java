package com.interview.hibernate.model; // 包声明

import jakarta.persistence.*; // JPA 所有注解 (Entity, Table, Id, Column 等)
import java.time.LocalDateTime; // 日期时间类
import java.util.ArrayList; // ArrayList — 可变长度列表
import java.util.List; // List 接口

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ 面试要点: @OneToMany — 双向一对多关系 ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Department 是关系的反转端 (inverse/non-owning side) ║
 * ║ Employee 是关系的拥有端 (owning side) — 它有外键列 dept_id ║
 * ║ ║
 * ║ 双向 @OneToMany 的关键规则: ║
 * ║ 1. @OneToMany 端必须用 mappedBy = "<拥有端的字段名>" ║
 * ║ 2. @ManyToOne 端拥有外键 — 它是数据库中的"真相" ║
 * ║ 3. 必须同步两端 (添加到集合 + 设置父引用) ║
 * ║ 4. 使用辅助方法 (addEmployee/removeEmployee) 保持双向同步 ║
 * ║ ║
 * ║ 面试要点: 级联类型 (Cascade Types) ║
 * ║ ───────────────────────────────────────────────────────────────────── ║
 * ║ • PERSIST → 保存父实体时自动保存子实体 ║
 * ║ • MERGE → 合并父实体时自动合并子实体 ║
 * ║ • REMOVE → 删除父实体时自动删除子实体 ║
 * ║ • ALL → 以上全部 + DETACH + REFRESH ║
 * ║ • orphanRemoval=true → 从集合中移除子实体时自动从数据库删除 ║
 * ║ ║
 * ║ ⚠️ 在 @ManyToOne 端使用 CascadeType.ALL 几乎总是 BUG！ ║
 * ║ (删除一个员工会连带删除整个部门) ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Entity // 标记此类为 JPA 实体 — Hibernate 会为它创建/映射数据库表
@Table(name = "departments") // 指定映射的表名为 "departments" (不指定则默认用类名)
public class Department {

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: 主键生成策略 (ID Generation Strategies)
    // ─────────────────────────────────────────────────────────────────
    // • IDENTITY → 使用数据库自增 (先 INSERT，再获取 ID)
    // ⚠️ 会阻止 JDBC 批处理 (Hibernate 必须逐条 INSERT
    // 才能获取生成的 ID)
    // • SEQUENCE → 使用数据库序列 (批处理性能最佳)
    // • TABLE → 用一张表模拟序列 (可移植但性能差)
    // • AUTO → Hibernate 根据方言自动选择策略
    // • UUID → Hibernate 6 支持 @UuidGenerator
    //
    // SQLite 使用 IDENTITY 是最自然的选择 (AUTOINCREMENT)。
    // ═══════════════════════════════════════════════════════════════════
    @Id // 标记此字段为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略: 数据库自增
    private Long id; // 主键字段 — 使用 Long 类型 (可为 null)

    // 部门名称 — nullable=false: 不允许为空, unique=true: 唯一约束, length=100: 最大100字符
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    // 部门描述 — 最多500字符
    @Column(name = "description", length = 500)
    private String description;

    // ═══════════════════════════════════════════════════════════════════
    // ── 双向 @OneToMany 关系映射 ──
    // mappedBy = "department" → 指向 Employee 类中的 department 字段
    // 表示 Employee 是关系的拥有端 (拥有外键 dept_id)
    // cascade = ALL → 对 Department 的 persist/merge/remove 操作会传播到 employees
    // orphanRemoval = true → 从 employees 列表中移除的 Employee 会从数据库删除
    // fetch = LAZY → 延迟加载: 只在访问 employees 时才查询数据库
    //
    // 面试要点: @OneToMany 默认就是 LAZY，这里显式声明只是为了代码清晰
    // ═══════════════════════════════════════════════════════════════════
    @OneToMany(mappedBy = "department", // 反转端: 由 Employee.department 字段拥有外键
            cascade = CascadeType.ALL, // 级联所有操作
            orphanRemoval = true, // 孤儿删除: 移除即删除
            fetch = FetchType.LAZY) // 延迟加载
    private List<Employee> employees = new ArrayList<>(); // 初始化为空列表，避免 NullPointerException

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: @Embedded vs @OneToOne
    // ─────────────────────────────────────────────────────────────────
    // @Embedded → 列直接内嵌到当前表 (不需要额外的表)
    // @OneToOne → 创建一个外键指向另一张表
    // 当对象是纯值对象 (没有独立标识) 时，使用 @Embedded
    // ═══════════════════════════════════════════════════════════════════
    @Embedded // 嵌入 AuditInfo 的字段到 departments 表中
    private AuditInfo auditInfo; // 审计信息 (创建时间、更新时间)

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: 实体生命周期回调 (Entity Lifecycle Callbacks)
    // ─────────────────────────────────────────────────────────────────
    // @PrePersist → INSERT 之前调用
    // @PostPersist → INSERT 之后调用
    // @PreUpdate → UPDATE 之前调用
    // @PostUpdate → UPDATE 之后调用
    // @PreRemove → DELETE 之前调用
    // @PostLoad → SELECT 之后 (实体从数据库加载后) 调用
    //
    // 替代方案: @EntityListeners(AuditListener.class) 可以将回调逻辑
    // 抽取到独立的监听器类中，实现跨实体复用。
    // Spring Data 的 @CreatedDate 就是基于这个机制实现的。
    // ═══════════════════════════════════════════════════════════════════
    @PrePersist // INSERT 前自动调用此方法
    public void onPrePersist() {
        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
        this.auditInfo = new AuditInfo(now, now); // 创建审计信息，创建时间和更新时间相同
    }

    @PreUpdate // UPDATE 前自动调用此方法
    public void onPreUpdate() {
        if (this.auditInfo != null) { // 防止空指针
            this.auditInfo.setUpdatedAt(LocalDateTime.now()); // 更新"修改时间"
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // 面试要点: 双向关系同步辅助方法
    // ─────────────────────────────────────────────────────────────────
    // 在双向关系中，你必须保持两端同步。
    // 如果没有这些辅助方法，你可能会把 Employee 添加到列表中，
    // 却忘记调用 employee.setDepartment(this)，导致数据库中外键为 NULL
    // (因为 Employee 是拥有端，外键由它控制)。
    // ═══════════════════════════════════════════════════════════════════

    /**
     * 添加员工到此部门 — 同步双向关系
     * 
     * @param employee 要添加的员工
     */
    public void addEmployee(Employee employee) {
        employees.add(employee); // 将员工添加到部门的员工列表
        employee.setDepartment(this); // 同时设置员工的部门引用 → 保持双向同步
    }

    /**
     * 从此部门移除员工 — 同步双向关系
     * 因为 orphanRemoval=true，从列表中移除后会自动从数据库删除
     * 
     * @param employee 要移除的员工
     */
    public void removeEmployee(Employee employee) {
        employees.remove(employee); // 从列表中移除
        employee.setDepartment(null); // 解除员工对部门的引用
    }

    // ── 构造函数 ──

    // JPA 规范要求的无参构造函数 — Hibernate 通过反射使用它来创建实例
    public Department() {
    }

    // 便捷构造函数 — 用于代码中快速创建 Department 对象
    public Department(String name, String description) {
        this.name = name; // 设置部门名称
        this.description = description; // 设置部门描述
    }

    // ── Getter 和 Setter 方法 ──

    public Long getId() {
        return id;
    } // 获取主键 ID

    public String getName() {
        return name;
    } // 获取部门名称

    public void setName(String name) {
        this.name = name;
    } // 设置部门名称

    public String getDescription() {
        return description;
    } // 获取部门描述

    public void setDescription(String description) {
        this.description = description;
    } // 设置描述

    public List<Employee> getEmployees() {
        return employees;
    } // 获取员工列表

    public AuditInfo getAuditInfo() {
        return auditInfo;
    } // 获取审计信息

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    } // 设置审计信息

    @Override
    public String toString() {
        // 返回部门的字符串表示 — 注意不要包含 employees 列表，否则会触发懒加载
        return "Department{id=" + id + ", name='" + name + "'}";
    }
}
