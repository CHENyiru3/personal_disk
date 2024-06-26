---
marp: true
theme: gaia
_class: lead
paginate: true
backgroundColor: #fff
backgroundImage: url('https://marp.app/assets/hero-background.svg')
---
<style>
section {
    font-size: 29px;

}
</style>
# Spatial Transcriptomics Simulation Tools: Overview and Comparison
Chen Yiru
Zhejiang University

---

# Input Data
- Cell-by-feature matrix $Y \in R^{n\times m}$
  - n: number of cells
  - m: number of features
- Cell-by-state-covariate matrix $X \in R^{n \times p}$
  - n: number of cells
  - p: number of cell-state covariates
  - Includes spatial locations
- Cell-by-design-covariate matrix $Z \in R^{n\times q}$
  - n: number of cells
  - q: number of design covariates

---

# Marginal Distribution
- Key step in modeling process
- Represents the distribution of each gene's expression
- Foundation for joint distribution learning

---

# Marginal Distribution Formulas

- Unified expression for each cell's gene distribution:
  $Y_{ij}|x_i,z_i (\text{ind})\sim F_j(Y_{ij}|x_i,z_i;\mu_{ij},\sigma_{ij},p_{ij})$
  - ind: independent distribution
  - Expression values are independent between cells and genes
- Calculation of $\mu_{ij}$:
  $\theta_j(\mu_{ij}) = \alpha_{j0}+\alpha_{jb_i}+\alpha_{jc_i}+f_{jc_i}(x_i)$
  - $\alpha_{j0}$: overall expression level of gene j
  - $\alpha_{jb_i}$: batch effect of gene j in different batches (if present)
  - $\alpha_{jc_i}$: condition effect of gene j under different conditions
  - $f_{jc_i}(x_i)$: effect of spatial location of spot i on the expression of gene j, implemented using Gaussian process regression
  - $\theta()$: link function

---
![bg](https://i.imgur.com/mzyRb4G.png)

---

# Marginal Distribution Formulas 
- Calculation of $\sigma_{ij}$:
  $\log(\sigma_{ij}) = \beta_{j0}+\beta_{jb_i}+\beta_{jc_i}+g_{jc_i}(x_i)$
  - Captures the dispersion of expression values
  - Similar to variance in Gaussian distribution
  - $\beta_{j0}$: overall level of dispersion for gene j
  - $\beta_{jb_i}$: batch effect on dispersion
  - $\beta_{jc_i}$: condition effect on dispersion
  - $g_{jc_i}(x_i)$: effect of cell-state covariates on overdispersion

---

## Low-rank Gaussian Process
- For spatial locations as cell-state covariates: $x_i=(x_{i1},x_{i2})^T$
- Calculation of $f_{jc_i}(x_i)$, $h_{jc_i}(x_i)$, $g_{jc_i}(x_i)$:
  $f_{jc_i}(x_i)=f_{jc_i}^{GP}(x_{i1},x_{i2},K)$
- Steps:
  1. Construct basis function matrix (K dimension)
  2. Compute basis matrix (K × n matrix, denoted as B)
  3. Construct Gaussian process function:
     $f_{jc_i}^{GP}(x_{i1},x_{i2}) = B\alpha_i+ε$, where $\alpha_i$ is an $n\times 1$ coefficient vector

---

# Joint Distribution
- Joint distribution of genes within a cell (spot): $F(y_i|x_i,z_i)$
- Input: cumulative distribution function (CDF) of marginal distributions
  - $u_{ij}=\tilde F_j(y_{ij}|x_i,z_i)$
- Vine copula for estimation:
  - $u_i=(u_{i1},\dots,u_{im})^T$
  - Estimate parameters of $C(u_i|x_i,z_i)$ using vinecop() function from rvinecopulib package
- Final result:
  $\hat F(y_i|x_i,z_i)=\hat C(\tilde F_1(y_{ij}|x_i,z_i),\dots, \tilde F_m(y_{ij}|x_i,z_i))$

---

## Data Generation
- Objective: Generate simulated data $Y'\in R^{n'\times m}$
  - n′: number of synthetic cells
  - m: same features as in the training data
