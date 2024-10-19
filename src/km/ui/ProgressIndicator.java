package km.ui;

public class ProgressIndicator {
    private int totalIterations;
    private int currentIteration;

    public ProgressIndicator(int totalIterations) {
        this.totalIterations = totalIterations;
        this.currentIteration = 0;
    }

    public void updateProgress() {
        currentIteration++;
        double progressPercentage = (double) currentIteration / totalIterations * 100;

        System.out.printf("Progress: %.2f%%\n", progressPercentage);
    }
}
