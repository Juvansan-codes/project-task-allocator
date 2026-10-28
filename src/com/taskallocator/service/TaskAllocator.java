package com.taskallocator.service;

import com.taskallocator.dao.MemberDAO;
import com.taskallocator.dao.TaskDAO;
import com.taskallocator.model.Member;
import com.taskallocator.model.Task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * Member 4 Responsibility: Allocation Recommendation Engine
 * =====================================================================
 * TaskAllocator implements the transparent, explainable allocation logic
 * for second-year OOPS syllabus:
 *
 * Rule 1: Skill Matching
 * - The system queries all team members who possess the skill required by the task.
 *
 * Rule 2: Workload Balancing (Tie-Breaking)
 * - If multiple team members match the required skill, the system counts each
 *   candidate's active tasks (Pending or In Progress).
 * - The member with the fewest active tasks is recommended.
 *
 * Transparent & Human-in-the-loop:
 * - The recommendation is presented to the Project Leader with clear reasoning.
 * - The Project Leader confirms or overrides the final assignment.
 */
public class TaskAllocator {

    /**
     * Inner helper class to hold recommendation output details.
     */
    public static class CandidateWorkload {
        private final Member member;
        private final int activeTaskCount;

        public CandidateWorkload(Member member, int activeTaskCount) {
            this.member = member;
            this.activeTaskCount = activeTaskCount;
        }

        public Member getMember() {
            return member;
        }

        public int getActiveTaskCount() {
            return activeTaskCount;
        }

        @Override
        public String toString() {
            return member.getName() + " (" + activeTaskCount + " active tasks)";
        }
    }

    /**
     * Holds the full outcome of an allocation analysis.
     */
    public static class AllocationResult {
        private final boolean success;
        private final Member recommendedMember;
        private final int recommendedMemberActiveTasks;
        private final String rationale;
        private final List<CandidateWorkload> candidates;

        public AllocationResult(boolean success, Member recommendedMember, 
                                int activeTasks, String rationale, 
                                List<CandidateWorkload> candidates) {
            this.success = success;
            this.recommendedMember = recommendedMember;
            this.recommendedMemberActiveTasks = activeTasks;
            this.rationale = rationale;
            this.candidates = candidates;
        }

        public boolean isSuccess() {
            return success;
        }

        public Member getRecommendedMember() {
            return recommendedMember;
        }

        public int getRecommendedMemberActiveTasks() {
            return recommendedMemberActiveTasks;
        }

        public String getRationale() {
            return rationale;
        }

        public List<CandidateWorkload> getCandidates() {
            return candidates;
        }
    }

    /**
     * Recommends the best team member for a given task.
     *
     * @param task      The task needing allocation
     * @param memberDAO Data access object for members and skills
     * @param taskDAO   Data access object for task workload counts
     * @return AllocationResult with recommendation and full rationale
     * @throws SQLException if a database error occurs during queries
     */
    public AllocationResult recommendMember(Task task, MemberDAO memberDAO, TaskDAO taskDAO) throws SQLException {
        String requiredSkill = task.getRequiredSkill();

        // Step 1: Find all members with matching skill
        List<Member> matchingMembers = memberDAO.getMembersBySkill(requiredSkill);

        if (matchingMembers == null || matchingMembers.isEmpty()) {
            return new AllocationResult(
                false,
                null,
                0,
                "No team members currently possess the required skill: '" + requiredSkill + "'.",
                new ArrayList<>()
            );
        }

        // Step 2: Compute active task count for each matching member
        List<CandidateWorkload> candidateList = new ArrayList<>();
        Member bestMember = null;
        int minWorkload = Integer.MAX_VALUE;

        for (Member m : matchingMembers) {
            int activeTasks = taskDAO.getActiveTaskCount(m.getMemberId());
            candidateList.add(new CandidateWorkload(m, activeTasks));

            // Select candidate with minimum active tasks
            if (activeTasks < minWorkload) {
                minWorkload = activeTasks;
                bestMember = m;
            }
        }

        // Step 3: Format clear rationale for viva explanation
        StringBuilder rationale = new StringBuilder();
        rationale.append("Recommended Member: ").append(bestMember.getName()).append("\n");
        rationale.append("- Required Skill Match: ").append(requiredSkill).append("\n");
        rationale.append("- Active Workload: ").append(minWorkload).append(" active task(s)\n");
        rationale.append("- Evaluated Candidates:\n");
        for (CandidateWorkload cw : candidateList) {
            rationale.append("   * ").append(cw.getMember().getName())
                     .append(": ").append(cw.getActiveTaskCount()).append(" active task(s)\n");
        }

        return new AllocationResult(true, bestMember, minWorkload, rationale.toString(), candidateList);
    }
}
