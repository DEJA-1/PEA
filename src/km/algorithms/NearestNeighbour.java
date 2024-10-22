package km.algorithms;

import km.model.TSPProblem;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighbour extends Algorithm {
    private final TSPProblem problem;

    public NearestNeighbour(TSPProblem problem) {
        this.problem = problem;
    }

    @Override
    public List<Integer> solve() {
        boolean[] visited = new boolean[problem.getCitiesCount()];
        List<Integer> path = new ArrayList<>();
        int currentCity = 0;
        path.add(currentCity);
        visited[currentCity] = true; // Zaczynamy od pierwszego miasta, dodajemy do ściezki i ustawiamy jako odwiedzone

        for (int i = 1; i < problem.getCitiesCount(); i++) {
            int nextCity = -1;
            int shortestDistance = Integer.MAX_VALUE;

            for (int j = 0; j < problem.getCitiesCount(); j++) {
                if (!visited[j] && problem.getDistance(currentCity, j) < shortestDistance) {
                    nextCity = j;
                    shortestDistance = problem.getDistance(currentCity, j);
                } /* Iterujemy przez wszystkie miasta, gdy znajdziemy nieodwiedzone o najmniejszej odległości to
                     nextCity staje sie znalezionym miastem i aktualizujemy najkrótszą odległość
                */
            }
            currentCity = nextCity;
            path.add(currentCity);
            visited[currentCity] = true; // po znalezieniu najbliższego, nieodwiedzonego miasta dodajemy je do ścieżki i ustawiamy jako odwiedzone
        }

        return path;
    }
}
