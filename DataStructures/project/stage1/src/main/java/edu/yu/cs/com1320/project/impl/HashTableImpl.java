package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;
public class HashTableImpl<Key,Value> implements HashTable<Key,Value>{
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
    private Entry<?,?>[][] table;
    public HashTableImpl() {
        this.table = new Entry[5][5];
    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }

    @Override
    public Value get(Key k) {
       int index = this.hashFunction(k);
       Entry current = this.table[index][0];
       for(int i = 0; i < this.table[index].length; i++){
           if(this.table[index][i].key.equals(k)){
               current = this.table[index][i];
               break;
           }
       }
       if(current != null){
           return (Value)current.value;
       }
       return null;

    }
    private Entry getEntry(Key k){
        int index = this.hashFunction(k);
        Entry current = this.table[index][0];
        for(int i = 0; i < this.table[index].length; i++){
            if(this.table[index][i].key.equals(k)){
                current = this.table[index][i];
                break;
            }
            current = null;
        }
        return current;
    }
    @Override
    public Value put(Key k, Value v) {
        int index = this.hashFunction(k);
        if(getEntry(k) != null){
            for(int i = 0; i < this.table[index].length;i++){
                if(this.table[index][i].key.equals(k)){
                    Entry old = this.table[index][i];
                    this.table[index][i] = new Entry<Key,Value>(k,v);
                    if(old != null){
                        checkIfsized(index);
                        return (Value)old.value;
                    }
                    checkIfsized(index);
                    return null;
                }
            }
        }
        for(int i = 0; i < this.table[index].length; i++){
            if(this.table[index][i] == null){
                this.table[index][i] = new Entry<Key,Value>(k,v);
                checkIfsized(index);
                return null;
            }
        }
        checkIfsized(index);
        return null;
    }

    private void checkIfsized(int index) {
        boolean space = false;
        for(int i = 0; i < this.table[index].length; i++){
           if(this.table[index][i] == null){
             space = true;
             break;
           }
       }
       if(!space){
           arrayDouble(index);
       }
    }

    private void arrayDouble(int index){
        Entry<?,?>[] oldRow = this.table[index];
        Entry<?,?>[] newRow = new Entry[oldRow.length * 2];
        for(int i = 0; i < oldRow.length; i++){
            newRow[i] = oldRow[i];
        }
        this.table[index] = newRow;
    }
}
