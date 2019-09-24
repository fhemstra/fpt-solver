close all

files = dir('*600*random*heur*.csv');
file = files(1);
file_content = importdata(file.name);

figure
hold on;
x = file_content.data(:,1); % nodes
y = file_content.data(:,7); % k
z = file_content.data(:,11); % ke nodes
z(z < 0) = 0;
plot3(x,y,z);
title 'Kernelgröße mit n und k';
xlabel 'n'
ylabel 'k';
zlabel 'kernel nodes';
hold off;