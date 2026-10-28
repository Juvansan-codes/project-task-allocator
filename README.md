# Project Task & Collaboration Allocator

A straightforward, functional desktop Java application developed for the second-year **Object-Oriented Programming (OOPS)** micro-project. It assists college student teams in organizing members, managing required skills, defining tasks, recommending optimal task assignments based on skill match and workload balance, tracking task progress, and exporting structured project reports.

---

## 1. Key Project Highlights

- **Direct Main Window:** Opens directly into the primary Swing workspace (`JFrame`) without tedious login hurdles.
- **Role Toggle:** Optional role selector (`Project Leader` vs. `Team Member`) to demonstrate role-based permissions in viva.
- **Pure Java Swing GUI:** Built using `JFrame`, `JPanel`, `JButton`, `JLabel`, `JTextField`, `JComboBox`, `JTable`, and `JOptionPane`.
- **Relational Database:** MySQL 8.x with pure JDBC (`PreparedStatement`, `try-with-resources`, transactions).
- **Transparent Task Allocation:** Skill matching + Workload minimization (recommends candidate with fewest active tasks).
- **Human-in-the-Loop:** Project Leader reviews candidate recommendation and confirms or overrides assignment.
- **Progress Tracking:** Tracks task statuses (`Pending`, `In Progress`, `Completed`) with live completed-task KPI counters.
- **Report Export:** Exports formatted plain-text `.txt` project summary reports using standard Java File I/O (`BufferedWriter`).

---

## 2. Four-Member Team Division

