# PHASES.md — Project Task & Collaboration Allocator

## 1. Purpose

This document divides the development of **Project Task & Collaboration Allocator** among **four team members**, organized by implementation phases.

The project is a Java desktop application for the OOPS micro-project. This file is the shared work plan for the team and the Antigravity coding agent. Read it together with `CONTEXT.md`.

### Locked technology stack

- Java 17 (confirm the installed JDK)
- Java Swing, using `JFrame` for main windows
- MySQL 8.x
- JDBC with MySQL Connector/J
- Eclipse-compatible project structure
- Git and GitHub for collaboration
- Java I/O for `.txt` project report export

Do not add web frameworks, Python, AI/ML, Firebase, cloud backends, or unrelated features without explicit team approval.

### Core rule

The application suggests a suitable team member based on skills and workload. **The Project Leader makes the final assignment decision.** The allocator must never silently assign a task.

---

## 2. Team Collaboration Rules

Four people are contributing to one application. Work is divided by phase and module, but shared contracts must be agreed before parallel implementation begins.

### Shared rules

1. All four members must read `CONTEXT.md` and this `PHASES.md`.
2. Use one shared GitHub repository and one agreed branch/merge process.
3. Agree on the database schema, model fields, method signatures, and package names before dividing dependent coding work.
4. Do not create separate competing versions of common classes such as `Task`, `User`, `DBConnection`, or `Project`.
5. One person owns each shared file. Other members propose changes through the agreed Git workflow.
6. Commit small, meaningful changes. Do not commit database passwords, real student data, generated build output, or IDE-specific secrets.
7. Each member must test their module and document how to run it.
8. Integrate frequently; do not wait until the final phase to combine everyone's work.
9. Use pull requests or review each other's changes before merging into the main branch.
10. If a shared contract must change, notify all affected members before changing code.
11. Each member should understand the overall system well enough to explain it during the viva.
12. Keep progress visible in GitHub issues or a simple project task board.

### Suggested role placeholders

Replace these placeholders with the actual names before work begins:

- **Member 1:** `[Name — Authentication & Student Skills]`
- **Member 2:** `[Name — Database & Project Membership]`
- **Member 3:** `[Name — Task Management & Progress]`
- **Member 4:** `[Name — Allocation Engine, Reporting & Integration]`

This allocation intentionally gives one member database foundation responsibilities, but database design and core models must be reviewed by all four before being locked. Every member should still contribute to design review and integration.

---

## 3. Phase Overview

| Phase | Name | Primary owner | Supporting members | Deliverable / exit condition |
|---|---|---|---|---|
| 0 | Requirements and contracts | All members; coordinator appointed by team | All | Approved scope, ER diagram, class diagram, shared interfaces |
| 1 | Project setup and database foundation | Member 2 | Members 1 and 4; all review | Project opens, schema runs, JDBC connection tested |
| 2 | Models and authentication | Member 1 | Member 2 for DB integration; Member 4 for review | Registration/login works with hashed passwords and role routing |
| 3 | Student skills and project membership | Member 2 | Member 1 for profile integration; Member 3 for project screens | Skills persist; leader can create a project and select eligible members |
| 4 | Task management | Member 3 | Member 2 for DAO/schema integration; Member 1 for UI consistency | Task CRUD, multi-skill requirements, status and deadline handling work |
| 5 | Allocation engine and assignment workflow | Member 4 | Members 2 and 3 for data access and task integration | Ranked recommendation works; leader confirms or overrides assignment |
| 6 | Progress dashboard and report export | Member 3 owns progress; Member 4 owns report export | Members 1 and 2; all test | Accurate progress/workload summaries and `.txt` report export |
| 7 | Integration, testing, and polish | All; Member 4 coordinates integration unless team chooses another coordinator | All | End-to-end tested application and resolved critical bugs |
| 8 | Demo, report, and viva preparation | All; tasks divided below | All | Reproducible demo, setup instructions, diagrams, and clear viva explanations |

