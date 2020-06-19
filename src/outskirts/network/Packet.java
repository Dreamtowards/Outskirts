package outskirts.network;

import outskirts.event.Event;
import outskirts.util.CollectionUtils;
import outskirts.util.ReflectionUtils;
import outskirts.util.registry.Registry;

import java.io.IOException;
import java.util.*;

/**
 * Packet-(Event).
 */
public abstract class Packet extends Event {

    public static long PROTOCOL_DIGEST = 0; // Packet Registry Hash/Digest. for verify REGISTRY content.
    private static List<Class<? extends Packet>> REGISTRY = new ArrayList<>();

    /**
     * note that packet must have an empty constructor for fast-instance-creating.
     */
    public Packet() {}

    public abstract void read(PacketBuffer buf) throws IOException;

    public abstract void write(PacketBuffer buf) throws IOException;





    public static Packet createPacket(int packetID) {
        try {
            return REGISTRY.get(packetID).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create the Packet instance.", ex);
        }
    }

    public static void registerPacket(Class<? extends Packet> clazz) {
        REGISTRY.add(clazz);
    }

    public static int findPacketID(Class<? extends Packet> clazz) {
        int i = REGISTRY.indexOf(clazz);
        if (i == -1)
            throw new NoSuchElementException("No such packet.");
        return i;
    }

    /**
     * calls when all Packets been registered.
     */
    public static void buildRegistry() {
        // sort registry
        CollectionUtils.quickSort(REGISTRY, Comparator.comparing(Class::getName));
        // lock registry
        REGISTRY = Collections.unmodifiableList(REGISTRY);
        // calculate hash
        long hash = 1;
        for (Class<?> c : REGISTRY)
            hash = 31*hash + c.getName().hashCode();
        PROTOCOL_DIGEST = hash;
    }
}
