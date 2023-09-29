package edu.yu.introtoalgs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

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
    public double doublingRatio(String bigOMeasurable, long timeOutInMs){

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

        //Timer Stuff
        TimerThread timer = new TimerThread(timeOutInMs);

        //Thread Stuff
        int n = 1000;
        AlgThread algThread = new AlgThread(alg, setup, execute, n);

        //Doubling Ratio Stuff
        int count = 0;
        int totalAlgTime = 0;


        try {
            timer.start();
            while(!timer.isFinished()){
                algThread.start();
                algThread.join();
                algThread = new AlgThread(alg, setup, execute, n);
                totalAlgTime += algThread.executeTime();
                n *= 2;
                count++;
            }
            double avgAlgTime = (double) totalAlgTime / count;

            if(count <= 25){
                return Double.NaN; //not enough data
            }else{
                return avgAlgTime;
            }

        } catch (Exception e) {throw new RuntimeException(e);}

    }

    private class AlgThread extends Thread{
        private BigOMeasurable alg;
        private Method setup;
        private Method execute;
        private int n;
        private long time;
        public AlgThread(BigOMeasurable alg, Method setup, Method execute, int n){
            this.alg = alg;
            this.setup = setup;
            this.execute = execute;
            this.n = n;
        }

        @Override
        public void run(){
            try {
                setup.invoke(alg, n);
                long startTime = System.currentTimeMillis();
                execute.invoke(alg);
                long endTime = System.currentTimeMillis();
                this.time = endTime - startTime;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private long executeTime(){
            return this.time;
        }


    }


    private class TimerThread extends Thread {
        long timeOutInMs;
        boolean isDone = false;
        public TimerThread(long timeOutInMs){
            this.timeOutInMs = timeOutInMs;
        }

        public boolean isFinished(){
            return this.isDone;
        }
        @Override
        public void run(){
            try {
                Thread.sleep(timeOutInMs);
                this.isDone = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
