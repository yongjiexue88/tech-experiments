#!/usr/bin/env python3
from __future__ import annotations

import hashlib
from datetime import datetime
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import LETTER
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import ListFlowable, ListItem, PageBreak, Paragraph, Preformatted, SimpleDocTemplate, Spacer, Table, TableStyle


ROOT = Path(__file__).resolve().parents[1]
SOURCE_MD = ROOT / "docs" / "interview_prep.md"
OUTPUT_DIR = ROOT / "output" / "pdf"
OUTPUT_PATH = OUTPUT_DIR / "interview_prep.pdf"

INK = colors.HexColor("#17313B")
ACCENT = colors.HexColor("#A2542A")
ACCENT_DARK = colors.HexColor("#7C3D1B")
MUTED = colors.HexColor("#5E6C73")
LINE = colors.HexColor("#D9DFE3")
PAPER = colors.HexColor("#FBF8F2")
SOFT = colors.HexColor("#F3F6F7")
CALLOUT = colors.HexColor("#EEF3E7")
WARN = colors.HexColor("#FFF3DB")
CODE_BG = colors.HexColor("#F6F7F9")


def build_styles() -> dict[str, ParagraphStyle]:
    styles = getSampleStyleSheet()
    return {
        "eyebrow": ParagraphStyle(
            "eyebrow",
            parent=styles["Normal"],
            fontName="Helvetica-Bold",
            fontSize=8.5,
            leading=10,
            textColor=ACCENT_DARK,
            alignment=TA_CENTER,
            uppercase=True,
            spaceAfter=8,
            tracking=0.6,
        ),
        "title": ParagraphStyle(
            "title",
            parent=styles["Title"],
            fontName="Helvetica-Bold",
            fontSize=25,
            leading=30,
            textColor=INK,
            alignment=TA_CENTER,
            spaceAfter=10,
        ),
        "subtitle": ParagraphStyle(
            "subtitle",
            parent=styles["Normal"],
            fontName="Helvetica",
            fontSize=11,
            leading=15,
            textColor=MUTED,
            alignment=TA_CENTER,
            spaceAfter=18,
        ),
        "section": ParagraphStyle(
            "section",
            parent=styles["Heading1"],
            fontName="Helvetica-Bold",
            fontSize=16,
            leading=20,
            textColor=INK,
            spaceBefore=8,
            spaceAfter=8,
        ),
        "subsection": ParagraphStyle(
            "subsection",
            parent=styles["Heading2"],
            fontName="Helvetica-Bold",
            fontSize=11.5,
            leading=14,
            textColor=ACCENT_DARK,
            spaceBefore=4,
            spaceAfter=4,
        ),
        "body": ParagraphStyle(
            "body",
            parent=styles["Normal"],
            fontName="Helvetica",
            fontSize=10.2,
            leading=14.2,
            textColor=INK,
            alignment=TA_LEFT,
            spaceAfter=7,
        ),
        "body_tight": ParagraphStyle(
            "body_tight",
            parent=styles["Normal"],
            fontName="Helvetica",
            fontSize=9.6,
            leading=12.8,
            textColor=INK,
            alignment=TA_LEFT,
            spaceAfter=5,
        ),
        "small": ParagraphStyle(
            "small",
            parent=styles["Normal"],
            fontName="Helvetica",
            fontSize=8.5,
            leading=11,
            textColor=MUTED,
            spaceAfter=4,
        ),
        "callout_title": ParagraphStyle(
            "callout_title",
            parent=styles["Heading3"],
            fontName="Helvetica-Bold",
            fontSize=11,
            leading=13,
            textColor=INK,
            spaceAfter=4,
        ),
        "callout_body": ParagraphStyle(
            "callout_body",
            parent=styles["Normal"],
            fontName="Helvetica",
            fontSize=9.7,
            leading=13,
            textColor=INK,
            spaceAfter=4,
        ),
        "table": ParagraphStyle(
            "table",
            parent=styles["Normal"],
            fontName="Helvetica",
            fontSize=8.9,
            leading=11.5,
            textColor=INK,
            spaceAfter=0,
        ),
        "table_header": ParagraphStyle(
            "table_header",
            parent=styles["Normal"],
            fontName="Helvetica-Bold",
            fontSize=8.9,
            leading=11,
            textColor=colors.white,
            spaceAfter=0,
        ),
        "code": ParagraphStyle(
            "code",
            parent=styles["Code"],
            fontName="Courier",
            fontSize=8,
            leading=10.2,
            textColor=INK,
            leftIndent=0,
            rightIndent=0,
            spaceAfter=0,
        ),
        "footer": ParagraphStyle(
            "footer",
            parent=styles["Normal"],
            fontName="Helvetica",
            fontSize=8,
            leading=10,
            textColor=MUTED,
            alignment=TA_CENTER,
        ),
    }


