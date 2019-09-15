import os
import random

# Creates random graphs using the GNP model

probability = 0.001
variance_count = 10

for nr_of_nodes in range(600,6200,200):
	# create directory if it does not exist yet
	dir_path = os.path.dirname(os.path.realpath(__file__))
	dest_dir = dir_path + os.sep + 'workspace_BA' + os.sep + 'FooSearchTree' + os.sep + 'random_graphs'
	if not os.path.exists(dest_dir):
		os.makedirs(dest_dir) 

	# Make multiple examples for variance
	for i in range(variance_count):
		# Calculate possible number of edges and reset actual number
		actual_edges = 0
		possible_edges = int(pow(nr_of_nodes,2)/2)
		# print("Possible edges: " + str(possible_edges))
		# Generate filename
		filename = 'rand_n_' + str(nr_of_nodes) + '_prob_' + str(probability) + '_' + str(i) +  '.txt'
		full_path = dest_dir + os.sep + filename
		print("Creating " + filename)
		with open(full_path, 'w') as curr_file:			
			# Create edges and write them to the file
			for j in range(1,nr_of_nodes+1):
				for k in range(1,nr_of_nodes+1):
					# Don't handle both (1,2) and (2,1)
					if j < k:
						rand_number = random.random()
						# Decide, if the edge (j,k) should exist
						if(rand_number <= probability):
							# Add edge
							curr_file.write(str(j) + ' ' + str(k) + '\n')
							actual_edges += 1

		# PACE-headline is supposed to be the first line
		headline = 'p td ' + str(nr_of_nodes) + ' ' + str(actual_edges) + ' \n'
		file_content = None
		# Read current file content
		with open(full_path, 'r') as curr_file:
			file_content = curr_file.readlines()
		# Prepend headline
		file_content.insert(0,headline)
		# Write other content back into file
		with open(full_path, 'w') as curr_file:
			curr_file.writelines(file_content)
		# print("Actual edges:   " + str(actual_edges))
		# print("Ratio: " + str(round(actual_edges/possible_edges, 5)))
