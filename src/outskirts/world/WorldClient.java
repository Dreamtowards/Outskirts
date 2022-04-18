package outskirts.world;

import outskirts.client.Outskirts;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.Maths.floor;

@SideOnly(Side.CLIENT)
public class WorldClient extends World {

    @Override
    public void onTick() {
        super.onTick();

        Vector3f dpos = Vector3f.floor(vec3(Outskirts.getPlayer().position()), 16);
        final int vd = 1;

        // Load up.
        for (int dx = -vd;dx <= vd;dx++) {
            for (int dy = -vd;dy <= vd;dy++) {
                for (int dz = -vd;dz <= vd;dz++) {

                    provideChunk(vec3(dpos).add(dx*16, dy*16, dz*16));
                }
            }
        }

        // Unload.
        for (Chunk chunk : new ArrayList<>(getLoadedChunks())) {
            Vector3f cpos = chunk.getPosition();
            if (abs(dpos.x-cpos.x) > 16*vd || abs(dpos.y-cpos.y) > 16*vd || abs(dpos.z-cpos.z) > 16*vd) {
                unloadChunk(chunk);
            }
        }
    }

}
