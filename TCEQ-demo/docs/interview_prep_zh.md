# TCEQ Programmer IV — 面试准备指南

> **职位:** Programmer IV (得州职务代码 0244A, 薪级 B26)  
> **部门:** 信息资源部 (IRD), 行政服务科  
> **地点:** 12100 Park 35 Circle, Austin, TX 78753  
> **薪资:** 每月 $7,745 (年薪约 $92,940), 固定薪资  
> **名额:** 2 人 | **工作时间:** 周一至周五, 早8点到晚5点 | **加班:** 豁免 (Exempt)  
> **发布日期:** 2026年1月16日 – 1月30日

---

## 目录

1. [评分系统如何运作 (破解矩阵)](#1-评分系统如何运作)
2. [六个要点概括这份工作](#2-六个要点概括这份工作)
3. [面试形式与后勤安排](#3-面试形式与后勤安排)
4. [JAD 会议引导与战略咨询](#4-jad-会议引导与战略咨询)
5. [技术设计与架构](#5-技术设计与架构)
6. [代码领导力与最佳实践](#6-代码领导力与最佳实践)
7. [QA/QC 与测试协调](#7-qaqc-与测试协调)
8. [客户支持](#8-客户支持)
9. [行为面试题库](#9-行为面试题库)
10. [笔试 / 上机操作准备](#10-笔试--上机操作准备)
11. [薪资定位策略](#11-薪资定位策略)
12. [面试当天清单](#12-面试当天清单)
13. [技术面试题库 — 按技术栈分类](#13-技术面试题库--按技术栈分类)
14. [高级行为面试题](#14-高级行为面试题)
15. [你应该问的问题](#15-你应该问的问题)
16. [现场面试礼仪](#16-现场面试礼仪)
17. [系统设计问题](#17-系统设计问题)
18. [算法与数据结构问题](#18-算法与数据结构问题)
19. [Java 高级问题](#19-java-高级问题)
20. [Web 架构问题](#20-web-架构问题)

---

## 1. 评分系统如何运作

> **这是需要理解的最重要的一点。** 得州政府的招聘考官使用标准化的“面试评分矩阵 (Interview Scoring Matrix)”。他们不能凭“直觉”做决定。

### 运作机制

- 每道题都会由每位考官独立按照 **李克特量表 (Likert scale，1–5分)** 进行打分。
- 分数会汇总 → 累计总分最高者将获得推荐给 HRSS (人力资源) 的机会。
- 考官 **只能根据你明确说出的内容给分** — 如果你没有直接说出对应的关键词，他们就不能在评分表上打勾。

### 在每个回答中都融入这些 FJD (职位描述) 关键词

招聘公告使用了特定的措辞。请在面试中映射这些词语 (请在面试时直接使用英文原文)：

| FJD 短语 (摘自招聘公告) | 如何自然地融入回答 |
|---|---|
| "agency information technology (IT) standards" (机构IT标准) | "…adhering to agency IT standards…" |
| "requirements documentation" (需求文档) | "…based on requirements documentation…" |
| "requirements and design documentation" (需求与设计文档) | "…tested against requirements and design documentation…" |
| "configuration management" (配置管理) | "…tracked through our configuration management process…" |
| "quality assurance / quality control" (QA/QC) | "…coordinated QA/QC testing…" |
| "customer acceptance testing" (用户验收测试 / CAT) | "…guided the customer through customer acceptance testing…" |
| "peer reviews" (代码同侪审查) | "…submitted for peer review before merging…" |
| "coding best practices, techniques, and training needs" (编码最佳实践、技术及培训需求) | "…reviewing coding best practices and identifying training needs…" |
| "charters" / "business cases" (章程 / 商业案例) | "…documented in the project charter and business case…" |
| "project plans and status reports" (项目计划与状态报告) | "…communicated progress through status reports…" |
| "sponsor meetings" (赞助人/高管会议) | "…presented at sponsor meetings…" |
| "facilitate joint application development (JAD) sessions" (引导JAD会议) | "…which we defined during the JAD session…" |
| "analysis, development, programming, and implementation/modification" (分析、开发、编程以及实施/修改) | 在描述你的工作流程时，使用这个完整的序列。 |

> **经验法则：** 如果你在每个回答中都融入 3-4 个 FJD 短语，你就能将评分矩阵的得分最大化。

---

## 2. 六个要点概括这份工作

公告列出了整整六项职责。预期面试问题会映射到这几点：

| # | 职责 (摘自公告) | 他们会如何提问 |
|---|---|---|
| 1 | 领导/参与分析、开发、编程、实施/修改 | "请讲述一个由你从需求分析领导到最终部署的系统案例。" |
| 2 | 领导编码任务；审查/评估编码最佳实践及培训需求 | "你如何执行编码标准？" / "你如何指导他人？" |
| 3 | 基于需求文档提供技术建议并执行设计任务 | "请带我们了解一个你的设计决策。" / "你如何将需求转化为架构？" |
| 4 | 协调 QA/QC；参与/领导代码同侪审查；指导用户进行验收测试 | "你如何定义 '已完成(done)'？" / "你如何指导非技术用户进行测试？" |
| 5 | 创建章程、商业案例、项目计划、状态报告；引导 JAD 会议 | "你如何引导需求收集？" / "你如何处理利益相关方之间的冲突？" |
| 6 | 为机构员工提供客户支持 | "你如何处理一个感到沮丧的用户？" |

### 三项关键任职资格 (摘自公告)

这些是评分矩阵中占据最大权重的地方：

1. **ColdFusion, Java, Hibernate, Oracle, SQL, PL/SQL, JavaScript, CSS, XML, HTML** — 你的演示项目涵盖了所有这些。
2. **配置管理 (Configuration management)、QA、变更管理 (Change management)** — 讨论分支策略、CI 检查点、代码审查以及发布检查清单。
3. **业务分析：需求、设计/测试计划文档、JAD 会议** — 这是让你脱颖而出的关键差异点。

---

## 3. 面试形式与后勤安排

| 预期情况 | 详情 |
|---|---|
| **考官人数** | 2–3人：招聘经理 + 技术专家 (SME) + 可能有一名业务部门代表。 |
| **风格** | 按顺序阅读设定好的问题；考官通常 **不会** 换种说法，也不会偏离剧本。 |
| **多部分问题** | 他们可能只会一字不差地重复整个问题，而不会将其拆解。 |
| **时长** | 约 1 小时；可能是电话初面 + 线下面试/Teams视频。 |
| **笔试部分** | 很有可能 — SQL 除错、代码审查、设计题，或撰写需求。 |
| **发 Offer 周期** | 面试后 4–6 周。 |
| **背景调查** | 公告中确认需进行犯罪历史记录查询。 |
| **关键基础设施** | 根据《得州商业与商务法》§117.001(2)，此职位可能接触关键基础设施（网络安全、危险废物系统、水处理等）。 |

### 多部分问题应对策略

1. 考官阅读时，在你的记事本上 **记下关键短语**。
2. **重述：** "那个问题包含三个部分。让我逐一回答。" (That question has three parts. Let me address each.)
3. **明确标记：** "关于第一部分… 关于第二部分…" (For part one… For part two…)
4. **总结：** "总而言之…" (To bring it all together…)

---

## 4. JAD 会议引导与战略咨询

> **公告明确指出："facilitate joint application development (JAD) sessions" (引导JAD会议)。** 这是区分中级程序员与 Programmer IV 的关键。

### 他们会问什么

**"你如何引导一场需求收集会议？"**

> "我使用 JAD 方法论。在会议之前，我会准备好议程，确定合适的利益相关者 — 业务领域的专家 (SMEs)、IT 安全人员、开发负责人 — 并分发预读材料。在会议期间，我充当 **facilitator (引导者)**：我执行议程安排，将技术术语转化为业务语言，并维护一个 **parking lot (停车场/待定区)** 用于存放超出范围的想法。我会指派一名记录员实时记录决策。产出包括带有验收标准的 **requirements documentation (需求文档)**、流程图，以及包含范围和限制的 **project charter (项目章程)** — 这些都为接下来的 **design (设计)** 审查做好准备，并最终驱动 **customer acceptance testing (用户验收测试)** 的场景。"

### 冲突场景

| 场景 | 你的回答 |
|---|---|
| **"项目经理要求的功能违反了 IT 安全规定。"** | "我会首先确认他们潜在的业务需求，然后如实陈述限制条件。我会与他们协同设计一个能达到相同结果的 **secure alternative (安全替代方案)**，同时遵循 **agency IT standards (机构IT标准)**。" |
| **"与会者偏离主题，开始抱怨无关紧要的事情。"** | "我表示理解：'那很重要 — 让我把它加到 **parking lot (待定区)** 里。' 然后把话题拉回：'对于这次会议，我们需要在下午3点前敲定许可证的工作流。'" |
| **"两位利益相关者对某项业务规则产生分歧。"** | "我会中立地重述双方的立场，并询问：'我们能否根据 **requirements documentation (需求文档)** 对这两种规则进行测试评估？' 如果无法解决，我会记录下包含各自利弊的两种选择，并向上汇报到 **sponsor meeting (赞助人会议)**。" |

### 你的 JAD / 咨询方向的 STAR 故事

| | |
|---|---|
| **情境 (Situation)** | 构建 TCEQ 合规仪表板需要定义哪些指标才是关键的 — 设施、违规、罚款、逾期检查、即将过期的许可证 — 这需要跨越多个利益相关者的视角。 |
| **任务 (Task)** | 将监管合规需求转化为连贯的数据模型和双视图的 UI。 |
| **行动 (Action)** | 我创建了 **requirements documentation (需求文档)**，定义了 9 个仪表板指标及其明确的验收标准。我进行了领域建模：Facilities (设施) → Permits (许可证), Inspections (检查), Violations (违规) (1对多关系)。我记录了采用双重渲染 (交互式 SPA + 服务端渲染报告) 的 **business case (商业案例)**，以满足不同用户的需求。合规评分算法在编码前已被详细记录在 **design documentation (设计文档)** 中。 |
| **结果 (Result)** | 打造了 9 个实时指标、3 种图表可视化、可搜索的设施表格 — 而且全都可以追溯到 **requirements documentation (需求文档)**。双视图设计展示了满足各种利益相关者需求的灵活性。 |

---

## 5. 技术设计与架构

> **公告要求："Provide technical advice and perform design tasks… based on requirements documentation while adhering to agency IT standards." (基于需求文档提供技术建议并执行设计任务，同时遵循机构IT标准。)**

### 架构话术

**"向我们演示一下你将如何设计一个合规追踪应用。"**

> "我会采用符合 **agency IT standards (机构IT标准)** 的 **layered architecture (分层架构)**：表现层 → 控制器层 → 服务层 → 仓储层 → 持久层。在 **Java**/Spring 中，服务层拥有业务逻辑，而仓储层通过 **Hibernate**/JPA 对 **Oracle** 数据库处理数据访问。我会使用 **sequences (序列)**、带有级联删除的外键、**CHECK 约束** 以及对所有过滤列建立策略性索引来设计 Oracle 模式。ColdFusion 表现层可以在分阶段迁移的过程中通过指向同一个 Oracle 数据源与之共存。在安全方面，所有查询均使用参数化输入以防止 **SQL injection (SQL注入)**，所有输出都被编码以防范 XSS。"

### 数据库 (Oracle SQL / PL/SQL)

**Q: "如果遇到慢查询，你如何进行诊断？"**

> "首先，我会提取执行计划 — `EXPLAIN PLAN FOR` + `DBMS_XPLAN.DISPLAY` — 查找在应该使用索引扫描的地方是否发生了全表扫描。我会用 `SELECT last_analyzed FROM user_tables` 检查统计信息是否过时，并用 `DBMS_STATS` 重新收集。常见的修复方式：添加缺失的索引（尤其是外键列 — 在我的演示项目中我为每个外键都加了索引），将相关子查询重写为 JOIN，并检查隐式类型转换。在我的合规演示项目中，我为 `FACILITY_ID`、`STATUS`、`SEVERITY` 和 `EPA_ID` 创建了索引，因为这些都是仪表板的过滤列。"

**Q: "你如何设计向后兼容的模式 (Schema) 变更？"**

> "三条规则：(1) 在部署期间 **只能添加 (additive only)** — 将新增的列设为可为空或是带默认值；(2) **先改数据库，后动代码** — 这样新老版本代码都能运行；(3) 带有回滚脚本的 **版本化迁移 (versioned migrations)**。每一次改动都要通过 **configuration management (配置管理)** 和 **peer review (代码审查)**。这符合 TCEQ 的 **change management (变更管理)** 期望。"

### Web 服务 (SOAP vs REST)

**Q: "你什么时候会选择 SOAP 而不是 REST？"**

> "**SOAP** 适用于机构间正式的数据交换，这种场景需要 WS-Security 提供签名/加密消息、WSDL 契约以及事务支持。由于必须具备审计追踪，政府对政府的集成通常强制要求使用 SOAP。**REST** 适用于供 JavaScript 前端消费的内部 API，使用 JSON 就显得很自然。我的演示项目对内部仪表板使用了 REST (`/api/facilities`, `/api/dashboard/stats`)。"

### Java + Hibernate

**Q: "请解释一个由 ORM 行为引发的生产环境问题。"**

> "N+1 查询问题。例如获取设施列表后，遍历调用 `facility.getPermits()`，这会导致对每个设施触发一次 SELECT 查询。在我的演示里，`getFacilitySummaries()` 就有这个问题 — 在内存 H2 数据库上没感觉，但在涉及数千行数据的生产环境 **Oracle** 上将是灾难。解决方案：在 JPQL 中使用 `JOIN FETCH` 或使用 `@EntityGraph`。我会在 **QA/QC** 期间通过监控集成测试中的查询数量来捕捉这个问题，并在 **peer review (代码审查)** 时指出来。"

### ColdFusion

**Q: "给我展示一个你会使用的 ColdFusion 模式。"**

> "在我的 `facility_search.cfm` 中：使用 `<cfif>` 块动态生成 `WHERE 1=1` 查询，使用带有明确 `cfsqltype` 的 `<cfqueryparam>` 防止 SQL 注入，使用 `HTMLEditFormat()` 防护 XSS，以及使用 `startrow`/`maxrows` 实现服务端分页。在 `permit_report.cfm` 中：使用 `<cfstoredproc>` 调用 Oracle **PL/SQL**，`<cfchart>` 进行可视化，**Query-of-Queries (查询的查询)** 进行在内存中的数据重塑，以及 `<cfdocument>` 用于生成 PDF。"

### Linux / Shell 脚本编程

**Q: "你如何编写生产环境的 Shell 脚本？"**

> "三个原则：(1) **幂等性 (idempotent)** — 例如 `mkdir -p`, `CREATE TABLE IF NOT EXISTS`; (2) **快速失败 (fail-fast)** — `set -euo pipefail`; (3) **记录日志 (logged)** — 将输出重定向到带有时间戳的日志文件。凭证均来自环境变量，绝不硬编码。"

### 变更管理 (Change Management)

**Q: "你如何安全地发布代码？"**

> "特性分支 (Feature branch) → **peer review (代码审查)** → CI 运行测试 → 部署到 Staging 准生产环境 → **QA/QC** 根据 **design documentation (设计文档)** 进行签收 → **change request (变更请求)** 获批 → 带回滚脚本部署到生产环境。我的演示项目把 `./mvnw test` 作为 CI 关卡 — 15 个测试必须全过。在生产环境中，我还会加入静态分析和模式迁移的验证。"

---

## 6. 代码领导力与最佳实践

> **公告要求："Lead coding tasks… Review and evaluate coding best practices, techniques, and training needs." (领导编码任务...审查并评估编码最佳实践、技术及培训需求。)**

### STAR 故事

| | |
|---|---|
| **情境 (Situation)** | 需要构建一个合规系统，展示跨越 TCEQ 完整技术栈的生产级别模式：**Java, Hibernate, Oracle, SQL, PL/SQL, ColdFusion, JavaScript, CSS, XML, HTML**。 |
| **任务 (Task)** | 将其结构化，使得任何开发者 — 包括初级的 Programmer I–III — 都能维护和扩展它。 |
| **行动 (Action)** | 强制执行 **layered architecture (分层架构)**：模型 → 仓储 → 服务 → 控制器。每个实体都有注释。使用注解 *与* **XML** Hibernate 映射以展示新老项目的能力。编写了 15 个 JUnit 5 测试。兼容 Oracle 的 DDL 采用了统一的规范 (`FK_`, `UK_`, `CHK_`, `IDX_` 前缀)。 |
| **结果 (Result)** | 自解释的代码库：`architecture.md` 将每个需求映射到了其实现。一条命令即可测试通过。新来的开发者读 README 就能上手，这也展示了我会如何为团队创造 **training (培训)** 资料。 |

**Q: "你如何处理代码审查 (Code Reviews)？"**

> "我从四个维度审查：(1) **正确性** — 是否匹配 **requirements documentation (需求文档)**？(2) **安全性** — 参数化查询、输入验证、输出编码；(3) **可维护性** — 清晰命名、关注点分离；(4) **测试覆盖率** — 未测试的逻辑将被要求修改。我提供建设性的反馈，并将 **peer reviews (代码审查)** 视为为初级员工提供 **training (培训)** 的机会 — 这也正好呼应了公告里的 'evaluating training needs (评估培训需求)'。"

---

## 7. QA/QC 与测试协调

> **公告要求："Coordinate testing and Quality Assurance/Quality Control tasks… participate/lead peer reviews. Guide and assist customers with customer acceptance testing." (协调测试与QA/QC任务...参与/领导代码审查。指导并协助用户进行验收测试。)**

### STAR 故事

| | |
|---|---|
| **情境 (Situation)** | 合规评分算法非常关键 — 错误的评分将误导某个设施的环保合规状态。 |
| **任务 (Task)** | 确保逻辑经过彻底测试，且可追溯至 **requirements and design documentation (需求与设计文档)**。 |
| **行动 (Action)** | 为边界条件编写 JUnit 测试：无违规的设施 (≥90)，有2个 CRITICAL 违规的设施 (≤40)。验证扣分表：-5 MINOR / -15 MAJOR / -30 CRITICAL 等。我把算法记录在 `architecture.md` 里，这样不懂代码的 **QA/QC** 人员和参与 **customer acceptance testing (用户验收测试)** 的人也能验证设计意图。 |
| **结果 (Result)** | 15 个测试全部通过。规则清晰、可测试且可追溯。任何审核员均可验证其覆盖。 |

**Q: "你如何指导用户完成验收测试 (CAT)？"**

> "我会拿出一份根据 **JAD session (JAD会议)** 获取的验收标准而制定的 **test plan (测试计划)**。测试步骤以简单的描述编号写出：'步骤 1：搜索设施 X。预期：出现 3 条结果。' 我会现场带领用户走第一遍场景，接着在旁观察。缺陷附上严重程度标签追踪、分级，并在修复后重新测试。目标是让业务用户感受到他们是来掌控质量的 — 而非走走过场。"

---

## 8. 客户支持

> **公告要求："Provide customer support to agency staff specific to agency applications, IT policies, and general related IT areas." (就机构应用、IT政策及通用相关IT领域为机构员工提供客户支持。)**

**Q: "你如何支持机构员工？"**

> "带着耐心和同理心。机构员工是领域专家（环境科学家等），而非 IT 人员。我先 **倾听 (listen)** 去理解他们的工作流。我会用业务流程术语解释修复方案，而非抛给他们系统报错代码。在应用设计里我采用防御性编程：REST API 在找不到记录时返回标准的 HTTP 404，而非崩溃报错 500；ColdFusion 模板使用 `<cftry>/<cfcatch>` 容错；服务层使用 `@Transactional(readOnly = true)` 防止意外的数据变动。"

---

## 9. 行为面试题库

### "我们为什么要雇佣你？ (Why should we hire you?)"

> "我具备并且带来了职位所需的全套技术栈 — **ColdFusion, Java, Hibernate, Oracle, SQL, PL/SQL, JavaScript, CSS, XML, HTML** — 这些都在我写的一个合规追踪应用里展现。但这不仅仅是写代码：这个角色要求 **facilitating JAD sessions (引导JAD会议)**，创建 **charters and business cases (章程与商业案例)**，**leading peer reviews (领导代码审查)**，并指导 **customer acceptance testing (用户验收测试)**。我有在这整个生命周期中桥接业务需求与技术执行的经验。我受到了 TCEQ 保护健康与自然资源这笔使命的驱动，并且非常重视这份工作提供的 **长期稳定性 (long-term stability)**。"

### "讲述一次你与同事发生冲突的经历。"

> "我曾跟队友在重构还是打补丁这事上有分歧。与其凭直觉争论，我建议我们 **timebox a spike (设定期限做一个可行性实验)** 评估工作量。实验表明重构只需 3 天而且途径明确；于是我们达成共识，并先做了个垫片解决当下的冲刺瓶颈。这也是我解决 **JAD session** 分歧的方式 — 放缓情绪、收集证据、协同决策。"

### "说出一个你失败的经历。"

> "我曾部署过一个通过了 QA 的数据库模式迁移，却由于是全量数据而将生产环境某张表死锁了 20 分钟。我学到了：(1) 针对生产级规模的数据测试迁移；(2) 将迁移时间纳入 **QA/QC**；(3) 在 **change management (变更管理)** 请求中务必附带回滚脚本。现在我像对待代码一样对待数据变更 — **peer review (代码审查)**，准生产验证加上可靠的回滚措施。"

### "你如何向非技术用户解释技术概念？"

> "利用他们领域的类比。'数据库索引就好比文件柜标签 — 没它你就得每个抽屉挨个翻去找某一份文件。' 在我的演示里，我做了双视图 — 交互式 SPA 和传统服务端渲染 — 满足不同用户偏好。在 **JAD sessions** 里，我能实时将技术行话翻译成业务流程语言。"

### "面临相互冲突的需求，你如何排列优先级？"

> "影响度 × 紧急程度。一个阻碍合规汇报的生产环境 Bug 绝对优先于新功能请求。我通过 **status reports (状态报告)** 传达理由，确保各方明白。如果优先顺序在领导层内无法调和，我会带上权衡方案的正式报告，向上级或 **project sponsor (项目赞助人)** 汇报请示。"

---

## 10. 笔试 / 上机操作准备

### SQL 练习找 Bug

**原题: "Find the bug and fix it." (找出错误并修复。)**

```sql
SELECT f.facility_name, COUNT(p.id) AS permit_count, COUNT(v.id) AS violation_count
FROM facilities f
LEFT JOIN permits p ON f.id = p.facility_id
LEFT JOIN violations v ON f.id = v.facility_id
GROUP BY f.facility_name;
```

**答案:** "两次 LEFT JOIN 引起笛卡尔积（例如 3 许可证 × 2 违规 = 6 行），会放大了双边的计数统计。修复：使用 `COUNT(DISTINCT p.id)` 和 `COUNT(DISTINCT v.id)`，或者将其重构为关联子查询。"

### Code Review (代码审查) 实战

**原题: "Identify issues." (指出代码问题。)**

```java
public List<Facility> search(String term) {
    String sql = "SELECT * FROM facilities WHERE name LIKE '%" + term + "%'";
    return jdbcTemplate.query(sql, new FacilityRowMapper());
}
```

**答案:** "(1) **SQL 注入** — 拼接字符串不安全，应使用 `?`。 (2) 缺乏 **null 检查**。 (3) **前置通配符 (`%...`)** 会导致索引失效，可考虑 Oracle Text `CONTAINS()`。 (4) **未忽略大小写** — 添入 `UPPER()`。 (5) **没有分页** — 极其危险的无限制 `SELECT *`。"

### 设计 / 需求问答提示

**原题: "A division needs permit renewal tracking with notifications. Outline your approach." (某部门需要带有通知机制的许可证续签追踪。简单概述你的方案。)**

**答题骨架:**
- **Charter (章程):** 范畴 (Scope)、干系人、限制条件、时间表。
- **Requirements (需求) (来自 JAD 会议):** 追踪截止日，90/60/30天自动发通知，跟进审批流。
- **Data model (数据模型):** `PERMIT_RENEWALS (id, permit_id FK, due_date, notification_date, status, reviewer_id)`
- **Architecture (架构):** 定时任务 (Spring `@Scheduled` 或 Oracle `DBMS_SCHEDULER`) → 查询即将过期的情况 → 生成通知记录 → SMTP 电邮推送
- **QA/QC:** 将相关边界日期输入跑单元测试，整合针对发送邮件及整体流程的 **customer acceptance testing (用户验收测试)**。
- **Change management (变更管理):** 特性分支 → **peer review (代码审查)** → QA签收 → 变更请求获批 → 安排附带回退措施的投产。

---

## 11. 薪资定位策略

> **固定薪资不议价，考官更看重你能长期待下去并安于此待遇。**

### 切忌
- 别问升学通道如何或者其他商业福利
- 别跟市场的商业大厂瞎比行情
- 别一副特别想急着晋级升职的模样

### 最佳回复定场诗
- **"I value long-term stability" (我看重长治久安的环境)** — 直接告诉他们你不跳槽。
- **"I'm motivated by TCEQ's mission…" (我是真被本署使命召唤的...)** — 照原样用上机构宣言的内容。
- **"The total compensation is attractive…" (全面叠加起来的政府年薪福利真的打动我...)** — 对比强调福利的保障让你心安。
- **"I want to build and maintain systems that serve Texans for years" (我很向往能把青春奉献在这片为所有德克萨斯州民众护航的系统架构身上永不断电不停歇...)** — 奠定愿意持久为人民服务的核心。

---

## 12. 面试当天清单

### 面试之前
- 运行 `./mvnw clean test` — 确保 15 个测试全绿。
- 启动应用 `./mvnw spring-boot:run` — 确认仪表板依然健在。
- 打印简历 + `docs/architecture.md` 系统架构图。
- 出声过几遍那几则简短有力的 STAR 故事（卡好在2分钟内）。
- 翻遍那堆 FJD 重点高频职场英文硬词汇（多念两遍找感觉，现场脱口而出）。

### 你应该储备的向考官发问的杀手提问
1. "关于那个 **ColdFusion-to-Java migration (ColdFusion向Java的转化迁移)** 目前的长线蓝图是怎么落子的？"
2. "针对那门大任务 **JAD sessions (联手协作共调会)**，通常是几头下功夫办的？那种严肃且长周期的大会，定论又如何下呢？"
3. "在此部门内推动 **peer review and change management (群议互审及统筹部署更改)** 的规范化程度，它实际上运作起来是啥光景？"
4. "如果有机会重构推进一下 **automated testing or CI/CD (全自动化测试铺路与CI持续发版流水线)** 进程的话，你们乐意拨下这个余力么？"
5. "当下署里面关于 **telework/hybrid policy (远程灵活工时准则)** 是如何把控风向和执行尺度的？"

---

## 13. 技术面试题库 — 按技术栈分类 (缩略要点)

### ColdFusion
- **`<cfqueryparam>`:** 它的 `cfsqltype` 能把 SQL 注入阻挡在千里之外。绝不拼接字符串进 `<cfquery>`。
- **Query-of-Queries (QoQ/连环查):** 把已抽上来的盘集放内存里就地过滤或是重新组合（用在减灾报表重筛选极佳）。
- **生成 PDF:** 就是套上 `<cfdocument format="PDF">`。
- **Application vs Session:** 区分开全盘全局通用 (Application) 还是仅限制在单一访客名头跟进 (Session)。防死串号踩踏案。

### Java
- **接口 / 抽象类:** 接口立约，抽象带框架实做。
- **依赖注入 (DI):** 拿 `@Autowired` 把外接工具强塞进入。易于造出 mock 工具给测试层做检验。松耦合配置的法宝。
- **`try-with-resources`:** JDK7出的铁布衫，它能自觉且果决的将任何实现 `AutoCloseable` 资源的开支闭流给收缴干净（绝杀连接泄露隐患）。
- **Streams (流操作的利器):** 给出一套好上手的流水管道对各集合盘类处理进行组合化运算：像通过 `.filter(...)` 去纯净提取所需队列便毫不造作。

### Hibernate / JPA
- **N+1 连环杀手之谜:** 父类带下 N 多子栏却每点一个就查一次，1+N次循环查询毁天灭地。对策：扔上 `JOIN FETCH` 去斩立决！
- **惰态 / 急迫 加载 (Lazy / Eager):** 集合等大多一并设在底部的通常就走惰态（用到才去扫）。只有真死磕紧连不分家的小量集合方适用急迫上树加载机制。
- **ORM产生的隐性更新 (Dirty Checking):** Hibernate会紧盯被事务层托管着的那堆实体一旦变异直接触发 UPDATE (无需任何 save)。要防备被自己作死误惹！

### Oracle SQL
- **WHERE / HAVING:** `WHERE` 挡在排队聚合前；`HAVING` 生效于聚合扎堆后的总门槛处。聚合函式断不可拿进 `WHERE` 里掺和。
- **Oracle 序列号机制:** 搞定那条 `CREATE SEQUENCE` 然后配对使用 `NEXTVAL` 是规矩所在。好处是可以共享还带缓存不撞号不塌方。
- **DELETE, TRUNCATE, DROP:** 讲明白 `DELETE` 有迹可跟、可回溯，但是它在带日志运作下自然很慢；而涉及到严防严守的监控系统只当留迹取证为优，即绝不多推 `TRUNCATE` 或 `DROP` 上前线毁尸灭迹。

### JavaScript, CSS 等相关
- 用 `const`/`let` 取缔旧习里的 `var`。使用具有更强承接语义的 `fetch()` 去平替垂老的 `XHR`。防制 XSS 绝对不能裸塞 `innerHTML` (乖乖用 `textContent`！)。排版布阵时把响应式基准卡定在 Flexbox / CSS Grid 上面去。

---

## 14. 高级行为面试题

- **"如果你觉得下级写的代码总是很垃圾，你怎么处理？"**
  私下展开对谈为第一优先。陪着做结对编程拉拉手感，然后审视是否属于对那些既定框架标准还没吸收全吃透，有针对性的发放下发文档帮助。不可当众鞭挞指点，要注重提升修养并回归为一种 **training needs (培训拉伸需求)**！
- **"若是遭遇工期将超红线告急，身为把控人的你会咋着整？"**
  不掩饰！诚恳、透亮的通过定期 **status reports (行军报告书)** 告知上峰风险进度。提出对相关功能下放删减挪后处理的主意提议案。永远都不能选择闷不吭气藏匿败象，这会引发信用雪崩。

---

## 17. 系统设计问题

### SD-1: "如何设计许可证追踪与自动推发警示系统？"
- **切入框架:** 先讲上 `DBMS_SCHEDULER` (驻守在底层定时敲钟的定时大妖)。
- **查账查表:** 捞一遍还有90/60/30日出局的。
- **排雷与落印:** 送警报前必验是否存挡 `NOTIFICATION` 以绝重传或错送；警钟信差全推入发送队去。最后切莫落了加层监控去统调。

### SD-2: "接盘一大堆旧日的 ColdFusion 代码要往新的 Java 系拔管，你将怎样开动？"
- 大旗插上：**绝不一刀切 (Never big-bang rewrite)**！
- 首当其冲先搞好前线收放哨点(代理服务)，将旧老和新式统统接好线分好流再统共吃用相同的 Oracle 数据池子。
- 跟着把最硬核业务用 Java REST 下放代替。页面一页一页拿下来（先上核心后补冷门）直达平顺度劫。

### SD-6: "面对锁死停摆的表盘与死锁 (Deadlocks) 怎么办？"
- 识破阵眼在互相抢地盘不放手的时差：顺着用 `V$LOCK` 加找寻蛛丝马迹。
- 处方对路化：必须勒令全部进程全按同样统序来进击并搜取表列加封锁线 (如按主键顺序来攀咬资源)，将过长战线切掉搞快准狠频密释放 (多重快提交)，抑或加派只读化特快道 (`READ ONLY`) 来保住查报表活路的通顺性。

---

## 致谢范文要点提取
- 邮件题目标定好编号 `Programmer IV Interview (Position 00055221)`。
- 内文提及 `I enjoyed discussing the agency's approach to [specific topic] (非常喜欢与你们探讨就某某技术话题的深意远景)` 来展现你是真入局真倾听了的。
- 压轴重申你很想要与 TCEQ 合璧，并且你热烈抱持着能够通过效命而惠及大众、匡扶水土的崇高奉献热诚。
