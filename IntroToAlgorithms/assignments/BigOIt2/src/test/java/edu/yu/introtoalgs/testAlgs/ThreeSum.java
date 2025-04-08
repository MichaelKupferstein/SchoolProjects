package edu.yu.introtoalgs.testAlgs;

import edu.yu.introtoalgs.BigOMeasurable;

import java.util.Random;

public class ThreeSum extends BigOMeasurable {

    private int[] a;

    public static int count(int[] a){
        int N= a.length;
        int cnt = 0;
        for(int i = 0; i < N; i++){
            for(int j = i+1; j < N; j++){
                for(int k = j+1; k < N; k++){
                    if(a[i] + a[j] + a[k] == 0){
                        cnt++;
                    }
                }
            }
        }
        return cnt;
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
        count(a);
    }

    @Override
    public void setup(int n){
        this.n = n;
        int MAX = 1000000;
        a = new int[n];
        for(int i = 0; i < n; i++){
            a[i] = (int) (Math.random() * MAX);
        }
    }
}
