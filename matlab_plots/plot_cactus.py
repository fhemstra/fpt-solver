import os
import numpy as np
import sys
from matplotlib import pyplot as plt

# Filter for graphs containing a string in their filename
filter_string = 'cluster-deletion'

def plot_one_set_of_results(directory_name):
	# Get file path
	script_dir = os.path.dirname(os.path.realpath(__file__))
	output_dir = os.path.join(script_dir, directory_name)
	output_dir_list = os.listdir(output_dir)
	file_list = [os.path.join(directory_name, file) for file in output_dir_list if ".res" == file[-4:]]
	assert len(file_list) > 0, "No RES files in output directory."

	# Find solved files
	solved_files = []
	for curr_file in file_list:
		content = []
		# Filter for certain files
		if(len(filter_string) > 0):
			if not filter_string in curr_file:
				continue
		# Read file
		with open(curr_file) as file:
			content = file.readlines()
		# Remove whitespace characters
		content = [x.strip() for x in content]

		# Check if the current file has been solved
		for entry in content:
			if("pipe_2_res: true" in entry or "pipe_1_res: true" in entry):
				solved_files.append(curr_file)
				break

	solution_k_list = []
	# Collect k values per solved file
	for curr_file in solved_files:
		content = []
		with open(curr_file) as file:
			content = file.readlines()
		# Remove whitespace characters
		content = [x.strip() for x in content]

		# Get the solution k
		for entry in content:
			if("pipe_2_sol_k" in entry or "pipe_1_sol_k" in entry):
				split_entry = entry.split(':')
				k = int(split_entry[1].strip())
				solution_k_list.append(k)
				break

	solver_times = []
	# Collect time values per solved file
	for curr_file in solved_files:
		content = []
		with open(curr_file) as file:
			content = file.readlines()
		# Remove whitespace characters
		content = [x.strip() for x in content]

		# Get the time it took to solve the file
		for entry in content:
			if("pipe_2_sum" in entry or "pipe_1_sum" in entry):
				split_entry = entry.split(':')
				time = float(split_entry[1].strip())
				solver_times.append(time)
				break

	# Print solved files
	if len(solution_k_list) == len(solved_files):
		print("Solved files:")
		for i in range(len(solved_files)):
			print(solved_files[i] + ', k = ' + str(solution_k_list[i]) + ', time = ' + str(solver_times[i]))
	else:
		for i in range(len(solved_files)):
			print(solved_files[i])

	# Sort list of times
	solver_times.sort()
	print("Reading done.")

	# Results are always 1 for solved graphs
	ones = np.ones(len(solver_times))
	prefix_sum = np.cumsum(ones)

	# Plot stuff
	plt.plot(solver_times, prefix_sum)

def main():
	# Close previous plots
	plt.close("all")
	# Break if no dirs are passed
	input_args = sys.argv
	assert len(input_args) > 1, 'You need to pass at least one directory of result files.'
	# Get CLI arguments ([0] is script name)
	for i in range(1,len(input_args)):
		directory_name = input_args[i]
		plot_one_set_of_results(directory_name)

	# Remove path from legend labels
	legend_label_splits = [label.split('/') for label in input_args[1:]]
	# Only take the last field of the split
	legend_labels = [label_split[len(label_split)-1] for label_split in legend_label_splits]

	# Plot settings
	# plt.xlim(left=0)
	# plt.ylim(bottom=0)
	plt.xlabel('Zeit in Sekunden')
	plt.ylabel('Instanzen')
	plt.title('Zahl der gelösten Instanzen über Zeit')
	plt.legend(labels=legend_labels, loc='best')
	plt.show()

if __name__ == "__main__":
	main()