package edu.yu.introtoalgs;

import edu.yu.introtoalgs.WordLayout;
import edu.yu.introtoalgs.WordLayoutBase;
import edu.yu.introtoalgs.WordLayoutBase.Grid;
import edu.yu.introtoalgs.WordLayoutBase.LocationBase;

import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordLayoutTest {
    private long startTime;
    private Logger logger = LogManager.getLogger(WordLayoutTest.class.getName());

    @BeforeEach
    public void beforeEach() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    public void afterEach(TestInfo testInfo) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String testName = testInfo.getTestMethod().get().getName();
        logger.info("Test {} took {} ms", testName, duration);
    }

    @BeforeAll
    public static void setup() {
        Configurator.initialize(null, "log4j2.xml");
    }

    @Test
    void TestFromDoc(TestInfo testInfo){
        final int nRows = 3;
        final int nColumns = 3;
        final List<String> words = List.of("CAT","DOG","BOB");
        logger.info("Using this list of words: {}", words);

        final WordLayoutBase layout = new WordLayout(nRows, nColumns, words);

        for(String word : words){
            final List<LocationBase> locations = layout.locations(word);
            logger.info("Locations for word {}: {}", word, locations);
        }
        final Grid grid = layout.getGrid();
        logger.info("The filled in grid: {}", grid);

    }
}