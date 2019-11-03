#!/bin/bash
# G(n,m) bev + heu + bound (dom_set_bev_opt)
java -jar target/ba-solver-1.0.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/gnm_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -ke -be -heu -bo
echo "------------"
