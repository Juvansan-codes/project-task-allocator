# CONTEXT.md — Project Task & Collaboration Allocator

## 1. Purpose of This File

This file is the single source of truth for the AI coding agent working on this project in Antigravity. Read this file before planning, generating, modifying, or reviewing any code.

Follow the agreed scope and technology choices. Do not introduce additional frameworks or features without asking first. Build the project incrementally, keep the code understandable for a college OOP micro-project, and explain important implementation decisions.

---

## 2. Project Identity

- **Project title:** Project Task & Collaboration Allocator
- **Subject:** Object-Oriented Programming (OOPS)
- **Application type:** Java desktop application
- **Primary purpose:** Help college students organize project teams, define tasks, recommend task assignments based on skills and workload, and track progress.
- **Team size:** 3 students
- **Expected standard:** A working, demonstrable college micro-project whose code clearly demonstrates the concepts in the supplied Java syllabus.
- **Core principle:** The system recommends a suitable member; the Project Leader makes the final assignment decision.

### Problem Being Solved

Students often divide academic project work through informal conversations. This can cause unclear responsibilities, uneven workload, missed deadlines, and difficulty tracking progress. The application centralizes project membership, skills, tasks, suggested assignments, and progress.

---

## 3. Locked Technology Stack

Use this stack unless the user explicitly approves a change:

- **Language:** Java 17 (confirm the installed JDK before relying on Java 17-specific features)
- **GUI:** Java Swing
- **Main windows:** `JFrame`
- **Reusable GUI sections:** `JPanel`
- **Dialogs:** `JDialog` and `JOptionPane` where appropriate
- **Database:** MySQL 8.x
- **Database connectivity:** JDBC
- **Driver:** MySQL Connector/J
- **IDE:** Eclipse IDE; Antigravity may be used as the coding assistant/editor
- **Version control:** Git and GitHub
- **Build approach:** Start with a straightforward Eclipse-compatible Java project. Do not introduce Maven or Gradle unless the project setup or team explicitly requires it.
- **File export:** Standard Java I/O, such as `BufferedWriter` or `Files.newBufferedWriter`, to export a `.txt` project report.

### Explicitly Do Not Introduce

Do not convert this into a web, mobile, or cloud application. Do not introduce:

- Python or a Python backend
- JavaScript, React, Next.js, or web frameworks
- Firebase or cloud databases
- AI APIs, machine learning, or claims that the allocator uses AI
- Spring Boot or other unnecessary application frameworks
- A separate REST API/backend server
- Chat, video calling, file sharing, email notifications, or real-time collaboration
- Socket programming unless the user/faculty explicitly requires it for this project

Avoid unnecessary third-party libraries. Prefer the Java standard library, Swing, JDBC, and MySQL Connector/J.

---

## 4. GUI Rules: JFrame and Swing

Use `JFrame` as the main window class for the application's major screens. Use Swing components as appropriate:

- `JFrame`, `JPanel`, `JDialog`
- `JButton`, `JLabel`
- `JTextField`, `JPasswordField`, `JTextArea`
- `JComboBox`, `JCheckBox`, `JRadioButton`
- `JTable`, `JScrollPane`
- `JOptionPane`
- `ActionListener` and other relevant listeners
- Layout managers such as `BorderLayout`, `GridLayout`, `GridBagLayout`, and `BoxLayout`

### GUI implementation principles

1. Do not put SQL queries or the allocation algorithm directly in a `JFrame`.
2. Keep each window focused on presentation and user interaction.
3. Use service classes for business rules and DAO classes for database operations.
4. Launch and update Swing UI on the Event Dispatch Thread (EDT).
5. Do not run slow database or file operations on the EDT if they can make the UI freeze. Use `SwingWorker` when appropriate.
6. Use layout managers rather than absolute positioning.
7. Keep the interface consistent, clean, readable, and suitable for a college demonstration.
8. Use a mostly white/light interface with dark navy or blue elements and restrained orange accents. Avoid flashy gradients and unnecessary animation.
9. Use `JOptionPane` or custom dialogs for clear success/error/confirmation messages.
10. Use sensible window sizing and `setLocationRelativeTo(null)` for centering.
11. Dispose of or hide obsolete windows appropriately when navigating. Avoid opening duplicate windows accidentally.
12. Never treat client-side GUI checks as a replacement for validation in the service/database layers.

