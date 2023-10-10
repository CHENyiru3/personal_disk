这是我个人创建的一个笔记（或者也许会被团队来使用），来记录2023年SRTP项目（浙江大学ZJE）“从bulk TCR测序数据中鉴定疾病特异T细胞受体序列”的进度。

## 背景知识学习
##### - 读文献记录
1. https://pubmed.ncbi.nlm.nih.gov/32889231/
- 大多数人类T细胞受体的结构式二硫键连接的α/β二聚体，由V（variable）,D(diversity), J(joining) gene segment进行重排或者增删核苷酸而成。
- 互补决定区：CDR。其中以CDR3对多样性的贡献最大
- CDR3能够直接与肽抗原进行通讯，当然CDR1，CDR2也可能参与抗原识别
- Global and local motifs similarity (the GLIPH algorithm): 基于CDR3区域是T细胞特异性决定性区域的假设前提，尤其考虑CDR3β, 开发了一种预测TCRs的共享特异性的聚类算法。 考虑常见的V区域的富集情况，CDR3长度，克隆拓展，共享的HLA等位基因进行评分。可以用于验证新的TCR与GLIPH确定的特异性组的隶属关系来预测新的TCR的特异性。
- TCRdist算法
基于序列的相似性，开发了一种算法允许聚类和可视化TCR曲目多样性，分析CDR1，2，3的残基，基于BLOSUM62进行打分，CDR3区域的占比会增大。可用于TCR聚类或者构建分层距离树，以分析多样性和复杂性。
- 表征T细胞库多样性和同源性的主要指标
a. ![[截屏2023-06-26 11.09.52.png|400]]
不同克隆型的有效数量，即给定的多样性的同等丰度序列的数量
where _pi_ is the frequency of sequence _i_ in the repertoire and _N_ is the total number of unique sequences. The order _α_ parametrizes the diversity index and allows to calculate different features of immune repertoire diversity.

2. https://www.ncbi.nlm.nih.gov/pmc/articles/PMC9825246/#sup1
似乎ergo-2和vibtcr都是需要MHC的肽的数据的，这我们应该是没有的，感觉不能使用这些工具。

###### -生物学知识
1. 每个T细胞有唯一的TCR克隆型：由于在T细胞发育和成熟的过程中，TCR基因通过基因重组和突变产生了巨大的多样性，每个T细胞都会持有一个独特的TCR克隆型。

2. TCR克隆型由V、D和J基因的组合决定：TCR克隆型的特定组合由V、D和J基因的选择和连接方式所决定。在T细胞发育的早期阶段，这些基因的重组和连接决定了克隆型的形成。
    
3. CDR3序列是TCR克隆型的独特标志：CDR3序列是TCR中最变异的区域，其在TCR克隆型间具有高度可变性。CDR3序列的多样性是由于基因重组、突变和选择过程中的变化所引起的。
    
4. TCR克隆型的频度和多样性：通过TCR测序技术，可以计算每个TCR克隆型在样本中的频度（或百分比），反映这个克隆型的相对丰度。此外，TCR克隆型的数量和多样性可提供信息，用于评估免疫系统的复杂程度和疾病状态。
##### 3. Quantifiable predictive features define epitope-specific T cell receptor repertoires 
- TCRdist具体是什么：比较两个TCR的差异度（或者说距离）时利用相似性对比汉明距离（similarity-weighted Hamming distance）对比两个TCR的对应CDR区域。引入间隙惩罚机制（存在gap就扣分），并且为了凸显CDR3 的重要地位，进行对CDR3的比重增加。
- ![[Pasted image 20230703150351.png|400]]
- TCRdiv：他们针对某一个库，为了测量库内的受体密度，量化聚集和分散的TCR的相对贡献，他们开发了一个算法计算nearest-neighbour score (NN-distance) 
- 小的NN距离证明，某个TCR周围具有更多相似的样本，并且说明附近有较高的采样密度。作者采用了距离目标样本最近的10%来进行评估，根据与最近邻的距离逐渐减小的权重来计算加权平均距离。
- 如何聚类：
1. 初始化：将每个TCR视为一个独立的聚类中心。此时，每个TCR都是一个单独的聚类。
    
