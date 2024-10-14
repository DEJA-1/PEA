package km;

import km.algorithms.BruteForce;
import km.algorithms.NearestNeighbour;
import km.algorithms.Random;
import km.data.FileLoader;
import km.model.TSPProblem;
import km.utils.TimeMeasurer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String rootPath = "C:\\Users\\Krzysiek\\Downloads\\";
            String matrixFileName = "matrix_6x6.txt";
            int[][] distanceMatrix = FileLoader.loadMatrixFromFile(rootPath + matrixFileName);
            TSPProblem problem = new TSPProblem(distanceMatrix);

            BruteForce bruteForce = new BruteForce(problem);
            long time = TimeMeasurer.measureAlgorithmTime(bruteForce);
            System.out.println("Brute Force Time: " + time + " ns");

            NearestNeighbour nearestNeighbour = new NearestNeighbour(problem);
            long nearestNeighbourTime = TimeMeasurer.measureAlgorithmTime(nearestNeighbour);
            System.out.println("Nearest Neighbour Time: " + nearestNeighbourTime + " ns");

            Random random = new Random(problem);
            long randomTime = TimeMeasurer.measureAlgorithmTime(random);
            System.out.println("Nearest Neighbour Time: " + randomTime + " ns");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}