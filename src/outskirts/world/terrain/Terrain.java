package outskirts.world.terrain;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Material;
import outskirts.client.material.Model;
import outskirts.client.material.ModelData;
import outskirts.client.render.terrain.TerrainModelGenerator;
import outskirts.entity.Entity;
import outskirts.entity.player.EntityPlayer;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.util.nbt.NBTTagList;
import outskirts.util.nbt.Savable;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.World;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Terrain implements Savable {

    public static final int DENSITY = 64; // heights fields density
    public static final int SIZE = 16;    // world coordinates

    public final int x;
    public final int z;

    public float[][] heights = new float[DENSITY][DENSITY];

    private final World world; // ref to world

    private Material material = new Material();

    public BufferedImage texture = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);

    // Players which watching this chunk (in their viewDistance
    @SideOnly(Side.SERVER)
    public List<EntityPlayerMP> listeningPlayers = new ArrayList<>();

    public Terrain(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public float getHeight(float x, float z) {
        return heights[(int)(Maths.mod(x, SIZE) / SIZE * DENSITY)]
                      [(int)(Maths.mod(z, SIZE) / SIZE * DENSITY)];
    }

    public Material getMaterial() {
        return material;
    }

    public World getWorld() {
        return world;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    @Override
    public void readNBT(NBTTagCompound tagCompound) {

        // heights map
        byte[] heimap = tagCompound.getByteArray("heightsMap");
        for (int z = 0;z < DENSITY;z++) {
            for (int x = 0; x < DENSITY; x++) {
                heights[x][z] = Float.intBitsToFloat(IOUtils.readInt(heimap, (z*DENSITY+x)*4));
            }
        }

        // texture
        try {
            byte[] tex = tagCompound.getByteArray("texture");
            texture = ImageIO.read(new ByteArrayInputStream(tex));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // entities
        NBTTagList tagEntities = tagCompound.getListTag("entities");
        for (int i = 0;i < tagEntities.size();i++) {
            NBTTagCompound tagEntity = tagEntities.getCompoundTag(i);
            Entity entity = Entity.createEntity(tagEntity);
            world.addEntity(entity); // really..? this operations wrd.
        }

    }

    @Override
    public NBTTagCompound writeNBT(NBTTagCompound tagCompound) {

        // heights map
        byte[] heimap = new byte[DENSITY * DENSITY * 4];
        for (int z = 0;z < DENSITY;z++) {
            for (int x = 0; x < DENSITY;x++) {
                IOUtils.writeInt(heimap, (z*DENSITY+x)*4, Float.floatToIntBits(heights[x][z]));
            }
        }
        tagCompound.setByteArray("heightsMap", heimap);

        // texture
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(texture, "PNG", baos);
            tagCompound.setByteArray("texture", baos.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // entities
        NBTTagList tagEntities = new NBTTagList();
        for (Entity entity : world.getEntities()) {
            if (entity instanceof EntityPlayer)
                continue;
            // when entity in this chunk (check pos
            Vector3f pos = entity.getPosition();
            if (pos.x >= this.x && pos.z >= this.z && pos.x < this.x+Terrain.SIZE && pos.z < this.z+Terrain.SIZE) {
                NBTTagCompound tagEntity = entity.writeNBT(new NBTTagCompound());
                tagEntities.add(tagEntity);
            }
        }
        tagCompound.setListTag("entities", tagEntities);


        return tagCompound;
    }

    public void _update_model() {
        ModelData mdat = new TerrainModelGenerator().generateModel(this);
//        getMaterial().setModel(Loader.loadModel(mdat.indices, mdat.positions, mdat.textureCoords, mdat.normals));
    }
    public void _update_texture() {
        getMaterial().setDiffuseMap(Loader.loadTexture(texture));
    }
    public void _set_heights_map(BufferedImage hm) {
        //Validate.isTrue(bufferedImage.getWidth() == DENSITY && bufferedImage.getHeight() == DENSITY, "Illegal heightsMap Image size.");
        for (int x = 0;x < DENSITY;x++) {
            for (int z = 0;z < DENSITY;z++) {
//                int i = bufferedImage.getRGB(x, z) & 0xFF;
                Vector4f c = Colors.fromARGB(hm.getRGB(
                        (int)((float)x/DENSITY*hm.getWidth()),
                        (int)((float)z/DENSITY*hm.getHeight())), null);
                heights[x][z] = (c.x + c.y + c.z + c.w) / 4f * 20f;
            }
        }
    }



    public static long posLong(int x, int z) {
        return (x & 0xFFFFFFFFL) << 32 | (z & 0xFFFFFFFFL);
    }
}
