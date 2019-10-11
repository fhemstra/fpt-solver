package pack;
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
import org.apache.commons.cli.*;

public class Main {
	// +++++++++++ Settings +++++++++++++
	// Set this if the software is called from cmd instead of eclipse
	static boolean call_from_cmd = true;
	// Set this to mute debug output
	static boolean mute = true;
	// Set timeout, 30 min: 1800000, 10 min: 600000, 5 min: 300000, 3 min: 180000, 1 min: 60000
	static long timeout_value = 60000;
	// Set to activate timeouts
	static boolean timeout_active = false;
	// Set to only test one graph
	static boolean only_single_graph = false;
	static String single_graph_name = "gnm_n_10_m_10_1.gr";
	// Set to test only the first x graphs
	static boolean only_first_x_graphs = false;
	static int number_of_graphs_to_test = 1;
	// Set range of k
	static int start_k = 0;
	static int k_increment = 1;
	static int stop_k = 10000;
	// Set this to discard big graphs, set to -1 to discard nothing
	static int max_graph_size = -1;
	// Set this to sort input graphs by their size ascending
	static boolean sort_by_nodes = true;
	// Set to skip both pipelines, only reducing graphs
	static boolean skip_pipe_2 = true;
	// Set this if the first pipeline should be skipped
	static boolean skip_pipe_1 = true;
	// Set to abandon branches of HS ST that contain a big matching
	static boolean use_branch_and_bound = false;
	// Set this to use heuristics on the result of kernelization to improve HS ST
	// runtime
	static boolean use_heuristics_after_reduction = false;
	// Set to use the bevern kernel before using the sf kernel
	static boolean use_bevern_kernel = false;
	// Set this if the timeout per graph should be accumulated over all k (for PACE)
	static boolean accumulate_time_over_k = true;
	// Select a dataset
//	static String graph_dataset = "pace";
//	static String graph_dataset = "k_star_graphs";
//	static String graph_dataset = "d_reg_graphs";
	static String graph_dataset = "gnm_graphs";
//	static String graph_dataset = "bara_alb_graphs";
//	static String graph_dataset = "watts_strog_graphs";
//	static String graph_dataset = "reference_set_d2";
	// Select formula set
	static String formula_set = "instances";
	// ++++++++++ Settings done +++++++++
	
	// +++++++ RESULT CONTAINERS +++++++
	// Init timers
	// Formulas (like vertex-cover)
	static ArrayList<Formula> forms = new ArrayList<Formula>(); 
	static HashMap<String, Integer> c_list = new HashMap<String, Integer>();
	static HashMap<String, Double> dens_list = new HashMap<String, Double>();
	// Successfully reduced hypergraphs
	static ArrayList<Hypergraph> reduced_graphs = new ArrayList<Hypergraph>();
	// Result lists
	static ArrayList<String> graph_names = new ArrayList<String>();
	static HashMap<String, Integer> graph_sizes = new HashMap<String, Integer>();
	// Reduction
	static HashMap<String, Integer> reduced_nodes = new HashMap<String, Integer>();
	static HashMap<String, Integer> reduced_edges = new HashMap<String, Integer>();
	static HashMap<String, Long> reduction_times = new HashMap<String, Long>();
	// Heuristics
	static HashMap<String, Integer> heuristic_nodes = new HashMap<String, Integer>();
	static HashMap<String, Integer> heuristic_edges = new HashMap<String, Integer>();
	static HashMap<String, Long> heuristic_times = new HashMap<String, Long>();
	// Pipe 1: SearchTree
	static HashMap<String, ArrayList<Long>> search_tree_times = new HashMap<String, ArrayList<Long>>();
	static HashMap<String, Boolean> search_tree_results = new HashMap<String, Boolean>();
	static HashMap<String, Boolean> pipe_1_timeouts = new HashMap<String, Boolean>();
	// Pipe 2: Kernel
	static HashMap<String, ArrayList<Integer>> kernel_nodes = new HashMap<String, ArrayList<Integer>>();
	static HashMap<String, ArrayList<Integer>> kernel_edges = new HashMap<String, ArrayList<Integer>>();
	static HashMap<String, ArrayList<Long>> kernel_times = new HashMap<String, ArrayList<Long>>();
	static HashMap<String, Boolean> ke_results = new HashMap<String, Boolean>();
	// Pipe 2: Hitting-Set Search Tree
	static HashMap<String, ArrayList<Long>> hs_times = new HashMap<String, ArrayList<Long>>();
	static HashMap<String, Boolean> pipe_2_timeouts = new HashMap<String, Boolean>();
	// Keep track of graphs that have been solved already (only used when
	// accumulate_time_over_k is set)
	static HashSet<String> pipe_1_solved_forms = new HashSet<String>();
	static HashSet<String> pipe_2_solved_graphs = new HashSet<String>();
	static HashSet<String> timed_out_graphs = new HashSet<String>();
	// Save the smallest k that solved a graph
	static HashMap<String, Integer> solution_k = new HashMap<String, Integer>();
	// Check lower bounds of graphs (kernels) so we don't waste time with k = 1
	static int first_relevant_k = stop_k;
	static HashMap<String, Integer> lower_bounds_per_graph = new HashMap<String, Integer>();
	// Also note actual lower bounds for results
	static HashMap<String, Integer> actual_lower_bounds_per_graph = new HashMap<String, Integer>();
	// Heuristics add elements to the solution, therefore the remaining k must be
	// lowered
	static HashMap<String, Integer> k_used_in_heuristics_per_graph = new HashMap<String, Integer>();
	// +++++++ RESULT CONTAINERS DONE +++++++

