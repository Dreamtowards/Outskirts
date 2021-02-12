package outskirts.init;

import outskirts.entity.Entity;
import outskirts.entity.EntityStaticMesh;
import outskirts.entity.player.EntityPlayer;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.util.Side;

public final class Entities {

    public static final EntityPlayer PLAYER = register(Side.CURRENT.isClient() ? new EntityPlayerSP() : new EntityPlayerMP());

    public static final EntityStaticMesh STATIC_MESH = register(new EntityStaticMesh());


    private static <T extends Entity> T register(Entity entity) {
        return (T)Entity.REGISTRY.register(entity);
    }

    static void init() {}

}
