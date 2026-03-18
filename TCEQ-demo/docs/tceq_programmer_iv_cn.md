# TCEQ Programmer IV — 面试准备指南（中文直译完整版）

> **角色：** Programmer IV（州职位代码 0244A，薪级 B26）  
> **部门：** Information Resources Division (IRD)，Administrative Services  
> **地点：** 12100 Park 35 Circle, Austin, TX 78753  
> **薪资：** $7,745/月（约 $92,940/年），固定  
> **招聘人数：** 2 | **时间安排：** 周一至周五，上午 8 点至下午 5 点 | **加班：** Exempt（豁免加班费）  
> **发布时间：** 2026 年 1 月 16 日 – 2026 年 1 月 30 日

---

> **【新增说明｜核验与补充】**  
> 我已在不删除原始内容的前提下，对这份指南进行了完整中文直译，并补充了“公开信息核验与使用建议”。需要注意：本指南正文中关于 **“2026 年 1 月 16 日—1 月 30 日、2 个 openings”** 的这组信息，我未找到同一版本的完整官方 CAPPS 归档页面做逐项核实；但我找到了 **TCEQ 2026 年 3 月公开发布的同类 Programmer IV 岗位**，其职责、技术关键词、地址、州职位代码、B26 级别、固定月薪、HQ 地址等与本指南核心判断高度一致。因此，本文件已：  
> 1. **完整保留并翻译你原有全部内容**；  
> 2. **不删除任何原段落、表格、问题库或策略内容**；  
> 3. 在文末新增“**附录 A：公开信息核验与使用建议**”，将“已核实”和“建议谨慎表述”的内容分开说明，方便你面试时更稳妥地使用。  

---

## 目录

