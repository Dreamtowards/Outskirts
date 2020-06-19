package outskirts.event.world;

import outskirts.event.Event;
import outskirts.world.World;

public abstract class WorldEvent extends Event {

    private World world;

    public WorldEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
