import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Formula {
	String name;
	String[] universe;
	HashMap<String, Relation> rels;
	int k_par;
	String[] bound_vars;
	int c_par;
	ArrayList<String[]> clauses;
	ArrayList<String[]> assignments = new ArrayList<String[]>();

	public Formula(String path) {
		parseFormula(path);
		// Generate all assignments
		generateAssignments();
		ArrayList<String> solution = new ArrayList<>();
	}

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
			rels = new HashMap<String, Relation>();
			for (String s : relations) {
				int negation_offset = (s.charAt(0) == '~') ? 1 : 0;
				String identifier = s.substring(negation_offset, negation_offset + 1);
				int arity = Integer.parseInt(s.substring(negation_offset + 1, negation_offset + 2));
				int from = s.indexOf("{") + 1;
				int to = s.indexOf("}");
				String content = s.substring(from, to);
				String[] content_split = content.split(",");
				String[][] elements = new String[content_split.length][arity];
				HashSet<Tuple> hs = new HashSet<Tuple>();
				for (int i = 0; i < content_split.length; i++) {
					String[] element_split = content_split[i].split("\\|");
					for (int j = 0; j < arity; j++) {
						elements[i][j] = element_split[j].replaceAll("[()]", "");
					}
					hs.add(new Tuple(elements[i]));
				}
				rels.put(identifier, new Relation(identifier, arity, hs));
			}
			// Relation S which will contain the solution
			line = br.readLine();
			// TODO delete S from file format
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean searchTree(int k_par, ArrayList<String> sol) {
		// TODO schauen, ob solution sich mit sol mitverändert oder nicht
		if (sol.size() > k_par) {
			return false;
		}
		// Recursively search for solution S of size k
		for (int i = 0; i < assignments.size(); i++) {
			for (int j = 0; j < clauses.size(); j++) {
				if (!checkClause(clauses.get(j), assignments.get(i), sol)) {
					HashSet<String> f = new HashSet<String>();
					for (String s : assignments.get(i)) {
						if (!sol.contains(s)) {
							f.add(s);
						}
					}
					if (!f.isEmpty()) {
						boolean flag = false;
						for (String y : f) {
							ArrayList<String> sol_with_y = (ArrayList<String>) sol.clone();
							sol_with_y.add(y);
							System.out.print("S: ");
							for (String s : sol_with_y)
								System.out.print(s + " ");
							System.out.println();
							flag = flag || searchTree(k_par, sol_with_y);
						}
						return flag;
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void generateAssignments() {
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

	public boolean checkClause(String[] clause, String[] assignment, ArrayList<String> sol) {
		// Evaluate literals one at a time
		for (String l : clause) {
			int negation_offset = (l.charAt(0) == '~') ? 1 : 0;
			String id = l.substring(negation_offset, negation_offset + 1);
			String content = l.substring(negation_offset + 1);
			content = content.replaceAll("[()]", "");
			// [y,x]
			String[] variables = content.split(",");
			String[] assi_elements = new String[variables.length];
			for (int i = 0; i < variables.length; i++) {
				for (int j = 0; j < universe.length; j++) {
					if (variables[i].equals(bound_vars[j])) {
						assi_elements[i] = assignment[j];
						break;
					}
				}
			}
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
			r.getValue().printThis();
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

}
