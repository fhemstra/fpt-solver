close all

files = dir('*.csv');

figure
hold on;
subplot(2,1,1);
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'SearchTree, k = 7, c = 3'
xlabel 'n';
ylabel 'time (sec)';
hold off;

hold on;
subplot(2,1,2);
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_ke = file_content.data(:,3);
    boxplot(y_ke,x);
end
title 'Red. + Kern., k = 7, c = 3'
xlabel 'nodes';
ylabel 'time (sec)';
hold off;

print('c_3.pdf','-fillpage','-dpdf');