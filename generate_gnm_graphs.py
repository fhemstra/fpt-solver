import os
import networkx as nx

n = 200
m = 400
variance_count = 10

# create directory if it does not exist yet
dir_path = os.path.dirname(os.path.realpath(__file__))
dest_dir = dir_path + os.sep + 'workspace_BA' + os.sep + 'FooSearchTree' + os.sep + 'gnm_graphs'
if not os.path.exists(dest_dir):
	os.makedirs(dest_dir) 

for i in range(variance_count):
	graph = nx.gnm_random_graph(n, m)
	nodes = graph.nodes()
	edges = graph.edges()

	filename = "gnm_n_" + str(n) + "_m_" + str(m) + "_" + str(i) + ".txt"
	print(str(filename))
	full_path = dest_dir + os.sep + filename
	with open(full_path, 'w') as curr_file:
		header = "p td " + str(len(nodes)) + " " + str(len(edges))
		curr_file.write(str(header) + '\n')
		for edge in edges:
			curr_file.write(str(edge[0]) + " " + str(edge[1]) + "\n")