| Member | Module & Focus Area | Key Responsibilities |
|---|---|---|
| **Member 1** | Team Member & Skill Management | [Member.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/model/Member.java), [MemberDAO.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/dao/MemberDAO.java) member operations, Team Member UI Tab |
| **Member 2** | Database, JDBC Foundation & Configuration | [DBConnection.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/util/DBConnection.java), [schema.sql](file:///d:/College%20Files/Year%202/OOPS/MicroProject/sql/schema.sql), [migration.sql](file:///d:/College%20Files/Year%202/OOPS/MicroProject/sql/migration.sql), [TestDatabaseConnection.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/TestDatabaseConnection.java) |
| **Member 3** | Task Creation, Listing & Status Tracking | [Task.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/model/Task.java), [TaskDAO.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/dao/TaskDAO.java), Task JTable & Status Update Panel |
| **Member 4** | Allocation Engine, Report Export & GUI Integration | [TaskAllocator.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/service/TaskAllocator.java), [ReportExporter.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/service/ReportExporter.java), [MainFrame.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/gui/MainFrame.java), [Main.java](file:///d:/College%20Files/Year%202/OOPS/MicroProject/src/com/taskallocator/Main.java) |

---

## 3. Technology Stack & Prerequisites

| Component | Specification |
|---|---|
| **Language** | Java 17+ (SE Development Kit) |
| **GUI Framework** | Java Swing / AWT (Standard Java Library) |
| **Database** | MySQL 8.x (Service: `MySQL80`) |
| **Database Driver** | MySQL Connector/J 8.3.0 (`lib/mysql-connector-j-8.3.0.jar`) |
| **Build & IDE** | Eclipse IDE (Standard Java Project) |
| **File Export** | Java I/O (`BufferedWriter`, `FileWriter`) |

---

## 4. Feature Audit: Removed, Retained & Modified

### Features Removed (Unnecessary Enterprise Complexity)
- **User Registration & Login Screens:** Removed multi-step registration and login screens that hindered viva demonstration.
- **Password Hashing & PBKDF2 Utilities:** Removed `PasswordUtil.java` (cryptographic salts, PBKDF2 HMAC-SHA256) which was outside the 2nd-year OOPS syllabus.
- **Complex Authentication & Session Management:** Removed `AuthService.java` and `UserSession.java` singletons.
- **Overengineered Inheritance Hierarchies:** Removed abstract `BaseEntity.java`. Replaced with clean entity classes `Member.java` and `Task.java`.
- **Custom Exception Classes:** Removed 4 custom exception wrappers (`AppException`, `DatabaseException`, `ValidationException`, `AuthenticationException`) in favor of standard Java `SQLException` and `try-with-resources`.
- **Complex 8-Table Schema:** Replaced multi-table junction structure with a clear 4-table relational schema.

### Features Retained
- **Java Swing GUI:** Main desktop window with layout managers (`BorderLayout`, `FlowLayout`, `GridLayout`), buttons, tables, and dialogs.
- **Pure JDBC with PreparedStatement:** Parameterized SQL queries preventing SQL injection and connection pooling via `DBConnection`.
- **Database Configuration File:** `config/db.properties` safely externalizes credentials and remains ignored by Git.
- **Human-in-the-Loop Confirmation:** System recommends assignments; Project Leader makes final assignment decision.
- **Eclipse Project Descriptors:** Maintained `.project` and `.classpath` for out-of-the-box Eclipse import.

### Features Modified & Simplified
- **Direct Launch:** Application launches directly into `MainFrame` on EDT with a simple role dropdown.
- **Allocation Algorithm:** Replaced percentage formula with transparent 2-step rule:
  1. Skill match: Find members with task's required skill.
  2. Workload balance: Pick candidate with lowest active task count (`Pending` or `In Progress`).
- **Clean 4-Table Database Schema:** `members`, `member_skills`, `tasks`, and `task_assignments`.
- **Task Progress Dashboard:** Live KPI cards for Total Members, Total Tasks, In Progress, and Completed Tasks.
- **Report Exporter:** Formatted plain-text summary report exported via standard `BufferedWriter`.

---

## 5. Project Directory Structure

```text
├── .classpath                          # Eclipse classpath descriptor (Java 17 + MySQL Driver)
├── .gitignore                          # Excludes build output and local db credentials
├── .project                            # Eclipse project configuration
├── README.md                           # Documentation, setup & audit summary
├── VIVA_NOTES.md                       # Complete viva preparation guide with Q&A
├── config/
│   ├── db.properties                   # Local database credentials (untracked)
│   └── db.properties.example           # Credentials template
├── lib/
│   └── mysql-connector-j-8.3.0.jar     # MySQL JDBC driver
├── sql/
│   ├── schema.sql                      # 4-table relational DDL schema & seed data
│   └── migration.sql                   # Migration script from legacy schema
└── src/
    └── com/taskallocator/
        ├── Main.java                   # Application launcher (opens MainFrame directly)
        ├── TestDatabaseConnection.java # Phase 2 DB connectivity test
        ├── TestWorkflow.java           # Complete automated test suite (9 test cases)
        ├── model/
        │   ├── Member.java             # Team member model (Member 1)
        │   └── Task.java               # Project task model (Member 3)
        ├── dao/
        │   ├── MemberDAO.java          # Member & skill database operations (Member 1 & 2)
        │   └── TaskDAO.java            # Task CRUD & workload tracking (Member 3)
        ├── service/
        │   ├── TaskAllocator.java      # Workload-balanced allocation engine (Member 4)
        │   └── ReportExporter.java     # Text report export via Java I/O (Member 4)
        ├── gui/
        │   └── MainFrame.java          # Swing desktop interface & dashboard (Member 4)
        └── util/
            └── DBConnection.java       # Centralized JDBC connection manager (Member 2)
```

---

## 6. Setup and Running Instructions

### Step 1: Database Setup in MySQL
Open MySQL Workbench or MySQL Command Line Client, and run:
```sql
source sql/schema.sql;
```
*(This creates `task_allocator_db`, the 4 tables, and seeds sample college team data).*

### Step 2: Configure Database Credentials
Ensure `config/db.properties` has your local MySQL root credentials:
```properties
db.url=jdbc:mysql://localhost:3306/task_allocator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
db.user=root
db.password=your_mysql_password
```

### Step 3: Open in Eclipse IDE
1. Open Eclipse IDE.
2. Select **File -> Import... -> General -> Existing Projects into Workspace**.
3. Choose the root folder (`d:\College Files\Year 2\OOPS\MicroProject`).
4. Click **Finish**. Eclipse will recognize the `.project` and `.classpath` automatically.
5. Right-click `com.taskallocator.Main` -> **Run As -> Java Application**.

### Step 4: Run from Terminal (PowerShell / Command Prompt)

```powershell
# 1. Compile the entire project
$files = Get-ChildItem -Recurse -Filter "*.java" src | Select-Object -ExpandProperty FullName
javac --release 17 -cp "lib/mysql-connector-j-8.3.0.jar;src" -d bin $files

# 2. Run Database Connectivity Test
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.taskallocator.TestDatabaseConnection

# 3. Run Automated End-to-End Test Suite (9 Tests)
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.taskallocator.TestWorkflow

# 4. Launch Swing Desktop Application
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.taskallocator.Main
```

---

## 7. Viva Preparation

For a complete breakdown of every class, database table, JDBC concepts, PreparedStatement, Encapsulation, Exception Handling, File I/O, and 10 likely viva questions with answers, refer to [VIVA_NOTES.md](file:///d:/College%20Files/Year%202/OOPS/MicroProject/VIVA_NOTES.md).
