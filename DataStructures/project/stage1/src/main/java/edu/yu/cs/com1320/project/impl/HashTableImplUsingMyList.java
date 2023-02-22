package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;

public class HashTableImplUsingMyList<Key, Value> implements HashTable<Key,Value> {

    public class Entry<Key, Value>{
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
    public class myLinkedList<type> {
        private class Node<E>{
            E item;
            Node<type> next;
            Node(E d){
                this.item = d;
                this.next = null;
            }
        }
        private int size = 0;
        private Node<type> head;
        private Node<type> tail;
        public myLinkedList(){
            this.head = null;
        }
        public void add(type t){
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
        public Node<type> find(Node<type> t){
            Node<type> temp = head;
            while(temp.next != null && temp.next != t){
                temp = temp.next;
            }
            return temp;
        }
        public int getSize(){
            return this.size;
        }
    }

    private myLinkedList<Entry<Key,Value>>[] table;

    public HashTableImplUsingMyList() {
        this.table = new myLinkedList[5];
    }
    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }

    @Override
    public Value get(Key k) {
        int index = this.hashFunction(k);
        Entry<Key,Value> temp = getEntry(k);
        return null;
    }

    private Entry getEntry(Key k){
        int index = this.hashFunction(k);
        //this.table[index].
        return null;
    }

    @Override
    public Value put(Key k, Value v) {
        return null;
    }
}
