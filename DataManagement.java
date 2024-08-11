import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class DataManagement {

    public static void saveData(JList<String> taskList, JList<String> historyList, JList<String> rewardList, JList<String> rewardHistoryList, int pointTotal, JFrame frame) throws IOException {
        saveList(taskList.getModel(), "tasks.txt");
        saveList(historyList.getModel(), "taskHistory.txt");
        saveList(rewardList.getModel(), "rewards.txt");
        saveList(rewardHistoryList.getModel(), "rewardHistory.txt");
        savePoints(Integer.toString(pointTotal), "points.txt");
    }

    public static void loadData(JList<String> taskList, JList<String> historyList, JList<String> rewardList, JList<String> rewardHistoryList, JLabel pointTotalLabel) throws IOException {
        try {
            loadList("tasks.txt", taskList);
            loadList("taskHistory.txt", historyList);
            loadList("rewards.txt", rewardList);
            loadList("rewardHistory.txt", rewardHistoryList);
            int points = loadPoints("points.txt");
            pointTotalLabel.setText("Point Total: " + points);
        } catch (FileNotFoundException e) {
            initializeDefaults(taskList, historyList, rewardList, rewardHistoryList, pointTotalLabel);
        }
    }

    private static void initializeDefaults(JList<String> taskList, JList<String> historyList, JList<String> rewardList, JList<String> rewardHistoryList, JLabel pointTotalLabel) {
        // Default tasks with repeat options
        DefaultListModel<String> taskModel = new DefaultListModel<>();
        taskModel.addElement("Example Task - Deadline: " + getDefaultDate() + " - Points: 10 - Repeats: Daily");
        taskList.setModel(taskModel);

        // Default task history
        DefaultListModel<String> historyModel = new DefaultListModel<>();
        historyList.setModel(historyModel);

        // Default rewards with availability
        DefaultListModel<String> rewardModel = new DefaultListModel<>();
        rewardModel.addElement("Coffee Break - Points Needed: 5 - Always Available");
        rewardList.setModel(rewardModel);

        // Default reward history
        DefaultListModel<String> rewardHistoryModel = new DefaultListModel<>();
        rewardHistoryList.setModel(rewardHistoryModel);

        // Default points
        pointTotalLabel.setText("Point Total: 0");
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

    private static void savePoints(String points, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(points);
        writer.close();
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