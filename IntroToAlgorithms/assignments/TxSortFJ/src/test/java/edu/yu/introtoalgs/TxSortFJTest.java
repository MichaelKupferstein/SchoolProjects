package edu.yu.introtoalgs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;
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
    @Test
    void TestWith18_000_000(){
        TestWithInputs(1_000_000, 18_000_000);
    }

    @Test
    void TestWithNullTxTime(){
        final List<TxBase> txs = new ArrayList<>();
        final Account[] accounts = new Account[10];
        for (int i = 0; i < 10; ++i) {
            accounts[i] = new Account();
        }
        logger.info("Created {} accounts", 10);
        for(int i = 0; i < 10; i++) {
            //being silly here: no point in making this look more real
            final Account account1 = accounts[random.nextInt(0, 10)];
            final Account account2 = accounts[random.nextInt(0, 10)];
            txs.add(new Tx(account1, account2, 1));
        }
        int randomIndex = random.nextInt(0, 10);
        txs.get(randomIndex).setTimeToNull();
        logger.info("Nullified time of Tx at index {}", randomIndex);


        TxBase[] copyOfTxs = txs.toArray(new TxBase[0]);
        final long beforeNaive = System.currentTimeMillis();
        Arrays.sort(copyOfTxs);
        final long afterNaive = System.currentTimeMillis();
        logger.info("Basic sorting took {} ms", afterNaive - beforeNaive);

        TxBase[] copyOfTxs2 = txs.toArray(new TxBase[0]);
        final long beforePar  = System.currentTimeMillis();
        Arrays.parallelSort(copyOfTxs2);
        final long afterPar = System.currentTimeMillis();
        logger.info("Parallel sorting took {} ms", afterPar - beforePar);

        try {
            final TxSortFJBase txSortFJ = new TxSortFJ(txs);
            final long before = System.currentTimeMillis();
            final TxBase[] sortedTxs = txSortFJ.sort();
            final long after = System.currentTimeMillis();
            logger.info("Sorting took {} ms", after - before);
            final boolean isSorted = isSorted(sortedTxs);
            logger.info("isSorted: {}", isSorted);
            assertTrue(isSorted);
            assertTrue(sortedTxs[0].time()==null);
        }catch (Exception e) {
            final String msg = "Unexpected exception running test: ";
            logger.error(msg, e);
            fail(msg + e.toString());
        }finally {
            Configurator.shutdown(null);
        }

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

        TxBase[] copyOfTxs = txs.toArray(new TxBase[0]);
        final long beforeNaive = System.currentTimeMillis();
        Arrays.sort(copyOfTxs);
        final long afterNaive = System.currentTimeMillis();
        logger.info("Basic sorting took {} ms", afterNaive - beforeNaive);

        TxBase[] copyOfTxs2 = txs.toArray(new TxBase[0]);
        final long beforePar  = System.currentTimeMillis();
        Arrays.parallelSort(copyOfTxs2);
        final long afterPar = System.currentTimeMillis();
        logger.info("Parallel sorting took {} ms", afterPar - beforePar);

        try {
            final TxSortFJBase txSortFJ = new TxSortFJ(txs);
            final long before = System.currentTimeMillis();
            final TxBase[] sortedTxs = txSortFJ.sort();
            final long after = System.currentTimeMillis();
            logger.info("Sorting took {} ms", after - before);
            final boolean isSorted = isSorted(sortedTxs);
            logger.info("isSorted: {}", isSorted);
            assertTrue(isSorted);
            assertTrue(after - before <= afterNaive - beforeNaive);
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
            if(sortedTxs[i].compareTo(sortedTxs[i+1]) > 0) return false;
        }
        return true;
    }
}