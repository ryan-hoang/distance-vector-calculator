package PA2;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DistanceVectorCalculator
{

    public static void main(String[] args)
    {
        Pair p = network_init();
        @SuppressWarnings("unchecked")
        ArrayList<ServerSocket> sockets = (ArrayList<ServerSocket>) p.first;
        ExecutorService exec = (ExecutorService) p.second;


        /*

        ArrayList<Object> arr = new ArrayList<>();
        arr.add("test");

        int port = sockets.get(0).getLocalPort();

        try (Socket socket = new Socket(InetAddress.getLocalHost(),port))
        {

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(arr);

            @SuppressWarnings("unchecked")
            ArrayList<Object> data = (ArrayList) input.readObject();



            for(Object o : data)
            {
                String s = (String) o;
                System.out.println(Thread.currentThread().getName() + ": " + s);
            }



        }
        catch (UnknownHostException ex)
        {

            System.out.println("Server not found: " + ex.getMessage());

        }
        catch (IOException ex)
        {

            System.out.println("I/O error: " + ex.getMessage());
        }
        catch (ClassNotFoundException e)
        {

        }
        */
    }


    public static Pair network_init()
    {
        ArrayList<ArrayList<Integer>> matrix = readAdjacencyMatrix();

        ArrayList<ServerSocket> sockets = createNodeServerSockets(matrix.size());

        ArrayList<NodeRunnable> nodes = new ArrayList<>();

        HashMap<Integer, Integer> network = new HashMap<>();

        ExecutorService exec = Executors.newCachedThreadPool();

        for(int i = 0; i < matrix.size(); i++)
        {
            ArrayList<Integer> row = matrix.get(i);
            ArrayList<Integer> neighbors = new ArrayList<>();
            for(int j = 0; j < row.size(); j++)
            {
                if(row.get(j) != 0)
                {
                    neighbors.add(j);
                }
            }

            ServerSocket s = sockets.get(i);

            network.put(i, s.getLocalPort());

            NodeRunnable temp = new NodeRunnable(row, s, neighbors, i);

            nodes.add(temp);
        }

        for(NodeRunnable n : nodes) //Distribute the node to port mapping to each of the nodes.
        {
            n.setNetwork(network);
            exec.submit(n);
        }

        return new Pair(sockets, exec);
    }

    //Helper method to generate the server sockets for each node in our network.
    //Generates the specified number of server sockets and returns an ArrayList of the ServerSockets.
    public static ArrayList<ServerSocket> createNodeServerSockets(int numNodes)
    {
        ArrayList<ServerSocket> serverSockets = new ArrayList<>();

        for(int i = 0; i < numNodes; i++)
        {
            try
            {
                ServerSocket temp = new ServerSocket(0);
                serverSockets.add(temp);
            }
            catch(IOException e)
            {
                System.err.println("Failed to create server socket.");
            }
        }

        return serverSockets;
    }

    public static ArrayList<ArrayList<Integer>> readAdjacencyMatrix()
    {
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("network.txt"));
            String input = reader.readLine();

            while(input != null)
            {
                ArrayList<Integer> weight_vector = new ArrayList<Integer>();
                String[] temp = input.split("\t");

                for(String s : temp)
                {
                    weight_vector.add(Integer.parseInt(s));
                }
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