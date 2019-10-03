import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

public class Main {
	// +++++++++++ Settings +++++++++++++
	// Set this if the software is called from cmd instead of eclipse
	static boolean call_from_cmd = true;
	// Set this to mute debug output
	static boolean mute = true;
	// Set timeout, 30 min: 1800000, 10 min: 600000, 5 min: 300000, 1 min: 60000
	static long timeout_value = 180000;
	// Set to only test one graph
	static boolean only_single_graph = false;
	static String single_graph_name = "vc-exact_004.gr";
	// Set to test only the first x graphs
	static boolean only_first_x_graphs = false;
	static int number_of_graphs_to_test = 10;
	// Set range of k
	static int start_k = 0;
	static int k_increment = 1;
	static int stop_k = 1000;
	// Set this to discard big graphs, set to -1 to discard nothing
	static int max_graph_size = -1;
	// Set this to sort input graphs by their size ascending
	static boolean sort_by_nodes = false;
	// Set to skip both pipelines, only reducing graphs
	static boolean skip_solution = true;
	// Set this if the first pipeline should be skipped
	static boolean skip_search_tree = true;
	// Set to abandon branches of HS ST that contain a big matching
	static boolean use_branch_and_bound = true;
	// Set this to use heuristics on the result of kernelization to improve HS ST
	// runtime
	static boolean use_heuristics_after_reduction = true;
	// Set to decide which kernel to use
	static boolean use_bevern_kernel = false;
	// Set this if the timeout per graph should be accumulated over all k (for PACE)
	static boolean accumulate_time_over_k = true;
	// Set nr of columns the CSV file should have
	static int nr_of_columns = 24;
	// Select a dataset
//	static String current_dataset = "pace";
	static String current_dataset = "k_star_graphs";
//	static String current_dataset = "gnp_graphs";
//	static String current_dataset = "gnm_graphs";
//	static String current_dataset = "bara_alb_graphs";
//	static String current_dataset = "watts_strog_graphs";
	// ++++++++++ Settings done +++++++++
	
	// +++++++ RESULT CONTAINERS +++++++
	// Init timers
	// Formulas (like vertex-cover)
	static ArrayList<Formula> forms = new ArrayList<Formula>();
	static ArrayList<Integer> c_list = new ArrayList<Integer>();
	static ArrayList<Integer> dens_list = new ArrayList<Integer>();
	// Successfully reduced hypergraphs
	static ArrayList<Hypergraph> reduced_graphs = new ArrayList<Hypergraph>();
	// Result lists
	static ArrayList<String> graph_names = new ArrayList<String>();
	static ArrayList<Integer> graph_sizes = new ArrayList<Integer>();
	// Reduction
	static ArrayList<Integer> reduced_nodes = new ArrayList<Integer>();
	static ArrayList<Integer> reduced_edges = new ArrayList<Integer>();
	static ArrayList<Double> reduction_times = new ArrayList<Double>();
	// Heuristics
	static ArrayList<Integer> heuristic_nodes = new ArrayList<Integer>();
	static ArrayList<Integer> heuristic_edges = new ArrayList<Integer>();
	static ArrayList<Double> heuristic_times = new ArrayList<Double>();
	// Pipe 1: SearchTree
	static ArrayList<Double> search_tree_times = new ArrayList<Double>();
	static ArrayList<Boolean> search_tree_results = new ArrayList<Boolean>();
	static ArrayList<Boolean> pipe_1_timeouts = new ArrayList<Boolean>();
	static ArrayList<Long> pipe_1_time_used_per_instance = new ArrayList<Long>();
	// Pipe 2: Kernel
	static ArrayList<Integer> kernel_nodes = new ArrayList<Integer>();
	static ArrayList<Integer> kernel_edges = new ArrayList<Integer>();
	static ArrayList<Double> kernel_times = new ArrayList<Double>();
	static ArrayList<Boolean> ke_results = new ArrayList<Boolean>();
	// Pipe 2: Hitting-Set Search Tree
	static ArrayList<Double> hs_times = new ArrayList<Double>();
	static ArrayList<Boolean> pipe_2_timeouts = new ArrayList<Boolean>();
	static ArrayList<Long> pipe_2_time_used_per_instance = new ArrayList<Long>();
	// Keep track of graphs that have been solved already (only used when
	// accumulate_time_over_k is set)
	static HashSet<String> solved_graphs = new HashSet<String>();
	static HashSet<String> timed_out_graphs = new HashSet<String>();
	// Save the smallest k that solved a graph
	static HashMap<String, Integer> solution_k = new HashMap<String, Integer>();
	// Check lower bounds of graphs so we don't waste time with k = 1
	static int first_relevant_k = stop_k;
	static HashMap<String, Integer> lower_bounds_per_graph = new HashMap<String, Integer>();
	// Heuristics add elements to the solution, therefore the remaining k must be
	// lowered
	static HashMap<String, Integer> k_used_in_heuristics_after_reduction_per_graph = new HashMap<String, Integer>();
	// +++++++ RESULT CONTAINERS DONE +++++++

