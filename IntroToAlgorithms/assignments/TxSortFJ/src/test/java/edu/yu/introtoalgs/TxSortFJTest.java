package edu.yu.introtoalgs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.TestInfo;



class TxSortFJTest {

    private long startTime;
    private Logger logger = LogManager.getLogger(TxSortFJTest.class.getName());
    private Random random = new Random();

    @BeforeEach
    public void beforeEach() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    public void afterEach(TestInfo testInfo) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String testName = testInfo.getTestMethod().get().getName();
        logger.info("Test {} took {} ms", testName, duration);
    }

    @Test
    void TestFromDocs(){
        TestWithInputs(2, 5);
    }
    @Test
    void TestWith1000(){
        TestWithInputs(1000, 1000);
    }
    @Test
    void TestWith10000(){
        TestWithInputs(10000, 10000);
    }
    @Test
    void TestWith9_000_000Txs(){
        TestWithInputs(1_000_000, 9_000_000);
    }
    void TestWithInputs(int nAccount, int nTxs){
        final List<TxBase> txs = new ArrayList<>();
        final Account[] accounts = new Account[nAccount];
        for (int i = 0; i < nAccount; ++i) {
            accounts[i] = new Account();
        }
        logger.info("Created {} accounts", nAccount);
        for(int i = 0; i < nTxs; i++) {
            //being silly here: no point in making this look more real
            final Account account1 = accounts[random.nextInt(0, nAccount)];
            final Account account2 = accounts[random.nextInt(0, nAccount)];
            txs.add(new Tx(account1, account2, 1));
        }
        Collections.shuffle(txs);
        logger.info("Created {} Txs", txs.size());

        List<TxBase> copyOfTxs = new ArrayList<>(txs);
        final long beforeNaive = System.currentTimeMillis();
        Collections.sort(copyOfTxs);
        final long afterNaive = System.currentTimeMillis();
        logger.info("Basic sorting took {} ms", afterNaive - beforeNaive);

        try {
            final TxSortFJBase txSortFJ = new TxSortFJ(txs);
            final long before = System.currentTimeMillis();
            final TxBase[] sortedTxs = txSortFJ.sort();
            final long after = System.currentTimeMillis();
            logger.info("Sorting took {} ms", after - before);
            final boolean isSorted = isSorted(sortedTxs);
            logger.info("isSorted: {}", isSorted);
        }catch (Exception e) {
            final String msg = "Unexpected exception running test: ";
            logger.error(msg, e);
            fail(msg + e.toString());
        }finally {
            Configurator.shutdown(null);
        }
    }

    private boolean isSorted(TxBase[] sortedTxs) {
        for(int i = 0; i < sortedTxs.length - 1; i++){
            if(sortedTxs[i].time().compareTo(sortedTxs[i+1].time()) > 0) return false;
        }
        return true;
    }
}