package com.interview.hibernate.model; // 包声明 — 将此类归类到 model 包下

import jakarta.persistence.Column; // JPA 注解: 映射数据库列
import jakarta.persistence.Embeddable; // JPA 注解: 标记为可嵌入组件
import java.time.LocalDateTime; // Java 8+ 日期时间类 (不可变, 线程安全)

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ 面试要点: @Embeddable — JPA 中的值对象 (Value Object) ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ @Embeddable 是一个值对象 — 它没有自己的标识 (identity) ║
 * ║ 它的列会直接存储在拥有它的实体所在的表中 (不需要 JOIN) ║
 * ║ ║
 * ║ 与 @Entity 的关键区别: ║
 * ║ • 没有 @Id — 嵌入对象没有自己的主键 ║
 * ║ • 没有独立的表 — 列直接内嵌到父表中 ║
 * ║ • 生命周期与父实体绑定 (父实体删除，嵌入对象也消失) ║
 * ║ • 应该基于所有字段实现 equals/hashCode ║
 * ║ ║
 * ║ 常见使用场景: Address(地址), Money(金额), DateRange(日期范围), ║
 * ║ AuditInfo(审计信息), GeoCoordinate(地理坐标) ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Embeddable // 标记此类为可嵌入组件，可以被 @Embedded 引用嵌入到其他实体表中
public class AuditInfo {

    // 创建时间 — updatable=false 表示此列在 UPDATE 时不会被修改
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 更新时间 — 每次 UPDATE 时会被修改
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── JPA 规范要求所有 @Embeddable 和 @Entity 必须有无参构造函数 ──
    // Hibernate 通过反射调用此构造函数来创建对象实例
    public AuditInfo() {
    }

    // 全参构造函数 — 方便代码中直接创建实例
    public AuditInfo(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt; // 设置创建时间
        this.updatedAt = updatedAt; // 设置更新时间
    }

    // ── Getter 和 Setter 方法 ──
    // Hibernate 通过这些方法读写字段值 (字段访问 vs 属性访问取决于 @Id 的位置)

    public LocalDateTime getCreatedAt() {
        return createdAt; // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; // 设置创建时间
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt; // 返回更新时间
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt; // 设置更新时间
    }

    @Override
    public String toString() {
        // 返回对象的字符串表示，方便日志输出和调试
        return "AuditInfo{created=" + createdAt + ", updated=" + updatedAt + "}";
    }
}
