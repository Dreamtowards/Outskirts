package outskirts.event.world.terrain;

import outskirts.world.terrain.Terrain;

public class TerrainLoadedEvent extends TerrainEvent {

    public TerrainLoadedEvent(Terrain terrain) {
        super(terrain);
    }

}
