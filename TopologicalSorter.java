import java.util.Arrays;
import java.util.Stack;

/**
 * This must be a non-instantiable class.
 * 
 * @author Sumneet Brar
 */
abstract class TopologicalSorter {

  /**
   * Returns true if there is at least one cycle in the digraph.
   * 
   * @param digraph
   * @return
   */
  public static boolean isDirectedCycle(DirectedGraph digraph) {
    if (digraph == null)
      throw new IllegalArgumentException("Digraph cannot be null");

    boolean[] marked = new boolean[digraph.size()];
    boolean[] onStack = new boolean[digraph.size()];
    String[] vertices = digraph.getVertices();
    
    // Iterate over the digraph. For any unmarked vertex, check if it has a cycle.
    for (int v = 0; v < digraph.size(); v++) {
      if (!marked[v] && hasCycle(digraph, v, marked, onStack, vertices)) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasCycle(DirectedGraph digraph, int v, boolean[] marked, boolean[] onStack, String[] vertices) {
    marked[v] = true;
    onStack[v] = true;

    // Iterate over the given vertex's adjacency list.
    for (String w : digraph.getAdjacencyList(vertices[v])) {
      int indexOfW = digraph.vertexIndices.get(w);
      // If vertex W has not been explore, recurse on it.
      if (!marked[indexOfW]) {
        if (hasCycle(digraph, indexOfW, marked, onStack, vertices)) {
          return true;
        }
      }
      // If the vertex has been marked, but is still on the stack, there is a cycle.
      else if (onStack[indexOfW]) {
        return true;
      }
    }

    onStack[v] = false; // remove it from the cycle
    return false;
  }

  /**
   * Returns a sorted array of all the vertices,
   * such that each vertex is listed before all others to which it has an edge.
   * 
   * @param digraph
   * @return
   */
  public static String[] sortTopologically(DirectedGraph digraph) {
    if (digraph == null)
      throw new IllegalArgumentException("Digraph cannot be null");

    boolean[] marked = new boolean[digraph.size()];
    Stack<String> stack = new Stack<String>();
    String[] vertices = digraph.getVertices();

    // For each vertex that has not been visited, perform a DFS
    for (int v = 0; v < digraph.size(); v++) {
      if (!marked[v]) {
        topologicalSortDFS(digraph, v, marked, stack, vertices);
      }
    }

    // Create a string array from the stack with the correct order of vertices
    String[] sorted = new String[digraph.size()];
    for (int i = 0; i < digraph.size(); i++) {
      sorted[i] = stack.pop();
    }

    return sorted;
  }

  private static void topologicalSortDFS(DirectedGraph digraph, int v, boolean[] marked, Stack<String> stack, String[] vertices) {
    marked[v] = true; // Set vertex V to marked

    // Perform a DFS on each vertex in the adjacency list of vertex V
    for (String w : digraph.getAdjacencyList(vertices[v])) {
      int indexOfW = digraph.vertexIndices.get(w);
      // If vertex w has not been explored, recurse on it.
      if (!marked[indexOfW]) {
        topologicalSortDFS(digraph, indexOfW, marked, stack, vertices);
      }
    }

    // Once all neighbors are explored, add vertex V to the stack
    stack.push(vertices[v]);
  }

  /**
   * Returns a 2D sorted array of all the vertices.
   * Every element of the array is an array of vertices in one strongly-connected
   * component.
   * The ordering within each component is arbitrary,
   * but the components must be in a valid topological ordering for the kernel
   * DAG.
   * 
   * @param digraph
   * @return
   */
  public static String[][] findStrongComponents(DirectedGraph digraph) {
    if (digraph == null)
      throw new IllegalArgumentException("Digraph cannot be null");

    // Reverse the digraph
    DirectedGraph reverse = digraph.makeReverseGraph();

    boolean[] marked = new boolean[digraph.size()];
    Stack<String> stack = new Stack<String>();
    String[] vertices = reverse.getVertices();

    int size = digraph.size();

    // Find the pseudo-topological order
    for (int v = 0; v < reverse.size(); v++) {
      if (!marked[v]) {
        pseudoSortDFS(digraph, v, marked, stack, vertices);
      }
    }

    // Create a string array from the stack with the correct order of vertices
    String[] sorted = new String[size];
    for (int v = 0; v < size; v++) {
      sorted[v] = stack.pop();
    }

    // Perform a DFT on the orginal graph, starting at the next unexplored vertex in list 'sorted'
    String[][] strongComponents = new String[size][];
    marked = new boolean[size];
    int componentIndex = 0;
    for (int v = 0; v < sorted.length; v++) {
      if (!marked[digraph.vertexIndices.get(sorted[v])]) {
        pseudoSortDFS(digraph, digraph.vertexIndices.get(sorted[v]), marked, stack, sorted);

        // After returning from the DFS, all marked vertices are in one strong component
        strongComponents[componentIndex] = new String[stack.size()];
        for (int i = stack.size() - 1; i >= 0; i--) {
          strongComponents[componentIndex][i] = stack.pop();
        }
        componentIndex++;
      }
    }
    // Creat a new array with the filled size
    String[][] sc = new String[componentIndex][];
    // Copy the values from strongComponents to sc
    for (int i = 0; i < componentIndex; i++) {
      sc[i] = Arrays.copyOf(strongComponents[i], strongComponents[i].length);
    }
    return sc;
  }

  private static void pseudoSortDFS(DirectedGraph digraph, int v, boolean[] marked, Stack<String> stack, String[] vertices) {
    marked[v] = true; // Set vertex V to marked

    // Perform a DFS on each vertex in the adjacency list of vertex V
    for (String w : digraph.getAdjacencyList(vertices[v])) {
      int indexOfW = digraph.vertexIndices.get(w);
      if (!marked[indexOfW]) {
        pseudoSortDFS(digraph, indexOfW, marked, stack, vertices);
      }
    }

    // Once all neighbors are explored, add vertex V to the stack
    stack.push(vertices[v]);
  }
}
  


