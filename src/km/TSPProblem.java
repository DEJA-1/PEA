package km;

public class TSPProblem {
    private final int[][] distanceMatrix;
    private final int citiesCount;

    public TSPProblem(int[][] matrix) {
        this.distanceMatrix = matrix;
        this.citiesCount = matrix.length;
    }

    public int getDistance(int i, int j) {
        return distanceMatrix[i][j];
    }

    public int getCitiesCount() {
        return citiesCount;
    }
}
