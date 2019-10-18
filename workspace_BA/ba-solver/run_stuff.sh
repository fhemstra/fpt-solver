#!/bin/bash
# Dom-Set st (dom_set_st)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/d_reg_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/dom_set -t 30 -st
echo "------------"
# Dom-Set bev (dom_set_bev_default)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/d_reg_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/dom_set -t 30 -ke -be
echo "------------"
# Dom-Set bev + heu (dom_set_bev_heu)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/d_reg_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/dom_set -t 30 -ke -be -heu
echo "------------"
# Dom-Set bev + heu + bound (dom_set_bev_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/d_reg_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/dom_set -t 30 -ke -be -heu -bo
echo "------------"
# Dom-Set sf + heu + bound (dom_set_sf_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/d_reg_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/dom_set -t 30 -ke -sf -heu -bo
echo "------------"
# Dom-Set bev + sf + heu + bound (dom_set_both_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/d_reg_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/dom_set -t 30 -ke -be -sf -heu -bo
echo "------------"