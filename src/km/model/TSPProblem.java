package km.model;

import java.util.Random;

public class TSPProblem {
    private int[][] distanceMatrix;

    public TSPProblem(int[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public static TSPProblem generateRandomProblem(int size) {
        Random random = new Random();
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    matrix[i][j] = -1;
                } else {
                    matrix[i][j] = random.nextInt(100) + 1;
                }
            }
        } // Generowanie macierzy asymetrycznej
        return new TSPProblem(matrix);
    }

    public int getCitiesCount() {
        return distanceMatrix.length;
    }

    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public int getDistance(int from, int to) {
        return distanceMatrix[from][to];
    }
}
