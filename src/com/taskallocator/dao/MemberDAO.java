package com.taskallocator.dao;

import com.taskallocator.model.Member;
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
 * Member 1 & Member 2 Responsibility: Team Member & Skill Database Access
 * =====================================================================
 * MemberDAO encapsulates all database operations related to team members
 * and their technical skills.
 * Features:
 * - Uses PreparedStatement to prevent SQL injection.
 * - Uses try-with-resources to guarantee automatic closing of database connections.
 * - Returns clean Java objects (Member, ArrayList) to callers.
 */
public class MemberDAO {

    /**
     * Inserts a new team member into the database.
     *
     * @param member The Member object containing name, email, and role
     * @return true if insertion was successful, false otherwise
     * @throws SQLException if a database error occurs (e.g. duplicate email)
     */
    public boolean addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (name, email, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getRole());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        member.setMemberId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Retrieves all team members from the database, along with their recorded skills.
     *
     * @return List of Member objects
     * @throws SQLException if a query error occurs
     */
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT member_id, name, email, role FROM members ORDER BY member_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Member m = new Member(
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
                // Load skills for this member
                m.setSkills(getSkillsForMember(conn, m.getMemberId()));
                members.add(m);
            }
        }
        return members;
    }

    /**
     * Finds a single member by their primary key ID.
     *
     * @param memberId Member ID
     * @return Member object or null if not found
     * @throws SQLException if a query error occurs
     */
    public Member getMemberById(int memberId) throws SQLException {
        String sql = "SELECT member_id, name, email, role FROM members WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member(
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    m.setSkills(getSkillsForMember(conn, memberId));
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * Adds a skill to a member in the member_skills table.
     *
     * @param memberId  ID of the team member
     * @param skillName Name of the skill (e.g., 'Java Programming')
     * @return true if added, false if already exists
     * @throws SQLException if database insert fails
     */
    public boolean addSkillToMember(int memberId, String skillName) throws SQLException {
        String sql = "INSERT IGNORE INTO member_skills (member_id, skill_name) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setString(2, skillName.trim());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves the list of skill names recorded for a specific member.
     *
     * @param memberId ID of the team member
     * @return List of skill name strings
     * @throws SQLException if a query error occurs
     */
    public List<String> getSkillsForMember(int memberId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return getSkillsForMember(conn, memberId);
        }
    }

    /**
     * Internal helper to load skills using an existing open connection.
     */
    private List<String> getSkillsForMember(Connection conn, int memberId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM member_skills WHERE member_id = ? ORDER BY skill_name ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    skills.add(rs.getString("skill_name"));
                }
            }
        }
        return skills;
    }

    /**
     * Finds all team members who possess a specific required skill.
     *
     * @param requiredSkill The skill name to search for
     * @return List of matching Member objects
     * @throws SQLException if query fails
     */
    public List<Member> getMembersBySkill(String requiredSkill) throws SQLException {
        List<Member> matchingMembers = new ArrayList<>();
        String sql = "SELECT m.member_id, m.name, m.email, m.role " +
                     "FROM members m " +
                     "JOIN member_skills ms ON m.member_id = ms.member_id " +
                     "WHERE LOWER(ms.skill_name) = LOWER(?) " +
                     "ORDER BY m.name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, requiredSkill.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Member m = new Member(
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    m.setSkills(getSkillsForMember(conn, m.getMemberId()));
                    matchingMembers.add(m);
                }
            }
        }
        return matchingMembers;
    }

    /**
     * Returns a distinct list of standard skills currently in the system.
     * Useful for populating dropdown combo boxes.
     */
    public List<String> getAvailableSkills() {
        List<String> skills = new ArrayList<>();
        // Standard catalog skills for project tasks
        skills.add("Java Programming");
        skills.add("Swing GUI Design");
        skills.add("MySQL & Relational DB");
        skills.add("Algorithm Design");
        skills.add("Software Testing");
        skills.add("Documentation & Reports");
        return skills;
    }
}
