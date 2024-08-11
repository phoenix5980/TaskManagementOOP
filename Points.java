public class Points {
    private int totalPoints;

    // Constructor
    public Points() {
        this.totalPoints = 0; // Start with 0 points
    }

    public Points(int initialPoints) {
        this.totalPoints = initialPoints; // Initialize with a specific number of points
    }

    // Getter for total points
    public int getTotalPoints() {
        return totalPoints;
    }

    // Method to add points
    public void addPoints(int points) {
        if (points >= 0) {
            this.totalPoints += points;
        } else {
            throw new IllegalArgumentException("Points to add cannot be negative.");
        }
    }

    // Method to subtract points (e.g., when redeeming rewards)
    public void subtractPoints(int points) {
        if (points >= 0) {
            if (this.totalPoints >= points) {
                this.totalPoints -= points;
            } else {
                throw new IllegalArgumentException("Not enough points to subtract.");
            }
        } else {
            throw new IllegalArgumentException("Points to subtract cannot be negative.");
        }
    }

    // Method to reset points to zero
    public void resetPoints() {
        this.totalPoints = 0;
    }

    // Method to check if enough points are available
    public boolean hasEnoughPoints(int requiredPoints) {
        return this.totalPoints >= requiredPoints;
    }
}