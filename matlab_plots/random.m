clear all
close

files = dir('*_random_*.csv');

hold on;
for file = files'
    file_content = importdata(file.name);
    x = file_content.data(:,1);
    y_st = file_content.data(:,2);
    y_ke = file_content.data(:,3);
    plot(x,y_st);
    plot(x,y_ke);
end
xlabel 'nodes';
ylabel 'time (sec)';
legend('searchTree','kernel','searchTree','kernel');
hold off;