	public static void main(String[] args) {
		// Get time stamp for the name of the result file
		long main_init_time = System.currentTimeMillis();

		// Construct paths to input directories
		String graph_dir_path = "";
		String form_dir_path = "";
		if (call_from_cmd) {
			graph_dir_path = ".." + File.separator + "input_graphs" + File.separator + current_dataset;
			form_dir_path = ".." + File.separator + "instances";
		} else {
			graph_dir_path = "input_graphs" + File.separator + current_dataset;
			form_dir_path = "instances";
		}

		// Collect and sort files
		File graph_folder = new File(graph_dir_path);
		File form_folder = new File(form_dir_path);
		File[] graph_files = graph_folder.listFiles();
		File[] form_files = form_folder.listFiles();

		// Sort files by their size (nr of nodes)
		if (sort_by_nodes) {
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
		}
		// Otherwise sort by file name
		else {
			Arrays.sort(graph_files, new Comparator<File>() {
				@Override
				public int compare(File file_1, File file_2) {
					return file_1.getName().compareTo(file_2.getName());
				}
			});
		}

		// Prints
		System.out.println("> Constructing formulas with instances and reducing them to hypergraphs.");
		// Construct Formulas and reduce graphs (+ heuristics) (only once for all k)
		constructAndReduce(graph_files, form_files);
		System.out.println("-------");
		
		// Skip both pipelines if skip_solution is set
		if(!skip_solution) {			
			// Init time_used array for the first pipeline
			if (!skip_search_tree) {
				while (pipe_1_time_used_per_instance.size() < forms.size()) {
					pipe_1_time_used_per_instance.add((long) 0);
				}
			}
	
			// Iterate over k
			for (int k_par = start_k; k_par <= stop_k; k_par += k_increment) {
				// Pipeline 1: Solve formulas with SearchTree
				if (!skip_search_tree) {
					startNormalSearchTree(k_par);
				}
	
				// Pipeline 2: Kernelize + HS-Search
				for (int j = 0; j < reduced_graphs.size(); j++) {
					// Timeout happened during reduction or during previous kernelization with
					// smaller k
					if (reduced_graphs.get(j) == null || timed_out_graphs.contains(reduced_graphs.get(j).hypergraph_name)) {
						kernel_times.add((double) 0);
						kernel_edges.add(-1);
						kernel_nodes.add(-1);
						hs_times.add((double) 0);
						ke_results.add(false); // false for timeout					
						pipe_2_timeouts.add(true); // note timeout
					}
					// If the graph has already been solved and we don't want to keep solving for bigger k
					else if (accumulate_time_over_k && solved_graphs.contains(reduced_graphs.get(j).hypergraph_name)) {
						// Make empty entries and go to next graph
						kernel_times.add((double) 0);
						kernel_edges.add(-1);
						kernel_nodes.add(-1);
						hs_times.add((double) 0);
						ke_results.add(true); // true for already solved
					}
					// If k is below the lower bound of this graph
					else if (k_par < lower_bounds_per_graph.get(reduced_graphs.get(j).hypergraph_name)) {
						// Make empty entries and go to next graph
						kernel_times.add((double) 0);
						kernel_edges.add(-1);
						kernel_nodes.add(-1);
						hs_times.add((double) 0);
						ke_results.add(false); // false for below lower bound
					}
					// Kernelization
					else {
						// Kernelize
						Hypergraph curr_redu_graph = reduced_graphs.get(j);
						Hypergraph curr_kernel = startKernelizer(k_par, curr_redu_graph, j);
						
						// If kernelization was successful, go to HS Search
						if(curr_kernel !=null) {
							startHiSeSearchTree(k_par, j, curr_kernel);						
						}
						// Timeout during kernelization
						else {
							hs_times.add((double) 0);
						}
						
					} // End of Kernelization
					// If nothing timed out, note false
					pipe_2_timeouts.add(false);
					// When all graphs are done, leave 
					if(solved_graphs.size() == reduced_graphs.size() && accumulate_time_over_k) {
						break;
					}
				} // End of Pipeline 2
			} // End of loop through k
		} // End of solutions

		// Collect and save results
		System.out.println("------");
		collectResults(main_init_time);
		System.out.println("Done.");
	}

