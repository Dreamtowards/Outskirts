package outskirts.world;

import outskirts.world.terrain.Terrain;

public class WorldClient extends World {

    @Override
    protected Terrain loadTerrain(int x, int z) {
        return new Terrain(this, x, z); // just an empty chunk (and always waiting server to send data to fill/modify the chunk
    }
}
