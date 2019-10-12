package pack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

public class Hypergraph {
	public String hypergraph_name;
	public String formula_name;
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
		kernel.hypergraph_name = this.hypergraph_name;
		kernel.formula_name = this.formula_name;
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
	public Hypergraph kernelizeUniform(int k_par, boolean mute, long kernel_timeout, boolean timeout_active)
			throws TimeoutException {
		// Check Timeout
		if (System.currentTimeMillis() > kernel_timeout && timeout_active) {
			throw new TimeoutException();
		}

		// This can happen if heuristics already solved this graph (no edges/nodes
		// remain) but used more than current k
		if (k_par < 0)
			return this;
		// Having no nodes means having only -1 entries
		int[] init_nodes = new int[this.nodes.length];
		for (int i = 0; i < init_nodes.length; i++) {
			init_nodes[i] = -1;
		}
		// Init kernel as empty hypergraph
		Hypergraph kernel = new Hypergraph(init_nodes, new ArrayList<Tuple>());
		kernel.hypergraph_name = this.hypergraph_name;
		kernel.formula_name = this.formula_name;
		int sf_counter = 0;
		if (!mute)
			System.out.println(">> kernelizeUniform()");

		// Loop through {1,...,d}-uniform subgraphs
		for (int curr_d = 1; curr_d <= this.d_par; curr_d++) {
			// Find curr_d-uniform subgraph
			Hypergraph subgraph = this.getUniformSubgraph(curr_d);
			// Search first Sunflower
			Sunflower sun = findSunflower(subgraph, k_par, mute);
			if (sun == null) {
				if (!mute)
					System.out.println("Initial sunflower is already null, nothing to kernelize.");
			}

			while (sun != null) { // TODO not sure if this is right
				// Check timeout
				if (System.currentTimeMillis() > kernel_timeout) {
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

	public Hypergraph kernelizeBevern(int k_par, boolean mute, long kernel_timeout, boolean timeout_active)
			throws TimeoutException {
		// Check Timeout
		if (System.currentTimeMillis() > kernel_timeout && timeout_active) {
			throw new TimeoutException();
		}

		// Declare edge set
		ArrayList<Tuple> edge_set = new ArrayList<Tuple>();

		// Declare maps
		HashMap<Tuple, Integer> petals_per_core = new HashMap<Tuple, Integer>();
		HashMap<Tuple, HashSet<Integer>> used_verts_per_core = new HashMap<Tuple, HashSet<Integer>>();

		// Init maps
		for (Tuple edge : this.edges) {
			ArrayList<Tuple> possible_cores_for_edge = getPossibleCores(edge);
			for (Tuple possible_core : possible_cores_for_edge) {
				petals_per_core.put(possible_core, 0);
				used_verts_per_core.put(possible_core, new HashSet<Integer>());
			}
		}

		// Kernelize
		for (Tuple edge : this.edges) {
			ArrayList<Tuple> possible_cores_for_edge = getPossibleCores(edge);
			boolean no_core_more_than_k_petals = true;
			for (Tuple possible_core : possible_cores_for_edge) {
				if (petals_per_core.get(possible_core) > k_par) {
					no_core_more_than_k_petals = false;
				}
			}

			if (no_core_more_than_k_petals) {
				edge_set.add(edge);
				for (Tuple possible_core : possible_cores_for_edge) {
					int[] verts_of_edge_wo_core = edge.elements;
					// Remove core verts
					for (int core_vert : possible_core.elements) {
						verts_of_edge_wo_core = arrWithout(verts_of_edge_wo_core, core_vert);
					}
					boolean no_vert_of_edge_used = true;
					for (int edge_vert : verts_of_edge_wo_core) {
						if (used_verts_per_core.get(possible_core).contains(edge_vert)) {
							no_vert_of_edge_used = false;
						}
					}
					if (no_vert_of_edge_used) {
						petals_per_core.put(possible_core, petals_per_core.get(possible_core) + 1);
						HashSet<Integer> used_set = used_verts_per_core.get(possible_core);
						for (int edge_vert : verts_of_edge_wo_core) {
							used_set.add(edge_vert);
						}
						used_verts_per_core.put(possible_core, used_set);
					}
				}
			}
		}

		// Collect nodes
		ArrayList<Integer> nodes_list = new ArrayList<Integer>();
		for (Tuple edge : edge_set) {
			for (int edge_node : edge.elements) {
				if (!nodes_list.contains(edge_node) && edge_node != -1)
					nodes_list.add(edge_node);
			}
		}
		// Change nodes to int[]
		int[] nodes_arr = new int[nodes_list.size()];
		for (int i = 0; i < nodes_list.size(); i++) {
			nodes_arr[i] = nodes_list.get(i);
		}

		// Result
		Hypergraph kernel = new Hypergraph(nodes_arr, edge_set);
		kernel.hypergraph_name = this.hypergraph_name;
		kernel.formula_name = this.formula_name;
		return kernel;
	}

	private ArrayList<Tuple> getPossibleCores(Tuple edge) {
		int[] elements = edge.elements;
		ArrayList<Tuple> possible_cores = new ArrayList<Tuple>();
		for (int i = 0; i < (1 << elements.length); i++) {
			ArrayList<Integer> current_core = new ArrayList<Integer>();
			for (int j = 0; j < elements.length; j++) {
				if ((i & (1 << j)) > 0) {
					current_core.add(elements[j]);
				}
			}
			// Change to int[]
			int[] current_core_arr = new int[current_core.size()];
			for (int k = 0; k < current_core.size(); k++) {
				current_core_arr[k] = current_core.get(k);
			}
			possible_cores.add(new Tuple(current_core_arr));
		}
		return possible_cores;
	}

	/**
	 * Returns the result of merging this and another Hypergraph.
	 */
	private Hypergraph mergeWith(Hypergraph subgraph) {
		ArrayList<Tuple> res_edges = new ArrayList<Tuple>();
		// Collect edges
		for (Tuple edge : this.edges) {
			if (!res_edges.contains(edge))
				res_edges.add(edge);
		}
		for (Tuple edge : subgraph.edges) {
			if (!res_edges.contains(edge))
				res_edges.add(edge);
		}
		// Collect nodes from new edge set
		ArrayList<Integer> node_list = new ArrayList<Integer>();
		for (Tuple edge : res_edges) {
			for (int node : edge.elements) {
				if (node != -1 && !node_list.contains(node))
					node_list.add(node);
			}
		}
		// Convert to array
		int[] res_nodes = new int[node_list.size()];
		for (int i = 0; i < res_nodes.length; i++) {
			res_nodes[i] = node_list.get(i);
		}
		// Construct resulting graph
		Hypergraph merged_graph = new Hypergraph(res_nodes, res_edges);
		merged_graph.hypergraph_name = this.hypergraph_name;
		merged_graph.formula_name = this.formula_name;
		return merged_graph;
	}

	/**
	 * Returns a maximal d-uniform subgraph of the given hypergraph.
	 */
	private Hypergraph getUniformSubgraph(int curr_d) {
		ArrayList<Tuple> res_edges = new ArrayList<Tuple>();
		// Copy edges
		for (Tuple edge : this.edges) {
			try {
				res_edges.add(edge.copyThis());
			} catch (Exception e) {
				System.out.println("Copy Tuple broke.");
				e.printStackTrace();
				return null;
			}
		}
		// Collect nodes from new edge set
		ArrayList<Integer> node_list = new ArrayList<Integer>();
		for (Tuple edge : res_edges) {
			for (int node : edge.elements) {
				if (node != -1 && !node_list.contains(node))
					node_list.add(node);
			}
		}
		// Convert to array
		int[] res_nodes = new int[node_list.size()];
		for (int i = 0; i < res_nodes.length; i++) {
			res_nodes[i] = node_list.get(i);
		}
		// Construct resulting graph
		Hypergraph res_graph = new Hypergraph(res_nodes, res_edges);
		return res_graph;
	}

	/**
	 * Returns the given array, but with u replaced by -1.
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
	public ArrayList<Tuple> findMaxDisjEdges(ArrayList<Tuple> edges_to_search) {
		ArrayList<Tuple> res = new ArrayList<Tuple>();
		HashSet<Integer> marked_elements = new HashSet<Integer>();
		for (Tuple e : edges_to_search) {
			// Skip empty edges
			if (e.onlyMinusOne()) {
				continue;
			}
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
					if (node != -1) {
						marked_elements.add(node);
					}
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
	 */
	public boolean hsSearchTree(int k_par, HashSet<Integer> sol, boolean mute, long hs_timeout,
			boolean use_branch_and_bound, boolean timeout_active) throws TimeoutException {
		// TODO return solution
		// If heuristics used more than k_par nodes
		if (k_par < 0) {
			return false;
		}
		// Test bounding condition
		if (use_branch_and_bound) {
			// If there is a matching left which is bigger than what we can afford, return
			// false
			int max_match_size = calcMaxMatchSize(sol);
			int k_left = k_par - sol.size();
			if (k_left < max_match_size) {
				return false;
			}
		}
		// If there is still a solution to be found, test if all edges are covered.
		// Branch if not.
		for (int i = 0; i < this.edges.size(); i++) {
			// Check for timeout
			if (System.currentTimeMillis() > hs_timeout && timeout_active) {
				throw new TimeoutException();
			}
			// Check for empty (only -1) edges
			Tuple curr_edge = this.edges.get(i);
			if (curr_edge.onlyMinusOne()) {
				System.out.print("! Empty edge.");
				return false;
			}
			boolean edge_is_covered = false;
			for (int j = 0; j < curr_edge.elements.length; j++) {
				if (sol.contains(curr_edge.elements[j])) {
					edge_is_covered = true;
				}
			}
			if (!edge_is_covered) {
				boolean flag = false;
				if (sol.size() < k_par) {
					// branch into (at most) d branches, adding every element of the edge
					for (int j = 0; j < curr_edge.elements.length; j++) {
						// Don't add -1
						if (curr_edge.elements[j] == -1)
							continue;
						// Try adding current element
						sol.add(curr_edge.elements[j]);
						// print
						if (!mute)
							System.out.print("  sol: " + sol + "\r");
						flag = flag || hsSearchTree(k_par, sol, mute, hs_timeout, use_branch_and_bound, timeout_active);
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

	/**
	 * Returns the size of a maximal matching on the not covered edges of this.
	 * 
	 * @param sol The nodes that are part of the current solution and shall be
	 *            ignored.
	 */
	private int calcMaxMatchSize(HashSet<Integer> sol) {
		HashSet<Integer> matching_nodes = new HashSet<Integer>();
		// Size of the matching refers to the number of edges
		int matching_size = 0;
		for (Tuple edge : this.edges) {
			// Empty edges can not be part of matchings and do not need to be considered
			if (edge.actualSize() == 0) {
				continue;
			}
			// Check if edge is covered
			boolean is_covered = false;
			for (int edge_node : edge.elements) {
				if (sol.contains(edge_node)) {
					is_covered = true;
					break;
				}

			}
			// If the edge is covered, go next
			if (is_covered) {
				continue;
			}
			// If the edge is not covered, check if a node of the edge is contained in the
			// matching
			boolean is_matched = false;
			for (int edge_node : edge.elements) {
				if (matching_nodes.contains(edge_node)) {
					is_matched = true;
					break;
				}
			}
			// If the edge is already matched, it can not contribute to the matching
			if (is_matched) {
				continue;
			}
			// Else add all nodes to the matching
			for (int edge_node : edge.elements) {
				matching_nodes.add(edge_node);
			}
			matching_size++;
		}
		return matching_size;
	}

	public int removeDanglingNodes(boolean mute, long kernel_timeout, boolean timeout_active) throws TimeoutException {
		// Check for timeout
		if (System.currentTimeMillis() > kernel_timeout && timeout_active) {
			throw new TimeoutException();
		}
		int node_counter = 0;
		HashMap<Integer, ArrayList<Tuple>> node_occurences = this.getNodeOccurences();
		// Check for dangling nodes and singletons
		Iterator<Entry<Integer, ArrayList<Tuple>>> it = node_occurences.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, ArrayList<Tuple>> pair = it.next();
			int curr_node = pair.getKey();
			ArrayList<Tuple> curr_occurences = pair.getValue();
			// Remove any isolated nodes
			if (curr_occurences == null) {
				this.nodes = arrWithout(this.nodes, curr_node);
				node_counter++;
			}
			// If this node is only contained in one edge
			else if (curr_occurences.size() == 1) {
				Tuple curr_edge = curr_occurences.get(0);
				// If there are other nodes in this edge
				if (curr_edge.actualSize() > 1) {
					// Remove this node
					this.nodes = arrWithout(this.nodes, curr_node);
					node_counter++;
					// Now this edge contains a deleted node -> update this edge
					// Generate elements without the removed node
					int[] shrinked_elements = arrWithout(curr_edge.elements, curr_node);
					// Update edge (this manipulates this.edges)
					curr_edge.elements = shrinked_elements;
				}
				// Else, this is a singleton and will be removed by removeSingletons()
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
		return node_counter;
	}

	public int removeSingletons(boolean mute, long kernel_timeout) {
		int number_of_singletons_removed = 0;
		HashSet<Tuple> singletons_to_remove = new HashSet<Tuple>();
		// Look for singletons
		for (Tuple edge : this.edges) {
			if (edge.actualSize() == 1) {
				// This can still contain equivalent edges like (1,-1), (-1,1)
				singletons_to_remove.add(edge);
			}
		}
		HashSet<Integer> solution_nodes = new HashSet<Integer>();
		// Remove singletons
		for (Tuple edge : singletons_to_remove) {
			// Remove edge
			this.edges.remove(edge);
			// Find node from singleton edge
			for (int node : edge.elements) {
				if (node != -1) {
					solution_nodes.add(node);
				}
			}
		}
		// Remove the node contained in a singleton edge from the node set
		for (int solution_node : solution_nodes) {
			int nodes_hash_before = this.nodes.hashCode();
			// Remove solution nodes
			this.nodes = arrWithout(this.nodes, solution_node);
			int nodes_hash_after = this.nodes.hashCode();
			// Make sure the nodes set actually changed
			if (nodes_hash_before != nodes_hash_after) {
				number_of_singletons_removed++;
			}
		}
		// Now there are still edges that are covered by solution nodes, remove them as
		// well
		ArrayList<Tuple> covered_edges = new ArrayList<Tuple>();
		for (Tuple edge : this.edges) {
			for (int sol_node : solution_nodes) {
				if (edge.arrContains(sol_node)) {
					covered_edges.add(edge);
				}

			}
		}
		// Remove covered edges
		for (Tuple covered_edge : covered_edges) {
			this.edges.remove(covered_edge);
		}
		return number_of_singletons_removed;
	}

	private HashMap<Integer, ArrayList<Tuple>> getNodeOccurences() {
		HashMap<Integer, ArrayList<Tuple>> node_occurences = new HashMap<Integer, ArrayList<Tuple>>();
		// Init map by looking through nodes
		for (int node : this.nodes) {
			if (node == -1) {
				continue;
			}
			node_occurences.put(node, null);
		}
		// Fill map by looking through edges
		for (Tuple edge : this.edges) {
			for (int node : edge.elements) {
				if (node == -1) {
					continue;
				}
				// Update current List of occurences
				ArrayList<Tuple> curr_list = node_occurences.get(node);
				if (curr_list == null) {
					ArrayList<Tuple> list = new ArrayList<Tuple>();
					// Init list if there is none
					list.add(edge);
					node_occurences.put(node, list);
				} else {
					// Expand list
					curr_list.add(edge);
					node_occurences.put(node, curr_list);
				}
			}
		}
		return node_occurences;
	}

	/**
	 * Returns a set of edges only containing the specified nodes. Edges containing
	 * nodes which have been deleted will shrink by doing this.
	 */
	private ArrayList<Tuple> update_edges(int[] local_nodes, ArrayList<Tuple> local_edges) {
		ArrayList<Tuple> updated_edges = new ArrayList<Tuple>();
		// Fill node set
		HashSet<Integer> node_set = new HashSet<Integer>();
		for (int node : local_nodes) {
			node_set.add(node);
		}
		// Prevent the empty edge from being removed
		node_set.add(-1);
		// Check edges for deleted nodes
		for (Tuple edge : local_edges) {
			for (int edge_node : edge.elements) {
				// Remove deleted nodes from edges
				if (!node_set.contains(edge_node)) {
					edge.removeElement(edge_node);
				}
			}
			// Add edge, also if it is empty, // TODO Do this properly
			if (!updated_edges.contains(edge) || edge.onlyMinusOne()) {
				updated_edges.add(edge);
			}
		}
		return updated_edges;
	}

	/**
	 * Returns a copy of this Hypergraph.
	 * 
	 * @return
	 */
	public Hypergraph copyThis() {
		// Copy nodes
		int[] copy_nodes = new int[this.nodes.length];
		for (int i = 0; i < this.nodes.length; i++) {
			copy_nodes[i] = this.nodes[i];
		}

		// Copy edges
		ArrayList<Tuple> copy_edges = new ArrayList<Tuple>();
		for (Tuple edge : this.edges) {
			Tuple copy_of_edge = null;
			// Copy edge (Tuple), if hashIDs are equal an exception is thrown
			copy_of_edge = edge.copyThis();
			copy_edges.add(copy_of_edge);
		}

		// Contruct copy hyp
		Hypergraph copy_hyp = new Hypergraph(copy_nodes, copy_edges);

		// Also copy name
		copy_hyp.hypergraph_name = this.hypergraph_name;
		copy_hyp.formula_name = this.formula_name;
		return copy_hyp;
	}

	/**
	 * Returns an identifier including the formula this graph belongs to.
	 */
	public String getIdentifier() {
		return this.hypergraph_name + "," + this.formula_name;
	}

}