	private static void constructAndReduce(File[] graph_files, File[] form_files) {
		for (int i = 0; i < form_files.length; i++) {
			String form_path = form_files[i].getAbsolutePath();
			for (int j = 0; j < graph_files.length; j++) {
				Long pipe_2_time_used = (long) 0;
				String curr_graph_path = graph_files[j].getAbsolutePath();
				int curr_graph_size = graphSize(curr_graph_path);
				// Only take graphs, that are small enough
				if (curr_graph_size <= max_graph_size || max_graph_size == -1) {
					// Only test a single graph
					if (only_single_graph) {
						if (!curr_graph_path.contains(single_graph_name)) {
							continue;
						}
					}
					// Only test the first x graphs
					if (only_first_x_graphs && j >= number_of_graphs_to_test) {
						continue;
					}
					graph_names.add(graph_files[j].getName());
					graph_sizes.add(curr_graph_size);
					// Constructing Formula
					Formula curr_formula = new Formula(form_path, curr_graph_path);
					System.out.println("  Accepted \"" + graph_files[j].getName() + "\" with " + curr_graph_size
							+ " nodes on formula \"" + curr_formula.formula_name + "\".");
					forms.add(curr_formula);
					c_list.add(curr_formula.c_par);
					dens_list.add(curr_formula.graph_density);
					// Reducing Formula to Hypergraph
					System.out.print("> Reduction");
					long redu_start_time = System.currentTimeMillis();
					long reduction_timeout = redu_start_time + timeout_value;
					// Reduce
					Hypergraph reduced_graph = null;
					try {
						if (curr_formula.guard_rel_id == null) {
							System.out.println(" (without guard), " + curr_formula.nr_of_assignments + " assignments");
							reduced_graph = curr_formula.reduceToHsWoGuard(mute, reduction_timeout);
						} else {
							int nr_of_guard_assignments = curr_formula.relation_map
									.get(curr_formula.guard_rel_id).elements.size();
							System.out.println(" (with guard), " + nr_of_guard_assignments + " assignments");
							reduced_graph = curr_formula.reduceToHsWithGuard(mute, reduction_timeout);
						}
					} catch (TimeoutException e) {
						System.out.println("! Reduction timed out.");
					}
					long redu_stop_time = System.currentTimeMillis();
					// Process results
					double redu_time_passed = (double) (redu_stop_time  - redu_start_time ) / (double) 1000;
					reduction_times.add(redu_time_passed);
					pipe_2_time_used = redu_stop_time  - redu_start_time ;
					printTime(redu_time_passed);
	
					// Handle timeout
					if (reduced_graph == null) {
						// Add null, handle this during evaluation
						reduced_graphs.add(null);
						reduced_edges.add(-1);
						reduced_nodes.add(-1);
						// Make -1 entries for heuristics
						heuristic_times.add((double) -1);
						heuristic_edges.add(-1);
						heuristic_nodes.add(-1);
					}
	
					// If reduction was successful for the j-th graph
					else {
						// Save data about reduction
						reduced_graphs.add(reduced_graph);
						reduced_edges.add(reduced_graph.edges.size());
						reduced_nodes.add(actualArraySize(reduced_graph.nodes));
	
						// Use heuristics to shrink the graph
						if (use_heuristics_after_reduction) {
							System.out.print("> Heuristics, ");
							long heur_start_time = System.currentTimeMillis();
							long heuristic_timeout = heur_start_time + timeout_value - pipe_2_time_used;
							boolean done = false;
							int k_decrease = 0;
							try {
								while (!done) {
									// Remove dangling nodes
									int nodes_removed = 0;
									nodes_removed = reduced_graph.removeDanglingNodes(mute, heuristic_timeout);
									// Remove singletons
									int singletons_removed = reduced_graph.removeSingletons(mute, heuristic_timeout);
									k_decrease += singletons_removed;
									if (singletons_removed == 0 && nodes_removed == 0)
										done = true;
								}
							} catch (TimeoutException e) {
								System.out.println("! Heuristics timed out.");
							}
							// Add k_decrease to list
							k_used_in_heuristics_after_reduction_per_graph.put(reduced_graph.hypergraph_name,
									k_decrease);
							System.out.println("k used: " + k_decrease);
	
							// Process results
							long heur_stop_time = System.currentTimeMillis();
							double heur_time_passed = (double) (heur_stop_time - heur_start_time) / (double) 1000;
							heuristic_times.add(heur_time_passed);
							pipe_2_time_used += heur_stop_time - heur_start_time;
							printTime(heur_time_passed);
							// If heuristics did not time out
							if (done) {
								// Save data about heuristics
								heuristic_edges.add(reduced_graph.edges.size());
								heuristic_nodes.add(actualArraySize(reduced_graph.nodes));
							}
							// If heuristics timed out
							else {
								// Make -1 entries
								heuristic_edges.add(-1);
								heuristic_nodes.add(-1);
							}
						}
						// If we do not even use heuristics
						else {
							// Make -1 entries
							heuristic_times.add((double) -1);
							heuristic_edges.add(-1);
							heuristic_nodes.add(-1);
						}
	
						// Calculate and save lower bound for k of this graph
						ArrayList<Tuple> disj_edges = reduced_graph.findMaxDisjEdges(reduced_graph.edges);
						int lower_bound_k = disj_edges.size();
						lower_bounds_per_graph.put(reduced_graph.hypergraph_name, lower_bound_k);
	
					}
				}
				// Graph is too big
				else if (!mute) {
					System.out.println(
							"  Discarded " + graph_files[j].getName() + " with " + curr_graph_size + " nodes.");
				}
				// Add time used for reduction (+ Heuristics)
				pipe_2_time_used_per_instance.add(pipe_2_time_used);
			}
			// TODO only use the first formula
			// break;
		} // Reduction over
	}