### Suggested primary screens

- `LoginFrame`
- `RegisterFrame`
- `LeaderDashboardFrame`
- `MemberDashboardFrame`
- `ProjectFrame` or a project-management window
- `TaskFrame` or a task-management window
- `AllocationDialog` or `AllocationFrame`
- `ProgressFrame`

These names are suggestions; keep naming consistent once implementation starts. Do not create every screen as an empty placeholder. Implement screens incrementally with working actions.

---

## 5. User Roles and Permissions

There are exactly two application roles:

### Project Leader

A student who creates/manages a project. The Project Leader can:

- Register and log in
- Maintain their own profile and skill information
- Create and edit a project
- Select the project members
- View project-member skill profiles
- Create and edit tasks
- Define task requirements, including multiple required skills
- Set task priority, deadline, and estimated hours
- Request an allocation recommendation
- Accept the recommendation or manually choose another eligible member
- Reassign tasks
- Monitor project progress and workload
- Export a project report as a `.txt` file

### Team Member

A student participating in a project. A Team Member can:

- Register and log in
- Maintain their own profile and skill levels
- View their current project and its information
- View assigned tasks and deadlines
- Update the status/progress of tasks assigned to them
- View relevant project progress

Team Members must not manage the entire project, select project membership, or assign tasks unless the agreed requirements are changed.

### Authentication Rules

- There is **no Admin role**.
- Students register themselves.
- Registration creates the student's account/profile.
- Students define their own skill levels.
- Login identifies the authenticated user and their current role/context.
- Enforce permissions in service logic as well as by hiding/disabling unauthorized GUI actions.

### Password and Session Security

- Never store plaintext passwords.
- Use a suitable password-hashing approach. Prefer PBKDF2 using Java's standard cryptography APIs to avoid unnecessary dependencies.
- Use prepared statements for all SQL statements involving user input.
- Do not hard-code real database credentials into committed source files. Use a local, uncommitted configuration file or environment variables, and provide a safe example configuration with placeholders.
- Do not print passwords or sensitive credentials to logs.
- Handle login failures with a generic, user-friendly message.
- Do not add complex security infrastructure beyond what is appropriate for this educational desktop project.

---

## 6. Core Functional Modules

### Module 1 — Authentication

- Student registration
- Login and logout/navigation back to login
- Input validation
- Role-aware dashboard routing
- Safe password hashing and verification

### Module 2 — Student Profile and Skills

- View/edit the student's own profile
- Add/update skill levels
- Represent a skill level on a consistent scale, recommended 1–5
- View skills of members in the current project
- Prevent duplicate skill entries for the same student and skill

### Module 3 — Project Management

- Create and edit project information
- View project details
- Select/add project members
- View current project membership
- Restrict allocation candidates to the current project's members

**Project membership rule:** A student may participate in only one active project at a time. Enforce this rule in service logic and, where feasible, with suitable database constraints/transactions. A student may join another project after leaving or completing the current active project.

### Module 4 — Task Management

Each task may include:

- Task ID
- Project ID
- Task name
- Description
- Priority
- Estimated work hours
- Deadline
- Status
- Progress percentage
- One or more required skills
- Current assigned member, if assigned

Project Leaders can create, view, edit, and assign/reassign tasks. Team Members can view tasks assigned to them and update their permitted status/progress fields.

Use a consistent set of statuses, initially:

- `PENDING`
- `ASSIGNED`
- `IN_PROGRESS`
- `COMPLETED`

Prefer a Java `enum` for statuses and priorities if it improves type safety without complicating the code. Validate progress to stay between 0 and 100. Keep status and progress consistent; for example, marking a task completed should set progress to 100.

### Module 5 — Task Allocation (Core Feature)

The allocator is **rule-based Java logic, not AI or machine learning**.

When a leader asks for a recommendation:

1. Retrieve the eligible members of the task's project only.
2. Retrieve each member's skill levels.
3. Compare member skills with all required task skills.
4. Calculate current active-task count.
5. Calculate current assigned estimated work hours from active tasks.
6. Calculate an understandable score for each eligible member.
7. Rank candidates and show the recommended member plus a brief explanation.
8. Let the Project Leader accept the suggestion or manually select another eligible member.
9. Persist the final assignment only after the leader confirms it.

