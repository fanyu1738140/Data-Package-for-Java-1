package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;


/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;

    // Feel free to add more fields and constants.
    private int size;

    public ArrayHeap() {
        this.heap = makeArrayOfT(21);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    @Override
    public T removeMin() {
        if (this.size == 0) {
            throw new EmptyContainerException();
        } else {
            T item = this.peekMin();
            this.heap[0] = this.heap[this.size - 1];
            this.size--;
            this.percolateDown(0);
            return item;
        }
    }

    private void percolateDown(int index) {
        int child = minChild(index);
        if (child != index) {
            T temp = this.heap[index];
            this.heap[index] = this.heap[child];
            this.heap[child] = temp;
            this.percolateDown(child);
        }
    }

    private int minChild(int index) {
        int minIndex = index;
        T child = this.heap[index];
        for (int i = 1; i <= NUM_CHILDREN; i++) {
            int k = NUM_CHILDREN * index + i;
            if (k < this.size) {
                if (this.heap[k] != null && child.compareTo(this.heap[k]) > 0) {
                    child = this.heap[k];
                    minIndex = k;
                }
            } else {
                break;
            }
        }
        return minIndex;
    }

    @Override
    public T peekMin() {
        if (this.size == 0) {
            throw new EmptyContainerException();
        } else {
            return this.heap[0];
        }
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (this.size == this.heap.length){
            this.resize();
        }
        this.heap[this.size] = item;
        this.size++;
        this.percolateUp(this.size - 1);
    }

    private void resize() {
        T[] newHeap = makeArrayOfT(2 * this.heap.length);
        T[] temp = this.heap;
        this.heap = newHeap;
        this.size = 0;
        for (T item :  temp) {
            this.insert(item);
        }
    }

    private void percolateUp(int index) {
        int parent;
        if (index % NUM_CHILDREN == 0) {
            parent = index / NUM_CHILDREN - 1;
        } else  {
            parent = index / NUM_CHILDREN;
        }
        if (parent >= 0) {
            if (this.heap[parent].compareTo(this.heap[index]) > 0) {
                T temp = this.heap[index];
                this.heap[index] = this.heap[parent];
                this.heap[parent] = temp;
                this.percolateUp(parent);
            }
        }
    }

    @Override
    public int size() {
        return this.size;
    }
}
