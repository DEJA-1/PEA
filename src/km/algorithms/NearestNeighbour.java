package km.algorithms;

import km.model.TSPProblem;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighbour extends Algorithm {
    private TSPProblem problem;

    public NearestNeighbour(TSPProblem problem) {
        this.problem = problem;
    }

    @Override
    public List<Integer> solve() {
        boolean[] visited = new boolean[problem.getCitiesCount()];
        List<Integer> path = new ArrayList<>();
        int currentCity = 0;
        path.add(currentCity);
        visited[currentCity] = true;

        for (int i = 1; i < problem.getCitiesCount(); i++) {
            int nextCity = -1;
            int shortestDistance = Integer.MAX_VALUE;

            for (int j = 0; j < problem.getCitiesCount(); j++) {
                if (!visited[j] && problem.getDistance(currentCity, j) < shortestDistance) {
                    nextCity = j;
                    shortestDistance = problem.getDistance(currentCity, j);
                }
            }
            currentCity = nextCity;
            path.add(currentCity);
            visited[currentCity] = true;
        }

        System.out.println("Total Distance (Nearest Neighbour): " + calculateTotalDistance(path, problem));
        return path;
    }
}
