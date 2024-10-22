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
        int numIterations = citiesCount * citiesCount; // Ustawiona liczba iteracji która pomaga uzyskać bardziej optymalny wynik

        for (int iter = 0; iter < numIterations; iter++) {
            List<Integer> cities = new ArrayList<>();
            for (int i = 0; i < citiesCount; i++) {
                cities.add(i);
            } // Tworzymy tablice miast

            Collections.shuffle(cities, random); // Wybieramy losową permutacje tablicy

            int totalDistance = calculateTotalDistance(cities, problem); // Obliczamy trasę dla losowej permutacji

            if (totalDistance < bestDistance) {
                bestDistance = totalDistance;
                bestPath = new ArrayList<>(cities);
            } // Jeżeli znajdziemy permutacje o lepszej trasie, aktualizujemy bestDistance i ścieżkę
        }

        return bestPath;
    }
}
