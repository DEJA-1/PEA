package km;

import km.algorithms.Algorithm;
import km.algorithms.BruteForce;
import km.algorithms.NearestNeighbour;
import km.algorithms.Random;
import km.data.CSVWriter;
import km.model.TSPProblem;
import km.ui.Display;
import km.ui.ProgressIndicator;
import km.utils.MemoryMeasurer;
import km.utils.TimeMeasurer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String rootPath = "C:\\Users\\Krzysiek\\Downloads\\";
            int problemSize = 6;  // Ustawiamy rozmiar problemu (liczba miast)
            int executions = 10;
            int numberOfAlgorithms = 3;
            int totalIterations = numberOfAlgorithms * executions;

            ProgressIndicator progressIndicator = new ProgressIndicator(totalIterations);
            long initialMemory = MemoryMeasurer.getUsedMemory();
            CSVWriter csvWriter = new CSVWriter();
            StringBuilder summaryResults = new StringBuilder();

            // Generowanie losowej macierzy dla problemu TSP o zadanym rozmiarze
            TSPProblem problem = TSPProblem.generateRandomProblem(problemSize);

            // Random Algorithm
            csvWriter.setFilePath(rootPath + "random_algorithm_times.csv");
            Random random = new Random(problem);
            summaryResults.append(runAlgorithmWithWarmup("Random", random, executions, csvWriter, problem.getDistanceMatrix(), progressIndicator));

            // Brute Force Algorithm
            csvWriter.setFilePath(rootPath + "bruteforce_algorithm_times.csv");
            BruteForce bruteForce = new BruteForce(problem);
            summaryResults.append(runAlgorithmWithWarmup("Brute Force", bruteForce, executions, csvWriter, problem.getDistanceMatrix(), progressIndicator));

            // Nearest Neighbour Algorithm
            csvWriter.setFilePath(rootPath + "nearestneighbour_algorithm_times.csv");
            NearestNeighbour nearestNeighbour = new NearestNeighbour(problem);
            summaryResults.append(runAlgorithmWithWarmup("Nearest Neighbour", nearestNeighbour, executions, csvWriter, problem.getDistanceMatrix(), progressIndicator));

            csvWriter.close();

            Display.printSeparator();
            Display.printSummary(summaryResults.toString());
            Display.displayTotalMemoryUsage(initialMemory);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String runAlgorithmWithWarmup(String algorithmName, Algorithm algorithm, int executions, CSVWriter csvWriter, int[][] matrix, ProgressIndicator progressIndicator) throws IOException {
        StringBuilder algorithmResults = new StringBuilder();

        algorithm.solve();

        long totalTime = 0;
        int problemSize = matrix.length;
        long initialMemory = MemoryMeasurer.getUsedMemory();

        for (int i = 0; i < executions; i++) {
            Display.printIterationSeparator(algorithmName, i + 1);

            long timeNano = TimeMeasurer.measureAlgorithmTime(algorithm);
            totalTime += timeNano;

            Display.displayExecutionTime(timeNano);

            csvWriter.writeRecord(problemSize, matrix, algorithmName, timeNano, timeNano / 1_000_000);

            progressIndicator.updateProgress();
        }

        long averageTimeNano = totalTime / executions;
        long memoryUsed = MemoryMeasurer.getUsedMemory() - initialMemory;

        algorithmResults.append(algorithmName)
                .append(" - Średni czas wykonania: ").append(averageTimeNano).append(" ns (")
                .append(averageTimeNano / 1_000_000).append(" ms)\n")
                .append(algorithmName).append(" - Całkowita zajętość pamięci: ").append(memoryUsed).append(" B\n");

        return algorithmResults.toString();
    }
}
