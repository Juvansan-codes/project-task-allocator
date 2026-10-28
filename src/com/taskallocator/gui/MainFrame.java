package com.taskallocator.gui;

import com.taskallocator.dao.MemberDAO;
import com.taskallocator.dao.TaskDAO;
import com.taskallocator.model.Member;
import com.taskallocator.model.Task;
import com.taskallocator.service.ReportExporter;
import com.taskallocator.service.TaskAllocator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * =====================================================================
 * Member 4 & Integration Responsibility: Main Application Window
 * =====================================================================
 * MainFrame serves as the primary desktop interface for the Project Task &
 * Collaboration Allocator.
 * 
 * Features:
 * - Directly opens on launch without login hurdles.
 * - Simple role toggle (Project Leader vs. Team Member) to demonstrate permissions.
 * - KPI Summary Cards (Total Members, Total Tasks, In Progress, Completed Tasks).
 * - Tab 1: Task Management & Rule-Based Allocation (Member 3 & Member 4).
 * - Tab 2: Team Member & Skill Management (Member 1 & Member 2).
 * - Export Summary Report to formatted .txt file.
 */
public class MainFrame extends JFrame {

    // DAOs and Services
    private final MemberDAO memberDAO;
    private final TaskDAO taskDAO;
    private final TaskAllocator allocator;
    private final ReportExporter reportExporter;

    // Header & Role
    private JComboBox<String> roleSelector;
    private boolean isLeader = true;

    // KPI Labels
    private JLabel lblTotalMembers;
    private JLabel lblTotalTasks;
    private JLabel lblInProgressTasks;
    private JLabel lblCompletedTasks;

    // Tasks Tab Components
    private JTable taskTable;
    private DefaultTableModel taskTableModel;
    private JButton btnCreateTask;
    private JButton btnRecommendAssign;
    private JComboBox<String> cmbStatusUpdate;
    private JButton btnUpdateStatus;
    private JButton btnExportReport;

    // Members Tab Components
    private JTable memberTable;
    private DefaultTableModel memberTableModel;
    private JTextField txtMemberName;
    private JTextField txtMemberEmail;
    private JComboBox<String> cmbMemberRole;
    private JComboBox<String> cmbMemberSkill;
    private JButton btnAddMember;
    private JComboBox<String> cmbAddSkillToExisting;
    private JButton btnAddSkillToMember;

    public MainFrame() {
        super("Project Task & Collaboration Allocator — OOPS Micro-Project");

        this.memberDAO = new MemberDAO();
        this.taskDAO = new TaskDAO();
        this.allocator = new TaskAllocator();
        this.reportExporter = new ReportExporter();

        initComponents();
        loadAllData();
    }

