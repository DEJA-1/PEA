package km.utils;

import km.algorithms.Algorithm;

public class MemoryMeasurer {

    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory(); // Wynik w bajtach
    }

    public static long measureMemoryUsage(Algorithm algorithm) {
        long beforeMemory = getUsedMemory();
        algorithm.solve();
        long afterMemory = getUsedMemory();
        return afterMemory - beforeMemory; // Wynik w bajtach
    }

    public static long measureTotalMemoryUsage(Algorithm algorithm, int iterations) {
        long totalMemoryUsed = 0;
        for (int i = 0; i < iterations; i++) {
            totalMemoryUsed += measureMemoryUsage(algorithm);
        }
        return totalMemoryUsed; // Wynik w bajtach
    }
}
