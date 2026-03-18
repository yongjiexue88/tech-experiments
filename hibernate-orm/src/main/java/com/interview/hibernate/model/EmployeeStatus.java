package com.interview.hibernate.model; // 包声明

/**
 * 员工状态枚举。
 *
 * 面试要点: 在 Employee 实体中使用 @Enumerated(EnumType.STRING)，
 * 因此这些值以 "ACTIVE", "INACTIVE", "ON_LEAVE" 字符串形式存储在数据库中，
 * 可以安全地重新排序而不会导致数据损坏。
 */
public enum EmployeeStatus {
    ACTIVE, // 在职 — 正常工作状态
    INACTIVE, // 停用 — 账号已停用
    ON_LEAVE, // 休假 — 暂时离岗
    TERMINATED // 离职 — 已终止雇佣关系
}
