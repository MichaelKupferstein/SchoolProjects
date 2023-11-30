package edu.yu.introtoalgs;

import java.util.List;

public class TxSortFJ extends TxSortFJBase{
    /**
     * Constructor.
     *
     * @param transactions a list of transactions, possibly not sorted.
     */
    public TxSortFJ(List<TxBase> transactions) {
        super(transactions);
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
        return new TxBase[0];
    }
}