	public static void main(String[] args) {
		long main_init_time = System.currentTimeMillis();
		// Process input args 
		if(call_from_cmd) {
			handleInputArgs(args);
		} else {
			System.out.println("These binaries were built for use in the eclipse console. Recompile with updated settings.");
			// Collect new default values for debugging
			skip_pipe_2 = false;
			use_bevern_kernel = true;
		}
		
		// Construct paths to input directories
		String graph_dir_path = "";
		String form_dir_path = "";
		String prefix = "";
		if (call_from_cmd) {
			prefix = ".." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
		} else {
			prefix = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
		}
		graph_dir_path = prefix + "input_graphs" + File.separator + graph_dataset;
		form_dir_path = prefix  + formula_set;

		// Collect and sort files
		File graph_folder = new File(graph_dir_path);
		File form_folder = new File(form_dir_path);
		File[] graph_files = graph_folder.listFiles();
		File[] form_files = form_folder.listFiles();
		
		if(graph_files == null) {
			System.out.println("No graph files found.");
			return;
		}
		if(form_files == null) {
			System.out.println("No formula files found.");
			return;
		}
		
		// Apply filters if any are set
		File[] filtered_graph_files;
		if (only_single_graph || only_first_x_graphs) {
			// Filter files based on settings
			ArrayList<File> filtered_graph_files_list = new ArrayList<File>();
			for (int j = 0; j < graph_files.length; j++) {
				// Only test a single graph
				if (only_single_graph) {
					// Get filename from path
					String curr_graph_path = graph_files[j].getAbsolutePath();
					String[] path_split = curr_graph_path.split("\\\\");
					String file_name = path_split[path_split.length - 1];
					// Check if this is the specified file
					if (file_name.equals(single_graph_name)) {
						filtered_graph_files_list.add(graph_files[j]);
					}
				}
				// Only test the first x graphs
				if (only_first_x_graphs && j < number_of_graphs_to_test) {
					filtered_graph_files_list.add(graph_files[j]);
				}
			}
			// Leave, if there are no graph files
			if (filtered_graph_files_list.isEmpty()) {
				System.out.println("No graph files found.");
				return;
			}
			// Convert to Array
			filtered_graph_files = new File[filtered_graph_files_list.size()];
			for (int i = 0; i < filtered_graph_files_list.size(); i++) {
				filtered_graph_files[i] = filtered_graph_files_list.get(i);
			}
		}
		// Else don't filter anything
		else {
			filtered_graph_files = graph_files;
		}
		
		// Sort files by their size (nr of nodes)
		if (sort_by_nodes) {
			Arrays.sort(filtered_graph_files, new Comparator<File>() {
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
			Arrays.sort(filtered_graph_files, new Comparator<File>() {
				public int compare(File file_1, File file_2) {
					return file_1.getName().compareTo(file_2.getName());
				}
			});
		}

		// Prints
		System.out.println("> Constructing formulas with instances and reducing them to hypergraphs.");
		// Construct Formulas and reduce graphs (+ heuristics) (only once for all k)
		constructAndReduce(filtered_graph_files, form_files);
		System.out.println("-------");

		// Pipeline 1: Solve formulas with SearchTree
		if (!skip_pipe_1) {
			System.out.println("+++++ Pipe 1 +++++");
			for (int k_par = start_k; k_par <= stop_k; k_par += k_increment) {
				for (int j = 0; j < forms.size(); j++) {
					Formula curr_form = forms.get(j);
					// Skip solved forms
					if (!pipe_1_solved_forms.contains(curr_form.identifier)) {
						boolean st_res = startNormalSearchTree(k_par, curr_form);
						if (st_res) {
							pipe_1_solved_forms.add(curr_form.identifier);
						}
					}
				}
			}
		}

		// Skip pipe 2 if skip_pipe_2 is set
		if (!skip_pipe_2) {
			// Pipeline 2: Kernelize + HS-Search
			System.out.println("+++++ PIPE 2 ++++++");
			for (int k_par = start_k; k_par <= stop_k; k_par += k_increment) {
				boolean all_graphs_solved = false;
				for (int j = 0; j < reduced_graphs.size(); j++) {
					// If timeout happened during reduction or during previous kernelization with
					// smaller k
					if (reduced_graphs.get(j) == null) {
						continue;
					} else if(timed_out_graphs.contains(reduced_graphs.get(j).getIdentifier())) {
						continue;
					}
					// If the graph has already been solved and we don't want to keep solving for bigger k
					else if (accumulate_time_over_k && pipe_2_solved_graphs.contains(reduced_graphs.get(j).getIdentifier())) {
						// Make empty entries and go to next graph
						ke_results.put(reduced_graphs.get(j).getIdentifier(), true); // true for already solved
					}
					// If k is below the lower bound of this graph
					else if (k_par < lower_bounds_per_graph.get(reduced_graphs.get(j).getIdentifier())) {
						// Make empty entries and go to next graph
						ke_results.put(reduced_graphs.get(j).getIdentifier(), false); // false for below lower bound
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
					} // End of Kernelization
					// When all graphs are done, leave 
					if(pipe_2_solved_graphs.size() == reduced_graphs.size() && accumulate_time_over_k) {
						all_graphs_solved = true;
					}
				} // End of Pipeline 2
				// When all graphs are done, leave
				if(all_graphs_solved) {
					break;
				}
			} // End of loop through k
		} // End of solutions

		// Collect and save results
		System.out.println("------");
		collectResults();
		long main_terminate_time = System.currentTimeMillis();
		long total_time_passed = main_terminate_time - main_init_time;
		System.out.println("Done after " + formatTimeInSeconds(total_time_passed) + " seconds.");
	}

	/**
	 * Handles inputs given through CLI.
	 */
	private static void handleInputArgs(String[] args) {
		Options options = new Options();

		Option dataset_opt = new Option("g", "graph-set", true, "name of the directory containing graphs");
		dataset_opt.setRequired(true);
		options.addOption(dataset_opt);

		Option form_set_opt = new Option("f", "formula-set", true, "name of the directory containing formulas");
		form_set_opt.setRequired(false);
		options.addOption(form_set_opt);

		Option timeout_opt = new Option("t", "timeout", true, "timeout per graph-formula combination in seconds");
		timeout_opt.setRequired(false);
		options.addOption(timeout_opt);

		Option single_graph_opt = new Option("s", "single-graph", true, "specify the name of a single graph to test");
		single_graph_opt.setRequired(false);
		options.addOption(single_graph_opt);

		Option x_graphs_opt = new Option("x", "x-graphs", true, "only test the first x graphs");
		x_graphs_opt.setRequired(false);
		options.addOption(x_graphs_opt);

		Option skip_pipe_1_opt = new Option("st", "search-tree", false, "use the naive searchtree algorithm");
		skip_pipe_1_opt.setRequired(false);
		options.addOption(skip_pipe_1_opt);

		Option skip_pipe_2_opt = new Option("ke", "kernelization", false,
				"use the more sophisticated reduction and kernelization algorithm");
		skip_pipe_2_opt.setRequired(false);
		options.addOption(skip_pipe_2_opt);

		Option heuristics_opt = new Option("heu", "heuristics", false,
				"use ke together with heuristics to speed up the calculation");
		heuristics_opt.setRequired(false);
		options.addOption(heuristics_opt);

		Option upper_k_opt = new Option("k", "k-upper-bound", true, "the upper bound for the solution size k");
		upper_k_opt.setRequired(false);
		options.addOption(upper_k_opt);

		Option bevern_opt = new Option("b", "bevern-kernel", false,
				"use the bevern kernel before going into the normal kernel");
		bevern_opt.setRequired(false);
		options.addOption(bevern_opt);

		// Init parser
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		// Parse input
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);
			// Leave if there was an error
			System.exit(1);
		}

		// Process inputs
		graph_dataset = cmd.getOptionValue("graph-set");
		if (cmd.getOptionValue("formula-set") != null) {
			formula_set = cmd.getOptionValue("formula-set");
		}
		if (cmd.getOptionValue("timeout") != null) {
			timeout_active = true;
			// Handle decimal numbers
			Double input_value = Double.parseDouble(cmd.getOptionValue("timeout"));
			// Timeout is given in seconds but processed in milliseconds (* 1000)
			input_value *= 1000;
			timeout_value = input_value.longValue();
		}
		if (cmd.getOptionValue("single-graph") != null) {
			only_single_graph = true;
			single_graph_name = cmd.getOptionValue("single-graph");
		}
		if (cmd.getOptionValue("x-graphs") != null) {
			only_first_x_graphs = true;
			number_of_graphs_to_test = Integer.parseInt(cmd.getOptionValue("x-graphs"));
		}
		if (cmd.hasOption("st")) {
			skip_pipe_1 = false;
		}
		if (cmd.hasOption("ke")) {
			skip_pipe_2 = false;
		}
		if (cmd.hasOption("heu")) {
			skip_pipe_2 = false;
			use_heuristics_after_reduction = true;
			use_branch_and_bound = true;
		}
		if (cmd.getOptionValue("k") != null) {
			stop_k = Integer.parseInt(cmd.getOptionValue("k"));
		}
		if (cmd.hasOption("bevern-kernel")) {
			skip_pipe_2 = false;
			use_bevern_kernel = true;
		}
	}

