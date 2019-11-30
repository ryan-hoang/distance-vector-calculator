package PA2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public final class Packet implements Serializable
{
    private final ArrayList<Integer> weight;
    private final boolean shutdown;
    private final boolean updated;
    private final boolean isControlPacket;
    private final boolean activate;
    private final HashMap<Integer,ArrayList<Integer>> answers;
    private final int originNodeID;

    public Packet(ArrayList<Integer> weightVector, boolean shutdown, boolean updated, boolean isControlPacket, boolean activate, int id)
    {
        this.weight = new ArrayList<Integer>(weightVector);
        this.answers = new HashMap<Integer, ArrayList<Integer>>();
        this.shutdown = shutdown;
        this.updated = updated;
        this.isControlPacket = isControlPacket;
        this.activate = activate;
        this.originNodeID = id;
    }

    public Packet(HashMap<Integer,ArrayList<Integer>> weightVector, boolean shutdown, boolean updated, boolean isControlPacket, boolean activate, int id)
    {
        this.weight = new ArrayList<Integer>();
        this.answers = new HashMap<Integer, ArrayList<Integer>>(weightVector);
        this.shutdown = shutdown;
        this.updated = updated;
        this.isControlPacket = isControlPacket;
        this.activate = activate;
        this.originNodeID = id;
    }

    public boolean flagShutdown() { return shutdown; }
    public boolean flagControlPacket() { return isControlPacket; }
    public boolean flagUpdated() { return updated; }
    public boolean flagActivate() { return activate; }
    public HashMap<Integer, ArrayList<Integer>> getAnswers() { return new HashMap<Integer, ArrayList<Integer>>(answers); }
    public int senderID() { return originNodeID; }
    public ArrayList<Integer> getWeightVector() { return new ArrayList<Integer>(weight); }

}
