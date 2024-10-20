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
import km.utils.TimeMeasurer;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String rootPath = "C:\\Users\\Krzysiek\\Downloads\\";
            String configFileName = "PEA_projekt_config.txt";
            String configFilePath = rootPath + configFileName;

            ConfigLoader configLoader = new ConfigLoader(configFilePath);

            int problemSize = configLoader.getIntProperty("problemSize");
            int executions = configLoader.getIntProperty("executions");
            int useInputFile = configLoader.getIntProperty("useInputFile");
            boolean showProgress = configLoader.getBooleanProperty("showProgress");

            String bruteForceOutputFile = configLoader.getProperty("bruteForceOutputFile");
            String randomOutputFile = configLoader.getProperty("randomOutputFile");
            String nearestNeighbourOutputFile = configLoader.getProperty("nearestNeighbourOutputFile");

            String matrix6x6File = configLoader.getProperty("matrix6x6File");
            String matrix8x8File = configLoader.getProperty("matrix8x8File");
            String matrix11x11File = configLoader.getProperty("matrix11x11File");

            ProgressIndicator progressIndicator = new ProgressIndicator(executions * 3); // 3 algorytmy

            long initialMemory = MemoryMeasurer.getUsedMemory();
            CSVWriter bruteForceWriter = new CSVWriter();
            CSVWriter nearestNeighbourWriter = new CSVWriter();
            CSVWriter randomWriter = new CSVWriter();
            StringBuilder summaryResults = new StringBuilder();

            bruteForceWriter.setFilePath(bruteForceOutputFile);
            nearestNeighbourWriter.setFilePath(nearestNeighbourOutputFile);
            randomWriter.setFilePath(randomOutputFile);

            if (useInputFile == 0) {
                for (int i = 0; i < executions; i++) {
                    // Losowanie nowej instancji problemu po każdym cyklu dla wszystkich algorytmów
                    TSPProblem problem = TSPProblem.generateRandomProblem(problemSize);

                    runAndAppendResult("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress, i + 1);
                    runAndAppendResult("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress, i + 1);
                    runAndAppendResult("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress, i + 1);
                }
            } else {
                // Gdy korzystamy z macierzy z pliku - jedna instancja dla wszystkich algorytmów
                TSPProblem problem = initializeProblem(useInputFile, problemSize, matrix6x6File, matrix8x8File, matrix11x11File);
                for (int i = 0; i < executions; i++) {
                    runAndAppendResult("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress, i + 1);
                    runAndAppendResult("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress, i + 1);
                    runAndAppendResult("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress, i + 1);
                }
            }

            bruteForceWriter.close();
            nearestNeighbourWriter.close();
            randomWriter.close();

            Display.printSummarySeparator();
            Display.printSummary(summaryResults.toString());
            Display.displayTotalMemoryUsage(initialMemory);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TSPProblem initializeProblem(int useInputFile, int problemSize, String matrix6x6File, String matrix8x8File, String matrix11x11File) throws IOException {
        switch (useInputFile) {
            case 1:
                return new TSPProblem(FileLoader.loadMatrixFromFile(matrix6x6File));
            case 2:
                return new TSPProblem(FileLoader.loadMatrixFromFile(matrix8x8File));
            case 3:
                return new TSPProblem(FileLoader.loadMatrixFromFile(matrix11x11File));
            default:
                return TSPProblem.generateRandomProblem(problemSize);
        }
    }

    public static void runAndAppendResult(String algorithmName, Algorithm algorithm, CSVWriter csvWriter, int[][] matrix, StringBuilder summaryResults, ProgressIndicator progressIndicator, TSPProblem problem, boolean showProgress, int iteration) throws IOException {
        String result = runAlgorithmWithWarmup(algorithmName, algorithm, csvWriter, matrix, progressIndicator, problem, showProgress, iteration);
        summaryResults.append(result);
    }

    public static String runAlgorithmWithWarmup(String algorithmName, Algorithm algorithm, CSVWriter csvWriter, int[][] matrix, ProgressIndicator progressIndicator, TSPProblem problem, boolean showProgress, int iteration) throws IOException {
        StringBuilder algorithmResults = new StringBuilder();

        long totalTime = 0;
        long initialMemory = MemoryMeasurer.getUsedMemory();

        Display.printIterationSeparator(algorithmName, iteration, showProgress, progressIndicator.getProgress());

        List<Integer> solution = algorithm.solve();
        int totalDistance = calculateTotalDistance(solution, problem);
        long timeNano = TimeMeasurer.measureAlgorithmTime(algorithm);
        totalTime += timeNano;

        Display.displayRoute(solution);
        Display.displayDistance(totalDistance);
        Display.displayExecutionTime(timeNano);

        // Zapis rekordu do pliku CSV
        csvWriter.writeRecord(matrix.length, matrix, algorithmName, timeNano, timeNano / 1_000_000);

        progressIndicator.updateProgress();

        long averageTimeNano = totalTime;
        long memoryUsed = MemoryMeasurer.getUsedMemory() - initialMemory;

        algorithmResults.append(algorithmName)
                .append(" - Średni czas wykonania: ").append(averageTimeNano).append(" ns (")
                .append(averageTimeNano / 1_000_000).append(" ms)\n")
                .append(algorithmName).append(" - Całkowita zajętość pamięci: ").append(memoryUsed).append(" B\n");

        return algorithmResults.toString();
    }

    public static int calculateTotalDistance(List<Integer> cities, TSPProblem problem) {
        int distance = 0;
        for (int i = 0; i < cities.size() - 1; i++) {
            distance += problem.getDistance(cities.get(i), cities.get(i + 1));
        }
        distance += problem.getDistance(cities.get(cities.size() - 1), cities.get(0));
        return distance;
    }
}
