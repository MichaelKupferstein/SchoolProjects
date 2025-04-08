package edu.yu.parallel;

import edu.yu.parallel.implementation.SequentialDataProcessor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class GenerateReference {
    public static void main(String[] args) throws IOException, ProcessingException {
        String inputPath = "C:\\Users\\mkupf\\Downloads\\snp_adjclose_volume_data\\snp_adjclose_volume_data.csv";
        String outputPath = "C:\\Users\\mkupf\\OneDrive\\Documents\\reference_output.txt";

        System.out.println("Generating reference output...");
        SequentialDataProcessor processor = new SequentialDataProcessor();
        Map<Integer, TickerStats> referenceOutput = processor.processFile(inputPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (Map.Entry<Integer, TickerStats> entry : referenceOutput.entrySet()) {
                TickerStats stats = entry.getValue();
                writer.write(String.format("%d: %s%n",
                        entry.getKey(),
                        stats.Summary()
                ));
            }
        }
        System.out.println("Reference output saved to: " + outputPath);

        // Print the output to console as well
        for (Map.Entry<Integer, TickerStats> entry : referenceOutput.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().Summary());
        }
    }
}