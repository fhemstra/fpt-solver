clear all
close

% Only look at search tree variance for now

files = dir('*.csv');

figure
hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    
    name = extractAfter(file.name, 'random_');
    name_end_ind = strlength(name)-4;
    name = name(1:name_end_ind);
    name = strrep(name, '_', '=');
    boxplot(y_st,x);
end
axis([0 7 0 25]);
title 'Varianz der ST-Laufzeiten auf festen n mit k=10 und k = 12'
xlabel 'nodes';
ylabel 'time (sec)';
hold off;

figure
hold on;   
for file = files'
    file_content = importdata(file.name);
    y_ke = file_content.data(:,3);
    x = file_content.data(:,1);
    name = extractAfter(file.name, 'random_');
    name_end_ind = strlength(name)-4;
    name = name(1:name_end_ind);
    name = strrep(name, '_', '=');
    boxplot(y_ke,x);
end
axis([0 7 0 25]);
title 'Varianz der KE-Laufzeiten auf festen n'
xlabel 'nodes';
ylabel 'time (sec)';
hold off;