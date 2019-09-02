import os
import re
import shutil

dir_path = os.path.dirname(os.path.realpath(__file__))
dir_list = os.listdir(dir_path)
csv_list = [f for f in dir_list if ".csv" == f[-4:]]

# Remove result file in case it already exists
for f in csv_list:
    if f == "merge_result.csv":
        os.remove("merge_result.csv")
        # Update list
        csv_list.remove("merge_result.csv")

# Reverse to get k in order
csv_list.reverse()

print("Merging:")
for f in csv_list:
    print(f)

# Get headline
headline = ""
with open(csv_list[0]) as first_file:
    headline = first_file.readline()

# Write to result file
with open("merge_result.csv", 'a') as result_file:
    result_file.write(headline)
    # go through all csv files and copy body
    for f in csv_list:
        with open(f) as curr_file:
            # Ignore headline
            content = curr_file.readlines()[1:]
            for line in content:
                result_file.write(line)

# create directory if it does not exist yet
merged_dir = dir_path + os.sep + "merged"
if not os.path.exists(merged_dir):
    os.makedirs(merged_dir)

# Move source csv files into seperate dir
for f in csv_list:
    shutil.move(f, merged_dir)