	private static void constructAndReduce(File[] graph_files, File[] form_files) {
		for (int i = 0; i < form_files.length; i++) {
			String form_path = form_files[i].getAbsolutePath();
			for (int j = 0; j < graph_files.length; j++) {
				String curr_graph_path = graph_files[j].getAbsolutePath();
				int curr_graph_size = graphSize(curr_graph_path);
				String curr_graph_file_name = graph_files[j].getName();
				String curr_graph_name = curr_graph_file_name.split("\\.")[0];
				// Only take graphs, that are small enough
				if (curr_graph_size <= max_graph_size || max_graph_size == -1) {
					Long redu_time_long = (long) 0;
					graph_names.add(curr_graph_name);
					graph_sizes.put(curr_graph_name, curr_graph_size);
					// Constructing Formula
					Formula curr_formula = new Formula(form_path, curr_graph_path);
					System.out.println("  Accepted \"" + graph_files[j].getName() + "\" with " + curr_graph_size
							+ " nodes on formula \"" + curr_formula.formula_name + "\".");
					forms.add(curr_formula);
					c_list.put(curr_formula.formula_name, curr_formula.c_par);
					dens_list.put(curr_formula.formula_name, curr_formula.graph_density);
					// Reducing Formula to Hypergraph
					System.out.print("> Reduction");
					long redu_start_time = System.currentTimeMillis();
					long reduction_timeout = redu_start_time + timeout_value;
					// Reduce
					Hypergraph reduced_graph = null;
					try {
						if (curr_formula.guard_rel_id == null) {
							System.out.println(" (without guard), " + curr_formula.nr_of_assignments + " assignments");
							reduced_graph = curr_formula.reduceToHsWoGuard(mute, reduction_timeout, timeout_active);
						} else {
							int nr_of_guard_assignments = curr_formula.relation_map
									.get(curr_formula.guard_rel_id).elements.size();
							System.out.println(" (with guard), " + nr_of_guard_assignments + " assignments");
							reduced_graph = curr_formula.reduceToHsWithGuard(mute, reduction_timeout, timeout_active);
						}
					} catch (TimeoutException e) {
						// Supplement graph identifier
						pipe_2_timeouts.put(curr_graph_file_name + "," + curr_formula.formula_name, true);
						System.out.println("! Reduction timed out.");
					}
					long redu_stop_time = System.currentTimeMillis();
	
					// If there was no timeout
					if (reduced_graph != null) {
						// Process results
						long redu_time_passed = redu_stop_time - redu_start_time;
						reduction_times.put(reduced_graph.getIdentifier(), redu_time_passed);
						redu_time_long = redu_stop_time  - redu_start_time;
						printTime(redu_time_passed);
						// Save data about reduction
						reduced_graphs.add(reduced_graph);
						reduced_edges.put(reduced_graph.getIdentifier(), reduced_graph.edges.size());
						reduced_nodes.put(reduced_graph.getIdentifier(), actualArraySize(reduced_graph.nodes));
	
						// Use heuristics to shrink the graph
						if (use_heuristics_after_reduction) {
							System.out.print("> Heuristics, ");
							long heur_start_time = System.currentTimeMillis();
							long heuristic_timeout = heur_start_time + timeout_value - redu_time_long;
							boolean done = false;
							int k_decrease = 0;
							try {
								while (!done) {
									// Remove dangling nodes
									int nodes_removed = 0;
									nodes_removed = reduced_graph.removeDanglingNodes(mute, heuristic_timeout, timeout_active);
									// Remove singletons
									int singletons_removed = reduced_graph.removeSingletons(mute, heuristic_timeout);
									k_decrease += singletons_removed;
									if (singletons_removed == 0 && nodes_removed == 0)
										done = true;
								}
								System.out.println("k used: " + k_decrease);
							} catch (TimeoutException e) {
								System.out.println("! Heuristics timed out.");
							}
							// Add k_decrease to list
							k_used_in_heuristics_per_graph.put(reduced_graph.getIdentifier(),
									k_decrease);
	
							// If heuristics did not time out
							if (done) {
								// Process results
								long heur_stop_time = System.currentTimeMillis();
								long heur_time_passed = heur_stop_time - heur_start_time;
								heuristic_times.put(reduced_graph.getIdentifier(), heur_time_passed);
								printTime(heur_time_passed);
								// Save data about heuristics
								heuristic_edges.put(reduced_graph.getIdentifier(), reduced_graph.edges.size());
								heuristic_nodes.put(reduced_graph.getIdentifier(), actualArraySize(reduced_graph.nodes));
							}
							// If heuristics timed out, don't do anything
						}
						// If we do not even use heuristics, don't do anything
						// Calculate and save lower bound for k of this graph
						ArrayList<Tuple> disj_edges = reduced_graph.findMaxDisjEdges(reduced_graph.edges);
						int lower_bound_k = disj_edges.size();
						lower_bounds_per_graph.put(reduced_graph.getIdentifier(), lower_bound_k);
						if (k_used_in_heuristics_per_graph.get(reduced_graph.getIdentifier()) != null) {
							actual_lower_bounds_per_graph.put(reduced_graph.getIdentifier(),
									lower_bound_k + k_used_in_heuristics_per_graph.get(reduced_graph.getIdentifier()));
						} else {
							actual_lower_bounds_per_graph.put(reduced_graph.getIdentifier(),
									lower_bound_k);
						}
					}
				}
				// Graph is too big
				else if (!mute) {
					System.out.println(
							"  Discarded " + graph_files[j].getName() + " with " + curr_graph_size + " nodes.");
				}
			}
			// TODO only use the first formula
			// break;
		} // Reduction over
	}

