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
        Triple t = network_init();
        @SuppressWarnings("unchecked")
        ArrayList<ServerSocket> sockets = (ArrayList<ServerSocket>) t.first;
        ExecutorService exec = (ExecutorService) t.second;
        @SuppressWarnings("unchecked")
        ArrayList<ArrayList<Integer>> matrix = (ArrayList<ArrayList<Integer>>) t.third;
        startDV(sockets, matrix);
        shutdown(sockets, exec);
    }

    public static void startDV(ArrayList<ServerSocket> nodes, ArrayList<ArrayList<Integer>> mat)
    {
        boolean updated;
        int counter = 0;

        Matrix lastMatrix = new Matrix();
        Matrix currentMatrix = new Matrix(mat);

        do {
            updated = false;

            System.out.println("Current DV matrix: ");
            System.out.println(currentMatrix.toString());
            System.out.println("Last DV matrix: ");
            System.out.println(lastMatrix.toString());

            lastMatrix = new Matrix(currentMatrix);

            for (int i = 0; i < nodes.size(); i++)
            {
                System.out.println("Round " + counter + ": Node " + i);
                ServerSocket s = nodes.get(i);

                try (Socket socket = new Socket(InetAddress.getLocalHost(),s.getLocalPort()))
                {

                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                    Packet payload = new Packet(new ArrayList<Integer>(), false, false, true, true, -1);

                    output.writeObject(payload);

                    Packet response = (Packet) input.readObject();

                    if(response.flagControlPacket() && response.flagUpdated())
                    {
                        updated = true;
                        for(int key: response.getAnswers().keySet())
                        {
                            currentMatrix.setRow(key, response.getAnswers().get(key));
                        }
                    }



                }
                catch (UnknownHostException ex)
                {
                    System.err.println(Thread.currentThread().getName() + ": UnknownHostException, failed to connect to host.");
                }
                catch (IOException ex)
                {
                    System.err.println(Thread.currentThread().getName() + ": IOException.");
                }
                catch(ClassNotFoundException e)
                {
                    System.err.println(Thread.currentThread().getName() + ": Failed to deserialize payload.");
                }
            }
            counter++;
            boolean sameOrDifferent = currentMatrix.equals(lastMatrix);
            String ans = sameOrDifferent ? "Same" : "Updated";
            System.out.println("Updated from last DV matrix or the same? " + ans);

            if(sameOrDifferent)
            {
                updated = false;
                printDV(currentMatrix);
                System.out.println("Number of rounds till convergence = " + (counter-1));
                break;
            }
        } while(updated);
    }

    public static void printDV(Matrix m)
    {
        for(int i = 0; i < m.getRowSize(); i++)
        {
            System.out.println("Node " + i + " DV = " + m.getRow(i));
        }
    }

    public static Triple network_init()
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

        return new Triple(sockets, exec, matrix);
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

    public static void shutdown(ArrayList<ServerSocket> sockets, ExecutorService exec)
    {
        for(ServerSocket sock: sockets)
        {
            try (Socket socket = new Socket(InetAddress.getLocalHost(), sock.getLocalPort()))
            {
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                Packet payload = new Packet(new ArrayList<Integer>(), true, false, true, false, -1);
                output.writeObject(payload);
            }
            catch (UnknownHostException ex)
            {
                System.err.println(Thread.currentThread().getName() + ": UnknownHostException, failed to connect to host.");
            }
            catch (IOException ex)
            {
                System.err.println(Thread.currentThread().getName() + ": IOException.");
            }
        }
        exec.shutdown();
    }
}