	private static void startNormalSearchTree(int k_par) {
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
			long st_start_time = System.currentTimeMillis();
			long st_timeout = 0;
			if (!accumulate_time_over_k) {
				st_timeout = st_start_time + timeout_value;
			} else {
				st_timeout = st_start_time + timeout_value - pipe_1_time_used_per_instance.get(j);
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
			long st_stop_time = System.currentTimeMillis();
			if (!mute)
				System.out.println();
	
			// Process results
			System.out.println("  result: " + st_result);
			search_tree_results.add(st_result);
			double st_time_passed = (double) (st_stop_time - st_start_time) / (double) 1000;
			search_tree_times.add(st_time_passed);
	
			// Update time passed
			pipe_1_time_used_per_instance.set(j,
					pipe_1_time_used_per_instance.get(j) + (st_stop_time - st_start_time));
			printTime(st_time_passed);
		}
	}

	private static Hypergraph startKernelizer(int k_par, Hypergraph curr_redu_graph, int graph_index) {		
		if (k_par < first_relevant_k) {
			// Save the first k we used for later (result printing)
			first_relevant_k = k_par;
		} else {
			// Only print if there is a graph for this
			System.out.println("------");
		}
		if (use_heuristics_after_reduction) {
			int k_after_heuristics = k_par + k_used_in_heuristics_after_reduction_per_graph
					.get(curr_redu_graph.hypergraph_name);
			System.out.println("> Kernelization, " + curr_redu_graph.hypergraph_name + ", k = " + k_par
					+ ", actual k = " + k_after_heuristics);
		} else {
			System.out.println("> Kernelization, " + curr_redu_graph.hypergraph_name + ", k = " + k_par);
		}

		// Set timer
		Hypergraph curr_kernel = null;
		long kernel_start_time = System.currentTimeMillis();
		long kernel_timeout = 0;
		if (!accumulate_time_over_k) {
			kernel_timeout = kernel_start_time + reduction_times.get(graph_index).longValue() + timeout_value;
		} else {
			kernel_timeout = kernel_start_time + timeout_value - pipe_2_time_used_per_instance.get(graph_index);
		}

		// Kernelize
		try {
			// Copy reduced graph to kernel
			curr_kernel = curr_redu_graph.copyThis();
			// Kernelize graph
			if (use_bevern_kernel) {
				curr_kernel = curr_kernel.kernelizeBevern(k_par, mute, kernel_timeout);
			} else {
				curr_kernel = curr_kernel.kernelizeUniform(k_par, mute, kernel_timeout);
			}
		} catch (TimeoutException e) {
			long timeout_stop = System.currentTimeMillis();
			double additional_time = (double) ((double) (timeout_stop - kernel_start_time) / 1000);
			System.out.println("! Kernelize timed out after additional "
					+ String.format("%.3f", additional_time) + " sec.");
			pipe_2_timeouts.add(true);
			timed_out_graphs.add(curr_kernel.hypergraph_name);
			kernel_edges.add(-1);
			kernel_nodes.add(-1);
			ke_results.add(false);
		}
		long kernel_stop_time = System.currentTimeMillis();

		// Prints
		if (!mute) {
			System.out.println("  hyp edges:     " + curr_redu_graph.edges.size());
			System.out.println("  hyp nodes:     " + actualArraySize(curr_redu_graph.nodes));
			int edges_removed = curr_redu_graph.edges.size() - curr_kernel.edges.size();
			System.out.println("  edges removed: " + edges_removed);
			int nodes_removed = actualArraySize(curr_redu_graph.nodes)
					- actualArraySize(curr_kernel.nodes);
			System.out.println("  nodes removed: " + nodes_removed);
			System.out.println("  kernel edges:  " + curr_kernel.edges.size());
			System.out.println("  kernel nodes:  " + actualArraySize(curr_kernel.nodes));
			long sf_lemma_boundary = factorial(curr_redu_graph.d_par)
					* (long) Math.pow(k_par, curr_redu_graph.d_par);
			System.out.println("  Lemma d!*k^d:  " + sf_lemma_boundary);
		}

		// Note time used
		double kernel_time_passed = (double) (kernel_stop_time - kernel_start_time) / (double) 1000;
		kernel_times.add(kernel_time_passed);
		pipe_2_time_used_per_instance.set(graph_index,
				pipe_2_time_used_per_instance.get(graph_index) + (kernel_stop_time - kernel_start_time));
		printTime(kernel_time_passed);
		return curr_kernel;
	}

