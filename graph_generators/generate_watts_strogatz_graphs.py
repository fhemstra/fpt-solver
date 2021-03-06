import os
import networkx as nx

variance_count = 1

# create directory if it does not exist yet
dir_path = os.path.dirname(os.path.realpath(__file__))
dest_dir = dir_path + os.sep + 'watts_strog_graphs'
if not os.path.exists(dest_dir):
	os.makedirs(dest_dir) 

for n in range(20,100,20):
	for i in range(variance_count):
		for k in range(2,10,2):
			for p in range(1,8,3):
				p = p/10
				graph = nx.watts_strogatz_graph(n, k, p)
				nodes = graph.nodes()
				edges = graph.edges()
				density = len(edges)/len(nodes)

				filename = "watts_strog_n_" + str(n) + "_k_" + str(k) + "_p_" + str(p) + "_" + str(i) + ".gr"
				print(str(filename) + ", dens: " + str(density))
				full_path = dest_dir + os.sep + filename
				with open(full_path, 'w') as curr_file:
					header = "p td " + str(len(nodes)) + " " + str(len(edges))
					curr_file.write(str(header) + '\n')
					for edge in edges:
						curr_file.write(str(edge[0]) + " " + str(edge[1]) + "\n")
