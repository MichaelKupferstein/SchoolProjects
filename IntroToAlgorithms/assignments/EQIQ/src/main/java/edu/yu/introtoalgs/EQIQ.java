package edu.yu.introtoalgs;

public class EQIQ extends EQIQBase{

    /**
     * Constructor: supplies the information needed to solve the EQIQ problem.
     * When the constructor invocation completes successfully, clients invocation
     * of every other API method must return in O(1) time.
     *
     * @param totalQuestions
     * @param eqSuccessRate  the ith element of this array specifies the success
     *                       rate of the ith candidate for EQ questions.  Client maintains ownership.
     * @param iqSuccessRate  the ith element of this array specifies the success
     *                       rate of the ith candidate for IQ questions.  Client maintains ownership.
     *                       <p>
     *                       NOTE: the size of the two arrays must be identical, and greater than one.
     * @param nepotismIndex  the index in the above arrays that specifies the
     *                       values of the nepotism candidate.  Candidate indices are numbered
     *                       0..nCandidates -1.
     */
    public EQIQ(int totalQuestions, double[] eqSuccessRate, double[] iqSuccessRate, int nepotismIndex) {
        super(totalQuestions, eqSuccessRate, iqSuccessRate, nepotismIndex);
    }

    /**
     * Return true iff some combination of EQ and IQ questions allow the
     * "nepotism candidate" to win.
     */
    @Override
    public boolean canNepotismSucceed() {
        return false;
    }

    /**
     * If canNepotismSucceed() is true, returns the number of EQ questions
     * (accurate to three decimal places) in the optimal configuration for the
     * nepotism candidate; otherwise, returns -1.0
     */
    @Override
    public double getNumberEQQuestions() {
        return 0;
    }

    /**
     * If canNepotismSucceed() is true, returns the number of IQ questions
     * (accurate to three decimal places) in the optimal configuration for the
     * neptotism candidate; otherwise, returns -1.0
     */
    @Override
    public double getNumberIQQuestions() {
        return 0;
    }

    /**
     * If canNepotismSucceed() is true, returns the number of SECONDS (accurate
     * to three decimal places) by which the nepotism candidate completes the
     * test ahead of the next best candidate; ootherwise, returns -1.0
     */
    @Override
    public double getNumberOfSecondsSuccess() {
        return 0;
    }
}
