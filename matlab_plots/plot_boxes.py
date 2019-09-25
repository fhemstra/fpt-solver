import os
import numpy as np
import csv
from matplotlib import pyplot as plt

# Close previous plots
plt.close("all")

# Get file path
dir_path = os.path.dirname(os.path.realpath(__file__))
dir_list = os.listdir(dir_path)
curr_csv_file = "1569392218839_bara_alb_graphs_k_1-200.csv"

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
x_col = csv_header.index('k')
y_col = csv_header.index('Pipe 2 time')

# Find maximum k
max_k = 0
for row in csv_data:
	if int(row[x_col]) > max_k:
		max_k = int(row[x_col])

# Collect data rows per k
data_per_k = []
for k in range(1,max_k):
	# Append list of rows matching the same k
	data_per_k.append([row for row in csv_data if int(row[x_col]) == k])

# Collect pipe 2 times per k from rows
pipe_2_times_per_k = []
for index in range(len(data_per_k)):
	# Append list of pipe 2 times matching the same k
	pipe_2_times_per_k.append([float(row[y_col]) for row in data_per_k[index]])

plt.boxplot(pipe_2_times_per_k, showmeans=True)
plt.xlabel('k')
plt.ylabel('Zeit')
plt.title('Laufzeit der Lösung mit Reduktion, Kernelisierung und Suchbaum')
plt.show()
