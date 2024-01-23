import java.util.HashMap;
import java.util.Iterator;

/**
 * Creates a digraph object
 * 
 * @author Sumneet Brar
 */
public class DirectedGraph {

  public int initialVertexCount; // number of vertices
  public int edgeCount; // number of edges
  private Bag<String>[] adj; // adjacency lists

  public HashMap<String, Integer> vertexIndices; // map to store the indicies

  /**
   * Creates an empty digraph.
   */
  public DirectedGraph() {
    this.initialVertexCount = 20; // arbitrary value
    this.edgeCount = 0; // no edges yet
    this.vertexIndices = new HashMap<String, Integer>();

    // create array of lists
    adj = (Bag<String>[]) new Bag[initialVertexCount];
    for (int i = 0; i < initialVertexCount; i++)
      adj[i] = new Bag<String>();
  }

  /**
   * Inserts a vertex into the digraph.
   * Throws an IllegalArgumentException if a vertex by that name already exists.
   * 
   * @param name the name of the vertex to be inserted
   */
  public void addVertex(String name) {
    if (name == null)
      throw new IllegalArgumentException("Vertex name cannot be null");
    if (vertexIndices.containsKey(name))
      throw new IllegalArgumentException("Vertex already exists");
    if (vertexIndices.size() == initialVertexCount)
      resizeArray(); // resize array if necessary

    // add the vertex to the map and the adjacency list
    vertexIndices.put(name, vertexIndices.size());
    adj[vertexIndices.get(name)] = new Bag<String>();
  }

  private void resizeArray() {
    int newSize = initialVertexCount * 2;
    Bag<String>[] newAdj = (Bag<String>[]) new Bag[newSize];
    System.arraycopy(adj, 0, newAdj, 0, adj.length);
    adj = newAdj;
  }

  /**
   * Adds a new one-way edge from the first vertex to the second.
   * It should throw an IllegalArgumentException if the vertices don’t exist.
   * 
   * @param vertex1
   * @param vertex2
   */
  public void addEdge(String vertex1, String vertex2) {
    if(vertex1 == null || vertex2 == null || vertex1.isEmpty() || vertex2.isEmpty())
      throw new IllegalArgumentException("Vertex name cannot be null or empty");
    if (vertex1.equals(vertex2))
      throw new IllegalArgumentException("Cannot add edge to itself");
    if(vertexIndices.get(vertex1) == null || vertexIndices.get(vertex2) == null)
      throw new IllegalArgumentException("One of the vertices (or both) does not exist");
      
    // get the index of the first vertex
    int indexOfVertex1 = vertexIndices.get(vertex1);

    // add vertex 2 to the adjacency list of vertex 1
    adj[indexOfVertex1].add(vertex2);
    edgeCount++; // increment the edge count
  }

  /**
   * Removes an edge from the first vertex to the second.
   * (It will not affect an edge in the opposite direction.)
   * Returns true if successful, or false if there was no edge.
   * Throws anIllegalArgumentException if the vertices don’t exist.
   * 
   * @param vertex1
   * @param vertex2
   * @return
   */
  public boolean deleteEdge(String vertex1, String vertex2) {
    if (vertex1 == null || vertex1.isEmpty() || vertex2 == null || vertex2.isEmpty())
      throw new IllegalArgumentException("Vertex name cannot be null or empty");
    if(vertexIndices.get(vertex1) == null || vertexIndices.get(vertex2) == null)
      throw new IllegalArgumentException("One of the vertices (or both) does not exist");

    int indexOfVertex1 = vertexIndices.get(vertex1);

    // Iterate over the adjacency list of vertex1, searching for vertex 2
    Iterator<String> iterator = adj[indexOfVertex1].iterator();
    while (iterator.hasNext()) {
      if (iterator.next().equals(vertex2)) {
        iterator.remove();
        edgeCount--;
        return true;
      }
    }

    return false;
  }

