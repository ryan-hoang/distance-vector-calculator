package PA2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public final class NodeRunnable implements Runnable {
    private HashMap<Integer, Integer> network;
    private ArrayList<Integer> weightVector;
    private ServerSocket myServerSocket;
    private ArrayList<Integer> neighbors;
    private int id;

    @Override
    public void run()
    {
        System.out.println(Thread.currentThread().getName() + "is online. | " + "node: " + id + " | " + weightVector.toString() + " | neighbors:" + neighbors + " | listening on port: " + myServerSocket.getLocalPort() + " | " + network.toString());

        while(true)
        {
            try
            {
                Socket client = myServerSocket.accept();

                ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());

                ObjectInputStream input = new ObjectInputStream(client.getInputStream());

                System.out.println(Thread.currentThread().getName() + ": Accepted Incoming connection originating from port: " + client.getLocalPort());

                @SuppressWarnings("unchecked")
                ArrayList<Object> data = (ArrayList) input.readObject();


                for(Object o : data)
                {
                    String s = (String) o;
                    System.out.println(Thread.currentThread().getName() + ": " + s);
                }

                output.writeObject(data);


            }
            catch(IOException e)
            {
                System.err.println(Thread.currentThread().getName() + " Failed to accept incoming TCP client connection.");
            }
            catch(ClassNotFoundException e)
            {
                System.err.println(Thread.currentThread().getName() + "Failed to deserialize data.");
            }
        }


    }

    public NodeRunnable(ArrayList<Integer> weights, ServerSocket socket, ArrayList<Integer> neigh, int id)
    {
        this.weightVector = weights;
        this.myServerSocket = socket;
        this.neighbors = neigh;
        this.id = id;
    }

    public void setNetwork(HashMap<Integer, Integer> net)
    {
        this.network = new HashMap<Integer,Integer>(net);
    }

    //Updates this node's weight vector using a weight vector received from one of its neighbors.
    public void updateWeightVector(ArrayList<Integer> weights) //TODO - write this
    {

    }

    //opens a client TCP socket to the neighbor
    public void notifyNeighbor(int id)
    {
        int port = network.get(id);
        try (Socket socket = new Socket(InetAddress.getLocalHost(),port))
        {

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            ArrayList<Object> payload = generatePayload(); //TODO - Actually have to write the method to package the stuff to be sent.

            output.writeObject(payload);

            @SuppressWarnings("unchecked")
            ArrayList<Object> response = (ArrayList<Object>) input.readObject();

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

        //TODO - add logic for what you get back from the other node. (Did it update or not?)


    }


    public static ArrayList<Object> generatePayload() //TODO - Write this.
    {

        return new ArrayList<Object>();
    }

    //Closes this nodes ServerSocket when its time to shutdown.
    public void cleanup()
    {
         try
        {
            this.myServerSocket.close();
        }
        catch(IOException e)
        {
            System.err.println("Failed to close a ServerSocket.");
        }
    }


}