	private static boolean startNormalSearchTree(int k_par, Formula curr_form) {
		boolean st_result = false;		
		String curr_name = curr_form.identifier;
		System.out.println(
				"> SearchTree, " + curr_name + ", k = " + k_par);

		// Generate first assignment
		int[] start_assignment = new int[curr_form.c_par];
		for (int i = 0; i < curr_form.c_par; i++) {
			start_assignment[i] = curr_form.universe[0];
		}

		// Set timer
		long st_start_time = System.currentTimeMillis();
		long st_timeout = st_start_time + timeout_value;
		if (accumulate_time_over_k && search_tree_times.get(curr_name) != null) {
			Double prev_time_used = 0.0;
			for(long time : search_tree_times.get(curr_name)) {
				prev_time_used += time;
			}
			st_timeout -= prev_time_used;
		}
		// SearchTree
		try {
			st_result = curr_form.searchTree(k_par, new ArrayList<Integer>(), mute, start_assignment, 0,
					st_timeout, timeout_active);
			pipe_1_timeouts.put(curr_name, false);
		} catch (TimeoutException e) {
			System.out.println("! ST timed out.");
			pipe_1_timeouts.put(curr_name, true);
		}
		long st_stop_time = System.currentTimeMillis();
		if (!mute)
			System.out.println();

		// Process results
		if(st_result) System.out.println("  TRUE");
		search_tree_results.put(curr_name, st_result);
		long st_time_passed = st_stop_time - st_start_time;
		if(search_tree_times.get(curr_name) != null) {
			ArrayList<Long> used_time = search_tree_times.get(curr_name);
			used_time.add(st_time_passed);
			search_tree_times.put(curr_name, used_time);
		} else {
			ArrayList<Long> used_time = new ArrayList<Long>();
			used_time.add(st_time_passed);
			search_tree_times.put(curr_name, used_time);				
		}
		printTime(st_time_passed);
		return st_result;
	}

