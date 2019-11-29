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
    private HashMap<Integer, Integer> network; // maps node ID to port number.
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
                Packet received = (Packet) input.readObject();


                if(received.flagControlPacket() && received.flagActivate() && received.senderID() == -1) // received signal from the main thread to start sending info to neighbors. id -1 means its from the main thread.
                {
                    boolean updateHappenedElsewhere = false;

                    for(int i = 0; i < neighbors.size(); i++) // Start notifying my neighbors
                    {
                        Packet response = notifyNeighbor(neighbors.get(i));
                        if(response.flagControlPacket() && response.flagUpdated())
                        {
                            updateHappenedElsewhere = true;
                        }
                    }

                    if(updateHappenedElsewhere == false)
                    {
                        output.writeObject(new Packet(weightVector, true, false, true, false, id)); //send back my weight vector and signal time to stop.
                    }
                    else
                    {
                        output.writeObject(new Packet(weightVector, true, true, true, false, id)); //send back my weight vector and signal time to stop.
                    }
                    client.close();
                }
                else if(received.flagControlPacket() && received.flagShutdown() && received.senderID() == -1) // shutdown time
                {
                    output.writeObject(new Packet(weightVector, true, false, true, false, id)); //send back my weight vector and shutdown
                    client.close();
                    cleanup();
                    return;
                }
                else
                {
                    boolean update = updateWeightVector(received.getWeightVector(), received.senderID());
                    if (update)
                    {
                        output.writeObject(new Packet(null, false, true, true, false, id));
                    }
                    else
                    {
                        output.writeObject(new Packet(null, false, false, true, false, id));
                    }
                    client.close();
                }


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
        this.weightVector = new ArrayList<Integer> (weights);
        this.myServerSocket = socket;
        this.neighbors = neigh;
        this.id = id;
    }

    public void setNetwork(HashMap<Integer, Integer> net)
    {
        this.network = new HashMap<Integer,Integer>(net);
    }

    //Updates this node's weight vector using a weight vector received from one of its neighbors.
    public boolean updateWeightVector(ArrayList<Integer> otherWeightVector, int neighborID)
    {
        boolean updated = false;
        for(int i = 0; i < weightVector.size(); i++)
        {
            if(i == id || i == neighborID)
            {
                continue;
            }
            else
            {
                int currentDistance = weightVector.get(i);
                int proposedDistance = otherWeightVector.get(i) + weightVector.get(neighborID);

                if(proposedDistance < currentDistance || (currentDistance == 0 && otherWeightVector.get(i) != 0))
                {
                    weightVector.set(i,proposedDistance);
                    updated = true;
                }
            }

        }

        return updated;
    }

    //opens a client TCP socket to the neighbor
    public Packet notifyNeighbor(int id)
    {
        int port = network.get(id);
        Packet response = null;
        try (Socket socket = new Socket(InetAddress.getLocalHost(),port))
        {

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            Packet payload = new Packet(weightVector, false, false, false, false, id);

            output.writeObject(payload);

            response = (Packet) input.readObject();

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

        return response;
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
