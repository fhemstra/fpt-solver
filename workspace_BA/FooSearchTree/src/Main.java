import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

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
//		// Kernelize graphs
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
//			current_graph.kernelizeNonUniform(current_graph, chosen_k, mute);
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
//			System.out.println("--- FORMULA ---");
//			System.out.println(form.toOutputString());
//			System.out.println("\n--- SEARCH TREE ---");
//			System.out.println(form.searchTree(3, new ArrayList<Integer>(), mute, new int[form.c_par], 0));
//			System.out.println("\n--- REDUCTION ---");
//			Hypergraph hyp = form.reduceToHS(mute);
//			System.out.println(hyp.toOutputString());
//			System.out.println("\n--- KERNELIZATION ---"); // Kernelization Hypergraph
//			Hypergraph hyp_kernel = hyp.kernelizeUniform(hyp, 3, mute);
//			System.out.println("<< Main\nKernel:");
//			System.out.println(hyp_kernel.toOutputString());
//			System.out.println("--- END FORMULA ---\n");
////			// Hypergraph doc example
////			int[] ex_nodes = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
////			ArrayList<Tuple> ex_edges = new	ArrayList<Tuple>();
////			ex_edges.add(new Tuple(new int[]{1,4}));
////			ex_edges.add(new Tuple(new int[]{2,4,5}));
////			ex_edges.add(new Tuple(new int[]{3,4}));
////			ex_edges.add(new Tuple(new int[]{4}));
////			ex_edges.add(new Tuple(new int[]{4,6}));
////			ex_edges.add(new Tuple(new int[]{6,8,9}));
////			ex_edges.add(new Tuple(new int[]{7,8,9}));
////			ex_edges.add(new Tuple(new int[]{8,9,10,11}));
////			ex_edges.add(new Tuple(new int[]{8,9,13}));
////			ex_edges.add(new Tuple(new int[]{11,12,14,15}));
////			ex_edges.add(new Tuple(new int[]{13,15}));
////			Hypergraph doc_ex_1 = new Hypergraph(ex_nodes, ex_edges);
////			System.out.println("doc_ex:\n" + doc_ex_1.toOutputString() + "\n"); // k = 3
////			Hypergraph doc_ex_1_kernel = doc_ex_1.kernelizeNonUniform(doc_ex_1, 3, mute);
////			System.out.println("+++\nex_kernel:\n" + doc_ex_1_kernel.toOutputString());
//			
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
//		Hypergraph doc_ex_kernel = doc_ex.kernelizeNonUniform(doc_ex, 3, mute);
//		System.out.println("After kernelize:");
//		System.out.println(doc_ex_kernel.toOutputString());
//		hs_st_result = doc_ex.hsSearchTree(doc_ex, 3, new ArrayList<Integer>(), mute);
//		System.out.println();
//		System.out.println(hs_st_result);
		
		
		
		// Test pipelines
		int start_k = 12;
		int k_increment = 1;
		int stop_k = 12;
		boolean skip_search_tree = false;
		String graph_mode = "random";
//		String graph_mode = "vc_pos";
		
//		File graph_folder = new File("random_graphs"); // Use this for execution in eclipse
//		File form_folder = new File("instances"); // Use this for execution in eclipse
		File graph_folder = new File("../random_graphs"); // Use this for execution in windows cmd
		File form_folder = new File("../instances"); // Use this for execution in windows cmd
		
//		File graph_folder = new File("../vc_pos_graphs"); // Use this for execution in windows cmd
//		File form_folder = new File("../instances"); // Use this for execution in windows cmd
//		File graph_folder = new File("vc_pos_graphs"); // Use this for execution in eclipse
//		File form_folder = new File("instances"); // Use this for execution in eclipse
		
