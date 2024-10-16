package km.utils;

public class ProgressIndicator {
    private final boolean showProgress;

    public ProgressIndicator(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public void showProgress(int current, int total) {
        if (showProgress) {
            double progress = (double) current / total * 100;
            System.out.printf("Progress: %.2f%%\n", progress);
        }
    }
}

