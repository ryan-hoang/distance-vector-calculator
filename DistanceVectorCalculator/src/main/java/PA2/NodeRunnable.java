package PA2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public final class NodeRunnable implements Runnable {
    private HashMap<Integer, Integer> network;
    private ArrayList<Integer> weightVector;
    private ServerSocket myServerSocket;
    private ArrayList<Integer> neighbors;

    @Override
    public void run()
    {
        System.out.println(Thread.currentThread().getName() + "is online. | " + weightVector.toString() + " | neighbors:" + neighbors + " | listening on port: " + myServerSocket.getLocalPort() + " | " + network.toString());
    }

    public NodeRunnable(ArrayList<Integer> weights, ServerSocket socket, ArrayList<Integer> neigh)
    {
        this.weightVector = weights;
        this.myServerSocket = socket;
        this.neighbors = neigh;
    }

    public void setNetwork(HashMap<Integer, Integer> net)
    {
        this.network = new HashMap<Integer,Integer>(net);
    }

    //Updates this node's weight vector using a weight vector received from one of its neighbors.
    public void updateWeightVector(ArrayList<Integer> weights)
    {

    }

    //opens a client TCP socket to the neighbor
    public void notifyNeighbor()
    {

    }

    //Closes this nodes ServerSocket when its time to shutdown.
    public void cleanup() {
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
