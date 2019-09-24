    close all

files = dir('*.csv');

figure
hold on;
for file = files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,7);
    boxplot(y_ke,x);
end
% axis([1 16 0 200]);
set(gca, 'YScale', 'log');
title 'Red. + Kern., n = 1500, c = 2'
xlabel 'k';
ylabel 'time (sec)';
hold off;

print('k_gegen_t_n_1500_c_2.pdf','-bestfit','-dpdf');
