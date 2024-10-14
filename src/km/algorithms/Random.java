package km.algorithms;

import km.model.TSPProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Random extends Algorithm {
    private TSPProblem problem;
    private java.util.Random random;

    public Random(TSPProblem problem) {
        this.problem = problem;
        this.random = new java.util.Random();
    }

    @Override
    public List<Integer> solve() {
        List<Integer> cities = new ArrayList<>();
        for (int i = 0; i < problem.getCitiesCount(); i++) {
            cities.add(i);
        }
        Collections.shuffle(cities, random);
        System.out.println("Total Distance (Random): " + calculateTotalDistance(cities, problem));
        return cities;
    }
}