	private static void startHiSeSearchTree(int k_par, int graph_index, Hypergraph curr_kernel) {
		// Add results
		kernel_edges.add(curr_kernel.edges.size());
		kernel_nodes.add(actualArraySize(curr_kernel.nodes));

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
		System.out.println("- Nodes, Edges left: " + actualArraySize(curr_kernel.nodes) + ", "
				+ curr_kernel.edges.size());
		System.out.print("> HS-SearchTree, k = " + k_par + " ");
		boolean hs_result = false;
		// Set timer
		long hs_start_time = System.currentTimeMillis();
		long hs_timeout = 0;
		if (!accumulate_time_over_k) {
			hs_timeout = hs_start_time + reduction_times.get(graph_index).longValue()
					+ kernel_times.get(graph_index).longValue() + timeout_value;
		} else {
			hs_timeout = hs_start_time + timeout_value - pipe_2_time_used_per_instance.get(graph_index);
		}

		// Start HS-SearchTree
		try {
			hs_result = curr_kernel.hsSearchTree(k_par, new HashSet<Integer>(), mute, hs_timeout,
					use_branch_and_bound);
			if (!mute)
				System.out.println("\n");
			if (hs_result) {
				// Calc total k used by heuristics
				int k_used_by_heur = 0;
				if (use_heuristics_after_reduction) {
					k_used_by_heur += k_used_in_heuristics_after_reduction_per_graph
							.get(curr_kernel.hypergraph_name);
				}
				int actual_k = k_par + k_used_by_heur;
				solution_k.put(curr_kernel.hypergraph_name, actual_k);
				System.out.println("  TRUE");
			} else {
				System.out.println();
			}
		} catch (TimeoutException e) {
			long emergency_stop = System.currentTimeMillis();
			double additional_time = (double) ((double) (emergency_stop - hs_start_time) / 1000);
			System.out.println("! HS-SearchTree timed out after additional "
					+ String.format("%.3f", additional_time) + " sec.");
			pipe_2_timeouts.add(true);
			timed_out_graphs.add(curr_kernel.hypergraph_name);
		}
		long hs_stop_time = System.currentTimeMillis();

		// Add results
		ke_results.add(hs_result);
		double hs_time_passed = (double) (hs_stop_time - hs_start_time) / (double) 1000;
		hs_times.add(hs_time_passed);
		pipe_2_time_used_per_instance.set(graph_index,
				pipe_2_time_used_per_instance.get(graph_index) + (hs_stop_time - hs_start_time));
		printTime(hs_time_passed);
		if (hs_result) {
			// Note that the current graph has been solved
			solved_graphs.add(curr_kernel.hypergraph_name);
		}
	}

