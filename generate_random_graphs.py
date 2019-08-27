import os
from random import randint

for nr_of_nodes in range(50,6050,50):
    nr_of_edges = nr_of_nodes * 4

    dir_path = os.path.dirname(os.path.realpath(__file__))
    filename = dir_path + os.sep + 'workspace_BA' + os.sep + 'FooSearchTree' + os.sep + 'random_graphs' + os.sep + 'random_' + str(nr_of_nodes) + '_' + str(nr_of_edges) +  '.txt'

    with open(filename, 'w') as curr_file:
        curr_file.write('p td ' + str(nr_of_nodes) + ' ' + str(nr_of_edges) + ' \n')
        while nr_of_edges != 0:
            curr_start = randint(1, nr_of_nodes)
            curr_end = randint(1, nr_of_nodes)
            curr_file.write(str(curr_start) + ' ' + str(curr_end) + '\n')
            nr_of_edges -= 1
