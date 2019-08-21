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
//			// Set how many graphs should be loaded.
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
			
//			break;
//		}
		
		// Hypergraph doc example
		// Test HS Search Tree (works)
//		int[] ex_nodes = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
//		ArrayList<Tuple> ex_edges = new	ArrayList<Tuple>();
//		ex_edges.add(new Tuple(new int[]{1,4,-1,-1}));
//		ex_edges.add(new Tuple(new int[]{2,4,5,-1}));
//		ex_edges.add(new Tuple(new int[]{3,4,-1,-1}));
//		ex_edges.add(new Tuple(new int[]{4,-1,-1,-1}));
//		ex_edges.add(new Tuple(new int[]{4,6,-1,-1}));
//		ex_edges.add(new Tuple(new int[]{6,8,9,-1}));
//		ex_edges.add(new Tuple(new int[]{7,8,9,-1}));
//		ex_edges.add(new Tuple(new int[]{8,9,10,11}));
//		ex_edges.add(new Tuple(new int[]{8,9,13,-1}));
//		ex_edges.add(new Tuple(new int[]{11,12,14,15}));
//		ex_edges.add(new Tuple(new int[]{13,15,-1,-1}));
//		Hypergraph doc_ex = new Hypergraph(ex_nodes, ex_edges);
//		boolean hs_st_result = doc_ex.hsSearchTree(doc_ex, 3, new ArrayList<Integer>(), mute);
//		System.out.println();
//		System.out.println(hs_st_result);
//		doc_ex.kernelize(doc_ex, 3, mute);
//		System.out.println("After kernelize:");
//		hs_st_result = doc_ex.hsSearchTree(doc_ex, 3, new ArrayList<Integer>(), mute);
//		System.out.println();
//		System.out.println(hs_st_result);
		
		// Test formulas from PACE files
		File pace_folder = new File("../pace"); // Use this for execution in windows cmd
		File form_folder = new File("../instances"); // Use this for execution in windows cmd
//		File pace_folder = new File("pace"); // Use this inside of eclipse
//		File form_folder = new File("instances"); // Use this for execution inside of eclipse
		File[] pace_files = pace_folder.listFiles();
		File[] form_files = form_folder.listFiles();
		// TODO Iterate over this
		int k_par = 10;
		for( int i = 0; i < form_files.length; i++) {
			File curr_form_file = form_files[i];
			// TODO change back to j = 0
			for (int j = 0; j < pace_files.length; j++) {
				File curr_pace_file = pace_files[j];
				if (curr_form_file.isFile() && curr_pace_file.isFile()) {
					testFile(curr_form_file, curr_pace_file, k_par);
				}
			}	
		}
		
	}
	
	private static void testFile(File form_file, File graph_file, int k_par) {
		long start_time = 0;
		long stop_time = 0;
		System.out.println("--- Form: " + form_file.getName() + ", Graph: " + graph_file.getName() + " ---");
		String form_path = form_file.getAbsolutePath();
		String graph_path = graph_file.getAbsolutePath();
		
		// TODO outsource this, also does not have to be done multiple times
		System.out.println("> Constructiong formula");
		start_time = System.currentTimeMillis();
		Formula pace_form = new Formula(form_path, graph_path);
		stop_time = System.currentTimeMillis();
		double constr_time = (double)(stop_time-start_time)/(double)1000;
		printTime(constr_time);
		
		System.out.println("> SearchTree, k = " + k_par);
		start_time = System.currentTimeMillis();
		boolean st_res = pace_form.searchTree(k_par, new ArrayList<Integer>(), mute);
		stop_time = System.currentTimeMillis();
		System.out.println();
		System.out.println("  SearchTree result: " + st_res);
		double st_time = (double)(stop_time-start_time)/(double)1000;
		printTime(st_time);
		
		// TODO outsource reduction, only needs to be done once for all values of k
		System.out.println("> Reduction");
		start_time = System.currentTimeMillis();
		Hypergraph pace_reduced_graph = pace_form.reduceToHS();
		stop_time = System.currentTimeMillis();
		double red_time = (double)(stop_time-start_time)/(double)1000;
		printTime(red_time);
		
		// Kernelize
		System.out.println("> Kernelization, k = " + k_par + ", d = " + pace_reduced_graph.d_par);
		int edges_before = pace_reduced_graph.edges.size();
		int nodes_before = pace_reduced_graph.nodes.length;
		System.out.println("  edges:         " + edges_before);
		System.out.println("  nodes:         " + nodes_before);
		start_time = System.currentTimeMillis();
		pace_reduced_graph.kernelize(pace_reduced_graph, k_par, mute);
		stop_time = System.currentTimeMillis();
		int edges_removed = edges_before - pace_reduced_graph.edges.size();
		int nodes_removed = nodes_before - pace_reduced_graph.nodes.length;
		System.out.println("  edges removed: " + edges_removed);
		System.out.println("  nodes removed: " + nodes_removed);
		System.out.println("  kernel edges:  " + pace_reduced_graph.edges.size());
		System.out.println("  kernel nodes:  " + pace_reduced_graph.nodes.length);
		long sf_lemma_boundary = factorial(pace_reduced_graph.d_par) * (long) Math.pow(k_par, pace_reduced_graph.d_par);
		System.out.println("  Lemma d!*k^d:  " + sf_lemma_boundary);
		double kern_time = (double)(stop_time-start_time)/(double)1000;
		printTime(kern_time);
		
		// Search Tree after kernelize
		System.out.println("> HS-SearchTree ");
		start_time = System.currentTimeMillis();
		boolean hs_str_res = pace_reduced_graph.hsSearchTree(pace_reduced_graph, k_par, new ArrayList<Integer>(), mute);
		stop_time = System.currentTimeMillis();
		System.out.println();
		System.out.println("  result: " + hs_str_res);
		double hs_st_time = (double)(stop_time-start_time)/(double)1000;
		printTime(hs_st_time);		
		// compare times: SearchTree <-> Reduction + Kernel + hsSearchTree
		System.out.println("- Assignments time    : " + String.format("%.3f", constr_time) + " sec");
		System.out.println("- Force time          : " + String.format("%.3f", st_time) + " sec");
		double case_two_time = red_time + kern_time + hs_st_time;
		System.out.println("- Redu + Kern + Force : " + String.format("%.3f", case_two_time) + " sec");
		System.out.println();
	}

	private static void printTime(double time) {
		System.out.println("- Time elapsed:  " + String.format("%.3f", time) + " sec");
	}

	private static long factorial(int var) {
		long res = 1;
		for(int i = 2; i <= var; i++) {
			res *= i;
		}
		return res;
	}

}
