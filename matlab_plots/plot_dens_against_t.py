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
dens_col = csv_header.index('density')

# Calc the time it took to solve each graph
solver_times = [float(row[solver_time_col]) for row in csv_data]

# Get results, eiter 1 or 0 for true and false respectively
dens_list = [float(row[dens_col]) for row in csv_data]

# Correlate density values with their solver times
dens_to_time = []
for i in range(len(dens_list)):
	dens_to_time.append([dens_list[i], solver_times[i]])

# Sort by firt element, which is density
dens_to_time.sort(key=lambda elem: elem[0])

# Split arrays up again
sorted_dens_list = []
sorted_time_list = []
for tuple in dens_to_time:
	sorted_dens_list.append(tuple[0])
	sorted_time_list.append(tuple[1])

# Plot stuff
plt.plot(sorted_dens_list, sorted_time_list)
plt.xlabel('Dichte')
plt.ylabel('Zeit zur Lösung')
plt.title('Lösungszeit im Zusamenahng mit Dichte der Graphen')
plt.show()
