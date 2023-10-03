package edu.yu.introtoalgs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BigOIt2 extends BigOIt2Base{

    private Class<?> algClass;
    private BigOMeasurable alg;
    private Method setupMethod;
    private Method executeMethod;
    private ConcurrentHashMap<Integer, Double> times = new ConcurrentHashMap<>();
    private int count = 0;
    private boolean moreThanOne = false;


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
        long methodStartTime = System.currentTimeMillis();
        //Reflection Stuff
        try {
            this.algClass = Class.forName(bigOMeasurable);
            this.alg = (BigOMeasurable) algClass.newInstance();
            this.setupMethod = algClass.getMethod("setup", int.class);
            this.executeMethod = algClass.getMethod("execute");
        } catch (Exception e) {throw new RuntimeException(e);}

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        executor.schedule(() -> {
            //System.out.println("Task executed after " + timeOutInMs + " milliseconds.");
            executor.shutdownNow();
        }, timeOutInMs, TimeUnit.MILLISECONDS);


        for(int i = 0; i < 10; i++) {
            executor.execute(() -> {
                //System.out.println("Working on thread" + Thread.currentThread().getName());
                double prev = timeTrial(125);
                for (int N = 250; true; N += N) {
                    double time = timeTrial(N);
                    double ratio = time/prev;
                    //System.out.printf("%6d %7.1f", N, time);
                    //System.out.printf("%5.1f\n", time / prev);
                    times.put(N,times.getOrDefault(N,0.0)+ratio);
                    prev = time;
                }
            });
        }


        while(!executor.isTerminated()){
            if (System.currentTimeMillis() - methodStartTime > timeOutInMs) {
                //System.out.println("Task timed out.");
                executor.shutdownNow();
                break;
            }
        }

        //System.out.println("Finished all threads");

        times.forEach((k,v) -> times.put(k, (double) Math.round(v/10.0)));
        times.forEach((k,v) -> System.out.println(k + " " + v));
        double mode = mode(new ArrayList<>(times.values()));
        System.out.println("Count: " + count);
        System.out.println("More than one: " + moreThanOne);
        System.out.println("Mode: " + mode);

        if(!moreThanOne || count < 5){
            return Double.NaN;
        }else{
           return mode;
        }

    }

    private double timeTrial(int N) {
        try {
            setupMethod.invoke(alg, N);
            long startTime = System.currentTimeMillis();
            executeMethod.invoke(alg);
            long endTime = System.currentTimeMillis();
            return (endTime - startTime) / 1000.0;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private double mode(List<Double> nums){
        Map<Double, Integer> map = new HashMap<>();
        for(Double num : nums){
            map.put(num, map.getOrDefault(num, 0) + 1);
            if(num != 0.0){
                count++;
            }
        }
        int max = 0;
        double mode = 0;
        for(Map.Entry<Double, Integer> entry : map.entrySet()){
            if(entry.getValue() > max && entry.getKey() != 0.0){
                max = entry.getValue();
                mode = entry.getKey();
            }
        }
        if(map.get(mode) > 1){
            moreThanOne = true;
        }
        return mode;
    }


}
