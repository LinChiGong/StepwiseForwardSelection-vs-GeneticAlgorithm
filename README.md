# StepwiseForwardSelection-vs-GeneticAlgorithm

## Abstract

In this project, two algorithms – Stepwise Forward Selection and Genetic Algorithm Selection – are experimented to select features from 3 datasets obtained from the UCI Machine Learning Repository [1]. The feature selection outcomes are evaluated by the results of K-Means clustering. Both algorithms are able to select a feature set that leads to well-separated clusters for each problem. For 2 out of the 3 datasets, Stepwise Forward Selection and Genetic Algorithm Selection generate identical feature sets. For the last dataset, however, they produce very different results.

## Introduction

The main problems in this project are feature selection problems based on 3 datasets – “Iris”, “Glass”, and “Spam” with 4, 9, and 57 features respectively. All datasets contain only continuous features and have no missing values.

Stepwise Forward Selection (SFS) is a wrapper method that select features using a greedy approach. For datasets that only have few important features, SFS can select a good feature set in a relatively short time. I would expect SFS to perform well on problems where there are a lot of insignificant features. However, since SFS only makes locally optimal choice at each step, it may not be able to find the global optimum.

On the other hand, Genetic Algorithm Selection (GAS) is also a wrapper method. It uses a population-based method and therefore it is able to find the global optimum. With proper tuning and given enough time, I expect GAS to perform better, or at least no worse than SFS (If the local optimum found by SFS happens to be the global optimum as well, then GAS can only be as good as SFS).

Since both SFS and GAS are wrapper methods which generate subset and test performance repeatedly, we need a performance measure. In this project, the Silhouette Coefficient is used as the performance measure that evaluates the feature set selected by SFS and GAS in each step. Whenever, SFS or GAS generates a subset, the subset is used to perform K-Means clustering. After clusters are formed, we calculate the Silhouette Coefficient which indicates how well the clusters are separated. The better the clusters are separated, the higher the coefficient would be (ranging between -1 and +1). We define feature sets that have higher Silhouette Coefficient as the better feature sets.

## Methods

- Data processing:

  It is worth noting that K-Means is a distance-based method. That is, unit matters. All features in all datasets used in this project have varying units. For example, one feature in “Spam” ranges from 0 to 4.54 while another feature in “Spam” ranges from 1 to 1102.5. If we use the original units to calculate distance, the effect of the first feature would be seriously underestimated because while 1 unit change in the first feature is quite significant, 1 unit change in the second feature is almost negligible. As a result, all datasets are normalized before performing feature selection.

- Stepwise Forward Selection [2]:

  Features are visited sequentially. In the first round, we consider the features one-by-one and the feature that generates the highest Silhouette Coefficient is selected and kept to the next round. In each subsequent round, add the best-performing feature from the remaining features to the existing feature set if adding that feature increases the Silhouette Coefficient. Stop when no feature in the current round can improve the Silhouette Coefficient.

- Genetic Algorithm Selection [3]:

  For “Iris” and “Glass”, population size is set to 25 and number of generation is set to 10. Note that while “Iris” and “Glass” have 4 and 9 features respectively, “Spam” has 57 features. To accommodate the much higher dimension, for “Spam”, we set population size to 100 in order to increase the chance that the initial state of the population would contain an individual representing the optimal solution. Correspondingly, the generation is set to 50 to give the large population more time.

  In each generation, we keep the top 10% fittest individuals. From the top 50% individuals, we randomly select parents to mate to generate new individuals. Crossover takes place during mating. Mutations occur after the new population is formed with a probability of 0.05. For each individual in a population, the Silhouette Coefficient is calculated and set as their fitness. From the final population, we select the fittest individual as the final feature set.

- K-Means [4]:
  
  In this project, k = number of classes in the original dataset. We randomly select k instances as the initial centroids. When an instance is at equal distance to two clusters, is it randomly assigned to one of the clusters. Note that a new centroid is calculated based on instances in the cluster, so there is a problem when we encounter an empty cluster. One solution is to drop that cluster and keep only (k – 1) clusters. However, in this project, we want to keep k clusters. Thus, when there is an empty cluster, we take the mean of the existing centroids and assign it as the new centroid.

## Results

- Final feature set and best performance for each problem:

  

  Final feature set is the best set of feature(s) selected by each algorithm. Best performance is the corresponding Silhouette Coefficient calculated with clusters formed by the final feature set. 

## References

1. Dua, D. and Karra Taniskidou, E. (2017). UCI Machine Learning Repository [http://archive.ics.uci.edu/ml]. Irvine, CA: University of California, School of Information and Computer Science. 
2. An, H., Huang, D., Yao, Q. and Zhang, C. (2008). Stepwise searching for feature variables in high-dimensional linear regression. Technical report, Dept. Statistics, London School of Economics. 
3. Roeva, O., Fidanova, S., & Paprzycki, M. (2014). Population Size Influence on the Genetic and Ant Algorithms Performance in Case of Cultivation Process Modeling. Recent Advances in Computational Optimization Studies in Computational Intelligence, 107-120. doi:10.1007/978-3-319-12631-9_7 
4. Virmani D., Taneja S., Malhotra G. (2015). Normalization based K means Clustering Algorithm. International Journal of Advanced Engineering Research and Science, vol. 2, no. 2. 
5. Tran, B., Xue, B., & Zhang, M. (2017). Using Feature Clustering for GP-Based Feature Construction on High-Dimensional Data. Lecture Notes in Computer Science Genetic Programming, 210-226. doi:10.1007/978-3-319-55696-3_14

