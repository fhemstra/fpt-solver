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
title 'SearchTree, vc-pos, k = 12, c = 2'
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
title 'Red. + Kern., vc-pos, k = 12, c = 2'
xlabel 'nodes';
ylabel 'time (sec)';
hold off;

print('vc_pos_k_12_n_600_4000.pdf','-fillpage','-dpdf');