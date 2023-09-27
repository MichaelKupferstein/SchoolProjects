package edu.yu.introtoalgs;

import java.lang.reflect.Method;

public class BigOIt2 extends BigOIt2Base{
    /**
     * Given the name of a class that implements the BigOMeasurable API, creates
     * and executes instances of the class, such that by measuring the resulting
     * performance, can return the "doubling ratio" for that algorithm's
     * performance.
     * <p>
     * See extended discussion in Sedgewick, Chapter 1.4, on the topic of
     * doubling ratio experiments.
     *
     * @param bigOMeasurable name of the class for which we want to compute a
     *                       doubling ratio.  The client claims that the named class implements the
     *                       BigOMeasurable API, and can be constructed with a no-argument constructor.
     *                       This method is therefore able to (1) construct instances of the named
     *                       class, (2) invoke "setup(n)" for whatever values of "n" are desired, and
     *                       (3) then invoke "execute()" to measure the performance of a single
     *                       invocation of the algorithm.  The client is responsible for ensuring that
     *                       invocation of setup(n) produces a suitably populated (perhaps randomized)
     *                       set of state scaled as a function of n.
     * @param timeOutInMs    number of milliseconds allowed for the computation.  If
     *                       the implementation has not computed an answer by this time, it should
     *                       return NaN.
     * @return the doubling ratio for the specified algorithm if one can be
     * calculated, NaN otherwise.
     * @throws IllegalArgumentException if bigOMeasurable parameter doesn't
     *                                  fulfil the contract specified above or if some characteristic of the
     *                                  algorithm is at odds with the doubling ratio assumptions.
     */
    @Override
    public double doublingRatio(String bigOMeasurable, long timeOutInMs) {

        try {
            Class<?> algClass = Class.forName(bigOMeasurable);
            BigOMeasurable alg = (BigOMeasurable) algClass.newInstance();
            Method setup = algClass.getMethod("setup", int.class);
            Method execute = algClass.getMethod("execute");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }



        return Double.NaN;
    }
}
