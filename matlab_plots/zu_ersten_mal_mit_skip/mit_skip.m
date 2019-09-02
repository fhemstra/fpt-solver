close all

files = dir('*.csv');
hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    
    name = extractAfter(file.name, 'random_');
    name_end_ind = strlength(name)-4;
    name = name(1:name_end_ind);
    name = strrep(name, '_', '=');
    plot(x,y_st,'-','DisplayName',['st ' name]);
    
    y_ke = file_content.data(:,3);
    name = extractAfter(file.name, 'random_');
    name_end_ind = strlength(name)-4;
    name = name(1:name_end_ind);
    name = strrep(name, '_', '=');
    plot(x,y_ke,'--','DisplayName',['ke ' name]);
end
title 'Laufzeit der random-Graphen'
xlabel 'nodes';
ylabel 'time (sec)';
legend(gca, 'show');
hold off;