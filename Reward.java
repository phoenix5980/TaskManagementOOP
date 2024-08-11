import java.text.SimpleDateFormat;
import java.util.Date;

public class Reward {
    private String name;
    private int pointsNeeded;
    private boolean isAvailable;
    private String availabilityType;

    // Constructor
    public Reward(String name, int pointsNeeded, String availabilityType) {
        this.name = name;
        this.pointsNeeded = pointsNeeded;
        this.availabilityType = availabilityType;
        this.isAvailable = availabilityType.equals("Always Available");
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPointsNeeded() {
        return pointsNeeded;
    }

    public void setPointsNeeded(int pointsNeeded) {
        this.pointsNeeded = pointsNeeded;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
        this.isAvailable = availabilityType.equals("Always Available");
    }

    // Method to display reward information
    public String displayRewardDetails() {
        return name + " - Points Needed: " + pointsNeeded + " - " + availabilityType;
    }

    // Method to redeem reward
    public String redeemReward() {
        if (isAvailable) {
            return name + " - Redeemed: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        } else {
            setAvailable(false);
            return name + " - Redeemed: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " - No longer available";
        }
    }
}