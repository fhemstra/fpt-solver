import os
import numpy as np
import csv
from matplotlib import pyplot as plt

# Close previous plots
plt.close("all")

# Get file path
dir_path = os.path.dirname(os.path.realpath(__file__))
dir_list = os.listdir(dir_path)
csv_list = [file for file in dir_list if ".csv" == file[-4:]]
curr_csv_file = csv_list[0]

# Read CSV file
rows = []
with open(curr_csv_file) as file:
	file_reader = csv.reader(file, delimiter=";")
	for row in file_reader:
		rows.append(row)

# Separate data
csv_header = rows[0]
csv_data = rows[1:]

# Find column indices
n_col = csv_header.index('n')
k_col = csv_header.index('k')
redu_col = csv_header.index('Reduction time')
hs_col = csv_header.index('HS-ST time')
kernel_col = csv_header.index('Kernel time')
res_col = csv_header.index('Pipe 2 result')

# Find start k
start_k = -1
for row in csv_data:
	if start_k == -1 or int(row[k_col]) < start_k:
		start_k = int(row[k_col])

# Only consider k = start_k, because reduction only happens once in the beginning
data_set = [row for row in csv_data if int(row[k_col]) == start_k]

# Collect different values for n
n_values = []
for row in data_set:
	if not int(row[n_col]) in n_values:
		n_values.append(int(row[n_col]))

# Collect graphs per n
graphs_per_n = []
for curr_n in n_values:
	graphs_per_n.append([row for row in data_set if int(row[n_col]) == curr_n])

graph_times_per_n = []
# Calc matrix of graph times per n
for graphs_of_same_n in graphs_per_n:
	graph_times = []
	# Calc result time per graph
	for graph in graphs_of_same_n:
		reduction_time = float(graph[redu_col])
		graph_times.append(reduction_time)
	# Add set of times for fixed n to matrix
	graph_times_per_n.append(graph_times)

plt.boxplot(graph_times_per_n, showmeans=True, labels=n_values)
plt.xlabel('Knoten')
plt.ylabel('Zeit')
plt.title('Zeit zur Reduktion von Instanzen verschiedener Größe')
plt.show()
