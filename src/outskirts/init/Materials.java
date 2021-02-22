package outskirts.init;

import outskirts.material.*;

public final class Materials {

    public static final Material NULL = register(null);
//    public static final MaterialStone STONE = register(new MaterialStone());
public static final MaterialDirt DIRT = register(new MaterialDirt());
    public static final MaterialGrass GRASS = register(new MaterialGrass());
//    public static final MaterialDirt DIRT = register(new MaterialDirt());
//    public static final MaterialBrick BRICK = register(new MaterialBrick());

    private static <T> T register(Material m) {
        return (T)Material.REGISTRY.register(m);
    }

    static void init() {}
}