**Important:** Phase ownership means the owner is accountable for coordinating and delivering that phase; it does not mean that person does all the work alone. Supporting members should review, test, and integrate. The exact names can be assigned by the team.

---

## 4. Phase 0 — Requirements and Shared Design

**Participants:** All four members  
**Goal:** Agree on the design before creating implementation files.

### Tasks

- Read `CONTEXT.md` and agree on project scope.
- Confirm Java/JDK version, MySQL version, Eclipse setup, and GitHub repository.
- Confirm user roles: Project Leader and Team Member; no Admin role.
- Confirm task statuses: `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`.
- Agree on whether role is stored as a database role value, Java enum, or model subclass approach. Do not mix competing approaches.
- Finalize ER diagram and SQL schema draft.
- Finalize class diagram and package structure.
- Agree on model fields, DAO method signatures, service methods, and exception strategy.
- Decide how current assignments and reassignment history will be stored.
- Decide the project progress calculation.
- Review and approve the proposed allocation scoring formula and tie-breaking rules.
- Create GitHub repository, README, issue list, and contribution rules.

### Deliverables

- `docs/ER_DIAGRAM` or equivalent exported diagram
- `docs/CLASS_DIAGRAM` or equivalent
- `sql/schema.sql` draft
- Agreed API/method contract, for example `docs/INTERFACES.md`
- GitHub issues divided by phase

### Exit criteria

No member begins dependent module coding until shared models, schema relationships, and method contracts are agreed. Small independent setup tasks may proceed in parallel.

---

## 5. Phase 1 — Repository, Project Setup, and Database Foundation

**Primary owner:** Member 2  
**Support:** Member 1 and Member 4; all review the schema.

### Tasks

- Create the Eclipse-compatible Java project and agreed package structure.
- Set up `.gitignore` and `README.md`.
- Add MySQL Connector/J using the agreed dependency approach.
- Finalize and create `sql/schema.sql`.
- Create the MySQL database and tables, foreign keys, unique constraints, and useful indexes.
- Implement the shared `DBConnection` utility.
- Keep credentials in environment variables or a local untracked config file.
- Add a placeholder example configuration without secrets.
- Test JDBC connection and a simple parameterized query.
- Confirm transactions can be used for multi-step writes.

### Deliverables

- Project compiles in Eclipse.
- SQL schema runs on a clean development database.
- Database connection instructions in README.
- Successful JDBC connection test.
- Agreed database setup/configuration instructions.

### Exit criteria

Every team member can clone the repository, configure their own local database credentials, run the schema, and launch the basic project.

### Boundary

Member 2 owns the initial schema and connection utility. Changes to shared tables or `DBConnection` must be reviewed and communicated to all affected members.

---

## 6. Phase 2 — Shared Models, Authentication, and Student Accounts

**Primary owner:** Member 1  
**Support:** Member 2 for database operations; Member 4 for review/testing.

### Tasks

- Implement agreed user model and role representation.
- Implement registration and login forms using `JFrame`.
- Implement authentication service and user DAO operations.
- Validate required fields and email format.
- Prevent duplicate email registration.
- Hash passwords using a suitable standard-Java method such as PBKDF2; never store plaintext passwords.
- Use `PreparedStatement` and try-with-resources.
- Route successful login to the correct role dashboard.
- Implement logout/navigation behavior.
- Add clear success and error messages.
- Enforce role permissions in service logic, not just by hiding buttons.

### Deliverables

- Registration works and persists users.
- Login accepts valid credentials and rejects invalid credentials.
- Passwords are stored as hashes, not plaintext.
- Correct role-specific dashboard opens.
- Duplicate email and invalid input cases are handled.

### Exit criteria

A newly registered student can log in, and role routing/permission checks work against the shared MySQL database.

### Boundary