1. [评分机制是如何运作的（利用评分矩阵）](#1-评分机制是如何运作的利用评分矩阵)
2. [这份工作可以概括为六个要点](#2-这份工作可以概括为六个要点)
3. [面试形式与流程安排](#3-面试形式与流程安排)
4. [JAD 主持能力与战略顾问能力](#4-jad-主持能力与战略顾问能力)
5. [技术设计与架构](#5-技术设计与架构)
6. [代码领导力与最佳实践](#6-代码领导力与最佳实践)
7. [QA/QC 与测试协作](#7-qaqc-与测试协作)
8. [客户支持](#8-客户支持)
9. [行为面试题库](#9-行为面试题库)
10. [书面测试 / 实操练习准备](#10-书面测试--实操练习准备)
11. [薪酬定位策略](#11-薪酬定位策略)
12. [面试当天检查清单](#12-面试当天检查清单)
13. [技术题题库——按技术栈划分](#13-技术题题库按技术栈划分)
14. [高级行为面试题](#14-高级行为面试题)
15. [你应该问他们的问题](#15-你应该问他们的问题)
16. [现场面试礼仪](#16-现场面试礼仪)
17. [系统设计题](#17-系统设计题)
18. [算法与数据结构题](#18-算法与数据结构题)
19. [高级 Java 问题](#19-高级-java-问题)
20. [Web 架构问题](#20-web-架构问题)

---

## 1. 评分机制是如何运作的（利用评分矩阵）

> **这是你最需要理解的一点。** 德州州政府的招聘面试小组通常使用标准化的 Interview Scoring Matrix（面试评分矩阵）。他们不能凭“感觉”做决定。

### 它是如何运作的

- 每一道回答都会由每位 panelist（面试官）独立按照 **Likert scale（1–5 分量表）** 打分
- 分数会被汇总 → **总分最高的人** 获得推荐给 HRSS 的资格
- 面试小组 **只能为你明确说出来的内容打分** ——如果你没有说出那个关键词，他们就不能勾选对应的格子

### 在每个回答中自然塞入这些 FJD 关键词

招聘公告使用的是特定措辞。你要**镜像复用**这些词：

| FJD 原话（招聘公告逐字表述） | 自然融入方式 |
|---|---|
| "agency information technology (IT) standards" | “……同时遵循 agency IT standards……” |
| "requirements documentation" | “……基于 requirements documentation……” |
| "requirements and design documentation" | “……依据 requirements and design documentation 进行测试……” |
| "configuration management" | “……通过我们的 configuration management process 跟踪……” |
| "quality assurance / quality control" | “……协同 QA/QC testing……” |
| "customer acceptance testing" | “……引导客户完成 customer acceptance testing……” |
| "peer reviews" | “……在合并前先进行 peer review……” |
| "coding best practices, techniques, and training needs" | “……审查 coding best practices 并识别 training needs……” |
| "charters" / "business cases" | “……在 project charter 和 business case 中进行文档化……” |
| "project plans and status reports" | “……通过 status reports 沟通进展……” |
| "sponsor meetings" | “……在 sponsor meetings 上进行汇报……” |
| "facilitate joint application development (JAD) sessions" | “……这是我们在 JAD session 中定义出来的……” |
| "analysis, development, programming, and implementation/modification" | 在描述你的工作时直接用这一整串 |

> **经验法则：** 如果你能在每个回答里自然穿插 3–4 个 FJD 关键词，你就在最大化自己的评分矩阵得分。

---

## 2. 这份工作可以概括为六个要点

招聘公告实际上列出了六项职责。面试问题大概率会一一对应：

| # | 职责（来自原招聘） | 他们会问什么 |
|---|---|---|
| 1 | 领导/参与分析、开发、编程、实施/修改 | “请讲一个你从分析到上线全流程主导的系统” |
| 2 | 领导编码工作；审查/评估编码最佳实践与培训需求 | “你如何执行代码标准？” / “你怎么带人？” |
| 3 | 基于 requirements documentation 提供技术建议并做设计工作 | “带我们过一遍你的一个设计决策” / “你如何把需求转成架构？” |
| 4 | 协调 QA/QC；参与/主导 peer review；引导 customer acceptance testing | “你如何定义 done？” / “你怎么引导非技术用户做测试？” |
| 5 | 制作 charters、business cases、project plans、status reports；主持 JAD sessions | “你如何主持需求收集？” / “你如何处理 stakeholder 冲突？” |
| 6 | 为 agency staff 提供 customer support | “你如何处理一个很沮丧的用户？” |

### 三项关键资格（来自招聘公告）

这是评分矩阵最可能重点加权的部分：

1. **ColdFusion, Java, Hibernate, Oracle, SQL, PL/SQL, JavaScript, CSS, XML, HTML** ——你的 demo project 覆盖了这些
2. **Configuration management, quality assurance, change management** ——可以讲分支策略、CI 门禁、peer review 和发布检查清单
3. **Business analysis：requirements、design/test plan documentation、JAD sessions** ——这会是差异化亮点

---

## 3. 面试形式与流程安排

| 可能遇到的情况 | 细节 |
|---|---|
| **面试小组人数** | 2–3 人：hiring manager + technical SME + 可能还有业务方代表 |
| **风格** | 按脚本顺序提问；panelists **不会**重述、改写或临场发挥太多 |
| **多段式问题** | 他们通常只会把整道题再重复一遍，不会帮你拆开 |
| **时长** | 约 1 小时；可能先 phone screen，再到现场/Teams |
| **书面环节** | 很可能有——SQL debugging、code review、design prompt 或 requirements writing |
| **到 offer 的时间** | 面试后约 4–6 周 |
| **背景调查** | 招聘公告已确认会做 criminal history records search |
| **关键基础设施** | 这个岗位可能接触关键基础设施（cybersecurity、hazardous waste systems、water treatment）——依据 Texas Business & Commerce Code §117.001(2) |

### 多段式问题应对策略

1. **在记事本上记下关键词**，趁他们念题的时候写
2. **先复述结构：** “这个问题有三个部分，我分别回答。”
3. **显式分段：** “第一部分……第二部分……”
4. **最后总结：** “把它们综合起来讲……”

---

## 4. JAD 主持能力与战略顾问能力

> 招聘公告明确写了：**“facilitate joint application development (JAD) sessions.”** 这正是区分中级 coder 和 Programmer IV 的关键点。

### 他们会问什么

**“你如何主持一场 requirements gathering session？”**

> “我会使用 JAD 方法。在会前，我会准备 agenda，识别合适的 stakeholders——program area SMEs、IT security、development lead——并提前发出 pre-read materials。会议中，我作为 **facilitator（主持人）**：我会控制议程，把技术术语翻译成业务语言，并维护一个 **parking lot（停车场清单）** 来记录超出范围的想法。我会指定一名 scribe 实时记录决策。产出物包括：带有 acceptance criteria 的 **requirements documentation**、process flow diagrams，以及包含 scope 和 constraints 的 **project charter**，这些都能直接进入后续 **design** 评审，并最终驱动 **customer acceptance testing** 场景。”

### 冲突场景

| 场景 | 你的回应 |
|---|---|
| **“一个项目经理坚持要一个违反 IT security 的功能。”** | “我会先确认其底层业务需求，然后客观说明限制条件，再协作设计一个 **secure alternative（安全替代方案）**，在满足目标的同时遵循 **agency IT standards**。” |
| **“参会者开始跑题，开始抱怨别的问题。”** | “我会先认可：‘这个很重要——我把它加入 **parking lot**。’ 然后把焦点拉回来：‘这场会我们今天必须在下午 3 点前把 permit workflow 定下来。’” |
| **“两个 stakeholder 对一个业务规则意见不一致。”** | “我会中立地复述双方立场，然后问：‘我们能不能把这两种方案都拿来对照 **requirements documentation** 测一下？’ 如果还是不能解决，我会把两个选项及其 trade-offs 文档化，并升级到 **sponsor meeting** 处理。” |

### 你的 JAD / 顾问型 STAR 故事

| | |
|---|---|
| **Situation** | 构建 TCEQ compliance dashboard 需要先定义“哪些指标才重要”——facilities、violations、penalties、overdue inspections、expiring permits——这些在不同 stakeholder 眼中权重并不一样。 |
| **Task** | 把 regulatory compliance requirements 转换成一个一致的数据模型和双视图 UI。 |
| **Action** | 我创建了 **requirements documentation**，定义 9 个 dashboard metrics，并给出清晰的 acceptance criteria。我对领域进行了建模：Facilities → Permits, Inspections, Violations（1:N）。我在 **business case** 中说明了 dual rendering（交互式 SPA + server-rendered report）的理由，以满足不同用户需求。compliance scoring algorithm 在编码前就已经写入 **design documentation**。 |
| **Result** | 最终产出了 9 个实时指标、3 个图表可视化和一个可搜索的 facility table——全部都能回溯到 **requirements documentation**。dual-view 设计也展示了服务不同 stakeholder 的灵活性。 |

---

## 5. 技术设计与架构

> 招聘公告写的是：**“Provide technical advice and perform design tasks… based on requirements documentation while adhering to agency IT standards.”**

### 架构回答模板

**“请讲讲你会如何设计一个 compliance tracking application。”**

> “我会采用一个遵循 **agency IT standards** 的 **layered architecture（分层架构）**：presentation → controller → service → repository → persistence。在 **Java**/Spring 中，service layer 负责 business logic，repository layer 通过 **Hibernate**/JPA 访问 **Oracle**。Oracle schema 我会设计成带有 **sequences**、级联删除的 foreign keys、**CHECK constraints**，并在所有过滤列上建立有策略的 indexes。ColdFusion presentation layer 可以在分阶段迁移时继续存在，只需要指向同一个 Oracle datasource。安全方面，所有查询都使用 parameterized inputs 来防 SQL injection，所有输出都做编码来防止 XSS。”

### 数据库（Oracle SQL / PL/SQL）

**问：** “带我过一遍你如何诊断一条慢查询。”

> “第一步，我会拉 execution plan——`EXPLAIN PLAN FOR` + `DBMS_XPLAN.DISPLAY`——查看是否出现了本该走 index scan 却变成 full table scan 的情况。我会检查统计信息是否过期，比如用 `SELECT last_analyzed FROM user_tables`，必要时用 `DBMS_STATS` 重收集。常见修复手段包括：增加缺失索引（特别是 foreign key 列——在我的 demo 中我给每个 FK 都建索引）、把 correlated subquery 重写成 JOIN，以及检查是否存在 implicit type conversion。在我的 compliance demo 中，我为 `FACILITY_ID`、`STATUS`、`SEVERITY` 和 `EPA_ID` 建了索引，因为这些都是 dashboard 的过滤列。”

**问：** “你如何设计 backwards-compatible 的 schema changes？”

> “三条规则：（1）部署期间只做 **additive changes** ——新增列要么 nullable，要么带默认值；（2）**schema first, then code** ——这样旧版本和新版本都能同时工作；（3）使用 **versioned migrations** 并且准备 rollback scripts。每个变更都要经过 **configuration management** 和 **peer review**。这也符合 TCEQ 对 **change management** 的期待。”

### Web Services（SOAP vs REST）

**问：** “什么时候你会选 SOAP 而不是 REST？”

> “如果是正式的跨机构数据交换，而且你需要 WS-Security、签名/加密消息、WSDL 契约、事务支持，我会选 **SOAP**。很多政府对政府的集成会偏好 SOAP，因为它更强调审计性。**REST** 则更适合内部 API，被 JavaScript 前端消费时 JSON 更自然。我的 demo 使用的是 REST（`/api/facilities`, `/api/dashboard/stats`）来支撑内部 dashboard。”

### Java + Hibernate

**问：** “讲一个由 ORM 行为导致的生产问题。”

> “典型就是 N+1 问题：先查 facility 列表，再在循环里访问 `facility.getPermits()`，结果每个 facility 又多打一条 SELECT。在我的 demo 里，`getFacilitySummaries()` 就有这个潜在问题——在 H2 内存库里不明显，但在生产 **Oracle**、几千行数据时会非常致命。修复方式可以是 JPQL 的 `JOIN FETCH` 或 `@EntityGraph`。我会在 **QA/QC** 里通过 integration tests 监控 query count，并在 **peer review** 阶段就把它指出来。”

### ColdFusion

**问：** “说一个你会使用的 ColdFusion 模式。”

> “在我的 `facility_search.cfm` 里，我用了动态 `WHERE 1=1` 配合条件 `<cfif>` 块，使用带明确 `cfsqltype` 的 `<cfqueryparam>` 来防 SQL injection，使用 `HTMLEditFormat()` 做 XSS 防护，并通过 `startrow` / `maxrows` 实现 server-side pagination。在 `permit_report.cfm` 里，我用了 `<cfstoredproc>` 调用 Oracle **PL/SQL**，用 `<cfchart>` 做图表可视化，用 **Query-of-Queries** 做内存态数据重组，并通过 `<cfdocument>` 生成 PDF。”

### Linux / Shell Scripting

**问：** “你怎么写 production shell scripts？”

> “三个原则：（1）**idempotent（幂等）** ——`mkdir -p`、`CREATE TABLE IF NOT EXISTS`；（2）**fail-fast** ——`set -euo pipefail`；（3）**logged** ——输出重定向到带时间戳的日志文件。Secrets 从环境变量读取，绝不硬编码。”

### Change Management

**问：** “你如何安全发布？”

> “Feature branch → **peer review** → CI 跑测试 → 部署到 staging → 根据 **design documentation** 做 **QA/QC** sign-off → change request 获批 → 带 rollback script 上生产。我的 demo 里用 `./mvnw test` 作为 CI gate——15 个测试全部通过。在生产里，我还会加 static analysis 和 schema migration validation。”

---

## 6. 代码领导力与最佳实践

> 招聘公告写的是：**“Lead coding tasks… Review and evaluate coding best practices, techniques, and training needs.”**

### STAR 故事

| | |
|---|---|
| **Situation** | 我需要构建一个 compliance system，用来展示 TCEQ 整个技术栈下的 production patterns：**Java, Hibernate, Oracle, SQL, PL/SQL, ColdFusion, JavaScript, CSS, XML, HTML**。 |
| **Task** | 把它组织成任何开发者——包括初级的 Programmer I–III——都能维护和扩展的结构。 |
| **Action** | 我强制执行 **layered architecture**：model → repository → service → controller。每个 entity 都有文档。为了同时体现 legacy 和 modern patterns，我同时使用 annotation 和 **XML** Hibernate mappings。编写了 15 个 JUnit 5 测试，覆盖 repository queries、service logic 和 compliance scoring。Oracle-compatible DDL 统一命名（`FK_`、`UK_`、`CHK_`、`IDX_` 前缀）。 |
| **Result** | 代码库具备自文档特性：`architecture.md` 把每条 requirement 对应到实现位置。测试一条命令即可跑通。新开发者只需要读 README 就能上手，这本身就是一种我会给团队提供的 **training artifact（培训型文档）**。 |

**问：** “你如何做 code review？”

> “我会从四个维度审查：（1）**correctness** ——是否符合 **requirements documentation**；（2）**security** ——参数化查询、输入校验、输出编码；（3）**maintainability** ——命名是否清晰、职责是否分离；（4）**test coverage** ——没有测试的业务逻辑通常要打回修改。我会给出建设性且具体的反馈，并把 **peer reviews** 当作识别 **training needs** 的机会——这正是招聘公告里点出来的重点。”

---

## 7. QA/QC 与测试协作

> 招聘公告写的是：**“Coordinate testing and Quality Assurance/Quality Control tasks… participate/lead peer reviews. Guide and assist customers with customer acceptance testing.”**

### STAR 故事

| | |
|---|---|
| **Situation** | compliance scoring algorithm 属于业务关键逻辑——如果打分错误，会直接误导一个 facility 的环境合规状态。 |
| **Task** | 确保 scoring logic 得到充分测试，并且能够追溯到 **requirements and design documentation**。 |
| **Action** | 我为边界条件编写了 JUnit tests：如 clean facility（≥ 90）、带 2 个 CRITICAL violations 的 facility（≤ 40）。我验证了 deduction table：-5 MINOR / -15 MAJOR / -30 CRITICAL / -10 expired permit / -5 overdue inspection。我把算法写进 `architecture.md`，这样 **QA/QC** 审查人员和 **customer acceptance testing** 参与者就算不看代码，也能验证逻辑意图。 |
| **Result** | 15 个测试全部通过。评分规则清晰、可测、可追溯，任何 auditor 都能核对测试覆盖。 |

**问：** “你如何引导客户做 acceptance testing？”

> “我会从 JAD session 里定义出的 acceptance criteria 反推一份 **test plan**。步骤用 plain language 编写：‘Step 1：搜索 facility X。Expected：出现 3 条结果。’ 我会先现场带着用户做前几个 scenario，然后转为观察模式。缺陷会按 severity 打标签、triage，修复后再复测。目标是让业务用户真正对质量有 ownership，而不是走形式签字。”

---

## 8. 客户支持

> 招聘公告写的是：**“Provide customer support to agency staff specific to agency applications, IT policies, and general related IT areas.”**

**问：** “你如何支持 agency staff？”

> “用耐心和同理心。Agency staff 往往是领域专家——环境科学家、permit reviewers——而不是 IT 专业人员。我会先倾听，理解他们的 workflow，再定位技术根因。我会用业务流程语言解释修复方案，而不是 stack trace。在我的应用设计中，我也会做防御式处理：REST APIs 对缺失记录返回 HTTP 404 而不是 500；ColdFusion templates 使用 `<cftry>/<cfcatch>`；service layer 使用 `@Transactional(readOnly = true)` 以避免意外修改。”

---

## 9. 行为面试题库

### “我们为什么要雇你？”

> “我具备这个岗位明确列出的完整技术栈——**ColdFusion, Java, Hibernate, Oracle, SQL, PL/SQL, JavaScript, CSS, XML, HTML**——并且我已经在一个可运行的 compliance tracking application 中展示了这些能力。但这个岗位不只是写代码。它还明确要求 **主持 JAD sessions**、编写 **charters 和 business cases**、**主导 peer reviews**，以及引导 **customer acceptance testing**。我有贯穿整个生命周期、把业务需求和技术落地衔接起来的经验。我也被 TCEQ 保护公共健康和自然资源的使命所吸引，同时我重视这类工作所提供的 **长期稳定性** 与实际影响力。”
>
> *(命中点：完整技术栈、JAD、charters、peer reviews、customer acceptance testing、使命感、稳定性/留任信号)*

### “讲一个你和同事有冲突的例子。”

> “我曾经和一位 teammate 在‘应该重构一个 legacy module，还是只打一个 patch’这个问题上意见不一致。与其争论谁感觉对，我提议先 **timebox 一个 spike**——用两个小时估算 scope。结果 spike 证明完整 refactor 需要 3 天，但路径清晰，于是我们决定走重构，同时做一个 shim 先 unblock 当前 sprint。我的经验是：拿出数据驱动的评估方式。这也是我在 **JAD session** 里处理分歧时会采用的方式——先降温、再收集证据、然后协作决策。”

### “讲一个你失败的经历。”

> “我曾经上线过一个 schema migration，它在小数据量 QA 环境里是通过的，但在生产数据量下锁表 20 分钟。我的收获是：（1）migration 必须在接近生产规模的数据上测试；（2）把 migration timing 纳入 **QA/QC**；（3）每个 **change management** 请求都要带 rollback scripts。从那以后，我把 schema changes 也当作和代码一样严格的对象来看待——**peer review**、staging 验证、rollback 文档缺一不可。”

### “你如何向非技术用户解释技术概念？”

> “会用他们熟悉领域里的类比。比如，‘数据库索引就像文件柜的标签页——没有它你就得把每个抽屉都打开，才能找到 Harris County。’ 在我的 demo 中，我做了 dual views——交互式 SPA 和传统 server-rendered 页面——因为不同用户偏好不同。在 **JAD sessions** 中，我也会实时把开发术语翻译成业务流程语言。”

### “你如何给 competing demands 排优先级？”

> “Impact × urgency。一个阻塞 compliance reporting 的生产问题，优先级一定高于一个新的 feature request。我会通过 **status reports** 清晰说明排序理由，让 stakeholders 理解。如果优先级冲突升级到领导层，我会把 trade-off 写清楚后提交给 **project sponsor** 决策。”

---

## 10. 书面测试 / 实操练习准备

### SQL 练习

**“找出 bug 并修复。”**

```sql
-- Buggy: double LEFT JOIN fans out rows
SELECT f.facility_name,
       COUNT(p.id) AS permit_count,
       COUNT(v.id) AS violation_count
FROM   facilities f
LEFT JOIN permits    p ON f.id = p.facility_id
LEFT JOIN violations v ON f.id = v.facility_id
GROUP BY f.facility_name;
```

**答案：** “双 LEFT JOIN 会制造笛卡尔放大——3 个 permits × 2 个 violations = 6 行，导致两个 count 都被放大。修复方式：`COUNT(DISTINCT p.id)` 和 `COUNT(DISTINCT v.id)`，或者改写成 correlated subqueries。”

### 代码审查

**“指出问题。”**

```java
public List<Facility> search(String term) {
    String sql = "SELECT * FROM facilities WHERE name LIKE '%" + term + "%";
    return jdbcTemplate.query(sql, new FacilityRowMapper());
}
```

**答案：** “（1）**SQL injection** ——字符串拼接；应改为 `?` 参数占位符。（2）`term` **没有 null check**。（3）前导 `%` 会让索引失效——可以考虑 Oracle Text `CONTAINS()`。（4）**没有大小写归一化** ——可加 `UPPER()`。（5）**没有分页** ——`SELECT *` 无上限。”

### 设计 / 需求题

**“某部门需要 permit renewal tracking 和 notifications。请概述你的方案。”**

**答题骨架：**
- **Charter：** Scope、stakeholders、constraints、timeline
- **Requirements（来自 JAD session）：** 跟踪到期日、90/60/30 天自动通知、记录 renewal submissions、approval workflow
- **Data model：** `PERMIT_RENEWALS (id, permit_id FK, due_date, notification_date, status, reviewer_id)`
- **Architecture：** 定时批任务（Spring `@Scheduled` 或 Oracle `DBMS_SCHEDULER`）→ 查询即将到期 permit → 创建 notification records → 集成 SMTP
- **QA/QC：** 日期逻辑的单元测试、mock email 的集成测试、**customer acceptance testing** 脚本
- **Change management：** Feature branch → **peer review** → staging → QA sign-off → change request → 带 rollback 的 production deploy

---

## 11. 薪酬定位策略

> **有两个职位空缺，薪资固定为 $7,745/月。** 面试小组想知道你是否会留下来。

### 不要说什么

- 不要问快速涨薪或 equity packages
- 不要拿私企 offer 来比较
- 不要表现出你特别在意 promotion timeline

### 要说什么

- **“我重视长期稳定性”** ——这是留任信号
- **“我被 TCEQ 的使命感吸引——保护公共健康和自然资源”** ——这与招聘文案高度一致
- **“整体补偿很有吸引力——401(k)/457、完整 health/dental/vision、longevity pay、wellness programs”** ——说明你认真读过福利部分
- **“我想建设并维护那些会长期服务 Texans 的系统”** ——把自己定位成长期投入者

### 福利（来自原招聘）

- 401(k) 和 457 计划
- Health, Vision, Dental 保险 + 可选 FSA
- 带薪假日（联邦/州法定节假日）
- Professional development opportunities
- Longevity pay（随着州政府工龄增加）
- Wellness Program + 总部 onsite Nurse Practitioner
- Work-Life Balance

---

## 12. 面试当天检查清单

### 面试前
- [ ] 运行 `./mvnw clean test` ——确保 15 个测试全部通过
- [ ] 启动应用：`./mvnw spring-boot:run` ——dashboard 能在 `localhost:8080` 打开
- [ ] 打印：resume + `docs/architecture.md` diagram
- [ ] 把 STAR stories 大声练到每个都控制在 2 分钟内
- [ ] 复习：ColdFusion templates、PL/SQL procedures、`schema.sql`
- [ ] 再读一遍本指南，尤其是 **第 1 节**（评分机制）、**第 4 节**（JAD）和下面的 **关键词清单**

### 带什么
- [ ] Notepad 和笔（用来记多段式问题）
- [ ] 打印好的作品材料
- [ ] Photo ID（进 12100 Park 35 Circle 大楼可能需要）
- [ ] 如有要求，准备 criminal history records check 相关材料

### 可以问他们的问题
1. “目前 **ColdFusion-to-Java migration** 的 roadmap 是怎样的？”
2. “团队通常如何组织 **JAD sessions** ——是正式 workshop，还是更短的 sprint-style？”
3. “这里的 **peer review 和 change management** 流程通常是什么样？”
4. “有没有机会改进 **automated testing 或 CI/CD**？”
5. “IRD 目前的 **telework/hybrid policy** 是怎样的？”
6. “入职第一天我最可能接手哪些 systems 或 applications？”

### 物流安排
- [ ] Business attire
- [ ] 提前 15 分钟到 **12100 Park 35 Circle, Austin 78753**
- [ ] 提前规划停车
- [ ] 如果是 Teams：提前测试摄像头 + 麦克风，整理背景

---

## 快速参考：要主动说出来的 FJD 关键词

这些都是**招聘公告逐字用语**。面试时要把它们**说出来**：

> **analysis, development, programming, implementation/modification** · **agency IT standards** · **coding best practices, techniques, and training needs** · **requirements documentation** · **design documentation** · **configuration management** · **quality assurance / quality control** · **peer reviews** · **customer acceptance testing** · **charters** · **business cases** · **project plans and status reports** · **sponsor meetings** · **JAD sessions** · **customer support** · **application maintenance, support, and development**

你每说出一个关键词，就是给面试小组一个能在评分表上打勾的机会。**一定要说出来。**

---

## 关键基础设施说明

> 招聘公告中写道：*“Employees in this classification series may research, work on, or have access to critical infrastructure, including but not limited to a communication infrastructure system, cybersecurity system, electric grid, hazardous waste treatment system, or water treatment facility.”*

这意味着：（1）你要预期更严格的背景调查；（2）面试回答里要突出 **security-first coding practices**；（3）你要体现出你理解——你写的软件会直接影响真实世界中的环境与公共健康。

---

## 13. 技术题题库——按技术栈划分

招聘公告中列到的每种技术，都可能衍生 3–5 道面试题。你要全部准备到位。

---

### ColdFusion

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “什么是 `<cfqueryparam>`，为什么重要？” | 它通过绑定带类型的参数（`cfsqltype`）防止 SQL injection。绝不能把用户输入直接拼进 `<cfquery>`。 |
| 2 | “解释一下 Query-of-Queries（QoQ）。什么时候用？” | 它是在内存中的 ColdFusion recordset 上再跑 SQL——适合对已取回的数据做重组、过滤或 join，而不用再次访问数据库。我的 `permit_report.cfm` 就用 QoQ 从已拉取的数据里按 severity 过滤 violations。 |
| 3 | “你怎么在 ColdFusion 里生成 PDF 报表？” | 用 `<cfdocument format="PDF">` 包裹 HTML/CSS 内容并输出成 PDF。支持 header、footer、page breaks。很适合 agency staff 打印或归档 compliance reports。 |
| 4 | “你怎么从 ColdFusion 调 stored procedure？” | 用 `<cfstoredproc procedure="proc_name" datasource="ds">`，配合 `<cfprocparam>` 传 IN/OUT 参数，用 `<cfprocresult>` 接收结果集。我的 `permit_report.cfm` 展示了这个与 Oracle PL/SQL 联动的模式。 |
| 5 | “如果要把 ColdFusion 应用迁移到 Java，你会怎么做？” | 渐进式：（1）先找出业务逻辑最重的 CFM 页面；（2）把这些逻辑提取到 Java services，通过 REST 调用；（3）ColdFusion 页面先变成调用 Java API 的薄壳；（4）再逐步用 Thymeleaf 或 JS frontend 替换 CFM；（5）过渡期间两层共享同一个 Oracle datasource。 |
| 6 | “ColdFusion 的 Application scope 和 Session scope 有什么区别？” | Application scope 在整个应用生命周期内存在，所有用户共享（适合配置、缓存 lookup）；Session scope 是每个用户独立的，会超时。对可变数据滥用 Application scope 会引起并发 bug。 |

---

### Java

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “解释 interface 和 abstract class 的区别。” | Interface 更像 contract（做什么），abstract class 是 partial implementation（已经提供一部分行为）。Java 8 之后 interface 可以有 default methods，边界变得模糊；但 abstract class 仍然可以有状态（instance fields）和 constructor。 |
| 2 | “什么是 dependency injection，为什么重要？” | 类不自己 `new FacilityRepo()`，而是由外部注入依赖。好处：更易测试（可注入 mock）、更松耦合、更易做 **configuration management**。Spring Boot 会通过 `@Autowired` 或构造器注入自动处理。 |
| 3 | “解释 `try-with-resources`。” | 代码块结束时自动关闭实现了 `AutoCloseable` 的资源，避免 DB connections、file handles 等泄漏。对长期运行的 agency application 很重要。 |
| 4 | “什么是 Java Streams？举个例子。” | 它是处理集合的函数式管道式写法。比如我的 demo 里：`violations.stream().filter(v -> "OPEN".equals(v.getStatus())).toList()`，既无副作用，也更可读、可组合。 |
| 5 | “你如何在生产 Java 应用中处理异常？” | 区分 checked exceptions（可恢复，如 `IOException`）与 unchecked exceptions（编程错误，如 `NullPointerException`）。使用具体 catch、记录带上下文的日志（facility ID、user、timestamp）、REST controller 返回正确 HTTP 状态码（404、400、500），绝不默默吞异常。 |
| 6 | “什么是 Java Collections framework？什么时候用 `Map`，什么时候用 `List`？” | `List` 用于有序序列（facility search results），`Map` 用于 key-value 查找（如 dashboard stats 按 metric name 存放）。当显示顺序重要时可用 `LinkedHashMap`（比如我的 `getDashboardStats()` 方法）。 |
| 7 | “Java 的垃圾回收怎么工作？” | JVM 会自动回收没有引用的对象。典型是分代 GC（Young Gen → Old Gen）。可以用 `-verbose:gc` 监控。在长期运行的服务里，要防内存泄漏：未关闭资源、无限增长的静态集合、Hibernate session cache 等。 |

---

### Hibernate / JPA

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “什么是 N+1 问题，你如何修复？” | 查询父列表后再懒加载每个子集合，会变成 1 + N 条查询。修复方式：JPQL 的 `JOIN FETCH`、`@EntityGraph` 或 `@BatchSize`。我 demo 里的 `getFacilitySummaries()` 就是典型例子。 |
| 2 | “Lazy 和 Eager loading 的区别？什么时候用？” | Lazy（集合默认）：第一次访问才加载——适合你不一定会用到的大关联。Eager：立刻加载——只适用于很小、且总会被使用的关联。大集合做 Eager 常常会导致巨大 join。 |
| 3 | “Hibernate Session / EntityManager 生命周期是什么？” | EntityManager 通常是短生命周期（每请求一次）。Managed entities 在事务内会被跟踪并做 dirty checking。Detached entities（离开事务后）如果还要继续更新，需要 `merge()` 重新挂回。Spring 中通常由 `@Transactional` 管理。 |
| 4 | “JPA annotations 和 XML mapping 有什么区别？” | Annotation（`@Entity`, `@Column`）写在 Java 类里，是现代主流做法。XML（如 `Facility.hbm.xml`）把映射从代码中分离，是较老系统常见模式。我的 demo 同时展示两种，体现我能处理 legacy 和 modern patterns。 |
| 5 | “Hibernate 如何处理数据库生成 ID？” | 用 `@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facility_seq")` 配合 `@SequenceGenerator` 指向 Oracle sequence。我的 demo 里 Oracle 风格 `CREATE SEQUENCE` 与 annotation mapping 是配套的。 |
| 6 | “什么是 dirty checking？” | Hibernate 会自动检测 managed entity 字段变化，并在 flush 时生成 UPDATE。事务内不一定需要显式 `save()`。如果无意中改了对象，就可能出现“phantom updates”这类问题。 |

---

### Oracle SQL

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “写一条 SQL，找出 open violations 超过 3 个的 facilities。” | `SELECT f.facility_name, COUNT(v.id) AS viol_ct FROM facilities f JOIN violations v ON f.id = v.facility_id WHERE v.status = 'OPEN' GROUP BY f.facility_name HAVING COUNT(v.id) > 3;` |
| 2 | “`WHERE` 和 `HAVING` 的区别？” | `WHERE` 在分组前过滤行；`HAVING` 在聚合后过滤组。聚合函数不能直接放在 `WHERE` 中。 |
| 3 | “Oracle sequences 和 auto-increment 的区别？” | Oracle 用 `CREATE SEQUENCE` + `NEXTVAL` 来生成 ID（显式、可控、可配置 cache/increment）。MySQL/PostgreSQL 常见 `AUTO_INCREMENT` / `SERIAL`。在 Oracle 中 sequence 更灵活。 |
| 4 | “JOIN 有哪些类型？什么时候用？” | `INNER JOIN`：只要匹配；`LEFT JOIN`：保留左表全部；`FULL OUTER JOIN`：两边都保留；`CROSS JOIN`：笛卡尔积（很少希望出现）。在 compliance 场景里，LEFT JOIN facilities 到 violations 可以保留没有 violation 的 facility。 |
| 5 | “什么时候建索引，怎么建？” | `CREATE INDEX idx_name ON table(column);` 一般建在：FK 列、WHERE/ORDER BY/GROUP BY 中常用列、JOIN 列。不要乱建，索引会拖慢 INSERT/UPDATE。我的 demo 给每个 FK 和关键过滤列都建了索引。 |
| 6 | “`DELETE`、`TRUNCATE`、`DROP` 的区别？” | `DELETE`：删行，可带 WHERE，可回滚，会触发 trigger。`TRUNCATE`：删全表数据，日志少，通常不可回滚，重置高水位。`DROP`：连表结构一起删。在监管环境里，为了审计性，一般优先考虑 `DELETE`。 |
| 7 | “什么是 CHECK constraint？” | 例如 `CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','CLOSED'))` ——在数据库层保证数据有效性，不依赖应用层。我的 schema 在所有状态/严重级别列上都用了 CHECK constraint。 |

---

### PL/SQL

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “写一个返回 compliance score 的 PL/SQL function。” | 定义变量、用 `SELECT INTO` 取计数、应用 deduction formula、通过 `IF/THEN` 把结果下限夹到 0、最后 `RETURN`。我的 `procedures.sql` 里就有完整 Oracle PL/SQL 版本的 `calculate_compliance_score`。 |
| 2 | “stored procedure 和 function 的区别？” | Procedure 执行动作，本身不返回值（通常用 OUT 参数）；Function 返回一个值，还可以在 SQL 语句中调用。批量更新更适合 procedure，计算型结果更适合 function。 |
| 3 | “`NVL` 和 `COALESCE` 的区别？” | `NVL(expr, default)` 是 Oracle 专有，且会计算两个参数；`COALESCE(expr1, expr2, ...)` 是 ANSI SQL，支持多个参数，并短路求值。为了可移植性通常更推荐 COALESCE。 |
| 4 | “你如何在 PL/SQL 中处理错误？” | 用 `EXCEPTION WHEN OTHERS THEN ROLLBACK; RAISE;`——捕获、清理、重新抛出。更好的是优先用具体异常名（`NO_DATA_FOUND`, `TOO_MANY_ROWS`）。在监管系统中绝不能静默吞掉异常。 |
| 5 | “什么是 cursor？什么时候用 `SYS_REFCURSOR`？” | Cursor 是指向结果集的指针。`SYS_REFCURSOR` 常作为 OUT 参数，把结果集传回 Java/ColdFusion 调用方。我的 `get_overdue_inspections` procedure 就返回了 `SYS_REFCURSOR`。 |

---

### JavaScript

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “解释 `var`、`let`、`const` 的区别。” | `var`：函数作用域、会提升（旧写法）；`let`：块级作用域；`const`：块级作用域且绑定不可重新赋值（但对象内部内容仍可变）。一般默认 `const`，需要重赋值时再用 `let`，尽量不用 `var`。 |
| 2 | “什么是 Fetch API？和 XMLHttpRequest 有何不同？” | Fetch 返回 Promise，可以配合 `.then()` 或 `async/await`，写法更现代；XHR 是 callback 风格，更冗长。我的 dashboard 用 `fetch('/api/dashboard/stats').then(r => r.json())` 异步加载数据。 |
| 3 | “什么是 event delegation？” | 不是给每个子元素都绑事件，而是给父元素绑一个监听器，利用冒泡通过 `event.target` 判断来源。对动态列表（比如 facility search results）更高效。 |
| 4 | “如何防止 XSS？” | 不要把原始用户输入直接用 `innerHTML` 插进 DOM。文本用 `textContent`，或配合 sanitizer。服务端也要做输出编码（我的 ColdFusion 使用 `HTMLEditFormat()`）。 |
| 5 | “解释 closures。” | 函数在外层函数返回后，仍然保留对外层作用域变量的访问能力。常用于数据私有化、callbacks、factory patterns。 |
| 6 | “什么是 `async`/`await`？” | 这是 Promise 的语法糖。`async` 表示函数返回 Promise；`await` 会等待 Promise resolve。让异步代码（API 调用、DB 请求）读起来更像同步代码。 |

---

### CSS

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “解释 CSS box model。” | 每个元素都由 content + padding + border + margin 组成。`box-sizing: border-box` 会让 width 包含 padding 和 border，更好预测布局。我的 dashboard CSS 就使用了这一点。 |
| 2 | “如何做 responsive layout？” | 用 media queries（`@media (max-width: 768px)`）、灵活单位（`%`, `em`, `rem`, `vw`）、Flexbox 做一维布局、CSS Grid 做二维布局。一般 mobile-first。 |
| 3 | “Flexbox 和 Grid 什么时候分别用？” | Flexbox：一维（行或列）——比如导航栏、卡片横排。Grid：二维（行和列）——比如 dashboard 布局或复杂页面结构。 |
| 4 | “什么是 CSS specificity？” | 优先级一般是：inline styles > IDs > classes/attributes > elements。`!important` 可以强行覆盖（尽量避免）。理解 specificity 有助于快速排查“为什么样式没生效”。 |
| 5 | “如何保证 CSS 的可访问性？” | 足够颜色对比（4.5:1）、可见的 focus 样式、不要只靠颜色传达信息、用 `rem` 等相对单位让用户可缩放文字、确保点击/触摸区域足够大（至少 44×44px）。 |

---

### XML / HTML

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “XML 和 HTML 的区别是什么？” | XML 是严格的数据格式（区分大小写、标签必须闭合、没有预定义元素）；HTML 是用于渲染的语言（更宽松，有预定义元素如 `<h1>`、`<table>`）。在 TCEQ 场景中：XML 适合数据交换（配置、Hibernate mappings、web service payloads），HTML 适合用户界面。 |
| 2 | “什么是 semantic HTML，为什么重要？” | 用 `<header>`, `<nav>`, `<main>`, `<article>`, `<footer>` 而不是全用 `<div>`。这样 screen readers 和搜索引擎都更容易理解结构，这对 Section 508 accessibility compliance 非常关键。 |
| 3 | “你如何验证一个 XML 文档？” | 用 XSD（XML Schema Definition）或 DTD（Document Type Definition）做校验。Java 可以用 `javax.xml.validation` 程序化校验，保证系统集成中的数据有效性。 |
| 4 | “什么是 XSLT？” | XML Stylesheet Language Transformations，用于把 XML 转成 HTML、文本或另一个 XML。在 legacy reporting systems 中很常见。 |
| 5 | “解释 HTML form validation。” | 前端可用 `required`、`pattern`、`type="email"`、`min`/`max` 等属性；服务端必须再次校验，不能信任前端。我的 ColdFusion templates 用 `<cfparam>` 设置服务端默认值，用 `<cfqueryparam>` 做数据库级别输入校验。 |

---

## 14. 高级行为面试题

除了第 9 节的基础题之外，Programmer IV 级别通常还会被追问这些更深层的问题：

---

### 领导力与辅导能力

| # | 问题 | 回答方向 |
|---|---|---|
| 1 | “如果一个 junior developer 在某项技术上持续吃力，你会怎么带？” | 具体说：pair programming、逐步加难度的任务分配、写文档/runbooks、定期 1-on-1。并且要把它和 FJD 里的 **training needs** 关联起来。 |
| 2 | “讲一个你带团队做困难技术决策的例子。” | 用 STAR 格式。展示你如何建立共识、如何用数据驱动、如何把决策理由文档化。要呼应 **leading coding tasks**。 |
| 3 | “如果一个 team member 一直写低质量代码，你怎么处理？” | 先私下、建设性沟通；配合 pair review；先判断是 skill gap 还是 process gap（标准不明确？没有 linting？）。辅导无效才升级处理。不要公开羞辱。 |
| 4 | “你如何给团队建立并执行编码规范？” | 把标准文档化到 team wiki，用 CI 里的自动 linting 去落地，并通过 **peer reviews** 持续强化。你自己先以身作则。 |

---

### 项目管理与 stakeholder 沟通

| # | 问题 | 回答方向 |
|---|---|---|
| 5 | “你如何为一个新的软件项目制定 project plan？” | 从 **charter** 开始（scope、constraints、stakeholders），把工作拆成 analysis → design → develop → test → deploy 几个阶段，在每个 milestone 输出 **status reports**，并在 **sponsor meetings** 上汇报。 |
| 6 | “你如何管理 scope creep？” | 在 **charter** 和 **requirements documentation** 中先把范围定义清楚。新需求进来时，明确给出影响分析：‘我们可以加 X，但 Y 会延后两周，下面是更新后的 **project plan**。’ |
| 7 | “讲一个项目落后于计划的经历，你怎么处理？” | 诚实讲原因；说明你如何通过 **status reports** 透明沟通、如何重新排序 feature、如何和 stakeholders 协商。不要隐瞒坏消息。 |
| 8 | “如果不同 stakeholders 给出冲突需求，你怎么处理？” | 主持结构化讨论（像 **JAD session** 一样），把每个需求文档化，指出冲突点，用数据说明 trade-offs，必要时升级给 **project sponsor** 最终拍板。 |

---

### 适应力与问题解决能力

| # | 问题 | 回答方向 |
|---|---|---|
| 9 | “讲一个你不得不快速学习一项新技术的经历。” | 具体说明是什么技术、你如何学（官方文档、POC、向 SME 请教）、结果怎样。可以呼应 FJD 中“能够学习多种编程语言”的能力。 |
| 10 | “讲一个你改进现有流程的例子。” | 可以讲 CI/CD、文档标准、测试自动化、code review 流程等。最好给出量化结果——比如‘把部署时间从 4 小时降到 30 分钟。’ |
| 11 | “你如何在维护 legacy systems 的同时保持技术更新？” | 讲你如何安排学习时间、参与技术社区、做内部 brown-bag sharing。重点是：既尊重 legacy，又知道怎样做渐进式现代化。 |
| 12 | “讲一个你在信息不完整的情况下做决策的情况。” | 要体现风险评估、假设文档化、可逆性设计，以及你如何向 stakeholders 沟通不确定性。 |

---

### 政府 / 监管环境思维方式

| # | 问题 | 回答方向 |
|---|---|---|
| 13 | “你为什么想来州政府，而不是私企？” | 强调：使命驱动、长期稳定、建设服务数百万人的系统、可持续的 work-life balance。不要让人觉得这是退而求其次。 |
| 14 | “你如何看待在高监管、流程严格的环境下工作？” | “我把 governance 看成一种质量框架，而不是阻碍。在监管环境中，**configuration management**、**peer reviews** 和 **QA/QC** 并不是官僚流程——它们是在防止那些可能带来真实环境后果的生产事故。” |
| 15 | “这个岗位可能要维护几十年前的 legacy systems，你怎么看？” | “Legacy systems 往往承载最关键的数据。我会以尊重的态度去理解现有架构，再开始修改；先在周围补测试，然后再做渐进式现代化。我的 demo 同时展示了 legacy patterns（**XML** Hibernate mappings、ColdFusion）和 modern approaches。” |
| 16 | “如果你不同意经理的技术决策，你会怎么办？” | “我会用数据和书面比较来陈述我的观点。如果最终决策不同，我也会完整、专业地执行。我可以把我的顾虑记入 **requirements documentation** 供以后参考，但一旦拍板，我就会全力支持。” |

---

## 15. 你应该问他们的问题

> **面试最后通常会给你时间提问。** 这不是可有可无。一个问题都不问，会显得你没有兴趣。提前准备 4–5 个，并根据面试中的实际内容灵活挑选。

### 关于工作本身

| # | 问题 | 你为什么要问 |
|---|---|---|
| 1 | “入职后的前 30/60/90 天，我最可能负责哪些 applications 或 systems？” | 表现出你已经在思考如何快速上手并做出贡献。 |
| 2 | “当前新开发和 maintenance/support 的比例大概是多少？” | 帮你判断真实日常工作内容。 |
| 3 | “目前是否存在正在进行中的 ColdFusion-to-Java（或其他 modernization）计划？” | 体现你认真研究了技术栈，而且在做战略层面的思考。 |
| 4 | “部署流程通常是什么样的——代码从 dev 到 production 是怎么流转的？” | 这能暴露出他们 **change management** 的成熟度。 |
| 5 | “团队如何处理 production emergencies 或 on-call？” | 帮你了解 8–5 之外是否存在隐性值班要求。 |

### 关于团队

| # | 问题 | 你为什么要问 |
|---|---|---|
| 6 | “IRD 开发团队有多大？组织结构是怎样的？” | 帮你理解团队规模、汇报关系、是否有子团队。 |
| 7 | “这里的 **peer review** 流程在实际操作中通常是什么样？” | 体现你对 FJD 关键词的敏感度，也体现你对工作方式的真实兴趣。 |
| 8 | “团队通常怎样组织 **JAD sessions** ——正式的多天 workshop，还是更偏 agile 的短会形式？” | 直接切中这个岗位最重要的职责区域之一（35% 左右）。 |
| 9 | “团队通常会利用哪些 professional development opportunities？” | 体现长期成长心态，也呼应招聘福利中的培训机会。 |

### 关于政策与安排

| # | 问题 | 你为什么要问 |
|---|---|---|
| 10 | “IRD 当前的 telework/hybrid policy 是怎样的？” | **这个要问。** 州政府政策会变化，最好在接 offer 前明确。 |
| 11 | “周一到周五 8–5 是固定的吗，还是 IRD 内部有一定灵活性？” | 招聘公告写的是“Individual and work group schedule determined by IRD.” |
| 12 | “新入职的 Programmer IV onboarding 流程大概是什么样？” | 帮你判断他们的入职与交接是否规范。 |
| 13 | “这两个 openings 是在不同团队，还是会加入同一个团队？” | 既然有两个岗位，了解组织安排很有必要。 |

### 关于成长空间

| # | 问题 | 你为什么要问 |
|---|---|---|
| 14 | “在 IRD 内部，从 Programmer IV 往后发展的路径通常是什么样？” | 传达你有长期打算。 |
| 15 | “这个岗位是否有机会参与 architecture decisions 或 standards-setting？” | 表明你希望站在更高级别上发挥影响，而不只是做执行层开发。 |

> **提示：** 面试中如果他们提到了某个具体系统（例如 STEERS、CCEDS 等），你可以顺势追问——“你刚才提到 STEERS，这个岗位会直接参与那个系统吗？”

---

## 16. 现场面试礼仪

### 到场前

| 应该做 | 不要做 |
|---|---|
| 提前研究地址：**12100 Park 35 Circle, Austin, 78753**（靠近 Park 35 商业区，I-35 北侧） | 不要只靠 GPS，当天可能有施工或绕行 |
| **提前 15 分钟**到——留出 security、找楼层、上洗手间、稳定情绪的时间 | 不要提前 30 分钟以上到，容易让对方尴尬 |
| 穿 **business professional**（西装/西装外套、保守配色）——州政府通常更正式 | 不要穿 business casual 或科技公司风格（hoodie、sneakers） |
| 带 **5 份**简历（每个 panelist 一份，再加备用） | 不要只带一份 |
| 带 **notepad 和笔**（方便记多段式问题） | 不要带 laptop（会形成隔阂感） |
| 手机**彻底关机**（不是静音，是关机） | 不要只留在振动模式 |
| 带一个 **portfolio folder / padfolio**，把材料整理好 | 不要抱着一堆散纸或背着大背包进场 |

### 到场后

| 应该做 | 不要做 |
|---|---|
| **对所有人都礼貌**——前台、保安、走廊上的工作人员，panel 有时会问前台对你的印象 | 不要对支持人员冷淡或不耐烦 |
| 如有要求，主动出示 **photo ID** | 不要把证件忘在车里 |
| 安静地在 lobby 等待，简单回顾笔记 | 不要来回踱步、大声打电话或表现焦躁 |
| 被带进房间后，和每位 panelist **握手、眼神交流，并记住名字** | 不要在没人示意前就直接坐下 |

### 面试过程中

| 应该做 | 不要做 |
|---|---|
| **看向所有 panelists**，不要只盯着一个提问者 | 不要只对一个人说话 |
| 回答时先面向提问者，再自然扫向其他人 | 不要一直看天花板或盯桌子 |
| 多段式问题可以**简短记笔记**——这是专业表现 | 不要记太多导致失去互动 |
| 没听清就说：**“Could you repeat that question?”**（他们会逐字重念） | 不要在没听清时硬猜 |
| 回答前可以停顿 **3–5 秒**整理思路 | 不要急着开口导致结构混乱 |
| 行为题尽量用 **STAR**（Situation, Task, Action, Result） | 不要无结构地长篇大论 |
| **自然说出 FJD 关键词**——例如 “requirements documentation”, “peer review”, “agency IT standards” | 不要使用俚语、粗口或过于随便的表达 |
| 每个回答尽量控制在 **2 分钟内**，除非对方继续追问 | 不要一讲就是 5 分钟以上 |
| 遇到不会的题，可以说：**“这个具体技术我没有直接做过，但我会这样快速上手……”** | 不要硬编，technical SME 很容易听出来 |
| 回答技术题时**引用你的 demo project**——“在我做的 compliance tracking app 里……” | 不要只讲教科书理论而不落地 |

### 肢体语言

| 应该做 | 不要做 |
|---|---|
| 坐直，身体微微前倾，表示投入 | 不要瘫坐或后仰 |
| 保持开放姿态——手放桌上或腿上 | 不要抱臂 |
| panelists 说话时点头回应 | 不要看手机、手表或门口 |
| 自然微笑 | 不要一直僵硬地假笑 |

### 面试结束后

| 应该做 | 不要做 |
|---|---|
| 出门前**逐一感谢 panelists**——“Thank you, [Name], I appreciate your time.” | 不要一声不吭走掉 |
| 问一句 **next steps**：“What does the timeline look like from here?” | 不要直接问“我拿到工作了吗？” |
| 在 24 小时内发 **thank-you email** 给 HR contact（不是 panelists 本人——州政府通常不鼓励直接联系 panel） | 不要未经允许直接发邮件或打电话给 panelists |
| thank-you email 中提到一个**你们聊过的具体点**——比如 “I enjoyed discussing the agency's approach to JAD sessions.” | 不要发那种任何公司都能套用的模板感谢信 |
| **要有耐心**——州政府流程 4–6 周很常见，有时更久 | 不要每几天追着问进度 |
| 如果他们要 references 或 background check 材料，**当天就回复** | 不要拖延行政材料 |

### Thank-You Email 模板

> **Subject:** Thank You — Programmer IV Interview (Position 00055221)
>
> Dear [HR Contact Name],
>
> Thank you for the opportunity to interview for the Programmer IV position in the Information Resources Division. I enjoyed learning about the team's work and the agency's approach to [specific topic discussed — e.g., "modernizing compliance reporting systems"].
>
> The role's combination of technical leadership, JAD facilitation, and application development aligns strongly with my experience, and I'm enthusiastic about the opportunity to contribute to TCEQ's mission of protecting Texas's natural resources and public health.
>
> Please don't hesitate to reach out if you need any additional information.
>
> Best regards,
> [Your Name]
> [Phone] | [Email]

---

## 17. 系统设计题

> 州政府类面试一般更偏 **practical architecture**，而不是互联网公司那种纯白板式大系统设计。但 Programmer IV 这个级别，仍然很可能遇到“设计一个子系统”的题，考你的分层思考能力。

---

### SD-1：“设计一个 permit renewal notification system。”

**你的回答框架：**

```text
                  ┌──────────────┐
                  │  Scheduler   │ (Spring @Scheduled or Oracle DBMS_SCHEDULER)
                  │  (Nightly)   │
                  └──────┬───────┘
                         │ Queries permits expiring in 90/60/30 days
                  ┌──────▼───────┐
                  │  Service     │ ComplianceNotificationService
                  │  Layer       │ - Deduplicates (don't re-notify)
                  └──────┬───────┘ - Creates NOTIFICATION records
                         │
              ┌──────────┼──────────┐
              ▼          ▼          ▼
         ┌────────┐ ┌────────┐ ┌────────┐
         │ Email  │ │  DB    │ │ Audit  │
         │ (SMTP) │ │ Insert │ │  Log   │
         └────────┘ └────────┘ └────────┘
```

**一定要讲到的点：**
- **Data model：** `NOTIFICATIONS (id, permit_id FK, type, sent_date, status)`
- **Idempotency：** 先检查 `NOT EXISTS (SELECT 1 FROM notifications WHERE permit_id = ? AND type = '30_DAY')`
- **Error handling：** 如果 SMTP 失败，就把 status 标成 `FAILED`，下次重试
- **Monitoring：** Dashboard 上展示 “X permits expiring this month”
- **Change management：** Feature branch → **peer review** → staging → **QA/QC** → 带 rollback 上生产

---

### SD-2：“你会如何现代化一个 monolithic ColdFusion application？”

**回答框架：**

| 阶段 | 行动 | 风险控制 |
|---|---|---|
| **1. 评估** | 盘点所有 CFM 页面，按业务关键性和变更频率排序 | 不要一开始就动那些稳定且几乎不变的页面 |
| **2. Strangler Fig** | 在前面加 reverse proxy；把特定 URL 路由到新的 Java/Spring services | 新旧系统共享同一个 Oracle datasource |
| **3. 抽服务** | 把高变更、高业务价值逻辑迁到 Spring REST APIs；CFM 页面改为调用 API | ColdFusion 逐渐变成薄 presentation layer |
| **4. 替换 UI** | 一个模块一个模块地用 Thymeleaf 或 JavaScript SPA 替换 CFM | 在 **customer acceptance testing** 确认功能等价前，新旧并行 |
| **5. 下线旧模块** | Java 版本完全验证后，再移除旧的 ColdFusion 页面 | 旧版 CFM 仍保留在 source control 中做历史参考 |

**核心原则：** “永远不要 big-bang rewrite。要渐进推进，并且每一步都能 rollback。”

---

### SD-3：“设计一个可服务 500+ 并发内部用户的 compliance dashboard。”

**答案：**
- **Caching layer：** 对 dashboard aggregates（总 facilities、open violations）做 5 分钟 TTL 缓存——这些数据通常不需要秒级变化
- **Database：** 用 materialized views 或预聚合 summary tables，由定时任务更新，而不是每次页面加载都跑实时 COUNT(*)
- **Connection pooling：** 用 HikariCP，并根据 Oracle max sessions 调整池大小
- **API design：** summary stats 和 detail drill-down 分开接口，不要 over-fetch
- **CDN/static：** JS/CSS/images 由 CDN 或 reverse proxy cache 提供
- **Monitoring：** slow query log、connection pool saturation alerts、response time SLA

---

### SD-4：“你会如何设计 agency web services 的 API versioning strategy？”

**答案：**
- **URL-based versioning：** `/api/v1/facilities`, `/api/v2/facilities` ——对消费者最清晰
- **Deprecation policy：** 发布 v2 后，至少保留 v1 12 个月（政府消费者升级通常慢），在 v1 响应头中加入 `Sunset` 与 `Deprecation`
- **Backwards compatibility rules：** 只加字段，不删不改名；新的必填参数必须有默认值；response envelope 保持稳定
- **Documentation：** 为每个版本发布 OpenAPI/Swagger 规范
- **Testing：** 用 contract tests 确保 v1 消费方在新代码下仍能工作

---

### SD-5：“你会如何设计一个安全的 environmental compliance documents 文件上传系统？”

**答案：**
- **Validation：** 只允许 PDF、XLSX、CSV；服务端检查 MIME type（不只看扩展名）；限制文件大小（如 50MB）
- **Storage：** 不把文件正文直接放数据库——存 filesystem 或 blob storage，只在 DB 中存引用
- **Virus scanning：** 入库前使用 ClamAV 或 agency-approved scanner 扫描
- **Access control：** 文件带 facility_id；只有授权用户能下载对应文件
- **Audit trail：** 记录每次 upload/download 的 user、timestamp、IP、file hash
- **Encryption：** 存储层 at-rest encryption，传输层 TLS

---

### SD-6：“某条关键查询在 annual emissions inventory 期间引发 deadlocks。你怎么诊断并修复？”

**答案：**
1. **识别：** 查看 Oracle 的 `V$LOCK`、`DBA_BLOCKERS`，或导出 deadlock graph
2. **根因：** 通常是两个事务以相反顺序锁行——事务 A 先锁 row 1 再等 row 2；事务 B 先锁 row 2 再等 row 1
3. **修复方案：**
   - 确保所有事务以**同样顺序**加锁（如按表名或主键升序）
   - 缩小事务范围，更早 commit
   - 使用 `SELECT ... FOR UPDATE NOWAIT` 让事务快速失败，而不是长时间等待
   - 把读密集 dashboard 查询移到 **read replica**，或使用 `SET TRANSACTION READ ONLY`
4. **预防：** 监控 deadlock、对 lock wait > 10 秒触发告警

---

## 18. 算法与数据结构题

> TCEQ 不太会问你去“翻转二叉树”，但他们可能会给你一些**企业场景里的逻辑题/编码题**。这些题往往是有业务背景的 LeetCode 变体。

---

### Arrays & Strings

| # | 问题 | 思路 | 复杂度 |
|---|---|---|---|
| 1 | “给一组 facility EPA IDs，找出重复项。” | 用 `HashSet`——遍历时如果 `add()` 返回 false，就是重复。 | O(n) 时间，O(n) 空间 |
| 2 | “不用 `StringBuilder.reverse()` 反转字符串。” | 双指针：交换 `i` 和 `len-1-i` 位置字符。 | O(n) 时间，O(1) 空间 |
| 3 | “给一个按 inspection date 排好序的数组，找出第一个 overdue 的（早于今天）。” | 二分查找，比较中点和 `LocalDate.now()`。 | O(log n) |
| 4 | “去掉字符串中的重复字符，但保持原顺序。” | 用 `LinkedHashSet<Character>`。 | O(n) |

### HashMaps & Sets

| # | 问题 | 思路 | 复杂度 |
|---|---|---|---|
| 5 | “根据一组 Violation objects 统计不同 severity 的数量。” | 用 `Map<String, Integer>` 配合 `merge()`，或 `Collectors.groupingBy(Violation::getSeverity, Collectors.counting())`。 | O(n) |
| 6 | “找出字符串里第一个不重复字符。” | 第一遍建频次 map，第二遍找 count == 1 的第一个字符。 | O(n) |
| 7 | “给两组 permit numbers，求交集。” | 先把 list1 放入 `HashSet`，再遍历 list2 做 `contains()`。 | O(n + m) |

### Sorting & Searching

| # | 问题 | 思路 | 复杂度 |
|---|---|---|---|
| 8 | “按 compliance score 降序排序 facilities；分数相同按 name 升序。” | `Comparator.comparingInt(Facility::getScore).reversed().thenComparing(Facility::getName)` | O(n log n) |
| 9 | “按日期合并两组已排序 inspection records。” | 双指针 merge（类似 merge sort 的 merge 步骤）。 | O(n + m) |

### SQL 作为算法题

| # | 问题 | 对应 SQL |
|---|---|---|
| 10 | “找出 open violations 最多的前 3 个 counties。” | `SELECT county, COUNT(*) AS ct FROM violations v JOIN facilities f ON v.facility_id = f.id WHERE v.status = 'OPEN' GROUP BY county ORDER BY ct DESC FETCH FIRST 3 ROWS ONLY;` |
| 11 | “找出有 permits 但没有 inspections 的 facilities。” | `SELECT f.* FROM facilities f WHERE EXISTS (SELECT 1 FROM permits p WHERE p.facility_id = f.id) AND NOT EXISTS (SELECT 1 FROM inspections i WHERE i.facility_id = f.id);` |
| 12 | “按日期计算 penalty amounts 的 running total。” | 用窗口函数：`SUM(penalty_amount) OVER (ORDER BY violation_date ROWS UNBOUNDED PRECEDING)` |

---

### 如果他们给你白板题

**策略：**
1. **先问清输入/输出** ——“输入类型是什么？允许 null 吗？数据量多大？”
2. **先说 brute force** ——“最直观的方法是 O(n²)……”
3. **再优化** ——“我们可以用 HashMap 把它降到 O(n)……”
4. **代码写清楚** ——变量命名清晰，注意边界情况
5. **口头跑例子** ——“我用这个示例输入走一遍……”

---

## 19. 高级 Java 问题

---

### 并发与多线程

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “`synchronized` 和 `ReentrantLock` 的区别是什么？” | `synchronized`：隐式锁、自动释放、简单。`ReentrantLock`：显式 lock/unlock，支持 `tryLock()` 超时、可中断、公平锁策略。需要 timeout 或更细控制时更适合 `ReentrantLock`。 |
| 2 | “什么是 race condition？如何防止？” | 多线程同时修改共享状态，结果不可预测。防法包括：`synchronized`、`Atomic` 类（`AtomicInteger`、`AtomicReference`）、`ConcurrentHashMap`、不可变对象。Spring 应用里通常通过 stateless services + 合适的 `@Transactional` 隔离级别来控制。 |
| 3 | “解释 `volatile` 关键字。” | 它保证可见性：读写直接对主内存生效，而非线程本地缓存。但它**不保证原子性**——像自增这种 read-modify-write 仍需 `AtomicInteger` 等。 |
| 4 | “什么是 thread pool？为什么用 `ExecutorService` 而不是 `new Thread()`？” | 线程创建成本高，`ExecutorService` 可以复用线程、控制并发度、通过 `Future` 获取结果。在服务器端这能防止高负载时线程无限膨胀。 |
| 5 | “什么是 `CompletableFuture`？” | 它让异步操作可组合，可用 `thenApply()`、`thenCompose()`、`thenCombine()` 链式处理，并用 `exceptionally()` 做错误处理。对并行服务调用或异步 API 很有用。 |

---

### 泛型与类型系统

| # | 问题 | 回答要点 |
|---|---|---|
| 6 | “什么是 type erasure？” | Java 泛型只存在于编译期。运行时 `List<String>` 和 `List<Integer>` 都只是 `List`。所以不能直接 `new T()` 或在运行时做 `instanceof T`。这会带来 bridge methods、unchecked cast warnings 等问题。 |
| 7 | “解释 `? extends T` 和 `? super T`（PECS）。” | `? extends T`（Producer）适合“读出来”；`? super T`（Consumer）适合“写进去”。PECS：Producer Extends，Consumer Super。 |

---

### Spring Boot 内部机制

| # | 问题 | 回答要点 |
|---|---|---|
| 8 | “什么是 Spring 的 `@Transactional`？底层怎么工作？” | Spring 会给 bean 创建一个 proxy。当你调用 `@Transactional` 方法时，proxy 会先开启事务，调用真实方法，成功则 commit，遇到 `RuntimeException` 则 rollback。要注意：**同类内部自调用**会绕过 proxy，导致事务注解失效。 |
| 9 | “`@Component`、`@Service`、`@Repository`、`@Controller` 的区别？” | 本质上都属于 `@Component` 的特化，都会被 Spring 管理。区别在语义和附加行为：`@Repository` 有持久层异常转换，`@Controller` 参与 MVC 映射，`@Service` 是业务逻辑标识。 |
| 10 | “Spring dependency injection 如何解决歧义？” | 如果同一接口有多个实现，可通过 `@Primary` 指定默认 bean，通过 `@Qualifier("name")` 选定具体实现，也可以利用构造器参数名与 bean name 对应。 |

---

### 内存与性能

| # | 问题 | 回答要点 |
|---|---|---|
| 11 | “什么是 Java memory leak？举个例子。” | 指对象还被引用着，但实际上不再有用：不断增长的 static `List`、未关闭的 streams/connections、长期存大量数据的 `HttpSession`。可通过 heap dump（`jmap`）和分析工具（Eclipse MAT、VisualVM）排查。Hibernate 中，first-level cache 在 batch processing 时也可能越堆越大，要及时 clear。 |
| 12 | “Java 中 `==` 和 `.equals()` 的区别？” | `==` 比较引用是否相同；`.equals()` 比较值（如果类重写了它）。字符串因为 String pool 有时 `==` 恰好成立，但**永远优先用 `.equals()`**。枚举则可以安全用 `==`。 |

---

## 20. Web 架构问题

---

### CORS（Cross-Origin Resource Sharing）

| # | 问题 | 回答要点 |
|---|---|---|
| 1 | “什么是 CORS，为什么存在？” | 浏览器的同源策略会阻止前端页面请求不同域名的资源。CORS 是一组服务端响应头机制，用来明确声明哪些跨域请求被允许。否则，`app.tceq.texas.gov` 上的前端就不能直接调用 `api.tceq.texas.gov`。 |
| 2 | “你如何在 Spring Boot 配置 CORS？” | 可以在 controller 上加 `@CrossOrigin`，也可以全局用 `WebMvcConfigurer.addCorsMappings()`。生产环境中不要对 origins 使用 `*`，应该白名单具体域名。 |
| 3 | “什么是 CORS preflight request？” | 对于非简单请求（如 PUT/DELETE、自定义 headers），浏览器会先发一个 `OPTIONS` 请求。服务端必须正确返回 `Access-Control-Allow-*` 头，否则浏览器会阻止后续真实请求。 |

---

### 向后兼容（Backwards Compatibility）

| # | 问题 | 回答要点 |
|---|---|---|
| 4 | “当你修改 API 时，如何保证 backwards compatibility？” | （1）response 里只加字段，不删不改；（2）新的必填 request params 要有默认值；（3）使用版本化 URL（`/v1/`, `/v2/`）；（4）通过 deprecation headers 和迁移窗口管理升级；（5）用 consumer-driven contract tests 确保旧客户端继续可用。 |
| 5 | “当你修改数据库 schema 时，如何保证 backwards compatibility？” | （1）只做 additive schema changes；（2）先部署 schema 再部署代码；（3）不要直接 rename columns——先新增、迁移数据、再废弃旧列；（4）使用 versioned migrations + rollback scripts；（5）确保旧版和新版应用能同时运行在该 schema 上。 |
| 6 | “你如何处理 web service message formats（XML/JSON）的向后兼容？” | （1）反序列化时忽略未知字段（`@JsonIgnoreProperties(ignoreUnknown = true)`）；（2）只新增可选字段，不删除必填字段；（3）XML 可通过 namespace 做 schema versioning。 |

---

### HTTP 基础

| # | 问题 | 回答要点 |
|---|---|---|
| 7 | “解释 GET、POST、PUT、PATCH、DELETE。” | `GET`：读（幂等、可缓存）；`POST`：创建（非幂等）；`PUT`：整体替换（幂等）；`PATCH`：部分更新（通常非幂等）；`DELETE`：删除（幂等）。幂等意味着重试不会产生额外副作用。 |
| 8 | “什么是 HTTP status codes？说说关键区间。” | `2xx`：成功（200 OK、201 Created、204 No Content）；`3xx`：重定向；`4xx`：客户端错误（400、401、403、404）；`5xx`：服务端错误（500、502、503）。我的 demo 对缺失 facility 返回 404（`ResponseEntity.notFound()`）。 |
| 9 | “401 和 403 的区别？” | `401 Unauthorized`：本质上是“我不知道你是谁”（未认证/认证失败）；`403 Forbidden`：是“我知道你是谁，但你没权限”。很多人会混淆。 |

---

### 缓存

| # | 问题 | 回答要点 |
|---|---|---|
| 10 | “HTTP caching 有哪些常见机制？” | `Cache-Control`（max-age、no-cache、no-store）、`ETag`（基于 hash 的校验，未变化则返回 304）、`Last-Modified` / `If-Modified-Since`（基于时间的校验）。另外还要区分浏览器缓存、CDN 缓存、服务端缓存（Redis/内存）。 |
| 11 | “什么时候不应该缓存？” | 用户特定数据（带权限控制的 dashboard）、变化频繁的数据（实时 compliance status）、金融/交易型数据、以及任何带安全风险的信息。此时应设置 `Cache-Control: no-store, no-cache, must-revalidate`。 |

---

### Sessions 与认证

| # | 问题 | 回答要点 |
|---|---|---|
| 12 | “session 在 web application 中怎么工作？” | 服务端创建一个 session ID，把它作为 cookie 发给客户端；客户端每次请求都带回这个 cookie；服务端再把 session ID 映射到存储中的用户数据（用户信息、权限等）。这是 stateful 的，在负载均衡时通常需要 sticky sessions 或共享 session store（如 Redis）。 |
| 13 | “Cookies 和 JWT 的 trade-offs？” | Cookies：服务端存 session state，浏览器天然支持，简单，但要防 CSRF。JWT：无状态、可扩展性好，但难以撤销、payload 更大。政府系统里更常见的是服务端 session + CSRF tokens。 |

---

### Security Headers 与 Web 加固

| # | 问题 | 回答要点 |
|---|---|---|
| 14 | “一个 web application 应该默认设置哪些安全响应头？” | `Content-Security-Policy`（防 XSS）、`X-Content-Type-Options: nosniff`（防 MIME sniffing）、`X-Frame-Options: DENY`（防 clickjacking）、`Strict-Transport-Security`（强制 HTTPS）、`X-XSS-Protection: 1; mode=block`（旧浏览器兼容）。Spring Security 默认能帮你启用大部分。 |
| 15 | “什么是 CSRF，如何防？” | Cross-Site Request Forgery：恶意网站利用你的浏览器和 session cookie 替你发请求。防法包括：CSRF token（Spring Security 默认支持）、`SameSite` cookie、校验 `Referer`/`Origin`。我的 ColdFusion forms 也应该加隐藏的 CSRF token 字段。 |

---

# 附录 A：公开信息核验与使用建议（新增）

> 本附录是**新增内容**，用于帮助你区分：哪些内容已经能从公开来源核到，哪些内容更适合作为“面试准备推断”而不是“我确信官方写了什么”。正文其余部分均已保留并完成中文直译。

## A.1 已核实、可以放心使用的内容

以下内容可从 TCEQ / 德州州政府公开来源，或公开岗位镜像中得到支持：

1. **岗位核心职责和关键词高度一致**：包括 analysis / development / programming / implementation-modification、requirements documentation、QA/QC、peer reviews、JAD sessions、charters、business cases、project plans、customer support 等。  
2. **岗位技术栈方向可信**：ColdFusion、Java、Hibernate、Oracle、SQL、PL/SQL、JavaScript、CSS、XML、HTML，以及 Java web services、configuration management、quality assurance / change management 这些关键词在公开岗位镜像中都出现过。  
3. **工作地点与总部地址可信**：`12100 Park 35 Circle, Austin, TX 78753`。  
4. **州职位代码 / 级别可信**：State Job Code `0244PA`、Salary Admin Plan `B`、Grade `26`。  
5. **固定月薪可信**：公开的 2026 年 TCEQ Programmer IV 岗位镜像显示为 **$7,745/月**。  
6. **标准工时可信**：公开岗位镜像显示 Full-time、40 小时/周；TCEQ 官方 telework plan 也写明核心工作时间是 **周一到周五 8:00 a.m. – 5:00 p.m.**。  
7. **福利方向可信**：TCEQ 官方 Benefits and Perks 页面明确列出了 health / retirement / wellness / paid leave 等福利框架。  

## A.2 建议谨慎表述的内容

以下内容**建议你在面试中不要说成“我看到官方明确写了”**，而是说成“根据我准备材料整理”或“我理解可能是这样”：

1. **“Posted: Jan 16 – Jan 30, 2026”** 这一组日期：我找到了第三方岗位镜像与 CAPPS 搜索结果能对应到 2026 年 1 月 16 日发布的 TCEQ Programmer IV，但未成功打开同一版本的完整官方归档详情页逐项核验。  
2. **“Openings: 2”**：我没有找到与你文首完全一致、且能完整展开的官方归档页面来确认“2 个 openings”这一项。公开可见的 2026 年 3 月 TCEQ Programmer IV 镜像显示为 **1 opening**。因此，这一项请保留在你的准备稿中，但面试时不要把“2 openings”当成你最依赖的硬信息。  
3. **“4–6 weeks to offer”**、**“likely written component”**、**“panel size 2–3”**：这些更像是基于州政府招聘流程的经验性推断，适合当面试准备假设，不适合当官方事实陈述。  

## A.3 面试时如何更稳妥地说

你可以这样说，而不是冒险说得太死：

- “根据我看到的岗位职责，这个角色非常强调 requirements documentation、QA/QC、peer reviews 和 JAD facilitation。”
- “我了解到这个岗位属于 TCEQ IRD / Administrative Services 方向，并且技术栈覆盖 Java、Oracle、Hibernate、ColdFusion 等。”
- “我理解这是一个偏长期维护与改造并重的岗位，同时也需要较强的 stakeholder communication 和 customer support 能力。”
- 如果你被问到 telework / schedule，可以说：  
  “我看到 TCEQ 有 telework plan，也看到一些公开岗位会标注 hybrid 或 remote-within-Texas 的安排；不过我想了解的是，这个具体团队目前实际执行的工作模式是什么。”

## A.4 这份文件现在最适合怎么用

这份文档现在最适合作为：

1. **面试答题提纲**：尤其是第 1、4、5、6、7、9、14、17、20 节。  
2. **关键词记忆卡**：把 FJD 词汇和你自己的 STAR 例子绑定。  
3. **模拟面试脚本**：你可以把每个回答压缩成 60–90 秒版本。  
4. **面试前最后一晚复盘材料**：不是逐字背诵，而是确保你知道每一节最想传达的 1–2 个关键信号。  

---

## 附录 B：保留英文专有术语对照（新增，便于你现场切换）

| 中文表述 | 英文原术语 |
|---|---|
| 联合应用开发会议 | Joint Application Development (JAD) session |
| 需求文档 | requirements documentation |
| 设计文档 | design documentation |
| 质量保证 / 质量控制 | Quality Assurance / Quality Control (QA/QC) |
| 客户验收测试 | customer acceptance testing / User Acceptance Testing (UAT) |
| 同行审查 | peer review |
| 变更管理 | change management |
| 配置管理 | configuration management |
| 项目章程 | charter |
| 商业论证 / 业务立项说明 | business case |
| 状态报告 | status report |
| 赞助人会议 | sponsor meeting |
| 分层架构 | layered architecture |
| 向后兼容 | backwards compatibility |
| 幂等 | idempotent / idempotency |
| 安全优先编码实践 | security-first coding practices |

---

## 结尾提醒（新增）

这份文档最强的地方，不只是它列了很多题，而是它把 **“州政府评分逻辑” + “岗位原话关键词” + “你自己的技术故事”** 绑到了一起。真正面试时，不要试图逐字背全文，而是要做到下面三点：

1. **每个回答至少自然说出 2–4 个 FJD 关键词；**  
2. **每个大类都能落到你自己的真实项目例子；**  
3. **把自己表现成一个能长期留下来、能把业务与技术连接起来的 senior IC / tech lead 型候选人。**

