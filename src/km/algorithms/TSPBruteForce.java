package km.algorithms;

import km.TSPProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TSPBruteForce {
    private final TSPProblem problem;

    public TSPBruteForce(TSPProblem problem) {
        this.problem = problem;
    }

    public List<Integer> solve() {
        List<Integer> cities = new ArrayList<>();
        for (int i = 0; i < problem.getCitiesCount(); i++) {
            cities.add(i);
        }
        List<Integer> bestPath = null;
        int bestDistance = Integer.MAX_VALUE;

        while (nextPermutation(cities)) {
            int currentDistance = calculateTotalDistance(cities);
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath = new ArrayList<>(cities);
            }
        }

        System.out.println("Best distance (Brute Force): " + bestDistance);
        return bestPath;
    }

    private int calculateTotalDistance(List<Integer> cities) {
        int distance = 0;
        for (int i = 0; i < cities.size() - 1; i++) {
            distance += problem.getDistance(cities.get(i), cities.get(i + 1));
        }
        distance += problem.getDistance(cities.get(cities.size() - 1), cities.get(0));
        return distance;
    }

    private boolean nextPermutation(List<Integer> cities) {
        int n = cities.size();

        int i = n - 2;
        while (i >= 0 && cities.get(i) >= cities.get(i + 1)) {
            i--;
        }

        if (i < 0) {
            return false;
        }

        int j = n - 1;
        while (cities.get(j) <= cities.get(i)) {
            j--;
        }


        Collections.swap(cities, i, j);
        reverse(cities, i + 1, n - 1);

        return true;
    }

    private void reverse(List<Integer> list, int start, int end) {
        while (start < end) {
            Collections.swap(list, start, end);
            start++;
            end--;
        }
    }
}
