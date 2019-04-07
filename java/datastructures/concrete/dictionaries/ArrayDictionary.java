package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import datastructures.concrete.KVPair;
import misc.exceptions.NoSuchKeyException;
import java.util.NoSuchElementException;


import java.util.Iterator;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V>  {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;
    private int size;

    // You may add extra fields or helper methods though!

    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(16);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    private boolean sameKey(K key1, K key2) {
        return key1 == key2 || key1 != null && key1.equals(key2);
    }

    private int indexOfKey(K key) {
        for (int i = 0; i < size; i++) {
            if (sameKey(pairs[i].key, key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public V get(K key) {
        for (int i = 0; i < this.size; i++) {
            if (sameKey(this.pairs[i].key, key)) {
                return this.pairs[i].value;
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public void put(K key, V value) {
        if (this.size < this.pairs.length) {
            if (containsKey(key)) {
                for (int i = 0; i < this.size; i++) {
                    if (sameKey(this.pairs[i].key, key)) {
                        this.pairs[i].value = value;
                    }
                }
            } else {
                this.pairs[this.size] = new Pair<>(key, value);
                this.size++;
            }
        } else {
            Pair<K, V>[] myPairs = makeArrayOfPairs(this.size * 2);
            for (int i = 0; i < this.size; i++) {
                myPairs[i] = this.pairs[i];
            }
            myPairs[size] = new Pair<>(key, value);
            this.pairs = myPairs;
            this.size++;
        }
    }

    @Override
    public V remove(K key) {
        int myIndex = indexOfKey(key);
        if (myIndex > -1) {
            V myValue = this.pairs[myIndex].value;
            for (int i = myIndex; i < this.size - 1; i++) {
                this.pairs[i] = this.pairs[i + 1];
            }
            this.size--;
            return myValue;
        } else {
            throw new NoSuchKeyException();
        }
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < this.size; i++) {
            if (sameKey(this.pairs[i].key, key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new ArrayDictionary.ArrayDictionaryIterator<>(this.pairs, this.size);
    }


    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        // Add any fields you need to store state information
        int curentIndex;
        Pair<K, V> current;
        Pair<K, V>[] pairs;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs, int size) {
            this.pairs = pairs;
            this.curentIndex = 0;
            if (size > 0) {
                this.current = this.pairs[0];
            } else {
                this.current = null;
            }
        }

        public boolean hasNext() {
            return this.current != null;
        }

        public KVPair<K, V> next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            KVPair<K, V> output = new KVPair<>(this.current.key, this.current.value);
            this.curentIndex += 1;
            if (curentIndex < this.pairs.length) {
                this.current = this.pairs[curentIndex];
            } else {
                this.current = null;
            }
            return output;
        }
    }
}