STYLES = build_styles()


def source_metadata() -> tuple[str, str]:
    digest = hashlib.sha256(SOURCE_MD.read_bytes()).hexdigest()[:12]
    stamp = datetime.fromtimestamp(SOURCE_MD.stat().st_mtime).strftime("%Y-%m-%d %H:%M:%S")
    return stamp, digest


def p(text: str, style: str = "body") -> Paragraph:
    return Paragraph(text, STYLES[style])


def bullet_list(items: list[str], style: str = "body_tight", bullet_color=ACCENT_DARK) -> ListFlowable:
    flowables = [ListItem(Paragraph(item, STYLES[style])) for item in items]
    return ListFlowable(
        flowables,
        bulletType="bullet",
        leftIndent=16,
        bulletFontName="Helvetica-Bold",
        bulletFontSize=9,
        bulletColor=bullet_color,
        spaceBefore=2,
        spaceAfter=8,
    )


def box(title: str, body: list, background=SOFT, border=LINE, width=None) -> Table:
    width = width or 6.85 * inch
    content = [Paragraph(title, STYLES["callout_title"])] + body
    table = Table([[content]], colWidths=[width])
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), background),
                ("BOX", (0, 0), (-1, -1), 0.8, border),
                ("LEFTPADDING", (0, 0), (-1, -1), 12),
                ("RIGHTPADDING", (0, 0), (-1, -1), 12),
                ("TOPPADDING", (0, 0), (-1, -1), 10),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 10),
            ]
        )
    )
    return table


def simple_table(
    rows: list[list],
    col_widths: list[float],
    repeat_header: bool = True,
    top_space: float = 4,
    bottom_space: float = 10,
) -> list:
    table = Table(rows, colWidths=col_widths, repeatRows=1 if repeat_header else 0)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), INK),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("FONTSIZE", (0, 0), (-1, -1), 8.7),
                ("LEADING", (0, 0), (-1, -1), 11.2),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("ALIGN", (0, 0), (-1, 0), "LEFT"),
                ("GRID", (0, 0), (-1, -1), 0.5, LINE),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, SOFT]),
                ("LEFTPADDING", (0, 0), (-1, -1), 7),
                ("RIGHTPADDING", (0, 0), (-1, -1), 7),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    return [Spacer(1, top_space), table, Spacer(1, bottom_space)]


def code_block(code: str) -> Table:
    block = Table([[Preformatted(code.strip(), STYLES["code"])]], colWidths=[6.85 * inch])
    block.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), CODE_BG),
                ("BOX", (0, 0), (-1, -1), 0.8, LINE),
                ("LEFTPADDING", (0, 0), (-1, -1), 10),
                ("RIGHTPADDING", (0, 0), (-1, -1), 10),
                ("TOPPADDING", (0, 0), (-1, -1), 8),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
            ]
        )
    )
    return block


def story_card(title: str, situation: str, move: str, result: str, keywords: str) -> Table:
    rows = [
        [Paragraph(title, STYLES["callout_title"])],
        [Paragraph(f"<b>Situation:</b> {situation}", STYLES["callout_body"])],
        [Paragraph(f"<b>Your move:</b> {move}", STYLES["callout_body"])],
        [Paragraph(f"<b>Result:</b> {result}", STYLES["callout_body"])],
        [Paragraph(f"<b>Keywords to say:</b> {keywords}", STYLES["small"])],
    ]
    table = Table(rows, colWidths=[6.85 * inch])
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), colors.white),
                ("BOX", (0, 0), (-1, -1), 0.8, LINE),
                ("BACKGROUND", (0, 0), (-1, 0), CALLOUT),
                ("LEFTPADDING", (0, 0), (-1, -1), 11),
                ("RIGHTPADDING", (0, 0), (-1, -1), 11),
                ("TOPPADDING", (0, 0), (-1, -1), 7),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
            ]
        )
    )
    return table


