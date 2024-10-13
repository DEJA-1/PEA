package km.algorithms;

import km.model.TSPProblem;

import java.util.List;

abstract public class Algorithm {
    public abstract List<Integer> solve();

    public int calculateTotalDistance(List<Integer> cities, TSPProblem problem) {
        int distance = 0;
        for (int i = 0; i < cities.size() - 1; i++) {
            distance += problem.getDistance(cities.get(i), cities.get(i + 1));
        }
        distance += problem.getDistance(cities.get(cities.size() - 1), cities.get(0));
        return distance;
    }
}