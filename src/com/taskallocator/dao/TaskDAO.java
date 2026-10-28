package com.taskallocator.dao;

import com.taskallocator.model.Task;
import com.taskallocator.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * Member 3 Responsibility: Task Management & Progress Database Access
 * =====================================================================
 * TaskDAO handles all CRUD operations on the 'tasks' and 'task_assignments'
 * tables.
 * Features:
 * - Uses PreparedStatement for all parameter binding.
 * - Manages status transitions ('Pending', 'In Progress', 'Completed').
 * - Calculates active workloads and completion statistics.
 */
public class TaskDAO {

    /**
     * Inserts a new task into the database.
     *
     * @param task Task entity containing title, required skill, and hours
     * @return true if created successfully, false otherwise
     * @throws SQLException if insert fails
     */
    public boolean createTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (task_name, required_skill, estimated_hours, status, assigned_member_id) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getRequiredSkill());
            stmt.setInt(3, task.getEstimatedHours());
            stmt.setString(4, task.getStatus() != null ? task.getStatus() : "Pending");

            if (task.getAssignedMemberId() != null) {
                stmt.setInt(5, task.getAssignedMemberId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        task.setTaskId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Retrieves all tasks with their assigned member's display name.
     *
     * @return List of Task objects
     * @throws SQLException if query fails
     */
    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.task_id, t.task_name, t.required_skill, t.estimated_hours, t.status, " +
                     "       t.assigned_member_id, m.name AS member_name " +
                     "FROM tasks t " +
                     "LEFT JOIN members m ON t.assigned_member_id = m.member_id " +
                     "ORDER BY t.task_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("task_name");
                String requiredSkill = rs.getString("required_skill");
                int estimatedHours = rs.getInt("estimated_hours");
                String status = rs.getString("status");

                int memberId = rs.getInt("assigned_member_id");
                Integer assignedId = rs.wasNull() ? null : memberId;
                String memberName = rs.getString("member_name");
                if (memberName == null) {
                    memberName = "Unassigned";
                }

                Task t = new Task(taskId, taskName, requiredSkill, estimatedHours, status, assignedId, memberName);
                tasks.add(t);
            }
        }
        return tasks;
    }

    /**
     * Updates the status of an existing task.
     * Allowed statuses: 'Pending', 'In Progress', 'Completed'.
     *
     * @param taskId    ID of task to update
     * @param newStatus New status value
     * @return true if updated, false otherwise
     * @throws SQLException if update fails
     */
    public boolean updateTaskStatus(int taskId, String newStatus) throws SQLException {
        String sql = "UPDATE tasks SET status = ? WHERE task_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, taskId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Assigns a team member to a task and logs the assignment in the audit table.
     *
     * @param taskId   Task ID
     * @param memberId Assigned Member ID
     * @return true if successful
     * @throws SQLException if update fails
     */
    public boolean assignMemberToTask(int taskId, int memberId) throws SQLException {
        String updateTaskSql = "UPDATE tasks SET assigned_member_id = ? WHERE task_id = ?";
        String insertAssignmentSql = "INSERT INTO task_assignments (task_id, member_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction
            try {
                // 1. Update task assigned member
                try (PreparedStatement stmt1 = conn.prepareStatement(updateTaskSql)) {
                    stmt1.setInt(1, memberId);
                    stmt1.setInt(2, taskId);
                    stmt1.executeUpdate();
                }

                // 2. Record in assignment log
                try (PreparedStatement stmt2 = conn.prepareStatement(insertAssignmentSql)) {
                    stmt2.setInt(1, taskId);
                    stmt2.setInt(2, memberId);
                    stmt2.executeUpdate();
                }

                conn.commit(); // Commit transaction
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Roll back on error
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Counts active tasks currently assigned to a member.
     * Active tasks are those that are NOT 'Completed' (i.e. 'Pending' or 'In Progress').
     * Used by Member 4's allocation algorithm to balance workloads.
     *
     * @param memberId Member ID
     * @return count of active tasks
     * @throws SQLException if query fails
     */
    public int getActiveTaskCount(int memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks " +
                     "WHERE assigned_member_id = ? AND status != 'Completed'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Returns the total count of tasks in the database.
     */
    public int getTotalTaskCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Returns the count of tasks that have reached 'Completed' status.
     */
    public int getCompletedTaskCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status = 'Completed'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Returns the count of tasks that are currently 'In Progress'.
     */
    public int getInProgressTaskCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status = 'In Progress'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Returns the count of tasks that are currently 'Pending'.
     */
    public int getPendingTaskCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status = 'Pending'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
