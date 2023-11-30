package edu.yu.introtoalgs;

import java.time.LocalDateTime;

public class Tx extends TxBase{
    /**
     * Constructor.
     *
     * @param sender   non-null initiator of the transaction
     * @param receiver non-null recipient
     * @param amount   positive-integer-valued amount transfered in the
     *                 transaction.
     */
    Tx(Account sender, Account receiver, int amount) {
        super(sender, receiver, amount);
    }

    @Override
    public Account receiver() {
        return null;
    }

    @Override
    public Account sender() {
        return null;
    }

    @Override
    public int amount() {
        return 0;
    }

    /**
     * Returns a unique non-negative identifier.
     */
    @Override
    public long id() {
        return 0;
    }

    /**
     * Returns the time that the Tx was created or null.
     */
    @Override
    public LocalDateTime time() {
        return null;
    }

    /**
     * Returns the time that the Tx was created or null.
     */
    @Override
    public void setTimeToNull() {

    }
}
