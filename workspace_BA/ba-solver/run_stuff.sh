#!/bin/bash
# Hit-Set st (hs_st_guard)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -st -gu -int
echo "------------"
# Hit-Set bev (hs_bev_default)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be -int
echo "------------"
# Hit-Set bev (hs_bev_guard)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be -gu -int
echo "------------"
# Hit-Set bev + heu (hs_bev_guard_heu)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be -gu -heu -int
echo "------------"
# Hit-Set bev + heu + bound (hs_bev_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be -gu -heu -bo -int
echo "------------"
# Hit-Set sf + heu + bound (hs_sf_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -sf -gu -heu -bo -int
echo "------------"
# Hit-Set bev + sf + heu + bound (hs_both_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -bev -sf -gu -heu -bo -int
echo "------------"