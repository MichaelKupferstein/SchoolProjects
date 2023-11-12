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
}