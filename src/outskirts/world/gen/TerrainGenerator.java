package outskirts.world.gen;

import outskirts.client.material.Model;
import outskirts.world.World;
import outskirts.world.terrain.Terrain;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TerrainGenerator {

    private static BufferedImage DEF_TEXTURE;
    private static BufferedImage DEF_HEIMAP;

    static {
        try {
            DEF_TEXTURE = ImageIO.read(new File("def_tex.png"));
            DEF_HEIMAP = ImageIO.read(new File("def_tex.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Terrain generateTerrain(World world, int x, int z) {
        Terrain terrain = new Terrain(world, x, z);

        terrain.texture = DEF_TEXTURE;

        NoiseGenerator noise = new NoiseGenerator();
        for (int iz = 0;iz < Terrain.DENSITY;iz++) {
            for (int ix = 0;ix < Terrain.DENSITY;ix++) {
                terrain.heights[ix][iz] = noise.octavesNoise(
                        (x+(float)ix/Terrain.DENSITY*Terrain.SIZE)/26f,
                        (z+(float)iz/Terrain.DENSITY*Terrain.SIZE)/26f, 4) / 5f;
            }
        }

        return terrain;
    }

}
