package edu.yu.introtoalgs;

public class Factorial extends BigOMeasurable{
    public int factorial(int n) {
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Performs a single execution of an algorithm: MAY ONLY be invoked after
     * setup() has previously been invoked.  The algorithm must scale as a
     * function of the parameter "n" supplied to setup().
     * <p>
     * NOTE: ONLY the duration of this method should be considered when
     * evaluating algorithm performance.
     */
    @Override
    public void execute() {
        factorial(n);
    }
}
