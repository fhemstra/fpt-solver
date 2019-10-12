package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

/**
 * Holds information about a logical formula. Provides methods to parse, solve
 * and print the formula.
 */
public class Formula {
	String formula_name;
	String graph_name;
	String guard_rel_id;
	double graph_density;
	int[] universe;
	int uni_min;
	int uni_max;
	HashMap<String, Relation> relation_map;
	String[] bound_variables;
	int c_par;
	ArrayList<String[]> clauses;
	long nr_of_assignments;

	// TODO make a format-file.
	/**
	 * Constructs a Formula from just a formula-file located at the given path. The
	 * file must contain all information and must be formatted as can be seen in .
	 */
	public Formula(String path) {
		parseInternalFormula(path);
		nr_of_assignments = (long) Math.pow(universe.length, c_par);
	}

	// TODO make PACE-format file.
	/**
	 * Constructs a Formula for a graph problem. The formula itself must be
	 * specified in a formula file located at the first path. The graph must be
	 * located at the second path and be formated as can be seen in .
	 */
	public Formula(String form_path, String graph_path) {
		parseExternalFormula(form_path, graph_path);
		nr_of_assignments = (long) Math.pow(universe.length, c_par);
	}

	/**
	 * Parses a graph and a formula.
	 */
	private void parseExternalFormula(String form_path, String graph_path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(form_path));
			String line = "";
			// Name of the formula
			line = br.readLine();
			formula_name = line;
			graph_name = new File(graph_path).getName().split("\\.")[0];
			// Universe from .gr file
			universe = getExternalUniverse(graph_path);
			// Skip relations, only E is important
			line = br.readLine();
			while (true) {
				if (line.contains(";")) {
					break;
				}
				line = br.readLine();
			}
			relation_map = new HashMap<String, Relation>();
			// Parse external file
			HashSet<Tuple> edge_set = parseExternalGraph(graph_path);
			graph_density = Math.round((double) (edge_set.size() / 2) / (double) universe.length);
			Relation edge_relation = new Relation("E", 2, edge_set);
			relation_map.put("E", edge_relation);
			// Guard Relation
			line = br.readLine();
			if (!line.isEmpty()) {
				guard_rel_id = line.substring(0, 1);
			} else {
				guard_rel_id = null;
			}
			// Bound variables
			line = br.readLine();
			bound_variables = line.split(",");
			// Parameter c
			c_par = bound_variables.length;
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

