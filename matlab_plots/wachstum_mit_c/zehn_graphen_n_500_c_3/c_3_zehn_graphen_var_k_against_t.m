close all

files = dir('*.csv');

figure
subplot(1,2,1);
hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,7);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'Varianz der ST-Laufzeiten'
xlabel 'k';
ylabel 'time (sec)';
hold off;

subplot(1,2,2);
hold on;   
for file = files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,7);
    boxplot(y_ke,x);
end
title 'Varianz der KE-Laufzeiten'
xlabel 'k';
ylabel 'time (sec)';
hold off;