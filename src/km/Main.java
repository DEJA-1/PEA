package km;

import km.algorithms.Algorithm;
import km.algorithms.BruteForce;
import km.algorithms.NearestNeighbour;
import km.algorithms.Random;
import km.data.CSVWriter;
import km.data.ConfigLoader;
import km.data.FileLoader;
import km.model.TSPProblem;
import km.ui.Display;
import km.ui.ProgressIndicator;
import km.utils.MemoryMeasurer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Ustalanie ścieżki do pliku konfiguracyjnego
            String rootPath = "C:\\Users\\Krzysiek\\Downloads\\";
            String configFileName = "PEA_projekt_config.txt";
            String configFilePath = rootPath + configFileName;

            // Wczytywanie pliku konfiguracyjnego za pomocą ConfigLoader
            ConfigLoader configLoader = new ConfigLoader(configFilePath);

            // Wczytywanie parametrów z pliku konfiguracyjnego
            int problemSize = configLoader.getIntProperty("problemSize");
            int executions = configLoader.getIntProperty("executions");
            int useInputFile = configLoader.getIntProperty("useInputFile");
            String randomOutputFile = configLoader.getProperty("randomOutputFile");
            String bruteForceOutputFile = configLoader.getProperty("bruteForceOutputFile");
            String nearestNeighbourOutputFile = configLoader.getProperty("nearestNeighbourOutputFile");

            // Wczytywanie ścieżek plików macierzy z pliku konfiguracyjnego
            String matrix6x6File = configLoader.getProperty("matrix6x6File");
            String matrix8x8File = configLoader.getProperty("matrix8x8File");
            String matrix11x11File = configLoader.getProperty("matrix11x11File");

            // Inicjalizacja problemu TSP na podstawie wartości useInputFile
            TSPProblem problem = initializeProblem(useInputFile, problemSize, matrix6x6File, matrix8x8File, matrix11x11File);

            int numberOfAlgorithms = 3;
            int totalIterations = numberOfAlgorithms * executions;
            ProgressIndicator progressIndicator = new ProgressIndicator(totalIterations);

            long initialMemory = MemoryMeasurer.getUsedMemory();
            CSVWriter csvWriter = new CSVWriter();
            StringBuilder summaryResults = new StringBuilder();

            // Random Algorithm
            csvWriter.setFilePath(randomOutputFile);
            Random random = new Random(problem);
            summaryResults.append(runAlgorithmWithWarmup("Random", random, executions, csvWriter, problem.getDistanceMatrix(), progressIndicator));

            // Brute Force Algorithm
            csvWriter.setFilePath(bruteForceOutputFile);
            BruteForce bruteForce = new BruteForce(problem);
            summaryResults.append(runAlgorithmWithWarmup("Brute Force", bruteForce, executions, csvWriter, problem.getDistanceMatrix(), progressIndicator));

            // Nearest Neighbour Algorithm
            csvWriter.setFilePath(nearestNeighbourOutputFile);
            NearestNeighbour nearestNeighbour = new NearestNeighbour(problem);
            summaryResults.append(runAlgorithmWithWarmup("Nearest Neighbour", nearestNeighbour, executions, csvWriter, problem.getDistanceMatrix(), progressIndicator));

            csvWriter.close();

            // Wyświetlenie podsumowania na końcu
            Display.printSeparator();
            Display.printSummary(summaryResults.toString());
            Display.displayTotalMemoryUsage(initialMemory);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Funkcja do inicjalizacji problemu TSP na podstawie parametru useInputFile i wczytanych ścieżek plików macierzy
    public static TSPProblem initializeProblem(int useInputFile, int problemSize, String matrix6x6File, String matrix8x8File, String matrix11x11File) throws IOException {
        switch (useInputFile) {
            case 1:
                return new TSPProblem(FileLoader.loadMatrixFromFile(matrix6x6File));
            case 2:
                return new TSPProblem(FileLoader.loadMatrixFromFile(matrix8x8File));
            case 3:
                return new TSPProblem(FileLoader.loadMatrixFromFile(matrix11x11File));
            default:
                // Generowanie losowej macierzy, jeśli wartość useInputFile jest równa 0
                return TSPProblem.generateRandomProblem(problemSize);
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

            long timeNano = km.utils.TimeMeasurer.measureAlgorithmTime(algorithm);
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
