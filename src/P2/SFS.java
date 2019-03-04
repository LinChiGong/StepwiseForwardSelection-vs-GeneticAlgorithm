/**
 * This class implements a Stepwise Forward Selection where each feature set is
 * evaluated by clustering and calculating the Silhouette Coefficient using the
 * "KMeans" class 
 * 
 * @author Winston Lin
 */
package P2;

import java.io.*;
import java.util.*;

public class SFS 
{
    ProcessData processedData; // For initializing K-Means and denormalization
    ArrayList<double[]> processed = new ArrayList<double[]>();// Processed data
    int numClass = 0;   // Number of classes in the dataset
    int numFeature = 0; // Number of features in the dataset
    String fileName = ""; // Name of the dataset

    HashSet<Integer> featureSet = new HashSet<Integer>(); // Unused features
    double bestScore = 0; // Best performance
    ArrayList<ArrayList<double[]>> clusters; // Stores points for each cluster
    
    public SFS(ProcessData processedData)
    {
        this.processedData = processedData;
        this.processed = processedData.processed;
        this.numClass = processedData.numClass;
        this.numFeature = processedData.numFeature;
        this.fileName = processedData.fileName;
        
        for (int i = 0; i < numFeature; i++)
        {
            featureSet.add(i);
        }
    }
    
/**
 * This method selects the best-performing feature set
 * 
 * @return the final feature set
 */
    public HashSet<Integer> select()
    {
        // Current feature set
        HashSet<Integer> featureIndices = new HashSet<Integer>();
        double globalBestScore = Double.MIN_VALUE;// Overall highest silhouette
        while (featureSet.size() != 0)
        {
            int bestIndex = 0; // Index of the best feature in this round
            // Highest silhouette for the current number of features
            double localBestScore = Double.MIN_VALUE;
            for (int index : featureSet)
            {
                KMeans kmeans = new KMeans(processedData);
                featureIndices.add(index);
                kmeans.fit(featureIndices);
                double score = kmeans.silhouette(featureIndices);
                clusters = kmeans.clusters;
                
                // For demonstration purpose
                printToConsole(featureIndices, score, false); 
                
                if (score > localBestScore)
                {
                    localBestScore = score;
                    bestIndex = index;
                }
                featureIndices.remove(index);
            }
            // Keep adding features if the score is improving. Break otherwise
            if (localBestScore > globalBestScore)
            {
                globalBestScore = localBestScore;
                featureSet.remove(bestIndex);
                featureIndices.add(bestIndex);
            }
            else
            {
                break;
            }
        }
        bestScore = globalBestScore;
        
        // For demonstration purpose
        printToConsole(featureIndices, globalBestScore, true);
        
        return featureIndices;
    }

/**
 * This method prints the selection process and results to the console for
 * demonstration purpose
 * 
 * @param featureIndices is the reduced feature set
 * @param score is the performance
 * @param isFinal indicates whether the feature set is the final feature set
 */
    public void printToConsole(HashSet<Integer> featureIndices, double score, 
            boolean isFinal)
    {
        if (!isFinal)
        {
            System.out.print("Feature set: [ ");
        }
        else
        {
            System.out.println("-------------------------");
            System.out.print("Final feature set: [ ");
        }
        for (int featureIndex : featureIndices)
        {
            System.out.print(featureIndex + " ");
        }
        System.out.println("]");
        // Silhouette Coefficients are rounded to five decimal places
        if (!isFinal)
        {
            System.out.println("Performance: " + Math.round(score * 100000.0) 
            / 100000.0);
        }
        else
        {
            System.out.println("Best performance: " + Math.round(score 
                    * 100000.0) / 100000.0);
            System.out.println("-------------------------");
            System.out.println("Best clusters:");
            for (int i = 0; i < numClass; i++)
            {
                System.out.println("\tCluster " + (i + 1));
                for (double[] instance : clusters.get(i))
                {
                    System.out.print("\t\t[ ");
                    for (int j = 0; j < instance.length; j++)
                    {// Instances are denormalized into their original units
                        System.out.print(Math.round((instance[j] * (
                                processedData.maxs.get(j) 
                                - processedData.mins.get(j)) 
                                + processedData.mins.get(j)) * 10.0) / 10.0 
                                + " ");
                    }
                    System.out.println("]");
                }
            }
        }
    }
   
/**
 * This method writes the final feature set, the corresponding performance, and
 * the instances in each cluster to a file
 * 
 * @param featureIndices is the final feature set
 * @throws IOException
 */
    public void writeToFile(HashSet<Integer> featureIndices) throws IOException
    {
        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(
               fileName + "-SFS-output.txt", true)));
        fout.print("Final feature set: [ ");
        for (int featureIndex : featureIndices)
        {
            fout.print(featureIndex + " ");
        }
        fout.println("]");
        fout.println("Best performance: " + Math.round(bestScore * 100000.0) 
        / 100000.0);
        fout.println("-------------------------");
        fout.println("Best clusters:");
        for (int i = 0; i < numClass; i++)
        {
            fout.println("\tCluster " + (i + 1));
            for (double[] instance : clusters.get(i))
            {
                fout.print("\t\t[ ");
                for (int j = 0; j < instance.length; j++)
                {// Instances are denormalized into their original units
                    fout.print(Math.round((instance[j] * 
                            (processedData.maxs.get(j) 
                                    - processedData.mins.get(j)) 
                            + processedData.mins.get(j)) * 10.0) / 10.0 + " ");
                }
                fout.println("]");
            }
        }
        fout.close();
    }
}
