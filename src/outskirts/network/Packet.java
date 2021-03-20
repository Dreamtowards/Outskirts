package outskirts.network;

import outskirts.event.Event;
import outskirts.util.CollectionUtils;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Packet-(Event).
 */
public abstract class Packet extends Event implements Registrable {

    public static long PROTOCOL_DIGEST = 0; // Packet Registry Hash/Digest. for verify REGISTRY.
//    private static List<Class<? extends Packet>> REGISTRY = new ArrayList<>();
    public static Registry<Packet> REGISTRY = new Registry<>();

    /**
     * note that packet must have an empty constructor for fast-instance-creating.
     */
    public Packet() {}

    public abstract void read(PacketBuffer buf) throws IOException;

    public abstract void write(PacketBuffer buf) throws IOException;


    @Override
    public String getRegistryID() {
        return getClass().getName();
    }
    @Override
    public final void setRegistryID(String registryID) {
        throw new UnsupportedOperationException();
    }

    public static Packet createPacket(int packetID) {
        try {
            return REGISTRY.values().get(packetID).getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create the Packet instance.", ex);
        }
    }

//    public static void registerPacket(Class<? extends Packet> clazz) {
//        REGISTRY.add(clazz);
//    }

//    public static int findPacketID(Class<? extends Packet> clazz) {
//        int i = REGISTRY.indexOf(clazz);
//        if (i == -1)
//            throw new NoSuchElementException("No such packet.");
//        return i;
//    }

    /**
     * calls when all Packets had been registered.
     */
    public static void buildRegistry() {
        List<String> ids = new ArrayList<>(REGISTRY.keys());
        // sort registry. ensure consistence
        CollectionUtils.quickSort(ids, Comparator.naturalOrder());
        // calculate hash
        PROTOCOL_DIGEST = ids.hashCode();
    }
}
