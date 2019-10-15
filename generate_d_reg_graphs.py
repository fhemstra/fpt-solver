import os
import networkx as nx

variance_count = 5
d = 3

# create directory if it does not exist yet
dir_path = os.path.dirname(os.path.realpath(__file__))
dest_dir = dir_path + os.sep + 'workspace_BA' + os.sep + 'ba-solver' + os.sep + 'src' + os.sep + 'main' + os.sep + 'resources'+ os.sep + 'input_graphs' + os.sep + 'd_reg_graphs'
if not os.path.exists(dest_dir):
	os.makedirs(dest_dir) 
for n in range(10,110,10):
	for i in range(variance_count):
		graph = nx.random_regular_graph(d, n)
		nodes = graph.nodes()
		edges = graph.edges()
		density = len(edges)/len(nodes)

		filename = "d_reg_d_" + str(d) + "_n_" + str(n) + "_" + str(i) + ".gr"
		print(str(filename) + ", dens: " + str(density))
		full_path = dest_dir + os.sep + filename
		with open(full_path, 'w') as curr_file:
			header = "p td " + str(len(nodes)) + " " + str(len(edges))
			curr_file.write(str(header) + '\n')
			for edge in edges:
				curr_file.write(str(edge[0]) + " " + str(edge[1]) + "\n")