2. 在每个步骤中，选择在距离阈值范围内具有最多邻居的TCR作为聚类中心。这意味着选择具有最多与其距离在阈值范围内的邻居TCR的TCR作为新的聚类中心。
    
3. 将选定的聚类中心及其邻居从TCR库中移除，并将它们归入相应的聚类。这意味着将与聚类中心距离在阈值范围内的TCR从TCR库中移除，并将它们归入相应的聚类。
    
4. 重复以上步骤，直到所有的TCR都被聚类。这意味着循环进行这些步骤，直到TCR库中的所有TCR都归入一个聚类为止。


- ROC曲线：横坐标是False positive rate，纵坐标是TURE positive rate
- ![[Pasted image 20230703102302.png|300]]
所以每个点就代表在不同的判断阈值的情况下的真阴率和真阳率
这时候要涉及另一个概念：AUC(area under curve)，如果AUC越大，即ROC下面的面积越大，则越能说明选择的分类器的能力越强：
- AUC = 1，是完美分类器，采用这个预测模型时，存在至少一个阈值能得出完美预测。绝大多数预测的场合，不存在完美分类器。
- 0.5 < AUC < 1，优于随机猜测。这个分类器（模型）妥善设定阈值的话，能有预测价值。
- AUC = 0.5，跟随机猜测一样（例：丢铜板），模型没有预测价值。
- AUC < 0.5，比随机猜测还差；但只要总是反预测而行，就优于随机猜测。
通过分析TCR克隆型的丰度和多样性，可以研究T细胞免疫应答，包括免疫疾病、肿瘤免疫和感染等方面。TCR克隆型的研究对于了解免疫系统的功能、异常和治疗策略的发展具有重要意义。

- 多样性判断：TCRdiv
使用辛普森多样性作为计算的准则。辛普森多样性，可以被看作是：从一堆混合的个体中随机取出两个相同的个体的概率。
具体的计算：
1. 首先明确，对比两个独立样本，如果两个样本是一样的，那么输出1，不一样，则输出0。辛普森多样性就是综合所有样本对比的预期值。
2. 在tcrdist3计算中，他们考虑使用the expected value of a Gaussian function of the inter-sample distance（如果两个独立样本相同，那么输出1，要是不一样，则输出.                          exp(-(TCRdist(a,b)/s.d.)^2）[s.d. 在单链配对时取18.45，双链配对时取36.9]最后的结果就是所谓的辛普森多样性，离1越近说明多样性越小。
3. TCRdiv=1/预期的辛普森多样性

- backgroud TCR：背景TCR，通常是作为正常的TCR样本以供对比。为了正确地评估和解释实验中观察到的TCR序列，需要将其与背景TCR进行比较。背景TCR通常代表了正常生理条件下的免疫系统中所存在的TCR多样性范围。比如在机器学习中（例如tcrdist），他就是使用了一些已知的TCR作为背景进行分类预测。

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
这里目前是采用了preset分析（内置预设参数分析），利用参数``ampliseq-tcrb-plus-cdr3``

```shell
mixcr analyze ampliseq-tcrb-plus-cdr3 \ 
input_R1.fastq.gz \ 
input_R2.fastq.gz \ (se 那么这个可以省掉)
result
```


#### 1. align
 input: QC fastq.gz files
 output: vdjca files
 这里的preset暂时选择了ampliseq-tcrb-plus-cd3
 
#### 2. assemble
input: vdjca files
output: clns files

#### 3. export
input: clns files
output: mixcr files



### 其余的编程知识学习 

#####  1. selenium为基础的爬虫
为了爬取TCRd的表格数据（这是下载下来的数据没有的，就是他们自己给这些project加上的标签）

![[step1.py]]

这是爬虫源码，但是没有包含projects（没有projects就不能证明我爬过你的网站😭）不过TCRdb的网站也太垃圾了，华中科技大学就不能上点心吗，爬虫爬不下来居然是因为网站响应太慢，真的要好好反思。
所以这个之所以是step1，那是由于其实这个程序爬下来的数据储存在csv表格里是格式有问题的，所以后面还有step2，step3进行格式规范和添加部分内容。
