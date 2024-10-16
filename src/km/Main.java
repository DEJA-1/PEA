package km;

import km.algorithms.BruteForce;
import km.algorithms.NearestNeighbour;
import km.algorithms.Random;
import km.data.FileLoader;
import km.model.TSPProblem;
import km.utils.MemoryMeasurer;
import km.utils.ProgressIndicator;
import km.utils.TimeMeasurer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String rootPath = "C:\\Users\\allow\\OneDrive\\Dokumenty\\";
            String matrixFileName = "matrix_11x11.txt";
            int[][] distanceMatrix = FileLoader.loadMatrixFromFile(rootPath + matrixFileName);
            TSPProblem problem = new TSPProblem(distanceMatrix);

            int executions = 10; // Liczba wykonanych powtórzeń każdego algorytmu
            ProgressIndicator progressIndicator = new ProgressIndicator(true); // Włączony wskaźnik postępu

            // Pomiary pamięci przed uruchomieniem algorytmów
            long initialMemory = MemoryMeasurer.getUsedMemory();

            // ********** Brute Force **********
            BruteForce bruteForce = new BruteForce(problem);
            long totalTimeBruteForce = 0;

            for (int i = 0; i < executions; i++) {
                System.out.println("\n============================");
                System.out.println("Brute Force - Iteracja " + (i + 1));
                progressIndicator.showProgress(i + 1, executions); // Wyświetlanie paska postępu
                System.out.println("============================");

                long timeNano = TimeMeasurer.measureAlgorithmTime(bruteForce);  // Czas w nanosekundach
                long timeMilli = timeNano / 1_000_000;                         // Przeliczenie na milisekundy
                System.out.println("Czas wykonania: " + timeNano + " ns (" + timeMilli + " ms)");

                totalTimeBruteForce += timeNano;
            }
            long averageTimeBruteForceNano = totalTimeBruteForce / executions;
            long averageTimeBruteForceMilli = averageTimeBruteForceNano / 1_000_000;
            System.out.println("\nBrute Force - Średni czas wykonania: " + averageTimeBruteForceNano + " ns (" + averageTimeBruteForceMilli + " ms)");

            // Wyświetlenie zajętości pamięci dla Brute Force
            long memoryBruteForce = MemoryMeasurer.getUsedMemory() - initialMemory;
            System.out.println("Brute Force - Całkowita zajętość pamięci: " + memoryBruteForce + " B");

            // ********** Nearest Neighbour **********
            NearestNeighbour nearestNeighbour = new NearestNeighbour(problem);
            long totalTimeNearestNeighbour = 0;

            for (int i = 0; i < executions; i++) {
                System.out.println("\n============================");
                System.out.println("Nearest Neighbour - Iteracja " + (i + 1));
                progressIndicator.showProgress(i + 1, executions); // Wyświetlanie paska postępu
                System.out.println("============================");

                long timeNano = TimeMeasurer.measureAlgorithmTime(nearestNeighbour); // Czas w nanosekundach
                long timeMilli = timeNano / 1_000_000;                              // Przeliczenie na milisekundy
                System.out.println("Czas wykonania: " + timeNano + " ns (" + timeMilli + " ms)");

                totalTimeNearestNeighbour += timeNano;
            }
            long averageTimeNearestNeighbourNano = totalTimeNearestNeighbour / executions;
            long averageTimeNearestNeighbourMilli = averageTimeNearestNeighbourNano / 1_000_000;
            System.out.println("\nNearest Neighbour - Średni czas wykonania: " + averageTimeNearestNeighbourNano + " ns (" + averageTimeNearestNeighbourMilli + " ms)");

            // Wyświetlenie zajętości pamięci dla Nearest Neighbour
            long memoryNearestNeighbour = MemoryMeasurer.getUsedMemory() - initialMemory;
            System.out.println("Nearest Neighbour - Całkowita zajętość pamięci: " + memoryNearestNeighbour + " B");

            // ********** Random **********
            Random random = new Random(problem);
            long totalTimeRandom = 0;

            for (int i = 0; i < executions; i++) {
                System.out.println("\n============================");
                System.out.println("Random - Iteracja " + (i + 1));
                progressIndicator.showProgress(i + 1, executions); // Wyświetlanie paska postępu
                System.out.println("============================");

                long timeNano = TimeMeasurer.measureAlgorithmTime(random); // Czas w nanosekundach
                long timeMilli = timeNano / 1_000_000;                     // Przeliczenie na milisekundy
                System.out.println("Czas wykonania: " + timeNano + " ns (" + timeMilli + " ms)");

                totalTimeRandom += timeNano;
            }
            long averageTimeRandomNano = totalTimeRandom / executions;
            long averageTimeRandomMilli = averageTimeRandomNano / 1_000_000;
            System.out.println("\nRandom - Średni czas wykonania: " + averageTimeRandomNano + " ns (" + averageTimeRandomMilli + " ms)");

            // Wyświetlenie zajętości pamięci dla Random
            long memoryRandom = MemoryMeasurer.getUsedMemory() - initialMemory;
            System.out.println("Random - Całkowita zajętość pamięci: " + memoryRandom + " B");

            // Pomiary pamięci po zakończeniu algorytmów
            long finalMemory = MemoryMeasurer.getUsedMemory();
            long totalMemoryUsed = finalMemory - initialMemory;

            // Wyświetlenie całkowitej zajętości pamięci po uruchomieniu wszystkich algorytmów
            System.out.println("\nCałkowita zajętość pamięci po uruchomieniu wszystkich algorytmów: " + totalMemoryUsed + " B");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
