import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		// Collect and parse all files
		ArrayList<Formula> formulas = new ArrayList<Formula>();
		File folder = new File("instances");
		File[] listOfFiles = folder.listFiles();
		for (File f: listOfFiles) {
		  if (f.isFile()) {
		    formulas.add(new Formula(f.getAbsolutePath()));
		  }
		}
		
		// Solve each File
		for(Formula form : formulas) {
			form.printFormula();
			System.out.println("SearchTree: " + form.searchTree(form.k_par, new ArrayList<String>()));
			System.out.println("-------");
			System.out.println("Reduction: ");
			System.out.println("~~~~~~~~~~~~~~~~");
			form.saveToFile();
		}
		
		int[] nodes = {1,2,3};
		HashMap<Integer, String[]> hm = new HashMap<Integer, String[]>();
		ArrayList<String> edges = new ArrayList<String>();
		edges.add("e0");
		edges.add("e1");
		edges.add("e2");
		edges.add("e3");
		String[] s1 = {edges.get(0),edges.get(1)};
		hm.put(1, s1);
		String[] s2 = {edges.get(1),edges.get(2),edges.get(3)};
		hm.put(2, s2);
		String[] s3 = {edges.get(3)};
		hm.put(3, s3);
		Hypergraph hyp = new Hypergraph(nodes, edges, hm);
		System.out.println(hyp.toOutputString());
	}

}
