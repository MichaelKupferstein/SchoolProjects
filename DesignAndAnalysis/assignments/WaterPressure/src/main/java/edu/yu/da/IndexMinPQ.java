package edu.yu.da;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Code from Algorithms, 4th Edition by Robert Sedgewick and Kevin Wayne
 * https://algs4.cs.princeton.edu/24pq/IndexMinPQ.java.html
 * Modified to work with string keys
 * */

public class IndexMinPQ<Key extends Comparable<Key>>{

    private int maxN; // maximum number of elements on PQ
    private int n; // number of elements on PQ
    private String[] pq; // binary heap using 1-based indexing
    private HashMap<String, Integer> qp; // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private HashMap<String, Key> keys; // keys[i] = priority of i

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     *
     * @param  maxN the keys on this priority queue are index from {@code 0}
     *         {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN} < 0
     */
    public IndexMinPQ(int maxN){
        if(maxN < 0) throw new IllegalArgumentException();
        this.maxN = maxN;
        n = 0;
        keys = new HashMap<>();
        pq = new String[maxN + 1];
        qp = new HashMap<>();
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty(){
        return n == 0;
    }

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param  i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(String i){
        validateIndex(i);
        return qp.containsKey(i);
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size(){
        return n;
    }

    /**
     * Associates key with index {@code i}.
     *
     * @param  i an index
     * @param  key the key to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *         with index {@code i}
     */
    public void insert(String i, Key key){
        validateIndex(i);
        if(contains(i)) throw new IllegalArgumentException("Index is already in the priority queue");
        n++;
        qp.put(i, n);
        pq[n] = i;
        keys.put(i, key);
        swim(n);
    }

    /**
     * Returns an index associated with a minimum key.
     *
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public String minIndex(){
        if(n == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    /**
     * Returns a minimum key.
     *
     * @return a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key minKey(){
        if(n == 0) throw new NoSuchElementException("Priority queue underflow");
        return keys.get(pq[1]);
    }

    /**
     * Removes a minimum key and returns its associated index.
     *
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public String delMin(){
        if(n == 0) throw new NoSuchElementException("Priority queue underflow");
        String min = pq[1];
        exch(1, n--);
        sink(1);
        assert min.equals(pq[n + 1]);
        qp.remove(min);
        keys.remove(min);
        pq[n + 1] = null;
        return min;
    }

    /**
     * Returns the key associated with index {@code i}.
     *
     * @param  i the index of the key to return
     * @return the key associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public Key keyOf(String i){
        validateIndex(i);
        if(!contains(i)) throw new NoSuchElementException("Index is not in the priority queue");
        else return keys.get(i);
    }

    /**
     * Change the key associated with index {@code i} to the specified value.
     *
     * @param  i the index of the key to change
     * @param  key change the key associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void changeKey(String i, Key key){
        validateIndex(i);
        if(!contains(i)) throw new NoSuchElementException("Index is not in the priority queue");
        keys.put(i, key);
        swim(qp.get(i));
        sink(qp.get(i));
    }

    /**
     * Decrease the key associated with index {@code i} to the specified value.
     *
     * @param  i the index of the key to decrease
     * @param  key decrease the key associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     * @throws IllegalArgumentException if {@code key >= keyOf(i)}
     * @throws IllegalArgumentException if {@code key == null}
     */
    public void decreaseKey(String i, Key key){
        validateIndex(i);
        if(!contains(i)) throw new NoSuchElementException("Index is not in the priority queue");
        if (keys.get(i).compareTo(key) == 0)
            throw new IllegalArgumentException("Calling decreaseKey() with a key equal to the key in the priority queue");
        if (keys.get(i).compareTo(key) < 0)
            throw new IllegalArgumentException("Calling decreaseKey() with a key strictly greater than the key in the priority queue");
        keys.put(i, key);
        swim(qp.get(i));
    }

    /**
     * Increase the key associated with index {@code i} to the specified value.
     *
     * @param  i the index of the key to increase
     * @param  key increase the key associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     * @throws IllegalArgumentException if {@code key <= keyOf(i)}
     * @throws IllegalArgumentException if {@code key == null}
     */
    public void increaseKey(String i, Key key){
        validateIndex(i);
        if(!contains(i)) throw new NoSuchElementException("Index is not in the priority queue");
        if (keys.get(i).compareTo(key) == 0)
            throw new IllegalArgumentException("Calling increaseKey() with a key equal to the key in the priority queue");
        if (keys.get(i).compareTo(key) > 0)
            throw new IllegalArgumentException("Calling increaseKey() with a key strictly less than the key in the priority queue");
        keys.put(i, key);
        sink(qp.get(i));
    }

    /**
     * Remove the key associated with index {@code i}.
     *
     * @param  i the index of the key to remove
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void delete(String i){
        validateIndex(i);
        if(!contains(i)) throw new NoSuchElementException("Index is not in the priority queue");
        int index = qp.get(i);
        exch(index, n--);
        swim(index);
        sink(index);
        keys.remove(i);
        qp.remove(i);
    }

    /**
     * @param i index of key
     * @throws IllegalArgumentException if i is null or empty
     * */
    private void validateIndex(String i){
        if(i == null || i.length() == 0) throw new IllegalArgumentException("index is null or empty");
    }

    /**
     * returns true if the key at index i is greater than the key at index j
     * @param i index of key
     * @param j index of key
     * @return true if the key at index i is greater than the key at index j, false if otherwise
     *
     * */
    private boolean greater(int i, int j){
        return keys.get(pq[i]).compareTo(keys.get(pq[j])) > 0;
    }

    /**
     * Exchanges the elements at the given indices in the priority queue.
     *
     * @param i the index of the first element
     * @param j the index of the second element
     */
    private void exch(int i, int j){
        String swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp.put(pq[i], i);
        qp.put(pq[j], j);
    }

    /**
     * Promotes the element at the given index up the binary heap if it is greater than its parent.
     *
     * @param k the index of the element to be promoted
     */
    private void swim(int k){
        while(k > 1 && greater(k / 2, k)){
            exch(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * Demotes the element at the given index down the binary heap if it is less than its children.
     *
     * @param k the index of the element to be demoted
     */
    private void sink(int k){
        while(2 * k <= n){
            int j = 2 * k;
            if(j < n && greater(j, j + 1)) j++;
            if(!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }
}