def build_story() -> list:
    story = []
    source_stamp, source_hash = source_metadata()

    snapshot_rows = [
        [
            p("<b>Role Snapshot</b>", "table_header"),
            p("<b>What To Emphasize</b>", "table_header"),
        ],
        [
            p(
                "Programmer IV, Information Resources Division, Austin HQ.<br/>"
                "Posted salary: $7,745 per month. Two openings. Schedule: Monday to Friday, 8am to 5pm.",
                "table",
            ),
            p(
                "Lead the full lifecycle: analysis, design, programming, implementation, QA/QC, customer acceptance testing, and long-term support.",
                "table",
            ),
        ],
        [
            p(
                "Technical anchors from the posting and demo: ColdFusion, Java, Hibernate, Oracle SQL, PL/SQL, JavaScript, CSS, XML, HTML.",
                "table",
            ),
            p(
                "Senior-level differentiators: JAD facilitation, requirements documentation, business cases, project plans, peer reviews, and agency IT standards.",
                "table",
            ),
        ],
    ]

    snapshot_table = Table(
        [[snapshot_rows[0][0], snapshot_rows[0][1]], [snapshot_rows[1][0], snapshot_rows[1][1]], [snapshot_rows[2][0], snapshot_rows[2][1]]],
        colWidths=[3.35 * inch, 3.35 * inch],
    )
    snapshot_table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), INK),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("GRID", (0, 0), (-1, -1), 0.5, LINE),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white]),
                ("LEFTPADDING", (0, 0), (-1, -1), 9),
                ("RIGHTPADDING", (0, 0), (-1, -1), 9),
                ("TOPPADDING", (0, 0), (-1, -1), 8),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ]
        )
    )

    story.extend(
        [
            Spacer(1, 0.45 * inch),
            p("TCEQ Programmer IV", "eyebrow"),
            p("Interview Packet", "title"),
            p(
                "A tighter, PDF-ready version of the original prep guide, rewritten to be faster to scan, easier to rehearse, and easier to use the night before the interview.",
                "subtitle",
            ),
            snapshot_table,
            Spacer(1, 0.12 * inch),
            box(
                "Source Verification",
                [
                    p(
                        f"Built from docs/interview_prep.md. Source last updated: {source_stamp}. Source hash: {source_hash}.",
                        "callout_body",
                    )
                ],
                background=colors.white,
                border=LINE,
            ),
            Spacer(1, 0.18 * inch),
            box(
                "Your one-sentence positioning",
                [
                    p(
                        "I translate regulated business requirements into secure, testable software, lead the work from JAD through customer acceptance testing, and communicate clearly with both technical staff and agency users.",
                        "callout_body",
                    )
                ],
                background=PAPER,
                border=ACCENT,
            ),
            Spacer(1, 0.16 * inch),
            box(
                "What changed from the raw Markdown",
                [
                    bullet_list(
                        [
                            "Condensed repetitive sections into a practical interview packet.",
                            "Added a 30-second opener, a 90-second pitch, a 90-day plan, and cleaner closing lines.",
                            "Reframed the tone to sound more professional and less combative than the original notes.",
                            "Kept examples tied to the actual demo app so the prep matches the code you can discuss.",
                        ],
                        style="callout_body",
                    )
                ],
                background=SOFT,
            ),
            PageBreak(),
        ]
    )

    top_table = Table(
        [
            [p("<b>30-second opener</b>", "table_header"), p("<b>90-second value pitch</b>", "table_header")],
            [
                p(
                    "I am a full-stack developer who is strongest when the work sits at the intersection of legacy systems, modern application design, and stakeholder communication. The TCEQ Programmer IV role matches that well because it combines Java and Oracle development with requirements facilitation, QA/QC, peer reviews, and customer support.",
                    "table",
                ),
                p(
                    "In my TCEQ compliance demo, I built a system that mirrors the stack in the posting: Spring Boot, Hibernate, Oracle-style SQL, PL/SQL patterns, ColdFusion templates, JavaScript, CSS, XML, and HTML. What I would bring here is not just coding speed. I can convert requirements into design documentation, lead reviews, structure testing around acceptance criteria, and explain trade-offs clearly to program staff. That mix of technical depth and delivery discipline is what makes me a fit for a Programmer IV role.",
                    "table",
                ),
            ],
        ],
        colWidths=[3.35 * inch, 3.35 * inch],
    )
    top_table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), INK),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("GRID", (0, 0), (-1, -1), 0.5, LINE),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white]),
                ("LEFTPADDING", (0, 0), (-1, -1), 9),
                ("RIGHTPADDING", (0, 0), (-1, -1), 9),
                ("TOPPADDING", (0, 0), (-1, -1), 8),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ]
        )
    )

    story.extend(
        [
            p("1. Interview Frame", "section"),
            p(
                "The panel is scoring evidence, not potential. If a keyword or a delivery step is important, say it out loud. Your goal is to sound structured, senior, and easy to trust with regulated systems.",
                "body",
            ),
            top_table,
            Spacer(1, 8),
            box(
                "Why TCEQ answer",
                [
                    p(
                        "I want to work on systems with a clear public mission, long service life, and real operational impact. TCEQ sits in a regulated environment where software quality, change management, and security matter, and that is the kind of work I enjoy. I also value the chance to improve critical applications over time instead of just shipping short-lived features.",
                        "callout_body",
                    )
                ],
                background=CALLOUT,
            ),
            Spacer(1, 8),
            box(
                "Closing line",
                [
                    p(
                        "This role lines up with how I work best: start with clear requirements, design carefully, build cleanly, test against documented expectations, and support the users after go-live.",
                        "callout_body",
                    )
                ],
                background=WARN,
            ),
        ]
    )

    story.extend(
        [
            Spacer(1, 10),
            p("2. How To Score Well", "section"),
            p(
                "The posting strongly suggests a standard state interview matrix. That means you want answers with an explicit sequence instead of free-form storytelling.",
                "body",
            ),
        ]
    )

    answer_formula = [
        [p("Step", "table_header"), p("What to do", "table_header"), p("Example phrase", "table_header")],
        [p("1. Frame the work", "table"), p("Name the system, business problem, or customer need.", "table"), p("In a compliance tracking project, the business need was...", "table")],
        [p("2. Show ownership", "table"), p("State your role and why you were accountable.", "table"), p("I led the design and coordinated the implementation plan...", "table")],
        [p("3. Use process words", "table"), p("Say requirements documentation, design documentation, peer review, QA/QC, and customer acceptance testing when true.", "table"), p("We documented the requirement, reviewed the design, and tested against acceptance criteria.", "table")],
        [p("4. Show standards and risk control", "table"), p("Mention agency IT standards, security, configuration management, or change management.", "table"), p("We deployed through our configuration management and change control process.", "table")],
        [p("5. Land on outcome", "table"), p("Finish with business value, reduced risk, or user adoption.", "table"), p("The result was a traceable, supportable release with clearer reporting for staff.", "table")],
    ]
    story.extend(simple_table(answer_formula, [1.2 * inch, 2.55 * inch, 3.1 * inch]))

    keywords_rows = [
        [p("Use these phrases naturally", "table_header"), p("Why they matter", "table_header")],
        [p("agency IT standards", "table"), p("Signals governance and security awareness.", "table")],
        [p("requirements documentation and design documentation", "table"), p("Shows traceability from idea to implementation.", "table")],
        [p("configuration management and change management", "table"), p("Shows disciplined releases in regulated systems.", "table")],
        [p("QA/QC, peer reviews, and customer acceptance testing", "table"), p("Shows quality ownership beyond coding.", "table")],
        [p("charter, business case, project plan, status report", "table"), p("Shows you can operate above an individual contributor coding lane.", "table")],
        [p("JAD session and sponsor meeting", "table"), p("Shows stakeholder facilitation and escalation judgment.", "table")],
    ]
    story.extend(simple_table(keywords_rows, [3.2 * inch, 3.5 * inch]))

    story.extend(
        [
            p("3. Core Stories To Memorize", "section"),
            p(
                "Memorize five stories you can bend to different questions. The goal is not to memorize every sentence. Memorize the structure, the metrics, the design choices, and the outcome.",
                "body",
            ),
            story_card(
                "Story 1: End-to-end delivery",
                "You built a compliance tracking demo that had to show both technical breadth and a credible government-style delivery lifecycle.",
                "You used a layered architecture, documented the data model and scoring rules, and tied the implementation back to requirements and design documentation.",
                "You can point to a working application, a documented architecture, and a clear explanation of how analysis became implementation.",
                "analysis, development, programming, implementation, requirements documentation, design documentation",
            ),
            Spacer(1, 8),
            story_card(
                "Story 2: JAD and stakeholder alignment",
                "Different stakeholders care about different compliance metrics, workflows, and reporting outputs.",
                "You prepared an agenda, defined the metrics and acceptance criteria, captured decisions, and used a parking-lot approach to keep the session on track.",
                "You left with clearer scope, fewer assumptions, and artifacts the technical team could implement and the business side could review.",
                "JAD session, charter, business case, sponsor meeting, requirements documentation",
            ),
            Spacer(1, 8),
            story_card(
                "Story 3: Design and architecture judgment",
                "The posting expects technical advice, not just coding.",
                "You explain a layered design: presentation, controller, service, repository, persistence. You justify Oracle-style DDL, sequences, constraints, indexes, and secure query patterns.",
                "You sound like someone who can design a maintainable application that fits agency standards and survives production support.",
                "technical advice, agency IT standards, layered architecture, Oracle, Hibernate, secure coding",
            ),
            Spacer(1, 8),
            story_card(
                "Story 4: QA/QC and acceptance testing",
                "The compliance scoring logic is business-critical and easy to get wrong if it is not documented and tested carefully.",
                "You wrote tests against seeded data, covered clean and high-risk facilities, and translated the rules into a plain-language acceptance test flow.",
                "You can say the system is testable, traceable, and understandable by both reviewers and end users.",
                "QA/QC, peer review, customer acceptance testing, test plan, traceability",
            ),
            Spacer(1, 8),
            story_card(
                "Story 5: Legacy modernization without breaking support",
                "The role spans ColdFusion, Java, and long-lived agency systems.",
                "You explain how to keep ColdFusion pages usable while moving business logic into Java services, using shared Oracle data sources and phased migration.",
                "You show respect for legacy systems while still sounding modern and pragmatic.",
                "application maintenance, customer support, ColdFusion, phased migration, change management",
            ),
        ]
    )

    story.extend(
        [
            PageBreak(),
            p("4. Demo App Facts You Can Reference", "section"),
            p(
                "These details are useful because they map your answers to real files in the project instead of generic examples.",
                "body",
            ),
        ]
    )

    demo_rows = [
        [p("Artifact", "table_header"), p("What to say about it", "table_header")],
        [
            p("docs/architecture.md", "table"),
            p("Shows the layered architecture, the data model, and the compliance scoring algorithm in a reviewer-friendly format.", "table"),
        ],
        [
            p("ComplianceServiceTest.java", "table"),
            p("Contains 15 integration-style tests, including dashboard data, facility summaries, and high-severity compliance score scenarios.", "table"),
        ],
        [
            p("coldfusion/permit_report.cfm", "table"),
            p("Demonstrates Oracle stored procedure calls, Query-of-Queries, PDF-style reporting, and defensive error handling in CFML.", "table"),
        ],
        [
            p("Facility.hbm.xml plus JPA annotations", "table"),
            p("Lets you talk about legacy and modern Hibernate mapping approaches in the same codebase.", "table"),
        ],
        [
            p("schema.sql and procedures.sql", "table"),
            p("Useful proof points for Oracle-style DDL, sequences, constraints, and PL/SQL thinking.", "table"),
        ],
    ]
    story.extend(simple_table(demo_rows, [2.1 * inch, 4.6 * inch]))

    technical_rows = [
        [p("Stack", "table_header"), p("Likely prompt", "table_header"), p("Answer anchors", "table_header")],
        [
            p("ColdFusion", "table"),
            p("How do you query safely or generate reports?", "table"),
            p("Use &lt;cfqueryparam&gt;, &lt;cfstoredproc&gt;, Query-of-Queries, HTML encoding, and a phased migration plan to Java services.", "table"),
        ],
        [
            p("Java / Spring", "table"),
            p("How do you structure maintainable application code?", "table"),
            p("Constructor injection, service boundaries, exception handling with useful context, and tests that verify business rules.", "table"),
        ],
        [
            p("Hibernate / JPA", "table"),
            p("What production issue do you watch for?", "table"),
            p("N+1 queries, fetch strategy, transaction boundaries, and sequence-based ID generation for Oracle.", "table"),
        ],
        [
            p("Oracle SQL / PL/SQL", "table"),
            p("How do you tune or evolve the database safely?", "table"),
            p("Execution plans, index strategy, additive schema changes, rollback scripts, stored procedures, and validation constraints.", "table"),
        ],
        [
            p("JavaScript / HTML / CSS / XML", "table"),
            p("How do you keep the UI secure and maintainable?", "table"),
            p("Fetch API, no raw innerHTML for user data, semantic HTML, responsive layout, and accessibility basics like focus states and contrast.", "table"),
        ],
        [
            p("Delivery / Ops", "table"),
            p("How do you ship safely in government work?", "table"),
            p("Peer review, CI test gate, staging validation, QA/QC sign-off, change request approval, and rollback readiness.", "table"),
        ],
    ]
    story.extend(simple_table(technical_rows, [1.2 * inch, 1.95 * inch, 3.8 * inch]))

    story.extend(
        [
            box(
                "Security-first phrasing that works well in this interview",
                [
                    bullet_list(
                        [
                            "I validate inputs, use parameterized queries, encode output, and design for traceability.",
                            "I assume the application may support critical infrastructure workflows, so I favor conservative release practices.",
                            "I do not treat peer review or QA/QC as bureaucracy. In this environment they are production risk controls.",
                        ],
                        style="callout_body",
                    )
                ],
                background=CALLOUT,
            ),
            Spacer(1, 8),
            p("5. Written Exercise Playbook", "section"),
            p(
                "If they include a short written or whiteboard exercise, stay calm and show your thinking. Panels often reward method over perfect syntax.",
                "body",
            ),
            p("SQL bug pattern", "subsection"),
            code_block(
                """
SELECT f.facility_name,
       COUNT(p.id) AS permit_count,
       COUNT(v.id) AS violation_count
FROM facilities f
LEFT JOIN permits p ON f.id = p.facility_id
LEFT JOIN violations v ON f.id = v.facility_id
GROUP BY f.facility_name;
                """
            ),
            Spacer(1, 6),
            box(
                "What to say",
                [
                    p(
                        "The double LEFT JOIN can multiply rows. If one facility has 3 permits and 2 violations, the joined set returns 6 rows. I would fix it with COUNT(DISTINCT ...), or by aggregating permits and violations separately before joining back to facilities.",
                        "callout_body",
                    )
                ],
                background=SOFT,
            ),
            Spacer(1, 8),
            p("Code review pattern", "subsection"),
            code_block(
                """
public List<Facility> search(String term) {
    String sql = "SELECT * FROM facilities WHERE name LIKE '%" + term + "%'";
    return jdbcTemplate.query(sql, new FacilityRowMapper());
}
                """
            ),
            Spacer(1, 6),
            box(
                "What to flag",
                [
                    bullet_list(
                        [
                            "SQL injection risk from string concatenation.",
                            "No null or empty handling for term.",
                            "Leading wildcard can block normal index use.",
                            "No pagination or result limit.",
                            "No case-normalization strategy if the requirement expects case-insensitive search.",
                        ],
                        style="callout_body",
                    )
                ],
                background=SOFT,
            ),
            Spacer(1, 8),
            p("Design prompt pattern", "subsection"),
            box(
                "Safe answer outline",
                [
                    bullet_list(
                        [
                            "Start with charter: scope, stakeholders, timeline, constraints.",
                            "Run a JAD session and capture requirements documentation with acceptance criteria.",
                            "Propose the data model, service flow, notifications, reporting, and audit needs.",
                            "Define QA/QC, customer acceptance testing, deployment, and rollback up front.",
                            "End by naming assumptions, open questions, and the first deliverables you would produce.",
                        ],
                        style="callout_body",
                    )
                ],
                background=WARN,
            ),
        ]
    )

    story.extend(
        [
            PageBreak(),
            p("6. The Questions Most Worth Asking", "section"),
            p(
                "Prepare four to six. Prioritize the ones that reveal team maturity, modernization plans, and how much responsibility the role really carries.",
                "body",
            ),
        ]
    )

    ask_rows = [
        [p("Question", "table_header"), p("Why it matters", "table_header")],
        [p("What applications would I likely support in the first 90 days?", "table"), p("Shows readiness to contribute early and reveals the real mix of maintenance versus new development.", "table")],
        [p("Is there a formal ColdFusion-to-Java modernization roadmap?", "table"), p("Shows strategic thinking and tests how much architectural influence the role has.", "table")],
        [p("How are JAD sessions usually run here?", "table"), p("Directly probes one of the strongest differentiators in the posting.", "table")],
        [p("What does peer review and change management look like in practice?", "table"), p("Reveals delivery discipline and whether quality gates are real or informal.", "table")],
        [p("How much customer support responsibility sits with the development team?", "table"), p("Clarifies whether you will be expected to troubleshoot directly with agency staff.", "table")],
        [p("What would success look like in the first six months for this role?", "table"), p("Shows ownership and may surface expectations that were not obvious from the posting.", "table")],
    ]
    story.extend(simple_table(ask_rows, [3.5 * inch, 3.2 * inch]))

    story.extend(
        [
            p("7. Day-Of Plan", "section"),
            p(
                "Make the day easy on yourself. The goal is to reduce avoidable friction so you can spend all your energy on the interview itself.",
                "body",
            ),
        ]
    )

    day_rows = [
        [p("Before", "table_header"), p("During", "table_header"), p("After", "table_header")],
        [
            bullet_list(
                [
                    "Run the demo tests and verify you remember where the architecture and ColdFusion files live.",
                    "Practice the opener, the value pitch, and the five core stories out loud.",
                    "Bring resume copies, a notepad, a pen, and a photo ID.",
                ],
                style="table",
                bullet_color=ACCENT_DARK,
            ),
            bullet_list(
                [
                    "Take notes on multi-part questions and answer each part explicitly.",
                    "Keep answers near two minutes unless asked to go deeper.",
                    "Reference the demo app whenever a technical question gives you an opening.",
                ],
                style="table",
                bullet_color=ACCENT_DARK,
            ),
            bullet_list(
                [
                    "Thank the panel by name and ask about next steps.",
                    "Send a concise thank-you note to the HR contact within 24 hours.",
                    "Respond quickly if they ask for references or background-check materials.",
                ],
                style="table",
                bullet_color=ACCENT_DARK,
            ),
        ],
    ]
    day_table = Table(day_rows, colWidths=[2.2 * inch, 2.2 * inch, 2.2 * inch])
    day_table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), INK),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("GRID", (0, 0), (-1, -1), 0.5, LINE),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 8),
                ("RIGHTPADDING", (0, 0), (-1, -1), 8),
                ("TOPPADDING", (0, 0), (-1, -1), 8),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
            ]
        )
    )
    story.extend([day_table, Spacer(1, 10)])

    story.extend(
        [
            box(
                "Thank-you email template",
                [
                    p(
                        "Thank you for the opportunity to interview for the Programmer IV position. I appreciated learning more about the team and especially enjoyed our discussion about [specific topic]. The role's mix of technical leadership, requirements facilitation, and application delivery aligns closely with my experience, and I would be excited to contribute to TCEQ's mission.",
                        "callout_body",
                    )
                ],
                background=SOFT,
            ),
            Spacer(1, 10),
            p("8. Back-Pocket Answers", "section"),
        ]
    )

    back_pocket_rows = [
        [p("Scenario", "table_header"), p("A strong short answer", "table_header")],
        [p("If you do not know a technology", "table"), p("I have not used that exact tool in production, but I would ramp quickly by reading the documentation, building a focused proof of concept, and validating my assumptions with someone who already supports it.", "table")],
        [p("If stakeholders disagree", "table"), p("I would restate the competing needs, test them against the documented requirements, make the trade-off visible, and escalate to the sponsor only if the team cannot resolve it directly.", "table")],
        [p("If asked why government work", "table"), p("I want to build software with a durable mission, clear accountability, and long-term public impact. Regulated work also fits how I like to engineer systems: carefully and transparently.", "table")],
        [p("If asked about support", "table"), p("Support matters because agency users care about outcomes, not implementation details. I try to explain issues in workflow terms and design systems that fail gracefully and are easy to troubleshoot.", "table")],
    ]
    story.extend(simple_table(back_pocket_rows, [1.8 * inch, 4.9 * inch]))

    story.extend(
        [
            box(
                "90-day plan answer",
                [
                    bullet_list(
                        [
                            "First 30 days: learn the application landscape, release process, security constraints, and key business contacts.",
                            "Days 31 to 60: start handling bounded enhancements, join JAD sessions, and identify a small process improvement opportunity.",
                            "Days 61 to 90: own a more complete work item end to end and make one concrete improvement to documentation, testing, or release quality.",
                        ],
                        style="callout_body",
                    )
                ],
                background=CALLOUT,
            ),
            Spacer(1, 8),
            box(
                "Final reminder",
                [
                    p(
                        "You do not need to sound flashy. You need to sound methodical, credible, and comfortable with regulated software delivery. Clear structure beats cleverness in this interview.",
                        "callout_body",
                    )
                ],
                background=PAPER,
                border=ACCENT,
            ),
        ]
    )

    return story


