package edu.yu.introtoalgs.testAlgs;

import edu.yu.introtoalgs.BigOMeasurable;

public class ConstantTime extends BigOMeasurable {

    private int[] arr;

    @Override
    public void setup(int n) {
        arr = new int[n];
    }

    @Override
    public void execute() {
        int x = arr[0];
    }

}
