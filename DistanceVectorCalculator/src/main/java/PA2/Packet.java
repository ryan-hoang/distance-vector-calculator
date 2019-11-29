package PA2;

import java.io.Serializable;
import java.util.ArrayList;

public final class Packet implements Serializable
{
    private final ArrayList<Integer> weight;
    private final boolean shutdown;
    private final boolean updated;
    private final boolean isControlPacket;
    private final boolean activate;
    private final int originNodeID;

    public Packet(ArrayList<Integer> weightVector, boolean shutdown, boolean updated, boolean isControlPacket, boolean activate, int id)
    {
        this.weight = new ArrayList<Integer>(weightVector);
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
    public int senderID() { return originNodeID; }
    public ArrayList<Integer> getWeightVector() { return new ArrayList<Integer>(weight); }

}
