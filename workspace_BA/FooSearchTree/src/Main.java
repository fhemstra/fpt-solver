import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

public class Main {

	static String name;
	static String[] universe;
	static ArrayList<Relation> rels;
	static Relation solution;
	static int k_par;
	static String[] bound_vars;

	public static void main(String[] args) {
		parseFormula("vertex-cover.txt");
		printFormula();
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
			rels = new ArrayList<Relation>();
			for (String s : relations) {
				String identifier = s.substring(0, 1);
				int arity = Integer.parseInt(s.substring(1, 2));
				int from = s.indexOf("{") + 1;
				int to = s.indexOf("}");
				String content = s.substring(from, to);
				String[] content_split = content.split(",");
				String[][] elements = new String[content_split.length][arity];
				HashSet<String[]> hs = new HashSet<String[]>();
				for (int i = 0; i < content_split.length; i++) {
					String[] element_split = content_split[i].split("\\|");
					for (int j = 0; j < arity; j++) {
						elements[i][j] = element_split[j].replaceAll("[()]", "");
					}
					hs.add(elements[i]);
				}
				rels.add(new Relation(identifier, arity, hs));
			}
			// Relation S which will contain the solution
			line = br.readLine();
			String s_identifier = line.substring(0, 1);
			int s_arity = Integer.parseInt(line.substring(1, 2));
			HashSet<String[]> s_hs = new HashSet<String[]>();
			solution = new Relation(s_identifier, s_arity, s_hs);
			// Parameter k
			line = br.readLine();
			k_par = Integer.parseInt(line);
			// Bound variables
			line = br.readLine();
			bound_vars = line.split(",");
			// Formula
			line = br.readLine();
			// TODO extract formula
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		System.out.print("Relations:");
		for (Relation r : rels) {
			r.printThis();
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
		// TODO formula
	}

}
