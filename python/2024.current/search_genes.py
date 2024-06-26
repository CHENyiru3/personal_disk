import csv

# 要查找的字符列表
search_strings = ['Rel', 'kay', 'spz','PGRP-SD','Zdhhc8','Mekk1','Dif']

# 要读取的CSV文件路径
input_csv_path = '/Users/chen_yiru/Desktop/GP/new/Ecc_diff.bed_GO.csv'

# 输出结果的CSV文件路径
output_csv_path = '/Users/chen_yiru/Desktop/GP/new/Ecc_diff.extract_GO.csv'

# 打开输入CSV文件
with open(input_csv_path, newline='', encoding='utf-8') as csvfile:
    # 创建CSV读取器
    reader = csv.reader(csvfile)
    
    # 打开输出CSV文件
    with open(output_csv_path, 'w', newline='', encoding='utf-8') as outputfile:
        # 创建CSV写入器
        writer = csv.writer(outputfile)
        
        # 遍历CSV文件中的每一行
        for row in reader:
            # 检查是否至少包含一个指定字符
            if any(search_string in ''.join(row) for search_string in search_strings):
                # 如果包含，写入到输出文件
                writer.writerow(row)

print(f"至少包含'{search_strings}'中的任意一个字符的行已提取到'{output_csv_path}'")

