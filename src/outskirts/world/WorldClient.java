package outskirts.world;

import outskirts.client.Outskirts;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;

import java.util.ArrayList;

import static outskirts.util.Maths.floor;
import static outskirts.util.logging.Log.LOGGER;

@SideOnly(Side.CLIENT)
public class WorldClient extends World {

    {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Vector3f cenPos = Outskirts.getPlayer().position();
                    int cenX=floor(cenPos.x,16), cenZ=floor(cenPos.z,16);
                    //                int sz = 1;
                    for (int i = -sz;i <= sz;i++) {
                        for (int j = -sz;j <= sz;j++) {
                            provideChunk(cenX+i*16, cenZ+j*16);
                        }
                    }
                    for (Chunk c : new ArrayList<>(getLoadedChunks())) {
                        if (Math.abs(c.x-cenX) > sz*16 || Math.abs(c.z-cenZ) > sz*16 || Outskirts.getWorld() == null)
                            unloadChunk(c);
                    }
                    if (Outskirts.getWorld() == null)
                        break;


                    Thread.sleep(20);
                } catch (Exception ex) {
                    ex.printStackTrace();
//                    break;
                }
            }
            LOGGER.info("ChunkLoad Thread Done.");
        });
        t.setDaemon(true);
        t.start();
    }
}
