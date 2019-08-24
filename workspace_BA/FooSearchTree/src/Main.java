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
//
//		// Solve each File
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
		File[] graph_files = pace_folder.listFiles();
		File[] form_files = form_folder.listFiles();
		ArrayList<Formula> formulas = new ArrayList<Formula>();
		ArrayList<Hypergraph> reduced_graphs = new ArrayList<Hypergraph>();
		// Timer
		long start_time = 0;
		long stop_time = 0;
//		System.out.println("> Constructing " + form_files.length + " formulas with " + graph_files.length + " vc-instances and reducing them to hypergraphs.");
		System.out.println("> Constructing " + 1 + " formulas with " + 10 + " vc-instances and reducing them to hypergraphs.");		start_time = System.currentTimeMillis();
		
		// Construct Formulas and reduced graphs
		for( int i = 0; i < form_files.length; i++) {
			String form_path = form_files[i].getAbsolutePath();
			// TODO j = 0; j < graph_files.length
			for (int j = 0; j < 3; j++) {
				String graph_path = graph_files[j].getAbsolutePath();
				// Construction
				Formula curr_formula = new Formula(form_path, graph_path);
				formulas.add(curr_formula);
				// Reduction
				reduced_graphs.add(curr_formula.reduceToHS(mute));
				// TODO remove break;
//				break;
			}
			// TODO only the first formula works right now
			break;
		}
		stop_time = System.currentTimeMillis();
		double constr_time = (double)(stop_time-start_time)/(double)1000;
		printTime(constr_time);
		
		// Solving formulas
//		for(int k_par = 1; k_par < 11; k_par++) {
		for(int k_par = 5; k_par < 6; k_par++) {
			System.out.println("--- k = " + k_par + " ---");
			// Pipeline 1: Solve formulas with SearchTree
			for(int j = 0; j < formulas.size(); j++) {
				System.out.println("> SearchTree, graph " + j + ", k = " + k_par);
				start_time = System.currentTimeMillis();
				Formula curr_form = formulas.get(j);
				boolean st_result = curr_form.searchTree(k_par, new ArrayList<Integer>(), mute);
				stop_time = System.currentTimeMillis();
				if(!mute) System.out.println();
				System.out.println("  SearchTree result: " + st_result );
				double st_time = (double)(stop_time-start_time)/(double)1000;
				printTime(st_time);
			}
			// Pipeline 2: (Reduce) + Kernel + HS SearchTree
			for(int j = 0; j < reduced_graphs.size(); j++) {
				// Kernel
				Hypergraph curr_graph =  reduced_graphs.get(j);
				System.out.println("> Kernelization, graph " + j + ", k = " + k_par + ", d = " + curr_graph.d_par);
				start_time = System.currentTimeMillis();
				// TODO kernelize should return kernel, not manipulate this
				Hypergraph curr_kernel = curr_graph.kernelize(curr_graph, k_par, mute);
				stop_time = System.currentTimeMillis();
				int edges_removed = curr_graph.edges.size() - curr_kernel.edges.size();
				int nodes_removed = curr_graph.nodes.length - curr_kernel.nodes.length;
				System.out.println("  hyp edges:     " + curr_graph.edges.size());
				System.out.println("  hyp nodes:     " + curr_graph.nodes.length);
				System.out.println("  edges removed: " + edges_removed);
				System.out.println("  nodes removed: " + nodes_removed);
				System.out.println("  kernel edges:  " + curr_kernel.edges.size());
				System.out.println("  kernel nodes:  " + curr_kernel.nodes.length);
				long sf_lemma_boundary = factorial(curr_graph.d_par) * (long) Math.pow(k_par, curr_graph.d_par);
				System.out.println("  Lemma d!*k^d:  " + sf_lemma_boundary);
				double kern_time = (double)(stop_time-start_time)/(double)1000;
				printTime(kern_time);
				
				// HS SearchTree
				System.out.println("> HS-SearchTree, graph " + j);
				start_time = System.currentTimeMillis();
				boolean hs_st_result = curr_kernel.hsSearchTree(curr_kernel, k_par, new ArrayList<Integer>(), mute);
				stop_time = System.currentTimeMillis();
				System.out.println();
				System.out.println("  result: " + hs_st_result);
				double hs_st_time = (double)(stop_time-start_time)/(double)1000;
				printTime(hs_st_time);
			}
		}
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
