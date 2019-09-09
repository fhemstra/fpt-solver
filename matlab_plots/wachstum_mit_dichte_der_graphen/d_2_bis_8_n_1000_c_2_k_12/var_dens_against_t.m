close all

files = dir('*.csv');

figure
subplot(2,1,1);
hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,14);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'SearchTree, n = 1000, c = 2, k = 12'
xlabel 'density';
ylabel 'time (sec)';
hold off;

subplot(2,1,2);
hold on;   
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,14);
    y_ke = file_content.data(:,3);
    boxplot(y_ke,x);
end
title 'Red. + Kern., n = 1000, c = 2, k = 12'
xlabel 'density';
ylabel 'time (sec)';
hold off;

print('d_gegen_t_n_1000_c_2_k_12.pdf','-fillpage','-dpdf');