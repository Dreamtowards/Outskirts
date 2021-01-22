package outskirts.init;

import outskirts.material.Material;
import outskirts.material.MaterialStone;

public final class Materials {

    public static final MaterialStone STONE = register(new MaterialStone());

    private static <T> T register(Material m) {
        return (T)Material.REGISTRY.register(m);
    }

    static void init() {}
}
