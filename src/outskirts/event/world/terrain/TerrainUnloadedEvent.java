package outskirts.event.world.terrain;

import outskirts.world.terrain.Terrain;

public class TerrainUnloadedEvent extends TerrainEvent {

    public TerrainUnloadedEvent(Terrain terrain) {
        super(terrain);
    }

}
