# MEMBER_2_WORK.md — Database Foundation & Project Membership

**Primary Owner:** Member 2  
**Role Title:** Database Architect & Project Membership Lead  
**Module Focus:** Relational Database, JDBC Transactions, Project Lifecycle & Membership Governance  
**Subject:** Object-Oriented Programming (OOPS) — 2nd Year Micro-Project  

---

## 1. Executive Summary & Module Mission

Member 2 is responsible for:
1. **The Shared Database Foundation:** Maintaining schema integrity, foreign keys, indexes, and the JDBC connection utility (`DBConnection.java`).
2. **Project Lifecycle Governance:** Enabling Project Leaders to create, update, and complete projects (`Project`, `ProjectStatus`).
3. **Academic Membership Enforcement:** Ensuring that a student can only belong to **one active project at a time** (preventing split commitments across teams).
4. **Transactional Data Integrity:** Demonstrating robust ACID database transactions with manual `commit()` and `rollback()` handling.

---

## 2. Completed Work (Phase 1 Baseline)

Member 2 has already established the project foundation:

- [x] **Relational Schema (`sql/schema.sql`):** Created 8 normalized tables with foreign keys, checks, and performance indexes.
- [x] **MySQL Connector/J Integration:** Configured `lib/mysql-connector-j-8.3.0.jar` and Eclipse `.classpath`.
- [x] **Connection Utility (`src/com/taskallocator/util/DBConnection.java`):** Implemented configuration reader, driver registration, and safe resource closure.
- [x] **Local Credential Protection:** Created `config/db.properties.example` and secured `.gitignore` to prevent secret leaks.
- [x] **Connectivity Verification (`src/com/taskallocator/TestDatabaseConnection.java`):** Verified live connection to MySQL 8.x and transaction rollback.

---

## 3. Scope of Work for Phase 3 (Upcoming Implementation)

Member 2's primary development work in Phase 3 focuses on **Project Creation and Team Recruitment**:

```text
               ┌───────────────────────────────┐
               │    ProjectLeader (User)       │
               └──────────────┬────────────────┘
                              │ 1. Creates Project
                              ▼
               ┌───────────────────────────────┐
               │       projects table          │
               │  status: PLANNING | ACTIVE    │
               └──────────────┬────────────────┘
                              │ 2. Enrolls Members
                              ▼
               ┌───────────────────────────────┐
               │    project_members table      │
               │  (Single Active Project Rule) │
               └───────────────────────────────┘
```

### Core Business Rules to Enforce:
1. **Single Active Project Rule:** A student cannot join or lead an active project if they already belong to another project in `PLANNING` or `ACTIVE` status.
2. **Automatic Leader Enrollment:** When a leader creates a project, the leader must automatically be added as an active member in `project_members`.
3. **Authorized Recruitment:** Only the verified Project Leader of that specific project may recruit new members.
4. **Recruitable Candidate Filtering:** When selecting members, the system must only display registered students who are **not currently enrolled** in an active project.
5. **Project Completion:** When a project status is changed to `COMPLETED`, its members are freed and may subsequently join or create another project.

---

## 4. Class & File Blueprint for Member 2

Member 2 will implement the following classes in `src/com/taskallocator/`:

### A. Models & Enums (`com.taskallocator.model`)

1. **`ProjectStatus.java` (Enum):**
   - Values: `PLANNING`, `ACTIVE`, `COMPLETED`, `ARCHIVED`
   - Methods: `getDisplayName()`, `fromDbValue(String val)`, `isActive()`

2. **`Project.java` (Class — Extends `BaseEntity`):**
   - Encapsulated fields:
     - `int id` (inherited from `BaseEntity`)
     - `String title`
     - `String description`
     - `int leaderId`
     - `String leaderName` (convenience transient field for display)
     - `ProjectStatus status`
     - `java.sql.Date deadline`
     - `java.sql.Timestamp createdAt` (inherited from `BaseEntity`)
   - Getters, setters, domain validation, and formatted `toString()`.

3. **`ProjectMember.java` (Class — Extends `BaseEntity`):**
   - Encapsulated fields:
     - `int projectId`
     - `int userId`
     - `String userFullName`
     - `String userEmail`
     - `String status` (`ACTIVE`, `LEFT`, `COMPLETED`)
     - `java.sql.Timestamp joinedAt`

---

### B. Data Access Objects (`com.taskallocator.dao`)

1. **`ProjectDAO.java`:**
   - `Project insert(Connection conn, Project project)`: Inserts project within an external transaction and returns generated ID.
   - `Project findById(int projectId)`: Retrieves project details by ID.
   - `Project findActiveProjectByUserId(int userId)`: Finds the current active project a student belongs to (as leader or member).
   - `List<Project> findProjectsByLeader(int leaderId)`: Retrieves all projects led by a user.
   - `void update(Project project)`: Updates title, description, deadline.
   - `void updateStatus(int projectId, ProjectStatus status)`: Changes project lifecycle status.

