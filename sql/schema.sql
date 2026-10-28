-- =====================================================================
-- Project Task & Collaboration Allocator
-- Simplified Relational Database Schema for College OOPS Microproject
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
DROP TABLE IF EXISTS member_skills;
DROP TABLE IF EXISTS user_skills;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- 2. Members Table (Member 1 & Member 2)
-- Stores team members who participate in college projects.
-- ---------------------------------------------------------------------
CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'Team Member',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. Member Skills Table (Member 1 & Member 2)
-- Relates members to their technical/project skills.
-- ---------------------------------------------------------------------
CREATE TABLE member_skills (
    skill_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_member_skills_member FOREIGN KEY (member_id) 
        REFERENCES members(member_id) ON DELETE CASCADE,
    CONSTRAINT uq_member_skill UNIQUE (member_id, skill_name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. Tasks Table (Member 3)
-- Stores tasks with required skill, estimated effort, status, and assignment.
-- ---------------------------------------------------------------------
CREATE TABLE tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(150) NOT NULL,
    required_skill VARCHAR(100) NOT NULL,
    estimated_hours INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Pending',
    assigned_member_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_task_hours CHECK (estimated_hours > 0),
    CONSTRAINT fk_tasks_assigned_member FOREIGN KEY (assigned_member_id) 
        REFERENCES members(member_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Task Assignments Log Table (Member 4)
-- Records historical task assignments confirmed by the Project Leader.
-- ---------------------------------------------------------------------
CREATE TABLE task_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    member_id INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ta_task FOREIGN KEY (task_id) 
        REFERENCES tasks(task_id) ON DELETE CASCADE,
    CONSTRAINT fk_ta_member FOREIGN KEY (member_id) 
        REFERENCES members(member_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. Indexes for Fast Lookups
-- ---------------------------------------------------------------------
CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_member_skills_member ON member_skills(member_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_assigned ON tasks(assigned_member_id);

-- ---------------------------------------------------------------------
-- 7. Seed Data: Realistic College Project Team, Skills & Initial Tasks
-- ---------------------------------------------------------------------
INSERT INTO members (name, email, role) VALUES
('Arun Kumar', 'arun.kumar@college.edu', 'Project Leader'),
('Bhavna Sharma', 'bhavna.s@college.edu', 'Team Member'),
('Chetan Patel', 'chetan.p@college.edu', 'Team Member'),
('Divya Nair', 'divya.n@college.edu', 'Team Member');

INSERT INTO member_skills (member_id, skill_name) VALUES
(1, 'Java Programming'),
(1, 'MySQL & Relational DB'),
(2, 'Swing GUI Design'),
(2, 'Java Programming'),
(3, 'MySQL & Relational DB'),
(3, 'Software Testing'),
(4, 'Swing GUI Design'),
(4, 'Documentation & Reports');

INSERT INTO tasks (task_name, required_skill, estimated_hours, status, assigned_member_id) VALUES
('Design Database Tables', 'MySQL & Relational DB', 8, 'Completed', 1),
('Build Main Dashboard UI', 'Swing GUI Design', 12, 'In Progress', 2),
('Implement Task DAO Operations', 'Java Programming', 10, 'Pending', NULL),
('Write Unit Test Cases', 'Software Testing', 6, 'Pending', NULL),
('Prepare Viva Documentation', 'Documentation & Reports', 5, 'Pending', NULL);

INSERT INTO task_assignments (task_id, member_id) VALUES
(1, 1),
(2, 2);
