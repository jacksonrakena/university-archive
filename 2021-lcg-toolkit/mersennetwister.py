import csv
from random import randint

with open('data.csv', 'w') as csvfile:
    writer = csv.writer(csvfile)
    for count in range(1000, 2000):
        writer.writerow([randint(0, 1000), randint(0, 1000), randint(0, 1000)])