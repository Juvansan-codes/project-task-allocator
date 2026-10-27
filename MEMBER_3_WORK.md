# MEMBER_3_WORK.md — Task Management & Progress Tracking

**Primary Owner:** Member 3  
**Role Title:** Task Lifecycle & Progress Analytics Lead  
**Module Focus:** Multi-Skill Task Management, Status Lifecycle, Team Member Task Workspace & Progress Calculations  
**Subject:** Object-Oriented Programming (OOPS) — 2nd Year Micro-Project  

---

## 1. Executive Summary & Module Mission

Member 3 is responsible for:
1. **Multi-Skill Task Definition:** Enabling Project Leaders to create and configure tasks specifying title, description, priority, estimated hours, strict deadlines, and one or more required skills with expected proficiency levels (1–5).
2. **Task Lifecycle Management:** Governing safe state transitions (`PENDING` -> `ASSIGNED` -> `IN_PROGRESS` -> `COMPLETED`) and ensuring progress (0–100%) remains consistent with task status.
3. **Team Member Task Workspace:** Providing team members with a focused view to track their assigned tasks and update permitted status and progress fields.
4. **Project Progress & Workload Analytics:** Calculating accurate overall project progress using work-hour weighted formulas and aggregating member workloads for the allocation engine.

---

## 2. Alignment with Database Schema

Member 3's code interacts directly with the normalized schema created in Phase 1:

- **`tasks` Table:** Stores task metadata, priority, estimated hours, deadline, status, progress percentage, and the assigned user foreign key.
- **`task_skills` Table:** Join table supporting the many-to-many relationship between tasks and required skills, storing `min_level_required` (1–5).
- **`skills` Table:** Read-only access to master technical skills catalog.

---

## 3. Core Business Rules to Enforce

1. **Multi-Skill Requirements:** A task must require at least one skill. A single task can require multiple distinct skills (e.g., both "Java Programming" and "MySQL & Relational DB").
2. **Input Validation:**
   - Title: 2 to 150 characters, non-blank.
   - Estimated Hours: Strictly greater than 0 (`estimated_hours > 0`).
   - Deadline: Must be a valid date on or before the parent project's deadline.
   - Progress: Strictly between 0 and 100.
3. **Status & Progress Consistency:**
   - If progress reaches 100%, the status must automatically become `COMPLETED`.
   - If marked `COMPLETED`, progress must be set to 100%.
   - If marked `IN_PROGRESS`, progress must be between 1% and 99% (defaults to 10% if previously 0%).
   - A `PENDING` task has no assigned user; an `ASSIGNED` or `IN_PROGRESS` task must have an assigned member.
4. **Role Permission Boundaries:**
   - **Project Leader:** Can create, edit, delete, and reassign all tasks in their project.
   - **Team Member:** Can view only tasks assigned to them, and can only update progress percentage and task status (`ASSIGNED` -> `IN_PROGRESS` -> `COMPLETED`). Members cannot delete tasks or reassign them to others.
5. **Weighted Progress Calculation Formula:**
   $$\text{WeightedProgress\%} = \frac{\sum (\text{TaskProgress\%} \times \text{EstimatedHours})}{\sum \text{EstimatedHours}}$$
   *(Prevents a 1-hour minor task from distorting progress when a 25-hour critical task is incomplete).*

---

## 4. Class & File Blueprint for Member 3

Member 3 will implement the following classes in `src/com/taskallocator/`:

### A. Models & Enums (`com.taskallocator.model`)

1. **`TaskStatus.java` (Enum):**
   - Values: `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`
   - Methods: `getDisplayName()`, `fromDbValue(String val)`, `isFinished()`

2. **`TaskPriority.java` (Enum):**
   - Values: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
   - Methods: `getDisplayName()`, `fromDbValue(String val)`, `getColor()`

3. **`TaskSkillRequirement.java` (Class):**
   - Encapsulates:
     - `int skillId`
     - `String skillName`
     - `int minLevelRequired` (1 to 5)
   - Validation in setter ensuring `minLevelRequired` is between 1 and 5.

4. **`Task.java` (Class — Extends `BaseEntity`):**
   - Encapsulated fields:
     - `int id` (inherited from `BaseEntity`)
     - `int projectId`
     - `String title`
     - `String description`
     - `TaskPriority priority`
     - `int estimatedHours`
     - `java.sql.Date deadline`
     - `TaskStatus status`
     - `int progressPercent`
     - `Integer assignedUserId` (null if unassigned)
     - `String assignedUserName` (transient display helper)
     - `List<TaskSkillRequirement> requiredSkills`
     - `java.sql.Timestamp createdAt` (inherited from `BaseEntity`)
   - Domain methods: `isAssigned()`, `isCompleted()`, `addRequiredSkill(...)`.

5. **`ProjectProgressSummary.java` (DTO):**
   - Holds aggregated metrics:
     - `int totalTasks`, `int completedTasks`, `int inProgressTasks`, `int pendingTasks`
     - `int totalEstimatedHours`, `int completedEstimatedHours`
     - `double taskCountProgressPercent`
     - `double weightedProgressPercent`
     - `Map<Integer, Integer> memberActiveHours` (memberId -> total active estimated hours)
     - `Map<Integer, Integer> memberActiveTaskCounts` (memberId -> active task count)

---

### B. Data Access Objects (`com.taskallocator.dao`)

