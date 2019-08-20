import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	static boolean mute = true;
	
	public static void main(String[] args) {
		// Test PACE parser
//		ArrayList<Hypergraph> graphs = new ArrayList<Hypergraph>();
////		File pace_folder = new File("../pace"); // Use this for execution in windows cmd
//		File pace_folder = new File("pace"); // Use this inside of eclipse
//		File[] listOfPaceFiles = pace_folder.listFiles();
//		for (int i = 0; i < listOfPaceFiles.length; i++) {
//			// TODO Set how many graphs should be loaded.
////			if (i > 20)
////				break;
//			File f = listOfPaceFiles[i];
//			if (f.isFile()) {
//				System.out.print(f.getName() + "\r");
//				if(i == listOfPaceFiles.length-1) System.out.println("\n");
//				Hypergraph graph_to_add = new Hypergraph(f.getAbsolutePath());
//				// System.out.println(graph_to_add.toOutputString());
//				graphs.add(graph_to_add);
//			}
//		}
//		System.out.println("Loaded PACE successfully.\n");
//		System.out.flush();
//		for (int i = 0; i < graphs.size(); i++) {
//			Hypergraph current_graph = graphs.get(i);
//			int edges_before = current_graph.edges.size();
//			int nodes_before = current_graph.nodes.length;
//			int chosen_k = edges_before/10;
//			System.out.println("--- GRAPH \"" + listOfPaceFiles[i] + "\", k = " + chosen_k + ", d = " + current_graph.d_par + " ---");
//			System.out.println("edges:         " + edges_before);
//			System.out.println("nodes:         " + nodes_before);
//			long start_time = System.currentTimeMillis();
//			current_graph.kernelize(current_graph, chosen_k, mute);
//			long stop_time = System.currentTimeMillis();
//			int edges_removed = edges_before - current_graph.edges.size();
//			int nodes_removed = nodes_before - current_graph.nodes.length;
//			System.out.println("edges removed: " + edges_removed);
//			System.out.println("nodes removed: " + nodes_removed);
//			System.out.println("kernel edges:  " + current_graph.edges.size());
//			System.out.println("kernel nodes:  " + current_graph.nodes.length);
//			long sf_lemma_boundary = factorial(current_graph.d_par) * (long) Math.pow(chosen_k, current_graph.d_par);
//			System.out.println("Lemma d!*k^d:  " + sf_lemma_boundary);
//			System.out.println("Time elapsed:  " + (stop_time-start_time)/1000 + " seconds");
//			System.out.println();
//		}

		// Collect and parse all formulas
//		ArrayList<Formula> formulas = new ArrayList<Formula>();
////		File folder = new File("../instances"); // Use this for execution in windows cmd
//		File folder = new File("instances"); // Use this inside eclipse
//		File[] listOfFiles = folder.listFiles();
//		for (File f : listOfFiles) {
//			if (f.isFile()) {
//				formulas.add(new Formula(f.getAbsolutePath()));
//			}
//		}

		// Solve each File
//		for (Formula form : formulas) {
//
//			System.out.println("--- FORMULA ---");
//			form.printFormula();
//			System.out.println("\n--- SEARCH TREE ---");
//			System.out.println(form.searchTree(form.k_par, new ArrayList<Integer>(), mute));
//			System.out.println("\n--- REDUCTION ---");
//			Hypergraph hyp = form.reduceToHS();
//			System.out.println(hyp.toOutputString());
//			System.out.println("\n--- KERNELIZATION ---"); // Kernelization Hypergraph
//			hyp.kernelize(hyp, form.k_par, mute);
//			System.out.println("<< Main\nKernel:");
//			System.out.println(hyp.toOutputString());
//			System.out.println("--- END FORMULA ---\n");
			
//			// Hypergraph doc example
//			int[] ex_nodes = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
//			ArrayList<Tuple> ex_edges = new	ArrayList<Tuple>();
//			ex_edges.add(new Tuple(new int[]{1,4}));
//			ex_edges.add(new Tuple(new int[]{2,4,5}));
//			ex_edges.add(new Tuple(new int[]{3,4}));
//			ex_edges.add(new Tuple(new int[]{4}));
//			ex_edges.add(new Tuple(new int[]{4,6}));
//			ex_edges.add(new Tuple(new int[]{6,8,9}));
//			ex_edges.add(new Tuple(new int[]{7,8,9}));
//			ex_edges.add(new Tuple(new int[]{8,9,10,11}));
//			ex_edges.add(new Tuple(new int[]{8,9,13}));
//			ex_edges.add(new Tuple(new int[]{11,12,14,15}));
//			ex_edges.add(new Tuple(new int[]{13,15}));
//			Hypergraph doc_ex = new Hypergraph(ex_nodes, ex_edges);
//			System.out.println("doc_ex:\n" + doc_ex.toOutputString() + "\n"); // k = 3
//			doc_ex.kernelize(doc_ex, 3);
//			System.out.println("+++\nex_kernel:\n" + doc_ex.toOutputString());
			
//			// College block hypergraph example (non-optimal kernel)
//			int[] col_nodes = {1,2,3,4,5,6,7};
//			ArrayList<Tuple> col_edges = new ArrayList<Tuple>();
//			col_edges.add(new Tuple(new	int[]{1,2,4,6}));
//			col_edges.add(new Tuple(new int[]{2,3}));
//			col_edges.add(new Tuple(new int[]{4,5}));
//			col_edges.add(new Tuple(new int[]{6,7}));
//			Hypergraph col_ex = new Hypergraph(col_nodes, col_edges);
//			System.out.println("col_ex:\n" + col_ex.toOutputString() + "\n"); // k = 2
//			col_ex.kernelize(col_ex, 2);
//			System.out.println("+++\ncol_ex_kernel:\n" + col_ex.toOutputString());
			
			// TODO I don't really need this anymore, this was meant to save hypergraphs
			// back when I planned to express them in Formula instances.
			// form.saveToFile();
			// TODO remove break
//			break;
//		}
//		System.out.println();
		
		// Test formulas from PACE files
		Formula pace_form = new Formula("C:\\Users\\falko\\Documents\\Eigenes\\Uni\\6_Semester\\Bachelorarbeit\\Bachelorarbeit_Code\\workspace_BA\\FooSearchTree\\instances\\5_vc_doc_example.txt", "C:\\Users\\falko\\Documents\\Eigenes\\Uni\\6_Semester\\Bachelorarbeit\\Bachelorarbeit_Code\\workspace_BA\\FooSearchTree\\pace\\vc-exact_005.gr");
		boolean st_res = pace_form.searchTree(10, new ArrayList<Integer>(), mute);
		System.out.print("                                                                     \r");
		System.out.println("SearchTree result: " + st_res);
		Hypergraph pace_reduced_graph = pace_form.reduceToHS();
		// Kernelize
		int edges_before = pace_reduced_graph.edges.size();
		int nodes_before = pace_reduced_graph.nodes.length;
		int chosen_k = edges_before/100;
		System.out.println("--- GRAPH \"" + "005" + "\", k = " + chosen_k + ", d = " + pace_reduced_graph.d_par + " ---");
		System.out.println("edges:         " + edges_before);
		System.out.println("nodes:         " + nodes_before);
		System.out.println("Kernelization");
		long start_time = System.currentTimeMillis();
		pace_reduced_graph.kernelize(pace_reduced_graph, chosen_k, mute);
		long stop_time = System.currentTimeMillis();
		int edges_removed = edges_before - pace_reduced_graph.edges.size();
		int nodes_removed = nodes_before - pace_reduced_graph.nodes.length;
		System.out.println("edges removed: " + edges_removed);
		System.out.println("nodes removed: " + nodes_removed);
		System.out.println("kernel edges:  " + pace_reduced_graph.edges.size());
		System.out.println("kernel nodes:  " + pace_reduced_graph.nodes.length);
		long sf_lemma_boundary = factorial(pace_reduced_graph.d_par) * (long) Math.pow(chosen_k, pace_reduced_graph.d_par);
		System.out.println("Lemma d!*k^d:  " + sf_lemma_boundary);
		System.out.println("Time elapsed:  " + (stop_time-start_time)/1000 + " seconds");
	}

	private static long factorial(int var) {
		long res = 1;
		for(int i = 2; i <= var; i++) {
			res *= i;
		}
		return res;
	}

}
