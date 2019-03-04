/**
 * This class runs the whole project. It would accept an input file, process 
 * it, perform feature selection using Stepwise Forward Selection & Genetic
 * Algorithm Selection, and print the selection processes & results to the
 * console
 * 
 * @author Winston Lin
 */
package P2;

import java.io.*;
import java.util.*;

public class Main 
{
    public static void main(String[] args) throws IOException
    {
        String inputFileName;
        
        // Prompt for the dataset to be used
        Scanner scan = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter input file name: ");
        inputFileName = scan.nextLine().trim();
        System.out.println();
        
        // Preprocess and normalize the dataset
        ProcessData data = new ProcessData(inputFileName);
        data.process();
        System.out.println("Dataset has been processed and normalized");
        System.out.println();
        
        // For demonstration purpose
        System.out.print("Press 'Enter' to start selecting features using"
                + " Stepwise Forward Selection:");
        String temp = scan.nextLine().trim();
        System.out.println();
        
        // Stepwise Forward Selection
        System.out.println("Stepwise Forward Selection");
        System.out.println("--------------------------");
        SFS sfs = new SFS(data);
        sfs.select();
        System.out.println();
        
        // For demonstration purpose
        System.out.print("Press 'Enter' to start selecting features using"
                + " Genetic Algorithm Selection:");
        temp = scan.nextLine().trim();
        System.out.println();
        
        // Genetic Algorithm Selection
        System.out.println("Genetic Algorithm Selection");
        System.out.println("---------------------------");
        GAS gas = new GAS(data);
        // Use small population size and few generations for demonstration
        gas.select(10, 5); 
        
        scan.close();
    }
}
