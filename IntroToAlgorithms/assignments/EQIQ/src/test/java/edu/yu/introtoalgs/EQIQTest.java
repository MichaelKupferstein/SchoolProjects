package edu.yu.introtoalgs;

import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class EQIQTest {

    @Test
    void testFromDoc(){
        int totalQuestions = 2;
        int nCandidates = 2;
        double[] eqSuccessRate = {10.0, 20.0};
        double[] iqSuccessRate = {40.0, 40.0};
        int nepotismIndex = 1;
        final double DELTA = 0.001;
        EQIQBase eqiq = new EQIQ(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
        assertTrue(eqiq.canNepotismSucceed());
        assertEquals(2.0, eqiq.getNumberEQQuestions(), DELTA);
        assertEquals(0.0, eqiq.getNumberIQQuestions(), DELTA);
        assertEquals(360, eqiq.getNumberOfSecondsSuccess(), DELTA);
    }

    @Test
    void ownTest(){
        int totalQuestions = 2;
        int nCandidates = 3;
        double[] eqSuccessRate = {10.0, 20.0,15.0};
        double[] iqSuccessRate = {40.0, 40.0,40.0};
        int nepotismIndex = 1;
        final double DELTA = 0.001;
        EQIQBase eqiq = new EQIQ(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
        assertTrue(eqiq.canNepotismSucceed());
        assertEquals(2.0, eqiq.getNumberEQQuestions(), DELTA);
        assertEquals(0.0, eqiq.getNumberIQQuestions(), DELTA);
        assertEquals(120, eqiq.getNumberOfSecondsSuccess(), DELTA);
    }
    @Test
    void testWithMoreQuestions(){
        int totalQuestions = 5;
        int nCandidates = 3;
        double[] eqSuccessRate = {10.0, 20.0,15.0};
        double[] iqSuccessRate = {40.0, 40.0,40.0};
        int nepotismIndex = 1;
        final double DELTA = 0.001;
        EQIQBase eqiq = new EQIQ(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
        assertTrue(eqiq.canNepotismSucceed());
        assertEquals(5.0, eqiq.getNumberEQQuestions(), DELTA);
        assertEquals(0.0, eqiq.getNumberIQQuestions(), DELTA);
        assertEquals(300, eqiq.getNumberOfSecondsSuccess(), DELTA);
    }

    @Test
    void testWithDifferntNep(){
        int totalQuestions = 2;
        int nCandidates = 3;
        double[] eqSuccessRate = {10.0, 20.0,15.0,30.0};
        double[] iqSuccessRate = {40.0, 40.0,40.0,40.0};
        int nepotismIndex = 3;
        final double DELTA = 0.001;
        EQIQBase eqiq = new EQIQ(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
        assertTrue(eqiq.canNepotismSucceed());
        assertEquals(2.0, eqiq.getNumberEQQuestions(), DELTA);
        assertEquals(0.0, eqiq.getNumberIQQuestions(), DELTA);
        assertEquals(120, eqiq.getNumberOfSecondsSuccess(), DELTA);
    }

    @Test
    void testWithTensOfCandidates(){
        int totalQuestions = 2;
        int nCandidates = 10;
        double[] eqSuccessRate = {10.0, 20.0,15.0,30.0,10.0, 20.0,15.0,30.0,10.0, 20.0};
        double[] iqSuccessRate = {40.0, 40.0,40.0,40.0,40.0, 40.0,40.0,40.0,40.0, 40.0};
        int nepotismIndex = 3;
        final double DELTA = 0.001;
        EQIQBase eqiq = new EQIQ(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
        assertFalse(eqiq.canNepotismSucceed());
        assertEquals(-1, eqiq.getNumberEQQuestions(), DELTA);
        assertEquals(-1.0, eqiq.getNumberIQQuestions(), DELTA);
        assertEquals(-1, eqiq.getNumberOfSecondsSuccess(), DELTA);
    }

    //create a test with 50 candidates and a lot of questions, and nepotism succceeds and everyone has different rates
    @Test
    void testWithFiftyCandidates(){
        int totalQuestions = 50;
        int nCandidates = 50;
        double[] eqSuccessRate = new double[50];
        double[] iqSuccessRate = new double[50];
        for(int i = 0; i < 50; i++){
            eqSuccessRate[i] = i;
            iqSuccessRate[i] = i;
        }
        int nepotismIndex = 49;
        final double DELTA = 0.001;
        EQIQBase eqiq = new EQIQ(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
        assertTrue(eqiq.canNepotismSucceed());
        assertEquals(50.0, eqiq.getNumberEQQuestions(), DELTA);
        assertEquals(0.0, eqiq.getNumberIQQuestions(), DELTA);
        assertEquals(76.530, eqiq.getNumberOfSecondsSuccess(), DELTA);
    }

    @Test
    void testHigherEQlowerIQ(){
        int totalQuestions = 2;
        int nCandidates = 2;
        double[] eqSuccessRate = {20.0, 20.0};
        double[] iqSuccessRate = {40.0, 30.0};
        int nepotismIndex = 1;
        final double DELTA = 0.001;
        EQIQBase eqiq = new EQIQ(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
        assertFalse(eqiq.canNepotismSucceed());

    }

}