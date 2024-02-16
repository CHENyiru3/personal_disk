import csv
with open('project.csv', 'r') as infile, open('project_new.csv', 'w') as outfile:
    reader = csv.reader(infile)
    writer = csv.writer(outfile)

    for row in reader:
        new_row = [cell.replace('[', '').replace(']', '').replace("'", "").replace("PAIRED","PE").replace("SINGLE","SE") for cell in row]
        writer.writerow(new_row)


