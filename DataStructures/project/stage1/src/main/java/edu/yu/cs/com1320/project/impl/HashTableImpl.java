package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value> implements HashTable<Key,Value> {
    private myLinkedList<Key,Value>[] table;

    public HashTableImpl() {
        this.table = new myLinkedList[5];
        for(int i = 0; i < this.table.length; i++){
            this.table[i] = new myLinkedList<Key,Value>();
        }

    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }

    @Override
    public Value get(Key k) {
        int index = hashFunction(k);
        return this.table[index].get(k);
    }
    @Override
    public Value put(Key k, Value v){
        int index = hashFunction(k);
        //if v == null then its a deletion
        if(v == null){
            Value old = this.table[index].get(k);
            this.table[index].remove(k);
            return old;
        }
        if(this.table[index].get(k) == null){
            this.table[index].add(k,v);
            return null;
        }else{
            Value old = this.table[index].get(k);
            this.table[index].remove(k);
            this.table[index].add(k,v);
            return old;
        }

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
            }else{
                Node<Key,Value> temp = head;
                while(temp.next != null && !(temp.next.k.equals(k))){
                    temp = temp.next;
                }
                if(temp.next != null){
                    temp.next = temp.next.next;
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
        private class Node<Key,Value>{
            private Key k;
            private Value v;
            private Node<Key,Value> next;

            private Node(Key k1, Value v1){
                this.k = k1;
                this.v = v1;
                this.next = null;
            }
        }
    }

}
