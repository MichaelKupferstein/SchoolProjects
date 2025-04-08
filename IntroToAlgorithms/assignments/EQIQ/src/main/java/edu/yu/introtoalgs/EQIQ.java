package edu.yu.introtoalgs;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class EQIQ extends EQIQBase{

    private int totalQuestions,nepotismIndex,bestOtherCandidateIndex;
    private double[] eqRate,iqRate;

    private boolean canNepotismSucceed = false;

    private double bestIQ,bestEQ,time;

    /** Constructor: supplies the information needed to solve the EQIQ problem.
     * When the constructor invocation completes successfully, clients invocation
     * of every other API method must return in O(1) time.
     *
     * @param totalQuestion the number of questions on the candidate interview
     * test, must be greater than 1
     * @param eqSuccessRate the ith element of this array specifies the success
     * rate of the ith candidate for EQ questions.  Client maintains ownership.
     * @param iqSuccessRate the ith element of this array specifies the success
     * rate of the ith candidate for IQ questions.  Client maintains ownership.
     *
     * NOTE: the size of the two arrays must be identical, and greater than one.
     * @param nepotismIndex the index in the above arrays that specifies the
     * values of the nepotism candidate.  Candidate indices are numbered
     * 0..nCandidates -1.
     */
    public EQIQ(int totalQuestions, double[] eqSuccessRate, double[] iqSuccessRate, int nepotismIndex) {
        super(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);

        if(totalQuestions < 1) throw new IllegalArgumentException("Total questions must be greater than 1");
        if(eqSuccessRate.length != iqSuccessRate.length) throw new IllegalArgumentException("EQ and IQ arrays must be the same length");
        if(eqSuccessRate.length < 1 || iqSuccessRate.length < 1) throw new IllegalArgumentException("EQ and IQ arrays must be greater than 1");
        if(nepotismIndex < 0 || nepotismIndex > eqSuccessRate.length - 1) throw new IllegalArgumentException("Nepotism index must be between 0 and the length of the EQ and IQ arrays");

        this.totalQuestions = totalQuestions;
        this.eqRate = eqSuccessRate;
        this.iqRate = iqSuccessRate;
        this.nepotismIndex = nepotismIndex;

        calculateBestNepotismScore();
        if(canNepotismSucceed){
            calculateTime();
        }

    }

    private void calculateBestNepotismScore(){
        double bestScore = 0;
        double bestEQ = 0;
        double bestIQ = 0;
        double bestOtherScore = 0;
        for(int iq = 0,eq = totalQuestions; iq <= totalQuestions; eq--,iq++){
            double Qeq = eqRate[nepotismIndex] * eq;
            double Qiq = iqRate[nepotismIndex] * iq;
            double score = Qeq + Qiq;//calculate score for given amount of questions
            for(int i = 0; i < eqRate.length; i++){//calcuate score for other candidates
                if(i != nepotismIndex){
                    double otherEq = eqRate[i] * eq;
                    double otherIq = iqRate[i] * iq;
                    double otherScore = otherEq + otherIq;
                    if(otherScore > bestOtherScore){
                        bestOtherScore = otherScore;//save the best score for the other candidates
                        this.bestOtherCandidateIndex = i;
                    }
                }
            }
            if(score > bestOtherScore){
                canNepotismSucceed = true;
                bestScore = score;
                bestEQ = eq;
                bestIQ = iq;
                this.bestEQ = eq;
                this.bestIQ = iq;
                break;
            }
        }
        System.out.printf("EQ: %.3f, IQ: %.3f, Bestscore: %.3f\n", bestEQ, bestIQ, bestScore);
    }

    private void calculateTime(){
        double nepoEQ = eqRate[nepotismIndex];
        double nepoIQ = iqRate[nepotismIndex];

        double otherEQ = eqRate[bestOtherCandidateIndex];
        double otherIQ = iqRate[bestOtherCandidateIndex];

        double timeForEQ = 1/nepoEQ;
        double timeForIQ = 1/nepoIQ;
        double nepoEQT = timeForEQ * bestEQ;
        double nepoIQT = timeForIQ * bestIQ;

        double otherTimeForEQ = 1/otherEQ;
        double otherTimeForIQ = 1/otherIQ;
        double otherEQT = otherTimeForEQ * bestEQ;
        double otherIQT = otherTimeForIQ * bestIQ;

        double nepoTime = nepoEQT + nepoIQT;
        double otherTime = otherEQT + otherIQT;

        this.time = (otherTime - nepoTime)*3600;

    }

    /**
     * Return true iff some combination of EQ and IQ questions allow the
     * "nepotism candidate" to win.
     */
    @Override
    public boolean canNepotismSucceed() {
        return this.canNepotismSucceed;
    }

    /**
     * If canNepotismSucceed() is true, returns the number of EQ questions
     * (accurate to three decimal places) in the optimal configuration for the
     * nepotism candidate; otherwise, returns -1.0
     */
    @Override
    public double getNumberEQQuestions() {
        if(!this.canNepotismSucceed){
            return -1.0;
        }
        return bestEQ;
    }

    /**
     * If canNepotismSucceed() is true, returns the number of IQ questions
     * (accurate to three decimal places) in the optimal configuration for the
     * neptotism candidate; otherwise, returns -1.0
     */
    @Override
    public double getNumberIQQuestions() {
        if(!this.canNepotismSucceed){
            return -1.0;
        }
        return bestIQ;
    }

    /**
     * If canNepotismSucceed() is true, returns the number of SECONDS (accurate
     * to three decimal places) by which the nepotism candidate completes the
     * test ahead of the next best candidate; ootherwise, returns -1.0
     */
    @Override
    public double getNumberOfSecondsSuccess() {//well if we know how his success rate on EQ and IQ quesitons per hour
        // we can just find out how long he takes per question and also the next best candidate and then subtract
        if(!this.canNepotismSucceed){
            return -1.0;
        }
        return time;
    }

}
