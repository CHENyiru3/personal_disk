---
marp: true
theme: gaia
_class: lead
paginate: true
backgroundColor: #fff
backgroundImage: url('https://marp.app/assets/hero-background.svg')
---

# Overview and comparision of ST Simulation Tools 
Chen Yiru
Zhejiang University

---
# Input data
- cell-by-feature matrix $Y \in R^{n\times m}$, n means n cells and m means m  features
-  cell-by-state-covariate matrix $X \in R^{n \times p}$ , with n cells as rows and p cell-state covariates as columns.spatial locations are stored in this matrix.
- cell-by-design-covariate matrix $Z \in R^{n\times q}$ , with n cells and q design covariantes

---

## Marginal distribution 
这是模型第一步的学习过程，只有把每个基因表达的边缘分布表示出来，才能进行下一步的联合分布学习。


第一个式子代表每个细胞的每个基因的分布的统一表达形式：
$Y_{ij}|x_i,z_i (\text{ind})\sim F_j(Y_{ij}|x_i,z_i;\mu_{ij},\sigma_{ij},p_{ij})$

其中ind代表的是独立的分布，细胞i和不同基因j之间的表达值是独立的

第二个式子, 表达了$\mu_{ij}$的计算过程：$\theta_j(\mu_{ij}) = \alpha_{j0}+\alpha_{jb_i}+\alpha_{jc_i}+f_{jc_i}(x_i)$

---

![](https://i.imgur.com/mzyRb4G.png)

---

第三个式子，表达了$\sigma_{ij}$的计算过程：$\log(\sigma_{ij}) = \beta_{j0}+\beta_{jb_i}+\beta_{jc_i}+g_{jc_i}(x_i)$

log同样也是link function。$\sigma_{ij}$捕捉到了表达值的离散程度，作用如同高斯分布中的方差（有一些分布不需要这个参数，比如泊松分布）。其中：
1. $\beta_{j0}$是基因j的离散程度的整体水平
2. $\beta_{jb_i}$是基因j在不同批次下的批次效应对于离散程度的影响（如果存在的话）
3. $\beta_{jc_i}$是基因j在不同条件下的条件效应
4. $g_{jc_i}(x_i)$的效果等同于$f_{jc_i}(x_i)$,描述了在第ci个条件下,细胞i的状态协变量xi对基因j的过度离散参数σij的影响。

---

### low-rank Gaussian process
cell-state covariates are two-dimensional spatial locations: $x_i=(x_{i1},x_{i2})^T$ 。此时$f_{jc_i}(x_i)$, $h_{jc_i}(x_i)$, $g_{jc_i}(x_i)$ 计算方法为（下面用f说明）：

$f_{jc_i}(x_i)=f_{jc_i}^{GP}(x_{i1},x_{i2},K)$


首先，第一步就是要构建基底函数矩阵，K dimension

第二步就是计算基底矩阵，得到了$K\times n$的矩阵，称为B。

第三步构建高斯过程函数，使用B的线性组合计算得到每个spot对应的值$f_{jc_i}^{GP}(x_{i1},x_{i2}) = B\alpha_i+ε$, $\alpha_i$是一个$n\times 1$的系数向量。

---

# Joint distribution 
一个细胞（spot）内的基因的联合分布表达为$F(y_i|x_i,z_i)$

将marginal distribution 的累积分布函数CDF作为输入，记为$u_{ij}=\tilde F_j(y_{ij}|x_i,z_i)$，其中$\tilde F_j(y_{ij}|x_i,z_i)$代表marginal distribution的累积分布函数。这个转换满足了copula要求的连续性要求，并且范围都在[0,1]

然后使用vine copula开始计算：$$u_i=(u_{i1},\dots,u_{im})^T$$
这个向量其实就是一个细胞中，联合所有基因的边缘分布表达式的向量.我们的目标是估计这些$u_i$向量的联合分布$C(u_i|x_i,z_i)$对于Vine Copula,文中说明了使用rvinecopulib包中的vinecop()函数对C(ui*|xi,zi)进行参数估计，作者们具体也没有细究数学层面的，直接调包解决问题。根据Copula理论，C(ui*|xi,zi)就等价于原始数据yi的联合分布F(yi|xi,zi)。因此最后结果是：
$$\hat F(y_i|x_i,z_i)=\hat C(\tilde F_1(y_{ij}|x_i,z_i),\dots, \tilde F_m(y_{ij}|x_i,z_i))$$

# Generation 
目的： generate stimulated data, $Y'\in R^{n'\times m}$ ,which contains n′ synthetic cells and the same m features as in the training data。

模拟的数据依据: 
1. 综上所述拟合出的marginal distribution和joint distribution
2. resample或者其他方法，输入新的一组X' andZ'(n' cells) 

## 模拟过程：
### 1. sample joint distribution
$$(U_{i'1},\dots,U_{i'm})^T \sim \hat C((U_{i'1},\dots,U_{i'm})^T|x_{i'},z_{i'})$$
where $$i = 1,...,n$$
### 2. sample marginal distribution
和当时求marginal distribution一样，这里保留了之前训练得来的参数，将新的数据X‘ ，Z'带入。新的关于细胞基因表达的矩阵$Y_{i'j}$ 的conditional distribution也被求了出来$$\hat F_j(.|x_{i'},z_{i'})=F_j(.|x_{i'},z_{i'};\hat \mu_{i'j},\hat \sigma_{i'j})$$$$\theta_j(\hat \mu_{i'j}) = \alpha_{j0}+\alpha_{jb_{i'}}+\alpha_{jc_{i'}}+f_{jc_{i'}}(x_i')$$
$$\log(\hat \sigma_{i'j}) = \beta_{j0}+\beta_{jb_{i'}}+\beta_{jc_{i'}}+g_{jc_{i'}}(x_i')$$
### 3. final step
for cell i', the gene expression m-dimensional feature vector is $(Y_{i'1},\dots, Y_{i'm})$
where:$$Y_{i'j}=\hat F_j^{-1}(U_{i'j}|x_{i'},z_{i'})$$$$j = 1,\dots,m$$
这一步$Y_{i'j}=\hat F_j^{-1}(U_{i'j}|x_{i'},z_{i'})$是为了从联合分布$C$和边缘分布$F_j$中生成新的基因表达值$Y_{i'j}$。

具体来说:
1. 我们已经从第一步得到了联合分布$C$的一个样本$(U_{i'1},...,U_{i'm})$,其中每个$U_{i'j}$服从边缘分布$F_j$的累积分布函数(CDF)。
2. 现在我们需要从这些$U_{i'j}$值中反过来得到相应的$Y_{i'j}$值。这就需要利用之前估计的边缘分布$\hat F_j$的逆过程。
3. $\hat F_j^{-1}(U_{i'j}|x_{i'},z_{i'})$的含义是:对于给定的$U_{i'j}$值和新的协变量$(x_{i'}, z_{i'})$, 通过$\hat F_j$的逆过程计算出相应的$Y_{i'j}$值。
4. 这里的$\hat F_j^{-1}$是$\hat F_j$的逆函数或广义逆函数,具体取决于$\hat F_j$的分布形式。比如如果$\hat F_j$是正态分布,那么$\hat F_j^{-1}$就是正态分布的分位数函数(quantile function)。
5. 对于每个$j=1,...,m$,我们都这样计算一次,最终得到一个长度为$m$的向量$(Y_{i'1},...,Y_{i'm})$,它就是我们生成的第$i'$个细胞的基因表达值。