	/**
	 * Returns the universe of the specified PACE-graph.
	 */
	private int[] getExternalUniverse(String graph_path) {
		uni_min = -1;
		uni_max = -1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(graph_path)));
			// Skip header
			String line = br.readLine();
			line = "";
			// Find min and max element
			while ((line = br.readLine()) != null) {
				String[] line_split = line.split(" ");
				for (String elem : line_split) {
					int elem_int = Integer.parseInt(elem);
					if (uni_min == -1 || uni_min > elem_int) {
						uni_min = elem_int;
					}
					if (uni_max == -1 || uni_max < elem_int) {
						uni_max = elem_int;
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int universe_size = uni_max - uni_min + 1;
		if (universe_size > 0) {
			int[] universe = new int[universe_size];
			// PACE nodes go from min to max
			for (int i = 0; i < universe_size; i++) {
				universe[i] = uni_min + i;
			}
			return universe;
		} else {
			return null;
		}
	}

	/**
	 * Returns the set of edges of the specified PACE-graph. For every entry (x,y)
	 * this adds the edges (x,y) and (y,x) because the graph is supposed to be
	 * undirected.
	 */
	private HashSet<Tuple> parseExternalGraph(String graph_path) {
		HashSet<Tuple> edge_set = new HashSet<Tuple>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(graph_path)));
			// First line is the descriptor
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] split_line = line.split(" ");
				int[] tuple_nodes = new int[2];
				tuple_nodes[0] = Integer.parseInt(split_line[0]);
				tuple_nodes[1] = Integer.parseInt(split_line[1]);
				edge_set.add(new Tuple(tuple_nodes));
				int[] tuple_nodes_reversed = new int[2];
				tuple_nodes_reversed[0] = tuple_nodes[1];
				tuple_nodes_reversed[1] = tuple_nodes[0];
				edge_set.add(new Tuple(tuple_nodes_reversed));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return edge_set;
	}

	/**
	 * Parses a file specified by the path parameter into an formula instance.
	 */
	private void parseInternalFormula(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			// Name of the formula
			line = br.readLine();
			formula_name = line;
			graph_name = "internal";
			// Relations are listed in the next lines. The last relation ends on ';'
			ArrayList<String> relation_lines = new ArrayList<String>();
			line = br.readLine();
			while (true) {
				// If this is the last line, ending on ';'
				if (line.charAt(line.length() - 1) == ';') {
					// Delete ';' and add line, then leave
					relation_lines.add(line.substring(0, line.length() - 1));
					break;
				} else {
					relation_lines.add(line);
				}
				line = br.readLine();
			}
			// Collect Relation Objects into map (except Solution S)
			relation_map = new HashMap<String, Relation>();
			// Find min and max universe elements on the way
			uni_min = -1;
			uni_max = -1;
			for (String s : relation_lines) {
				// Process statements like E2 = {(1|2),(3|4)}
				String identifier = s.substring(0, 1);
				int arity = Integer.parseInt(s.substring(1, 2));
				int left_bracket = s.indexOf("{") + 1;
				int right_bracket = s.indexOf("}");
				// Check if Relation is empty
				if (!(right_bracket > left_bracket)) {
					// Add empty Relation
					relation_map.put(identifier, new Relation(identifier, arity, new HashSet<Tuple>()));
					continue;
				}
				// Else, collect (1|2),(3|4)
				String content = s.substring(left_bracket, right_bracket);
				String[] content_split = content.split(",");
				// Matrix of elements: [[1,2],[3,4]]
				int[][] elements = new int[content_split.length][arity];
				// Init set of tuples which will make up the relation
				HashSet<Tuple> relation_set = new HashSet<Tuple>();
				// Loop through Tuples
				for (int i = 0; i < content_split.length; i++) {
					String[] element_split = content_split[i].split("\\|");
					// Loop through elements per Tuple
					for (int j = 0; j < arity; j++) {
						// Add element to Tuple
						int elem = Integer.parseInt(element_split[j].replaceAll("[()]", ""));
						elements[i][j] = elem;
						// Check for min and max
						if(elem < uni_min || uni_min == -1) {
							uni_min = elem;
						}
						if(elem > uni_max || uni_max == -1) {
							uni_max = elem;
						}
					}
					// Convert int[] to Tuple and add to relation set
					relation_set.add(new Tuple(elements[i]));
				}
				// Add Relation to relation map, mapping from identifier (E) to the Relation Object
				relation_map.put(identifier, new Relation(identifier, arity, relation_set));
			}
			// Construct universe from min and max
			universe = new int[uni_max-uni_min];
			for(int i = 0; i < (uni_max-uni_min); i++) {
				universe[i] = uni_min + i;
			}
			// Guard Relation
			line = br.readLine();
			if (!line.isEmpty()) {
				guard_rel_id = line.substring(0, 1);
			} else {
				guard_rel_id = null;
			}
			// Bound variables
			line = br.readLine();
			bound_variables = line.split(",");
			// Parameter c
			c_par = bound_variables.length;
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
			// TODO Add flag which lets Relations be reflexive
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Hyphergraph which results when this formula is reduced to
	 * hitting-set.
	 * 
	 * @param reduction_timeout
	 * @throws TimeoutException
	 */
	public Hypergraph reduceToHsWoGuard(boolean mute, long reduction_timeout, boolean timeout_active)
			throws TimeoutException {
		// Edges of the hypergraph are found while checking clauses
		ArrayList<Tuple> hyp_edges = new ArrayList<Tuple>();
		// The solution S is always empty in this reduction
		ArrayList<Integer> empty_sol = new ArrayList<Integer>();
		int[] curr_assignment = new int[c_par];
		// generate first assignment
		for (int i = 0; i < c_par; i++) {
			curr_assignment[i] = universe[0];
		}
		double progress = 0;
		for (long i = 0; i < nr_of_assignments; i++) {
			// Check timeout
			if (i % 10000 == 0 && timeout_active) {
				if (System.currentTimeMillis() > reduction_timeout) {
					throw new TimeoutException();
				}
			}
			// prints
			if (!mute && i % 500000 == 0) {
				// print string
				String curr_assignment_str = "";
				curr_assignment_str = "";
				for (int j = 0; j < c_par; j++) {
					curr_assignment_str += curr_assignment[j] + " ";
				}
				progress = (double) i / (nr_of_assignments - 1);
				progress *= 100;
				String tmp = String.format("%.2f", progress);
				System.out
						.print("  Testing assignments , Progress " + tmp + "%, current: " + curr_assignment_str + "\r");
			}
			// Check current assignment on all clauses
			for (int j = 0; j < clauses.size(); j++) {
				String[] curr_clause = clauses.get(j);
				// If clause does not hold, add edge containing current assignment (only
				// elements bound to S)
				if (!checkClause(curr_clause, curr_assignment, empty_sol, true)) {
					// Find elements that are bound to S in this clause
					Tuple edge_to_add = findCandidates(curr_clause, curr_assignment);
					if (!hyp_edges.contains(edge_to_add)) {
						hyp_edges.add(edge_to_add);
					}
				}
			}
			curr_assignment = nextAssignment(curr_assignment);
		}
		if (!mute)
			System.out.println();
		// Nodes of the Hypergraph are derived from the universe of the formula
		Hypergraph hyp = new Hypergraph(universe, hyp_edges);
		// Copy name from this formula
		hyp.hypergraph_name = this.graph_name;
		hyp.formula_name = this.formula_name;
		return hyp;
	}

	public Hypergraph reduceToHsWithGuard(boolean mute, long reduction_timeout, boolean timeout_active)
			throws TimeoutException {
		// Edges of the hypergraph are found while checking clauses
		ArrayList<Tuple> hyp_edges = new ArrayList<Tuple>();
		// The solution S is always empty in this reduction
		ArrayList<Integer> empty_sol = new ArrayList<Integer>();
		int cntr = 0;
		Relation guard_relation = relation_map.get(guard_rel_id);
		for (Tuple assignment : guard_relation.elements) {
			int[] assignment_arr = assignment.elements;
			// Check timeout
			if (cntr % 10000 == 0 && timeout_active) {
				if (System.currentTimeMillis() > reduction_timeout) {
					throw new TimeoutException();
				}
			}
			// Check current assignment on all clauses
			for (int j = 0; j < clauses.size(); j++) {
				String[] curr_clause = clauses.get(j);
				// If clause does not hold, add edge containing current assignment (only
				// elements bound to S)
				if (!checkClause(curr_clause, assignment_arr, empty_sol, true)) {
					// Find elements that are bound to S in this clause
					Tuple edge_to_add = findCandidates(curr_clause, assignment_arr);
					if (!hyp_edges.contains(edge_to_add)) {
						hyp_edges.add(edge_to_add);
					}
				} else {
					// This should not occur, no clause should be fulfilled
					System.out.println("This is weird.");
				}
			}
			cntr++;
		}
		// Nodes of the Hypergraph are derived from the universe of the formula
		Hypergraph hyp = new Hypergraph(universe, hyp_edges);
		// Copy name from this formula
		hyp.hypergraph_name = this.graph_name;
		hyp.formula_name = this.formula_name;
		return hyp;
	}

	/**
	 * Returns the assignment which comes after the given one.
	 */
	private int[] nextAssignment(int[] curr_assignment) {
		for (int i = c_par - 1; i >= 0; i--) {
			// Increment the rightmost digit if possible
			if (curr_assignment[i] < uni_max) {
				curr_assignment[i]++;
				return curr_assignment;
			}
			// Else reset digit and go to next digit
			else {
				curr_assignment[i] = uni_min;
			}
		}
		return null;
	}

	/**
	 * Returns a Tuple of elements from the given assignment which are bound to S in
	 * the given clause, meaning they are candidates to branch over during solving
	 * or reducing this formula.
	 */
	private Tuple findCandidates(String[] curr_clause, int[] assignment) {
		HashSet<Integer> confirmed_candidates = new HashSet<Integer>();
		// All edges have length c_par, but can contain null
		int[] candidates = new int[c_par];
		for (int i = 0; i < candidates.length; i++) {
			candidates[i] = -1;
		}
		int i = 0;
		for (String s : curr_clause) {
			// All candidates shall be unique
			if (s.charAt(0) == 'S' && !confirmed_candidates.contains(assign(s, assignment)[0])) {
				candidates[i] = assign(s, assignment)[0];
				confirmed_candidates.add(candidates[i]);
				i++;
			}
		}
		return new Tuple(candidates);
	}

	/**
	 * Returns the result of assigning the given atom with the given assignment.
	 * Given ~R(y,x) and the assignment x=2,y=1 this returns [2,1].
	 */
	private int[] assign(String atom, int[] assignment) {
		// s = ~R(y,x)
		// assignment = [1,2] (x=1,y=2)
		// Extract variables
		int negation_offset = (atom.charAt(0) == '~') ? 1 : 0;
		String content = atom.substring(negation_offset + 1);
		// content = "(y,x)"
		content = content.replaceAll("[()]", "");
		// content = "y,x"
		String[] variables = content.split(",");
		// variables = ["y","x"]
		int[] assi_elements = new int[variables.length];
		// Init empty
		for (int i = 0; i < assi_elements.length; i++) {
			assi_elements[i] = -1;
		}
		// assi_elements = [-1,-1]
		for (int i = 0; i < variables.length; i++) {
			for (int j = 0; j < bound_variables.length; j++) {
				// bound_vars = ["x","y","z"]
				// y: i = 0, j = 1
				if (variables[i].equals(bound_variables[j])) {
					assi_elements[i] = assignment[j];
					// assi = [2,0]
					break;
				}
			}
		}
		return assi_elements;
	}

	/**
	 * Returns weather this formula has a solution of size k_par. Initially, the
	 * solution should be empty, the last_index should be 0 and the last assignment
	 * an array containing c_par times the first element of the universe.
	 * 
	 * @param st_timeout
	 * @throws TimeoutException
	 */
	public boolean searchTree(int k_par, ArrayList<Integer> sol, boolean mute, int[] last_assignment, long last_index,
			long st_timeout, boolean timeout_active) throws TimeoutException {
		// TODO return solution

		// Return if |S| > k
		if (sol.size() > k_par) {
			return false;
		}
		// Copy last assignment
		int[] curr_assignment = new int[c_par];
		for (int i = 0; i < c_par; i++) {
			curr_assignment[i] = last_assignment[i];
		}
		// Check all clauses considering S
		for (long i = last_index; i < nr_of_assignments; i++) {
			// Check timeout
			if (i % 1000 == 0) {
				if (System.currentTimeMillis() > st_timeout && timeout_active) {
					throw new TimeoutException();
				}
			}
			for (int j = 0; j < clauses.size(); j++) {
				// If a clause is false, branch over relevant candidates of current assignment
				if (!checkClause(clauses.get(j), curr_assignment, sol, false)) {
					// Only branch if there is still space in sol
					if (sol.size() == k_par) {
						return false;
					}
					HashSet<Integer> f = new HashSet<Integer>();
					// Find variables that are bound to S in this clause
					Tuple candidates = findCandidates(clauses.get(j), curr_assignment);
					for (int c : candidates.elements) {
						if (!sol.contains(c)) {
							f.add(c);
						}
					}
					// If there is anything to branch over
					if (!f.isEmpty()) {
						boolean flag = false;
						// Construct branches with each branch adding one literal to S respectively
						for (int y : f) {
							// Try adding y to solution
							sol.add(y);
							// print
							if (!mute) {
								String prnt = "  ";
								prnt += "S: ";
								for (int s : sol)
									prnt += s + " ";
								prnt += "                                 \r";
								System.out.print(prnt);
							}
							// if one branch is successful we win, else go back through recursion.
							flag = flag || searchTree(k_par, sol, mute, curr_assignment, i, st_timeout, timeout_active);
							if (flag) {
								if (!mute) {
									System.out.println(sol);
								}
								return true;
							} else {
								// Branch failed, remove y again to clean up sol for next branch.
								sol.remove((Object) y);
							}
						}
						return flag;
					}
					// No possible branches, return false
					else {
						return false;
					}
				}
			}
			curr_assignment = nextAssignment(curr_assignment);
		}
		return true;
	}

	/**
	 * Returns weather the specified clause holds under the given assignment with
	 * the Relation S being sol. When ignore_S is true, the test on S is skipped.
	 * This is useful for Reduction, because there S stays empty and is therefore
	 * unnecessary to check.
	 */
	private boolean checkClause(String[] clause, int[] assignment, ArrayList<Integer> sol, boolean ignore_S) {
		// Evaluate literals one at a time
		for (String literal : clause) {
			int negation_offset = (literal.charAt(0) == '~') ? 1 : 0;
			String id = (negation_offset == 1) ? literal.substring(1, 2) : literal.substring(0, 1);
			int[] assi_elements = assign(literal, assignment);
			// Handle S(x)
			if (id.equals("S")) {
				// Ignore S during reduction, because it stays empty
				if (ignore_S) {
					continue;
				}
				// check if S contains the element
				if (sol.contains(assi_elements[0])) {
					return true;
				}
				// Else: look in all other available relations
			} else if (id.equals("=")) {
				// Check if the elements are equal
				int reference = assi_elements[0];
				boolean elems_are_equal = true;
				for (int other_element : assi_elements) {
					if (reference != other_element) {
						elems_are_equal = false;
					}
				}
				// Return true is the elements are equal
				if (elems_are_equal) {
					return true;
				}
				// Else: Do not return, because this is a dijunction and can still become true
				// for a different literal
			} else {
				Relation r = relation_map.get(id);
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
					System.out.println("Unknown relation symbol: " + id);
				}
			}
		}
		return false;
	}

	/**
	 * Returns a String which represents this Formula for debugging.
	 */
	public String toOutputString() {
		String res = "";
		res += formula_name + ":\n";
		res += "U = {";
		for (int i = 0; i < universe.length; i++) {
			if (i + 1 != universe.length)
				res += universe[i] + ",";
			else
				res += universe[i];
		}
		res += "}\n";
		res += "Relations:\n";
		for (Entry<String, Relation> r : relation_map.entrySet()) {
			res += r.getValue().toOutputString() + "\n";
		}
		res += "Bound varibales: (";
		for (int i = 0; i < bound_variables.length; i++) {
			if (i + 1 != bound_variables.length)
				res += bound_variables[i] + ",";
			else
				res += bound_variables[i];
		}
		res += ")\n";
		res += "Clauses:\n";
		for (String[] c : clauses) {
			for (String l : c) {
				res += l + " ";
			}
			res += "\n";
		}
		return res;
	}

	/**
	 * Saves this Formula to a file.
	 */
	public void saveToFile() {
		try {
			File out_file = new File("saves" + File.separator + this.formula_name + "-save.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
			// Name
			bw.write(formula_name + "\n");
			// Universe
			for (int i = 0; i < universe.length; i++) {
				if (i + 1 != universe.length)
					bw.write(universe[i] + ",");
				else
					bw.write(universe[i] + "\n");
			}
			// Relations
			Iterator<Entry<String, Relation>> it = relation_map.entrySet().iterator();
			while (it.hasNext()) {
				bw.write(it.next().getValue().toOutputString());
				if (it.hasNext())
					bw.write(";");
			}
			bw.write("\n");
			// Parameter k
			bw.write("\n");
			// Bound vars
			for (int i = 0; i < bound_variables.length; i++) {
				if (i < bound_variables.length - 1)
					bw.write(bound_variables[i] + ",");
				else
					bw.write(bound_variables[i]);
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

	public String getIdentifier() {
		return this.graph_name + "," + this.formula_name;
	}

}
