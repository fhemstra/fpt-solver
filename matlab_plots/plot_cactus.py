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
assert len(csv_list) > 0, "No CSV files in current directory."
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
solver_time_col = csv_header.index('Time to solve')
pipe_2_res_col = csv_header.index('Pipe 2 result')

# Get the time it took to solve each graph
solver_times = [float(row[solver_time_col]) for row in csv_data]
np_solver_times = np.array(solver_times)
np_solver_times = np.cumsum(np_solver_times)

# Get results, eiter 1 or 0 for true and false respectively
pipe_2_results = [float(row[pipe_2_res_col]) for row in csv_data]
np_pipe_2_results = np.array(pipe_2_results)
# Add everything up to a prefixsum
np_pipe_2_results = np.cumsum(np_pipe_2_results)

# Plot stuff
plt.plot(np_solver_times, np_pipe_2_results)
plt.xlabel('Zeit')
plt.ylabel('Instanzen')
plt.title('Zahl der gelösten Instanzen über Zeit')
plt.show()
