package outskirts.world.gen;

import outskirts.client.material.Model;
import outskirts.world.World;
import outskirts.world.terrain.Terrain;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static outskirts.world.terrain.Terrain.VERT_SIZE;

public class TerrainGenerator {

    private static BufferedImage DEF_TEXTURE;

    static {
        try {
            DEF_TEXTURE = ImageIO.read(new File("def_tex.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Terrain generateTerrain(World world, int x, int z) {
        Terrain terrain = new Terrain(world, x, z);

        terrain.texture = DEF_TEXTURE;

        NoiseGenerator noise = new NoiseGenerator();
        for (int iz = 0;iz < VERT_SIZE;iz++) {
            for (int ix = 0;ix < VERT_SIZE;ix++) {
                terrain.heights[ix][iz] = noise.octavesNoise(
                        (x+(float)ix/VERT_SIZE*Terrain.SIZE)/20f,
                        (z+(float)iz/VERT_SIZE*Terrain.SIZE)/20f, 4) * 8f;
            }
        }

        return terrain;
    }

}
