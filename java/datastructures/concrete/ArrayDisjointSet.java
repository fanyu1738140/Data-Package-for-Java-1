package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private int cid;
    private IDictionary<T, Integer> dic;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        cid = 0;
        pointers = new int[17];
        dic = new ChainedHashDictionary<>();
    }

    @Override
    public void makeSet(T item) {
        if (dic.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        this.pointers[cid]  = -1;
        this.dic.put(item, cid);
        cid++;
        resize();
    }

    private void resize() {
        if (cid == pointers.length) {
            int[] newP = new int[pointers.length * 2];
            for (int i = 0; i < pointers.length; i++) {
                newP[i] = pointers[i];
            }
            this.pointers = newP;
        }
    }

    @Override
    public int findSet(T item) {
        if (!dic.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        return compressionFind(dic.get(item));
    }

    private int compressionFind(int index) {
        int next = this.pointers[index];
        if (next < 0) {
            return index;
        } else {
            int root = compressionFind(next);
            this.pointers[index] = root;
            return root;
        }
    }

    @Override
    public void union(T item1, T item2) {
        int index1 = findSet(item1);
        int index2 = findSet(item2);
        if (index1 == index2) {
            throw new IllegalArgumentException();
        }
        int rank1 = 0 - (1 + this.pointers[index1]);
        int rank2 = 0 - (1 + this.pointers[index2]);
        if (rank1 > rank2) {
            this.pointers[index2] = index1;
        } else if (rank2 > rank1) {
            this.pointers[index1] = index2;
        } else {
            this.pointers[index2] = index1;
            this.pointers[index1]--;
        }
    }
}
