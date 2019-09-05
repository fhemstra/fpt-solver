close all

files = dir('*.csv');

figure
hold on;
subplot(1,2,1);
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'Varianz der ST-Laufzeiten auf festem k'
xlabel 'n';
ylabel 'time (sec)';
hold off;

hold on;
subplot(1,2,2);
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_ke = file_content.data(:,3);
    boxplot(y_ke,x);
end
title 'Varianz der KE-Laufzeiten auf festem k'
xlabel 'nodes';
ylabel 'time (sec)';
hold off;