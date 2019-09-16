import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

public class Hypergraph {
	public String hypergraph_name;
	boolean show_minus_one_entries = true;
	boolean printGraphs = false;
	int d_par;
	int[] nodes;
	// A hyperedge is a Tuple, which contains an array of nodes (int)
	ArrayList<Tuple> edges = new ArrayList<Tuple>();

	public Hypergraph(int[] nodes, ArrayList<Tuple> edges) {
		this.nodes = nodes;
		this.edges = edges;
		this.d_par = computeD();
	}

	/**
	 * Generates a hypergraph from a vertex cover file, formated like the ones used
	 * by the PACE Challenge.
	 */
	public Hypergraph(String vc_file_path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(vc_file_path)));
			// First line is the descriptor
			String first_line = br.readLine();
			String[] first_split_line = first_line.split(" ");
			int num_nodes = Integer.parseInt(first_split_line[2]);
			int num_edges = Integer.parseInt(first_split_line[3]);
			nodes = new int[num_nodes];
			// nodes are labeled from 1 to num_nodes
			for (int i = 0; i < num_nodes; i++) {
				nodes[i] = i + 1;
			}
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] split_line = line.split(" ");
				int[] tuple_nodes = new int[2];
				tuple_nodes[0] = Integer.parseInt(split_line[0]);
				tuple_nodes[1] = Integer.parseInt(split_line[1]);
				edges.add(new Tuple(tuple_nodes));
			}
			br.close();
			d_par = computeD();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries to return a sunflower of size at least k_par in the given hypergraph.
	 * If there is no Sunflower which can be easily found, this returns null.
	 */
	public Sunflower findSunflower(Hypergraph local_hyp, int k_par, boolean mute) {
		if (!mute)
			System.out.println(">> findSunflower()");
		if (local_hyp.edges.isEmpty()) {
			if (!mute)
				System.out.println("<< Graph has no edges, return null.\n---");
			return null;
		}
		ArrayList<Tuple> f = findMaxDisjEdges(local_hyp.edges);
		if (!mute)
			System.out.println("Found f of size " + f.size() + ".");
		if (f.size() > k_par) {
			// Empty core
			if (!mute)
				System.out.println("<< Found more than k (" + k_par + ") petals, return sunflower with empty core.");
			return new Sunflower(f, new ArrayList<Integer>());
		} else {
			if (!mute)
				System.out.println("f.size() <= k, (" + f.size() + " <= " + k_par + "). Look for most common node.");
			int u = findCommonNode(local_hyp.edges);
			if (u == -1) {
				if (!mute)
					System.out.println("<< Common node is -1 (only edge left is empty), return null.");
				return null;
			}
			if (!mute)
				System.out.println("Found common node u: " + u + ". Removing " + u + " from edges.");
			ArrayList<Tuple> updated_e = new ArrayList<Tuple>();
			// Remove u from edges that contain u
			for (Tuple edge : local_hyp.edges) {
				if (edge.arrContains(u)) {
					Tuple removed_u = new Tuple(arrWithout(edge.elements, u));
					if (!removed_u.onlyMinusOne()) {
						updated_e.add(new Tuple(arrWithout(edge.elements, u)));
					}
				}
			}
			// Print
			if (updated_e.isEmpty()) {
				if (!mute)
					System.out.println("<< No edges left, returning null.");
				return null;
			}
			if (!mute) {
				System.out.println(updated_e.size() + " edges that are left after removing " + u + ":");
				if (printGraphs) {
					for (Tuple t : updated_e) {
						System.out.println(t.toOutputString(show_minus_one_entries));
					}
				} else {
					System.out.println("* hidden *");
				}
				System.out.println("Go into recursion, find core.");
			}
			// recur
			Sunflower sun = findSunflower(new Hypergraph(local_hyp.nodes, updated_e), k_par, mute);
			if (sun == null) {
				if (!mute)
					System.out.println("<< sun is null, return null.");
				return null;
			}
			// re-add u
			if (!mute)
				System.out.println("Got sunflower, re-adding " + u + ":");
			ArrayList<Tuple> petals_with_u = sun.petals;
			for (Tuple petal : petals_with_u) {
				petal.addElement(u);
			}
			ArrayList<Integer> core_with_u = sun.core;
			core_with_u.add(u);
			Sunflower updated_sun = new Sunflower(petals_with_u, core_with_u);
			if (!mute) {
				if (printGraphs) {
					System.out.println(updated_sun.toOutputString());
				} else {
					System.out.println("* hidden *");
				}
				System.out.println("<< Returning sunflower of size " + updated_sun.petals.size() + ".");
			}
			return updated_sun;
		}
	}

	/**
	 * Returns a kernel for the given hypergraph with the parameter k_par. By using
	 * non-uniform graphs this does not exactly use the Sunflower-Lemma correctly.
	 */
	public Hypergraph kernelizeNonUniform(Hypergraph local_hyp, int k_par, boolean mute) {
		// TODO handle this better
		if (k_par < 1)
			return null;
		// "Clone" hyp
		Hypergraph kernel = new Hypergraph(local_hyp.nodes, local_hyp.edges);
		int sf_counter = 0;
		if (!mute)
			System.out.println(">> kernelizeNonUniform()");
		Sunflower sun = findSunflower(kernel, k_par, mute);
		if (sun == null) {
			if (!mute)
				System.out.println("Initial sunflower is already null, nothing to kernelize.");
		}
		while (sun != null) { // TODO not sure if this is right
			if (!mute)
				System.out.println("KERNELIZE received a SUNFLOWER of size " + sun.petals.size() + ":");
			if (!mute) {
				if (printGraphs) {
					System.out.println(sun.toOutputString());
				} else {
					System.out.println("* hidden *");
				}
			}
			// Reduction Rule: Only remove Sunflowers with at least k+1 petals
			if (!(sun.petals.size() >= k_par + 1)) {
				if (!mute)
					System.out.println("Sunflower of size " + sun.petals.size() + " not >= " + (k_par + 1)
							+ " or bigger, break kernelize().");
				break;
			}
			// Remove petals from graph
			ArrayList<Tuple> updated_e = new ArrayList<Tuple>();
			for (Tuple edge : kernel.edges) {
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
			// Convert core to int[] and fill up with -1
			int[] int_core = new int[kernel.d_par];
			for (int i = 0; i < kernel.d_par; i++) {
				if (i < sun.core.size()) {
					int_core[i] = sun.core.get(i);
				} else {
					int_core[i] = -1;
				}
			}
			// Add core
			Tuple core = new Tuple(int_core);
			if (!updated_e.contains(core)) {
				updated_e.add(core);
			}
			// Update nodes
			ArrayList<Integer> updated_nodes = new ArrayList<Integer>();
			for (Tuple edge : updated_e) {
				for (int node : edge.elements) {
					if (!updated_nodes.contains(node) && node != -1)
						updated_nodes.add(node);
				}
			}
			// Change nodes to int[]
			int[] int_nodes = new int[updated_nodes.size()];
			for (int i = 0; i < updated_nodes.size(); i++) {
				int_nodes[i] = updated_nodes.get(i);
			}
			// Update kernel
			kernel.nodes = int_nodes;
			kernel.edges = updated_e;
			sf_counter++;
			if (!mute)
				System.out.print("  SFs removed:   " + sf_counter + "\r");
			// print kernel
			if (!mute)
				System.out.println("KERNELIZED hyp:");
			if (!mute) {
				if (printGraphs) {
					System.out.println(kernel.toOutputString());
				} else {
					System.out.println("* hidden *");
				}
			}
			// Repeat
			if (!mute)
				System.out.println("Go find another sunflower.");
			sun = findSunflower(kernel, k_par, mute);
			if (!mute)
				System.out.println("LOOP KERNELIZE.");
		}
		if (sf_counter > 0 && !mute)
			System.out.println();
		if (!mute)
			System.out.println("<< END KERNELIZE.");
		return kernel;
	}

	/**
	 * @throws TimeoutException 
	 * 
	 */
	public Hypergraph kernelizeUniform(Hypergraph local_hyp, int k_par, boolean mute, long kernel_timeout) throws TimeoutException {
		// TODO handle this better
		if (k_par < 1)
			return null;
		// Init kernel as empty hypergraph
		Hypergraph kernel = new Hypergraph(new int[local_hyp.nodes.length], new ArrayList<Tuple>());
		int sf_counter = 0;
		if (!mute)
			System.out.println(">> kernelizeUniform()");
		
		// Loop through {1,...,d}-uniform subgraphs
		for(int curr_d = 1; curr_d <= local_hyp.d_par; curr_d++) {
			// Find curr_d-uniform subgraph
			Hypergraph subgraph = getUniformSubgraph(local_hyp, curr_d);
			// Search first Sunflower
			Sunflower sun = findSunflower(subgraph, k_par, mute);
			if (sun == null) {
				if (!mute)
					System.out.println("Initial sunflower is already null, nothing to kernelize.");
			}
			
			while (sun != null) { // TODO not sure if this is right
				// Check timeout
				if(System.currentTimeMillis() > kernel_timeout) {
					throw new TimeoutException(); 
				}
				if (!mute)
					System.out.println("KERNELIZE received a SUNFLOWER of size " + sun.petals.size() + ":");
				if (!mute) {
					if (printGraphs) {
						System.out.println(sun.toOutputString());
					} else {
						System.out.println("* hidden *");
					}
				}
				// Reduction Rule: Only remove Sunflowers with at least k+1 petals
				if (!(sun.petals.size() >= k_par + 1)) {
					if (!mute)
						System.out.println("Sunflower of size " + sun.petals.size() + " not >= " + (k_par + 1)
								+ " or bigger, break kernelize().");
					break;
				}
				// Remove petals from graph
				ArrayList<Tuple> updated_e = new ArrayList<Tuple>();
				for (Tuple edge : subgraph.edges) {
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
				// Convert core to int[] and fill up with -1
				int[] int_core = new int[subgraph.d_par];
				for (int i = 0; i < subgraph.d_par; i++) {
					if (i < sun.core.size()) {
						int_core[i] = sun.core.get(i);
					} else {
						int_core[i] = -1;
					}
				}
				// Add core
				Tuple core = new Tuple(int_core);
				if (!updated_e.contains(core)) {
					updated_e.add(core);
				}
				// Update nodes
				ArrayList<Integer> updated_nodes = new ArrayList<Integer>();
				for (Tuple edge : updated_e) {
					for (int node : edge.elements) {
						if (!updated_nodes.contains(node) && node != -1)
							updated_nodes.add(node);
					}
				}
				// Change nodes to int[]
				int[] int_nodes = new int[updated_nodes.size()];
				for (int i = 0; i < updated_nodes.size(); i++) {
					int_nodes[i] = updated_nodes.get(i);
				}
				// Update subgraph-kernel
				subgraph.nodes = int_nodes;
				subgraph.edges = updated_e;
				sf_counter++;
				if (!mute)
					System.out.print("  SFs removed:   " + sf_counter + "\r");
				// print kernel
				if (!mute)
					System.out.println("KERNELIZED hyp:");
				if (!mute) {
					if (printGraphs) {
						System.out.println(subgraph.toOutputString());
					} else {
						System.out.println("* hidden *");
					}
				}
				// Repeat
				if (!mute)
					System.out.println("Go find another sunflower.");
				sun = findSunflower(subgraph, k_par, mute);
				if (!mute)
					System.out.println("LOOP KERNELIZE.");
			}
			// Update global kernel
			kernel = kernel.mergeWith(subgraph);
		}
		// Prints
		if (sf_counter > 0 && !mute)
			System.out.println();
		if (!mute)
			System.out.println("<< END KERNELIZE.");
		return kernel;
	}
	
	/**
	 * Returns the result of merging this and another Hypergraph.
	 */
	private Hypergraph mergeWith(Hypergraph subgraph) {
		ArrayList<Tuple> res_edges = new ArrayList<Tuple>();
		// Collect edges
		for(Tuple edge : this.edges) {
			if(!res_edges.contains(edge)) res_edges.add(edge);
		}
		for(Tuple edge : subgraph.edges) {
			if(!res_edges.contains(edge)) res_edges.add(edge);
		}
		// Collect nodes from new edge set
		ArrayList<Integer> node_list = new ArrayList<Integer>();
		for(Tuple edge : res_edges) {
			for(int node : edge.elements) {
				if(node != -1 && !node_list.contains(node)) node_list.add(node);
			}
		}
		// Convert to array
		int[] res_nodes = new int[node_list.size()];
		for(int i = 0; i < res_nodes.length; i++) {
			res_nodes[i] = node_list.get(i);
		}
		// Construct resulting graph
		Hypergraph merged_graph = new Hypergraph(res_nodes, res_edges);
		return merged_graph;
	}

	/**
	 * Returns a maximal d-uniform subgraph of the given hypergraph.
	 */
	private Hypergraph getUniformSubgraph(Hypergraph local_hyp, int curr_d) {
		ArrayList<Tuple> res_edges = new ArrayList<Tuple>();
		// Collect edges
		for(Tuple edge: local_hyp.edges) {
			if(actualSize(edge.elements) == curr_d) {
				res_edges.add(edge);
			}
		}
		// Collect nodes from new edge set
		ArrayList<Integer> node_list = new ArrayList<Integer>();
		for(Tuple edge : res_edges) {
			for(int node : edge.elements) {
				if(node != -1 && !node_list.contains(node)) node_list.add(node);
			}
		}
		// Convert to array
		int[] res_nodes = new int[node_list.size()];
		for(int i = 0; i < res_nodes.length; i++) {
			res_nodes[i] = node_list.get(i);
		}
		// Construct resulting graph
		Hypergraph res_graph = new Hypergraph(res_nodes, res_edges);
		return res_graph;
	}

	/**
	 * Returns the number of elements in the given array that are not -1.
	 */
	private int actualSize(int[] elements) {
		int counter = 0;
		for(int e : elements) {
			if(e != -1) counter++; 
		}
		return counter;
	}

	/**
	 * Returns the given array, but with u removed.
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
	 * Returns the most common node in the handed list of edges. Returns -1 if all
	 * edges are empty.
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
				if (occurences.get(node) > occurences.get(max_node)) {
					max_node = node;
				}
			}
		}
		return max_node;
	}

	/**
	 * Returns an inclusion-wise maximal subset of the given set of edges. Maximal
	 * means there is no edge to add greedily.
	 */
	private ArrayList<Tuple> findMaxDisjEdges(ArrayList<Tuple> edges_to_search) {
		ArrayList<Tuple> res = new ArrayList<Tuple>();
		HashSet<Integer> marked_elements = new HashSet<Integer>();
		for (Tuple e : edges_to_search) {
			// Check if e contains any marked nodes
			boolean flag = true;
			for (int node : e.elements) {
				if (marked_elements.contains(node)) {
					flag = false;
					break;
				}
			}
			// e can be added
			if (flag) {
				res.add(e);
				// Mark all nodes in e
				for (int node : e.elements) {
					marked_elements.add(node);
				}
			}
		}
		return res;
	}

	/*
	 * Returns the parameter d for this Hypergraph, which is the maximal number of
	 * nodes that exist in any Hyperedge.
	 */
	private int computeD() {
		int max = 0;
		for (Tuple e : edges) {
			if (max < e.elements.length)
				max = e.elements.length;
		}
		return max;
	}

	/**
	 * Returns a String representing this Hypergraph for debugging.
	 */
	public String toOutputString() {
		String res = "nodes: {";
		if (nodes.length == 0) {
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
			res += t.toOutputString(show_minus_one_entries);
			res = (i < edges.size() - 1) ? res + "," : res;
		}
		res += "}\n";
		return res;
	}

	/**
	 * Returns weather there is a hitting-set of size k_par in the given Hypergraph
	 * or not. Initially the solution sol is supposed to be empty.
	 * @param hs_timeout 
	 * @throws TimeoutException 
	 */
	public boolean hsSearchTree(Hypergraph local_hyp, int k_par, ArrayList<Integer> sol, boolean mute, long hs_timeout) throws TimeoutException {
		// TODO return solution
		int[] local_nodes = local_hyp.nodes;
		ArrayList<Tuple> local_edges = local_hyp.edges;
		// check for empty edges at the start
		for (Tuple edge : local_edges) {
			if (edge.elements.length == 0) {
				System.out.println("Empty edge."); // should not be reached
				return false;
			}
		}
		for (int i = 0; i < local_edges.size(); i++) {
			// Check for timeout
			if(System.currentTimeMillis() > hs_timeout) {
				throw new TimeoutException();
			}
			Tuple curr_edge = local_edges.get(i);
			boolean edge_is_covered = false;
			for (int j = 0; j < local_hyp.d_par; j++) {
				if (sol.contains(curr_edge.elements[j])) {
					edge_is_covered = true;
				}
			}
			if (!edge_is_covered) {
				boolean flag = false;
				if (sol.size() < k_par) {
					// branch into d branches, adding every element of the edge
					for (int j = 0; j < local_hyp.d_par; j++) {
						// Don't add -1
						if (curr_edge.elements[j] == -1)
							continue;
						// Try adding this
						sol.add(curr_edge.elements[j]);
						// print
						if (!mute)
							System.out.print("  Sol size: " + sol.size() + "\r");
						flag = flag || hsSearchTree(local_hyp, k_par, sol, mute, hs_timeout);
						if (flag) {
							return true;
						} else {
							// This branch failed, remove element again, to clean up for next branch
							sol.remove((Object) curr_edge.elements[j]);
						}
					}
					return flag;
				} else {
					return false;
				}
			}
		}
		return true;
	}
}
