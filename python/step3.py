import csv

# 读取CSV文件
with open('project_new.csv', 'r') as file:
    reader = csv.reader(file)
    lines = [line for line in reader if line]

# 将数据写入新文件
with open('project.csv', 'w', newline='') as file:
    writer = csv.writer(file)
    writer.writerows(lines)