package edu.yu.parallel;

import java.util.*;

public class RWLock implements RWLockInterface {

    private Thread owner = null;
    private int writeCounter = 0;
    private int waitingWriters = 0;
    private final ThreadLocal<Integer> readCounter = ThreadLocal.withInitial(() -> 0);
    private int totalReaders = 0;

    private final Queue<Thread> waitQueue = new LinkedList<>();
    private final Set<Thread> waitingReaders = new HashSet<>();


    @Override
    public synchronized void lockRead() {
        Thread cur = Thread.currentThread();
        if(owner == cur || readCounter.get() > 0 ) {
            readCounter.set(readCounter.get() + 1);
            return;
        }

       boolean shouldWait = owner != null || waitingWriters > 0;

        if(shouldWait){
            waitQueue.offer(cur);
            waitingReaders.add(cur);

            try{
                while(owner != null || (waitingWriters > 0 && !isNextReader())){
                    wait();
                }
            } catch (InterruptedException e) {
                waitQueue.remove(cur);
                waitingReaders.remove(cur);
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            waitQueue.remove(cur);
            waitingReaders.remove(cur);
        }

        readCounter.set(1);
        totalReaders++;

    }

    private boolean isNextReader(){
        Thread next = waitQueue.peek();
        return next != null && waitingReaders.contains(next);
    }

    @Override
    public synchronized void lockWrite() {
        Thread cur = Thread.currentThread();
        if(owner == cur){
            writeCounter++;
            return;
        }
        if(readCounter.get() >0){
            throw new IllegalMonitorStateException("Cant acquire write lock while holding read lock");
        }
        waitingWriters++;

        if(owner != null || totalReaders >0){
            waitQueue.offer(cur);

            try {
                while(owner != null || totalReaders > 0 || waitQueue.peek() != cur){
                    wait();
                }
            }catch (InterruptedException e){
                waitQueue.remove(cur);
                waitingWriters--;
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            waitQueue.remove(cur);
        }
        owner = cur;
        writeCounter = 1;
    }

    @Override
    public synchronized void unlock() {
        Thread cur = Thread.currentThread();

        if(owner == cur){
            writeCounter--;
            if(writeCounter == 0){
                owner = null;
                notifyAll();
            }
            return;
        }

        int count = readCounter.get();
        if(count == 0){
            throw new IllegalMonitorStateException("Thread does not own lock");
        }
        count--;
        if(count ==0){
            readCounter.remove();
            totalReaders--;
            if(totalReaders == 0){
                notifyAll();
            }
        }else{
            readCounter.set(count);
        }
    }


}
