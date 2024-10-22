package km;

import km.algorithms.Algorithm;
import km.algorithms.BruteForce;
import km.algorithms.NearestNeighbour;
import km.algorithms.Random;
import km.data.CSVWriter;
import km.data.ConfigLoader;
import km.data.FileLoader;
import km.model.TSPProblem;
import km.ui.Display;
import km.ui.ProgressIndicator;
import km.utils.MemoryMeasurer;
import km.utils.TimeMeasurer;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Inicjalizacja Config Loadera
            String rootPath = "C:\\Users\\Krzysiek\\Downloads\\";
            String configFileName = "PEA_projekt_config.txt";
            String configFilePath = rootPath + configFileName;

            ConfigLoader configLoader = new ConfigLoader(configFilePath);

            // Zczytywanie wartości z configu
            int problemSize = configLoader.getIntProperty("problemSize");
            int executions = configLoader.getIntProperty("executions");
            int useInputFile = configLoader.getIntProperty("useInputFile");
            boolean showProgress = configLoader.getBooleanProperty("showProgress");

            String bruteForceOutputFile = configLoader.getProperty("bruteForceOutputFile");
            String randomOutputFile = configLoader.getProperty("randomOutputFile");
            String nearestNeighbourOutputFile = configLoader.getProperty("nearestNeighbourOutputFile");

            String inputDataFile = configLoader.getProperty("inputData");

            // Inicjalizacja paska progresu, bierzemy pod uwagę wykonanie wszystkich algorytmów stąd * 3
            ProgressIndicator progressIndicator = new ProgressIndicator(executions * 3);

            // Inicjalizacja CSVWriterów, dla porządku wyniki zapisujemy do 3 osobnych plików
            CSVWriter bruteForceWriter = new CSVWriter();
            CSVWriter nearestNeighbourWriter = new CSVWriter();
            CSVWriter randomWriter = new CSVWriter();

            // Ustawianie ścieżek plików wynikowych
            bruteForceWriter.setFilePath(bruteForceOutputFile);
            nearestNeighbourWriter.setFilePath(nearestNeighbourOutputFile);
            randomWriter.setFilePath(randomOutputFile);

            // Inicjalizacja całkowitego zajętego czasu wywołania n algorytmów w celu obliczenia średniej
            long bruteForceTotalTime = 0;
            long nearestNeighbourTotalTime = 0;
            long randomTotalTime = 0;
            long initialMemory = MemoryMeasurer.getUsedMemory();
            int displayProblemSize = 0;

            /*
                Jeżeli useInputFile = 0, to z każdą iteracją (po wykonaniu wszystkich 3 algorytmów na danej instancji) generujemy
                nową, losową instancje problemu i wykonujemy algorytmy wraz z pomiarem czasu

                Jeżeli useInputFile = 1, to zczytujemy instancje problemu z plików, i wykonujemy na niej wszystkie 3 algorytmy
                n razy
             */
            if (useInputFile == 0) {
                displayProblemSize = problemSize;
                for (int i = 0; i < executions; i++) {
                    TSPProblem problem = TSPProblem.generateRandomProblem(problemSize);
                    bruteForceTotalTime += runAlgorithm("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    nearestNeighbourTotalTime += runAlgorithm("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    randomTotalTime += runAlgorithm("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                }
            } else {
                TSPProblem problem = initializeProblemFromFile(inputDataFile);
                displayProblemSize = problem.getCitiesCount();
                for (int i = 0; i < executions; i++) {
                    bruteForceTotalTime += runAlgorithm("Brute Force", new BruteForce(problem), bruteForceWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    nearestNeighbourTotalTime += runAlgorithm("Nearest Neighbour", new NearestNeighbour(problem), nearestNeighbourWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                    randomTotalTime += runAlgorithm("Random", new Random(problem), randomWriter, problem.getDistanceMatrix(), progressIndicator, problem, showProgress, i + 1);
                }
            }

            // Po zapisaniu wyników zamykamy pliki
            bruteForceWriter.close();
            nearestNeighbourWriter.close();
            randomWriter.close();

            // Wyświetlenie podsumowania
            Display.printSummarySeparator();
            Display.printProblemSize(displayProblemSize);
            Display.printSummary("Brute Force - Średni czas wykonania: " + (bruteForceTotalTime / executions) + " ns (" + (bruteForceTotalTime / executions / 1_000_000) + " ms)");
            Display.printSummary("Nearest Neighbour - Średni czas wykonania: " + (nearestNeighbourTotalTime / executions) + " ns (" + (nearestNeighbourTotalTime / executions / 1_000_000) + " ms)");
            Display.printSummary("Random - Średni czas wykonania: " + (randomTotalTime / executions) + " ns (" + (randomTotalTime / executions / 1_000_000) + " ms)");
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

        csvWriter.writeRecord(matrix.length, matrix, algorithmName, timeNano, timeNano / 1_000_000);

        progressIndicator.updateProgress();

        return timeNano;
    }

    /*
        Obliczanie całkowitej długości ścieżki na podstawie listy miast.
   */
    public static int calculateTotalDistance(List<Integer> cities, TSPProblem problem) {
        int distance = 0;
        for (int i = 0; i < cities.size() - 1; i++) {
            distance += problem.getDistance(cities.get(i), cities.get(i + 1));
        } // Iterujemy przez każdy element tablicy obliczając dystans od miasta na indexie i do miasta na indexie i + 1
        distance += problem.getDistance(cities.get(cities.size() - 1), cities.get(0));
        // Na samym końcu należy dodać ścieżkę do punktu początkowego
        return distance;
    }
}
