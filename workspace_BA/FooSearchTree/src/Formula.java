import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Holds information about a logical formula. Provides methods to parse, solve
 * and print the formula.
 */
public class Formula {
	String name;
	int[] universe;
	HashMap<String, Relation> rels;
	int k_par;
	String[] bound_vars;
	int c_par;
	ArrayList<String[]> clauses;
	ArrayList<int[]> assignments = new ArrayList<int[]>();

	public Formula() {
		// Do everything yourself.
	}

	public Formula(String path) {
		parseFormula(path);
		// Generate all assignments
		generateAssignments();
	}

	/**
	 * Parses a file specified by the path parameter into an formula instance.
	 */
	private void parseFormula(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";

			// Name of the formula
			line = br.readLine();
			name = line;
			// Universe
			line = br.readLine();
			String[] universe_line = line.split(",");
			universe = new int[universe_line.length];
			for (int i = 0; i < universe_line.length; i++) {
				universe[i] = Integer.parseInt(universe_line[i]);
			}
			// Signature
			line = br.readLine();
			String[] relations = line.split(";");
			// Solution S is not contained in rels
			rels = new HashMap<String, Relation>();
			for (String s : relations) {
				int negation_offset = (s.charAt(0) == '~') ? 1 : 0;
				String identifier = s.substring(negation_offset, negation_offset + 1);
				int arity = Integer.parseInt(s.substring(negation_offset + 1, negation_offset + 2));
				int from = s.indexOf("{") + 1;
				int to = s.indexOf("}");
				// Check if Relation is empty
				if (!(to > from)) {
					rels.put(identifier, new Relation(identifier, arity, new HashSet<Tuple>()));
					continue;
				}
				String content = s.substring(from, to);
				String[] content_split = content.split(",");
				int[][] elements = new int[content_split.length][arity];
				HashSet<Tuple> hs = new HashSet<Tuple>();
				for (int i = 0; i < content_split.length; i++) {
					String[] element_split = content_split[i].split("\\|");
					for (int j = 0; j < arity; j++) {
						elements[i][j] = Integer.parseInt(element_split[j].replaceAll("[()]", ""));
					}
					hs.add(new Tuple(elements[i]));
				}
				rels.put(identifier, new Relation(identifier, arity, hs));
			}
			// Parameter k
			line = br.readLine();
			k_par = Integer.parseInt(line);
			// Bound variables
			line = br.readLine();
			bound_vars = line.split(",");
			// Parameter c
			c_par = bound_vars.length;
			// Formula as clauses
			clauses = new ArrayList<String[]>();
			while ((line = br.readLine()) != null) {
				String[] clause_split = line.split(" ");
				String[] clause = new String[clause_split.length];
				for (int i = 0; i < clause_split.length; i++) {
					clause[i] = clause_split[i];
				}
				clauses.add(clause);
			}
			br.close();
			// TODO Add flag which states if the solution should be true or not
			// TODO Add flag which lets Relations be refelxive
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Hypergraph reduceToHS() {
		// Nodes of the Hypergraph are derived from the universe of the formula
		int[] hyp_nodes = new int[universe.length];
		for (int i = 0; i < universe.length; i++) {
			hyp_nodes[i] = universe[i];
		}
		// Edges of the hypergraph are found while checking clauses
		ArrayList<Tuple> hyp_edges = new ArrayList<Tuple>();
		// The solution S is always empty in this reduction
		ArrayList<Integer> empty_sol = new ArrayList<Integer>();
		for (int i = 0; i < assignments.size(); i++) {
			for (int j = 0; j < clauses.size(); j++) {
				String[] curr_clause = clauses.get(j);
				// If clause does not hold, add edge containing current assignment
				if (!checkClause(curr_clause, assignments.get(i), empty_sol)) {
					// Find elements that are bound to S in this clause
					Tuple edge_to_add = findCandidates(curr_clause, assignments.get(i));
					if (!hyp_edges.contains(edge_to_add)) {
						hyp_edges.add(edge_to_add);
					}
				}
			}
		}
		// Contruct HashMap which maps nodes to all edges they are contained in
		HashMap<Integer, ArrayList<Tuple>> node_to_edges = new HashMap<Integer, ArrayList<Tuple>>();
		// First, every node in the universe gets an empty list of edges
		for (int node : hyp_nodes) {
			node_to_edges.put(node, new ArrayList<Tuple>());
		}
		for (Tuple t : hyp_edges) {
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
		Hypergraph hyp = new Hypergraph(hyp_nodes, hyp_edges, node_to_edges);
		return hyp;
	}

	private Tuple findCandidates(String[] curr_clause, int[] assignment) {
		// All edges have length c_par, but can contain null
		int[] candidates = new int[c_par];
		for(int i = 0; i < candidates.length; i++) {
			candidates[i] = -1;
		}
		int i = 0;
		for (String s : curr_clause) {
			if (s.charAt(0) == 'S') {
				candidates[i] = assign(s, assignment)[0];
				i++;
			}
		}
		return new Tuple(candidates);
	}

	/**
	 * Assigns a literal wit the given assignment. Given ~R(y,x) and assignment
	 * x=2,y=1 this returns [2,1].
	 */
	private int[] assign(String literal, int[] assignment) {
		// s = ~R(y,x)
		// assignment = [1,2] (x=1,y=2)
		// Extract variables
		int negation_offset = (literal.charAt(0) == '~') ? 1 : 0;
		String id = literal.substring(negation_offset, negation_offset + 1);
		// id = "R"
		String content = literal.substring(negation_offset + 1);
		// content = "(y,x)"
		content = content.replaceAll("[()]", "");
		// content = "y,x"
		String[] variables = content.split(",");
		// variables = ["y","x"]
		int[] assi_elements = new int[variables.length];
		// assi_elements = [0,0]
		for (int i = 0; i < variables.length; i++) {
			for (int j = 0; j < universe.length; j++) {
				// bound_vars = ["x","y","z"]
				// y: i = 0, j = 1
				if (variables[i].equals(bound_vars[j])) {
					assi_elements[i] = assignment[j];
					// assi = [2,0]
					break;
				}
			}
		}
		return assi_elements;
	}

	/**
	 * Recursively searches a solution sol of size k_par for the given formula.
	 * 
	 * @return True if a solution of size k is found, else false.
	 */
	public boolean searchTree(int k_par, ArrayList<Integer> sol) {
		// Return if |S| > k
		if (sol.size() > k_par) {
			return false;
		}
		// Check all clauses considering S
		for (int i = 0; i < assignments.size(); i++) {
			for (int j = 0; j < clauses.size(); j++) {
				// If a clause is false, branch over current assignment
				if (!checkClause(clauses.get(j), assignments.get(i), sol)) {
					HashSet<Integer> f = new HashSet<Integer>();
					for (int a : assignments.get(i)) {
						// TODO check if the literal a even matters to the current clause
						if (!sol.contains(a)) {
							f.add(a);
						}
					}
					// If there is anything to branch over
					if (!f.isEmpty()) {
						boolean flag = false;
						// Construct branches with each branch adding one literal to S respectively
						for (int y : f) {
							ArrayList<Integer> sol_with_y = (ArrayList<Integer>) sol.clone();
							sol_with_y.add(y);
							// - print
							System.out.print("S: ");
							for (int s : sol_with_y)
								System.out.print(s + " ");
							System.out.println();
							// - print
							// if one branch is successful we win, else go back through recursion.
							flag = flag || searchTree(k_par, sol_with_y);
							if (flag)
								return true;
						}
						return flag;
					}
					// No possible branches, return false
					else {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Generates all possible subsets of the Universe of size c_par and put them
	 * into the assignments list.
	 */
	private void generateAssignments() {
		int[] curr_assi_ind = new int[c_par];
		ArrayList<int[]> assi_indices = new ArrayList<int[]>();
		int inc_pos = c_par - 1;
		boolean overflow = false;
		// Number of iterations = universe.length ^ c_par
		// TODO maybe change while to for
		while (!overflow) {
			// Add to set of assignments
			int[] add_this_copy = curr_assi_ind.clone();
			assi_indices.add(add_this_copy);
			// Move to the right until there is something to increment
			while (inc_pos + 1 < curr_assi_ind.length && curr_assi_ind[inc_pos + 1] < universe.length) {
				inc_pos++;
			}
			// Increment current position if possible
			if (curr_assi_ind[inc_pos] < universe.length - 1) {
				curr_assi_ind[inc_pos]++;
			}
			// If not possible, move left
			else {
				// Move left until there is something to increment
				while (inc_pos - 1 >= 0 && !(curr_assi_ind[inc_pos] < universe.length - 1)) {
					// Also set everything back to 0 on the way
					curr_assi_ind[inc_pos] = 0;
					inc_pos--;
					// If we reached far left and can not increment further, we are done.
					if (inc_pos == 0 && curr_assi_ind[inc_pos] == universe.length - 1) {
						overflow = true;
					}
				}
				// After moving left far enough, increment
				curr_assi_ind[inc_pos]++;
			}
		}
		// Generate actual assignments from indices
		for (int i = 0; i < assi_indices.size(); i++) {
			int[] s_arr = new int[c_par];
			// c_par is equal to bound_vars.lenght
			for (int j = 0; j < c_par; j++) {
				// assi_indices.get(i)[j] contains the index of the element in the universe,
				// that should now be added to the assignment
				s_arr[j] = universe[assi_indices.get(i)[j]];
			}
			assignments.add(s_arr);
		}
	}

	/**
	 * Checks if the specified clause holds under the specified assignment with S
	 * being sol.
	 */
	private boolean checkClause(String[] clause, int[] assignment, ArrayList<Integer> sol) {
		// Evaluate literals one at a time
		for (String l : clause) {
			int negation_offset = (l.charAt(0) == '~') ? 1 : 0;
			String id = (negation_offset == 1) ? l.substring(1,2) : l.substring(0,1); 
			int[] assi_elements = assign(l, assignment);
			// Handle S(x)
			if (id.equals("S")) {
				// check if S contains the element
				if (sol.contains(assi_elements[0])) {
					return true;
				}
				// Else: continue
			} else {
				Relation r = rels.get(id);
				if (r != null) {
					// Test if relation r contains the tuple t or not
					// (v1, v2)
					Tuple t = new Tuple(assi_elements);
					if (r.elements.contains(t) && negation_offset == 0) {
						// If one literal is correct, we can stop.
						return true;
					}
					// negation case
					else if (!r.elements.contains(t) && negation_offset == 1) {
						return true;
					}
					// Else: Assignment does not hold on literal l, continue
				} else {
					System.out.println("Unknown relation symbol.");
				}
			}
		}
		return false;
	}

	/**
	 * Prints info about this into sysout.
	 */
	public void printFormula() {
		System.out.println(name + ":");
		System.out.print("U = {");
		for (int i = 0; i < universe.length; i++) {
			if (i + 1 != universe.length)
				System.out.print(universe[i] + ",");
			else
				System.out.print(universe[i]);
		}
		System.out.println("}");
		System.out.println("Relations:");
		for (Entry<String, Relation> r : rels.entrySet()) {
			System.out.println(r.getValue().toOutputString());
		}
		System.out.println("Parameter k = " + k_par);
		System.out.print("Bound varibales: (");
		for (int i = 0; i < bound_vars.length; i++) {
			if (i + 1 != bound_vars.length)
				System.out.print(bound_vars[i] + ",");
			else
				System.out.print(bound_vars[i]);
		}
		System.out.println(")");
		System.out.println("Clauses:");
		for (String[] c : clauses) {
			for (String l : c) {
				System.out.print(l + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Saves this Formula to a file.
	 */
	public void saveToFile() {
		try {
			File out_file = new File("saves" + File.separator + this.name + "-save.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
			// Name
			bw.write(name + "\n");
			// Universe
			for (int i = 0; i < universe.length; i++) {
				if (i + 1 != universe.length)
					bw.write(universe[i] + ",");
				else
					bw.write(universe[i] + "\n");
			}
			// Relations
			Iterator<Entry<String, Relation>> it = rels.entrySet().iterator();
			while (it.hasNext()) {
				bw.write(it.next().getValue().toOutputString());
				if (it.hasNext())
					bw.write(";");
			}
			bw.write("\n");
			// Parameter k
			bw.write(Integer.toString(k_par));
			bw.write("\n");
			// Bound vars
			for (int i = 0; i < bound_vars.length; i++) {
				if (i < bound_vars.length - 1)
					bw.write(bound_vars[i] + ",");
				else
					bw.write(bound_vars[i]);
			}
			bw.write("\n");
			// Formula in clauses
			for (String[] c : clauses) {
				for (String l : c) {
					bw.write(l + " ");
				}
				bw.write("\n");
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
