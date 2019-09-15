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
title 'SearchTree, vc-rand, k = 12, c = 2'
xtickangle(70);
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
title 'Red. + Kern., vc-rand, k = 12, c = 2'
xlabel 'n';
xtickangle(70);
ylabel 'time (sec)';
hold off;

print('vc_rand_k_12_n_600_6000.pdf','-fillpage','-dpdf');