//		File graph_folder = new File("../pace"); // Use this for execution in windows cmd
//		File form_folder = new File("../instances"); // Use this for execution in windows cmd
//		File pace_folder = new File("pace"); // Use this inside of eclipse
//		File form_folder = new File("instances"); // Use this for execution inside of eclipse
		
		File[] graph_files = graph_folder.listFiles();
		File[] form_files = form_folder.listFiles();
		ArrayList<Formula> forms = new ArrayList<Formula>();
		ArrayList<Hypergraph> reduced_graphs = new ArrayList<Hypergraph>();
		// Timer
		long start_time = 0;
		long stop_time = 0;
		long timeout_intervall = 1800000; // 30 min: 1800000
		long st_timeout = 0;
		long reduction_timeout = 0;
		long kernel_timeout = 0;
		long hs_timeout = 0;
		// Result lists
		ArrayList<Integer> graph_sizes = new ArrayList<Integer>();
		ArrayList<Double> reduction_times = new ArrayList<Double>();
		ArrayList<Double> search_tree_times = new ArrayList<Double>();
		ArrayList<Double> kernel_times = new ArrayList<Double>();
		ArrayList<Double> kernel_edges = new ArrayList<Double>();
		ArrayList<Double> kernel_nodes = new ArrayList<Double>();
		ArrayList<Double> reduced_nodes = new ArrayList<Double>();
		ArrayList<Double> reduced_edges = new ArrayList<Double>();
		ArrayList<Double> hs_times = new ArrayList<Double>();
		ArrayList<Double> c_list = new ArrayList<Double>();
		ArrayList<Double> dens_list = new ArrayList<Double>();
		ArrayList<Boolean> search_tree_results = new ArrayList<Boolean>();
		ArrayList<Boolean> ke_results = new ArrayList<Boolean>();
		ArrayList<Boolean> pipe_1_timeouts = new ArrayList<Boolean>();
		ArrayList<Boolean> pipe_2_timeouts = new ArrayList<Boolean>();
		// System.out.println("> Constructing " + form_files.length + " formulas with "
		// + graph_files.length + " vc-instances and reducing them to hypergraphs.");
		System.out.println("> Constructing formulas with vc-instances and reducing them to hypergraphs.");

		// Construct Formulas and reduced graphs
		for (int i = 0; i < form_files.length; i++) {
			String form_path = form_files[i].getAbsolutePath();
			// TODO j = 0; j < graph_files.length
			for (int j = 0; j < graph_files.length; j++) {
				// TODO remove condition
				// if(j%2 == 1) {
				if (true) {
					String graph_path = graph_files[j].getAbsolutePath();
					int curr_graph_size = graphSize(graph_path);
					// Only take graphs, that are not too big
					// TODO change this, fixed n
					if (curr_graph_size <= 1000) {
						graph_sizes.add(curr_graph_size);
						// Construction
						Formula curr_formula = new Formula(form_path, graph_path);
						System.out.println("  Accepted \"" + graph_files[j].getName() + "\" with " + curr_graph_size
								+ " nodes on formula \"" + curr_formula.formula_name + "\".");
						forms.add(curr_formula);
						c_list.add((double) curr_formula.c_par);
						dens_list.add(curr_formula.graph_density);
						// Reduction
						System.out.println("> Reduction");
						start_time = System.currentTimeMillis();
						reduction_timeout = start_time + timeout_intervall;
						Hypergraph reduction_result = null;
						try {
							reduction_result = curr_formula.reduceToHS(mute, reduction_timeout);
						} catch (TimeoutException e) {
							System.out.println("! Reduction timed out.");
						}
						stop_time = System.currentTimeMillis();
						// Handle timeout
						if(reduction_result == null) {
							// Add null, handle this during evaluation
							reduced_graphs.add(null);
							reduced_edges.add((double) -1);
							reduced_nodes.add((double) -1);
						} else {
							reduced_graphs.add(reduction_result);
							reduced_edges.add((double) reduction_result.edges.size());
							reduced_nodes.add((double) reduction_result.nodes.length);
						}
						double time_passed = (double) (stop_time - start_time) / (double) 1000;
						reduction_times.add(time_passed);
						printTime(time_passed);
					}
					// Graph too big
					else {
						System.out.println(
								"  Discarded " + graph_files[j].getName() + " with " + curr_graph_size + " nodes.");
					}
					// TODO remove break, only test one graph
//					break;
				}
			}
			// TODO only use the first formula
			break;
		}

		// Solving formulas
		for (int k_par = start_k; k_par <= stop_k; k_par += k_increment) {
			System.out.println("--- k = " + k_par + " ---");
			// Pipeline 1: Solve formulas with SearchTree
			if (!skip_search_tree) {
				for (int j = 0; j < forms.size(); j++) {
					Formula curr_form = forms.get(j);
					System.out.println(
							"> SearchTree, " + curr_form.formula_name + ", " + curr_form.graph_name + ", k = " + k_par);
					int[] start_assignment = new int[curr_form.c_par];
					// generate first assignment
					for (int i = 0; i < curr_form.c_par; i++) {
						start_assignment[i] = curr_form.universe[0];
					}
					start_time = System.currentTimeMillis();
					st_timeout = start_time + timeout_intervall;
					boolean st_result = false;
					try {
						st_result = curr_form.searchTree(k_par, new ArrayList<Integer>(), mute, start_assignment, 0,
								st_timeout);
						pipe_1_timeouts.add(false);
					} catch (TimeoutException e) {
						System.out.println("! ST timed out.");
						pipe_1_timeouts.add(true);
					}
					stop_time = System.currentTimeMillis();
					if (!mute)
						System.out.println();
					System.out.println("  result: " + st_result);
					search_tree_results.add(st_result);
					double time_passed = (double) (stop_time - start_time) / (double) 1000;
					search_tree_times.add(time_passed);
					printTime(time_passed);
				}
			}
			// Pipeline 2: (Reduce) + Kernel + HS SearchTree
			for (int j = 0; j < reduced_graphs.size(); j++) {
				// Prevent adding multiple timeout for single pipeline
				boolean timeout_noted = false;
				// Timeout happened during reduction
				if(reduced_graphs.get(j) == null) {
					kernel_times.add((double) 0);
					kernel_edges.add((double) -1);
					kernel_nodes.add((double) -1);
					hs_times.add((double) 0);
					ke_results.add(false);
					if(!timeout_noted) {
						pipe_2_timeouts.add(true);
						timeout_noted = true;
					}
				}
				else {
					// Kernel
					Hypergraph curr_graph = reduced_graphs.get(j);
					System.out.println("> Kernelization, " + curr_graph.hypergraph_name + ", k = " + k_par + ", d = "
							+ curr_graph.d_par);
					start_time = System.currentTimeMillis();
					Hypergraph curr_kernel = null;
					kernel_timeout = start_time + reduction_times.get(j).longValue() + timeout_intervall;
					try {
						curr_kernel = curr_graph.kernelizeUniform(curr_graph, k_par, mute, kernel_timeout);
					} catch (TimeoutException e) {
						System.out.println("! Kernelization timed out.");
						if(!timeout_noted) {
							pipe_2_timeouts.add(true);
							timeout_noted = true;
						}
					}
					stop_time = System.currentTimeMillis();
					if (!mute && curr_graph != null) {
						System.out.println("  hyp edges:     " + curr_graph.edges.size());
						System.out.println("  hyp nodes:     " + curr_graph.nodes.length);
						int edges_removed = curr_graph.edges.size() - curr_kernel.edges.size();
						System.out.println("  edges removed: " + edges_removed);
						int nodes_removed = curr_graph.nodes.length - curr_kernel.nodes.length;
						System.out.println("  nodes removed: " + nodes_removed);
						System.out.println("  kernel edges:  " + curr_kernel.edges.size());
						System.out.println("  kernel nodes:  " + curr_kernel.nodes.length);
						long sf_lemma_boundary = factorial(curr_graph.d_par) * (long) Math.pow(k_par, curr_graph.d_par);
						System.out.println("  Lemma d!*k^d:  " + sf_lemma_boundary);
					}
					double kernel_time_passed = (double) (stop_time - start_time) / (double) 1000;
					kernel_times.add(kernel_time_passed);
					// Timeout happened during kernelization
					if(curr_kernel != null) {
						kernel_edges.add((double) curr_kernel.edges.size());
						kernel_nodes.add((double) curr_kernel.nodes.length);
						printTime(kernel_time_passed);						
						// HS SearchTree
						System.out.println("> HS-SearchTree");
						start_time = System.currentTimeMillis();
						boolean hs_result = false;
						hs_timeout = start_time + reduction_times.get(j).longValue() + kernel_times.get(j).longValue() + timeout_intervall;
						try {
							hs_result = curr_kernel.hsSearchTree(curr_kernel, k_par, new ArrayList<Integer>(), mute, hs_timeout);
						} catch (TimeoutException e) {
							System.out.println("! HS_SearchTree timed out.");
							if(!timeout_noted) {
								pipe_2_timeouts.add(true);
								timeout_noted = true;
							}
						}
						stop_time = System.currentTimeMillis();
						if (!mute)
							System.out.println();
						System.out.println("  result: " + hs_result);
						ke_results.add(hs_result);
						double hs_time_passed = (double) (stop_time - start_time) / (double) 1000;
						hs_times.add(hs_time_passed);
						printTime(hs_time_passed);
					} else {
						kernel_edges.add((double) -1);
						kernel_nodes.add((double) -1);
						printTime(kernel_time_passed);
						ke_results.add(false);
						hs_times.add((double) 0);
					}
				}
				// If nothing timed out, add false
				if(!timeout_noted) {
					pipe_2_timeouts.add(false);
					timeout_noted = true;
				}
			}
		}
		
		// Collect results
		int number_of_iterations = Math.max(search_tree_results.size(), ke_results.size());
		System.out.println("\n------------------------------------");
		int curr_k_par = start_k;
		ArrayList<String> write_buffer = new ArrayList<String>();
		String headline = "nodes;pipe 1;pipe 2;reduction_time;kernel_time;hs_st_time;k;st_result;ke_result;equal;ke_nodes;ke_edges;c_par;density;reduced_nodes;reduced_edges;pipe_1_timeout;pipe_2_timeout\n";
		write_buffer.add(headline);
		String file_name = "";
		for (int i = 0; i < number_of_iterations; i++) {
			int form_and_redu_index = i % forms.size();
			System.out.println(
					"--- Graph: " + forms.get(form_and_redu_index).graph_name + ", k = " + curr_k_par + " ---");
			if (!skip_search_tree) {
				System.out.println("1. SearchTree:    " + search_tree_times.get(i));
				System.out.println("   " + search_tree_results.get(i));
			} else {
				System.out.println("1. SearchTree: skipped");
			}
			System.out.println("2. Reduction:     " + reduction_times.get(form_and_redu_index));
			System.out.println("   Kernelisation: " + kernel_times.get(i));
			System.out.println("   HS-SearchTree: " + hs_times.get(i));
			System.out.println("   " + ke_results.get(i));

			// Create String for csv file
			double pipe_2_sum = reduction_times.get(form_and_redu_index) + kernel_times.get(i) + hs_times.get(i);
			double curr_ke_res = ke_results.get(i) ? 1 : 0;
			double timeout_1 = pipe_1_timeouts.get(i) ? 1 : 0;
			double timeout_2 = pipe_2_timeouts.get(i) ? 1 : 0;
			if (!skip_search_tree) {
				double equal_res = search_tree_results.get(i) == ke_results.get(i) ? 1 : 0;
				double curr_st_res = search_tree_results.get(i) ? 1 : 0;
				write_buffer.add(graph_sizes.get(form_and_redu_index) + ";" + search_tree_times.get(i) + ";"
						+ pipe_2_sum + ";" + reduction_times.get(form_and_redu_index) + ";" + kernel_times.get(i) + ";"
						+ hs_times.get(i) + ";" + curr_k_par + ";" + curr_st_res + ";" + curr_ke_res + ";" + equal_res
						+ ";" + kernel_nodes.get(i) + ";" + kernel_edges.get(i) + ";" + c_list.get(i) + ";"
						+ String.format("%.3f", dens_list.get(i)) + ";" + reduced_nodes.get(i) + ";"
						+ reduced_edges.get(i) + ";" + timeout_1 + ";" + timeout_2 + "\n");
			} else {
				write_buffer.add(graph_sizes.get(form_and_redu_index) + ";" + "-1" + ";" + pipe_2_sum + ";"
						+ reduction_times.get(form_and_redu_index) + ";" + kernel_times.get(i) + ";" + hs_times.get(i)
						+ ";" + curr_k_par + ";" + "-1" + ";" + curr_ke_res + ";" + "-1" + ";" + kernel_nodes.get(i)
						+ ";" + kernel_edges.get(i) + ";" + c_list.get(i) + ";"
						+ String.format("%.3f", dens_list.get(i)) + ";" + reduced_nodes.get(i) + ";"
						+ reduced_edges.get(i) + ";" + timeout_1 + ";" + timeout_2 + "\n");
			}

			// Prepare next iteration and save to csv
			if ((i + 1) % forms.size() == 0) {
				// Save buffer to csv
				file_name = Integer.toString((int) System.currentTimeMillis()) + "_" + graph_mode + "_k_" + curr_k_par
						+ ".csv";
				writeToCsv(write_buffer, file_name);
				write_buffer.clear();
				write_buffer.add(headline);
				// go to next k
				curr_k_par += k_increment;
			}
		}
	}

	/**
	 * Writes all lines from the given list to the specified csv-file.
	 */
	private static void writeToCsv(ArrayList<String> write_buffer, String file_name) {
		File out_file = new File(".." + File.separator + ".." + File.separator + ".." + File.separator + "matlab_plots"
				+ File.separator + file_name);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
			// TODO sort write buffer
			for (String s : write_buffer) {
				bw.write(s);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of nodes the given PACE-formatted graph has.
	 */
	private static int graphSize(String graph_path) {
		int size = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(graph_path)));
			// First line is the descriptor
			String first_line = br.readLine();
			String[] first_split_line = first_line.split(" ");
			size = Integer.parseInt(first_split_line[2]);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * Prints the given time in a nice format.
	 */
	private static void printTime(double time) {
		System.out.println("- Time elapsed:  " + String.format("%.3f", time) + " sec");
	}

	/**
	 * Calculates the factorial of var.
	 */
	private static long factorial(int var) {
		long res = 1;
		for(int i = 2; i <= var; i++) {
			res *= i;
		}
		return res;
	}

}
