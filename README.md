# Project Task & Collaboration Allocator

A desktop Java application developed for the second-year **Object-Oriented Programming (OOPS)** micro-project. It assists college students in organizing project teams, defining multi-skill tasks, generating transparent rule-based allocation recommendations, tracking task progress, and exporting structured project reports.

---

## 1. Key Project Highlights

- **Desktop GUI:** Built using Java Swing (`JFrame`, `JPanel`, `JDialog`, `JTable`).
- **Database:** MySQL 8.x with pure JDBC (`PreparedStatement`, manual transactions).
- **Rule-Based Allocation:** Transparent, explainable algorithmic recommendation based on skill match and workload availability (70% skills + 30% workload).
- **Human-in-the-Loop:** Project Leader reviews candidate scores and confirms or overrides assignments.
- **Academic Governance:** Enforces that a student may belong to only **one active project** at a time.
- **Report Generation:** Generates formatted `.txt` project summary reports using standard Java I/O (`BufferedWriter`).

---

## 2. Technology Stack & Prerequisites

| Component | Specification |
|---|---|
| **Language** | Java 17+ (SE Development Kit) |
| **GUI Framework** | Java Swing / AWT |
| **Database** | MySQL 8.x (Service: `MySQL80`) |
| **Database Driver** | MySQL Connector/J 8.3.0 (`lib/mysql-connector-j-8.3.0.jar`) |
| **Build & IDE** | Eclipse IDE (Standard Java Project) |
| **Version Control** | Git & GitHub |

---

## 3. Project Directory Structure

```text
├── .classpath                          # Eclipse classpath descriptor
├── .gitignore                          # Ignores build artifacts and local credentials
├── .project                            # Eclipse project descriptor
├── README.md                           # Project documentation & run guide
├── CONTEXT.md                          # Single source of truth for requirements
├── PHASES.md                           # Team division & phase-wise work plan
├── config/
│   ├── db.properties                   # Local database credentials (untracked)
│   └── db.properties.example           # Template for database configuration
├── lib/
│   └── mysql-connector-j-8.3.0.jar     # MySQL JDBC driver
├── sql/
│   └── schema.sql                      # Database DDL schema & seed skills
└── src/
    └── com/taskallocator/
        ├── TestDatabaseConnection.java # Database connection verification test
        ├── exception/
        │   ├── AppException.java       # Custom base checked exception
        │   ├── DatabaseException.java  # SQL and connection errors
        │   └── ValidationException.java# Business & input validation errors
        ├── util/
        │   └── DBConnection.java       # JDBC connection manager & transactions
        ├── model/                      # Domain entities (Phase 3)
        ├── dao/                        # Data Access Objects (Phase 3)
        ├── service/                    # Business rules & services (Phase 3)
        ├── strategy/                   # Rule-based allocation algorithms (Phase 3)
        └── gui/                        # Swing JFrames & Dialogs (Phase 3)
```

---

## 4. Setup and Installation

### Step 1: Database Setup in MySQL
Open MySQL Workbench or MySQL Command Line Client, and run:
```sql
source sql/schema.sql;
```
*(Or paste the contents of `sql/schema.sql` into MySQL Workbench and execute).*

### Step 2: Configure Database Credentials
Copy `config/db.properties.example` to `config/db.properties` and enter your MySQL password:
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

### Step 4: Verify Database Connection
Run `com.taskallocator.TestDatabaseConnection` as a Java Application in Eclipse, or via terminal:
```bash
# Compile
javac -cp "lib/mysql-connector-j-8.3.0.jar;src" -d bin src/com/taskallocator/exception/*.java src/com/taskallocator/util/*.java src/com/taskallocator/TestDatabaseConnection.java

# Execute Test
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.taskallocator.TestDatabaseConnection
```
You should see:
```text
[SUCCESS] Successfully connected to MySQL server!
Database Product : MySQL
Driver Name      : MySQL Connector/J
Current DB       : task_allocator_db
[SUCCESS] Transaction commit/rollback verified successfully.
```

---

## 5. Team Roles (3-Member College Team)

- **Member 1 (Authentication, Security & Skills):** User models, PBKDF2 password hashing, registration/login flow, skill profile management (1–5 scale).
- **Member 2 (Database Foundation, Projects & Membership):** Relational schema, JDBC connection management, project creation, single active project constraint, DAO layer.
- **Member 3 (Tasks, Allocation Engine, Progress & Reports):** Multi-skill task management, rule-based `AllocationStrategy`, leader confirmation workflow, progress dashboard, Java I/O `.txt` report export.
