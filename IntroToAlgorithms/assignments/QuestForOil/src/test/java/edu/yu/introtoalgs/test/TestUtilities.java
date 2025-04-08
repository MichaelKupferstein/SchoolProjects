package edu.yu.introtoalgs.test;

import org.apache.logging.log4j.Logger;

public class TestUtilities {

    private static Logger logger;
    public TestUtilities(Logger logger) {
        this.logger = logger;
    }
    public void setRowAtATime(final int row, final String s, final char[][] map) {
        for (int i = 0; i < s.length(); ++i) {
            map[row][i] = s.charAt(i);
        }
    }
    public void logMap(final char[][] map) {
        for (int i = 0; i < map.length; ++i) {
            logger.info("{}", map[i]);
        }
    }

    public static class SoftAssert {
        private boolean failure = false;
        public void fail(final String msg) {
            failure = true;
            logger.error(msg);
        }
        public void assertEquals(final int a, final int b, final String msg) {
            if (a != b) {
                fail(msg + ": " + a + " != " + b);
            }
        }
        public void assertAll(final String msg) {
            if (failure) {
                throw new AssertionError(msg);
            }
        }
    }


}
