import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class DataManagement {

    public static void saveData(List<Task> tasks, List<Reward> rewards, Points pointTotal, JList<String> historyList, JList<String> rewardHistoryList) throws IOException {
        saveTasks(tasks, "tasks.txt");
        saveRewards(rewards, "rewards.txt");
        savePoints(pointTotal.getTotalPoints(), "points.txt");
        saveList(historyList.getModel(), "taskHistory.txt");
        saveList(rewardHistoryList.getModel(), "rewardHistory.txt");
    }

    public static void loadData(List<Task> tasks, List<Reward> rewards, Points pointTotal, JList<String> historyList, JList<String> rewardHistoryList) throws IOException {
        try {        
            tasks.addAll(loadTasks("tasks.txt"));
            rewards.addAll(loadRewards("rewards.txt"));
            pointTotal.addPoints(loadPoints("points.txt"));
            loadList("taskHistory.txt", historyList);
            loadList("rewardHistory.txt", rewardHistoryList);
        } catch (FileNotFoundException e) {
            initializeDefaults();
            // Reload after initializing defaults
            loadData(tasks, rewards, pointTotal, historyList, rewardHistoryList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void initializeDefaults() {
        DefaultListModel<String> taskModel = new DefaultListModel<>();
        DefaultListModel<String> historyModel = new DefaultListModel<>();
        DefaultListModel<String> rewardModel = new DefaultListModel<>();
        DefaultListModel<String> rewardHistoryModel = new DefaultListModel<>();
        taskModel.addElement("Review - Deadline: " + getDefaultDate() + " - Points: 10 - Repeats: Weekly");
        taskModel.addElement("Assignment-5 - Deadline: 12/08/2024 23:59:00 - Points: 4 - Repeats: None");
        taskModel.addElement("Final Exam - Deadline: 15/08/2024 19:00:00 - Points: 100 - Repeats: None");
        historyModel.addElement("Assignment-1 - Deadline: 10/06/2024 23:59:00 - Points: 4");
        historyModel.addElement("Assignment-2 - Deadline: 03/07/2024 23:59:00 - Points: 4");
        historyModel.addElement("Assignment-3 - Deadline: 17/07/2024 23:59:00 - Points: 4");
        historyModel.addElement("Assignment-4 - Deadline: 01/08/2024 23:59:00 - Points: 4");
        rewardModel.addElement("Coffee Break - Points Needed: 5 - Always Available");
        rewardModel.addElement("Movie Night - Points Needed: 20 - One Time Only");
        // Save defaults
        try {
            saveList(taskModel, "tasks.txt");
            saveList(historyModel, "taskHistory.txt");
            saveList(rewardModel, "rewards.txt");
            saveList(rewardHistoryModel, "rewardHistory.txt");
            savePoints(0, "points.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving default data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static String getDefaultDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }
    private static void saveList(ListModel<String> listModel, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (int i = 0; i < listModel.getSize(); i++) {
            writer.write(listModel.getElementAt(i));
            writer.newLine();
        }
        writer.close();
    }

    public static void savePoints(int points, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(Integer.toString(points));  // Save points as an integer string
        }
    }
    public static int loadPoints(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return Integer.parseInt(reader.readLine());  // Parse points as an integer
        } catch (NumberFormatException | IOException e) {
            return 0;  // Return 0 if there's an error or the file is not found
        }
    }

    private static void loadList(String filename, JList<String> list) throws IOException {
        DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
        model.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                model.addElement(line);
            }
        }
    }


    public static void saveTasks(List<Task> tasks, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Task task : tasks) {
                writer.write(task.getTitle() + " - ");
                writer.write("Deadline: " + task.getFormattedDueDate() + " - ");
                writer.write("Points: " + task.getPoints() + " - ");
                writer.write("Repeats: " + (task.isRepetitive() ? task.getRepeatFrequency() : "None"));
                writer.newLine();
            }
        }
    }
    public static List<Task> loadTasks(String fileName) throws IOException {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                String title = parts[0];
                Date dueDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(parts[1].replace("Deadline: ", ""));
                int points = Integer.parseInt(parts[2].replace("Points: ", ""));
                boolean isRepetitive = !parts[3].equals("Repeats: None");
                String repeatFrequency = parts[3].replace("Repeats: ", "");

                Task task = new Task(title, title, dueDate, points, isRepetitive, repeatFrequency);
                tasks.add(task);
            }
        } catch (ParseException e) {
            throw new IOException("Failed to parse the date format.", e);
        }
        return tasks;
    }
    public static void saveRewards(List<Reward> rewards, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Reward reward : rewards) {
                writer.write(reward.getName() + " - ");
                writer.write("Points Needed: " + reward.getPointsNeeded() + " - ");
                writer.write(reward.getAvailabilityType());
                writer.newLine();
            }
        }
    }
    public static List<Reward> loadRewards(String fileName) throws IOException {
        List<Reward> rewards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                String name = parts[0];
                int pointsNeeded = Integer.parseInt(parts[1].replace("Points Needed: ", ""));
                String availabilityType = parts[2];
                
                Reward reward = new Reward(name, pointsNeeded, availabilityType);
                rewards.add(reward);
            }
        }
        return rewards;
    }
}