  /**
   * Return the number of vertices in the digraph
   * 
   * @return
   */
  public int size() {
    return vertexIndices.size();
  }

  /**
   * Returns true if the vertex exists, or false otherwise
   * 
   * @param vertex
   * @return
   */
  public boolean isValidVertex(String vertex) {
    if (vertex == null || vertex.isEmpty())
      throw new IllegalArgumentException("Vertex name cannot be null or empty");
    if (vertexIndices == null) return false;
    else return vertexIndices.containsKey(vertex);
  }

  /**
   * Gets a list of all other vertices that this vertex has an edge to.
   * It may return an array of length 0 if the vertex has no outward edges.
   * 
   * @param vertex
   * @return
   */
  public String[] getAdjacencyList(String vertex) {
    if (vertex == null || vertex.isEmpty())
      throw new IllegalArgumentException("Vertex name cannot be null or empty");
    if (!vertexIndices.containsKey(vertex))
      throw new IllegalArgumentException("Vertex does not exist");

    int indexOfVertex = vertexIndices.get(vertex);
    Bag<String> adjacencyList = adj[indexOfVertex];

    // Create a new array of the vertex's adjacency list
    String[] adjList = new String[vertexIndices.size()];
    int i = 0;
    for (String s : adjacencyList) {
      adjList[i] = s;
      i++;
    }

    // Copy the adjacent values to a new array of the proper size
    String[] trimmedAdjList = new String[i];
    System.arraycopy(adjList, 0, trimmedAdjList, 0, i);

    return trimmedAdjList;
  }

  /**
   * Returns an array of all the vertices.
   * 
   * @return
   */
  public String[] getVertices() {
    if (vertexIndices == null) 
      throw new IllegalStateException("No vertices exist");

    // Iterate over the keySet, adding values to the new array 'vertices'
    String[] vertices = new String[vertexIndices.size()];
    int i = 0;
    for (String vertex : vertexIndices.keySet()) {
      vertices[i] = vertex;
      i++;
    }
    return vertices;
  }

  /**
   * Returns a new Digraph with the same vertices,
   * but edges in the opposite directions.
   * 
   * @return
   */
  public DirectedGraph makeReverseGraph() {
    DirectedGraph reverseGraph = new DirectedGraph();

    // Add all vertices to the new graph
    for (String vertex : vertexIndices.keySet()) {
      reverseGraph.addVertex(vertex);
    }

    // Add reversed edges to the new graph
    for (String vertex : vertexIndices.keySet()) {
      int indexOfVertex = vertexIndices.get(vertex);
      for (String adjVertex : adj[indexOfVertex]) {
        reverseGraph.addEdge(adjVertex, vertex);
      }
    }

    return reverseGraph;
  }

  private DirectedGraph altMakeReverseGraph() {
    if (vertexIndices == null || adj == null) {
        throw new IllegalStateException("vertexIndices or adj is null");
    }

    DirectedGraph reverseGraph = new DirectedGraph();

    // Add vertices and reversed edges to the new graph
    for (String vertex : vertexIndices.keySet()) {
        reverseGraph.addVertex(vertex);

        int indexOfVertex = vertexIndices.get(vertex);
        for (String adjVertex : adj[indexOfVertex]) {
            reverseGraph.addEdge(adjVertex, vertex);
        }
    }

    return reverseGraph;
  }

  public class Bag<Item> implements Iterable<Item> {

    private Node first;

    private class Node {
      Item item;
      Node next;
    }

    public void add(Item item) {
      Node oldfirst = first;
      first = new Node();
      first.item = item;
      first.next = oldfirst;
    }

    @Override
    public Iterator<Item> iterator() {
      return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
      private Node current = first;

      @Override
      public boolean hasNext() {
        return current != null;
      }

      public void remove() {
      }

      public Item next() {
        Item item = current.item;
        current = current.next;
        return item;
      }
    }
  }
}
