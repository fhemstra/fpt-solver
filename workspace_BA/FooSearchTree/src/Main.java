import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

public class Main {
	// +++++++++++ Settings +++++++++++++
	// Set this if the software is called from cmd instead of eclipse
	static boolean call_from_cmd = false;

	// Set this to mute debug output
	static boolean mute = true;

	// Set timeout, 30 min: 1800000, 10 min: 600000, 5 min: 300000
	static long timeout_value = 300000;

	// Set to only test one graph
	static boolean only_single_graph = true;
	static String single_graph_name = "vc-exact_037.gr";

	// Set range of k
	static int start_k = 26;
	static int k_increment = 1;
	static int stop_k = 91;

	// Set this to discard big graphs, set to -1 to discard nothing
	static int max_graph_size = 200;

	// Set this if the first pipeline should be skipped
	static boolean skip_search_tree = true;
	
	// Set this to use heuristics on the result of kernelization to improve HS ST runtime
	static boolean use_heuristics = true;

	// Set to decide which kernel to use
	static boolean use_bevern_kernel = false;

	// Set this if the timeout per graph should be accumulated over all k (for PACE)
	static boolean accumulate_time_over_k = true;

	// Select a dataset
	// static String current_dataset = "random_graphs";
	// static String current_dataset = "vc_pos_graphs";
	static String current_dataset = "pace";

	// ++++++++++ Settings done +++++++++