    /**
     * Initializes all UI components and layouts.
     */
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null); // Center on screen

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        contentPane.setBackground(new Color(245, 247, 250));
        setContentPane(contentPane);

        // 1. Top Panel (Header & Role Bar + KPI Cards)
        JPanel topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);
        topContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        topContainer.add(createKpiPanel(), BorderLayout.SOUTH);
        contentPane.add(topContainer, BorderLayout.NORTH);

        // 2. Center Panel (Tabbed Workspace)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("  Tasks & Allocation (Member 3 & 4)  ", createTaskPanel());
        tabbedPane.addTab("  Team Members & Skills (Member 1 & 2)  ", createMemberPanel());
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // 3. Bottom Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setOpaque(false);
        JLabel lblStatus = new JLabel("System Ready | MySQL Connected | 4-Member OOPS Project Team");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 116, 139));
        statusBar.add(lblStatus);
        contentPane.add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Creates top header banner with title and simple role switcher.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(new Color(30, 41, 59)); // Dark Navy
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Title and Subtitle
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Project Task & Collaboration Allocator");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSubtitle = new JLabel("OOPS Micro-Project • Transparent Workload-Balanced Task Distribution");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(203, 213, 225));
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Role Switcher Dropdown (Demonstrates Role-Based Permissions in Viva)
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rolePanel.setOpaque(false);
        JLabel lblRolePrompt = new JLabel("Active Role:");
        lblRolePrompt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRolePrompt.setForeground(Color.WHITE);

        roleSelector = new JComboBox<>(new String[]{
            "Project Leader (Full Access)", 
            "Team Member (View & Status Only)"
        });
        roleSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleSelector.addActionListener(e -> onRoleChanged());

        rolePanel.add(lblRolePrompt);
        rolePanel.add(roleSelector);
        headerPanel.add(rolePanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates the KPI Dashboard Cards summary strip.
     */
    private JPanel createKpiPanel() {
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiPanel.setOpaque(false);

        lblTotalMembers = new JLabel("0", SwingConstants.CENTER);
        lblTotalTasks = new JLabel("0", SwingConstants.CENTER);
        lblInProgressTasks = new JLabel("0", SwingConstants.CENTER);
        lblCompletedTasks = new JLabel("0", SwingConstants.CENTER);

        kpiPanel.add(createCard("Total Members", lblTotalMembers, new Color(59, 130, 246)));
        kpiPanel.add(createCard("Total Tasks", lblTotalTasks, new Color(99, 102, 241)));
        kpiPanel.add(createCard("In Progress", lblInProgressTasks, new Color(245, 158, 11)));
        kpiPanel.add(createCard("Completed Tasks", lblCompletedTasks, new Color(16, 185, 129)));

        return kpiPanel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Builds Tab 1: Task Management & Smart Allocation (Member 3 & Member 4).
     */
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        // Top Actions Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new LineBorder(new Color(226, 232, 240), 1));

        btnCreateTask = new JButton("+ Create Task");
        btnCreateTask.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCreateTask.setBackground(new Color(37, 99, 235));
        btnCreateTask.setForeground(Color.WHITE);
        btnCreateTask.setFocusPainted(false);
        btnCreateTask.addActionListener(e -> showCreateTaskDialog());
        toolbar.add(btnCreateTask);

        btnRecommendAssign = new JButton("Recommend & Assign");
        btnRecommendAssign.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRecommendAssign.setBackground(new Color(13, 148, 136));
        btnRecommendAssign.setForeground(Color.WHITE);
        btnRecommendAssign.setFocusPainted(false);
        btnRecommendAssign.addActionListener(e -> onRecommendAndAssignClicked());
        toolbar.add(btnRecommendAssign);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toolbar.add(lblStatus);

        cmbStatusUpdate = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
        cmbStatusUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toolbar.add(cmbStatusUpdate);

        btnUpdateStatus = new JButton("Update Status");
        btnUpdateStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUpdateStatus.addActionListener(e -> onUpdateTaskStatusClicked());
        toolbar.add(btnUpdateStatus);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        btnExportReport = new JButton("Export Report (.txt)");
        btnExportReport.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnExportReport.addActionListener(e -> onExportReportClicked());
        toolbar.add(btnExportReport);

        JButton btnRefreshTasks = new JButton("Refresh");
        btnRefreshTasks.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshTasks.addActionListener(e -> loadAllData());
        toolbar.add(btnRefreshTasks);

        panel.add(toolbar, BorderLayout.NORTH);

        // Center Table
        String[] taskColumns = {"ID", "Task Name", "Required Skill", "Hours", "Status", "Assigned Member"};
        taskTableModel = new DefaultTableModel(taskColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        taskTable = new JTable(taskTableModel);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taskTable.setRowHeight(24);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        taskTable.getTableHeader().setBackground(new Color(241, 245, 249));

        // Column widths
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(230);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(170);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(140);

        // Center align ID, Hours, Status
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        taskTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        taskTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        taskTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds Tab 2: Team Member & Skill Management (Member 1 & Member 2).
     */
    private JPanel createMemberPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        // Top Form: Add New Member
        JPanel addMemberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        addMemberPanel.setBackground(Color.WHITE);
        addMemberPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(226, 232, 240)), 
            "Add New Team Member (Member 1)", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 12)
        ));

        addMemberPanel.add(new JLabel("Name:"));
        txtMemberName = new JTextField(12);
        addMemberPanel.add(txtMemberName);

        addMemberPanel.add(new JLabel("Email:"));
        txtMemberEmail = new JTextField(14);
        addMemberPanel.add(txtMemberEmail);

        addMemberPanel.add(new JLabel("Role:"));
        cmbMemberRole = new JComboBox<>(new String[]{"Team Member", "Project Leader"});
        addMemberPanel.add(cmbMemberRole);

        addMemberPanel.add(new JLabel("Initial Skill:"));
        cmbMemberSkill = new JComboBox<>(memberDAO.getAvailableSkills().toArray(new String[0]));
        addMemberPanel.add(cmbMemberSkill);

        btnAddMember = new JButton("Add Member");
        btnAddMember.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddMember.setBackground(new Color(37, 99, 235));
        btnAddMember.setForeground(Color.WHITE);
        btnAddMember.addActionListener(e -> onAddMemberClicked());
        addMemberPanel.add(btnAddMember);

        panel.add(addMemberPanel, BorderLayout.NORTH);

        // Center Table: All Members and their Skills
        String[] memberColumns = {"ID", "Name", "Email", "Role", "Recorded Skills", "Active Tasks"};
        memberTableModel = new DefaultTableModel(memberColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(memberTableModel);
        memberTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        memberTable.setRowHeight(24);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        memberTable.getTableHeader().setBackground(new Color(241, 245, 249));

        memberTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        memberTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        memberTable.getColumnModel().getColumn(2).setPreferredWidth(190);
        memberTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        memberTable.getColumnModel().getColumn(4).setPreferredWidth(260);
        memberTable.getColumnModel().getColumn(5).setPreferredWidth(90);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        memberTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        memberTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(memberTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Form: Add Skill to Selected Member
        JPanel addSkillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        addSkillPanel.setBackground(Color.WHITE);
        addSkillPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(226, 232, 240)), 
            "Add Skill to Selected Member", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 12)
        ));

        addSkillPanel.add(new JLabel("Select Skill to Add:"));
        cmbAddSkillToExisting = new JComboBox<>(memberDAO.getAvailableSkills().toArray(new String[0]));
        addSkillPanel.add(cmbAddSkillToExisting);

        btnAddSkillToMember = new JButton("Attach Skill");
        btnAddSkillToMember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAddSkillToMember.addActionListener(e -> onAttachSkillToMemberClicked());
        addSkillPanel.add(btnAddSkillToMember);

        JButton btnRefreshMembers = new JButton("Refresh List");
        btnRefreshMembers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshMembers.addActionListener(e -> loadAllData());
        addSkillPanel.add(btnRefreshMembers);

        panel.add(addSkillPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Refreshes all tables and KPI summary metrics from MySQL.
     */
    public void loadAllData() {
        try {
            // 1. Load Tasks
            List<Task> tasks = taskDAO.getAllTasks();
            taskTableModel.setRowCount(0);
            for (Task t : tasks) {
                taskTableModel.addRow(new Object[]{
                    t.getTaskId(),
                    t.getTaskName(),
                    t.getRequiredSkill(),
                    t.getEstimatedHours(),
                    t.getStatus(),
                    t.getAssignedMemberName()
                });
            }

            // 2. Load Members
            List<Member> members = memberDAO.getAllMembers();
            memberTableModel.setRowCount(0);
            for (Member m : members) {
                int activeCount = taskDAO.getActiveTaskCount(m.getMemberId());
                memberTableModel.addRow(new Object[]{
                    m.getMemberId(),
                    m.getName(),
                    m.getEmail(),
                    m.getRole(),
                    m.getSkillsFormatted(),
                    activeCount
                });
            }

            // 3. Update KPI Labels
            lblTotalMembers.setText(String.valueOf(members.size()));
            lblTotalTasks.setText(String.valueOf(taskDAO.getTotalTaskCount()));
            lblInProgressTasks.setText(String.valueOf(taskDAO.getInProgressTaskCount()));
            lblCompletedTasks.setText(String.valueOf(taskDAO.getCompletedTaskCount()));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error while loading data:\n" + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Role Switcher Event: Adjusts button availability according to role.
     */
    private void onRoleChanged() {
        isLeader = roleSelector.getSelectedIndex() == 0;
        btnCreateTask.setEnabled(isLeader);
        btnRecommendAssign.setEnabled(isLeader);
        btnAddMember.setEnabled(isLeader);
        btnAddSkillToMember.setEnabled(isLeader);
    }

    /**
     * Event: Displays a dialog to input and create a new task.
     */
    private void showCreateTaskDialog() {
        JTextField txtTitle = new JTextField();
        JComboBox<String> cmbSkill = new JComboBox<>(memberDAO.getAvailableSkills().toArray(new String[0]));
        JTextField txtHours = new JTextField("8");

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Task Name:"));
        form.add(txtTitle);
        form.add(new JLabel("Required Skill:"));
        form.add(cmbSkill);
        form.add(new JLabel("Estimated Hours:"));
        form.add(txtHours);

        int result = JOptionPane.showConfirmDialog(this, form, 
                "Create New Project Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = txtTitle.getText().trim();
            String skill = (String) cmbSkill.getSelectedItem();
            String hoursStr = txtHours.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Task name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int hours;
            try {
                hours = Integer.parseInt(hoursStr);
                if (hours <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number for hours.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Task newTask = new Task(title, skill, hours);
            try {
                boolean success = taskDAO.createTask(newTask);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Task created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create task.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Event: Triggers allocation recommendation engine for selected task.
     */
    private void onRecommendAndAssignClicked() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a task from the table to recommend and assign.", 
                "No Task Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int taskId = (int) taskTableModel.getValueAt(selectedRow, 0);
        String taskName = (String) taskTableModel.getValueAt(selectedRow, 1);
        String requiredSkill = (String) taskTableModel.getValueAt(selectedRow, 2);
        int hours = (int) taskTableModel.getValueAt(selectedRow, 3);
        String status = (String) taskTableModel.getValueAt(selectedRow, 4);

        Task task = new Task(taskId, taskName, requiredSkill, hours, status, null, null);

        try {
            // Run Member 4's Allocation Recommendation
            TaskAllocator.AllocationResult result = allocator.recommendMember(task, memberDAO, taskDAO);

            if (!result.isSuccess()) {
                JOptionPane.showMessageDialog(this, 
                    result.getRationale(), 
                    "No Matching Candidates", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create recommendation confirmation dialog
            JPanel dialogPanel = new JPanel(new BorderLayout(0, 10));
            dialogPanel.setPreferredSize(new Dimension(450, 220));

            JTextArea txtRationale = new JTextArea(result.getRationale());
            txtRationale.setEditable(false);
            txtRationale.setFont(new Font("Monospaced", Font.PLAIN, 12));
            txtRationale.setBackground(new Color(248, 250, 252));
            txtRationale.setBorder(new LineBorder(new Color(203, 213, 225)));
            dialogPanel.add(new JScrollPane(txtRationale), BorderLayout.CENTER);

            // Choice dropdown to confirm recommended or choose another candidate
            JPanel choicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            choicePanel.add(new JLabel("Assign to:"));
            JComboBox<TaskAllocator.CandidateWorkload> cmbCandidates = new JComboBox<>();
            for (TaskAllocator.CandidateWorkload cw : result.getCandidates()) {
                cmbCandidates.addItem(cw);
            }
            choicePanel.add(cmbCandidates);
            dialogPanel.add(choicePanel, BorderLayout.SOUTH);

            int choice = JOptionPane.showConfirmDialog(this, dialogPanel, 
                "Confirm Task Assignment — Human-in-the-Loop", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.OK_OPTION) {
                TaskAllocator.CandidateWorkload selectedCandidate = 
                    (TaskAllocator.CandidateWorkload) cmbCandidates.getSelectedItem();
                if (selectedCandidate != null) {
                    int memberId = selectedCandidate.getMember().getMemberId();
                    boolean assigned = taskDAO.assignMemberToTask(taskId, memberId);
                    if (assigned) {
                        JOptionPane.showMessageDialog(this, 
                            "Task successfully assigned to " + selectedCandidate.getMember().getName() + "!", 
                            "Assignment Confirmed", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadAllData();
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Allocation query error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Event: Updates task status (Pending, In Progress, Completed).
     */
    private void onUpdateTaskStatusClicked() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task from the table to update status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int taskId = (int) taskTableModel.getValueAt(selectedRow, 0);
        String newStatus = (String) cmbStatusUpdate.getSelectedItem();

        try {
            boolean updated = taskDAO.updateTaskStatus(taskId, newStatus);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Task status updated to '" + newStatus + "'!", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                loadAllData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Event: Exports plain-text report summarizing project.
     */
    private void onExportReportClicked() {
        File defaultFile = new File("project_summary_report.txt");
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setSelectedFile(defaultFile);
        fileChooser.setDialogTitle("Save Project Summary Report");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            try {
                String path = reportExporter.exportSummaryReport(targetFile, memberDAO, taskDAO);
                JOptionPane.showMessageDialog(this, 
                    "Report exported successfully!\nLocation: " + path, 
                    "Report Exported", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to export report: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Event: Adds a new team member.
     */
    private void onAddMemberClicked() {
        String name = txtMemberName.getText().trim();
        String email = txtMemberEmail.getText().trim();
        String role = (String) cmbMemberRole.getSelectedItem();
        String skill = (String) cmbMemberSkill.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both name and email.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Member m = new Member(name, email, role);
        try {
            boolean success = memberDAO.addMember(m);
            if (success) {
                if (skill != null && !skill.trim().isEmpty()) {
                    memberDAO.addSkillToMember(m.getMemberId(), skill);
                }
                JOptionPane.showMessageDialog(this, "Team member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtMemberName.setText("");
                txtMemberEmail.setText("");
                loadAllData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding member (check if email is unique):\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Event: Attaches an additional skill to the selected member.
     */
    private void onAttachSkillToMemberClicked() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to attach a skill.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) memberTableModel.getValueAt(selectedRow, 0);
        String memberName = (String) memberTableModel.getValueAt(selectedRow, 1);
        String skill = (String) cmbAddSkillToExisting.getSelectedItem();

        try {
            boolean added = memberDAO.addSkillToMember(memberId, skill);
            if (added) {
                JOptionPane.showMessageDialog(this, "Skill '" + skill + "' attached to " + memberName + "!", "Skill Added", JOptionPane.INFORMATION_MESSAGE);
                loadAllData();
            } else {
                JOptionPane.showMessageDialog(this, memberName + " already has the skill: " + skill, "Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
