close all

files = dir('*.csv');
file = files(1);
file_content = importdata(file.name);

figure
hold on;
x = file_content.data(:,1); % nodes
y = file_content.data(:,9); % pipe 2 results
y = cumsum(y); % prefixsum
plot(x,y);
title 'Zahl der gel�sten Instanzen innerhalb des timeouts';
xlabel 'Knoten';
ylabel 'Zeit';
hold off;