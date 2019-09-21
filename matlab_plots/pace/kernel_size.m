close all

files = dir('*all*heur*.csv');
file = files(1);
file_content = importdata(file.name);

figure
hold on;
x = file_content.data(:,7); % k
y = file_content.data(:,11); % ke nodes, -1 for timeout...
%y = cumsum(y); % prefixsum
boxplot(y,x);
title 'Kernelgröße gegen k';
xlabel 'k';
ylabel 'kernel nodes';
hold off;