1. **`TaskDAO.java`:**
   - `Task insert(Connection conn, Task task)`: Inserts task metadata with `RETURN_GENERATED_KEYS`.
   - `void insertTaskSkills(Connection conn, int taskId, List<TaskSkillRequirement> skills)`: Inserts required skills into `task_skills`.
   - `void update(Task task)`: Updates task details (title, description, priority, hours, deadline).
   - `void updateStatusAndProgress(int taskId, TaskStatus status, int progressPercent)`: Used by team members for quick updates.
   - `void assignUser(int taskId, int userId)`: Updates `assigned_user_id` and sets status to `ASSIGNED`.
   - `Task findById(int taskId)`: Retrieves task and its associated `requiredSkills`.
   - `List<Task> findByProjectId(int projectId)`: Retrieves all tasks for a project.
   - `List<Task> findByAssignedUser(int userId)`: Retrieves only tasks assigned to a specific team member.
   - `void delete(int taskId)`: Deletes task (cascades to `task_skills`).

---

### C. Business Logic & Service Layer (`com.taskallocator.service`)

1. **`TaskService.java`:**
   - `Task createTask(int projectId, String title, String description, TaskPriority priority, int estimatedHours, java.sql.Date deadline, List<TaskSkillRequirement> requiredSkills)`:
     - Validates all fields and ensures at least one required skill is specified.
     - Runs in a database transaction inserting both `tasks` and `task_skills`.
   - `void updateTask(Task task)`: Validates and persists leader edits.
   - `void updateMemberProgress(int taskId, int memberUserId, int newProgress, TaskStatus newStatus)`:
     - Security check: verifies the task is actually assigned to `memberUserId`.
     - Validates progress (0–100) and applies consistency rules (100% -> `COMPLETED`).
   - `List<Task> getTasksForProject(int projectId)`: Fetches project task list.
   - `List<Task> getTasksForMember(int userId)`: Fetches student's assigned task list.

2. **`ProgressService.java`:**
   - `ProjectProgressSummary getProjectProgress(int projectId)`:
     - Retrieves all tasks for the project and calculates total/completed tasks, unweighted progress, and work-hour weighted progress.
     - Aggregates active workloads per member (`PENDING`, `ASSIGNED`, `IN_PROGRESS` tasks) to supply data for Member 4's allocation engine.

---

### D. Swing GUI Components (`com.taskallocator.gui`)

1. **`TaskTableModel.java` (Extends `AbstractTableModel`):**
   - Clean Swing table model displaying Task ID, Title, Priority, Hours, Deadline, Status, Progress, and Assigned Member.
2. **`TaskEditorDialog.java` (`JDialog`):**
   - Modal dialog for Project Leaders to create or edit tasks.
   - Contains multi-skill selection checklist or table allowing the leader to select skills and choose minimum required level (1–5).
3. **`MemberTaskUpdateDialog.java` (`JDialog`):**
   - Streamlined dialog for team members to adjust their task progress via a `JSlider` (0–100%) and status dropdown.
4. **`ProgressDashboardPanel.java` (`JPanel`):**
   - Reusable progress view showing `JProgressBar` (weighted completion), status counters (Pending, In Progress, Completed), and member workload breakdown table.

---

## 5. Automated Verification Plan (`TestTaskAndProgress.java`)

Member 3 must write and run an automated test suite verifying:

| # | Test Scenario | Expected Outcome |
|---|---|---|
| **1** | Create task with multiple required skills | Task persisted with generated ID; `task_skills` rows verified in database. |
| **2** | Create task with 0 or negative hours | Rejected with `ValidationException("Estimated hours must be greater than 0")`. |
| **3** | Create task without any required skills | Rejected with `ValidationException("At least one required skill must be specified")`. |
| **4** | Member updates assigned task progress to 50% | Status becomes `IN_PROGRESS`, progress updated to 50%. |
| **5** | Member updates progress to 100% | Status automatically transitions to `COMPLETED`. |
| **6** | Unauthorized member attempts to update another student's task | Rejected with `ValidationException` or `AuthenticationException`. |
| **7** | Calculate work-hour weighted project progress | Accurate percentage matching: $\frac{\sum (\text{Progress} \times \text{Hours})}{\sum \text{Hours}}$. |
| **8** | Workload aggregation for allocation engine | Active hours and task counts per member correctly computed. |

---

## 6. Java OOPS Syllabus Defense Matrix (Viva Guide for Member 3)

During the faculty viva, Member 3 should be prepared to defend these specific concepts:

| OOPS Syllabus Topic | Implementation in Member 3's Code |
|---|---|
| **AbstractTableModel & GUI MVC** | `TaskTableModel` extends `AbstractTableModel`, cleanly separating task data from `JTable` Swing presentation. |
| **Collections & Generics** | `ArrayList<Task>`, `List<TaskSkillRequirement>`, and `HashMap<Integer, Integer>` for member workload mappings. |
| **Enums with Custom Methods** | `TaskStatus` and `TaskPriority` enums providing type safety, avoiding brittle String comparisons in business logic. |
| **Composition & Encapsulation** | `Task` composes a `List<TaskSkillRequirement>` representing the 1-to-many task-skills requirement. |
| **Lambdas & Stream API** | Filtering active tasks, computing hour sums, and grouping tasks by status using Java Streams (`tasks.stream().filter(...).mapToInt(...).sum()`). |
| **Swing Event Handling** | `ChangeListener` on `JSlider`, `ActionListener` on buttons, and table selection listeners. |

---

## 7. Next Steps for Implementation

1. Create `TaskStatus.java`, `TaskPriority.java`, `TaskSkillRequirement.java`, and `Task.java` in `com.taskallocator.model`.
2. Implement `ProjectProgressSummary.java` DTO.
3. Implement `TaskDAO.java` in `com.taskallocator.dao` with multi-skill join operations.
4. Implement `TaskService.java` and `ProgressService.java` in `com.taskallocator.service`.
5. Implement `TestTaskAndProgress.java` and execute all automated tests against MySQL.
6. Build `TaskTableModel.java`, `TaskEditorDialog.java`, and `ProgressDashboardPanel.java` in `com.taskallocator.gui`.