Member 1 owns authentication screens and services. Coordinate changes to shared `User` model fields with all members before modifying them.

---

## 7. Phase 3 — Student Skills, Project Creation, and Membership

**Primary owner:** Member 2  
**Support:** Member 1 for profile/skills integration; Member 3 for project GUI integration.

### Tasks

- Implement the `Skill` and user-skill mapping model/DAO/service agreed in Phase 0.
- Allow a student to create and update their own skill levels, recommended scale 1–5.
- Prevent duplicate skill records for a student and skill.
- Implement project creation and editing.
- Allow the Project Leader to select/add project members.
- Display project members and their skills.
- Enforce the rule that a student may belong to only one active project at a time.
- Prevent members outside the project from being treated as eligible allocation candidates.
- Implement a safe way to leave or complete an active project so a student may join another later.
- Use transactions where membership changes require multiple database operations.

### Deliverables

- Skill profiles persist in MySQL.
- A leader can create and view a project.
- A leader can add eligible team members.
- Membership restrictions are enforced.
- Project member list and skill details can be retrieved for later modules.

### Exit criteria

Project and skill data can be retrieved by the task module and allocator using the agreed DAO/service interfaces.

### Boundary

Member 2 owns project membership and skills persistence. Member 3 owns task UI and task behavior; coordinate shared project/member model changes.

---

## 8. Phase 4 — Task Management

**Primary owner:** Member 3  
**Support:** Member 2 for schema/DAO integration; Member 1 for UI consistency.

### Tasks

- Implement the agreed `Task`, `TaskStatus`, and `TaskPriority` models.
- Create task screens with Swing `JFrame`/`JPanel` components.
- Allow the Project Leader to create, view, and edit tasks.
- Store task name, description, project, priority, estimated hours, deadline, status, and progress.
- Support multiple required skills per task using the task-skill relationship.
- Validate task name, estimated hours, deadline, priority, and required skills.
- Show tasks in `JTable` or another suitable Swing component.
- Allow Team Members to view only their assigned tasks.
- Allow Team Members to update permitted status/progress fields.
- Enforce valid status transitions and progress between 0 and 100.
- Keep status and progress consistent; completing a task sets progress to 100.
- Implement task DAO/service contracts required by the allocator.

### Deliverables

- Task create/read/update operations work with MySQL.
- Tasks can require multiple skills.
- Task lists are filtered by project or assigned member as appropriate.
- Validation and status/progress rules work.
- Allocator can retrieve task requirements and active assignments.

### Exit criteria

A leader can create a task with multiple required skills, and the task is available to the allocation module through agreed interfaces.

### Boundary

Member 3 owns task-management code. Member 4 should not duplicate task assignment persistence; use the shared DAO/service contract.

---

## 9. Phase 5 — Allocation Engine and Assignment Workflow

**Primary owner:** Member 4  
**Support:** Member 2 for member/skill/workload data; Member 3 for task integration; Member 1 for test accounts and role checks.

### Tasks

- Define `AllocationStrategy` and implement `SkillWorkloadStrategy`.
- Keep allocation logic independent of Swing and SQL.
- Retrieve only members of the task's project.
- Calculate skill match across all required skills.
- Treat missing skill data consistently and never silently score it as high proficiency.
- Calculate active-task count and assigned estimated work hours.
- Apply the approved workload availability calculation and score weights.
- Rank candidates and show a recommended member.
- Handle ties using agreed rules, such as fewer active tasks followed by fewer assigned hours.
- Display the suggested member, skill match, active task count, workload, and explanation.
- Let the Project Leader accept the suggestion or manually choose another eligible project member.
- Validate manual assignments in the service layer.
- Save the final assignment only after leader confirmation.
- Support reassignment and preserve history if the approved schema includes it.
- Test cases with different skills, workloads, missing skills, ties, and no eligible members.

### Deliverables

