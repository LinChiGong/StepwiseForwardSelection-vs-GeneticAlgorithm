/**
 * This class implements a Genetic Algorithm Selection where each feature set 
 * is evaluated by clustering and calculating the Silhouette Coefficient using 
 * the "KMeans" class 
 * 
 * @author Winston Lin
 */
package P2;

import java.io.*;
import java.util.*;

public class GAS 
{
    /* Implement the Comparable interface so that we can sort the population 
       by fitness */
    class Individual implements Comparable<Individual>
    {   
        ArrayList<Integer> featureSet = new ArrayList<Integer>();// Reduced set
        double fitness; // Silhouette Coefficient
        ArrayList<ArrayList<double[]>> clusters; // Stores clusters information
        ArrayList<double[]> means; // Stores centroids information

/**
 * This method allows sorting based on fitness        
 */
        public int compareTo(Individual individual)
        {
            return Double.compare(fitness, individual.fitness);
        }
    }
    
    ProcessData processedData; // For initializing K-Means and denormalization
    ArrayList<double[]> processed = new ArrayList<double[]>();// Processed data
    int numClass = 0;   // Number of classes in the dataset
    int numFeature = 0; // Number of features in the dataset
    String fileName = ""; // Name of the dataset
    // Stores individuals in the current population
    ArrayList<Individual> population = new ArrayList<Individual>();
    
