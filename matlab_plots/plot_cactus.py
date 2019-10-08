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

solution_k_list = []
# Collect time values for solved files
for curr_file in solved_files:
	content = []
	with open(curr_file) as file:
		content = file.readlines()
	# Remove whitespace characters
	content = [x.strip() for x in content]

	# Get the solution k
	for entry in content:
		if("solved_k" in entry):
			split_entry = entry.split(':')
			k = int(split_entry[1].strip())
			solution_k_list.append(k)
			break

# Print solved files
print("Solved files:")
for i in range(len(solved_files)):
	print(solved_files[i] + ', k = ' + str(solution_k_list[i]))

solver_times = []
# Collect time values for solved files
for curr_file in solved_files:
	content = []
	with open(curr_file) as file:
		content = file.readlines()
	# Remove whitespace characters
	content = [x.strip() for x in content]

	# Get the time it took to solve the file
	for entry in content:
		if("pipe_2_sum" in entry):
			split_entry = entry.split(':')
			time = float(split_entry[1].strip())
			solver_times.append(time)
			break
# Sort list of times
solver_times.sort()
print("Reading done.")


# Results are always 1 for solved graphs
ones = np.ones(len(solver_times))
prefix_sum = np.cumsum(ones)

# Plot stuff
plt.plot(solver_times, prefix_sum)
plt.xlabel('Zeit')
plt.ylabel('Instanzen')
plt.title('Zahl der gelösten Instanzen über Zeit')
plt.show()
