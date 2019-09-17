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