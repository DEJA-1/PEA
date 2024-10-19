package km.ui;

public class ProgressIndicator {
    private int totalIterations;  // Całkowita liczba iteracji dla wszystkich algorytmów
    private int currentIteration; // Aktualna iteracja

    public ProgressIndicator(int totalIterations) {
        this.totalIterations = totalIterations;
        this.currentIteration = 0;
    }

    // Aktualizuje postęp i wyświetla procentowy postęp
    public void updateProgress() {
        currentIteration++;
        double progressPercentage = (double) currentIteration / totalIterations * 100;

        // Wyświetlamy tekstowy postęp w formacie "Progress: X%"
        System.out.printf("Progress: %.2f%%\n", progressPercentage);
    }
}
