package outskirts.init;

import outskirts.material.Material;
import outskirts.material.MaterialDirt;
import outskirts.material.MaterialGrass;
import outskirts.material.MaterialStone;

public final class Materials {

    public static final MaterialStone STONE = register(new MaterialStone());
    public static final MaterialGrass GRASS = register(new MaterialGrass());
    public static final MaterialDirt DIRT = register(new MaterialDirt());

    private static <T> T register(Material m) {
        return (T)Material.REGISTRY.register(m);
    }

    static void init() {}
}
