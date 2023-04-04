package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value> implements HashTable<Key,Value> {
    private int itemCount;
    private myLinkedList<Key,Value>[] table;

    public HashTableImpl() {
        this.table = new myLinkedList[5];
        this.itemCount = 0;
        for(int i = 0; i < this.table.length; i++){
            this.table[i] = new myLinkedList<Key,Value>();
        }
    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }
    private int hashFunction(Key key,int i){return (key.hashCode() & 0x7fffffff) % i;}

    private void arrayDouble(){
        Node[] tempArr = new Node[this.itemCount];
        int count = 0;
        for(int i = 0; i < this.table.length; i++){
            Node tempNode = this.table[i].head;
            if(tempNode != null){
                tempArr[count] = tempNode;
                count++;
                while(tempNode.next != null){
                    tempNode = tempNode.next;
                    tempArr[count] = tempNode;
                    count++;
                }
            }
        }
        myLinkedList<Key,Value>[] tempTable = new myLinkedList[this.table.length*2];
        for(int i = 0; i < tempTable.length; i++){
            tempTable[i] = new myLinkedList<Key,Value>();
        }
        for(int i = 0; i < tempArr.length; i++){
            if(tempArr[i] != null) {
                //had to create a second hashFunction bc this.table.length wasnt updating until the end
                int index = hashFunction((Key) tempArr[i].getKey(),this.table.length*2);
                tempTable[index].add((Key) tempArr[i].getKey(), (Value) tempArr[i].getValue());
            }
        }
        this.table = tempTable;
    }
    @Override
    public Value get(Key k) {
        if(k == null) return null;
        int index = hashFunction(k);
        return this.table[index].get(k);
    }
    @Override
    public Value put(Key k, Value v){
        //First check if the array needs to be doubled
        if(this.itemCount/this.table.length > 0.25){
            arrayDouble();
        }
        int index = hashFunction(k);
        //if v == null then its a deletion
        if(v == null){
            Value old = this.table[index].get(k);
            this.table[index].remove(k);
            this.itemCount--;
            return old;
        }
        //if this entry didnt exist return null
        if(this.table[index].get(k) == null){
            this.table[index].add(k,v);
            this.itemCount++;
            return null;
        }else{
            Value old = this.table[index].get(k);
            this.table[index].remove(k);
            this.table[index].add(k,v);
            //if this entry did exist and its a replacment return the old value
            return old;
        }
    }

    @Override
    public boolean containsKey(Key key){
        if(key == null){
            throw new NullPointerException();
        }
        int index = hashFunction(key);
        if(this.table[index].get(key) == null) return false;
        return true;
    }

    private class myLinkedList<Key,Value>{
        private int size = 0;
        private Node<Key,Value> head;
        private myLinkedList(){
            this.head = null;
        }
        private void add(Key k, Value v){
            if(head == null){
                head = new Node<>(k,v);
                size++;
            }else{
                Node<Key,Value> temp = head;
                while(temp.next != null){
                    temp = temp.next;
                }
                temp.next = new Node<Key,Value>(k,v);
                size++;
            }
        }
        private void remove(Key k){
            if(head.k.equals(k)){
                head = head.next;
                size--;
            }else{
                Node<Key,Value> temp = head;
                while(temp.next != null && !(temp.next.k.equals(k))){
                    temp = temp.next;
                }
                if(temp.next != null){
                    temp.next = temp.next.next;
                    size--;
                }
            }
        }
        private Value get(Key k){
            Node<Key,Value> temp = head;
            if(temp == null){
                return null;
            }
            if(temp.k.equals(k)){
                return temp.v;
            }
            while(temp.next != null){
                if(temp.k.equals(k)){
                    return temp.v;
                }
                temp = temp.next;
            }
            if(temp.k != null){
                if(temp.k.equals(k)){
                    return temp.v;
                }
            }
            //return null if it doesnt exist, becuase it went through each element and none equaled k so it ended the while loop bc its null
            return null;
        }

        private int getSize(){
            return this.size;
        }

    }
    private class Node<Key,Value>{
        private Key k;
        private Value v;
        private Node<Key,Value> next;

        private Node(Key k1, Value v1){
            this.k = k1;
            this.v = v1;
            this.next = null;
        }
        private Key getKey(){
            return this.k;
        }
        private Value getValue(){
            return this.v;
        }
    }
}
