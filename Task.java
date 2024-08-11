import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Task {
    private String title;
    private String description;
    private Date dueDate;
    private int points;
    private boolean isCompleted;
    private boolean isRepetitive;
    private String repeatFrequency;

    // Constructor
    public Task(String title, String description, Date dueDate, int points, boolean isRepetitive, String repeatFrequency) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.points = points;
        this.isCompleted = false;
        this.isRepetitive = isRepetitive;
        this.repeatFrequency = repeatFrequency != null ? repeatFrequency : "None";
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getFormattedDueDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //System.out.println(dueDate);
        return sdf.format(dueDate);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isRepetitive() {
        return isRepetitive;
    }

    public void setRepetitive(boolean repetitive) {
        isRepetitive = repetitive;
    }

    public String getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(String repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    // Method to display task information
    public String displayTaskDetails() {
        return title + " - Deadline: " + getFormattedDueDate() + " - Points: " + points +
                " - Repeats: " + (isRepetitive ? repeatFrequency : "None");
    }
    
    // Method to update the due date for repetitive tasks
    public void updateDueDate() {
        if (!isRepetitive) return;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        
        switch (repeatFrequency) {
            case "Daily":
                cal.add(Calendar.DATE, 1);
                break;
            case "Weekly":
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "Monthly":
                cal.add(Calendar.MONTH, 1);
                break;
            // Add more cases if necessary
        }
        
        this.dueDate = cal.getTime();
    }
}