- Unit-testable allocation strategy.
- Candidate ranking with understandable scores.
- Swing allocation view/dialog.
- Accept-suggestion workflow.
- Manual override workflow.
- Database-persisted final assignment.
- No automatic assignment without leader confirmation.

### Formula decision

The formula must be approved in Phase 0 and documented. A proposed starting point is:

- `skillMatch = average(required-skill levels) / 5 * 100`
- `workloadAvailability = clamp((referenceCapacity - assignedActiveHours) / referenceCapacity * 100, 0, 100)`
- `finalScore = 0.70 * skillMatch + 0.30 * workloadAvailability`

Use active-task count as an additional consideration or tie-breaker. This formula is only a proposal until the team explicitly approves it. Ensure reference capacity is configurable or clearly documented. Avoid division by zero and negative availability scores.

### Exit criteria

For test projects, the system produces sensible, explainable recommendations and the leader can confirm or override them without violating membership rules.

---

## 10. Phase 6 — Progress Dashboard and Report Export

**Progress dashboard owner:** Member 3  
**Report export owner:** Member 4  
**Support:** Members 1 and 2 for authentication/data access; all members test.

### Member 3 — Progress and workload summaries

Tasks:

- Show total, pending, assigned, in-progress, and completed task counts.
- Calculate project completion using the method agreed in Phase 0.
- Display each member's active task count and estimated assigned hours.
- Display upcoming deadlines.
- Refresh summary data after task status or assignment changes.
- Use correct project/member scope and role permissions.
- Handle empty projects and missing deadlines gracefully.

Deliverables:

- Leader progress dashboard.
- Member task summary.
- Correct workload and project progress calculations.

### Member 4 — Text report export

Tasks:

- Implement `ReportService`.
- Retrieve report data from MySQL through DAOs/services.
- Export project name, members, tasks, assignments, priorities, deadlines, statuses, and overall progress.
- Use `BufferedWriter` or `Files.newBufferedWriter`.
- Use a file chooser or a clearly communicated save location.
- Handle file-system errors and empty reports.
- Never include password hashes or authentication secrets.

Deliverables:

- Export action in the GUI.
- Valid, readable `.txt` project report.
- Graceful error handling for failed writes.

### Exit criteria

Progress/workload numbers match database records, and the report exports accurate current data without sensitive authentication fields.

---

## 11. Phase 7 — Integration and Testing

**Primary coordinator:** Member 4, unless the team assigns a different coordinator  
**Participants:** All four members

### Tasks

- Merge modules in small, reviewed changes.
- Resolve class/package/method signature mismatches.
- Run the SQL schema on a clean database and test setup instructions.
- Verify DAO queries match the final schema.
- Test the end-to-end flow:
  1. Register users.
  2. Log in as a Project Leader.
  3. Create a project.
  4. Add team members.
  5. Enter member skills.
  6. Create a task with multiple required skills.
  7. Request an allocation recommendation.
  8. Accept or override the recommendation.
  9. Update task status/progress as a Team Member.
  10. Check dashboard/workload calculations.
  11. Export the project report.
- Test invalid inputs, duplicate email, unauthorized actions, database failures, empty states, and report write errors.
- Verify no task can be assigned to a member outside its project.
- Verify a member cannot join two active projects.
- Verify the leader is the only role that confirms/reassigns tasks.
- Check Swing EDT behavior and use `SwingWorker` for slow operations where needed.
- Remove debug-only credentials/data and verify `.gitignore`.
- Fix critical and high-priority bugs before visual polish.

### Deliverables

- Integrated working application.
- Test checklist with pass/fail results.
- Known-issues list for anything not completed.
- Updated README and database setup instructions.

### Exit criteria

The full core workflow passes on a clean setup, and no critical data-integrity or role-permission issue remains. Do not claim a test passed unless it was actually run.

---

## 12. Phase 8 — Final Demo, Documentation, and Viva

**Participants:** All four members

