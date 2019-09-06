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
	String form_name;
	String graph_name;
	int graph_density;
	int[] universe;
	HashMap<String, Relation> rels;
	String[] bound_vars;
	int c_par;
	ArrayList<String[]> clauses;
	long nr_of_assignments;

	public Formula() {
		// Do everything yourself.
	}

	public Formula(String path) {
		parseInternalFormula(path);
		nr_of_assignments = (long) Math.pow(universe.length, c_par);
	}

	public Formula(String form_path, String graph_path) {
		parseExternalFormula(form_path, graph_path);
		nr_of_assignments = (long) Math.pow(universe.length, c_par);
	}

	private void parseExternalFormula(String form_path, String graph_path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(form_path));
			String line = "";

			// Name of the formula
			line = br.readLine();
			form_name = line;
			graph_name = new File(graph_path).getName();
			// Universe from .gr file -> skip a line
			line = br.readLine();
			universe = getPaceUniverse(graph_path);
			
			// Skip relations, only E is important
			line = br.readLine();
			while(true) {
				if(line.contains(";")) {
					break;
				}
				line = br.readLine();
			}
			rels = new HashMap<String, Relation>();
			HashSet<Tuple> edge_set = parsePaceGraph(graph_path);
			graph_density = (int) Math.round(((double)(edge_set.size()/2)/(double)universe.length));
			Relation edge_relation = new Relation("E", 2, edge_set);
			rels.put("E", edge_relation);
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

	private int[] getPaceUniverse(String graph_path) {
		int arr_lenght = -1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(graph_path)));
			// First line is the descriptor
			String first_line = br.readLine();
			String[] first_split_line = first_line.split(" ");
			arr_lenght = Integer.parseInt(first_split_line[2]);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (arr_lenght > -1) {
			int[] universe = new int[arr_lenght];
			// PACE nodes go from 1 to arr_lenght
			for (int i = 0; i < arr_lenght; i++) {
				universe[i] = i+1;
			}
			return universe;
		} else {
			return null;
		}
	}

	private HashSet<Tuple> parsePaceGraph(String graph_path) {
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
			form_name = line;
			graph_name = "internal";
			// Universe
			line = br.readLine();
			int universe_size = Integer.parseInt(line);
			for (int i = 0; i < universe_size; i++) {
				universe[i] = i+1;
			}
			// Signature
			// All relations are listed in the next lines, where the last relation ends on ';'
			ArrayList<String> relations = new ArrayList<String>();
			line = br.readLine(); 
			while(true) {
				// if this is the last line, ending on ';'
				if(line.charAt(line.length()-1) == ';') {
					// Delete ';' and add line, then leave
					relations.add(line.substring(0, line.length()-1));
					break;
				} else {
					relations.add(line);					
				}
				line = br.readLine();
			}
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

	public Hypergraph reduceToHS(boolean mute) {
		// Edges of the hypergraph are found while checking clauses
		ArrayList<Tuple> hyp_edges = new ArrayList<Tuple>();
		// The solution S is always empty in this reduction
		ArrayList<Integer> empty_sol = new ArrayList<Integer>();
		int[] curr_assignment = new int[c_par];
		// generate first assignment 
		for(int i = 0; i < c_par; i++) {
			curr_assignment[i] = universe[0];
		}
		// print string
		String curr_assignment_str = "";
		double progress = 0;
		for (long i = 0; i < nr_of_assignments; i++) {
			// prints
//			if(!mute && i % 100 == 0) {
			if(!mute && i % 500000 == 0) {
				curr_assignment_str = "";
				for(int j = 0; j < c_par; j++) {
					curr_assignment_str += curr_assignment[j] + " ";
				}
				progress = (double)i/(nr_of_assignments-1);
				progress *= 100;
				String tmp = String.format("%.2f", progress);
				System.out.print("  Testing assignments , Progress " + tmp + "%, current: " + curr_assignment_str + "\r");
			}
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
		if(!mute) System.out.println();
		// Nodes of the Hypergraph are derived from the universe of the formula
		Hypergraph hyp = new Hypergraph(universe, hyp_edges);
		// Copy name from this formula
		hyp.name = graph_name;
		return hyp;
	}

	private int[] nextAssignment(int[] curr_assignment) {
		int mode = universe.length;
		for(int i = c_par-1; i >= 0; i--) {
			if(curr_assignment[i] < mode) {
				curr_assignment[i]++;
				return curr_assignment;
			} else {
				curr_assignment[i] = universe[0];
			}
		}
		return null;
	}

	private Tuple findCandidates(String[] curr_clause, int[] assignment) {
		// All edges have length c_par, but can contain null
		int[] candidates = new int[c_par];
		for (int i = 0; i < candidates.length; i++) {
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
	public boolean searchTree(int k_par, ArrayList<Integer> sol, boolean mute, int[] last_assignment, long last_index) {
		// TODO return solution
		
		// Return if |S| > k
		if (sol.size() > k_par) {
			return false;
		}
		// Copy last assignment
		int[] curr_assignment = new int[c_par];
		for(int i = 0; i < c_par; i++) {
			curr_assignment[i] = last_assignment[i];
		}
		// Check all clauses considering S
		for (long i = last_index; i < nr_of_assignments; i++) {
			for (int j = 0; j < clauses.size(); j++) {
				// If a clause is false, branch over relevant candidates of current assignment
				if (!checkClause(clauses.get(j), curr_assignment, sol, false)) {
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
							if(!mute) {
								String prnt = "  ";
								prnt += "S: ";
								for (int s : sol)
									prnt += s + " ";
								prnt += "                                 \r";
								System.out.print(prnt);
							}
							// if one branch is successful we win, else go back through recursion.
							flag = flag || searchTree(k_par, sol, mute, curr_assignment, i);
							if (flag) {
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
	 * Checks if the specified clause holds under the specified assignment with S
	 * being sol.
	 */
	private boolean checkClause(String[] clause, int[] assignment, ArrayList<Integer> sol, boolean ignore_S) {
		// Evaluate literals one at a time
		for (String l : clause) {
			int negation_offset = (l.charAt(0) == '~') ? 1 : 0;
			String id = (negation_offset == 1) ? l.substring(1, 2) : l.substring(0, 1);
			int[] assi_elements = assign(l, assignment);
			// Handle S(x)
			if (id.equals("S")) {
				// Ignore S during reduction, because it stays empty
				if(ignore_S) {
					continue;
				}
				// check if S contains the element
				if (sol.contains(assi_elements[0])) {
					return true;
				}
				// Else: look in all other available relations
			} else if(id.equals("=")) {
				// Check if the elements are equal
				int reference = assi_elements[0];
				for(int other_element : assi_elements) {
					if(reference != other_element) return false;
				}
				return true;
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
					System.out.println("Unknown relation symbol: " + id);
				}
			}
		}
		return false;
	}

	/**
	 * Prints info about this into sysout.
	 */
	public String toOutputString() {
		String res = "";
		res += form_name + ":\n";
		res += "U = {";
		for (int i = 0; i < universe.length; i++) {
			if (i + 1 != universe.length)
				res += universe[i] + ",";
			else
				res += universe[i];
		}
		res += "}\n";
		res += "Relations:\n";
		for (Entry<String, Relation> r : rels.entrySet()) {
			res += r.getValue().toOutputString() + "\n";
		}
		res += "Bound varibales: (";
		for (int i = 0; i < bound_vars.length; i++) {
			if (i + 1 != bound_vars.length)
				res += bound_vars[i] + ",";
			else
				res += bound_vars[i];
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
			File out_file = new File("saves" + File.separator + this.form_name + "-save.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
			// Name
			bw.write(form_name + "\n");
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
