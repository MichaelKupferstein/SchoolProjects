package edu.yu.introtoalgs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BigOIt2 extends BigOIt2Base{

    private Class<?> algClass;
    private BigOMeasurable alg;
    private Method setupMethod;
    private Method executeMethod;

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
        //Reflection Stuff
        try {
            this.algClass = Class.forName(bigOMeasurable);
            this.alg = (BigOMeasurable) algClass.newInstance();
            this.setupMethod = algClass.getMethod("setup", int.class);
            this.executeMethod = algClass.getMethod("execute");
        } catch (Exception e) {throw new RuntimeException(e);}

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            // Shut down the executor after the countdown task is completed
            executor.shutdown();
        }, timeOutInMs, TimeUnit.MILLISECONDS);



        executor.execute(() -> {
            try {
                double prev = timeTrial(1000);

                for(int N = 2000; true; N+=N){
                    double time = timeTrial(N);
                    //double ratio = time/prev;
                    System.out.printf("%6d %7.1f\n", N, time);
                    System.out.printf("%5.1f\n", time/prev);
                    prev = time;
                }


            } catch (IllegalAccessException | InvocationTargetException e) {throw new RuntimeException(e);}
        });


        while(!executor.isTerminated()){}




        return 0;
    }

    private double timeTrial(int N) throws InvocationTargetException, IllegalAccessException {
        setupMethod.invoke(alg, N);
        long startTime = System.currentTimeMillis();
        executeMethod.invoke(alg);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

}
