-- =====================================================================
-- Project Task & Collaboration Allocator
-- Database Schema Definition & Seed Script
-- Compatible with MySQL 8.x
-- =====================================================================

CREATE DATABASE IF NOT EXISTS task_allocator_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE task_allocator_db;

-- ---------------------------------------------------------------------
-- 1. Drop existing tables in reverse order of foreign key dependencies
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS task_assignments;
DROP TABLE IF EXISTS task_skills;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS project_members;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS user_skills;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- 2. Users Table
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role ENUM('PROJECT_LEADER', 'TEAM_MEMBER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. Skills Catalog Table
-- ---------------------------------------------------------------------
CREATE TABLE skills (
    skill_id INT AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(50) DEFAULT 'Technical',
    description VARCHAR(255)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. User Skills (Proficiency levels 1 to 5)
-- ---------------------------------------------------------------------
CREATE TABLE user_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    proficiency_level INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_proficiency CHECK (proficiency_level BETWEEN 1 AND 5),
    CONSTRAINT fk_user_skills_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE,
    CONSTRAINT uq_user_skill UNIQUE (user_id, skill_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Projects Table
-- ---------------------------------------------------------------------
CREATE TABLE projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description TEXT,
    leader_id INT NOT NULL,
    status ENUM('PLANNING', 'ACTIVE', 'COMPLETED', 'ARCHIVED') DEFAULT 'ACTIVE',
    deadline DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_projects_leader FOREIGN KEY (leader_id) REFERENCES users(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. Project Members Table
-- ---------------------------------------------------------------------
CREATE TABLE project_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    status ENUM('ACTIVE', 'LEFT', 'COMPLETED') DEFAULT 'ACTIVE',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_project_member UNIQUE (project_id, user_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. Tasks Table
-- ---------------------------------------------------------------------
CREATE TABLE tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    estimated_hours INT NOT NULL,
    deadline DATE NOT NULL,
    status ENUM('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'PENDING',
    progress_percent INT DEFAULT 0,
    assigned_user_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_task_hours CHECK (estimated_hours > 0),
    CONSTRAINT chk_task_progress CHECK (progress_percent BETWEEN 0 AND 100),
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_assigned_user FOREIGN KEY (assigned_user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. Task Skills (Skills required for a specific task)
-- ---------------------------------------------------------------------
CREATE TABLE task_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    skill_id INT NOT NULL,
    min_level_required INT DEFAULT 1,
    CONSTRAINT chk_min_level CHECK (min_level_required BETWEEN 1 AND 5),
    CONSTRAINT fk_ts_task FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
    CONSTRAINT fk_ts_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE RESTRICT,
    CONSTRAINT uq_task_skill UNIQUE (task_id, skill_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. Task Assignments Audit Table (Tracks assignment history)
-- ---------------------------------------------------------------------
CREATE TABLE task_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    user_id INT NOT NULL,
    assigned_by INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes VARCHAR(255),
    CONSTRAINT fk_ta_task FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
    CONSTRAINT fk_ta_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ta_assigned_by FOREIGN KEY (assigned_by) REFERENCES users(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. Performance Indexes
-- ---------------------------------------------------------------------
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_skills_user ON user_skills(user_id);
CREATE INDEX idx_projects_leader ON projects(leader_id);
CREATE INDEX idx_project_members_user ON project_members(user_id);
CREATE INDEX idx_project_members_project ON project_members(project_id);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_assigned ON tasks(assigned_user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_task_skills_task ON task_skills(task_id);
CREATE INDEX idx_task_assignments_task ON task_assignments(task_id);

-- ---------------------------------------------------------------------
-- 11. Seed Data: Master Technical & Project Skills
-- ---------------------------------------------------------------------
INSERT INTO skills (skill_name, category, description) VALUES
('Java Programming', 'Technical', 'Core Java, OOP principles, collections, exception handling'),
('Swing GUI Design', 'Technical', 'Java desktop interface creation using JFrame, JPanel, Layouts'),
('MySQL & Relational DB', 'Technical', 'SQL schema design, foreign keys, JDBC queries, normalization'),
('Algorithm Design', 'Analytical', 'Rule-based logic, sorting, optimization, workload balancing'),
('Software Testing', 'Quality', 'Test case creation, boundary analysis, bug identification'),
('Documentation & Reports', 'Communication', 'Technical writing, report structuring, viva defense prep'),
('System Architecture', 'Design', 'Layered architecture (MVC/DAO), design patterns, UML modeling'),
('Database Administration', 'Technical', 'Indexing, transaction management, query optimization');
