close all

files = dir('*.csv');
file = files(1);
file_content = importdata(file.name);

figure
hold on;
x = file_content.data(:,3); % pipe 2 time
y = file_content.data(:,9); % pipe 2 results
y = cumsum(y) % prefixsum
plot(x,y);
title 'Zahl der gelösten Instanzen innerhalb des timeouts';
xlabel 'Zeit';
ylabel 'Instanzen';
hold off;