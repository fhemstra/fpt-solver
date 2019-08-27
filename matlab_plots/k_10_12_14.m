clear all
close

data_k_10 = importdata('26_08_01_k_10.csv',';');
x_nodes = data_k_10.data(:,1);
y_k_10_st = data_k_10.data(:,2);
y_k_10_ke = data_k_10.data(:,3);

data_k_12 = importdata('26_08_01_k_12.csv',';');
y_k_12_st = data_k_12.data(:,2);
y_k_12_ke = data_k_10.data(:,3);

data_k_14 = importdata('26_08_01_k_14.csv',';');
y_k_14_st = data_k_14.data(:,2);
y_k_14_ke = data_k_10.data(:,3);

hold on;
plot(x_nodes, y_k_10_st,'g');
plot(x_nodes, y_k_10_ke,'g--');
plot(x_nodes, y_k_12_st,'b');
plot(x_nodes, y_k_12_ke,'b:');
plot(x_nodes, y_k_14_st,'r');
plot(x_nodes, y_k_14_ke,'r-.');
title 'Laufzeit der kleinen PACE-Graphen'
xlabel 'nodes';
ylabel 'time (sec)';
legend('k=10 st', 'k=10 ke', 'k=12 st', 'k=12 ke', 'k=14 st', 'k=14 ke');
hold off;