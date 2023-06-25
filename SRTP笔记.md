这是我个人创建的一个笔记（或者也许会被团队来使用），来记录2023年SRTP项目（浙江大学国际联合学院ZJE）“从bulk TCR测序数据中鉴定疾病特异T细胞受体序列”的进度。也许最后结果会是“我们至少学到了东西”，但是要是能确实学到一些东西，也是大有益处的。
## 背景知识学习

## 上游的pipeline建立

 - ### 下载数据
```bash
 prefetch PRJ*
 ```
这一步是从NCBI的TCR库中下载下来原始数据，sra文件格式

- ### sra转fastq.gz

这一步需要用到fastq-dump，建议直接转为gz压缩格式即可
```shell
fastq-dump SRR*.sra --split-files --gzip -o {the file you want the output in}
```


- ### 质量控制
这一步需要用到一个软件，Trimmomatic，可以用来去除这些原始数据中质量不佳的片段，或者去除一些过短的、不符合要求的数据。

input: raw fastq.gz files

 ```shell

java -jar ~/Trimmomatic-0.39/trimmomatic-0.39.jar SE -threads 12 {inputfile} \
{output} \
ILLUMINACLIP:/mnt/volume1/2023SRTP/library/TCRdb/try/Trimmomatic-0.39/adapters/TruSeq3-SE.fa:2:30:10 \
SLIDINGWINDOW:8:25 \
LEADING:25  \
TRAILING:25

```
	
output: QC fastq.gz files

- ### MIXCR分析
这一步将采用常用的分析软件MIXCR，进行上游的初步分析。MIXCR的分析分为多步，我也将按照它的运行顺序进行讲述。

#### 1. align
 input: QC fastq.gz files

```shell
mixcr align -f -s hs -t 8 {input} {output}
```
 output: vdjca files
 
#### 2. assemble
input: vdjca files

```shell
mixcr assemble -t 8 -f {input} {output}
```

output: clns files

#### 3. export
input: clns files

```shell
mixcr exportClones -o -t -f -c TRB {input} {output}
```

output: mixcr files












### 其余的编程知识学习 

#####  1. selenium为基础的爬虫
为了爬取TCRd的表格数据（这是下载下来的数据没有的，就是他们自己给这些project加上的标签）

![[step1.py]]

这是爬虫源码，但是没有包含projects（没有projects就不能证明我爬过你的网站😭）不过TCRdb的网站也太垃圾了，华中科技大学就不能上点心吗，爬虫爬不下来居然是因为网站响应太慢，真的要好好反思。😡

所以这个之所以是step1，那是由于其实这个程序爬下来的数据储存在csv表格里是格式有问题的，所以后面还有step2，step3进行格式规范和添加部分内容。
