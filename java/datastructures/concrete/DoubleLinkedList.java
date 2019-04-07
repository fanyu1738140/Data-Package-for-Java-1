package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        if (this.size == 0) {
            this.front = new Node<>(item);
            this.back = this.front;
        } else {
            this.back.next = new Node<>(this.back, item, null);
            this.back.next.prev = this.back;
            this.back = this.back.next;
        }
        this.size++;
    }

    @Override
    public T remove() {
        if (this.back == null) {
            throw new EmptyContainerException();
        }
        T removeValue = this.back.data;
        if (this.size == 1) {
            this.back = null;
            this.front = null;
        } else {
            this.back = this.back.prev;
            this.back.next = null;
        }
        size--;
        return removeValue;
    }

    private Node<T> getNode(int index) {
        Node<T> myNode = this.front;
        int count = 0;
        if (index <= (this.size / 2)) {
            while (count != index) {
                myNode = myNode.next;
                count++;
            }
        } else {
            count = this.size - 1;
            myNode = back;
            while (count != index) {
                myNode = myNode.prev;
                count--;
            }
        }
        return myNode;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } else {
            return getNode(index).data;
        }
    }

    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } else {
            if (this.size == 1) {
                this.front = new Node<>(null, item, null);
                this.back = this.front;
            } else if (index == 0) {
                this.front.next.prev = new Node<>(null, item, this.front.next);
                this.front = this.front.next.prev;
            } else if (index == size - 1) {
                this.back.prev.next = new Node<>(this.back.prev, item, null);
                this.back = this.back.prev.next;
            } else {
                Node<T> current = getNode(index);
                current.prev.next = new Node<>(current.prev, item, current.next);
                current.next.prev = current.prev.next;
            }
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= this.size + 1) {
            throw new IndexOutOfBoundsException();
        } else if (index == this.size || this.size == 0) {
            add(item);
        } else {
            if (index == 0) {
                this.front = new Node<>(null, item, this.front);
                this.front.next.prev = this.front;
            } else if (index == this.size - 1) {
                this.back.prev = new Node<>(this.back.prev, item, this.back);
                this.back.prev.prev.next = this.back.prev;
            } else {
                Node<T> myNode = getNode(index);
                myNode.prev = new Node<>(myNode.prev, item, myNode);
                myNode.prev.prev.next = myNode.prev;
            }
            this.size++;
        }
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } else {
            if (this.size == 1 || index == this.size - 1) {
                return remove();
            } else {
                T myValue = front.data;
                if (index == 0) {
                    this.front = this.front.next;
                    this.front.prev = null;
                } else {
                    Node<T> myNode = getNode(index);
                    myNode.prev.next = myNode.next;
                    myNode.next.prev = myNode.prev;
                    myValue = myNode.data;
                }
                this.size--;
                return myValue;
            }
        }
    }

    private boolean compareItem(T item1, T item2) {
        try {
            return item1 == item2 || item1 != null && item1.equals(item2);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int indexOf(T item) {
        Node<T> myNode = this.front;
        int index = 0;
        while (myNode != null) {
            if (compareItem(myNode.data, item)) {
                return index;
            }
            myNode = myNode.next;
            index++;
        }
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        int judger = indexOf(other);
        return judger >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return this.current != null;
        }


        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            T myValue = this.current.data;
            this.current = current.next;
            return myValue;
        }
    }
}
