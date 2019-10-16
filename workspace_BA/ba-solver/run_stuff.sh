#!/bin/bash
# VC st (vc_st_guard)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/reference_vc -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -st -gu
echo "------------"
# VC bev (vc_bev_default)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/reference_vc -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -ke -be
echo "------------"
# VC bev + guard (vc_bev_guard)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/reference_vc -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -ke -be -gu
echo "------------"
# VC bev + guard + heu (vc_bev_guard_heu)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/reference_vc -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -ke -be -gu -heu
echo "------------"
# VC bev + guard + heu + bound (vc_bev_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/reference_vc -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -ke -be -gu -heu -bo
echo "------------"
# VC sf + guard + heu + bound (vc_sf_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/reference_vc -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -ke -sf -gu -heu -bo
echo "------------"
# VC bev + sf + guard + heu + bound (vc_both_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/reference_vc -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/vc -t 30 -ke -be -sf -gu -heu -bo
echo "------------"

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

# Clu-Del st (clu_del_st)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/cluster_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/clu_del -t 30 -st
echo "------------"
# Clu-Del bev (clu_del_bev_default)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/cluster_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/clu_del -t 30 -ke -be
echo "------------"
# Clu-Del bev + heu (clu_del_bev_heu)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/cluster_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/clu_del -t 30 -ke -be -heu
echo "------------"
# Clu-Del bev + heu + bound (clu_del_bev_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/cluster_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/clu_del -t 30 -ke -be -heu -bo
echo "------------"
# Clu-Del sf + heu + bound (clu_del_sf_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/cluster_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/clu_del -t 30 -ke -sf -heu -bo
echo "------------"
# Clu-Del bev + sf + heu + bound (clu_del_both_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -g C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/input_graphs/cluster_graphs -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/clu_del -t 30 -ke -be -sf -heu -bo
echo "------------"

# Hit-Set st (hs_st_guard)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -st -gu
echo "------------"
# Hit-Set bev (hs_bev_default)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be
echo "------------"
# Hit-Set bev (hs_bev_guard)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be -gu
echo "------------"
# Hit-Set bev + heu (hs_bev_guard_heu)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be -gu -heu
echo "------------"
# Hit-Set bev + heu + bound (hs_bev_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -be -gu -heu -bo
echo "------------"
# Hit-Set sf + heu + bound (hs_sf_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -sf -gu -heu -bo
echo "------------"
# Hit-Set bev + sf + heu + bound (hs_both_opt)
java -jar target/ba-solver-0.0.1-SNAPSHOT.jar -f C:/Users/falko/Documents/Eigenes/Uni/6_Semester/Bachelorarbeit/Bachelorarbeit_Code/workspace_BA/instances/internal_hyps -t 30 -ke -bev -sf -gu -heu -bo
echo "------------"