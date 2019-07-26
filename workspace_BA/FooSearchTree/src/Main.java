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
			System.out.println("----------------");
			System.out.println("SearchTree:");
			System.out.println(form.searchTree(form.k_par, new ArrayList<Integer>()));
			System.out.println("----------------");
			System.out.println("Reduction to hypergraph: ");
			Hypergraph hyp = form.reduceToHS();
			System.out.println(hyp.toOutputString());
			System.out.println("~~~~~~~~~~~~~~~~");
			// Kernelization
			Hypergraph kernel = hyp.kernelize(hyp, form.k_par);
			System.out.println(kernel.toOutputString());
			System.out.println("++++++++++++++++");
			/*
			// Sunflower tests
			System.out.println("Sunflower:");
			Sunflower sun = hyp.findSunflower(hyp, form.k_par);
			System.out.println("<< Return:");
			if(sun != null) System.out.println(sun.toOutputString());
			else System.out.println("null");
			System.out.println("++++++++++++++++");
			*/
			// TODO I don't really need this anymore, this was meant to save hypergraphs
			// back when I planned to express them in Formula instances.
			// form.saveToFile();
			// TODO remove break
			break;
		}
		System.out.println();

		/*
		System.out.println("Test-hypergraph:");
		int[] nodes = { 0, 1, 2, 3 };
		HashMap<Integer, ArrayList<Tuple>> hm = new HashMap<Integer, ArrayList<Tuple>>();
		ArrayList<Tuple> edges = new ArrayList<Tuple>();
		// TODO all edges should have size d, so deletions and additions are easy
		int[] e0 = { 0 };
		int[] e1 = { 1, 2, 3 };
		int[] e2 = { 0, 2, 3 };
		Tuple t0 = new Tuple(e0);
		Tuple t1 = new Tuple(e1);
		Tuple t2 = new Tuple(e2);
		edges.add(t0);
		edges.add(t1);
		edges.add(t2);
		ArrayList<Tuple> edges_of_v0 = new ArrayList<Tuple>();
		edges_of_v0.add(t0);
		edges_of_v0.add(t2);
		hm.put(0, edges_of_v0);
		ArrayList<Tuple> edges_of_v1 = new ArrayList<Tuple>();
		edges_of_v1.add(t1);
		hm.put(1, edges_of_v1);
		ArrayList<Tuple> edges_of_v2 = new ArrayList<Tuple>();
		edges_of_v2.add(edges.get(1));
		edges_of_v2.add(edges.get(2));
		hm.put(2, edges_of_v2);
		ArrayList<Tuple> edges_of_v3 = new ArrayList<Tuple>();
		edges_of_v3.add(edges.get(1));
		edges_of_v3.add(edges.get(2));
		hm.put(3, edges_of_v3);
		Hypergraph hyp = new Hypergraph(nodes, edges, hm);
		System.out.println(hyp.toOutputString());
		*/
	}

}
