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
            int algorithmToRun = configLoader.getIntProperty("algorithmToRun");
            boolean showProgress = configLoader.getBooleanProperty("showProgress");

            String bruteForceOutputFile = configLoader.getProperty("bruteForceOutputFile");
            String randomOutputFile = configLoader.getProperty("randomOutputFile");
            String nearestNeighbourOutputFile = configLoader.getProperty("nearestNeighbourOutputFile");

            String matrix6x6File = configLoader.getProperty("matrix6x6File");
            String matrix8x8File = configLoader.getProperty("matrix8x8File");
            String matrix11x11File = configLoader.getProperty("matrix11x11File");

            TSPProblem problem = initializeProblem(useInputFile, problemSize, matrix6x6File, matrix8x8File, matrix11x11File);

            int totalIterations = 3 * executions; // Dostosujemy do algorytmu, który jest uruchamiany
            ProgressIndicator progressIndicator = new ProgressIndicator(totalIterations);

            long initialMemory = MemoryMeasurer.getUsedMemory();
            CSVWriter csvWriter = new CSVWriter();
            StringBuilder summaryResults = new StringBuilder();

            // Wybór algorytmu na podstawie konfiguracji
            switch (algorithmToRun) {
                case 0:  // Uruchomienie wszystkich algorytmów
                    totalIterations = 3 * executions;
                    progressIndicator = new ProgressIndicator(totalIterations);

                    runAndAppendResult("Brute Force", new BruteForce(problem), executions, csvWriter, bruteForceOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress);
                    runAndAppendResult("Nearest Neighbour", new NearestNeighbour(problem), executions, csvWriter, nearestNeighbourOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress);
                    runAndAppendResult("Random", new Random(problem), executions, csvWriter, randomOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress);
                    break;
                case 1:
                    totalIterations = executions;
                    progressIndicator = new ProgressIndicator(totalIterations);

                    runAndAppendResult("Brute Force", new BruteForce(problem), executions, csvWriter, bruteForceOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress);
                    break;
                case 2:
                    totalIterations = executions;
                    progressIndicator = new ProgressIndicator(totalIterations);

                    runAndAppendResult("Nearest Neighbour", new NearestNeighbour(problem), executions, csvWriter, nearestNeighbourOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress);
                    break;
                case 3:
                    totalIterations = executions;
                    progressIndicator = new ProgressIndicator(totalIterations);

                    runAndAppendResult("Random", new Random(problem), executions, csvWriter, randomOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator, problem, showProgress);
                    break;
                default:
                    System.out.println("Nieznany algorytm w pliku konfiguracyjnym.");
                    return;
            }

            csvWriter.close();

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

    public static void runAndAppendResult(String algorithmName, Algorithm algorithm, int executions, CSVWriter csvWriter, String outputFilePath, int[][] matrix, StringBuilder summaryResults, ProgressIndicator progressIndicator, TSPProblem problem, boolean showProgress) throws IOException {
        csvWriter.setFilePath(outputFilePath);
        String result = runAlgorithmWithWarmup(algorithmName, algorithm, executions, csvWriter, matrix, progressIndicator, problem, showProgress);
        summaryResults.append(result);
    }

    public static String runAlgorithmWithWarmup(String algorithmName, Algorithm algorithm, int executions, CSVWriter csvWriter, int[][] matrix, ProgressIndicator progressIndicator, TSPProblem problem, boolean showProgress) throws IOException {
        StringBuilder algorithmResults = new StringBuilder();

        algorithm.solve();

        long totalTime = 0;
        long initialMemory = MemoryMeasurer.getUsedMemory();

        for (int i = 0; i < executions; i++) {
            Display.printIterationSeparator(algorithmName, i + 1, showProgress, progressIndicator.getProgress());

            List<Integer> solution = algorithm.solve();

            int totalDistance = calculateTotalDistance(solution, problem);

            long timeNano = TimeMeasurer.measureAlgorithmTime(algorithm);
            totalTime += timeNano;

            Display.displayRoute(solution);
            Display.displayDistance(totalDistance);
            Display.displayExecutionTime(timeNano);

            csvWriter.writeRecord(matrix.length, matrix, algorithmName, timeNano, timeNano / 1_000_000);

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

    public static int calculateTotalDistance(List<Integer> cities, TSPProblem problem) {
        int distance = 0;
        for (int i = 0; i < cities.size() - 1; i++) {
            distance += problem.getDistance(cities.get(i), cities.get(i + 1));
        }
        distance += problem.getDistance(cities.get(cities.size() - 1), cities.get(0));
        return distance;
    }
}
