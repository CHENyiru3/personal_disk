url="http://bioinfo.life.hust.edu.cn/TCRdb/#/dataset"
import csv
from selenium import webdriver
from selenium.webdriver.common.by import By
import time
f=open("project.csv","a+")
writer=csv.writer(f) 

driver=webdriver.Edge()
driver.implicitly_wait(120)
driver.get(url)
cl = driver.find_element(By.XPATH,'//*[@id="app"]/div/section/main/div[2]/div/div[1]/div[2]/span/div/div/span/span/i')
cl.click()
cl2=driver.find_element(By.XPATH,'/html/body/div/div[1]/div[1]/ul/li[4]/span')
cl2.click()
time.sleep(10)
tbody=driver.find_element(By.CSS_SELECTOR,"tbody")
datas=tbody.find_elements(By.TAG_NAME,"tr")
n=0
for i in datas:
    next_datas=i.find_elements(By.TAG_NAME,"td")
    csv_list=[]
    for e in next_datas:
        real_data=e.find_elements(By.TAG_NAME,"span")
        tem_list=[]
        for k in real_data:
            tem_list.append(k.text)
        csv_list.append(tem_list)
    csv_new1=[]
    csv_new1.append(csv_list[0][0])
    csv_list[0]=csv_new1
    writer.writerow(csv_list)
            
       
            
            
