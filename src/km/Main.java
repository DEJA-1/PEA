package km;

import km.algorithms.Algorithm;
import km.algorithms.BruteForce;
import km.algorithms.NearestNeighbour;
import km.algorithms.Random;
import km.data.CSVWriter;
import km.data.FileLoader;
import km.model.TSPProblem;
import km.ui.Display;
import km.utils.MemoryMeasurer;
import km.utils.TimeMeasurer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String rootPath = "C:\\Users\\Krzysiek\\Downloads\\";
            String matrixFileName = "matrix_6x6.txt";
            int[][] distanceMatrix = FileLoader.loadMatrixFromFile(rootPath + matrixFileName);
            TSPProblem problem = new TSPProblem(distanceMatrix);
            int executions = 10;

            long initialMemory = MemoryMeasurer.getUsedMemory();

            // Tworzymy jedną instancję CSVWriter
            CSVWriter csvWriter = new CSVWriter();

            // Random Algorithm
            csvWriter.setFilePath(rootPath + "random_algorithm_times.csv");
            Random random = new Random(problem);
            runAlgorithmWithWarmup("Random", random, executions, csvWriter, distanceMatrix);
            Display.displayMemoryUsage(initialMemory, "Random");

            // Brute Force Algorithm
            csvWriter.setFilePath(rootPath + "bruteforce_algorithm_times.csv");
            BruteForce bruteForce = new BruteForce(problem);
            runAlgorithmWithWarmup("Brute Force", bruteForce, executions, csvWriter, distanceMatrix);
            Display.displayMemoryUsage(initialMemory, "Brute Force");

            // Nearest Neighbour Algorithm
            csvWriter.setFilePath(rootPath + "nearestneighbour_algorithm_times.csv");
            NearestNeighbour nearestNeighbour = new NearestNeighbour(problem);
            runAlgorithmWithWarmup("Nearest Neighbour", nearestNeighbour, executions, csvWriter, distanceMatrix);
            Display.displayMemoryUsage(initialMemory, "Nearest Neighbour");

            csvWriter.close();

            Display.displayTotalMemoryUsage(initialMemory);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runAlgorithmWithWarmup(String algorithmName, Algorithm algorithm, int executions, CSVWriter csvWriter, int[][] matrix) throws IOException {
        algorithm.solve(); /*
            Pierwsze wywołanie zawsze trwa o wiele dłużej niż reszta ze względu na czynniki Javowe.
            Aby nie zakłamywać wyników, pierwszego wywołania nie uwzględniamy w pomiarach.
         */

        long totalTime = 0;
        int problemSize = matrix.length;

        for (int i = 0; i < executions; i++) {
            Display.displayIterationProgress(algorithmName, i + 1, executions);

            long timeNano = TimeMeasurer.measureAlgorithmTime(algorithm);
            Display.displayExecutionTime(timeNano);

            totalTime += timeNano;

            csvWriter.writeRecord(problemSize, matrix, algorithmName, timeNano, timeNano / 1_000_000);
        }

        long averageTimeNano = totalTime / executions;
        Display.displayAverageExecutionTime(algorithmName, averageTimeNano);
    }
}
