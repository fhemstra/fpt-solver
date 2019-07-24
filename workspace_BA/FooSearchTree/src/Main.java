import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		// Collect and parse all files
		ArrayList<Formula> formulas = new ArrayList<Formula>();
		File folder = new File("instances");
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.isFile()) {
				formulas.add(new Formula(f.getAbsolutePath()));
			}
		}

		// Solve each File
		for (Formula form : formulas) {
			form.printFormula();
			System.out.println("SearchTree: " + form.searchTree(form.k_par, new ArrayList<Integer>()));
			System.out.println("-------");
			System.out.println("Reduction: ");
			System.out.println("~~~~~~~~~~~~~~~~");
			Hypergraph tmp = form.reduceToHS();
			// TODO I don't really need this anymore, this was meant to save hypergraphs
			// back when I planned to express them in Formula instances.
			// form.saveToFile();
		}
		System.out.println();

		int[] nodes = { 0, 1, 2, 3 };
		HashMap<Integer, Integer[][]> hm = new HashMap<Integer, Integer[][]>();
		ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
		// TODO all edges should have size d, so deletions and additions are easy
		Integer[] edge0 = { 0 };
		Integer[] edge1 = { 1, 2, 3 };
		Integer[] edge2 = { 0, 2, 3 };
		edges.add(edge0);
		edges.add(edge1);
		edges.add(edge2);
		Integer[][] edges_of_v0 = { edges.get(0), edges.get(2) };
		hm.put(0, edges_of_v0);
		Integer[][] edges_of_v1 = { edges.get(1) };
		hm.put(1, edges_of_v1);
		Integer[][] edges_of_v2 = { edges.get(1), edges.get(2) };
		hm.put(2, edges_of_v2);
		Integer[][] edges_of_v3 = { edges.get(1), edges.get(2) };
		hm.put(3, edges_of_v3);
		Hypergraph hyp = new Hypergraph(nodes, edges, hm);
		System.out.println(hyp.toOutputString());
	}

}
