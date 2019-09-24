close all

files = dir('*n_600_1200*heur*.csv');
file = files(1);
file_content = importdata(file.name);

figure
hold on;
x = file_content.data(:,5); % kernel time, TODO Redution is missing now
% Remove -1 timeout entries
x(x < 0) = 0;
x_pos = x(x > 0);
x_sum = cumsum(x); % prefixsum
y = file_content.data(:,9); % pipe 2 results (1 or 0)
y_pos = y > 0;
y_sum = cumsum(y_pos);
plot(x_sum,y_sum);
title 'Zahl der gelösten Instanzen innerhalb des timeouts';
xlabel 'Zeit (sec)';
ylabel 'Instanzen';
hold off;