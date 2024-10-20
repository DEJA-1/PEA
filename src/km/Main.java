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

            CSVWriter bruteForceWriter = new CSVWriter();
            CSVWriter nearestNeighbourWriter = new CSVWriter();
            CSVWriter randomWriter = new CSVWriter();

            bruteForceWriter.setFilePath(bruteForceOutputFile);
            nearestNeighbourWriter.setFilePath(nearestNeighbourOutputFile);
            randomWriter.setFilePath(randomOutputFile);

            long bruteForceTotalTime = 0;
            long nearestNeighbourTotalTime = 0;
            long randomTotalTime = 0;
            long initialMemory = MemoryMeasurer.getUsedMemory();
            int displayProblemSize = 0;

            if (useInputFile == 0) {
                displayProblemSize = problemSize;
                for (int i = 0; i < executions; i++) {
                    TSPProblem problem = TSPProblem.generateRandomProblem(problemSize);
                    bruteForceTotalTime += runAlgorithm("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    nearestNeighbourTotalTime += runAlgorithm("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    randomTotalTime += runAlgorithm("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                }
            } else {
                TSPProblem problem = initializeProblem(useInputFile, problemSize, matrix6x6File, matrix8x8File, matrix11x11File);
                displayProblemSize = problem.getCitiesCount();
                for (int i = 0; i < executions; i++) {
                    bruteForceTotalTime += runAlgorithm("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    nearestNeighbourTotalTime += runAlgorithm("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    randomTotalTime += runAlgorithm("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                }
            }

            bruteForceWriter.close();
            nearestNeighbourWriter.close();
            randomWriter.close();

            Display.printSummarySeparator();

            Display.printProblemSize(displayProblemSize);
            Display.printSummary("Brute Force - Średni czas wykonania: " + (bruteForceTotalTime / executions) + " ns (" + (bruteForceTotalTime / executions / 1_000_000) + " ms)");
            Display.printSummary("Nearest Neighbour - Średni czas wykonania: " + (nearestNeighbourTotalTime / executions) + " ns (" + (nearestNeighbourTotalTime / executions / 1_000_000) + " ms)");
            Display.printSummary("Random - Średni czas wykonania: " + (randomTotalTime / executions) + " ns (" + (randomTotalTime / executions / 1_000_000) + " ms)");
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

    public static long runAlgorithm(String algorithmName, Algorithm algorithm, CSVWriter csvWriter, int[][] matrix, ProgressIndicator progressIndicator, TSPProblem problem, boolean showProgress, int iteration) throws IOException {
        Display.printIterationSeparator(algorithmName, iteration, showProgress, progressIndicator.getProgress());

        List<Integer> solution = algorithm.solve();
        int totalDistance = calculateTotalDistance(solution, problem);
        long timeNano = TimeMeasurer.measureAlgorithmTime(algorithm);

        Display.displayRoute(solution);
        Display.displayDistance(totalDistance);
        Display.displayExecutionTime(timeNano);

        csvWriter.writeRecord(matrix.length, matrix, algorithmName, timeNano, timeNano / 1_000_000);

        progressIndicator.updateProgress();

        return timeNano;
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
