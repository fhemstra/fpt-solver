import os
import numpy as np
from matplotlib import pyplot as plt

# Close previous plots
plt.close("all")
# Get file path
script_dir = os.path.dirname(os.path.realpath(__file__))
output_dir = os.path.join(script_dir, 'output')
output_dir_list = os.listdir(output_dir)
file_list = [os.path.join('output', file) for file in output_dir_list if ".res" == file[-4:]]
assert len(file_list) > 0, "No RES files in output directory."

# Find solved files
solved_files = []
for curr_file in file_list:
	content = []
	with open(curr_file) as file:
		content = file.readlines()
	# Remove whitespace characters
	content = [x.strip() for x in content]
	# Check if the current file has been solved
	for entry in content:
		if("pipe_2_res: true" in entry):
			solved_files.append(curr_file)
			break

# Collect [n,time] tuples and all possible values for n
n_to_time_list = []
n_values = []
for curr_file in solved_files:
	content = []
	with open(curr_file) as file:
		content = file.readlines()
	# Remove whitespace characters
	content = [x.strip() for x in content]
	# Get values
	curr_n = 0
	curr_time = 0
	for entry in content:
		if("total_nodes" in entry):
			split_entry = entry.split(':')
			curr_n = int(split_entry[1].strip())
		if("pipe_2_sum" in entry):
			split_entry = entry.split(':')
			curr_time = float(split_entry[1].strip())
	# Append tuple
	n_to_time_list.append([curr_n,curr_time])
	# Append n value
	n_values.append(curr_n)

# Get unique n values
n_set = set(n_values)
# Convert back to list
unique_sorted_n_list = list(n_set)
# Sort the list
unique_sorted_n_list.sort()

# Collect a list of time values per n
graph_times_per_n = []
for curr_n in unique_sorted_n_list:
	curr_n_graphs = []
	for tuple in n_to_time_list:
		# If n matches
		if tuple[0] == curr_n:
			# Append time
			curr_n_graphs.append(tuple[1])
	# Append list to matrix
	graph_times_per_n.append(curr_n_graphs)

print("Reading done.")

plt.boxplot(graph_times_per_n, showfliers=False, showmeans=True, labels=unique_sorted_n_list, meanprops=dict(markerfacecolor='g', marker='D'))
plt.xlabel('Knoten')
plt.ylabel('Zeit')
plt.title('Zeit zur Lösung von Instanzen verschiedener Größe')
plt.show()
