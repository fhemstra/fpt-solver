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
x_col = csv_header.index('Kernel time')
y_col = csv_header.index('Pipe 2 result')

kernel_times = [float(row[x_col]) for row in csv_data]
np_kernel_times = np.array(kernel_times)
np_kernel_times = np.cumsum(np_kernel_times)

pipe_2_results = [float(row[y_col]) for row in csv_data]
np_pipe_2_results = np.array(pipe_2_results)
np_pipe_2_results = np.cumsum(np_pipe_2_results)

plt.plot(np_kernel_times, np_pipe_2_results)
plt.xlabel('Zeit')
plt.ylabel('Instanzen')
plt.title('Zahl der gelösten Instanzen über Zeit')
plt.show()

# TODO: Nochmal Zeit-Wert überdenken (Redduktion nur einmal...)