	private static Hypergraph startKernelizer(int k_par, Hypergraph curr_redu_graph, int graph_index) {	
		String curr_name = curr_redu_graph.getIdentifier();
		if (k_par < first_relevant_k) {
			// Save the first k we used for later (result printing)
			first_relevant_k = k_par;
		} else {
			// Only print if there is a graph for this
			System.out.println("------");
		}
		if (use_heuristics_after_reduction) {
			int k_after_heuristics = k_par + k_used_in_heuristics_per_graph
					.get(curr_name);
			System.out.println("> Kernelization, " + curr_name + ", k = " + k_par
					+ ", actual k = " + k_after_heuristics);
		} else {
			System.out.println("> Kernelization, " + curr_name + ", k = " + k_par);
		}

		// Set timer
		Hypergraph curr_kernel = null;
		long kernel_start_time = System.currentTimeMillis();
		// Start + Redu + timeout
		long kernel_timeout = kernel_start_time + timeout_value;
		if(reduction_times.get(curr_name) != null) {
			kernel_timeout -= reduction_times.get(curr_name);
		}
		if(heuristic_times.get(curr_name) != null) {
			kernel_timeout -= heuristic_times.get(curr_name);			
		}
		// Add previously used time
		if (accumulate_time_over_k) {
			long prev_kernel_time = 0;
			if (kernel_times.get(curr_name) != null) {
				for (long time : kernel_times.get(curr_name)) {
					prev_kernel_time += time;
				}
			}
			kernel_timeout -= prev_kernel_time;
		}

		// Kernelize
		try {
			// Copy reduced graph to kernel
			curr_kernel = curr_redu_graph.copyThis();
			// Kernelize graph
			if (use_bevern_kernel) {
				curr_kernel = curr_kernel.kernelizeBevern(k_par, mute, kernel_timeout, timeout_active);
			}
			curr_kernel = curr_kernel.kernelizeUniform(k_par, mute, kernel_timeout, timeout_active);
		} catch (TimeoutException e) {
			long timeout_stop = System.currentTimeMillis();
			double additional_time = (double) ((double) (timeout_stop - kernel_start_time) / 1000);
			System.out.println("! Kernelize timed out after additional "
					+ String.format("%.3f", additional_time) + " sec.");
			pipe_2_timeouts.put(curr_name, true);
			timed_out_graphs.add(curr_kernel.getIdentifier());
			ke_results.put(curr_name, false);
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
		long kernel_time_passed = kernel_stop_time - kernel_start_time;
		ArrayList<Long> time_passed_previously = kernel_times.get(curr_name);
		// Add time to already used kernel time
		if(time_passed_previously == null) {
			ArrayList<Long> empty = new ArrayList<Long>();
			empty.add(kernel_time_passed);
			kernel_times.put(curr_name, empty);
		} else {
			ArrayList<Long> previously_used = kernel_times.get(curr_name);
			previously_used.add(kernel_time_passed);
			kernel_times.put(curr_name, previously_used);
		}
		printTime(kernel_time_passed);
		return curr_kernel;
	}

	private static void startHiSeSearchTree(int k_par, int graph_index, Hypergraph curr_kernel) {
		String curr_name = curr_kernel.getIdentifier();
		// If there were previous results, add to them
		if (kernel_edges.get(curr_name) != null) {
			// Add results
			ArrayList<Integer> prev_kernel_edges = kernel_edges.get(curr_name);
			prev_kernel_edges.add(curr_kernel.edges.size());
			kernel_edges.put(curr_name, prev_kernel_edges);
			ArrayList<Integer> prev_kernel_nodes = kernel_nodes.get(curr_name);
			prev_kernel_nodes.add(actualArraySize(curr_kernel.nodes));
			kernel_nodes.put(curr_name, prev_kernel_nodes);
		}
		// Else, init new lists
		else {
			ArrayList<Integer> tmp_edges = new ArrayList<Integer>();
			tmp_edges.add(curr_kernel.edges.size());
			kernel_edges.put(curr_name, tmp_edges);
			ArrayList<Integer> tmp_nodes = new ArrayList<Integer>();
			tmp_nodes.add(actualArraySize(curr_kernel.nodes));
			kernel_nodes.put(curr_name, tmp_nodes);
		}
		// Sort edges of current_kernel to make the SearchTree faster
		curr_kernel.edges.sort(new Comparator<Tuple>() {
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
		long hs_timeout = hs_start_time + timeout_value;
		if(reduction_times.get(curr_name) != null) {
			hs_timeout -= reduction_times.get(curr_name);
		}
		if(heuristic_times.get(curr_name) != null) {
			hs_timeout -= heuristic_times.get(curr_name);			
		}
		// Calc sum of previous times
		if (accumulate_time_over_k) {
			long prev_time = 0;
			if (kernel_times.get(curr_name) != null) {
				for (long time : kernel_times.get(curr_name)) {
					prev_time += time;
				}
			}
			if (hs_times.get(curr_name) != null) {
				for (long time : hs_times.get(curr_name)) {
					prev_time += time;
				}
			}
			hs_timeout -= prev_time;
		}

		// Start HS-SearchTree
		try {
			hs_result = curr_kernel.hsSearchTree(k_par, new HashSet<Integer>(), mute, hs_timeout,
					use_branch_and_bound, timeout_active);
			if (!mute)
				System.out.println("\n");
			if (hs_result) {
				// Calc total k used by heuristics
				int k_used_by_heur = 0;
				if (use_heuristics_after_reduction) {
					k_used_by_heur += k_used_in_heuristics_per_graph.get(curr_name);
				}
				int actual_k = k_par + k_used_by_heur;
				solution_k.put(curr_name, actual_k);
				System.out.println("  TRUE");
				// Note that the current graph has been solved
				pipe_2_solved_graphs.add(curr_kernel.getIdentifier());
			} else {
				System.out.println();
			}
			// Add results
			long hs_stop_time = System.currentTimeMillis();
			ke_results.put(curr_name, hs_result);
			long hs_time_passed = hs_stop_time - hs_start_time;
			if (hs_times.get(curr_name) != null) {
				// Update list
				ArrayList<Long> prev_hs_times = hs_times.get(curr_name);
				prev_hs_times.add(hs_time_passed);
				hs_times.put(curr_name, prev_hs_times);
			}
			else {
				// Make new list
				ArrayList<Long> tmp_hs_times = new ArrayList<Long>();
				tmp_hs_times.add(hs_time_passed);
				hs_times.put(curr_name, tmp_hs_times);
			}
			printTime(hs_time_passed);
		} catch (TimeoutException e) {
			long emergency_stop = System.currentTimeMillis();
			double additional_time = (double) ((double) (emergency_stop - hs_start_time) / 1000);
			System.out.println("! HS-SearchTree timed out after additional "
					+ String.format("%.3f", additional_time) + " sec.");
			pipe_2_timeouts.put(curr_name, true);
			timed_out_graphs.add(curr_kernel.getIdentifier());
		}
	}

	private static void collectResults() {
		if (!mute)
			System.out.println("\n------------------------------------");
		// Loop over all reduced graphs
		for(Hypergraph curr_graph : reduced_graphs) {
			String curr_id = curr_graph.getIdentifier();
			// Construct file path
			String result_file_path = "";
			if (call_from_cmd) {
				result_file_path = ".." + File.separator + ".." + File.separator + ".." + File.separator + "matlab_plots" + File.separator + "output"
						+ File.separator + curr_id.substring(0, curr_id.length()-3) + ".res";
			} else {
				result_file_path = ".." + File.separator + ".." + File.separator+ "matlab_plots" + File.separator + "output"
						+ File.separator + curr_id.substring(0, curr_id.length()-3) + ".res";
			}
			// Calculate pipe_1_sum
			long pipe_1_sum = 0;
			if (search_tree_times.get(curr_id) != null) {
				for (long time : search_tree_times.get(curr_id)) {
					pipe_1_sum += time;
				} 
			}
			// Calculate pipe_2_time
			long kernel_sum = 0;
			if(kernel_times.get(curr_id) != null) {
				for(long time : kernel_times.get(curr_id)) {
					kernel_sum += time;
				}
			}
			long hs_sum = 0;
			if(hs_times.get(curr_id) != null) {
				for(long time : hs_times.get(curr_id)) {
					hs_sum += time;
				}
			}
			long pipe_2_sum = kernel_sum + hs_sum;
			if (reduction_times.get(curr_id) != null) {
				pipe_2_sum += reduction_times.get(curr_id);
			}
			if(heuristic_times.get(curr_id) != null) {
				pipe_2_sum += heuristic_times.get(curr_id);
			}
			// Open file
			File out_file = new File(result_file_path);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
				bw.write("file: " + curr_id + "\n");
				// TODO Only use single formula and print it
//				bw.write("formula: " + );
				bw.write("total_nodes: " + graph_sizes.get(curr_graph.hypergraph_name) + "\n");
				bw.write("c_par: " + c_list.get(curr_graph.formula_name) + "\n");
				bw.write("dens: " + dens_list.get(curr_graph.formula_name) + "\n");
				bw.write("lowest_k: " + actual_lower_bounds_per_graph.get(curr_id) + "\n");
				if (solution_k.get(curr_id) != null) {
					bw.write("solved_k: " + solution_k.get(curr_id) + "\n");
				}
				if(search_tree_times.get(curr_id) != null) {
					bw.write("pipe_1_times:");
					for(long time : search_tree_times.get(curr_id)) {
						bw.write(formatTimeInSeconds(time) + ";");
					}
					bw.write("\n");					
					bw.write("pipe_1_sum: " + formatTimeInSeconds(pipe_1_sum) + "\n");
					bw.write("pipe_1_res: " + search_tree_results.get(curr_id) + "\n");
					bw.write("pipe_1_timeout: " + pipe_1_timeouts.get(curr_id) + "\n");
				}
				bw.write("redu_time: " + formatTimeInSeconds(reduction_times.get(curr_id)) + "\n");
				bw.write("redu_nodes: " + reduced_nodes.get(curr_id) + "\n");
				bw.write("redu_edges: " + reduced_edges.get(curr_id) + "\n");
				if (heuristic_times.get(curr_id) != null) {
					bw.write("heur_time: " + formatTimeInSeconds(heuristic_times.get(curr_id)) + "\n");
					bw.write("heur_nodes: " + heuristic_nodes.get(curr_id) + "\n");
					bw.write("heur_edges: " + heuristic_edges.get(curr_id) + "\n");
					bw.write("heur_k_used: " + k_used_in_heuristics_per_graph.get(curr_id) + "\n");
				}
				if(kernel_times.get(curr_id) != null) {
					if (!kernel_times.get(curr_id).isEmpty()) {
						bw.write("kernel_times: ");
						for (long time : kernel_times.get(curr_id)) {
							bw.write(formatTimeInSeconds(time) + ";");
						}
						bw.write("\n");
					}					
					bw.write("kernel_nodes: ");
					for (int nodes : kernel_nodes.get(curr_id)) {
						bw.write(nodes + ";");
					}
					bw.write("\n");
					bw.write("kernel_edges: ");
					for (int edges : kernel_edges.get(curr_id)) {
						bw.write(edges + ";");
					}
					bw.write("\n");
				}
				if (hs_times.get(curr_id) != null) {
					bw.write("hs_st_times: ");
					for (long time : hs_times.get(curr_id)) {
						bw.write(formatTimeInSeconds(time) + ";");
					}
					bw.write("\n");
				}
				bw.write("pipe_2_sum: " + formatTimeInSeconds(pipe_2_sum) + "\n");
				bw.write("pipe_2_res: " + ke_results.get(curr_id) + "\n");
				if (pipe_1_timeouts.get(curr_id) != null) {
					bw.write("pipe_1_timeout: " + pipe_1_timeouts.get(curr_id) + "\n");
				}
				if(pipe_2_timeouts.get(curr_id) == null) {
					bw.write("pipe_2_timeout: false\n");
				} else {
					bw.write("pipe_2_timeout: " + pipe_2_timeouts.get(curr_id) + "\n");					
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	private static void printTime(long time) {
		System.out.println("- Time elapsed:  " + formatTimeInSeconds(time) + " sec");
	}

	private static String formatTimeInSeconds(long time) {
		return String.format("%.3f", (double)time/(double)1000).replace(',', '.');
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