def draw_cover(canvas, doc):
    width, height = LETTER
    canvas.saveState()
    canvas.setFillColor(PAPER)
    canvas.rect(0, 0, width, height, fill=1, stroke=0)
    canvas.setFillColor(INK)
    canvas.rect(0, height - 0.78 * inch, width, 0.78 * inch, fill=1, stroke=0)
    canvas.setFillColor(ACCENT)
    canvas.rect(0, 0, width, 0.18 * inch, fill=1, stroke=0)
    canvas.restoreState()


def draw_page(canvas, doc):
    width, height = LETTER
    canvas.saveState()
    canvas.setFillColor(colors.white)
    canvas.rect(0, 0, width, height, fill=1, stroke=0)
    canvas.setStrokeColor(LINE)
    canvas.setLineWidth(0.7)
    canvas.line(doc.leftMargin, height - 0.55 * inch, width - doc.rightMargin, height - 0.55 * inch)
    canvas.setFont("Helvetica-Bold", 9)
    canvas.setFillColor(INK)
    canvas.drawString(doc.leftMargin, height - 0.42 * inch, "TCEQ Programmer IV Interview Packet")
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(MUTED)
    canvas.drawRightString(width - doc.rightMargin, 0.38 * inch, f"Page {doc.page}")
    canvas.restoreState()


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    doc = SimpleDocTemplate(
        str(OUTPUT_PATH),
        pagesize=LETTER,
        leftMargin=0.55 * inch,
        rightMargin=0.55 * inch,
        topMargin=0.72 * inch,
        bottomMargin=0.55 * inch,
        title="TCEQ Programmer IV Interview Packet",
        author="OpenAI Codex",
    )

    story = build_story()
    doc.build(story, onFirstPage=draw_cover, onLaterPages=draw_page)
    print(OUTPUT_PATH)


if __name__ == "__main__":
    main()
