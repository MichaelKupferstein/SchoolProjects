package edu.yu.introtoalgs.test;


import edu.yu.introtoalgs.QuestForOil;
import edu.yu.introtoalgs.QuestForOilBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.TestInfo;


public class QuestForOilTest {

    private Logger logger = LogManager.getLogger(QuestForOil.class.getName());
    private TestUtilities utils = new TestUtilities(logger);


    @Test
    public void demo() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[2][2];
            int i = 0;
            utils.setRowAtATime(i++, "SS", map);
            utils.setRowAtATime(i++, "SS", map);
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            final int retval = qfo.nContiguous(0, 1);
            softAssert.assertEquals(retval, 4, "Mismatch on nContiguous");
        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("demo");
        }
    }
    @Test
    public void testNullMap() {
        assertThrows(IllegalArgumentException.class, () -> {
            QuestForOilBase questForOil = new QuestForOil(null);
        });
    }
    @Test
    public void testAllSafeSquares() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try{
            final char[][] map = new char[4][10];
            int i = 0;
            for (int row = 0; row < map.length; ++row) {
                utils.setRowAtATime(row, "SSSSSSSSSS", map);
            }
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            for (int row = 0; row < map.length; ++row) {
                for (int col = 0; col < map[0].length; ++col) {
                    final int retval = qfo.nContiguous(row, col);
                    softAssert.assertEquals(retval, 40, "Mismatch on nContiguous");
                }
            }
        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testAllSafeSquares");
        }

    }
    @Test
    public void testAllUnsafeSquares() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[4][10];
            int i = 0;
            for (int row = 0; row < map.length; ++row) {
                utils.setRowAtATime(row, "UUUUUUUUUU", map);
            }
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            for (int row = 0; row < map.length; ++row) {
                for (int col = 0; col < map[0].length; ++col) {
                    final int retval = qfo.nContiguous(row, col);
                    softAssert.assertEquals(retval, 0, "Mismatch on nContiguous");
                }
            }
        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testAllUnsafeSquares");
        }
    }
    @Test
    public void testSingleSafeSquare() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[5][5];
            int i = 0;
            utils.setRowAtATime(i++, "UUUUU", map);
            utils.setRowAtATime(i++, "UUUUU", map);
            utils.setRowAtATime(i++, "UUUSU", map);
            utils.setRowAtATime(i++, "UUUUU", map);
            utils.setRowAtATime(i++, "UUUUU", map);
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            //test all squares and make sure the safe square returns 1
            for (int row = 0; row < map.length; ++row) {
                for (int col = 0; col < map[0].length; ++col) {
                    final int retval = qfo.nContiguous(row, col);
                    if (row == 2 && col == 3) {
                        softAssert.assertEquals(retval, 1, "Mismatch on nContiguous");
                    } else {
                        softAssert.assertEquals(retval, 0, "Mismatch on nContiguous");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testSingleSafeSquare");
        }

    }
    @Test
    public void testCorners() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[5][5];
            int i = 0;
            utils.setRowAtATime(i++, "SUUUS", map);
            utils.setRowAtATime(i++, "UUUUU", map);
            utils.setRowAtATime(i++, "UUUUU", map);
            utils.setRowAtATime(i++, "UUUUU", map);
            utils.setRowAtATime(i++, "SUUUS", map);
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            //test all squares and make sure the corners return 1
            for (int row = 0; row < map.length; ++row) {
                for (int col = 0; col < map[0].length; ++col) {
                    final int retval = qfo.nContiguous(row, col);
                    if ((row == 0 && col == 0) || (row == 0 && col == 4) || (row == 4 && col == 0) || (row == 4 && col == 4)) {
                        softAssert.assertEquals(retval, 1, "Mismatch on nContiguous");
                    } else {
                        softAssert.assertEquals(retval, 0, "Mismatch on nContiguous");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testCorners");
        }
    }
    @Test
    public void testBorders() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[5][5];
            int i = 0;
            utils.setRowAtATime(i++, "SSSSS", map);
            utils.setRowAtATime(i++, "SUUUS", map);
            utils.setRowAtATime(i++, "SUUUS", map);
            utils.setRowAtATime(i++, "SUUUS", map);
            utils.setRowAtATime(i++, "SSSSS", map);
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            //test all squares and make sure the borders return 1
            for (int row = 0; row < map.length; ++row) {
                for (int col = 0; col < map[0].length; ++col) {
                    final int retval = qfo.nContiguous(row, col);
                    if (row == 0 || row == 4 || col == 0 || col == 4) {
                        softAssert.assertEquals(retval, 16, "Mismatch on nContiguous");
                    } else {
                        softAssert.assertEquals(retval, 0, "Mismatch on nContiguous");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testBorders");
        }
    }
    @Test
    public void testDiagonals() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[5][5];
            int i = 0;
            utils.setRowAtATime(i++, "SUUUU", map);
            utils.setRowAtATime(i++, "USUUU", map);
            utils.setRowAtATime(i++, "UUSUU", map);
            utils.setRowAtATime(i++, "UUUSU", map);
            utils.setRowAtATime(i++, "UUUUS", map);
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            //test that diagonal squares are counted as contiguous
            for (int row = 0; row < map.length; ++row) {
                for (int col = 0; col < map[0].length; ++col) {
                    final int retval = qfo.nContiguous(row, col);
                    if ((row == 0 && col == 0) || (row == 1 && col == 1) || (row == 2 && col == 2) || (row == 3 && col == 3) || (row == 4 && col == 4)) {
                        softAssert.assertEquals(retval, 5, "Mismatch on nContiguous");
                    } else {
                        softAssert.assertEquals(retval, 0, "Mismatch on nContiguous");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testBorders");
        }
    }

    @Test
    public void testLargeMap() {
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[5000][5000];
            int i = 0;
            for (int row = 0; row < map.length; ++row) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < map[0].length; ++col) {
                    sb.append(Math.random() < 0.5 ? 'S' : 'U');
                }
                utils.setRowAtATime(row, sb.toString(), map);
            }
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);
            for (int j = 0; j < 4; j++) {
                int row = (int) (Math.random() * 5000);
                int col = (int) (Math.random() * 5000);
                final int retval = qfo.nContiguous(row, col);
                softAssert.assertEquals(retval, 1, "Mismatch on nContiguous");
            }
        }catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testLargeMap");
        }
    }
    @Test //will work on later
    public void createMap(){
        final TestUtilities.SoftAssert softAssert = new TestUtilities.SoftAssert();
        try {
            final char[][] map = new char[10][10];
            int i = 0;
            utils.setRowAtATime(i++, "USUUUUUUUU", map);//0
            utils.setRowAtATime(i++, "UUSUSUUSUU", map);//1
            utils.setRowAtATime(i++, "USUUUUUUUU", map);//2
            utils.setRowAtATime(i++, "USUUUUSUUU", map);//3
            utils.setRowAtATime(i++, "UUUSUUUUSU", map);//4
            utils.setRowAtATime(i++, "UUSUUUUSUU", map);//5
            utils.setRowAtATime(i++, "UUUSUUSUUU", map);//6
            utils.setRowAtATime(i++, "UUUUUSUUUU", map);//7
            utils.setRowAtATime(i++, "UUUUSUUUUU", map);//8
            utils.setRowAtATime(i++, "SSSSSUUUUU", map);//9
            utils.logMap(map);
            final QuestForOilBase qfo = new QuestForOil(map);

        } catch (Exception e) {
            logger.error("Problem", e);
            softAssert.fail("Unexpected exception: " + e.toString());
        } finally {
            softAssert.assertAll("testBorders");
        }
    }

}