2. **`ProjectMemberDAO.java`:**
   - `void addMember(Connection conn, int projectId, int userId)`: Adds member within a transaction.
   - `boolean isUserInActiveProject(int userId)`: Returns `true` if student is currently in any `ACTIVE` or `PLANNING` project.
   - `List<ProjectMember> getProjectMembers(int projectId)`: Retrieves all active members of a project with student names and emails.
   - `List<User> getRecruitableStudents(int projectId)`: Retrieves all registered students who are NOT in any active project.
   - `void removeMember(int projectId, int userId)`: Sets member status to `LEFT`.

---

### C. Business Logic & Transactions (`com.taskallocator.service`)

**`ProjectService.java`:**
- `Project createProject(String title, String description, int leaderId, java.sql.Date deadline)`:
  - Validates title not blank, deadline is in the future.
  - Verifies that `leaderId` does NOT already have an active project.
  - Executes inside a JDBC transaction:
    ```java
    Connection conn = DBConnection.getConnection();
    conn.setAutoCommit(false);
    try {
        Project project = projectDAO.insert(conn, newProject);
        projectMemberDAO.addMember(conn, project.getId(), leaderId);
        conn.commit();
        return project;
    } catch (Exception e) {
        conn.rollback();
        throw new DatabaseException("Failed to create project", e);
    } finally {
        conn.setAutoCommit(true);
        conn.close();
    }
    ```
- `void recruitMember(int projectId, int studentUserId, int leaderUserId)`:
  - Validates requesting user is indeed the project's leader.
  - Validates `studentUserId` is not already in an active project.
  - Enrolls student into `project_members`.
- `void completeProject(int projectId, int leaderUserId)`:
  - Verifies leader identity and marks project as `COMPLETED`.

---

### D. Swing GUI Components (`com.taskallocator.gui`)

1. **`ProjectOverviewPanel.java` (Reusable `JPanel`):**
   - Displays project title, status badge, deadline, and member count.
   - Centered inside the Project Leader workspace.
2. **`CreateProjectDialog.java` (`JDialog`):**
   - Modal dialog with title, description, and deadline inputs.
   - Validates inputs and creates project using `SwingWorker`.
3. **`ManageMembersDialog.java` (`JDialog`):**
   - Displays current team members in a `JTable`.
   - "Recruit Member" dropdown listing only recruitable students.
   - "Remove Member" action button.

---

## 5. Automated Verification Plan (`TestProjectMembership.java`)

Member 2 must write and run an automated test suite verifying:

| # | Test Scenario | Expected Outcome |
|---|---|---|
| **1** | Create project with valid title & deadline | Project persisted, generated ID > 0, status is `ACTIVE`. |
| **2** | Auto-enrollment of leader | Leader is present in `project_members` for the new project. |
| **3** | Leader attempts second active project | Rejected with `ValidationException("Leader already has an active project")`. |
| **4** | Recruit eligible student | Member added successfully to `project_members`. |
| **5** | Recruit student already in another active project | Rejected with `ValidationException("Student already belongs to an active project")`. |
| **6** | Filter recruitable students | Recruited student no longer appears in recruitable list. |
| **7** | Mark project as `COMPLETED` | Project status becomes `COMPLETED`; members are now free to join new projects. |
| **8** | Transaction rollback on failure | Partial data is rolled back cleanly if membership insert fails. |

---

## 6. Java OOPS Syllabus Defense Matrix (Viva Guide for Member 2)

During the faculty viva, Member 2 should be prepared to defend these specific concepts:

| OOPS Syllabus Topic | Implementation in Member 2's Code |
|---|---|
| **JDBC Transactions (ACID)** | `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()` ensuring atomic creation of a project and its leader membership. |
| **Prepared Statements & SQL Injection Defense** | Parameterized queries with `PreparedStatement` in `ProjectDAO` and `ProjectMemberDAO`. |
| **Referential Integrity & Constraints** | `ON DELETE RESTRICT` on `leader_id` prevents deleting a leader while projects exist; `ON DELETE CASCADE` removes member links if a project is purged. |
| **Inheritance & Polymorphism** | `Project` and `ProjectMember` extend abstract `BaseEntity`, inheriting common identity (`id`) and audit timestamps (`createdAt`). |
| **Collections Framework** | `ArrayList<ProjectMember>` for table display, `HashMap` for membership lookups, and Lambda streams for filtering recruitable students. |
| **Swing Layout Management** | `BorderLayout` and `GridBagLayout` in `ManageMembersDialog` ensuring clean presentation without absolute positioning. |

---

## 7. Next Steps for Implementation

1. Create `ProjectStatus.java`, `Project.java`, and `ProjectMember.java` in `com.taskallocator.model`.
2. Implement `ProjectDAO.java` and `ProjectMemberDAO.java` in `com.taskallocator.dao`.
3. Implement `ProjectService.java` with transaction boundaries in `com.taskallocator.service`.
4. Implement `TestProjectMembership.java` and verify all tests pass against local MySQL.
5. Build the Swing GUI dialogs (`CreateProjectDialog`, `ManageMembersDialog`) in `com.taskallocator.gui`.
