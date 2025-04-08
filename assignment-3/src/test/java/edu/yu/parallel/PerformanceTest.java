package edu.yu.parallel;

import edu.yu.parallel.implementation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformanceTest {
    private final static Logger logger = LogManager.getLogger(PerformanceTest.class);
    private static final int WARM_UP_RUNS = 2;
    private static final int TEST_RUNS = 5;

    @Test
    void compareImplementations() throws IOException, ProcessingException {
        Path filePath = Paths.get("C:\\Users\\mkupf\\Downloads\\snp_adjclose_volume_data\\snp_adjclose_volume_data.csv");

        // List of processors to test
        List<DataProcessor> processors = new ArrayList<>();
        processors.add(new SequentialDataProcessor());
        processors.add(new ParallelDataProcessor());
        processors.add(new ParallelDataProcessor_1()); // Your alternative implementation
        processors.add(new ParallelStreamsDataProcessor());

        // Results storage
        List<List<Long>> allTimings = new ArrayList<>();
        for (int i = 0; i < processors.size(); i++) {
            allTimings.add(new ArrayList<>());
        }

        // Warm-up runs
        logger.info("Performing warm-up runs...");
        for (int i = 0; i < WARM_UP_RUNS; i++) {
            for (DataProcessor processor : processors) {
                processor.processFile(filePath.toString());
            }
        }

        // Actual test runs
        logger.info("Performing test runs...");
        for (int run = 0; run < TEST_RUNS; run++) {
            logger.info("Run {} of {}", run + 1, TEST_RUNS);

            for (int i = 0; i < processors.size(); i++) {
                DataProcessor processor = processors.get(i);
                long startTime = System.nanoTime();
                processor.processFile(filePath.toString());
                long duration = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds
                allTimings.get(i).add(duration);

                logger.info("{}: {} ms",
                        processor.getClass().getSimpleName(),
                        duration);
            }
        }

        // Print formatted table header
        String headerFormat = "| %-30s | %-8s | %-8s | %-8s | %-8s | %-8s | %-8s |%n";
        String rowFormat    = "| %-30s | %8d | %8d | %8d | %8d | %8d | %8.2f |%n";
        String separator    = "+" + "-".repeat(32) + "+" + "-".repeat(10) + "+" + "-".repeat(10) + "+" +
                "-".repeat(10) + "+" + "-".repeat(10) + "+" + "-".repeat(10) + "+" + "-".repeat(10) + "+";

        logger.info("\nPerformance Results (times in milliseconds):");
        logger.info(separator);
        logger.info(String.format(headerFormat,
                "Implementation", "Run 1", "Run 2", "Run 3", "Run 4", "Run 5", "Average"));
        logger.info(separator);

        // Print results for each implementation
        for (int i = 0; i < processors.size(); i++) {
            List<Long> timings = allTimings.get(i);
            double avg = timings.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);

            logger.info(String.format(rowFormat,
                    processors.get(i).getClass().getSimpleName(),
                    timings.get(0),
                    timings.get(1),
                    timings.get(2),
                    timings.get(3),
                    timings.get(4),
                    avg));
            logger.info(separator);
        }
    }
}