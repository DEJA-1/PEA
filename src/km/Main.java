package km;

import km.data.ConfigLoader;
import km.algorithms.Algorithm;
import km.algorithms.BruteForce;
import km.algorithms.NearestNeighbour;
import km.algorithms.Random;
import km.data.CSVWriter;
import km.data.FileLoader;
import km.model.TSPProblem;
import km.ui.Display;
import km.ui.ProgressIndicator;
import km.utils.MemoryMeasurer;
import km.utils.TimeMeasurer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConfigLoader configLoader = null;

        // Pobranie ścieżki pliku konfiguracyjnego
        while (configLoader == null) {
            System.out.println("Wprowadź ścieżkę pliku konfiguracyjnego: ");
            String configFilePath = scanner.nextLine();

            try {
                // Inicjalizacja Config Loadera
                configLoader = new ConfigLoader(configFilePath);
            } catch (IOException e) {
                // Jeśli ścieżka jest niepoprawna, wyświetl komunikat
                System.out.println("Błąd: Nieprawidłowa ścieżka pliku konfiguracyjnego lub plik nie istnieje. Spróbuj ponownie.");
            }
        }

        try {
            // Zczytywanie wartości z configu
            String[] problemSizes = configLoader.getProperty("problemSize").split("\\s+");  // Zmieniamy na listę wartości
            int executions = configLoader.getIntProperty("executions");
            int useInputFile = configLoader.getIntProperty("useInputFile");
            boolean showProgress = configLoader.getBooleanProperty("showProgress");
            boolean isSymmetric = configLoader.getBooleanProperty("isSymmetric");


            String bruteForceOutputFile = configLoader.getProperty("bruteForceOutputFile");
            String randomOutputFile = configLoader.getProperty("randomOutputFile");
            String nearestNeighbourOutputFile = configLoader.getProperty("nearestNeighbourOutputFile");

            String inputDataFile = configLoader.getProperty("inputData");

            // Inicjalizacja paska progresu, bierzemy pod uwagę wykonanie wszystkich algorytmów dla wszystkich rozmiarów problemu
            ProgressIndicator progressIndicator = new ProgressIndicator(problemSizes.length * executions * 3); // Liczymy wszystkie algorytmy

            // Inicjalizacja CSVWriterów
            CSVWriter bruteForceWriter = new CSVWriter();
            CSVWriter nearestNeighbourWriter = new CSVWriter();
            CSVWriter randomWriter = new CSVWriter();

            try {
                bruteForceWriter.setFilePath(bruteForceOutputFile);
                nearestNeighbourWriter.setFilePath(nearestNeighbourOutputFile);
                randomWriter.setFilePath(randomOutputFile);
            } catch (IOException e) {
                System.out.println("Błąd: Nieprawidłowa ścieżka pliku wyjściowego w pliku konfiguracyjnym.");
                return;
            }

            // Inicjalizacja całkowitego zajętego czasu wywołania n algorytmów w celu obliczenia średniej
            long bruteForceTotalTime = 0;
            long nearestNeighbourTotalTime = 0;
            long randomTotalTime = 0;
            long initialMemory = MemoryMeasurer.getUsedMemory();

            // Iteracja po wszystkich problemSize
            for (String problemSizeString : problemSizes) {
                int problemSize = Integer.parseInt(problemSizeString);
                int displayProblemSize = problemSize;
                bruteForceTotalTime = 0;
                nearestNeighbourTotalTime = 0;
                randomTotalTime = 0;

                /*
                    Jeżeli useInputFile = 0, to z każdą iteracją (po wykonaniu wszystkich 3 algorytmów na danej instancji) generujemy
                    nową, losową instancje problemu i wykonujemy algorytmy wraz z pomiarem czasu
                 */
                if (useInputFile == 0) {
                    /*
                        Wywołanie "rozgrzewkowych" algorytmów aby uniknąć przekłamania wyników algorytmów
                    */
                    for (int j = 0; j < executions; j++) {
                        TSPProblem problem = TSPProblem.generateRandomProblem(problemSize, isSymmetric);
                        runAlgorithm("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, j + 1);
                        runAlgorithm("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, j + 1);
                        runAlgorithm("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, j + 1);
                    }
                    for (int i = 0; i < executions; i++) {
                        TSPProblem problem = TSPProblem.generateRandomProblem(problemSize, isSymmetric);
                        bruteForceTotalTime += runAlgorithm("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                        nearestNeighbourTotalTime += runAlgorithm("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                        randomTotalTime += runAlgorithm("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    }
                } else {
                    try {
                        TSPProblem problem = initializeProblemFromFile(inputDataFile);
                        displayProblemSize = problem.getCitiesCount();
                        for (int i = 0; i < executions; i++) {
                            bruteForceTotalTime += runAlgorithm("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                            nearestNeighbourTotalTime += runAlgorithm("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                            randomTotalTime += runAlgorithm("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                        }
                    } catch (IOException e){
                        System.out.println("Błąd: Nieprawidłowa ścieżka pliku wejściowego w pliku konfiguracyjnym.");
                        return;
                    }
                }

                // Wyświetlenie podsumowania po każdym problemSize
                Display.printSummarySeparator();
                Display.printProblemSize(displayProblemSize);
                Display.printSummary("Brute Force - Średni czas wykonania: " + (bruteForceTotalTime / executions) + " ns (" + (bruteForceTotalTime / executions / 1_000_000) + " ms)");
                Display.printSummary("Nearest Neighbour - Średni czas wykonania: " + (nearestNeighbourTotalTime / executions) + " ns (" + (nearestNeighbourTotalTime / executions / 1_000_000) + " ms)");
                Display.printSummary("Random - Średni czas wykonania: " + (randomTotalTime / executions) + " ns (" + (randomTotalTime / executions / 1_000_000) + " ms)");
            }

            // Po zapisaniu wyników zamykamy pliki
            bruteForceWriter.close();
            nearestNeighbourWriter.close();
            randomWriter.close();

            // Wyświetlenie całkowitego zużycia pamięci
            Display.displayTotalMemoryUsage(initialMemory);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TSPProblem initializeProblemFromFile(String inputDataFile) throws IOException {
        return new TSPProblem(FileLoader.loadMatrixFromFile(inputDataFile));
    }

    /* Odpalenie algorytmu. Zwracany jest czas wykonania algorytmu, który wyżej dodajemy do sumy. Aktualizujemy stan progressu
        po każdym wywołaniu oraz zapisujemy rezultat do pliku
    */
    public static long runAlgorithm(String algorithmName, Algorithm algorithm, CSVWriter csvWriter, int[][] matrix, ProgressIndicator progressIndicator, TSPProblem problem, boolean showProgress, int iteration) throws IOException {
        Display.printIterationSeparator(algorithmName, iteration, showProgress, progressIndicator.getProgress());

        List<Integer> solution = algorithm.solve();
        int totalDistance = calculateTotalDistance(solution, problem);
        long timeNano = TimeMeasurer.measureAlgorithmTime(algorithm);

        Display.displayRoute(solution);
        Display.displayDistance(totalDistance);
        Display.displayExecutionTime(timeNano);

        if (iteration > 5000) {
            csvWriter.writeRecord(matrix.length, matrix, algorithmName, timeNano, timeNano / 1_000_000);
            progressIndicator.updateProgress();
        }


        return timeNano;
    }

    /*
        Obliczanie całkowitej długości ścieżki na podstawie listy miast.
   */
    public static int calculateTotalDistance(List<Integer> cities, TSPProblem problem) {
        int distance = 0;
        for (int i = 0; i < cities.size() - 1; i++) {
            distance += problem.getDistance(cities.get(i), cities.get(i + 1));
        }
        distance += problem.getDistance(cities.get(cities.size() - 1), cities.get(0));
        return distance;
    }
}
