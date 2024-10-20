package km.ui;

public class ProgressIndicator {
    private final int totalIterations;
    private int currentIteration;

    public ProgressIndicator(int totalIterations) {
        this.totalIterations = totalIterations;
        this.currentIteration = 0;
    }

    public void updateProgress() {
        currentIteration++;
    }

    public double getProgress() {
        return ((double) currentIteration / totalIterations) * 100;
    }

    public int getTotalIterations() {
        return totalIterations;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }
}
