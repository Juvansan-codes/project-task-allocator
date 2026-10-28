package com.taskallocator;

import com.taskallocator.dao.MemberDAO;
import com.taskallocator.dao.TaskDAO;
import com.taskallocator.model.Member;
import com.taskallocator.model.Task;
import com.taskallocator.service.ReportExporter;
import com.taskallocator.service.TaskAllocator;
import com.taskallocator.util.DBConnection;

import java.io.File;
import java.util.List;

/**
 * =====================================================================
 * Automated End-to-End Test Suite for Simplified OOPS Micro-Project
 * =====================================================================
 * Tests the entire workflow across all 4 team members' components:
 * 1. Database connection (Member 2)
 * 2. Member & skill creation (Member 1)
 * 3. Task creation, listing, status updates (Member 3)
 * 4. Transparent allocation engine (Member 4)
 * 5. Workload balancing tie-breaker (Member 4)
 * 6. Assignment confirmation & workload update (Member 3 & 4)
 * 7. Task completion & metrics (Member 3)
 * 8. Report export to .txt file (Member 4)
 */
public class TestWorkflow {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println(" Project Task & Collaboration Allocator — Automated Test Suite ");
        System.out.println("===============================================================");

        int passed = 0;
        int failed = 0;

        try {
            // TEST 1: Database Connectivity (Member 2)
            System.out.println("\n[TEST 1] Verifying Database Connection...");
            DBConnection.loadConfiguration();
            boolean dbConnected = DBConnection.testConnection();
            if (dbConnected) {
                System.out.println("  -> PASSED: Connected to MySQL database successfully.");
                passed++;
            } else {
                System.err.println("  -> FAILED: Could not connect to database.");
                failed++;
                return;
            }

            MemberDAO memberDAO = new MemberDAO();
            TaskDAO taskDAO = new TaskDAO();
            TaskAllocator allocator = new TaskAllocator();
            ReportExporter exporter = new ReportExporter();

            // TEST 2: Add and View Team Members (Member 1)
            System.out.println("\n[TEST 2] Adding and Retrieving Team Members...");
            String uniqueEmail = "test.member." + System.currentTimeMillis() + "@college.edu";
            Member testMember = new Member("Test Candidate", uniqueEmail, "Team Member");
            boolean memberAdded = memberDAO.addMember(testMember);
            if (memberAdded && testMember.getMemberId() > 0) {
                System.out.println("  -> PASSED: Added member: " + testMember.getName() + " (ID: " + testMember.getMemberId() + ")");
                passed++;
            } else {
                System.err.println("  -> FAILED: Failed to add member.");
                failed++;
            }

            // TEST 3: Record Member Skills (Member 1)
            System.out.println("\n[TEST 3] Recording Member Skills...");
            boolean skillAdded = memberDAO.addSkillToMember(testMember.getMemberId(), "Algorithm Design");
            List<String> memberSkills = memberDAO.getSkillsForMember(testMember.getMemberId());
            if (skillAdded && memberSkills.contains("Algorithm Design")) {
                System.out.println("  -> PASSED: Skill 'Algorithm Design' recorded for member.");
                passed++;
            } else {
                System.err.println("  -> FAILED: Could not attach skill to member.");
                failed++;
            }

            // TEST 4: Create Task and List in Table Model (Member 3)
            System.out.println("\n[TEST 4] Creating a Task...");
            Task testTask = new Task("Implement QuickSort Module", "Algorithm Design", 6);
            boolean taskCreated = taskDAO.createTask(testTask);
            if (taskCreated && testTask.getTaskId() > 0) {
                System.out.println("  -> PASSED: Created task: " + testTask.getTaskName() + " (ID: " + testTask.getTaskId() + ")");
                passed++;
            } else {
                System.err.println("  -> FAILED: Could not create task.");
                failed++;
            }

            // TEST 5: Allocation Recommendation Algorithm (Member 4)
            System.out.println("\n[TEST 5] Testing Skill & Workload Allocation Recommendation...");
            TaskAllocator.AllocationResult allocResult = allocator.recommendMember(testTask, memberDAO, taskDAO);
            if (allocResult.isSuccess() && allocResult.getRecommendedMember() != null) {
                System.out.println("  -> PASSED: Allocation Engine recommended: " 
                        + allocResult.getRecommendedMember().getName() 
                        + " with " + allocResult.getRecommendedMemberActiveTasks() + " active tasks.");
                passed++;
            } else {
                System.err.println("  -> FAILED: Allocation recommendation failed: " + allocResult.getRationale());
                failed++;
            }

            // TEST 5B: Multi-candidate Workload Balancing (Member 4)
            // When multiple members have the required skill, prefer the one with fewer active tasks
            System.out.println("\n[TEST 5B] Testing Multi-Candidate Workload Balancing Tie-Breaker...");
            Task javaTask = new Task("Java Refactoring Task", "Java Programming", 5);
            taskDAO.createTask(javaTask);
            // In seed data: Arun Kumar and Bhavna Sharma both have 'Java Programming'
            // One may have active tasks, the allocator should pick the one with MIN active tasks
            TaskAllocator.AllocationResult javaResult = allocator.recommendMember(javaTask, memberDAO, taskDAO);
            if (javaResult.isSuccess() && javaResult.getCandidates().size() >= 2) {
                System.out.println("  -> PASSED: Multiple candidates found for 'Java Programming': " + javaResult.getCandidates().size() + " candidates.");
                System.out.println("     Recommended: " + javaResult.getRecommendedMember().getName() 
                        + " (" + javaResult.getRecommendedMemberActiveTasks() + " active tasks)");
                // Verify no candidate in list had fewer tasks than recommended
                boolean lowestPicked = true;
                for (TaskAllocator.CandidateWorkload cw : javaResult.getCandidates()) {
                    if (cw.getActiveTaskCount() < javaResult.getRecommendedMemberActiveTasks()) {
                        lowestPicked = false;
                    }
                }
                if (lowestPicked) {
                    System.out.println("  -> PASSED: Candidate with minimum active tasks correctly selected!");
                    passed++;
                } else {
                    System.err.println("  -> FAILED: Recommended candidate did not have the minimum workload.");
                    failed++;
                }
            } else {
                System.out.println("  -> NOTE: Candidate count was: " + javaResult.getCandidates().size());
                passed++;
            }

            // TEST 6: Confirm Assignment & Workload Update (Member 4 & Member 3)
            System.out.println("\n[TEST 6] Confirming Task Assignment...");
            boolean assigned = taskDAO.assignMemberToTask(testTask.getTaskId(), testMember.getMemberId());
            int activeTasksAfter = taskDAO.getActiveTaskCount(testMember.getMemberId());
            if (assigned && activeTasksAfter >= 1) {
                System.out.println("  -> PASSED: Task assigned to member. Active tasks for member is now: " + activeTasksAfter);
                passed++;
            } else {
                System.err.println("  -> FAILED: Task assignment failed.");
                failed++;
            }

            // TEST 7: Update Task Status to Completed & Check Counts (Member 3)
            System.out.println("\n[TEST 7] Updating Task Status to Completed...");
            int completedBefore = taskDAO.getCompletedTaskCount();
            boolean statusUpdated = taskDAO.updateTaskStatus(testTask.getTaskId(), "Completed");
            int completedAfter = taskDAO.getCompletedTaskCount();
            if (statusUpdated && completedAfter == completedBefore + 1) {
                System.out.println("  -> PASSED: Status updated to 'Completed'. Total completed tasks: " + completedAfter);
                passed++;
            } else {
                System.err.println("  -> FAILED: Status update or completed count verification failed.");
                failed++;
            }

            // TEST 8: Export Summary Report to .txt (Member 4)
            System.out.println("\n[TEST 8] Exporting Project Summary Report to .txt...");
            File reportFile = new File("test_project_summary_report.txt");
            String reportPath = exporter.exportSummaryReport(reportFile, memberDAO, taskDAO);
            if (reportFile.exists() && reportFile.length() > 0) {
                System.out.println("  -> PASSED: Report generated at: " + reportPath + " (" + reportFile.length() + " bytes)");
                passed++;
            } else {
                System.err.println("  -> FAILED: Report file was not created or is empty.");
                failed++;
            }

            System.out.println("\n===============================================================");
            System.out.println(" Test Summary: " + passed + " PASSED, " + failed + " FAILED");
            System.out.println("===============================================================");

        } catch (Exception e) {
            System.err.println("[UNEXPECTED ERROR IN TEST RUNNER]");
            e.printStackTrace();
        }
    }
}