The allocator must not silently assign or reassign tasks. The Project Leader retains final control.

#### Allocation formula status

The exact formula has not been finalized. Treat any formula below as a **proposed initial design**, not as an unchangeable requirement:

- Skill match percentage: average of the member's levels for all required skills divided by 5, multiplied by 100.
- Workload availability: compare active assigned hours with a configurable reference capacity; clamp the result between 0 and 100.
- A possible combined score is `0.70 * skillMatch + 0.30 * workloadAvailability`.
- Use active-task count as an additional consideration or tie-breaker.
- Handle missing required skills explicitly and consistently. Do not silently treat missing skill data as high proficiency.
- If multiple candidates tie, use a deterministic tie-breaker such as fewer active tasks, then fewer assigned hours.
- Keep the formula isolated in `AllocationStrategy`/`SkillWorkloadStrategy` so it can be adjusted without changing GUI or database code.
- Show the leader enough information to understand the recommendation, including skill match and workload.

Before finalizing the formula, document the chosen behavior and test representative scenarios.

### Module 6 — Progress Tracking

- Show tasks grouped by status
- Show completion percentage
- Show member task counts and assigned hours
- Show upcoming deadlines
- Show project-level progress
- Avoid misleading percentages; document how project progress is calculated (for example, based on completed task count or estimated-hour weighting).

### Module 7 — Project Report Export

- Generate a `.txt` report from data stored in MySQL.
- Include project name, team members, tasks, assigned members, priorities, deadlines, statuses, and overall progress.
- Use Java file handling (`BufferedWriter` or `Files.newBufferedWriter`).
- Ask the user where to save the report or use a clearly displayed, sensible default.
- Handle file errors and report generation failures gracefully.
- Do not export passwords or other sensitive authentication data.

---

## 7. Required OOP and Java Syllabus Alignment

The application should naturally demonstrate the syllabus concepts below. Do not add artificial features solely to tick boxes.

### Basic programming constructs

- Variables, data types, operators
- `if`/`else`, loops, and `switch`
- Methods, strings, arrays where suitable
- Input validation

### Object-oriented features

- Classes and objects
- Encapsulation using private fields and appropriate methods
- Inheritance where it represents a meaningful relationship, such as an abstract `User` superclass with `ProjectLeader` and `TeamMember` subclasses if the design supports it
- Abstraction
- Interfaces, especially an `AllocationStrategy` interface
- Method overriding and polymorphism where appropriate
- Packages
- Appropriate use of `static` and `final`

Do not force an inheritance hierarchy if it makes role changes or persistence unnecessarily complicated. A shared `User` model with a role enum is also a valid alternative; select one approach and keep it consistent.

### Exception handling and optimized application development

- Handle SQL, validation, parsing, and file errors.
- Use meaningful custom exceptions where they improve clarity, such as `ValidationException` or `InvalidTaskException`.
- Use `SwingWorker` for long-running UI operations where appropriate.
- Do not add synchronization unless there is a real shared-state concurrency problem.
- Avoid empty catch blocks and avoid showing raw stack traces to end users. Log useful debugging details separately.

### Collections framework

Use suitable collections such as:

- `ArrayList<Task>`
- `ArrayList<TeamMember>` or an appropriate member model
- `HashMap<Integer, ...>` for lookups and workload summaries
- `HashSet<Integer>` or `HashSet<String>` for uniqueness
- Generics
- Lambda expressions for natural filtering/sorting
- `enum` for fixed statuses/priorities where appropriate

The database remains the persistent source of truth. In-memory collections are used to process loaded data, not as a replacement for MySQL.

### File handling

- Create and write project report text files.
- Handle encoding and I/O errors correctly.

### GUI design and JDBC

- Swing components and `JFrame`
- Event listeners and event handling
- Layout managers
- JDBC connection, `PreparedStatement`, and `ResultSet`
- CRUD operations with proper resource cleanup

The syllabus also lists networking/socket programming. Do not add sockets to the main project unless the user or faculty explicitly requires them.

---

## 8. Proposed Package Structure

