/**
 * This class preprocesses a dataset. Class information is dropped and the
 * dataset is normalized to between 0 and 1
 * 
 * @author Winston Lin
 */
package P2;

import java.io.*;
import java.util.*;

public class ProcessData 
{
    String filePath = ""; // Path of the dataset
    ArrayList<String[]> original = new ArrayList<String[]>(); // Original data
    ArrayList<double[]> processed = new ArrayList<double[]>();// Processed data
    int numClass = 0;   // Number of classes in the dataset
    int numFeature = 0; // Number of features in the dataset
    String fileName = ""; // Name of the dataset
    ArrayList<Double> mins = new ArrayList<Double>(); // For denormalization
    ArrayList<Double> maxs = new ArrayList<Double>(); // For denormalization
    
    public ProcessData(String filePath) throws FileNotFoundException
    {
        this.filePath = filePath;
        File file = new File(filePath);
        Scanner input = new Scanner(file);
        while (input.hasNextLine())
        {
            String row = input.nextLine();
            String[] rowArray = row.split(",");
            original.add(rowArray);
        }
        input.close();
    }
    
/**
 *  This method stores the dataset and performs normalization
 */
    public void process()
    {
        if (filePath.equals("iris.data"))
        {
            processIris();
            numClass = 3;
            numFeature = original.get(0).length - 1;
            fileName = "Iris";
        }
        else if (filePath.equals("glass.data"))
        {
            processGlass();
            numClass = 6;
            numFeature = original.get(0).length - 2;
            fileName = "Glass";
        }
        else if (filePath.equals("spambase.data"))
        {
            processSpam();
            numClass = 2;
            numFeature = original.get(0).length - 1;
            fileName = "Spam";
        }
        // Normalize the data since K-means is a distanced-based method
        for (int i = 0; i < numFeature; i++)
        {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (int j = 0; j < processed.size(); j++)
            {
                if (processed.get(j)[i] < min)
                {
                    min = processed.get(j)[i];
                }
                if (processed.get(j)[i] > max)
                {
                    max = processed.get(j)[i];
                }
            }
            for (int k = 0; k < processed.size(); k++)
            {// Normalize all features to range from 0 to 1
                processed.get(k)[i] = (processed.get(k)[i] - min) /
                        (max - min);
            }
            mins.add(min);
            maxs.add(max);
        }   
    }
    
/**
 * This method processes the "Iris" dataset
 */
    public void processIris()
    {
        for (int i = 0; i < original.size(); i++)
        {
            double[] row = new double[original.get(i).length - 1];
            for (int j = 0; j < original.get(i).length - 1; j++)
            {
                row[j] = Double.parseDouble(original.get(i)[j]);
            }
            processed.add(row);
        }
    }
    
/**
 * This method processes the "Glass" dataset
 */
    public void processGlass()
    {
        for (int i = 0; i < original.size(); i++)
        {
            double[] row = new double[original.get(i).length - 2];
            for (int j = 1; j < original.get(i).length - 1; j++) // Drop the Id
            {
                row[j - 1] = Double.parseDouble(original.get(i)[j]);
            }
            processed.add(row);
        }
    }
    
/**
 * This method processes the "Spam" dataset
 */
    public void processSpam()
    {
        for (int i = 0; i < original.size(); i++)
        {
            double[] row = new double[original.get(i).length - 1];
            for (int j = 0; j < original.get(i).length - 1; j++)
            {
                row[j] = Double.parseDouble(original.get(i)[j]);
            }
            processed.add(row);
        }
    }
}
