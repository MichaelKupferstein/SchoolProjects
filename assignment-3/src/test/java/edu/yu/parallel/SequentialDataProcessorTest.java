package edu.yu.parallel;

import edu.yu.parallel.implementation.SequentialDataProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SequentialDataProcessorTest {

    private final static Logger logger = LogManager.getLogger(AppTest.class);
    private SequentialDataProcessor sdp;


    @BeforeEach
    void setUp() {
        logger.info("Creating SequentialDataProcessor");
        sdp = new SequentialDataProcessor();
    }

    @AfterEach
    void tearDown() {
        logger.info("After");
    }

    @Test
    void testValidInput() throws IOException, ProcessingException {
        Path tempFile = Files.createTempFile("test", ".csv");
        try(FileWriter write = new FileWriter(tempFile.toFile())) {
            write.write("Date,Ticker,Adj Close,Volume\n");
            write.write("1970-01-02,AEP,0.8545475006103516,10300.0\n");
            write.write("1970-01-02,BA,0.2905673086643219,634838.0\n");
            write.write("1970-01-02,CAT,1.249887466430664,276000.0\n");
            write.write("1971-06-30,CNP,0.43642911314964294,35943.0\n");
        }


        //print out the temp file
//        logger.info("File contents:");
//        Files.readAllLines(tempFile).forEach(line -> logger.info(line));

        Map<Integer,TickerStats> result = sdp.processFile(tempFile.toString());
        //print out the result
        logger.info("Result:");
        result.forEach((year, stats) -> logger.info(year + ": " + stats));


        assert(result.size() == 2);
        TickerStats stats1970 = result.get(1970);
        assertNotNull(stats1970);
        assertEquals("CAT", stats1970.getHighestCloseTicker());
        assertEquals(1.249887466430664, stats1970.getHighestClose());
        assertEquals("BA", stats1970.getHighestVolumeTicker());
        assertEquals(634838.0, stats1970.getHighestVolume());
        assertEquals(3, stats1970.getQuoteCount());

        TickerStats stats1971 = result.get(1971);
        assertNotNull(stats1971);
        assertEquals("CNP", stats1971.getHighestCloseTicker());
        assertEquals(0.43642911314964294, stats1971.getHighestClose());
        assertEquals("CNP", stats1971.getHighestVolumeTicker());
        assertEquals(35943.0, stats1971.getHighestVolume());
        assertEquals(1, stats1971.getQuoteCount());
    }

    @Test
    void testEmptyFile() throws IOException {
        Path tempFile = Files.createTempFile("test", ".csv");
        assertThrows(ProcessingException.class, () -> sdp.processFile(tempFile.toString()));
    }

    @Test
    void testFullFile() throws IOException, ProcessingException {
        Path filePath = Paths.get("C:\\Users\\mkupf\\Downloads\\snp_adjclose_volume_data\\snp_adjclose_volume_data.csv");

        long startTime = System.nanoTime();
        Map<Integer, TickerStats> result = sdp.processFile(filePath.toString());
        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1_000_000;
        logger.info("Processing time: " + duration + " ms");

        assertNotNull(result);
        logger.info("Result:");
        result.forEach((year, stats) -> logger.info(year + ": " + stats));
    }


}
