package outskirts.event.world.terrain;

import outskirts.event.world.WorldEvent;
import outskirts.world.World;
import outskirts.world.terrain.Terrain;

public abstract class TerrainEvent extends WorldEvent {

    private Terrain terrain;

    public TerrainEvent(Terrain terrain) {
        super(terrain.getWorld());
        this.terrain = terrain;
    }

    public Terrain getTerrain() {
        return terrain;
    }
}