Use this structure as a starting point. Adjust only if there is a clear, documented reason:

```text
src/
└── com/taskallocator/
    ├── Main.java
    ├── model/
    │   ├── User.java
    │   ├── ProjectLeader.java
    │   ├── TeamMember.java
    │   ├── Skill.java
    │   ├── Project.java
    │   ├── Task.java
    │   ├── TaskStatus.java
    │   ├── TaskPriority.java
    │   └── Allocation.java
    ├── dao/
    │   ├── UserDAO.java
    │   ├── SkillDAO.java
    │   ├── ProjectDAO.java
    │   ├── TaskDAO.java
    │   └── AllocationDAO.java
    ├── service/
    │   ├── AuthService.java
    │   ├── SkillService.java
    │   ├── ProjectService.java
    │   ├── TaskService.java
    │   ├── AllocationService.java
    │   ├── ProgressService.java
    │   └── ReportService.java
    ├── strategy/
    │   ├── AllocationStrategy.java
    │   └── SkillWorkloadStrategy.java
    ├── gui/
    │   ├── LoginFrame.java
    │   ├── RegisterFrame.java
    │   ├── LeaderDashboardFrame.java
    │   ├── MemberDashboardFrame.java
    │   ├── ProjectFrame.java
    │   ├── TaskFrame.java
    │   ├── AllocationDialog.java
    │   └── ProgressFrame.java
    ├── exception/
    │   ├── ValidationException.java
    │   └── InvalidTaskException.java
    └── util/
        ├── DBConnection.java
        └── PasswordUtil.java

sql/
└── schema.sql

README.md
CONTEXT.md
```

Do not create all classes as empty stubs. Implement and test one vertical slice at a time.

---

## 9. Preliminary Database Design

The following table list is a proposal to refine before implementation, not a final schema. Produce an ER diagram or written relationships first, then finalize keys, constraints, indexes, and deletion/update rules.

Potential tables:

1. `users`
2. `skills`
3. `user_skills`
4. `projects`
5. `project_members`
6. `tasks`
7. `task_skills`
8. `task_assignments`

Key relationship expectations:

- One user can have multiple skill entries.
- One project has one leader and multiple members.
- A user can be a member of only one active project at a time.
- One project has multiple tasks.
- One task can require multiple skills.
- A task can have one current assignee, but assignments may need history for reassignments.
- Only eligible project members can be assigned to tasks in that project.
- Foreign keys should protect referential integrity.
- Use unique constraints to prevent duplicate skill entries or duplicate membership rows where applicable.
- Use transactions for operations that update related records together.
- Use sensible indexes on foreign keys and commonly queried columns.

Before writing `schema.sql`, decide whether `task_assignments` represents current assignments only or assignment history. Prefer a design that supports reassignment history without making the micro-project too complex.

---

## 10. Architecture and Code Quality Rules

Use a straightforward layered design:

```text
Swing JFrame / Dialog
        ↓
Service Layer (validation and business rules)
        ↓
DAO Layer (SQL and persistence)
        ↓
JDBC
        ↓
MySQL
```

Rules:

1. GUI classes must not contain raw SQL.
2. DAO classes must not show Swing dialogs.
3. Services should not depend on specific Swing windows.
4. Use prepared statements for user-supplied values.
5. Use try-with-resources for JDBC resources.
6. Do not concatenate user input into SQL.
7. Use transactions for multi-step operations that must succeed or fail together.
8. Validate all important data in the service layer.
9. Do not store application state only in static global variables.
10. Avoid overengineering and unnecessary design patterns.
11. Add comments to explain important decisions, not every obvious line.
12. Keep names consistent and code suitable for a student to explain in a viva.
13. Avoid adding dependencies unless needed and approved.
14. Do not invent completed features or claim that untested code works.
15. Do not overwrite working code or remove features without checking their use and explaining the change.
16. Never commit credentials, database passwords, or personal data.

---

## 11. Implementation Workflow for Antigravity

Do not attempt to generate the entire application in one enormous step.

Follow this sequence:

### Phase 1 — Inspect and plan

1. Read `CONTEXT.md` fully.
2. Inspect the current workspace before creating or overwriting files.
3. Report the existing files, JDK availability if detectable, and current project state.
4. Identify any decisions that remain unresolved.
5. Propose the ER diagram, schema, and class responsibilities.
6. Do not change the agreed technology stack.

