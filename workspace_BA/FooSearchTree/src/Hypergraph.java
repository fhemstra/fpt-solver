import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Hypergraph {
	boolean showEverything = true;
	int[] nodes;
	// A hyperedge is a Array of nodes (int)
	ArrayList<Tuple> edges = new ArrayList<Tuple>();
	// HashMap of nodes to global hyperedges
	HashMap<Integer, ArrayList<Tuple>> node_to_edges = new HashMap<Integer, ArrayList<Tuple>>();

	// TODO Change Integer to int

	public Hypergraph(int[] nodes, ArrayList<Tuple> edges) {
		this.nodes = nodes;
		this.edges = edges;
		this.node_to_edges = computeHashmap();
	}

	// TODO kernelize()

	private HashMap<Integer, ArrayList<Tuple>> computeHashmap() {
		// Contruct HashMap which maps nodes to all edges they are contained in
		HashMap<Integer, ArrayList<Tuple>> node_to_edges = new HashMap<Integer, ArrayList<Tuple>>();
		// First, every node in the universe gets an empty list of edges
		for (int node : nodes) {
			node_to_edges.put(node, new ArrayList<Tuple>());
		}
		for (Tuple t : edges) {
			for (int i = 0; i < t.elements.length; i++) {
				int curr_node = t.elements[i];
				ArrayList<Tuple> curr_edges = node_to_edges.get(curr_node);
				// If a node has no edges, it is the null-node which fills up all edges to c_par
				// entries.
				if (curr_edges == null)
					continue;
				curr_edges.add(t);
				node_to_edges.put(curr_node, curr_edges);
			}
		}
		return node_to_edges;
	}

	public Sunflower findSunflower(Hypergraph h, int k_par) {
		if (h.edges.isEmpty()) {
			System.out.println("Edges are empty.");
			return null;
		}
		ArrayList<Tuple> f = findMaxDisjEdges(h.edges);
		if (f.size() > k_par) {
			// Empty core
			System.out.println("Empty core");
			return new Sunflower(f, new ArrayList<Integer>());
		} else {
			int u = findCommonNode(h.edges);
			System.out.println("Common node: " + u);
			ArrayList<Tuple> updated_e = new ArrayList<Tuple>();
			for (Tuple edge : h.edges) {
				if (arrContains(edge.elements, u)) {
					Tuple removed_u = new Tuple(arrWithout(edge.elements, u));
					if (!removed_u.onlyMinusOne()) {
						updated_e.add(new Tuple(arrWithout(edge.elements, u)));
					}
				}
			}
			// Print
			System.out.println("New edges:");
			if (updated_e.isEmpty())
				System.out.println("None.");
			for (Tuple t : updated_e) {
				System.out.println(t.toOutputString(showEverything));
			}
			System.out.println(">> findSunflower, k = " + k_par);
			Sunflower sun = findSunflower(new Hypergraph(h.nodes, updated_e), k_par);
			if (sun == null) {
				// TODO I think this is the right thing to return? Gotta check on this again
				// tho.
				ArrayList<Integer> empty_core = new ArrayList<Integer>();
				return new Sunflower(h.edges, empty_core);
			}
			ArrayList<Tuple> petals_with_u = sun.petals;
			for (Tuple petal : petals_with_u) {
				reAddU(petal, u);
			}
			ArrayList<Integer> core_with_u = sun.core;
			core_with_u.add(u);
			return new Sunflower(petals_with_u, core_with_u);
		}
	}

	/**
	 * Adds node u into the Tuple petal.
	 */
	private void reAddU(Tuple petal, int u) {
		for (int i = 0; i < petal.elements.length; i++) {
			if (petal.elements[i] == -1) {
				petal.elements[i] = u;
				return;
			}
		}
	}

	/**
	 * Returns elements but without u.
	 */
	private int[] arrWithout(int[] elements, int u) {
		int[] res = elements.clone();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] == u)
				res[i] = -1;
		}
		return res;
	}

	/**
	 * Checks of elements contains u.
	 */
	private boolean arrContains(int[] elements, int u) {
		for (int e : elements) {
			if (e == u)
				return true;
		}
		return false;
	}

	/**
	 * Returns the node which is contained in the maximum amount of edges. 
	 */
	private int findCommonNode(ArrayList<Tuple> edges_to_search) {
		HashMap<Integer, Integer> occurences = new HashMap<Integer, Integer>();
		int max_node = -1;
		occurences.put(max_node, 0);
		for (int i = 0; i < edges_to_search.size(); i++) {
			for (int node : edges_to_search.get(i).elements) {
				// Don't count -1 occurences
				if (node == -1)
					continue;
				if (occurences.get(node) == null) {
					occurences.put(node, 1);
				} else {
					occurences.put(node, occurences.get(node) + 1);
				}
				if (occurences.get(node) > occurences.get(max_node))
					max_node = node;
			}
		}
		return max_node;
	}

	/**
	 * Returns a maximal set of edges, so there is no edge to add greedily.
	 */
	private ArrayList<Tuple> findMaxDisjEdges(ArrayList<Tuple> edges_to_search) {
		ArrayList<Tuple> res = new ArrayList<Tuple>();
		for (Tuple e : edges_to_search) {
			boolean add_curr_edge = false;
			for (Tuple f : res) {
				if (e.intersectsWith(f)) {
					add_curr_edge = false;
				}
			}
			if (add_curr_edge)
				res.add(e);
		}
		return res;
	}

	/**
	 * Return a String to output for this hypergraph. The node 0 is a placeholder.
	 */
	public String toOutputString() {
		String res = "nodes: {";
		for (int i = 0; i < nodes.length; i++) {
			res += nodes[i];
			if (i < nodes.length - 1)
				res += ",";
			else
				res += "}\n";
		}
		res += "edges: {";
		for (int i = 0; i < edges.size(); i++) {
			Tuple t = edges.get(i);
			res += t.toOutputString(showEverything);
			res = (i < edges.size() - 1) ? res + "," : res;
		}
		res += "}\n";
		res += "mapping:\n";
		// Loop through all mappings from nodes to edges
		Iterator<Entry<Integer, ArrayList<Tuple>>> it = node_to_edges.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, ArrayList<Tuple>> e = it.next();
			res += " " + e.getKey() + " -> ";
			// If a node is isolated
			if (e.getValue().size() == 0)
				res += "iso";
			// Loop through all edges the entry e is contained in
			for (int i = 0; i < e.getValue().size(); i++) {
				res += e.getValue().get(i).toOutputString(showEverything);
				if (i < e.getValue().size() - 1)
					res += ",";
			}
			if (it.hasNext())
				res += "\n";
		}
		return res;
	}
}