- Based on:
  1. Fitted marginal and joint distributions
  2. Resampled or new input data X' and Z' (n' cells)

---

## Simulation Process
1. Sample joint distribution:
   $(U_{i'1},\dots,U_{i'm})^T \sim \hat C((U_{i'1},\dots,U_{i'm})^T|x_{i'},z_{i'})$, where $i = 1,...,n$
2. Sample marginal distribution:
   - Retain trained parameters
   - Input new data X' and Z'
   - Obtain conditional distribution of $Y_{i'j}$:
     $\hat F_j(.|x_{i'},z_{i'})=F_j(.|x_{i'},z_{i'};\hat \mu_{i'j},\hat \sigma_{i'j})$
     $\theta_j(\hat \mu_{i'j}) = \alpha_{j0}+\alpha_{jb_{i'}}+\alpha_{jc_{i'}}+f_{jc_{i'}}(x_i')$
     $\log(\hat \sigma_{i'j}) = \beta_{j0}+\beta_{jb_{i'}}+\beta_{jc_{i'}}+g_{jc_{i'}}(x_i')$

---

## Final Step
- For cell i', the gene expression m-dimensional feature vector is $(Y_{i'1},\dots, Y_{i'm})$
- $Y_{i'j}=\hat F_j^{-1}(U_{i'j}|x_{i'},z_{i'})$, where $j = 1,\dots,m$
- Purpose: Generate new gene expression values $Y_{i'j}$ from joint distribution $C$ and marginal distributions $F_j$
- Process:
  1. Obtain a sample $(U_{i'1},...,U_{i'm})$ from joint distribution $C$
  2. Calculate corresponding $Y_{i'j}$ values using the inverse of estimated marginal distributions $\hat F_j$
  3. $\hat F_j^{-1}(U_{i'j}|x_{i'},z_{i'})$: Compute $Y_{i'j}$ using the inverse of $\hat F_j$ given $U_{i'j}$ and new covariates $(x_{i'}, z_{i'})$
  4. Repeat for each $j=1,...,m$ to obtain the vector $(Y_{i'1},...,Y_{i'm})$ for the $i'$-th synthetic cell
- Generates synthetic data with statistical characteristics similar to real data, considering new covariate information

---

## SRTsim: Simulation based on Reference Data
- 注重于基因表达在空间分布的情况
- Tissue-based and domain-specific fashion
- Input requirements:
        - Reference SRT data
        - Gene expression count matrix
        - Location matrix
        - Domain annotation (for domain-specific simulation)

---

### Steps:
1. Generate location information
        - Can use reference locations or generate new locations
2. Obtain gene expression count patterns
      - Fit gene distribution patterns (e.g., negative binomial, Poisson, zero-inflated Poisson)
          - Akaike Information Criterion (AIC) balances model complexity and accuracy
          - $AIC = 2k - 2\ln(L)$, where $k$ is the number of parameters and $L$ is the likelihood function
          - Choose the model with the lowest AIC value

    - Simulate count distributions
3. Assign simulated counts to location information in synthetic data

---

### Assigning Gene Expression to Locations
- Sort genes based on expression levels in reference data
- Assign simulated gene expressions to locations based on the obtained order
- For new synthetic locations, find $k$-nearest neighbors in reference data and randomly assign counts

---

### Spider: Simulated Annealing Spatial Simulation
- Generate spatial coordinates for $N$ cells uniformly, represented as $S = (s_1, s_2, \dots, s_N)$
- Assign cell types based on prior proportions $\Pi = (\pi_1, \dots, \pi_k)$ and cell type transition probabilities matrix $P \in \mathbb{R}^{k \times k}$
- Optimize the assignment $X \in \mathbb{R}^{N \times K}$ to minimize the difference between $P$ and the transition frequency matrix calculated from the assignment:

$$\min_{X} \|P - C^{-1}X^TAX\|_F^2$$

subject to:
- $X_{ij} \in \{0, 1\}$
- $X1_K = 1_N$
- $1_N^TX = \pi$

---

### Simulated Annealing Algorithm
1. Initialization
        - Divide cell plate space into an $l \times w$ grid (cell zones)
        - Randomly assign cell types based on prior proportions ($\pi$)
2. Perform simulated annealing at the cell zone level
        - Swap cell type labels between randomly selected zones
        - Accept swaps based on the standard simulated annealing probability rule
3. Subdivision
        - Divide zones into smaller subregions and perform similar annealing process
- Repeat steps 2 and 3 until termination conditions are met

---

## Comparison with scDesign
- SRTsim and Spider generate spatial location information and gene expression (cell type) information separately and integrate them during the assignment step
- scDesign fits all information into a single model
- SRTsim and Spider offer more flexibility, while scDesign has a more direct approach
- SRTsim focuses on gene expression, while Spider focuses on cell types


