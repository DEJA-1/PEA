package km.ui;

import km.utils.MemoryMeasurer;

public class Display {

    public static void displayExecutionTime(long timeNano) {
        long timeMilli = timeNano / 1_000_000;
        System.out.println("Czas wykonania: " + timeNano + " ns (" + timeMilli + " ms)");
    }

    public static void displayTotalMemoryUsage(long initialMemory) {
        long finalMemory = MemoryMeasurer.getUsedMemory();
        long totalMemoryUsed = finalMemory - initialMemory;
        System.out.println("\nCałkowita zajętość pamięci po uruchomieniu wszystkich algorytmów: " + totalMemoryUsed + " B");
    }

    public static void printIterationSeparator(String algorithmName, int iteration) {
        System.out.println("\n============================");
        System.out.println(algorithmName + " - Iteracja " + iteration);
    }

    public static void printSeparator() {
        System.out.println("\n==================== PODSUMOWANIE ====================\n");
    }

    public static void printSummary(String summary) {
        System.out.println(summary);
    }
}
