package PA2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DistanceVectorCalculator
{

    public static void main(String[] args)
    {
        ArrayList<String[]> matrix = readAdjacencyMatrix();
        /* //Test readAdjacencyMatrix
        for(String[] row : matrix)
        {
            for(String item : row)
            {
                System.out.print(item + " ");
            }
            System.out.println();
        }
         */

    }

    public static ArrayList<String[]> readAdjacencyMatrix()
    {
        ArrayList<String[]> matrix = new ArrayList<>();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("network.txt"));
            String input = reader.readLine();

            while(input != null)
            {
                String[] weight_vector = input.split("\t");
                matrix.add(weight_vector);
                input = reader.readLine();
            }
            reader.close();
        }
        catch(IOException e)
        {
            System.err.println("Failed to read in Adjacency Matrix.");
            System.exit(0);
        }

        return matrix;
    }
}