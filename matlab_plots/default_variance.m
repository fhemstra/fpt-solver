close all

% Only look at search tree variance for now

files = dir('*.csv');

figure
hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'Varianz der ST-Laufzeiten auf festen n'
xlabel 'nodes';
ylabel 'time (sec)';
hold off;

figure
hold on;   
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_ke = file_content.data(:,3);
    boxplot(y_ke,x);
end
title 'Varianz der KE-Laufzeiten auf festen n'
xlabel 'nodes';
ylabel 'time (sec)';
hold off;