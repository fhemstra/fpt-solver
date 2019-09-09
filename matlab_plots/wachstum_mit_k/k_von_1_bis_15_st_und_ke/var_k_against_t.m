close all

files = dir('*.csv');

hold on;
subplot(2,1,1);
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,7);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'SearchTree, n = 1500, c = 2'
xlabel 'k';
ylabel 'time (sec)';
hold off;

hold on;
subplot(2,1,2);
for file = files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,7);
    boxplot(y_ke,x);
end
title 'Red. + Kern., n = 1500, c = 2'
xlabel 'k';
ylabel 'time (sec)';
hold off;

print('k_gegen_t_n_1500_c_2','-fillpage','-dpdf');