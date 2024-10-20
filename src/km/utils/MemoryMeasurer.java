package km.utils;

import km.algorithms.Algorithm;

public class MemoryMeasurer {

    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
