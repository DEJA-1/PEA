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

            String randomOutputFile = configLoader.getProperty("randomOutputFile");
            String bruteForceOutputFile = configLoader.getProperty("bruteForceOutputFile");
            String nearestNeighbourOutputFile = configLoader.getProperty("nearestNeighbourOutputFile");

            String matrix6x6File = configLoader.getProperty("matrix6x6File");
            String matrix8x8File = configLoader.getProperty("matrix8x8File");
            String matrix11x11File = configLoader.getProperty("matrix11x11File");

            TSPProblem problem = initializeProblem(useInputFile, problemSize, matrix6x6File, matrix8x8File, matrix11x11File);

            int totalIterations = 3 * executions;
            ProgressIndicator progressIndicator = new ProgressIndicator(totalIterations);

            long initialMemory = MemoryMeasurer.getUsedMemory();
            CSVWriter csvWriter = new CSVWriter();
            StringBuilder summaryResults = new StringBuilder();

            runAndAppendResult("Random", new Random(problem), executions, csvWriter, randomOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator);
            runAndAppendResult("Brute Force", new BruteForce(problem), executions, csvWriter, bruteForceOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator);
            runAndAppendResult("Nearest Neighbour", new NearestNeighbour(problem), executions, csvWriter, nearestNeighbourOutputFile, problem.getDistanceMatrix(), summaryResults, progressIndicator);

            csvWriter.close();

            Display.printSeparator();
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

    public static void runAndAppendResult(String algorithmName, Algorithm algorithm, int executions, CSVWriter csvWriter, String outputFilePath, int[][] matrix, StringBuilder summaryResults, ProgressIndicator progressIndicator) throws IOException {
        csvWriter.setFilePath(outputFilePath);
        String result = runAlgorithmWithWarmup(algorithmName, algorithm, executions, csvWriter, matrix, progressIndicator);
        summaryResults.append(result);
    }

    public static String runAlgorithmWithWarmup(String algorithmName, Algorithm algorithm, int executions, CSVWriter csvWriter, int[][] matrix, ProgressIndicator progressIndicator) throws IOException {
        StringBuilder algorithmResults = new StringBuilder();
        algorithm.solve(); /*
            Pierwsze wywołanie zawsze trwa o wiele dłużej niż reszta ze względu na czynniki Javowe.
            Aby nie zakłamywać wyników, pierwszego wywołania nie uwzględniamy w pomiarach.
         */

        long totalTime = 0;
        long initialMemory = MemoryMeasurer.getUsedMemory();

        for (int i = 0; i < executions; i++) {
            Display.printIterationSeparator(algorithmName, i + 1);

            long timeNano = TimeMeasurer.measureAlgorithmTime(algorithm);
            totalTime += timeNano;

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
}
