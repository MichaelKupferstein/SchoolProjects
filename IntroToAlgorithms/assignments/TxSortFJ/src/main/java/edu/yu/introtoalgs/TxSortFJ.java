package edu.yu.introtoalgs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class TxSortFJ extends TxSortFJBase{

    private List<TxBase> transactions;
    private TxBase[] txs;
    private SortTasks sortTasks;

    /**
     * Constructor.
     *
     * @param transactions a list of transactions, possibly not sorted.
     */
    public TxSortFJ(List<TxBase> transactions) {
        super(transactions);
        if(transactions == null) throw new IllegalArgumentException("Transactions cannot be null");
        this.transactions = transactions;
        Collections.shuffle(transactions);
        this.txs = transactions.toArray(new TxBase[0]);
        this.sortTasks = new SortTasks(txs);
    }

    /**
     * Returns an array of transactions, sorted in ascending order of
     * TxBase.time() values: any instances with null TxBase.time() values precede
     * all other transaction instances in the sort results.
     *
     * @return the transaction instances passed to the constructor, returned as
     * an array, and sorted as specified above.  Students MAY ONLY use the
     * ForkJoin and their own code in their implementation.
     */
    @Override
    public TxBase[] sort() {
        if (this.txs.length <= 1) return this.txs;
        sortTasks.compute();
        return txs;
    }

    private class SortTasks extends RecursiveAction {

        private TxBase[] txs;
        public static final int CUTOFF = 10_000;

        public SortTasks(TxBase[] txs) {
            this.txs = txs;
        }

        /**
         * The main computation performed by this task.
         */
        @Override
        protected void compute() {
            if(txs.length <= CUTOFF){
                Arrays.sort(txs);
                return;
            }
            int mid = txs.length / 2;

            TxBase[] left = Arrays.copyOfRange(txs, 0, mid);
            TxBase[] right = Arrays.copyOfRange(txs, mid, txs.length);

            invokeAll(new SortTasks(left), new SortTasks(right));
            merge(left, right);
        }

        private void merge(TxBase[] left, TxBase[] right){
            int leftIndex = 0; int rightIndex = 0; int currentIndex = 0;

            while(leftIndex < left.length && rightIndex < right.length){
                if(left[leftIndex].compareTo(right[rightIndex]) <= 0){
                    txs[currentIndex++] = left[leftIndex++];
                } else {
                    txs[currentIndex++] = right[rightIndex++];
                }
            }

            while(leftIndex < left.length){
                txs[currentIndex++] = left[leftIndex++];
            }
            while(rightIndex < right.length){
                txs[currentIndex++] = right[rightIndex++];
            }

        }
    }
}
