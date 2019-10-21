import os
import numpy as np
import sys
from matplotlib import pyplot as plt

def plot_one_set_of_results(directory_name):
	# Get file path
	script_dir = os.path.dirname(os.path.realpath(__file__))
	output_dir = os.path.join(script_dir, directory_name)
	output_dir_list = os.listdir(output_dir)
	file_list = [os.path.join(directory_name, file) for file in output_dir_list if ".res" == file[-4:]]
	assert len(file_list) > 0, "No RES files in output directory."

	# Find kernelized files
	kernelized_files = []
	for curr_file in file_list:
		content = []
		with open(curr_file) as file:
			content = file.readlines()
		# Remove whitespace characters
		content = [x.strip() for x in content]

		# Check if the current file has been kernelized
		for entry in content:
			if("kernel_times:" in entry):
				kernelized_files.append(curr_file)
				break

	kernel_size_list = []
	# Collect average kernel size per solved file (over all k the file has been tested on)
	for curr_file in kernelized_files:
		content = []
		with open(curr_file) as file:
			content = file.readlines()
		# Remove whitespace characters
		content = [x.strip() for x in content]

		# Get kernel sizes
		for entry in content:
			if("kernel_edges" in entry):
				split_entry = entry.split(':')
				str_values = split_entry[1].split(';')
				# Remove empty entry
				str_values.pop()
				int_values = [int(val) for val in str_values]
				# Calc average
				average = sum(int_values)/float(len(int_values))
				kernel_size_list.append(average)
				break

	# Print kernelized files
	# print("Kernelized files:")
	# for i in range(len(kernelized_files)):
	# 	print(kernelized_files[i] + ', size = ' + str(kernel_size_list[i]))

	# Sort list of sizes
	kernel_size_list.sort()
	print("Reading done.")

	# Calculate interesting stuff
	if(len(kernel_size_list) > 0):
		np_sizes = np.array(kernel_size_list)

		print(directory_name)
		print('mean size: ' + str(np.mean(np_sizes)))
		print('medi size: ' + str(np.median(np_sizes)))
		print('max size:  ' + str(np.max(np_sizes)))
		print('min size:  ' + str(np.min(np_sizes)))

	# Sum up ones (each size value referrs to one instance)
	prefix_sum = np.cumsum(np.ones(len(kernel_size_list)))

	# Plot stuff
	plt.plot(kernel_size_list, prefix_sum)

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

	plt.xlabel('Kernelgröße in Hyperkanten')
	plt.ylabel('Instanzen')
	plt.title('Zahl der kernelisierten Instanzen pro Kernelgröße')
	plt.legend(labels=input_args[1:], loc='best')
	plt.show()

if __name__ == "__main__":
	main()