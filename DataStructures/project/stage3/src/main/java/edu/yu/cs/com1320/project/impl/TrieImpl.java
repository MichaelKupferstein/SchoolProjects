package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

    private class Node<Value>{
        Set<Value> values;
        private Node[] links = new Node[TrieImpl.alphabetSize];
        Node parent;
        private Node(){
            this.values = new HashSet<>();
            for(int i = 0; i < TrieImpl.alphabetSize; i++){
                links[i] = null;
            }
            this.parent = null;
        }
    }
    private static final int alphabetSize = 62; //a-z A-Z 0-9
    private Node<Value> root;
    public TrieImpl() {
        this.root = new Node<>();
    }

    /**
     * add the given value at the given key
     *
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if(val == null){
            return;
        }else{
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node put(Node x, String key, Value val, int d) {
        //create a new node
        if (x == null) {
            x = new Node();
        }
        //we've reached the last node in the key,
        //add the value for the key and return the node
        if (d == key.length()) {
            x.values.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        int temp = this.getArrayLoc(c);
        Node par = x;
        x.links[temp] = this.put(x.links[temp], key, val, d + 1);
        x.links[temp].parent = par;
        return x;
    }

    private int getArrayLoc(char c){
        if(65<= c && c <= 90){
            //upper case A-Z
            //math to put it in array
            return c-65;
        }
        if(97<= c && c<= 122){
            //lowerase a-z
            return c-71;
        }
        if(48<= c && c<= 57){
            //numbers 0-9
            return c+4;
        }
        //if its not a valid character, (not a-z, A-Z,0-9)
        throw new IllegalArgumentException("Character " + c + " is not a valid character (a-zA-Z1-9)");
    }

    private Node get(Node x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[getArrayLoc(c)], key, d + 1);
    }
    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     *
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values, in descending order
     *
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        Node x = this.get(this.root,key, 0);
        if(x == null){
            return Collections.emptyList();
        }
        List<Value> result = new ArrayList<>(x.values);
        result.sort(comparator);
        return result;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        Node x = this.get(this.root,prefix,0);
        if(x == null){
            return Collections.emptyList();
        }
        List<Value> result = new ArrayList<>(getAllLinksValues(x));
        result.sort(comparator);
        return result;
    }

    private List<Value> getAllLinksValues(Node x){
        List<Value> all = new ArrayList<>();
        all.addAll(x.values);
        for(Node links : x.links){
            if(links != null){
                all.addAll(getAllLinksValues(links));
            }
        }
        return all;
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        Node x = this.get(this.root, prefix, 0);
        if(x == null){
            return Collections.emptySet();
        }
        Set<Value> set = new HashSet<>(getAllLinksValues(x));
        x.values.clear();
        for(int i = 0; i < alphabetSize; i++){
            x.links[i] = null;
        }
        checkIfEmpty(x);
        return set;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     *
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key) {
        Node x = this.get(this.root, key, 0);
        if(x == null){
            return Collections.emptySet();
        }
        Set<Value> set = new HashSet<>(x.values);
        x.values.clear();
        return set;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     *
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val) {
        Node x = this.get(this.root, key, 0);
        if(x.values.remove(val)){
            checkIfEmpty(x);
            return val;
        }
        return null;

    }

    private void checkIfEmpty(Node x){
        if(x == null){
            return;
        }
        Node z = x.parent;
        if(z == null){
            return;
        }
        if(isEmpty(x)){
            List<Node> temp = Arrays.asList(z.links);
            int index = temp.indexOf(x);
            z.links[index] = null;
        }
        checkIfEmpty(z);
    }

    private boolean isEmpty(Node x){
        boolean emptyValues = x.values.isEmpty();
        boolean emptyLinks = true;
        for(Node z : x.links){
            if(z != null){
                emptyLinks = false;
            }
        }
        if(emptyValues && emptyLinks){
            return true;
        }
        return false;
    }
}
