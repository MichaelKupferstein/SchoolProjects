package edu.yu.introtoalgs;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BigOIt2_1 extends BigOIt2Base{


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
        Class<?> algClass;
        BigOMeasurable alg;
        Method setup;
        Method execute;
        try {
            algClass = Class.forName(bigOMeasurable);
            alg = (BigOMeasurable) algClass.newInstance();
            setup = algClass.getMethod("setup", int.class);
            execute = algClass.getMethod("execute");
        } catch (Exception e) {throw new RuntimeException(e);}

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);


        Timer timer = new Timer();
        //Submit the timer to the executor with a timeout of timeOutInMs





        while(!executor.isTerminated()){

        }



        return 0;
    }

    public static void main(String args[]){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        int delayInSeconds = 5;
        executor.schedule(() -> {
            // Code to be executed after the specified delay
            System.out.println("Task executed after " + delayInSeconds + " seconds.");

            // Shut down the executor after the countdown task is completed
            executor.shutdown();
        }, delayInSeconds, TimeUnit.SECONDS);

        System.out.println("did stuff here");

        // Concurrent tasks can be submitted to the executor for execution
        AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < 100 ; i++) {
            executor.execute(() -> {
                // Code for other tasks to be executed concurrently
                System.out.println( Thread.currentThread().getName() + " Concurrent task executed." + count.getAndIncrement());

            });
        }

        while(!executor.isTerminated()){

        }
        System.out.println("did stuff here also ");
    }
}
