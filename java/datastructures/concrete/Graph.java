package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.ISet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.IDisjointSet;
import misc.Sorter;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.
    private IDictionary<V, IList<E>> map;
    private IList<E> edgesList;
    private IList<V> verticesList;

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        if (vertices == null | edges == null) {
            throw new IllegalArgumentException();
        }
        this.map = new ChainedHashDictionary<>();
        this.edgesList = new DoubleLinkedList<>();
        this.verticesList = new DoubleLinkedList<>();
        for (V vertice : vertices) {
            if (vertice == null) {
                throw new IllegalArgumentException();
            }
            this.map.put(vertice, new DoubleLinkedList<>());
            this.verticesList.add(vertice);
        }
        for (E edge : edges) {
            if (edge == null | edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            V vertice1 = edge.getVertex1();
            V vertice2 = edge.getVertex2();
            if (!this.verticesList.contains(vertice1) | !this.verticesList.contains(vertice2)) {
                throw new IllegalArgumentException();
            }
            this.map.get(vertice1).add(edge);
            this.map.get(vertice2).add(edge);
            this.edgesList.add(edge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.verticesList.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edgesList.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        IDisjointSet<V> disjointSet = new ArrayDisjointSet<>();
        ISet<E> output = new ChainedHashSet<>();
        for (V vertice : this.verticesList) {
            disjointSet.makeSet(vertice);
        }
        IList<E> sortedEdge = Sorter.topKSort(this.numEdges(), this.edgesList);
        for (E edge : sortedEdge) {
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            if (disjointSet.findSet(v1) != disjointSet.findSet(v2)) {
                disjointSet.union(v1, v2);
                output.add(edge);
            }
        }
        return output;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        IList<E> path = new DoubleLinkedList<>();
        if ((!this.verticesList.contains(start)) | (!this.verticesList.contains(end))) {
            throw new IllegalArgumentException();
        }
        if (start.equals(end)) {
            return path;
        }

        IPriorityQueue<Vertice<V>> vertices = new ArrayHeap<>();
        ISet<V> visited = new ChainedHashSet<>();
        IDictionary<V, IList<E>> pathToPoint = new ChainedHashDictionary<>();
        IDictionary<V, Double> costs = new ChainedHashDictionary<>();

        for (V vertice : this.verticesList) {
            if (vertice.equals(start)) {
                costs.put(vertice, 0.0);
            } else {
                costs.put(vertice, Double.POSITIVE_INFINITY);
            }
        }
        vertices.insert(new Vertice<>(start, 0));
        pathToPoint.put(start, new DoubleLinkedList<>());

        while (!vertices.isEmpty()) {
            Vertice<V> vNow = vertices.removeMin();
            V pointNow = vNow.getPoint();

            if (!visited.contains(pointNow)) {
                visited.add(pointNow);
                double costNow = vNow.getCost();
                IList<E> pathNow = pathToPoint.get(pointNow);
                IList<E> edges = this.map.get(vNow.getPoint());

                for (E edge : edges) {
                    V dest = edge.getOtherVertex(pointNow);
                    if (!visited.contains(dest)) {
                        double destCost = costNow + edge.getWeight();
                        IList<E> destPath = new DoubleLinkedList<>();
                        for (E p : pathNow) {
                            destPath.add(p);
                        }
                        destPath.add(edge);
                        if (costs.containsKey(dest)) {
                            double destOldCost = costs.get(dest);
                            if (destOldCost > destCost) {
                                costs.put(dest, destCost);
                                pathToPoint.put(dest, destPath);
                                vertices.insert(new Vertice<>(dest, destCost));
                            }
                        } else {
                            costs.put(dest, destCost);
                            pathToPoint.put(dest, destPath);
                            vertices.insert(new Vertice<>(dest, destCost));
                        }
                    }
                }
            }
        }

        if (!pathToPoint.containsKey(end)) {
            throw new NoPathExistsException();
        }
        return pathToPoint.get(end);
    }

    private class Vertice<VV> implements Comparable<Vertice<VV>> {
        private double cost;
        private VV point;

        public Vertice(VV point, double cost) {
            this.cost = cost;
            this.point = point;
        }

        public double getCost() {
            return this.cost;
        }

        public VV getPoint() {
            return this.point;
        }

        public int compareTo(Vertice<VV> other) {
            double ans = this.getCost() - other.getCost();
            if (ans < 0) {
                return -1;
            } else if (ans > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