    public GAS(ProcessData processedData)
    {
        this.processedData = processedData;
        this.processed = processedData.processed;
        this.numClass = processedData.numClass;
        this.numFeature = processedData.numFeature;
        this.fileName = processedData.fileName;
    }
   
/**
 * This method selects the best-performing feature set
 * 
 * @param populationSize is the size of the population, tunable
 * @param iteration is the number of generations, tunable
 * @return the final feature set
 */
    public HashSet<Integer> select(int populationSize, int iteration)
    {
        // Randomly initialize populations
        for (int i = 0; i < populationSize; i++)
        {
            population.add(new Individual());
            for (int j = 0; j < numFeature; j++)
            {
                double random = Math.random();
                if (random < 0.5)
                {
                    population.get(i).featureSet.add(1);
                }
                else
                {
                    population.get(i).featureSet.add(0);
                }
            }
        }
        // Determine fitness of all individuals in the population
        determineFitness();
        
        for (int i = 0; i < iteration; i++)
        {
            ArrayList<Individual> newPopulation = new ArrayList<Individual>();
            // Keep the top 10% fittest individuals
            int top10 = (populationSize * 10) / 100;
            for (int j = 0; j < top10; j++)
            {
                newPopulation.add(population.get(j));
            }
            /* From the top 50% fittest individuals, randomly select parents to 
               mate to generate new population */
            int top50 = (populationSize * 50) / 100;
            int offspringNeeded = populationSize - top10;
            for (int k = 0; k < offspringNeeded; k++)
            {
                Random random = new Random();
                int p = random.nextInt(top50);
                Individual parent1 = population.get(p);
                p = random.nextInt(top50);
                Individual parent2 = population.get(p);
                Individual offspring = mate(parent1, parent2);
                newPopulation.add(offspring);
            }
            population = newPopulation;
            // Perform mutation on new population
            mutation(0.05);
            // Calculate fitness for new population
            determineFitness();
            
            // For demonstration purpose
            printToConsole(populationSize, false);
        }
        // Return feature set of the fittest individual in the final population
        HashSet<Integer> featureIndices = new HashSet<Integer>();
        for (int i = 0; i < numFeature; i++)
        {
            if (population.get(0).featureSet.get(i) == 1) // Fittest individual
            {
                featureIndices.add(i);
            }
        }
        // For demonstration purpose
        printToConsole(populationSize, true);
        
        return featureIndices;
    }
   
/**
 * This method calculates the Silhouette Coefficient for each individual in the
 * population, set the coefficient as fitness, and sort the population by
 * fitness in descending order
 */
    public void determineFitness()
    {
        for (int i = 0; i < population.size(); i++)
        {
            KMeans kmeans = new KMeans(processedData);
            HashSet<Integer> featureIndices = new HashSet<Integer>();
            for (int j = 0; j < population.get(i).featureSet.size(); j++)
            {
                if (population.get(i).featureSet.get(j) == 1)
                {
                    featureIndices.add(j);
                }
            }
            if (featureIndices.size() == 0)
            {// Individuals with empty feature sets have 0 fitness
                population.get(i).fitness = 0;
                continue;
            }
            kmeans.fit(featureIndices);
            double score = kmeans.silhouette(featureIndices);
            population.get(i).fitness = score;
            population.get(i).clusters = kmeans.clusters;
            population.get(i).means = kmeans.means;
        }
        // Sort the population by fitness in descending order
        Collections.sort(population, Collections.reverseOrder());
    }
    
/**
 * This method creates a new individual from two parents through crossover
 * 
 * @param parent1 is the first parent
 * @param parent2 is the second parent
 * @return the new individual
 */
    public Individual mate(Individual parent1, Individual parent2)
    {
        int crossoverAt = (int) (Math.random() * numFeature);
        Individual offspring = new Individual();
        int i = 0;
        while(i < numFeature)
        {
            if (i <= crossoverAt)
            {
                offspring.featureSet.add(parent1.featureSet.get(i));
                i++;
            }
            else
            {
                offspring.featureSet.add(parent2.featureSet.get(i));
                i++;
            }
        }
        return offspring;
    }
    
/**
 * This method causes mutation on all individuals in the population
 * 
 * @param probability is the mutation probability between 0 and 1
 */
    public void mutation(double probability)
    {
        for (int i = 0; i < population.size(); i++)
        {
            for (int j = 0; j < numFeature; j++)
            {
                double random = Math.random();
                if (random < probability)
                {// Mutation happens
                    if (population.get(i).featureSet.get(j) == 1)
                    {
                        population.get(i).featureSet.set(j, 0);
                    }
                    else
                    {
                        population.get(i).featureSet.set(j, 1);
                    }
                }
            }
        }
    }

/**
 * This method prints the selection process and results to the console for
 * demonstration purpose
 * 
 * @param populationSize is the size of the population
 * @param isFinal indicates whether the population is the final population
 */
    public void printToConsole(int populationSize, boolean isFinal)
    {
        if (!isFinal)
        {
            for (int m = 0; m < populationSize; m++)
            {
                System.out.print("Individual: [ ");
                for (int n = 0; n < numFeature; n++)
                {
                    System.out.print(population.get(m).featureSet.get(n) 
                            + " ");
                }
                System.out.println("]");
                // Silhouette Coefficients are rounded to five decimal places
                System.out.println("Fitness: " + Math.round(
                        population.get(m).fitness * 100000.0) / 100000.0);
            }
            System.out.println("-------------------------");
        }
        else
        {
            System.out.print("Final feature set: [ ");
            for (int i = 0; i < numFeature; i++)
            {
                if (population.get(0).featureSet.get(i) == 1)
                {
                    System.out.print(i + " ");
                }
            }
            System.out.println("]");
            System.out.println("Best performance: " + Math.round(
                    population.get(0).fitness * 100000.0) / 100000.0);
            System.out.println("-------------------------");
            System.out.println("Best clusters:");
            for (int i = 0; i < numClass; i++)
            {
                System.out.println("\tCluster " + (i + 1));
                for (double[] instance : population.get(0).clusters.get(i))
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
 * @throws IOException
 */
    public void writeToFile() throws IOException
    {
        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(
               fileName + "-GAS-output.txt", true)));
        fout.print("Final feature set: [ ");
        for (int i = 0; i < numFeature; i++)
        {
            if (population.get(0).featureSet.get(i) == 1)
            {
                fout.print(i + " ");
            }
        }
        fout.println("]");
        fout.println("Best performance: " + Math.round(
                population.get(0).fitness * 100000.0) / 100000.0);
        fout.println("-------------------------");
        fout.println("Best clusters:");
        for (int i = 0; i < numClass; i++)
        {
            fout.println("\tCluster " + (i + 1));
            for (double[] instance : population.get(0).clusters.get(i))
            {
                fout.print("\t\t[ ");
                for (int j = 0; j < instance.length; j++)
                {// Instances are denormalized into their original units
                    fout.print(Math.round((instance[j] * (
                            processedData.maxs.get(j) 
                            - processedData.mins.get(j)) 
                            + processedData.mins.get(j)) * 10.0) / 10.0 + " ");
                }
                fout.println("]");
            }
        }  
        fout.close();
    }
}
