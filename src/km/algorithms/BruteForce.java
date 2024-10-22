package km.algorithms;

import km.model.TSPProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static km.Main.calculateTotalDistance;

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
        } // Tworzymy tablice miast
        List<Integer> bestPath = null;
        int bestDistance = Integer.MAX_VALUE;

        while (nextPermutation(cities)) {
            int currentDistance = calculateTotalDistance(cities, problem);
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath = new ArrayList<>(cities);
            }
        } /* Do póki istnieją permutacje tablicy z miastami, obliczamy ich całkowitą trase, jeżeli zostanie znaleziona
            kombinacja z lepszym wynikiem, aktualizujemy tablicę
        */

        return bestPath;
    }

    private boolean nextPermutation(List<Integer> cities) {
        int n = cities.size();

        int i = n - 2;
        while (i >= 0 && cities.get(i) >= cities.get(i + 1)) {
            i--;
        } // Znalezienie pierwszego elementu od końca, który jest mniejszy od swojego następcy.

        if (i < 0) {
            return false;
        } // Jeśli nie ma takiego elementu to znaczy że lista jest w porządku malejącym, a więc nie da się wygenerować kolejnej permutacji.

        int j = n - 1;
        while (cities.get(j) <= cities.get(i)) {
            j--;
        } // Znalezienie najmniejszego elementu z prawej strony od i, który jest większy od cities[i] (liczby na wyżej znalezionym indeksie).


        Collections.swap(cities, i, j); // Zamiana miejsc
        reverse(cities, i + 1, n - 1); /*
        Zapewniamy, że kolejne permutacje są generowane w sposób uporządkowany, bez pomijania żadnej permutacji
        */

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
