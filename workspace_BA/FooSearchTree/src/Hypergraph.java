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

	public Sunflower findSunflower(Hypergraph h, int k_par) {
		System.out.println("---\n>> findSunflower, k = " + k_par);
		if (h.edges.isEmpty()) {
			System.out.println("<< Hyp without edges, return null.\n---");
			return null;
		}
		ArrayList<Tuple> f = findMaxDisjEdges(h.edges);
		System.out.println("MaxDisjointEdges f.size(): " + f.size());
		if (f.size() > k_par) {
			// Empty core
			System.out.println("Found more than " + k_par + " petals, sunflower with empty core.");
			return new Sunflower(f, new ArrayList<Integer>());
		} else {
			int u = findCommonNode(h.edges);
			if (u == -1) {
				System.out.println("Can't find common node. nodes.length: " + h.nodes.length);
				return null; // TODO Correct?
			}
			System.out.println("Found common node: " + u);
			ArrayList<Tuple> updated_e = new ArrayList<Tuple>();
			// Remove u from edges that contain u
			for (Tuple edge : h.edges) {
				if (edge.arrContains(u)) {
					Tuple removed_u = new Tuple(arrWithout(edge.elements, u));
					if (!removed_u.onlyMinusOne()) {
						updated_e.add(new Tuple(arrWithout(edge.elements, u)));
					}
				}
			}
			// Print
			System.out.println("Edges that are left after removing u from petals:");
			if (updated_e.isEmpty())
				System.out.println("No edges left."); // TODO Do something?
			for (Tuple t : updated_e) {
				System.out.println(t.toOutputString(showEverything));
			}
			System.out.println(">> Find another sunflower");
			Sunflower sun = findSunflower(new Hypergraph(h.nodes, updated_e), k_par);
			if (sun == null) {
				System.out.println("Sunflower is null, return current edges with empty core.");
				// TODO I think this is the right thing to return? Gotta check on this again
				// tho.
				ArrayList<Integer> empty_core = new ArrayList<Integer>();
				return new Sunflower(h.edges, empty_core);
			}
			ArrayList<Tuple> petals_with_u = sun.petals;
			for (Tuple petal : petals_with_u) {
				petal.addElement(u);
			}
			ArrayList<Integer> core_with_u = sun.core;
			core_with_u.add(u);
			return new Sunflower(petals_with_u, core_with_u);
		}
	}

	public Hypergraph kernelize(Hypergraph hyp, int k) {
		System.out.println(">> kernelize");
		Sunflower sun = findSunflower(hyp, k);
		while (sun != null) { // TODO not sure if this is right
			System.out.println(">> KERNELIZE received following SUNFLOWER:");
			System.out.println(sun.toOutputString());
			// Reduction Rule: Only remove Sunflowers with at least k+1 petals
			if (!(sun.petals.size() >= k + 1)) {
				System.out.println("Sunflower of size " + sun.petals.size() + " not >= " + (k + 1)
						+ " or bigger, break kernelize().");
				break;
			}
			// Only kernelize, if the sunflower has enough petals? Look it up in
			// "Parametrized Algorithms".
			ArrayList<Tuple> updated_e = new ArrayList<Tuple>();
			// Check for every edge if the edge is also a petal
			for (Tuple edge : hyp.edges) {
				boolean add_edge = true;
				for (Tuple petal : sun.petals) {
					if (petal.equals(edge)) {
						add_edge = false;
						break;
					}
				}
				// If break is not reached, add edge.
				if (add_edge)
					updated_e.add(edge);
			}
			// Convert core to int[]
			int[] int_core = new int[sun.core.size()];
			for (int i = 0; i < sun.core.size(); i++) {
				int_core[i] = sun.core.get(i);
			}
			// Add core
			Tuple core = new Tuple(int_core);
			updated_e.add(core);
			// Update nodes
			ArrayList<Integer> updated_nodes = new ArrayList<Integer>();
			for (Tuple edge : updated_e) {
				for (int e : edge.elements) {
					if (!updated_nodes.contains(e))
						updated_nodes.add(e);
				}
			}
			// Change to int[]
			int[] int_nodes = new int[updated_nodes.size()];
			for (int i = 0; i < updated_nodes.size(); i++) {
				int_nodes[i] = updated_nodes.get(i);
			}
			// Construct updated graph
			hyp.nodes = int_nodes;
			hyp.edges = updated_e;
			hyp.node_to_edges = hyp.computeHashmap();
			// print new, kernelized hyp
			System.out.println("KERNELIZED hyp:");
			System.out.println(hyp.toOutputString());
			// Repeat
			sun = findSunflower(hyp, k);
			System.out.println(">> LOOP KERNELIZE.");
		}
		System.out.println(">> END KERNELIZE");
		return hyp;
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
			boolean add_curr_edge = true;
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

	/**
	 * Return a String to output for this hypergraph. The node 0 is a placeholder.
	 */
	public String toOutputString() {
		String res = "nodes: {";
		if(nodes.length == 0) {
			res += "}\n";
		}
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
