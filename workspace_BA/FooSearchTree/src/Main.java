import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Main {

	static String name;
	static String[] universe;
	static HashMap<String, Relation> rels;
	static Relation solution;
	static int k_par;
	static String[] bound_vars;
	static ArrayList<String[]> clauses;

	public static void main(String[] args) {
		//parseFormula("vertex-cover.txt");
		parseFormula("test-thingie.txt");
		printFormula();

		System.out.println("Test assignment:");
		HashMap<String, String> assignment = new HashMap<>();
		assignment.put("x", "v1");
		assignment.put("y", "v2");
		for (String[] s : clauses) {
			System.out.println(checkClause(s, assignment));
		}
	}

	private static void parseFormula(String path) {
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
			String s_identifier = line.substring(0, 1);
			int s_arity = Integer.parseInt(line.substring(1, 2));
			HashSet<Tuple> s_hs = new HashSet<Tuple>();
			solution = new Relation(s_identifier, s_arity, s_hs);
			rels.put(s_identifier, solution);
			// Parameter k
			line = br.readLine();
			k_par = Integer.parseInt(line);
			// Bound variables
			line = br.readLine();
			bound_vars = line.split(",");
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

	private static boolean checkClause(String[] clause, HashMap<String, String> assignment) {
		// Evaluate literals one at a time
		for (String l : clause) {
			int negation_offset = (l.charAt(0) == '~') ? 1 : 0;
			String id = l.substring(negation_offset, negation_offset + 1);
			Relation r = rels.get(id);
			if (r != null) {
				String content = l.substring(negation_offset+1);
				content = content.replaceAll("[()]", "");
				String[] variables = content.split(",");
				String[] tmp = new String[variables.length];
				for (int i = 0; i < variables.length; i++) {
					tmp[i] = assignment.get(variables[i]);
				}
				// Test if relation r contains the tuple t or not 
				Tuple t = new Tuple(tmp);
				if (r.elements.contains(t) && negation_offset == 0) {
					// If one literal is correct, we can stop.
					return true;
				}
				// negation case
				else if (!r.elements.contains(t) && negation_offset == 1) {
					return true;
				}
				// Assignment does not hold
				else {
					return false;
				}
			} else {
				System.out.println("Unknown relation symbol.");
			}
		}
		return false;
	}

	public static void printFormula() {
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
		System.out.print("Solution: ");
		solution.printThis();
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
