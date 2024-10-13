package km;

import km.algorithms.TSPBruteForce;
import km.data.FileLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String rootPath = "C:\\Users\\Krzysiek\\Downloads\\";
            String matrixFileName = "matrix_11x11.txt";
            int[][] distanceMatrix = FileLoader.loadMatrixFromFile(rootPath + matrixFileName);
            TSPProblem problem = new TSPProblem(distanceMatrix);

            // Przegląd zupełny
            TSPBruteForce bruteForce = new TSPBruteForce(problem);
            long startTime = System.nanoTime();
            bruteForce.solve();
            long endTime = System.nanoTime();
            System.out.println("Brute Force Time: " + (endTime - startTime) + " ns");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}