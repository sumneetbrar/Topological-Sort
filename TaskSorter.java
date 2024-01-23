/** 
 * Loads up a .task file, that may or may not contain cycles. It sorts these
 * tasks. If there are no cycles, it will do a basic topological sort. However
 * if there is a cycle, it will first find the strong components, and then it
 * will sort those strong components topologically.
 *
 * It relies on both a Digraph object, and a TopologicalSorter non-instantiable
 * class. The APIs for these are given in the assignment.
 * 
 * Note that because you will be probably be using recursive methods, that this
 * program might overrun the stack very easily. You may have to increase the
 * stack size in your IDE's options.
 *
 * @author Adam A. Smith
 * @version 2023.11.12
 */

import java.io.*;
import java.util.*;

public class TaskSorter {
	public static void main(String[] args) {
		// make sure we have a file
		if (args.length == 0) {
			System.err.println("Please enter a file name!");
			System.exit(1);
		}

		// make the graph, and print out the ordering
		try {
			DirectedGraph digraph = makeDigraphFromTaskFile(args[0]);
			sortAndReport(args[0], digraph);
		}
		catch (IOException e) {
			System.err.println("Couldn't open file \"" +args[0]+ "\".");
			System.exit(1);
		}
	}

	// load a rask file & make a Digraph from its lines
	private static DirectedGraph makeDigraphFromTaskFile(String filename) throws IOException {
		DirectedGraph digraph = new DirectedGraph();

		// read the file
		Scanner scanner = new Scanner(new File(filename));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] tokens = line.split("\t");
			for (int i=0; i<tokens.length; i++) tokens[i] = tokens[i].trim();

			// make sure that all these vertices are added
			for (String token : tokens) {
				if (!digraph.isValidVertex(token)) digraph.addVertex(token);
			}

			// scan thru again & add edges
			for (int i=1; i<tokens.length; i++) {
				digraph.addEdge(tokens[i], tokens[0]);
			}
		}

		// close out & return
		scanner.close();
		return digraph;
	}

	// find if it has cycles, and do 2 different approaches based on that
	private static void sortAndReport(String fileName, DirectedGraph digraph) {
		// has cycles
		if (TopologicalSorter.isDirectedCycle(digraph)) {
			System.out.println("The file \"" +fileName+ "\" contains " +digraph.size()+ " tasks, some of which are mutually dependent. You must:");

			// sort & print the strong components
			String[][] strongComponents = TopologicalSorter.findStrongComponents(digraph);
			for (int i=0; i<strongComponents.length; i++) {
				System.out.print("  " + (i+1) + ". " + strongComponents[i][0]);
				for (int j=1; j<strongComponents[i].length; j++) {
					System.out.print(", " + strongComponents[i][j]);
				}
				System.out.println();
			}
		}

		// no cycles--just a plain topological sort
		else {
			System.out.println("The file \"" +fileName+ "\" contains " +digraph.size()+ " tasks, with no cycles. You must:");

			// get basic topo sort & print it
			String[] order = TopologicalSorter.sortTopologically(digraph);
			for (int i=0; i<order.length; i++) {
				System.out.println("  " + (i+1) + ". " +order[i]);
			}
		}
	}
}