#!/bin/bash
python plot_kernel_size.py final_runs/clu_del_30_secs_bev_opt final_runs/clu_del_30_secs_sf_opt
echo "------------"
python plot_kernel_time.py final_runs/clu_del_30_secs_bev_opt final_runs/clu_del_30_secs_sf_opt