package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;

public class HashTableImplUsingMyList<Key, Value> implements HashTable<Key,Value> {
    private myLinkedList<Key,Value>[] table;

    public HashTableImplUsingMyList() {
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
    public Value put(Key k, Value v) {
        int index = hashFunction(k);
        this.table[index].add(k,v);
        return v;
    }
    private class myLinkedList<Key,Value>{
        private int size = 0;
        Node<Key,Value> head;
        //Node<Key,Value> tail;
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
                while(temp.next != null && !(temp.k.equals(k))){
                    temp = temp.next;
                }
                if(temp.next != null){
                    temp.next = temp.next.next;
                }
            }
        }
        private Value get(Key k){
            Node<Key,Value> temp = head;
            while(temp.next != null){
                if(temp.k.equals(k)){
                    return temp.v;
                }
                temp = temp.next;
            }
            //return null if it doesnt exist, becuase it went through each element and none equaled k so it ended the while loop bc its null
            return null;
        }
        private int getSize(){
            return this.size;
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
