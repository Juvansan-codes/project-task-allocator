package com.taskallocator.service;

import com.taskallocator.dao.MemberDAO;
import com.taskallocator.dao.TaskDAO;
import com.taskallocator.model.Member;
import com.taskallocator.model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * =====================================================================
 * Member 4 Responsibility: Project Summary Report Export (File Handling)
 * =====================================================================
 * ReportExporter generates a clean, well-formatted plain-text (.txt) report
 * summarizing the project's current team members, skills, task assignments,
 * and completion statistics.
 * Demonstrates:
 * - Java File I/O using BufferedWriter and FileWriter.
 * - Automatic resource management with try-with-resources.
 */
public class ReportExporter {

    /**
     * Exports the project summary to a text file.
     *
     * @param targetFile File destination where the report will be saved
     * @param memberDAO  Member data access object
     * @param taskDAO    Task data access object
     * @return Absolute path of the exported file
     * @throws IOException  if a file writing error occurs
     * @throws SQLException if a database query error occurs
     */
    public String exportSummaryReport(File targetFile, MemberDAO memberDAO, TaskDAO taskDAO) 
            throws IOException, SQLException {

        List<Member> members = memberDAO.getAllMembers();
        List<Task> tasks = taskDAO.getAllTasks();

        int totalTasks = taskDAO.getTotalTaskCount();
        int pendingTasks = taskDAO.getPendingTaskCount();
        int inProgressTasks = taskDAO.getInProgressTaskCount();
        int completedTasks = taskDAO.getCompletedTaskCount();

        double completionPercentage = (totalTasks > 0) 
            ? ((double) completedTasks / totalTasks) * 100.0 
            : 0.0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            writer.write("======================================================================\n");
            writer.write("           PROJECT TASK & COLLABORATION ALLOCATOR REPORT              \n");
            writer.write("                Second-Year OOPS Micro-Project                        \n");
            writer.write("======================================================================\n");
            writer.write("Report Generated On : " + timestamp + "\n");
            writer.write("Export File Path    : " + targetFile.getAbsolutePath() + "\n");
            writer.write("----------------------------------------------------------------------\n\n");

            // SECTION 1: TEAM MEMBERS & SKILLS
            writer.write("----------------------------------------------------------------------\n");
            writer.write("1. TEAM MEMBERS & RECORDED SKILLS (" + members.size() + " Members)\n");
            writer.write("----------------------------------------------------------------------\n");
            writer.write(String.format("%-6s | %-20s | %-25s | %-16s | %s\n", 
                    "ID", "Name", "Email", "Role", "Skills"));
            writer.write("----------------------------------------------------------------------\n");
            for (Member m : members) {
                writer.write(String.format("%-6d | %-20s | %-25s | %-16s | %s\n",
                        m.getMemberId(),
                        m.getName(),
                        m.getEmail(),
                        m.getRole(),
                        m.getSkillsFormatted()));
            }
            writer.write("\n");

            // SECTION 2: TASKS & ASSIGNMENTS
            writer.write("----------------------------------------------------------------------\n");
            writer.write("2. TASKS & CURRENT ALLOCATIONS (" + tasks.size() + " Tasks)\n");
            writer.write("----------------------------------------------------------------------\n");
            writer.write(String.format("%-6s | %-30s | %-22s | %-6s | %-12s | %s\n",
                    "ID", "Task Name", "Required Skill", "Hours", "Status", "Assigned Member"));
            writer.write("----------------------------------------------------------------------\n");
            for (Task t : tasks) {
                writer.write(String.format("%-6d | %-30s | %-22s | %-6d | %-12s | %s\n",
                        t.getTaskId(),
                        t.getTaskName(),
                        t.getRequiredSkill(),
                        t.getEstimatedHours(),
                        t.getStatus(),
                        t.getAssignedMemberName()));
            }
            writer.write("\n");

            // SECTION 3: PROGRESS METRICS
            writer.write("----------------------------------------------------------------------\n");
            writer.write("3. PROJECT PROGRESS SUMMARY METRICS\n");
            writer.write("----------------------------------------------------------------------\n");
            writer.write(String.format("Total Tasks Defined       : %d\n", totalTasks));
            writer.write(String.format("Pending Tasks             : %d\n", pendingTasks));
            writer.write(String.format("In-Progress Tasks         : %d\n", inProgressTasks));
            writer.write(String.format("Completed Tasks           : %d\n", completedTasks));
            writer.write(String.format("Overall Completion Rate   : %.1f%%\n", completionPercentage));
            writer.write("----------------------------------------------------------------------\n");
            writer.write("End of Report. Generated by Project Task & Collaboration Allocator.\n");
            writer.write("======================================================================\n");
        }

        return targetFile.getAbsolutePath();
    }
}