	public static void main(String[] args) {
		// Get time stamp for the name of the result file
		long main_init_time = System.currentTimeMillis();

		// Construct path to graph dir
		String graph_dir_path = "";
		if (call_from_cmd) {
			graph_dir_path = "../" + current_dataset;
		} else {
			graph_dir_path = current_dataset;
		}
		// Construct path to form dir
		String form_dir_path = "";
		if (call_from_cmd) {
			form_dir_path = "../" + "instances";
		} else {
			form_dir_path = "instances";
		}

		// Collect and sort files
		File graph_folder = new File(graph_dir_path);
		File form_folder = new File(form_dir_path);
		File[] graph_files = graph_folder.listFiles();
		File[] form_files = form_folder.listFiles();
		Arrays.sort(graph_files, new Comparator<File>() {
			@Override
			public int compare(File file_1, File file_2) {
				int graph_1_size = graphSize(file_1.getAbsolutePath());
				int graph_2_size = graphSize(file_2.getAbsolutePath());
				if (graph_1_size == graph_2_size)
					return 0;
				else if (graph_1_size > graph_2_size)
					return 1;
				else
					return -1;
			}
		});

		// Init result lists and other containers
		ArrayList<Formula> forms = new ArrayList<Formula>();
		ArrayList<Hypergraph> reduced_graphs = new ArrayList<Hypergraph>();
		// Init timers
		long start_time, stop_time, st_timeout, reduction_timeout, kernel_timeout, hs_timeout = 0;
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
		ArrayList<Long> pipe_1_time_used_per_instance = new ArrayList<Long>();
		ArrayList<Long> pipe_2_time_used_per_instance = new ArrayList<Long>();
		// Keep track of graphs that have been solved already (only used when
		// accumulate_time_over_k is set)
		HashSet<String> solved_graphs = new HashSet<String>();
		HashSet<String> timed_out_graphs = new HashSet<String>();

		// Prints
		// System.out.println("> Constructing " + form_files.length + " formulas with "
		// + graph_files.length + " instances and reducing them to hypergraphs.");
		System.out.println("> Constructing formulas with instances and reducing them to hypergraphs.");

		// Construct Formulas and reduce graphs (only once for all k)
		for (int i = 0; i < form_files.length; i++) {
			String form_path = form_files[i].getAbsolutePath();
			for (int j = 0; j < graph_files.length; j++) {
				String curr_graph_path = graph_files[j].getAbsolutePath();
				int curr_graph_size = graphSize(curr_graph_path);
				// Only take graphs, that are small enough
				if (curr_graph_size <= max_graph_size || max_graph_size == -1) {
					if (only_single_graph) {
						if (!curr_graph_path.contains(single_graph_name)) {
							continue;
						}
					}
					graph_sizes.add(curr_graph_size);
					// Constructing Formula
					Formula curr_formula = new Formula(form_path, curr_graph_path);
					System.out.println("  Accepted \"" + graph_files[j].getName() + "\" with " + curr_graph_size
							+ " nodes on formula \"" + curr_formula.formula_name + "\".");
					forms.add(curr_formula);
					c_list.add((double) curr_formula.c_par);
					dens_list.add(curr_formula.graph_density);
					// Reducing Formula to Hypergraph
					System.out.println("> Reduction");
					start_time = System.currentTimeMillis();
					reduction_timeout = start_time + timeout_value;
					// Reduce
					Hypergraph reduction_result = null;
					try {
						reduction_result = curr_formula.reduceToHS(mute, reduction_timeout);
					} catch (TimeoutException e) {
						System.out.println("! Reduction timed out.");
					}
					stop_time = System.currentTimeMillis();
					// Handle timeout
					if (reduction_result == null) {
						// Add null, handle this during evaluation
						reduced_graphs.add(null);
						reduced_edges.add((double) -1);
						reduced_nodes.add((double) -1);
					} else {
						reduced_graphs.add(reduction_result);
						reduced_edges.add((double) reduction_result.edges.size());
						reduced_nodes.add((double) actualArraySize(reduction_result.nodes));
					}
					// Process results
					double time_passed = (double) (stop_time - start_time) / (double) 1000;
					reduction_times.add(time_passed);
					pipe_2_time_used_per_instance.add(stop_time - start_time);
					printTime(time_passed);
				}
				// Graph is too big
				else if (!mute) {
					System.out.println(
							"  Discarded " + graph_files[j].getName() + " with " + curr_graph_size + " nodes.");
				}
			}
			// TODO only use the first formula
			// break;
		}

		// Init time_used array for the first pipeline
		if (!skip_search_tree) {
			while (pipe_1_time_used_per_instance.size() < forms.size()) {
				pipe_1_time_used_per_instance.add((long) 0);
			}
		}
		
		// Iterate over k
		for (int k_par = start_k; k_par <= stop_k; k_par += k_increment) {
			System.out.println("--- k = " + k_par + " ---");
			
			// Pipeline 1: Solve formulas with SearchTree
			if (!skip_search_tree) {
				for (int j = 0; j < forms.size(); j++) {
					Formula curr_form = forms.get(j);
					System.out.println(
							"> SearchTree, " + curr_form.formula_name + ", " + curr_form.graph_name + ", k = " + k_par);
					
					// Generate first assignment
					int[] start_assignment = new int[curr_form.c_par];
					for (int i = 0; i < curr_form.c_par; i++) {
						start_assignment[i] = curr_form.universe[0];
					}
					
					// Set timer
					start_time = System.currentTimeMillis();
					if (!accumulate_time_over_k) {
						st_timeout = start_time + timeout_value;
					} else {
						st_timeout = start_time + timeout_value - pipe_1_time_used_per_instance.get(j);
					}
					// SearchTree
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
					
					// Process results
					System.out.println("  result: " + st_result);
					search_tree_results.add(st_result);
					double time_passed = (double) (stop_time - start_time) / (double) 1000;
					search_tree_times.add(time_passed);
					
					// Update time passed
					pipe_1_time_used_per_instance.set(j,
							pipe_1_time_used_per_instance.get(j) + (stop_time - start_time));
					printTime(time_passed);
				}
			}

			// Pipeline 2: Reduce (already done, only once for all k) + Kernelize +
			// HS-Search
			for (int j = 0; j < reduced_graphs.size(); j++) {
				// Prevent adding multiple timeouts for single pipeline
				boolean timeout_noted = false;
				
				// Timeout happened during reduction or during previous kernelization with
				// smaller k
				if (reduced_graphs.get(j) == null || timed_out_graphs.contains(reduced_graphs.get(j).hypergraph_name)) {
					kernel_times.add((double) 0);
					kernel_edges.add((double) -1);
					kernel_nodes.add((double) -1);
					hs_times.add((double) 0);
					ke_results.add(false); // false for timeout
					if (!timeout_noted) {
						pipe_2_timeouts.add(true);
						timeout_noted = true;
					}
				}
				
				// Graph has already been solved and we don't want to keep solving for bigger k
				else if (accumulate_time_over_k && solved_graphs.contains(reduced_graphs.get(j).hypergraph_name)) {
					// Make empty entries and go to next graph
					kernel_times.add((double) 0);
					kernel_edges.add((double) -1);
					kernel_nodes.add((double) -1);
					hs_times.add((double) 0);
					ke_results.add(true); // true for already solved
				}
				
				// Kernelization
				else {
					Hypergraph curr_reduced_graph = reduced_graphs.get(j);
					System.out.println("> Kernelization, " + curr_reduced_graph.hypergraph_name + ", k = " + k_par + ", d = "
							+ curr_reduced_graph.d_par);
					
					// Set timer
					Hypergraph curr_kernel = null;
					start_time = System.currentTimeMillis();
					if (!accumulate_time_over_k) {
						kernel_timeout = start_time + reduction_times.get(j).longValue() + timeout_value;
					} else {
						kernel_timeout = start_time + timeout_value - pipe_2_time_used_per_instance.get(j);
					}
					
					// Kernelize
					int updated_k_par = k_par;
					int k_decrease = 0;
					try {
						if (use_bevern_kernel) {
							curr_kernel = curr_reduced_graph.kernelizeBevern(k_par, mute, kernel_timeout);
						} else {
							curr_kernel = curr_reduced_graph.kernelizeUniform(k_par, mute, kernel_timeout);
							// Remove dangling nodes and singletons
							// TODO this does not work yet
							if(use_heuristics) {
								boolean done = false;
								while (updated_k_par > 0 && !done) {
									System.out.println("New k_par:" + updated_k_par);
									int nodes_removed = curr_kernel.removeDanglingNodes(mute, kernel_timeout);
									int singletons_removed = curr_kernel.removeSingletons(mute, kernel_timeout);
									k_decrease += singletons_removed;
									if (singletons_removed == 0)
										done = true;
									updated_k_par = k_par - k_decrease; // TODO use updated_k_par for hs Search
								}
							}
						}
					} catch (TimeoutException e) {
						long timeout_stop = System.currentTimeMillis();
						double additional_time = (double) ((double) (timeout_stop - start_time) / 1000);
						System.out.println("! Kernelize timed out after additional "
								+ String.format("%.3f", additional_time) + " sec.");
						if (timeout_noted)
							return;
						if (!timeout_noted) {
							pipe_2_timeouts.add(true);
							timed_out_graphs.add(curr_reduced_graph.hypergraph_name);
							timeout_noted = true;
						}
					}
					stop_time = System.currentTimeMillis();
					
					// Prints
					if (!mute && curr_reduced_graph != null) {
						System.out.println("  hyp edges:     " + curr_reduced_graph.edges.size());
						System.out.println("  hyp nodes:     " + actualArraySize(curr_reduced_graph.nodes));
						int edges_removed = curr_reduced_graph.edges.size() - curr_kernel.edges.size();
						System.out.println("  edges removed: " + edges_removed);
						int nodes_removed = actualArraySize(curr_reduced_graph.nodes) - actualArraySize(curr_kernel.nodes);
						System.out.println("  nodes removed: " + nodes_removed);
						System.out.println("  kernel edges:  " + curr_kernel.edges.size());
						System.out.println("  kernel nodes:  " + actualArraySize(curr_kernel.nodes));
						long sf_lemma_boundary = factorial(curr_reduced_graph.d_par) * (long) Math.pow(k_par, curr_reduced_graph.d_par);
						System.out.println("  Lemma d!*k^d:  " + sf_lemma_boundary);
					}
					
					// Note time used
					double kernel_time_passed = (double) (stop_time - start_time) / (double) 1000;
					kernel_times.add(kernel_time_passed);
					pipe_2_time_used_per_instance.set(j,
							pipe_2_time_used_per_instance.get(j) + (stop_time - start_time));
					
					// If kernelization was successful, go to HS Search
					if (curr_kernel != null) {
						// Add results
						kernel_edges.add((double) curr_kernel.edges.size());
						kernel_nodes.add((double) actualArraySize(curr_kernel.nodes));
						printTime(kernel_time_passed);
						
						// Sort edges of current_kernel to make the SearchTree faster
						curr_kernel.edges.sort(new Comparator<Tuple>() {
							@Override
							public int compare(Tuple edge_1, Tuple edge_2) {
								if (edge_1.actualSize() > edge_2.actualSize()) {
									return 1;
								} else if (edge_1.actualSize() < edge_2.actualSize()) {
									return -1;
								} else {
									return 0;
								}
							}
						});
						
						// HS-SearchTree
						System.out.print("> HS-SearchTree ");
						boolean hs_result = false;
						// Set timer
						start_time = System.currentTimeMillis();
						if (!accumulate_time_over_k) {
							hs_timeout = start_time + reduction_times.get(j).longValue()
									+ kernel_times.get(j).longValue() + timeout_value;
						} else {
							hs_timeout = start_time + timeout_value - pipe_2_time_used_per_instance.get(j);
						}
						
						// Start HS-SearchTree
						try {
							hs_result = curr_kernel.hsSearchTree(updated_k_par, new ArrayList<Integer>(), mute,
									hs_timeout);
							if (!mute)
								System.out.println("\n");
							System.out.println("  result: " + hs_result);
						} catch (TimeoutException e) {
							long emergency_stop = System.currentTimeMillis();
							double additional_time = (double) ((double) (emergency_stop - start_time) / 1000);
							System.out.println("\n! HS-SearchTree timed out after additional "
									+ String.format("%.3f", additional_time) + " sec.");
							if (!timeout_noted) {
								pipe_2_timeouts.add(true);
								timed_out_graphs.add(curr_reduced_graph.hypergraph_name);
								timeout_noted = true;
							}
						}
						stop_time = System.currentTimeMillis();
						
						// Add results
						ke_results.add(hs_result);
						double hs_time_passed = (double) (stop_time - start_time) / (double) 1000;
						hs_times.add(hs_time_passed);
						pipe_2_time_used_per_instance.set(j,
								pipe_2_time_used_per_instance.get(j) + (stop_time - start_time));
						printTime(hs_time_passed);
						if (hs_result) {
							// Note that the current graph has been solved
							solved_graphs.add(curr_reduced_graph.hypergraph_name);
						}
					} // End of HS Search
					
					// Timeout happened during kernelization
					else {
						kernel_edges.add((double) -1);
						kernel_nodes.add((double) -1);
						ke_results.add(false);
						hs_times.add((double) 0);
					}
				} // End of Kernelization
				// If nothing timed out, note false
				if (!timeout_noted) {
					pipe_2_timeouts.add(false);
					timeout_noted = true;
				}
			} // End of Pipeline 2
		} // End of loop through k

		// Collect and save results
		if (!mute)
			System.out.println("\n------------------------------------");
		int number_of_results = Math.max(search_tree_results.size(), ke_results.size());
		// Init write buffer
		ArrayList<String> write_buffer = new ArrayList<String>();
		String headline = "nodes;pipe 1;pipe 2;reduction_time;kernel_time;hs_st_time;k;st_result;ke_result;equal;ke_nodes;ke_edges;c_par;density;reduced_nodes;reduced_edges;pipe_1_timeout;pipe_2_timeout\n";
		write_buffer.add(headline);
		int curr_k_par = start_k;
		boolean all_graphs_timed_out = true;
		
		// Loop over all results
		for (int i = 0; i < number_of_results; i++) {
			// Formula and Reduction are the same for every k
			int k_indep_index = i % forms.size();
			
			// Prints
			if (!mute) {
				System.out
						.println("--- Graph: " + forms.get(k_indep_index).graph_name + ", k = " + curr_k_par + " ---");
				if (!skip_search_tree) {
					System.out.println("1. SearchTree:    " + search_tree_times.get(i));
					System.out.println("   " + search_tree_results.get(i));
				} else {
					System.out.println("1. SearchTree: skipped");
				}
				System.out.println("2. Reduction:     " + reduction_times.get(k_indep_index));
				System.out.println("   Kernelisation: " + kernel_times.get(i));
				System.out.println("   HS-SearchTree: " + hs_times.get(i));
				System.out.println("   " + ke_results.get(i));
			}

			// Create String for csv file
			double timeout_2 = pipe_2_timeouts.get(i) ? 1 : 0;
			double pipe_2_sum = 0;
			double curr_ke_res = ke_results.get(i) ? 1 : 0;
			// pipe_2_sum should be -1 when the instance timed out or is solved
			if (timeout_2 == 1.0 || curr_ke_res == 1.0) {
				pipe_2_sum = -1;
			} else {
				pipe_2_sum = reduction_times.get(k_indep_index) + kernel_times.get(i) + hs_times.get(i);
				all_graphs_timed_out = false;
			}
			
			// With ST
			if (!skip_search_tree) {
				double timeout_1 = pipe_1_timeouts.get(i) ? 1 : 0;
				double equal_res = search_tree_results.get(i) == ke_results.get(i) ? 1 : 0;
				double curr_st_res = search_tree_results.get(i) ? 1 : 0;
				write_buffer.add(graph_sizes.get(k_indep_index) + ";" + search_tree_times.get(i) + ";" + pipe_2_sum
						+ ";" + reduction_times.get(k_indep_index) + ";" + kernel_times.get(i) + ";" + hs_times.get(i)
						+ ";" + curr_k_par + ";" + curr_st_res + ";" + curr_ke_res + ";" + equal_res + ";"
						+ kernel_nodes.get(i) + ";" + kernel_edges.get(i) + ";" + c_list.get(k_indep_index) + ";"
						+ dens_list.get(k_indep_index) + ";" + reduced_nodes.get(k_indep_index) + ";"
						+ reduced_edges.get(k_indep_index) + ";" + timeout_1 + ";" + timeout_2 + "\n");
			}
			
			// Without ST
			else {
				write_buffer.add(graph_sizes.get(k_indep_index) + ";" + "-1" + ";" + pipe_2_sum + ";"
						+ reduction_times.get(k_indep_index) + ";" + kernel_times.get(i) + ";" + hs_times.get(i) + ";"
						+ curr_k_par + ";" + "-1" + ";" + curr_ke_res + ";" + "-1" + ";" + kernel_nodes.get(i) + ";"
						+ kernel_edges.get(i) + ";" + c_list.get(k_indep_index) + ";" + dens_list.get(k_indep_index)
						+ ";" + reduced_nodes.get(k_indep_index) + ";" + reduced_edges.get(k_indep_index) + ";" + "-1"
						+ ";" + timeout_2 + "\n");
			}

			// Prepare next iteration of k and save to csv
			if ((i + 1) % forms.size() == 0) {
				// Save buffer to csv
				String file_name = Long.toString(main_init_time) + "_" + current_dataset + "_k_" + start_k + "-"
						+ stop_k + ".csv";
				writeToCsv(write_buffer, file_name, call_from_cmd);
				write_buffer.clear();
				// If all graphs are timed out, leave
				if (all_graphs_timed_out) {
					break;
				}
				// go to next k
				curr_k_par += k_increment;
				// reset flag
				all_graphs_timed_out = true;
			}
		}
	}

	/**
	 * Writes all lines from the given list to the specified csv-file.
	 */
	private static void writeToCsv(ArrayList<String> write_buffer, String file_name, boolean cmd) {
		String result_file_path = "";
		if (cmd) {
			result_file_path = ".." + File.separator + ".." + File.separator + ".." + File.separator + "matlab_plots"
					+ File.separator + file_name;
		} else {
			result_file_path = ".." + File.separator + ".." + File.separator + "matlab_plots" + File.separator
					+ file_name;
		}
		File out_file = new File(result_file_path);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(out_file, true)); // true for append mode
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
		for (int i = 2; i <= var; i++) {
			res *= i;
		}
		return res;
	}
	
	/**
	 * Returns the size of the given array without counting -1 entries.
	 */
	private static int actualArraySize(int[] arr_with_minus_one) {
		int counter = 0;
		for(int elem : arr_with_minus_one) {
			if(elem != -1) {
				counter++;
			}
		}
		return counter;
	}

}
