import java.io.*;
import javax.swing.*;

public class DataManagement {

    public static void loadData(JList<String> taskList, JList<String> historyList, JList<String> rewardList, JList<String> rewardHistoryList, JLabel pointTotalLabel) {
        try {
            loadList("tasks.txt", taskList);
            loadList("taskHistory.txt", historyList);
            loadList("rewards.txt", rewardList);
            loadList("rewardHistory.txt", rewardHistoryList);
            pointTotalLabel.setText("Point Total: " + loadPoints("points.txt"));
        } catch (IOException e) {
            initializeDefaults(taskList, historyList, rewardList, rewardHistoryList, pointTotalLabel);
        }
    }

    private static void initializeDefaults(JList<String> taskList, JList<String> historyList, JList<String> rewardList, JList<String> rewardHistoryList, JLabel pointTotalLabel) {
        // Default tasks
        DefaultListModel<String> taskModel = new DefaultListModel<>();
        taskModel.addElement("Task 1 - Deadline: Tomorrow - Points: 10");
        taskModel.addElement("Task 2 - Deadline: Next Week - Points: 20");
        taskList.setModel(taskModel);

        // Default task history
        DefaultListModel<String> historyModel = new DefaultListModel<>();
        historyList.setModel(historyModel);

        // Default rewards
        DefaultListModel<String> rewardModel = new DefaultListModel<>();
        rewardModel.addElement("Coffee Break - Points Needed: 15");
        rewardModel.addElement("Extended Lunch - Points Needed: 30");
        rewardList.setModel(rewardModel);

        // Default reward history
        DefaultListModel<String> rewardHistoryModel = new DefaultListModel<>();
        rewardHistoryList.setModel(rewardHistoryModel);

        // Default points
        pointTotalLabel.setText("Point Total: 0");
    }
    public static void saveData(JList<String> taskList, JList<String> historyList, JList<String> rewardList, JList<String> rewardHistoryList, int pointTotal, JFrame frame) throws IOException {
        saveList("tasks.txt", taskList);
        saveList("taskHistory.txt", historyList);
        saveList("rewards.txt", rewardList);
        saveList("rewardHistory.txt", rewardHistoryList);
        savePoints("points.txt", pointTotal);
    }

    private static void saveList(String filename, JList<String> list) throws IOException {
        DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (int i = 0; i < model.getSize(); i++) {
                out.println(model.getElementAt(i));
            }
        }
    }

    private static void savePoints(String filename, int points) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println(points);
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

    private static int loadPoints(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return Integer.parseInt(reader.readLine());
        }
    }
}