package km.algorithms;

import km.model.TSPProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static km.Main.calculateTotalDistance;

public class Random extends Algorithm {
    private final TSPProblem problem;
    private final java.util.Random random;

    public Random(TSPProblem problem) {
        this.problem = problem;
        this.random = new java.util.Random();
    }

    @Override
    public List<Integer> solve() {
        List<Integer> bestPath = null;
        int bestDistance = Integer.MAX_VALUE;
        int citiesCount = problem.getCitiesCount();
        int numIterations = citiesCount * citiesCount;

        for (int iter = 0; iter < numIterations; iter++) {
            List<Integer> cities = new ArrayList<>();
            for (int i = 0; i < citiesCount; i++) {
                cities.add(i);
            }

            Collections.shuffle(cities, random);

            int totalDistance = calculateTotalDistance(cities, problem);

            if (totalDistance < bestDistance) {
                bestDistance = totalDistance;
                bestPath = new ArrayList<>(cities);
            }
        }

        return bestPath;
    }
}
