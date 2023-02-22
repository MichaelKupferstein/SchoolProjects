package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;

import java.util.LinkedList;

public class HashTableImplUsingMyList<Key, Value> implements HashTable<Key,Value> {
    private myLinkedList<Entry<Key,Value>>[] table;

    public HashTableImplUsingMyList() {
        this.table = new myLinkedList[5];
        for(int i = 0; i < this.table.length; i++){
            this.table[i] = new myLinkedList<Entry<Key,Value>>();
        }

    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }

    @Override
    public Value get(Key k) {
        Entry<Key,Value> temp = getEntry(k);
        if(temp.value != null){
            return temp.value;
        }else{
            return null;
        }
    }
    @Override
    public Value put(Key k, Value v) {
        Entry<Key,Value> temp = new Entry<>(k,v);
        int index = this.hashFunction(k);
//        if(getEntry(k) == null){
//            this.table[index].add(temp);
//            return null;
//        }else{
//            Entry<Key,Value> old = this.table[index].get(temp);
//            this.table[index].remove(old);
//            this.table[index].add(temp);
//            return old.value;
//        }
        this.table[index].add(temp);
        return null;
    }
    private Entry getEntry(Key k){
        int index = this.hashFunction(k);
        if(this.table[index] == null){
            return null;
        }
        Entry<Key,Value> temp = this.table[index].head.item;
        int current = 1;
        while(current < this.table[index].getSize()){
            if(temp.key.equals(k)){
                return temp;
            }else{
                temp = this.table[index].next();
                current++;
            }
        }
        //returns null if it doesnt exist
        return null;
    }
    private class Entry<Key, Value>{
        Key key;
        Value value;
        Entry(Key k, Value v){
            if(k == null){
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
        }
    }
    private class myLinkedList<type> {
        private int size = 0;
        private Node<type> head;
        private Node<type> tail;

        private myLinkedList(){
            //this.head = null;
        }
        private Node<type> lastReturn = head;
        private Node<type> next = head;
        private type next(){
            lastReturn = next;
            next = next.next;
            return lastReturn.item;
        }
        private void add(type t){
            if(head == null){
                head = new Node<>(t);
                tail = head;
                size++;
            }else{
                int count = 0;
                Node<type> temp = head;
                while(temp.next != null){
                    temp = temp.next;
                    count++;
                }
                temp.next = new Node<>(t);
                size++;
                if(count == this.size - 1){
                    tail = temp.next;
                }
            }
        }

        public void remove(type t){
            //Might be t.item.equals(head.item) also ^might be remove(node t)
            if(t == head){
                head = head.next;
            }else{
                Node<type> temp = head;
                while(temp.next != null && temp.next != t){
                    temp = temp.next;
                }
                if(temp.next != null){
                    temp.next = temp.next.next;
                }
            }
        }
        public type get(type t){
            Node<type> temp = head;
            while(temp.next != null && temp.next != t){
                temp = temp.next;
            }
            if(temp != null){
                return (type)temp.item;
            }
            return null;
        }
        public int getSize(){
            return this.size;
        }
        private class Node<type>{
            type item;
            Node<type> next;
            Node(type d){
                this.item = d;
                this.next = null;
            }
        }
    }

}