### Phase 2 — Finalize database and foundation

1. Finalize the ER diagram and table definitions.
2. Create `sql/schema.sql`.
3. Create the package structure.
4. Add the MySQL Connector/J dependency in the simplest compatible way.
5. Implement `DBConnection`.
6. Test a real JDBC connection and basic database query.
7. Keep credentials in local configuration/environment variables and provide a placeholder example.

### Phase 3 — Build functional modules incrementally

Implement each module in this order, testing after each:

1. Models and enums
2. Registration/login and password hashing
3. Student profile and skills
4. Project creation and membership
5. Task CRUD and required skills
6. Allocation scoring and leader confirmation
7. Task progress and project dashboard
8. Text report export
9. Final UI consistency and navigation

For each module:

- List files to create or modify.
- Explain the implementation briefly.
- Implement complete, usable behavior rather than placeholders.
- Compile the project and fix errors.
- Test normal cases, invalid inputs, and database failure cases where practical.
- Update the README with setup/run instructions.
- Do not proceed past a blocking failure without reporting it.

### Phase 4 — Integration and final verification

1. Integrate all three team members' modules.
2. Check the database schema against every DAO query.
3. Test registration, login, role permissions, and logout/navigation.
4. Test project membership restrictions.
5. Test task creation, editing, status updates, and assignment.
6. Test the allocator with different skill and workload combinations.
7. Confirm manual override works and that no task is silently assigned.
8. Test report export.
9. Check invalid input, SQL errors, duplicate data, and empty states.
10. Verify the application can be opened from Eclipse using the documented instructions.
11. Prepare a demo workflow and viva explanations.

If the project already contains code, preserve and inspect it before making changes. Do not blindly regenerate the repository.

---

## 12. Testing Requirements

At minimum, test:

- Empty or invalid registration fields
- Duplicate email registration
- Correct and incorrect login
- Role-specific access
- Adding, editing, and viewing skills
- Attempting to join a second active project
- Creating a project and adding eligible members
- Creating a task with one or multiple required skills
- Invalid estimated hours, deadline, or progress
- Allocation with exact skill matches
- Allocation with missing skill data
- Allocation when a member has high workload
- Tied allocation scores
- Manual assignment to a valid member
- Attempting to assign a member outside the project
- Task reassignment
- Status/progress consistency
- Empty project/task lists
- Database unavailable or invalid credentials
- Successful and failed report export

Do not report tests as passed unless they were actually executed.

---

## 13. Definition of Done

The project is ready for its final demonstration when:

- The application starts from the documented Eclipse setup.
- Students can register and log in.
- User roles and permissions are respected.
- Students can manage their own skills.
- A leader can create a project and select eligible members.
- A leader can create tasks with multiple required skills.
- The allocator generates an explainable recommendation using skill match and workload.
- The leader can accept the suggestion or manually choose an eligible member.
- Assignments and progress persist in MySQL.
- Members can update their assigned tasks within their permissions.
- Project/task summaries display correct information.
- A project report can be exported to `.txt`.
- Errors are handled without crashing the application.
- The team can explain the OOP, collections, exception-handling, Swing, file-handling, and JDBC concepts used.
- Setup instructions and the SQL schema are included in the repository.

---

## 14. How the Agent Should Respond and Work

When working on this project:

- Be practical, accurate, and incremental.
- Prioritize working software over decorative mockups.
- Ask before making a major scope or technology change.
- If a requirement is ambiguous, explain the ambiguity and propose the simplest suitable choice.
- Distinguish proposed design decisions from already implemented features.
- Explain important code in a way a second-year computer science student can understand and defend in a viva.
- Prefer simple, reliable implementations over clever abstractions.
- Do not claim the project is complete until the acceptance criteria have been tested.
- At the end of each phase, summarize files changed, tests performed, issues remaining, and the next recommended step.

## 15. One-Sentence Definition

Project Task & Collaboration Allocator is a Java Swing desktop application backed by MySQL and JDBC that helps college students create projects, define skills, manage multi-skill tasks, receive rule-based skill-and-workload allocation recommendations, manually confirm assignments, track progress, and export a project summary.
