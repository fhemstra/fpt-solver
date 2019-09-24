close all

ron_files = dir('ron*.csv')
falko_files = dir('falko*.csv')

% Ron
figure
subplot(2,2,1);
hold on;
for file = ron_files'
    file_content = importdata(file.name);
    x = file_content.data(:,7);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
axis([0 4 0 0.04]);
title 'ST-Laufzeiten (Ron), n = 1500'
xlabel 'k';
ylabel 'time (sec)';
hold off;

hold on;
subplot(2,2,3);
for file = ron_files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,7);
    boxplot(y_ke,x);
end
axis([0 4 1 7]);
title 'KE-Laufzeiten (Ron), n = 1500'
xlabel 'k';
ylabel 'time (sec)';
hold off;

% Falko
subplot(2,2,2);
hold on;
for file = falko_files'
    file_content = importdata(file.name);
    x = file_content.data(:,7);
    y_st = file_content.data(:,2);
    boxplot(y_st,x);
end
axis([0 4 0 0.04]);
title 'ST-Laufzeiten (Falko), n = 1500'
xlabel 'k';
ylabel 'time (sec)';
hold off;

hold on;
subplot(2,2,4);
for file = falko_files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,7);
    boxplot(y_ke,x);
end
axis([0 4 1 7]);
title 'KE-Laufzeiten (Falko), n = 1500'
xlabel 'k';
ylabel 'time (sec)';
hold off;
