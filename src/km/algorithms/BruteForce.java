package km.algorithms;

import km.model.TSPProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BruteForce extends Algorithm {
    private final TSPProblem problem;

    public BruteForce(TSPProblem problem) {
        this.problem = problem;
    }

    @Override
    public List<Integer> solve() {
        List<Integer> cities = new ArrayList<>();
        for (int i = 0; i < problem.getCitiesCount(); i++) {
            cities.add(i);
        }
        List<Integer> bestPath = null;
        int bestDistance = Integer.MAX_VALUE;

        while (nextPermutation(cities)) {
            int currentDistance = calculateTotalDistance(cities, problem);
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath = new ArrayList<>(cities);
            }
        }

        System.out.println("Best distance (Brute Force): " + bestDistance);
        return bestPath;
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
