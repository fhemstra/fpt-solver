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
	String[] universe;
	HashMap<String, Relation> rels;
	int k_par;
	String[] bound_vars;
	int c_par;
	ArrayList<String[]> clauses;
	ArrayList<String[]> assignments = new ArrayList<String[]>();

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
			universe = line.split(",");
			// Signature
			line = br.readLine();
			String[] relations = line.split(";");
			// Solution S is not contained in rels
			rels = new HashMap<String, Relation>();
			for (String s : relations) {
				int negation_offset = (s.charAt(0) == '~') ? 1 : 0;
				String identifier = s.substring(negation_offset, negation_offset + 1);
				String arity = s.substring(negation_offset + 1, negation_offset + 2);
				int from = s.indexOf("{") + 1;
				int to = s.indexOf("}");
				String content = s.substring(from, to);
				String[] content_split = content.split(",");
				ArrayList<ArrayList<String>> elements = new ArrayList<ArrayList<String>>();
				HashSet<Tuple> hs = new HashSet<Tuple>();
				for (int i = 0; i < content_split.length; i++) {
					String[] element_split = content_split[i].split("\\|");
					elements.add(new ArrayList<String>());
					for (int j = 0; j < element_split.length; j++) {
						elements.get(i).add(element_split[j].replaceAll("[()]", ""));
					}
					hs.add(new Tuple(elements.get(i)));
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
				String[] clause = line.split(" ");
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
	 * Recursively searches a solution sol of size k_par for the given formula.
	 * 
	 * @return True if a solution of size k is found, else false.
	 */
	public boolean searchTree(int k_par, ArrayList<String> sol) {
		// Return if |S| > k
		if (sol.size() > k_par) {
			return false;
		}
		// Check all clauses considering S
		for (int i = 0; i < assignments.size(); i++) {
			for (int j = 0; j < clauses.size(); j++) {
				// If a clause is false, branch over current assignment
				if (!checkClause(clauses.get(j), assignments.get(i), sol)) {
					HashSet<String> f = new HashSet<String>();
					for (String a : assignments.get(i)) {
						// TODO check if the literal a even matters to the current clause
						if (!sol.contains(a)) {
							f.add(a);
						}
					}
					// If there is anything to branch over
					if (!f.isEmpty()) {
						boolean flag = false;
						// Construct branches with each branch adding one literal to S respectively
						for (String y : f) {
							ArrayList<String> sol_with_y = (ArrayList<String>) sol.clone();
							sol_with_y.add(y);
							// - print
							System.out.print("S: ");
							for (String s : sol_with_y)
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
			String[] s_arr = new String[c_par];
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
	private boolean checkClause(String[] clause, String[] assignment, ArrayList<String> sol) {
		// Evaluate literals one at a time
		for (String l : clause) {
			int negation_offset = (l.charAt(0) == '~') ? 1 : 0;
			String id = l.substring(negation_offset, negation_offset + 1);
			String content = l.substring(negation_offset + 1);
			content = content.replaceAll("[()]", "");
			// [y,x]
			String[] variables = content.split(",");
			ArrayList<String> assi_elements = new ArrayList<String>();
			for (int i = 0; i < variables.length; i++) {
				for (int j = 0; j < universe.length; j++) {
					if (variables[i].equals(bound_vars[j])) {
						assi_elements.add(assignment[j]);
						break;
					}
				}
			}
			// Handle S(x)
			if (id.equals("S")) {
				// check if S contains the element
				if (sol.contains(assi_elements.get(0))) {
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

	public Formula hsReduction() {
		Formula hs_form = new Formula();
		hs_form.name = "hitting-set-of-" + this.name;
		hs_form.universe = this.universe;
		// TODO Construct Edge Relation
		// TODO implement reduction
		hs_form.saveToFile();
		return hs_form;
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
			for(int i = 0; i < bound_vars.length; i++) {
				if(i < bound_vars.length - 1) 
					bw.write(bound_vars[i] + ",");
				else bw.write(bound_vars[i]);
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
