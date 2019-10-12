import os
import sys
import random
import networkx as nx

variance_count = 2

def generate_edges(d_par, n, m):
	# Prevent endless loops
	assert n > d_par, "You need n to be bigger than d."
	# Generate m edges
	hyp_edges = []
	for i in range(m):
		edge = []
		# d elements per edge
		for j in range(d_par):
			element = random.randint(0,n-1)
			# Reroll if an element occurs twice in one edge
			while element in edge:
				element = random.randint(0,n-1)
			edge.append(element)
		# Append edge
		hyp_edges.append(edge)

	return hyp_edges

def generate_hyp(i, d_par, stub_path, n, m):
	# Get paths
	dir_path = os.path.dirname(os.path.realpath(__file__))
	dest_dir = dir_path + os.sep + 'internal_hyps'

	# Create directory if it does not exist yet
	if not os.path.exists(dest_dir):
		os.makedirs(dest_dir)

	# Generate edges of the hypergraph
	edges = generate_edges(d_par, n, m)

	# Extract information from the stub file
	with open(stub_path, 'r') as curr_file:
		stub_content = curr_file.readlines()
	form_name = stub_content[0]
	guard = stub_content[2]
	bound_vars = stub_content[3]
	clauses = stub_content[4:]

	# Remove linefeed from form name
	formname_formated = form_name.strip()
	formname_formated = formname_formated.replace('-','_')
	# Create paths
	filename = formname_formated + "_hyp_n_" + str(n) + "_m_" + str(int(m)) + "_d_" + str(d_par) + "_" + str(i) + ".form"
	print(str(filename))
	full_dest_path = dest_dir + os.sep + filename

	# Save everything
	with open(full_dest_path, 'w') as curr_file:
		# Formula name first
		curr_file.write(str(form_name))

		# Now fill in the edge relation of arity d
		curr_file.write('E' + str(d_par) + ' = {')
		line = ''
		for i in range(len(edges)):
			edge = edges[i]
			# Prepend bracket
			line += '('

			# Append elements
			for j in range(len(edge)):
				line += str(edge[j])
				# Don't attach '|' on the last element
				if not j == len(edge)-1:
					line += '|'

			# Append bracket
			line += ')'

			# Don't attach ',' on the last edge
			if not i == len(edges)-1:
				line += ','

		# Close bracket and end relations
		line += '};\n'
		# Write line out
		curr_file.write(line)

		# Add rest of the stub file
		curr_file.write(guard)
		curr_file.write(bound_vars)
		curr_file.writelines(clauses)

def main():
	# Get input
	input_args = sys.argv
	# First we expect a value for d, to generate d-tuples
	d_par = int(input_args[1])
	# Next is the path to a stub formula file
	stub_path = input_args[2]
	for n in range(50, 1000, 50):
		for i in range(variance_count):
			m = int(n*1.5)
			generate_hyp(i, d_par, stub_path, n, m)

	print('Done.')

if __name__ == "__main__":
	main()