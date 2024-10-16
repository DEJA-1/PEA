package km.data;

import java.util.List;

public class Statistics {
    public static double calculateAverage(List<Long> times) {
        if (times == null || times.isEmpty()) {
            return 0.0;
        }

        long sum = 0;
        for (Long time : times) {
            sum += time;
        }

        return (double) sum / times.size();
    }
}
