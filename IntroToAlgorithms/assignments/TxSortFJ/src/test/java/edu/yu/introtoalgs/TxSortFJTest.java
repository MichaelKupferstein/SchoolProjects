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
    void TestFromDoc(){
        final int nAccounts = 2;
        final int nTxs = 5;
        final List<TxBase> txs = new ArrayList<>();
        final Account[] accounts = new Account[nAccounts];
        for (int i = 0; i < nAccounts; ++i) {
            accounts[i] = new Account();
        }
        logger.info("Created {} accounts", nAccounts);
        for(int i = 0; i < nTxs; i++) {
            //being silly here: no point in making this look more real
            final Account account1 = accounts[random.nextInt(0, nAccounts)];
            final Account account2 = accounts[random.nextInt(0, nAccounts)];
            txs.add(new Tx(account1, account2, 1));
        }
        Collections.shuffle(txs);
        logger.info("Created {} Txs", txs.size());

        try {
            final TxSortFJBase txSortFJ = new TxSortFJ(txs);
            final TxBase[] sortedTxs = txSortFJ.sort();
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