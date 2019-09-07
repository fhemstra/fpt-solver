close all

files = dir('*.csv');

figure
subplot(1,2,1);
hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,14);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'Varianz der ST-Laufzeiten, k = 12, n = 250, c = 3'
xlabel 'density';
ylabel 'time (sec)';
hold off;

subplot(1,2,2);
hold on;   
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,14);
    y_ke = file_content.data(:,3);
    boxplot(y_ke,x);
end
title 'Varianz der KE-Laufzeiten, k = 12, n = 250, c = 3'
xlabel 'density';
ylabel 'time (sec)';
hold off;