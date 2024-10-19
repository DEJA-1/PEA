package km.ui;

import km.utils.MemoryMeasurer;
import km.utils.ProgressIndicator;

public class Display {

    public static void displayMemoryUsage(long initialMemory, String algorithmName) {
        long memoryUsed = MemoryMeasurer.getUsedMemory() - initialMemory;
        System.out.println(algorithmName + " - Całkowita zajętość pamięci: " + memoryUsed + " B");
    }

    public static void displayTotalMemoryUsage(long initialMemory) {
        long finalMemory = MemoryMeasurer.getUsedMemory();
        long totalMemoryUsed = finalMemory - initialMemory;
        System.out.println("\nCałkowita zajętość pamięci po uruchomieniu wszystkich algorytmów: " + totalMemoryUsed + " B");
    }

    public static void displayAverageExecutionTime(String algorithmName, long averageTimeNano) {
        long averageTimeMilli = averageTimeNano / 1_000_000;
        System.out.println("\n" + algorithmName + " - Średni czas wykonania: " + averageTimeNano + " ns (" + averageTimeMilli + " ms)");
    }

    public static void displayIterationProgress(String algorithmName, int iteration, int totalExecutions) {
        System.out.println("\n============================");
        System.out.println(algorithmName + " - Iteracja " + iteration);
        new ProgressIndicator(true).showProgress(iteration, totalExecutions); // Wskaźnik postępu
        System.out.println("============================");
    }

    public static void displayExecutionTime(long timeNano) {
        long timeMilli = timeNano / 1_000_000;
        System.out.println("Czas wykonania: " + timeNano + " ns (" + timeMilli + " ms)");
    }
}