	private static void collectResults(long main_init_time) {
		if (!mute)
			System.out.println("\n------------------------------------");
		int number_of_results = Math.max(search_tree_results.size(), ke_results.size());
		// Init write buffer
		ArrayList<String> write_buffer = new ArrayList<String>();
		String[] headline = new String[] {
				"file",
				"n",
				"c_par",
				"density",
				"k",
				"Pipe 1 time",
				"Pipe 1 result",
				"Reduction time",
				"Reduced nodes",
				"Reduced edges",
				"Heuristics time",
				"Heuristics nodes",
				"Heuristics edges",
				"Kernel time",
				"Kernel nodes",
				"Kernel edges",
				"HS-ST time",
				"Pipe 2 time",
				"Pipe 2 result",
				"Solution k",
				"Time to solve",
				"Pipe 1 == Pipe 2",
				"Pipe 1 timeout",
				"Pipe 2 timeout"
				};
		for(String s : headline) {
			write_buffer.add(s);			
		}
		int curr_k_par = first_relevant_k;
		boolean all_graphs_timed_out = true;
	
		// Loop over all results, but start at the first point of interest (i = nr_of...)
		int nr_of_irrelevant_entries = reduced_graphs.size() * (first_relevant_k - start_k);
		for (int i = nr_of_irrelevant_entries; i < number_of_results; i++) {
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
			int timeout_2 = pipe_2_timeouts.get(i) ? 1 : 0;
			double pipe_2_time = 0;
			int pipe_2_res = ke_results.get(i) ? 1 : 0;
			// pipe_2_time should be -1 when the instance timed out or is solved
			if (timeout_2 == 1.0) {
				pipe_2_time = -1;
			} else if (pipe_2_res == 1.0) {
				pipe_2_time = reduction_times.get(k_indep_index) + kernel_times.get(i) + hs_times.get(i);
			} else {
				pipe_2_time = reduction_times.get(k_indep_index) + kernel_times.get(i) + hs_times.get(i);
				all_graphs_timed_out = false;
			}
	
			// Collect results
			double pipe_1_time = -1;
			int pipe_1_res = -1;
			int equal_res = -1;
			int timeout_1 = -1;
			// With ST
			if (!skip_search_tree) {
				pipe_1_time = search_tree_times.get(i);
				pipe_1_res = search_tree_results.get(i) ? 1 : 0;
				equal_res = search_tree_results.get(i) == ke_results.get(i) ? 1 : 0;
				timeout_1 = pipe_1_timeouts.get(i) ? 1 : 0;
			}
			// Convert time used
			Double pipe_2_time_used = (double) pipe_2_time_used_per_instance.get(k_indep_index) / 1000;
			String curr_graph_name = graph_names.get(k_indep_index);
			// Get solution k
			int curr_solution_k = -1;
			if (solution_k.get(curr_graph_name) != null) {
				curr_solution_k = solution_k.get(curr_graph_name);
			}
			// Assemble print data
			write_buffer.add(curr_graph_name);
			write_buffer.add(Integer.toString(graph_sizes.get(k_indep_index)));
			write_buffer.add(Integer.toString(c_list.get(k_indep_index)));
			write_buffer.add(Integer.toString(dens_list.get(k_indep_index)));
			write_buffer.add(Integer.toString(curr_k_par));
			write_buffer.add(Double.toString(pipe_1_time));
			write_buffer.add(Integer.toString(pipe_1_res));
			write_buffer.add(Double.toString(reduction_times.get(k_indep_index)));
			write_buffer.add(Integer.toString(reduced_nodes.get(k_indep_index)));
			write_buffer.add(Integer.toString(reduced_edges.get(k_indep_index)));
			write_buffer.add(Double.toString(heuristic_times.get(k_indep_index)));
			write_buffer.add(Double.toString(heuristic_nodes.get(k_indep_index)));
			write_buffer.add(Double.toString(heuristic_edges.get(k_indep_index)));
			write_buffer.add(Double.toString(kernel_times.get(i)));
			write_buffer.add(Integer.toString(kernel_nodes.get(i)));
			write_buffer.add(Integer.toString(kernel_edges.get(i)));
			write_buffer.add(Double.toString(hs_times.get(i)));
			write_buffer.add(Double.toString(pipe_2_time));
			write_buffer.add(Integer.toString(pipe_2_res));
			write_buffer.add(Integer.toString(curr_solution_k));
			write_buffer.add(Double.toString(pipe_2_time_used));
			write_buffer.add(Integer.toString(equal_res));
			write_buffer.add(Integer.toString(timeout_1));
			write_buffer.add(Integer.toString(timeout_2));
	
			// Prepare next iteration of k and save to csv
			if ((i + 1) % forms.size() == 0) {
				// Save buffer to csv
				String file_name = Long.toString(main_init_time) + "_" + current_dataset + "_k_" + start_k + "-"
						+ stop_k + ".csv";
				writeLineToCsv(write_buffer, file_name, call_from_cmd, nr_of_columns);
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
	private static void writeLineToCsv(ArrayList<String> write_buffer, String file_name, boolean cmd,
			int number_of_columns) {
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
			for (int i = 0; i < write_buffer.size(); i++) {
				if (i != 0 && i % number_of_columns == 0) {
					bw.write("\n");
				}
				bw.write(write_buffer.get(i) + ";");
			}
			bw.write("\n");
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
		for (int elem : arr_with_minus_one) {
			if (elem != -1) {
				counter++;
			}
		}
		return counter;
	}

}
