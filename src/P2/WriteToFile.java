/**
 * This class uses 3 feature selection methods - Stepwise Forward Selection and
 * Genetic Algorithm Selection - to solve 3 feature selection problems specific 
 * to this project. Each problem and its selection results are written to a 
 * file. All selection results for all problems are written in a final report
 * 
 * @author Winston Lin
 */
package P2;

import java.io.*;
import java.util.*;

public class WriteToFile 
{
    public static void main(String[] args) throws IOException
    {
        HashSet<Integer> sfsFeatureSet = new HashSet<Integer>();
        HashSet<Integer> gasFeatureSet = new HashSet<Integer>();
        // 3 datasets used in this project
        String[] datasets = {"iris.data", "glass.data", "spambase.data"};
        // Write to the final report
        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(
                "Compare-all-results.txt", true)));
        fout.println("Perform Stepwise Forward Selection and Genetic Algorithm"
                + " Selection on 3 datasets. Performance is measured by "
                + "calculating the Silhouette Coefficient after K-Means "
                + "clustering.");
        fout.println();
        for (String dataset : datasets)
        {
            // Preprocess and normalize each dataset
            ProcessData data = new ProcessData(dataset);
            data.process();      
            
            // Stepwise Forward Selection
            SFS sfs = new SFS(data);
            sfsFeatureSet = sfs.select();
            sfs.writeToFile(sfsFeatureSet);
            
            // Genetic Algorithm Selection
            GAS gas = new GAS(data);
            if (dataset == "spambase.data")
            {// Larger population size and number of generations for "Spam"
                gasFeatureSet = gas.select(100, 50);
            }
            else
            {
                gasFeatureSet = gas.select(25, 10);
            }
            gas.writeToFile();
            
            // Write to the final report
            fout.println(data.fileName);
            fout.println("-----------");
            fout.println("Stepwise Forward Selection:");
            fout.print("Selected features: [ ");
            for (int featureIndex : sfsFeatureSet)
            {
                fout.print(featureIndex + " ");
            }
            fout.println("]");
            // Silhouette Coefficients are rounded to five decimal places
            fout.println("Performance: " + Math.round(sfs.bestScore * 100000.0)
            / 100000.0);
            fout.println();
            
            fout.println("Genetic Algorithm Selection:");
            fout.print("Selected features: [ ");
            for (int i = 0; i < gas.numFeature; i++)
            {
                if (gas.population.get(0).featureSet.get(i) == 1)
                {
                    fout.print(i + " ");
                }
            }
            fout.println("]");
            fout.println("Performance: " + Math.round(
                    gas.population.get(0).fitness * 100000.0) / 100000.0);
            fout.println();   
        }
        fout.close();
    }
}
