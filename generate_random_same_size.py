import os
from random import randint

for nr_of_nodes in range(500,3500,1000):
    nr_of_edges = nr_of_nodes * 4
    dir_path = os.path.dirname(os.path.realpath(__file__))
    # create directory if it does not exist yet
    dest_dir = dir_path + os.sep + 'workspace_BA' + os.sep + 'FooSearchTree' + os.sep + 'random_graphs'
    if not os.path.exists(dest_dir):
        os.makedirs(dest_dir) 

    for i in range(100):
        filename = dir_path + os.sep + 'workspace_BA' + os.sep + 'FooSearchTree' + os.sep + 'random_graphs' + os.sep + 'random_' + str(nr_of_nodes) + '_' + str(i) +  '.txt'

        with open(filename, 'w') as curr_file:
            curr_file.write('p td ' + str(nr_of_nodes) + ' ' + str(nr_of_edges) + ' \n')
            while nr_of_edges != 0:
                curr_start = randint(1, nr_of_nodes)
                curr_end = randint(1, nr_of_nodes)
                curr_file.write(str(curr_start) + ' ' + str(curr_end) + '\n')
                nr_of_edges -= 1
