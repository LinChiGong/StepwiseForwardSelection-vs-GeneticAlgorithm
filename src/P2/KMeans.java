/**
 * This class implements a K-Means algorithm. Since K-Means is a distance-based
 * algorithm, this class takes in dataset that is already normalized by the
 * "ProcessData" class.
 * 
 * @author Winston Lin
 */
package P2;

import java.io.*;
import java.util.*;

public class KMeans 
{
    ArrayList<double[]> processed = new ArrayList<double[]>();// Processed data
    int numClass = 0;   // Number of classes in the dataset
    int numFeature = 0; // Number of features in the dataset
    String fileName = ""; // Name of the dataset
    
    ArrayList<ArrayList<double[]>> clusters; // Stores points for each cluster
    ArrayList<double[]> means;    // Current centroids
    ArrayList<double[]> newMeans; // New centroids
    
    public KMeans(ProcessData processedData)
    {
        this.processed = processedData.processed;
        this.numClass = processedData.numClass;
        this.numFeature = processedData.numFeature;
        this.fileName = processedData.fileName;
        // Number of clusters equals number of classes
        clusters = new ArrayList<ArrayList<double[]>>(numClass);
        means = new ArrayList<double[]>(numClass);
        newMeans = new ArrayList<double[]>(numClass);
        for (int i = 0; i < numClass; i++)
        {
            clusters.add(new ArrayList<double[]>());
            newMeans.add(new double[numFeature]); // Initialized for comparison
        }
    }

/**
 * This method performs clustering repeatedly based on the reduced feature set 
 * till the centroids converge 
 * 
 * @param featureIndices is the reduced feature set
 */
    public void fit(HashSet<Integer> featureIndices)
    {
        // Initialize means by randomly selecting k (numClass) instances
        ArrayList<Integer> randoms = new ArrayList<Integer>();
        for (int i = 0; i < processed.size(); i++)
        {
            randoms.add(i);
        }
        Collections.shuffle(randoms);
        for (int i = 0; i < numClass; i++)
        {
            means.add(processed.get(randoms.get(i)));
        }
        
        // Iterative till the means converge
        boolean isFirstRound = true;
        while (meansNotConverge())
        {
            if (!isFirstRound)
            {// Set current centroids = new centroids after the first round
                means = newMeans;
                for (int i = 0; i < clusters.size(); i++)
                {
                    clusters.get(i).clear();
                }  
            }
            // Assign each data point to a cluster
            for (int i = 0; i < processed.size(); i++)
            {
                double minDistance = Double.MAX_VALUE;
                int minCluster = 0; // Closest cluster to the data point
                for (int j = 0; j < means.size(); j++)
                {
                    double distance = getDistance(featureIndices, 
                            processed.get(i), means.get(j));
                    if (distance == minDistance)
                    {// If at equal distance, randomly assigned to one cluster
                        Random random = new Random();
                        minCluster = random.nextBoolean() ? j : minCluster;
                    }
                    else if (distance < minDistance)
                    {
                        minDistance = distance;
                        minCluster = j;
                    }
                }
                // Add the data point to the closest cluster
                clusters.get(minCluster).add(processed.get(i));
            }
            
            // Calculate new means with new clusters
            newMeans = new ArrayList<double[]>(numClass);
            int emptyClusters = 0; // Number of empty clusters
            for (int i = 0; i < clusters.size(); i++)
            {        
                if (clusters.get(i).size() == 0)
                {// If cluster is empty, defer the calculation for the new mean
                    emptyClusters++;
                    continue;
                }
                double[] meanFeatures = new double[numFeature];
                for (int index : featureIndices)
                {
                    double meanFeature = 0;
                    for (int k = 0; k < clusters.get(i).size(); k++)
                    {
                        meanFeature += clusters.get(i).get(k)[index];
                    } 
                    meanFeature /= clusters.get(i).size();
                    meanFeatures[index] = meanFeature;
                }
                newMeans.add(meanFeatures);
            }
            
            /* Use centroid of existing centroids as the new mean for each 
               empty cluster */
            for (int i = 0; i < emptyClusters; i++)
            {
                double[] meanFeatures = new double[numFeature];
                for (int index : featureIndices)
                {
                    double meanFeature = 0;
                    for (int k = 0; k < newMeans.size(); k++)
                    {
                        meanFeature += newMeans.get(k)[index];
                    } 
                    meanFeature /= newMeans.size();
                    meanFeatures[index] = meanFeature;
                }
                newMeans.add(meanFeatures);
            }     
            isFirstRound = false;
        }
    }

/**
 * This method calculate the Euclidean distance between two data points based 
 * on the reduced feature set
 *     
 * @param featureIndices is the reduced feature set
 * @param x1 is the first data point
 * @param x2 is the second data point
 * @return the Euclidean distance
 */
    public double getDistance(HashSet<Integer> featureIndices, double[] x1, 
            double[] x2)
    {
        double distance = 0;
        for (int index : featureIndices)
        {
            distance += Math.pow((x1[index] - x2[index]), 2);
        }

        return Math.sqrt(distance);
    }
    
/**
 * This method evaluates whether the centroids have converged
 * 
 * @return false if the centroids have converged
 */
    public boolean meansNotConverge()
    {
        for (int i = 0; i < newMeans.size(); i++)
        {
            for (int j = 0; j < newMeans.get(i).length; j++)
            { 
                if (means.get(i)[j] != newMeans.get(i)[j])
                {
                    return true;
                }
            }
        }
        return false;
    }

/**
 * This method calculates the Silhouette Coefficient based on current clusters
 * and the reduced feature set
 * 
 * @param featureIndices is the reduced feature set
 * @return the Silhouette Coefficient
 */
    public double silhouette(HashSet<Integer> featureIndices)
    {       
        double sumS = 0; // Sum of the the silhouette coefficients
        for (int i = 0; i < clusters.size(); i++)
        {
            for (int j = 0; j < clusters.get(i).size(); j++) // Data point Xi
            {
                // Calculate "a" against all other points within the cluster
                double a = 0;  
                for (int k = 0; k < clusters.get(i).size(); k++)
                {
                    if (j == k)
                    {
                        continue;
                    }
                    a += getDistance(featureIndices, 
                            clusters.get(i).get(j), clusters.get(i).get(k));
                    
                }
                if (clusters.get(i).size() - 1 != 0) // Else a = a / 1
                {
                    a /= (clusters.get(i).size() - 1);
                }
                // Calculate "b" against all points outside the cluster
                double minB = Double.MAX_VALUE;
                for (int m = 0; m < clusters.size(); m++)
                {
                    if (i == m || clusters.get(m).size() == 0)
                    {
                        continue;
                    }
                    double b = 0;
                    for (int n = 0; n < clusters.get(m).size(); n++)
                    {
                        b += getDistance(featureIndices, 
                               clusters.get(i).get(j), clusters.get(m).get(n));
                    }
                    b /= clusters.get(m).size();
                    if (b < minB)
                    {
                        minB = b;
                    }
                }
                // Calculate the silhouette coefficient
                double s = 0;
                if ((a > minB ? a : minB) != 0)
                {
                    s = (minB - a) / (a > minB ? a : minB);
                }
                sumS += s;
            }  
        }
        return sumS / processed.size();
    }
}
