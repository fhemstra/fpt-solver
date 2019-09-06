close all

c_2_files = dir('c_2*.csv');
c_3_files = dir('c_3*.csv');

% c = 2
figure
subplot(2,2,1);
hold on;
for file = c_2_files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'ST-Laufzeiten (c=2), k = 7'
xlabel 'n';
ylabel 'time (sec)';
hold off;

hold on;
subplot(2,2,3);
for file = c_2_files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,1);
    boxplot(y_ke,x);
end
title 'KE-Laufzeiten (c=2), k = 7'
xlabel 'n';
ylabel 'time (sec)';
hold off;

% c = 3
subplot(2,2,2);
hold on;
for file = c_3_files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
title 'ST-Laufzeiten (c=3), k = 7'
xlabel 'n';
ylabel 'time (sec)';
hold off;

hold on;
subplot(2,2,4);
for file = c_3_files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,1);
    boxplot(y_ke,x);
end
title 'KE-Laufzeiten (c=3), k = 7'
xlabel 'n';
ylabel 'time (sec)';
hold off;
