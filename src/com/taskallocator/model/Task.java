package com.taskallocator.model;

/**
 * =====================================================================
 * Member 3 Responsibility: Task Creation, Task Listing & Status Tracking
 * =====================================================================
 * The Task class models a collaborative project assignment.
 * Demonstrates OOP Encapsulation:
 * - Attributes represent task properties (name, required skill, hours, status, assignee).
 * - Private fields with public getters/setters guard the state.
 */
public class Task {

    private int taskId;
    private String taskName;
    private String requiredSkill;
    private int estimatedHours;
    private String status;               // 'Pending', 'In Progress', 'Completed'
    private Integer assignedMemberId;    // null if unassigned
    private String assignedMemberName;   // Display name of assigned member

    /**
     * Default no-argument constructor.
     */
    public Task() {
        this.status = "Pending";
    }

    /**
     * Constructor for creating a new task prior to database insertion.
     *
     * @param taskName       Name/title of the task
     * @param requiredSkill  Primary technical skill needed (e.g., 'Java Programming')
     * @param estimatedHours Estimated effort in hours
     */
    public Task(String taskName, String requiredSkill, int estimatedHours) {
        this.taskName = taskName;
        this.requiredSkill = requiredSkill;
        this.estimatedHours = estimatedHours;
        this.status = "Pending";
        this.assignedMemberId = null;
        this.assignedMemberName = "Unassigned";
    }

    /**
     * Full constructor for reconstructing an existing task from database queries.
     *
     * @param taskId             Task ID primary key
     * @param taskName           Task name
     * @param requiredSkill      Required skill
     * @param estimatedHours     Estimated hours
     * @param status             Current status ('Pending', 'In Progress', 'Completed')
     * @param assignedMemberId   Assigned member ID (or null)
     * @param assignedMemberName Assigned member name (or 'Unassigned')
     */
    public Task(int taskId, String taskName, String requiredSkill, int estimatedHours, 
                String status, Integer assignedMemberId, String assignedMemberName) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.requiredSkill = requiredSkill;
        this.estimatedHours = estimatedHours;
        this.status = (status != null) ? status : "Pending";
        this.assignedMemberId = assignedMemberId;
        this.assignedMemberName = (assignedMemberName != null) ? assignedMemberName : "Unassigned";
    }

    // Getters and Setters

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getRequiredSkill() {
        return requiredSkill;
    }

    public void setRequiredSkill(String requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAssignedMemberId() {
        return assignedMemberId;
    }

    public void setAssignedMemberId(Integer assignedMemberId) {
        this.assignedMemberId = assignedMemberId;
    }

    public String getAssignedMemberName() {
        return assignedMemberName;
    }

    public void setAssignedMemberName(String assignedMemberName) {
        this.assignedMemberName = assignedMemberName;
    }

    /**
     * Helper method to check if this task is active (Pending or In Progress).
     */
    public boolean isActive() {
        return !"Completed".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return taskName + " [" + requiredSkill + ", " + status + "]";
    }
}
