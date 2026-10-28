-- =====================================================================
-- Project Task & Collaboration Allocator
-- Migration Script: From Complex Schema to Simplified 4-Table Schema
-- =====================================================================

USE task_allocator_db;

-- 1. Create new members table if not exists
CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'Team Member',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Migrate existing users from 'users' table if it exists
INSERT IGNORE INTO members (member_id, name, email, role, created_at)
SELECT user_id, full_name, email, 
       CASE WHEN role = 'PROJECT_LEADER' THEN 'Project Leader' ELSE 'Team Member' END,
       created_at
FROM users;

-- 3. Create member_skills table
CREATE TABLE IF NOT EXISTS member_skills (
    skill_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_member_skills_member FOREIGN KEY (member_id) 
        REFERENCES members(member_id) ON DELETE CASCADE,
    CONSTRAINT uq_member_skill UNIQUE (member_id, skill_name)
) ENGINE=InnoDB;

-- 4. Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
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

-- 5. Create task_assignments table
CREATE TABLE IF NOT EXISTS task_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    member_id INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ta_task FOREIGN KEY (task_id) 
        REFERENCES tasks(task_id) ON DELETE CASCADE,
    CONSTRAINT fk_ta_member FOREIGN KEY (member_id) 
        REFERENCES members(member_id) ON DELETE CASCADE
) ENGINE=InnoDB;