### Member 1 — Authentication and skills explanation

Prepare to explain:

- Registration/login flow
- Password hashing
- User roles and permission checks
- Skill profile storage
- Validation and exceptions

### Member 2 — Database and project membership explanation

Prepare to explain:

- ER diagram and foreign-key relationships
- JDBC connection
- DAO pattern
- Prepared statements and CRUD
- Project membership restriction and transactions

### Member 3 — Task and progress explanation

Prepare to explain:

- Task model and status lifecycle
- Multiple required skills
- Swing event handling and layout managers
- Progress and workload calculations
- Collections used in task processing

### Member 4 — Allocation and report explanation

Prepare to explain:

- `AllocationStrategy` interface
- Skill-match and workload formula
- Candidate ranking and tie-breaking
- Manual override and assignment persistence
- Java file handling and `.txt` export

### Shared final tasks

- Update README with JDK/MySQL/Connector setup and run instructions.
- Add ER and class diagrams.
- Prepare screenshots only from the actual application.
- Prepare a short, reliable demo scenario with sample data.
- Prepare a slide deck explaining the problem, solution, architecture, modules, schema, allocation algorithm, testing, and limitations.
- Ensure each member can explain the overall project, not just their assigned module.
- Clearly distinguish implemented features from future work.

### Exit criteria

The application can be launched from the documented setup and the team can demonstrate the core flow without relying on fake buttons or unimplemented placeholders.

---

## 13. Shared Interfaces to Agree Before Parallel Work

Before Phases 2–5 are developed in parallel, agree on these contracts:

- User identity and role representation
- How an authenticated session/current user is passed to a dashboard
- Model fields and constructors for `User`, `Skill`, `Project`, `Task`, and `Allocation`
- DAO methods for user, skill, project, task, and assignment persistence
- Service methods and expected exceptions
- Task status and priority values
- Representation of multiple task skills
- Representation of current assignments and assignment history
- Allocation candidate/result model
- Progress calculation method
- Database connection/configuration approach
- UI navigation and window-closing behavior

Create `docs/INTERFACES.md` to record agreed method signatures. Do not guess another member's API or duplicate their code.

---

## 14. Definition of Done

The project is ready for demonstration when:

- All core features described in `CONTEXT.md` work.
- The application uses Java Swing with `JFrame`, MySQL, and JDBC.
- Registration/login and role permissions work.
- Students can maintain skills.
- Project Leaders can create projects and choose eligible members.
- Leaders can create tasks with multiple required skills.
- The allocator generates explainable skill/workload-based recommendations.
- Leaders can accept or manually override suggestions.
- Assignments and progress persist in MySQL.
- Members can update their assigned tasks within their permissions.
- Progress and workload dashboards show accurate data.
- Project reports export to `.txt`.
- The database can be set up using the documented schema.
- The team has tested the core workflow and documented actual results.
- The code is understandable and each member can explain their part and the overall architecture.

---

## 15. Instructions for Antigravity

When Antigravity is used to implement the project:

1. Read both `CONTEXT.md` and `PHASES.md` first.
2. Inspect the current workspace and preserve existing work.
3. Start with Phase 0; do not generate the whole application in one step.
4. Present design decisions that need team approval before locking them.
5. Implement one phase/module at a time.
6. Do not edit another member's owned files without coordinating the change.
7. Compile and run relevant tests after each phase where possible.
8. Report files changed, commands/tests run, results, blockers, and next steps.
9. Never invent test results or claim unfinished features are complete.
10. Do not change the stack or project scope without explicit approval.
11. Keep the code appropriate for a second-year OOPS project and explain key concepts for viva preparation.

## One-Sentence Project Definition

Project Task & Collaboration Allocator is a Java Swing desktop application backed by MySQL and JDBC that helps college students manage project membership, skills, multi-skill tasks, rule-based skill-and-workload allocation recommendations, task progress, and project report export.
