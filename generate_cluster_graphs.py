import os
import sys
import random
import networkx as nx

variance_count = 2

def generate_edges(n, k):
	graph_edges = []
	known_nodes = []
	# Place n - k nodes in clusters
	nr_of_nodes_remaining = n - k

	# Generate a perfect cluster graph
	while(nr_of_nodes_remaining > 0):
		nr_of_nodes_remaining = n - k - len(known_nodes)
		cluster_size = random.randint(0,nr_of_nodes_remaining)

		# Collect nodes for cluster
		cluster_nodes = []
		for i in range(cluster_size):
			node_to_add = random.randint(0,n-1)
			# Reroll
			while(node_to_add in known_nodes):
				node_to_add = random.randint(0,n-1)
			cluster_nodes.append(node_to_add)
			known_nodes.append(node_to_add)

		# Draw edges between all cluster nodes
		for i in range(len(cluster_nodes)):
			for j in range(len(cluster_nodes)):
				if not i == j and i < j:
					edge = [cluster_nodes[i], cluster_nodes[j]]
					graph_edges.append(edge)

	# Add k nodes which destroy cluster property
	noise_nodes = []
	for i in range(k):
		node_to_add = random.randint(0,n-1)
		# Reroll
		while(node_to_add in known_nodes or node_to_add in noise_nodes):
			node_to_add = random.randint(0,n-1)
		noise_nodes.append(node_to_add)

	# Draw edges between noise and clusters
	for noise in noise_nodes:
		connected_nodes = []
		nr_of_connections = random.randint(1,n-k)
		for i in range(nr_of_connections):
			# Roll a node to connect to
			connection_index = random.randint(0,len(known_nodes)-1)
			connection_node = known_nodes[connection_index]
			# Reroll
			while(connection_node in connected_nodes):
				connection_index = random.randint(0,len(known_nodes)-1)
				connection_node = known_nodes[connection_index]
			# Create edge
			edge = []
			if connection_node > noise:
				edge = [noise, connection_node]
			else:
				edge = [connection_node, noise]

			connected_nodes.append(connection_node)
			graph_edges.append(edge)

	# Sort edge set
	graph_edges.sort(key = lambda tuple: tuple[0])

	return graph_edges

def generate_cluster_graph(n, k, i):
	# Get paths
	dir_path = os.path.dirname(os.path.realpath(__file__))
	dest_dir = dir_path + os.sep + 'cluster_graphs'

	# Create directory if it does not exist yet
	if not os.path.exists(dest_dir):
		os.makedirs(dest_dir)

	# Generate edges of the cluster graph
	edges = generate_edges(n, k)

	# Get edge count
	m = len(edges)

	# Create paths
	filename = "cluster_graph_n_" + str(n) + "_k_" + str(k) + "_" + str(i) + ".gr"
	print(str(filename))
	full_dest_path = dest_dir + os.sep + filename

	# Save everything
	with open(full_dest_path, 'w') as curr_file:
		# Header
		curr_file.write('p td ' + str(n) + ' ' + str(m) + '\n')

		# Edges
		for i in range(len(edges)):
			edge = edges[i]
			curr_file.write(str(edge[0]) + ' ' + str(edge[1]) + '\n')

def main():
	for n in range(20, 160, 20):
		quater_n = int(n/4)
		for k in range(2,16,2):
			for i in range(variance_count):
				generate_cluster_graph(n, k, i)

	print('Done.')

if __name__ == "__main__":
	main()