import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

public class TaskManagementApp {
    private static List<Task> tasks = new ArrayList<>();
    private static List<Reward> rewards = new ArrayList<>();
    private static Points pointTotal = new Points();
    private static JPanel todoPanel;
    private static JFrame frame;
    private static JList<String> taskList = new JList<>(new DefaultListModel<>());
    private static JList<String> rewardList = new JList<>(new DefaultListModel<>());
    private static JList<String> historyList = new JList<>(new DefaultListModel<>());
    private static JList<String> rewardHistoryList = new JList<>(new DefaultListModel<>());
    private static JLabel pointTotalLabel = new JLabel("Point Total: 0");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManagementApp::initializeUI);
    }

    private static void initializeUI() {
        frame = new JFrame("Task Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    DataManagement.saveData(tasks, rewards, pointTotal, historyList, rewardHistoryList);
                    System.exit(0);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(taskList);
        frame.add(scrollPane, BorderLayout.CENTER);
        setupTabs();
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(pointTotalLabel, BorderLayout.WEST);
        frame.add(headerPanel, BorderLayout.NORTH);
        loadData();
        frame.setVisible(true);
    }

    private static void setupTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        todoPanel = createToDoListPanel();
        tabbedPane.addTab("To Do List", createToDoListPanel());
        tabbedPane.addTab("Task History", createTaskHistoryPanel());
        tabbedPane.addTab("Rewards", createRewardsPanel());
        frame.add(tabbedPane, BorderLayout.CENTER);   
        // Add a change listener to refresh the display when the tab is switched
        tabbedPane.addChangeListener(e -> {
            updateTaskListDisplay();  // Ensure tasks are correctly displayed when tabs are switched
            updatePointsDisplay();    // Ensure points are updated as well
        });
    }
    
    private static void loadData() {
        try {
            DataManagement.loadData(tasks, rewards, pointTotal, historyList, rewardHistoryList);
            updateTaskListDisplay();
            updateRewardListDisplay();
            updatePointsDisplay();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading data: " + e.getMessage());
        }
    }
    
    private static void updatePointsDisplay() {
        pointTotalLabel.setText("Point Total: " + pointTotal.getTotalPoints());
    }
    private static JPanel createToDoListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
    
        // Adding top panel with + and - buttons
        JPanel topPanel = new JPanel();
        JButton addTaskButton = new JButton("+");
        JButton deleteTaskButton = new JButton("-");
        addTaskButton.addActionListener(e -> addNewTask());
        deleteTaskButton.addActionListener(e -> deleteSelectedTask(taskList));
        topPanel.add(addTaskButton);
        topPanel.add(deleteTaskButton);
        panel.add(topPanel, BorderLayout.NORTH);
    
        JScrollPane scrollPane = new JScrollPane(taskList);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        // Adding Complete Task button at the bottom
        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(e -> completeTask());
        panel.add(completeButton, BorderLayout.SOUTH);
    
        return panel;
    }
    
    private static void addNewTask() {
        JPanel taskInputPanel = new JPanel(new GridLayout(0, 2));
        JTextField descField = new JTextField();
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy HH:mm:ss"));
        JTextField pointsField = new JTextField();
        
        JCheckBox isRepetitive = new JCheckBox("Repetitive");
        JComboBox<String> repeatOptions = new JComboBox<>(new String[]{"None", "Daily", "Weekly", "Monthly", "Custom"});
        repeatOptions.setEnabled(false);  // Initially disabled
        
        // Only enable repetition options if the checkbox is checked
        isRepetitive.addActionListener(e -> repeatOptions.setEnabled(isRepetitive.isSelected()));
        
        taskInputPanel.add(new JLabel("Description:"));
        taskInputPanel.add(descField);
        taskInputPanel.add(new JLabel("Deadline:"));
        taskInputPanel.add(dateSpinner);
        taskInputPanel.add(new JLabel("Points:"));
        taskInputPanel.add(pointsField);
        taskInputPanel.add(isRepetitive);
        taskInputPanel.add(repeatOptions);
        
        int result = JOptionPane.showConfirmDialog(null, taskInputPanel, "Enter Task Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String description = descField.getText();
            Date dueDate = (Date) dateSpinner.getValue();
            int points = Integer.parseInt(pointsField.getText());
            boolean repetitive = isRepetitive.isSelected();
            String repeatFreq = repetitive ? repeatOptions.getSelectedItem().toString() : "None";
            
            // Create the task object
            Task newTask = new Task(description, description, dueDate, points, repetitive, repeatFreq);
            tasks.add(newTask);  // Add to the tasks list
            
            updateTaskListDisplay();
        }
    }
    private static void updateTaskListDisplay() {
        DefaultListModel<String> taskListModel = (DefaultListModel<String>) taskList.getModel();
        taskListModel.clear();
        for (Task task : tasks) {
            taskListModel.addElement(task.displayTaskDetails());
        }
    }
    private static void completeTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a task to complete.");
            return;
        }
    
        String task = ((DefaultListModel<String>) taskList.getModel()).getElementAt(selectedIndex);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String completionTime = dateFormat.format(new Date());
    
        try {
            // Extract points and repetition info correctly
            Matcher matcher = Pattern.compile("Points: (\\d+) - Repeats: (\\w+)").matcher(task);
            if (matcher.find()) {
                int points = Integer.parseInt(matcher.group(1));
                String repeats = matcher.group(2);
                pointTotal.addPoints(points);
                pointTotalLabel.setText("Point Total: " + pointTotal.getTotalPoints());
    
                // Remove repetition info for history
                String taskForHistory = task.replaceAll(" - Repeats: \\w+", "") + " - Completed: " + completionTime;
                ((DefaultListModel<String>) historyList.getModel()).addElement(taskForHistory);
                Task completedTask = tasks.get(selectedIndex);
                // Check if the task is repetitive and ask for confirmation
                if (completedTask.isRepetitive()) {
                    System.out.println("Task is repetitive");
                    int response = JOptionPane.showConfirmDialog(frame, "Keep this task repeating?", "Repetitive Task", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        // Only modify deadline part

                        Date nextDate = calculateNextDate(dateFormat.parse(task.split(" - Deadline: ")[1].split(" - ")[0]), repeats);
                        String newDeadline = " - Deadline: " + dateFormat.format(nextDate);
                        String newTask = task.substring(0, task.indexOf(" - Deadline:")) + newDeadline + task.substring(task.indexOf(" - Points:"));
                        ((DefaultListModel<String>) taskList.getModel()).set(selectedIndex, newTask);
                    }else{
                        tasks.remove(selectedIndex);
                        ((DefaultListModel<String>) taskList.getModel()).remove(selectedIndex);
                        updateTaskListDisplay(); 
                    }
                }else{
                    System.out.println("Task is not repetitive");
                    tasks.remove(selectedIndex);
                    ((DefaultListModel<String>) taskList.getModel()).remove(selectedIndex);
                    updateTaskListDisplay(); 
                }


            } else {
                throw new NumberFormatException("Points format incorrect or missing repetition info");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error handling task: " + e.getMessage());
        }
    }
    
    private static void regenerateTaskIfRepetitive(String task, SimpleDateFormat dateFormat) {
        try {
            String deadlinePart = task.split(" - Deadline: ")[1].split(" - ")[0];
            System.out.println("Attempting to parse date: " + deadlinePart);  // Debug output to see what date is being parsed
            Date currentDate = dateFormat.parse(deadlinePart);
            String repeats = task.split(" - Repeats: ")[1].split(" - ")[0];
            if (!"None".equals(repeats)) {
                Date nextDate = calculateNextDate(currentDate, repeats);
                String newTask = task.substring(0, task.indexOf(" - Points:")) + " - Deadline: " + dateFormat.format(nextDate) + task.substring(task.indexOf(" - Points:"));
                ((DefaultListModel<String>) taskList.getModel()).addElement(newTask);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(frame, "Failed to parse the date for repetitive task: " + e.getMessage());
            System.out.println("Date parse error: " + e.getMessage());  // More detailed debug output
        }
    }
    
    private static Date calculateNextDate(Date currentDate, String repeats) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        switch (repeats) {
            case "Daily":
                cal.add(Calendar.DATE, 1);
                break;
            case "Weekly":
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "Monthly":
                cal.add(Calendar.MONTH, 1);
                break;
        }
        return cal.getTime();
    }

    private static JPanel createTaskHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
    
        // Adding top panel with - button
        JPanel topPanel = new JPanel();
        JButton deleteHistoryButton = new JButton("-");
        deleteHistoryButton.addActionListener(e -> deleteSelectedTask(historyList));
        topPanel.add(deleteHistoryButton);
        panel.add(topPanel, BorderLayout.NORTH);
    
        JScrollPane scrollPane = new JScrollPane(historyList);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        return panel;
    }
    private static void deleteSelectedTask(JList<String> list) {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            // Show confirmation dialog
            int response = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete this?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
    
            if (response == JOptionPane.YES_OPTION) {
                ((DefaultListModel<String>) list.getModel()).remove(selectedIndex);
                if (list == taskList) {  // If the list is the task list
                    tasks.remove(selectedIndex);  // Remove the task from the tasks list
                    try {
                        DataManagement.saveData(tasks, rewards, pointTotal, historyList, rewardHistoryList);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage());
                    }
                }
                updateTaskListDisplay();  // Update the task list display
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No task selected for deletion.");
        }
    }

    private static JPanel createRewardsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create a top panel for the buttons and label
        JPanel topPanel = new JPanel(new GridBagLayout()); // Using GridBagLayout for better control over positioning
        GridBagConstraints gbc = new GridBagConstraints();

        // Position for the label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(pointTotalLabel, gbc);

        // Position for the Add Reward button
        JButton addRewardButton = new JButton("+");
        addRewardButton.addActionListener(e -> addNewReward());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(addRewardButton, gbc);

        // Position for the Delete Reward button
        JButton deleteRewardButton = new JButton("-");
        deleteRewardButton.addActionListener(e -> deleteSelectedTask(rewardList));
        gbc.gridx = 2;
        gbc.gridy = 0;
        topPanel.add(deleteRewardButton, gbc);

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
        JComboBox<String> availabilityOptions = new JComboBox<>(new String[]{"Always Available", "One Time Only"});
        
        rewardInputPanel.add(new JLabel("Reward Name:"));
        rewardInputPanel.add(nameField);
        rewardInputPanel.add(new JLabel("Points Needed:"));
        rewardInputPanel.add(pointsNeededField);
        rewardInputPanel.add(new JLabel("Availability:"));
        rewardInputPanel.add(availabilityOptions);
        
        int result = JOptionPane.showConfirmDialog(null, rewardInputPanel, "Add New Reward", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            int pointsNeeded = Integer.parseInt(pointsNeededField.getText());
            String availabilityType = (String) availabilityOptions.getSelectedItem();
            
            Reward newReward = new Reward(name, pointsNeeded, availabilityType);
            rewards.add(newReward);  // Add to the rewards list
            
            updateRewardListDisplay();
        }
    }
    

    private static void updateRewardListDisplay() {
        DefaultListModel<String> rewardListModel = (DefaultListModel<String>) rewardList.getModel();
        rewardListModel.clear();
        for (Reward reward : rewards) {
            rewardListModel.addElement(reward.displayRewardDetails());
        }
    }
    private static void redeemReward() {
        int selectedIndex = rewardList.getSelectedIndex();
        if (selectedIndex != -1) {
            Reward reward = rewards.get(selectedIndex);
            if (pointTotal.getTotalPoints() >= reward.getPointsNeeded()) {
                pointTotal.subtractPoints(reward.getPointsNeeded());
                pointTotalLabel.setText("Point Total: " + pointTotal.getTotalPoints());
                
                String redemptionDetails = reward.redeemReward();
                ((DefaultListModel<String>) rewardHistoryList.getModel()).addElement(redemptionDetails);
                
                if (!reward.isAvailable()) {
                    rewards.remove(selectedIndex);
                }
                
                updateRewardListDisplay();
            } else {
                JOptionPane.showMessageDialog(frame, "Not enough points to redeem this reward.", "Insufficient Points", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}