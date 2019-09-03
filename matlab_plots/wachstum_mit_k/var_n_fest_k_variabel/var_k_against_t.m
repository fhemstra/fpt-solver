close all

files = dir('*.csv');

figure
hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,7);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
axis([0 10 0 6]);
title 'Varianz der ST-Laufzeiten, n = 1500'
xlabel 'k';
ylabel 'time (sec)';
hold off;

figure
hold on;   
for file = files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,7);
    boxplot(y_ke,x);
end
axis([0 10 0 6]);
title 'Varianz der KE-Laufzeiten, n = 1500'
xlabel 'k';
ylabel 'time (sec)';
hold off;