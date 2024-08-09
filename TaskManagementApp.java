import java.awt.*;
import javax.swing.*;

public class TaskManagementApp {
    private static JFrame frame;
    private static JList<String> taskList = new JList<>(new DefaultListModel<>());
    private static JList<String> historyList = new JList<>(new DefaultListModel<>());
    private static JList<String> rewardList = new JList<>(new DefaultListModel<>());
    private static JList<String> rewardHistoryList = new JList<>(new DefaultListModel<>());
    private static JLabel pointTotalLabel = new JLabel("Point Total: 0");
    private static int pointTotal = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManagementApp::initializeUI);
    }

    private static void initializeUI() {
        frame = new JFrame("Task Management System");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    DataManagement.saveData(taskList, historyList, rewardList, rewardHistoryList, pointTotal, frame);
                    System.exit(0);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage());
                }
            }
        });

        setupTabs();
        loadData();
        frame.setVisible(true);
    }

    private static void setupTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("To Do List", createToDoListPanel());
        tabbedPane.addTab("Task History", createTaskHistoryPanel());
        tabbedPane.addTab("Rewards", createRewardsPanel());
        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    private static void loadData() {
        try {
            DataManagement.loadData(taskList, historyList, rewardList, rewardHistoryList, pointTotalLabel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading data: " + e.getMessage());
        }
    }

    private static JPanel createToDoListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(pointTotalLabel, BorderLayout.WEST);

        JButton addTaskButton = new JButton("+");
        addTaskButton.addActionListener(e -> addNewTask());
        topPanel.add(addTaskButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        taskList.setModel(new DefaultListModel<>());
        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(e -> completeTask());
        panel.add(completeButton, BorderLayout.SOUTH);

        return panel;
    }

    private static void addNewTask() {
        JPanel taskInputPanel = new JPanel(new GridLayout(0, 2));
        JTextField descField = new JTextField();
        JTextField deadlineField = new JTextField();
        JTextField pointsField = new JTextField();
        taskInputPanel.add(new JLabel("Description:"));
        taskInputPanel.add(descField);
        taskInputPanel.add(new JLabel("Deadline:"));
        taskInputPanel.add(deadlineField);
        taskInputPanel.add(new JLabel("Points:"));
        taskInputPanel.add(pointsField);

        int result = JOptionPane.showConfirmDialog(null, taskInputPanel, "Enter Task Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String taskEntry = descField.getText() + " - Deadline: " + deadlineField.getText() + " - Points: " + pointsField.getText();
            ((DefaultListModel<String>) taskList.getModel()).addElement(taskEntry);
        }
    }

    private static void completeTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String task = ((DefaultListModel<String>) taskList.getModel()).getElementAt(selectedIndex);
            int points = Integer.parseInt(task.split(" - Points: ")[1].trim());
            pointTotal += points;
            pointTotalLabel.setText("Point Total: " + pointTotal);
            ((DefaultListModel<String>) historyList.getModel()).addElement(task);
            ((DefaultListModel<String>) taskList.getModel()).remove(selectedIndex);
        }
    }

    private static JPanel createTaskHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(historyList), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createRewardsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(pointTotalLabel, BorderLayout.WEST);

        JButton addRewardButton = new JButton("+");
        addRewardButton.addActionListener(e -> addNewReward());
        topPanel.add(addRewardButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(rewardList), new JScrollPane(rewardHistoryList));
        panel.add(splitPane, BorderLayout.CENTER);

        JButton redeemRewardButton = new JButton("Redeem Reward");
        redeemRewardButton.addActionListener(e -> redeemReward());
        panel.add(redeemRewardButton, BorderLayout.SOUTH);

        return panel;
    }

    private static void addNewReward() {
        JPanel rewardInputPanel = new JPanel(new GridLayout(0, 2));
        JTextField nameField = new JTextField();
        JTextField pointsNeededField = new JTextField();
        rewardInputPanel.add(new JLabel("Reward Name:"));
        rewardInputPanel.add(nameField);
        rewardInputPanel.add(new JLabel("Points Needed:"));
        rewardInputPanel.add(pointsNeededField);

        int result = JOptionPane.showConfirmDialog(null, rewardInputPanel, "Add New Reward", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String rewardEntry = nameField.getText() + " - Points Needed: " + pointsNeededField.getText();
            ((DefaultListModel<String>) rewardList.getModel()).addElement(rewardEntry);
        }
    }

    private static void redeemReward() {
        int selectedIndex = rewardList.getSelectedIndex();
        if (selectedIndex != -1) {
            String reward = rewardList.getModel().getElementAt(selectedIndex);
            int pointsNeeded = Integer.parseInt(reward.split(" - Points Needed: ")[1].trim());
            if (pointTotal >= pointsNeeded) {
                pointTotal -= pointsNeeded;
                pointTotalLabel.setText("Point Total: " + pointTotal);
                ((DefaultListModel<String>) rewardHistoryList.getModel()).addElement(reward);
                ((DefaultListModel<String>) rewardList.getModel()).remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(frame, "Not enough points to redeem this reward.", "Insufficient Points", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}