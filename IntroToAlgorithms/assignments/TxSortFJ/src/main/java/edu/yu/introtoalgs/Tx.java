package edu.yu.introtoalgs;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Tx extends TxBase{

    private Account sender, receiver;
    private int amount;
    private static AtomicLong id = new AtomicLong(1);
    private LocalDateTime time;

    /**
     * Constructor.
     *
     * @param sender   non-null initiator of the transaction
     * @param receiver non-null recipient
     * @param amount   positive-integer-valued amount transfered in the
     *                 transaction.
     */
    public Tx(Account sender, Account receiver, int amount) {
        super(sender, receiver, amount);

        if(sender == null || receiver == null) throw new IllegalArgumentException("Sender and receiver cannot be null");
        if(amount < 0) throw new IllegalArgumentException("Amount cannot be negative");

        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    @Override
    public Account receiver() {
        return this.receiver;
    }

    @Override
    public Account sender() {
        return this.sender;
    }

    @Override
    public int amount() {
        return this.amount;
    }

    /**
     * Returns a unique non-negative identifier.
     */
    @Override
    public long id() {
        return id.getAndIncrement();
    }

    /**
     * Returns the time that the Tx was created or null.
     */
    @Override
    public LocalDateTime time() {
        return this.time;
    }

    /**
     * Sets the time to null
     */
    @Override
    public void setTimeToNull() {
        this.time = null;
    }

    @Override
    public int compareTo(TxBase other) {
        if(this.time == null && other.time() == null) return 0;
        if(this.time == null) return -1;
        if(other.time() == null) return 1;
        return this.time.compareTo(other.time());
    }
    

}
