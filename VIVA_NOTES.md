# VIVA_NOTES.md — Project Task & Collaboration Allocator

**Subject:** Object-Oriented Programming (OOPS) Micro-Project  
**Target Syllabus:** Second-Year B.Tech / B.E. Computer Science & IT  
**Architecture:** Java Swing (Desktop GUI) + MySQL 8.x + JDBC (PreparedStatement)  
**Team Size:** 4 College Students  

---

## 1. Four-Member Team Division & Ownership

| Member | Primary Responsibilities | Key Classes & Artifacts |
|---|---|---|
| **Member 1** | Team Member & Skill Management | [Member.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/model/Member.java), [MemberDAO.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/dao/MemberDAO.java) (member methods), Member Management UI Tab |
| **Member 2** | Database, JDBC Foundation & Configuration | [DBConnection.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/util/DBConnection.java), [schema.sql](file:///d:/College%20Files/Year%202/OOPS/MicroProject/sql/schema.sql), [migration.sql](file:///d:/College%20Files/Year%202/OOPS/MicroProject/sql/migration.sql), [TestDatabaseConnection.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/TestDatabaseConnection.java) |
| **Member 3** | Task Creation, Listing & Status Tracking | [Task.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/model/Task.java), [TaskDAO.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/dao/TaskDAO.java), Task JTable & Status Update Panel |
| **Member 4** | Allocation Engine, Report Export & GUI Integration | [TaskAllocator.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/service/TaskAllocator.java), [ReportExporter.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/service/ReportExporter.java), [MainFrame.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/gui/MainFrame.java), [Main.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/Main.java) |

---

## 2. Explanation of Every Java Class

### `Main.java`
- **Location:** `com.taskallocator.Main`
- **Purpose:** The main entry point of the desktop application (`public static void main(String[] args)`).
- **Key Logic:**
  1. Calls `DBConnection.loadConfiguration()` to load database settings.
  2. Sets native OS look-and-feel using `UIManager.setLookAndFeel()`.
  3. Uses `SwingUtilities.invokeLater(...)` to instantiate and display `MainFrame` safely on the Swing **Event Dispatch Thread (EDT)**.

### `Member.java`
- **Location:** `com.taskallocator.model.Member`
- **Purpose:** Domain model entity representing a student team member.
- **Key Logic & OOPS Concept:**
  - **Encapsulation:** Holds private fields (`memberId`, `name`, `email`, `role`, and `skills` as `List<String>`).
  - Access is controlled via getters and setters.
  - Helper methods: `addSkill(String skill)`, `hasSkill(String requiredSkill)`, and `getSkillsFormatted()` (returns comma-separated skill string for JTable display).

### `Task.java`
- **Location:** `com.taskallocator.model.Task`
- **Purpose:** Domain model entity representing a project task.
- **Key Logic & OOPS Concept:**
  - **Encapsulation:** Private attributes (`taskId`, `taskName`, `requiredSkill`, `estimatedHours`, `status`, `assignedMemberId`, `assignedMemberName`).
  - Status values are strictly managed: `"Pending"`, `"In Progress"`, and `"Completed"`.
  - Helper method `isActive()` checks if the task is still open (`!status.equals("Completed")`).

### `DBConnection.java`
- **Location:** `com.taskallocator.util.DBConnection`
- **Purpose:** Centralized JDBC utility managing database connections to MySQL.
- **Key Logic:**
  - Static initializer loads `com.mysql.cj.jdbc.Driver`.
  - Reads connection URL, user, and password from `config/db.properties` using `FileInputStream` and `Properties.load()`.
  - `getConnection()` returns an active `java.sql.Connection` via `DriverManager.getConnection(...)`.
  - `close(AutoCloseable... resources)` safely closes ResultSets, Statements, and Connections.

### `MemberDAO.java`
- **Location:** `com.taskallocator.dao.MemberDAO`
- **Purpose:** Data Access Object for team members and member skills.
- **Key Methods:**
  - `addMember(Member member)`: Inserts a member and retrieves the auto-generated primary key.
  - `getAllMembers()`: Retrieves all members from `members` and populates their skills from `member_skills`.
  - `addSkillToMember(int memberId, String skillName)`: Links a skill to a member using `INSERT IGNORE`.
  - `getMembersBySkill(String requiredSkill)`: Queries members who possess a specific skill via SQL `JOIN`.
  - `getAvailableSkills()`: Returns predefined list of standard college project skills.

### `TaskDAO.java`
- **Location:** `com.taskallocator.dao.TaskDAO`
- **Purpose:** Data Access Object for tasks and assignment logs.
- **Key Methods:**
  - `createTask(Task task)`: Inserts a new task with initial status `"Pending"`.
  - `getAllTasks()`: Queries all tasks using a `LEFT JOIN members` to include the assigned member's name.
  - `updateTaskStatus(int taskId, String newStatus)`: Updates task status.
  - `assignMemberToTask(int taskId, int memberId)`: Performs an atomic transaction (`conn.setAutoCommit(false)`) that updates `tasks.assigned_member_id` and logs the assignment in `task_assignments`.
  - `getActiveTaskCount(int memberId)`: Counts tasks assigned to a member where `status != 'Completed'`.
  - `getCompletedTaskCount()`: Returns count of completed tasks.

### `TaskAllocator.java`
- **Location:** `com.taskallocator.service.TaskAllocator`
- **Purpose:** The transparent, explainable allocation recommendation engine.
- **Key Logic:**
  1. Calls `memberDAO.getMembersBySkill(task.getRequiredSkill())`.
  2. For each matching member, calls `taskDAO.getActiveTaskCount(memberId)`.
  3. Identifies the candidate with the lowest active task count.
  4. Returns `AllocationResult` containing the recommended member, current workload counts of all evaluated candidates, and a clear text rationale.

### `ReportExporter.java`
- **Location:** `com.taskallocator.service.ReportExporter`
- **Purpose:** File handling utility generating a plain-text project summary report.
- **Key Logic:**
  - Uses `BufferedWriter` and `FileWriter` inside a `try-with-resources` block.
  - Formats tables with fixed-width column specifiers using `String.format(...)`.
  - Writes member skills, task status, assigned members, and progress metrics to `.txt`.

### `MainFrame.java`
- **Location:** `com.taskallocator.gui.MainFrame`
- **Purpose:** The primary Java Swing desktop user interface window.
- **Key Features:**
  - `BorderLayout` containing Top Header, Top KPI summary strip, and a `JTabbedPane`.
  - Top role switcher dropdown: `[Project Leader | Team Member]`.
  - Tab 1: Task Management JTable, "+ Create Task" dialog, "Recommend & Assign" dialog, status update dropdown, and report export button.
  - Tab 2: Team Member JTable, "Add Member" form, and "Attach Skill" controls.
  - All database calls populate `DefaultTableModel` rows and update KPI metric cards.

---

## 3. Purpose of Each Database Table

The application uses **4 clean relational tables** in MySQL (`task_allocator_db`):

```text
+-------------------+        1 : N         +-------------------+
|      members      |--------------------->|   member_skills   |
| (PK: member_id)   |                      | (FK: member_id)   |
+-------------------+                      +-------------------+
          | 1
          |
          | N (Optional Assignee)
          v
+-------------------+        1 : N         +-------------------+
|       tasks       |--------------------->| task_assignments  |
|  (PK: task_id)    |                      | (FK: task_id)     |
+-------------------+                      | (FK: member_id)   |
                                           +-------------------+
```

### 1. `members` Table
- **Columns:** `member_id` (INT PK AI), `name` (VARCHAR), `email` (VARCHAR UNIQUE), `role` (VARCHAR), `created_at` (TIMESTAMP).
- **Purpose:** Stores the roster of student team members participating in the project.

### 2. `member_skills` Table
- **Columns:** `skill_id` (INT PK AI), `member_id` (INT FK), `skill_name` (VARCHAR).
- **Purpose:** Represents the 1-to-many relationship between a student and their technical capabilities.
- **Foreign Key:** `member_id` references `members(member_id) ON DELETE CASCADE`.
- **Constraint:** `UNIQUE (member_id, skill_name)` prevents duplicate skill entries for the same member.

### 3. `tasks` Table
- **Columns:** `task_id` (INT PK AI), `task_name` (VARCHAR), `required_skill` (VARCHAR), `estimated_hours` (INT), `status` (VARCHAR), `assigned_member_id` (INT FK NULL), `created_at` (TIMESTAMP).
- **Purpose:** Stores all project deliverables, the technical skill needed to solve them, their effort, current progress status (`'Pending'`, `'In Progress'`, `'Completed'`), and current assignee.
- **Foreign Key:** `assigned_member_id` references `members(member_id) ON DELETE SET NULL`.

### 4. `task_assignments` Table
- **Columns:** `assignment_id` (INT PK AI), `task_id` (INT FK), `member_id` (INT FK), `assigned_date` (TIMESTAMP).
- **Purpose:** Serves as an audit log recording when a task assignment was confirmed by the Project Leader.

---

## 4. How JDBC Connects Java to MySQL

JDBC (**Java Database Connectivity**) is the standard Java API for connecting Java applications to relational databases.

### The Connection Steps:
1. **Load Driver:** The MySQL Connector/J driver class is registered with the Java runtime:
   ```java
   Class.forName("com.mysql.cj.jdbc.Driver");
   ```
2. **Read Credentials:** Connection parameters are externalized in `config/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/task_allocator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
   db.user=root
   db.password=mysql123
   ```
3. **Open Connection:** The connection factory calls:
   ```java
   Connection conn = DriverManager.getConnection(url, user, password);
   ```
4. **Execute SQL Statements:** Commands are sent over TCP port 3306 to MySQL using `PreparedStatement`.
5. **Close Connection:** Handled cleanly using Java's `try-with-resources` block to prevent socket leaks.

---

## 5. How PreparedStatement Works

`PreparedStatement` is a pre-compiled SQL statement object in `java.sql`.

### How it works:
1. Java sends the SQL template containing placeholder question marks (`?`) to MySQL:
   ```java
   String sql = "INSERT INTO tasks (task_name, required_skill, estimated_hours) VALUES (?, ?, ?)";
   try (PreparedStatement stmt = conn.prepareStatement(sql)) { ... }
   ```
2. MySQL parses, compiles, and optimizes the query plan **once**.
3. Java safely binds values to the placeholders by position and data type:
   ```java
   stmt.setString(1, task.getTaskName());
   stmt.setString(2, task.getRequiredSkill());
   stmt.setInt(3, task.getEstimatedHours());
   stmt.executeUpdate();
   ```

### Why PreparedStatement is used over Statement:
- **Prevents SQL Injection:** Values are treated strictly as data literals, never as executable SQL code.
- **Better Performance:** The query structure is compiled once and can be reused.
- **Type Safety:** Handles proper conversion and escaping of quotes, dates, and special characters automatically.

---

## 6. How Task Allocation Works (With Example)

The allocation algorithm is **transparent, deterministic, and explainable**:

```text
[Task Needing Allocation]
         |
         v
[Step 1: Skill Matching]
Query members who have required_skill in member_skills
         |
         +--> If 0 candidates: Return "No matching members found"
         |
         v
[Step 2: Workload Evaluation]
For each matching candidate:
Count active tasks (status = 'Pending' OR status = 'In Progress')
         |
         v
[Step 3: Workload Minimization]
Pick candidate with lowest active task count
         |
         v
[Step 4: Human-in-the-Loop]
Show recommendation & rationale in Swing dialog
Project Leader confirms or chooses alternative candidate
```

### Viva Walkthrough Example:
- **Task to Allocate:** `"Implement Task DAO"` (Requires: `"Java Programming"`)
- **Step 1 (Skill Match):**
  - **Arun Kumar** has `"Java Programming"`.
  - **Bhavna Sharma** has `"Java Programming"`.
  - *(Chetan and Divya do not have this skill, so they are filtered out).*
- **Step 2 (Workload Count):**
  - Arun has 0 active tasks (his previous task is `"Completed"`).
  - Bhavna has 1 active task (`"Build Main Dashboard UI"` is `"In Progress"`).
- **Step 3 (Decision):**
  - `0 < 1` -> The algorithm recommends **Arun Kumar** to balance the workload.
- **Step 4 (Leader Confirmation):**
  - The Project Leader sees this reasoning in the GUI and clicks "OK" to confirm the assignment.

---

## 7. Where Encapsulation and Objects are Demonstrated

- **Encapsulation:**
  - In `Member.java` and `Task.java`, all data members are marked `private` (e.g., `private String taskName;`, `private List<String> skills;`).
  - No external class can alter fields directly. Access is mediated through public getter and setter methods (`getTaskName()`, `setStatus()`).
  - Setters enforce integrity rules (e.g., ensuring `skills` list is never null).
- **Objects & Data Transfer:**
  - Rather than passing raw strings and numbers, entities are passed as cohesive Java objects (`Member`, `Task`, `AllocationResult`).
- **Collections & Abstraction:**
  - `List<Member> members = new ArrayList<>();` demonstrates programming to an interface (`List`) using a concrete implementation (`ArrayList`).

---

## 8. How Exception Handling is Used

- **Checked Exceptions:** Standard JDBC operations throw `java.sql.SQLException`, and file exports throw `java.io.IOException`.
- **Try-With-Resources:** Every database query uses try-with-resources:
  ```java
  try (Connection conn = DBConnection.getConnection();
       PreparedStatement stmt = conn.prepareStatement(sql)) {
      ...
  } catch (SQLException e) {
      JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
  }
  ```
  This guarantees that database connections, statements, and result sets are closed automatically, even if an unexpected error occurs.
- **Database Transactions:** Multi-statement updates (e.g., assigning a task and logging it in `task_assignments`) use explicit commit and rollback:
  ```java
  conn.setAutoCommit(false);
  try {
      // 1. update tasks
      // 2. insert task_assignments
      conn.commit();
  } catch (SQLException e) {
      conn.rollback(); // Rollback changes if either step fails
      throw e;
  }
  ```
- **User-Friendly Feedback:** Catch blocks display informative `JOptionPane.showMessageDialog` popups instead of crashing the GUI application.

---

## 9. How the Text Report is Generated

The report export feature in `ReportExporter.java` uses pure standard Java I/O:
1. Queries current data from MySQL using `MemberDAO.getAllMembers()` and `TaskDAO.getAllTasks()`.
2. Computes summary metrics: Total Tasks, Pending, In Progress, Completed, and Completion Percentage (`(completed / total) * 100`).
3. Uses `BufferedWriter` wrapping a `FileWriter` inside a `try-with-resources` block:
   ```java
   try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
       writer.write("======================================================================\n");
       writer.write("           PROJECT TASK & COLLABORATION ALLOCATOR REPORT              \n");
       ...
       writer.write(String.format("%-6d | %-30s | %-22s | %-6d | %-12s | %s\n",
           t.getTaskId(), t.getTaskName(), t.getRequiredSkill(),
           t.getEstimatedHours(), t.getStatus(), t.getAssignedMemberName()));
   }
   ```
4. Produces a clean, formatted `.txt` report file that can be opened in Notepad or printed for faculty review.

---

## 10. Top 10 Likely Viva Questions & Short Answers

### Q1: What is the purpose of this project?
**Answer:** It is a Java Swing desktop application that helps student teams allocate project tasks based on team members' skills and active workloads, tracks task progress through statuses, and exports a formatted summary report.

### Q2: Why did you use PreparedStatement instead of Statement?
**Answer:** `PreparedStatement` pre-compiles SQL queries on the server, improves performance, and protects the database against SQL Injection by separating SQL syntax from user data.

### Q3: What is the Event Dispatch Thread (EDT) in Java Swing?
**Answer:** Swing is single-threaded for UI operations. The Event Dispatch Thread (EDT) handles all GUI rendering, button clicks, and component updates. We launch our GUI using `SwingUtilities.invokeLater()` to ensure thread safety.

### Q4: How does your task allocation algorithm work?
**Answer:** It filters team members who possess the skill required by the task, counts each candidate's currently active tasks (Pending or In Progress), and recommends the candidate with the fewest active tasks to balance the workload.

### Q5: What happens if two candidates have the same skill and the same number of active tasks?
**Answer:** The algorithm breaks the tie by picking the first matching candidate returned from the query, and displays all eligible candidates in a dropdown so the Project Leader can make the final choice.

### Q6: What is "Human-in-the-Loop" in this application?
**Answer:** The system does not automatically assign tasks silently. Instead, it generates a transparent recommendation with full rationale, and requires the Project Leader to review and confirm the assignment.

### Q7: What are the tables in your database and how are they related?
**Answer:** There are 4 tables: `members` (stores team members), `member_skills` (skills linked to members via foreign key), `tasks` (tasks with foreign key to assigned member), and `task_assignments` (audit log of assignments).

### Q8: What does try-with-resources do in JDBC?
**Answer:** Introduced in Java 7, try-with-resources automatically closes any resource implementing `AutoCloseable` (like `Connection`, `PreparedStatement`, and `ResultSet`) when the try block exits, preventing memory and connection leaks.

### Q9: Why did you remove password hashing and login screens from this project?
**Answer:** The microproject's focus is Object-Oriented Programming, JDBC, and task allocation logic. Cryptographic PBKDF2 hashing and login flows added unnecessary complexity that distracted from demonstrating core OOP concepts and syllabus requirements.

### Q10: How did the four members divide the work?
**Answer:**
- **Member 1:** Member model, skill tracking, and team member management operations.
- **Member 2:** Relational database schema, JDBC connection setup, and database foundation.
- **Member 3:** Task model, task CRUD operations, JTable listing, and status updates.
- **Member 4:** Allocation recommendation engine, text report export, and main Swing GUI integration.
