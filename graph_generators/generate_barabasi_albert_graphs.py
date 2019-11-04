import os
import networkx as nx

m = 2 # for bara-alb graphs, dens = m
variance_count = 10

# create directory if it does not exist yet
dir_path = os.path.dirname(os.path.realpath(__file__))
dest_dir = dir_path + os.sep + 'bara_alb_graphs'
if not os.path.exists(dest_dir):
	os.makedirs(dest_dir) 

for n in range(20, 220, 20):
	for i in range(variance_count):
		graph = nx.barabasi_albert_graph(n, m)
		nodes = graph.nodes()
		edges = graph.edges()
		density = len(edges)/len(nodes) 

		filename = "bara_alb_n_" + str(n) + "_m_" + str(m) + "_" + str(i) + ".gr"
		print(str(filename) + ", dens: " + str(density))
		full_path = dest_dir + os.sep + filename
		with open(full_path, 'w') as curr_file:
			header = "p td " + str(len(nodes)) + " " + str(len(edges))
			curr_file.write(str(header) + '\n')
			for edge in edges:
				curr_file.write(str(edge[0]) + " " + str(edge[1]) + "\n")
