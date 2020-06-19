package outskirts.world.chunk;

import outskirts.block.Block;
import outskirts.block.state.BlockState;
import outskirts.util.Maths;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.util.nbt.NBTTagList;
import outskirts.util.nbt.Savable;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.io.*;
import java.util.*;

public class Chunk implements Savable {

    public static final int SIZE = 16;
    public static final int CAPACITY_Y = SIZE * 16;

    private Octree[] storageArray = new Octree[16];

    public final int x;
    public final int z;

    // ref to the world
    private World world;

    public Chunk(int x, int z, World world) {
        this.x = x;
        this.z = z;
        this.world = world;

        for (int i = 0;i < storageArray.length;i++) {
            storageArray[i] = new Octree(null, new Vector3f(x, i*SIZE, z));
        }
    }

    public Octree getOctree(float x, float y, float z, int depth, boolean creating) {
        if (y < 0 || y >= CAPACITY_Y)
            return null;

        return Octree.findChild(
                Maths.mod(x, SIZE),
                Maths.mod(y, SIZE),
                Maths.mod(z, SIZE), depth, octree(y), creating);
    }

    /**
     * @param y world-coordinates.
     */
    public Octree octree(float y) {
        return storageArray[(int)y/16];
    }

    public World getWorld() {
        return world;
    }



    @Override
    public void readNBT(NBTTagCompound tagCompound) {

        // read blockstate_table
        InputStream tabis = new ByteArrayInputStream(tagCompound.getByteArray("blockstate_table"));
        List<BlockState> stateTable = readStateTable(new DataInputStream(tabis));

        // read octrees
        NBTTagList tagOctrees = tagCompound.getListTag("octrees");
        for (int i = 0;i < tagOctrees.size();i++) {
            InputStream is = new ByteArrayInputStream(tagOctrees.getByteArray(i));
            Octree.readOctree(storageArray[i], is, stateTable);
        }

    }

    @Override
    public NBTTagCompound writeNBT(NBTTagCompound tagCompound) {

        tagCompound.setLong("modify_time", System.currentTimeMillis());

        // gen local StateTable
        List<BlockState> stateTable = generateStateTable();

        // write blockstate_table
        ByteArrayOutputStream tabos = new ByteArrayOutputStream();
        writeStateTable(new DataOutputStream(tabos), stateTable);
        tagCompound.setByteArray("blockstate_table", tabos.toByteArray());

        // write octrees
        NBTTagList tagOctrees = new NBTTagList();
        for (int i = 0;i < storageArray.length;i++) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Octree.writeOctree(storageArray[i], os, stateTable);
            tagOctrees.addByteArray(os.toByteArray());
        }
        tagCompound.setListTag("octrees", tagOctrees);

        return tagCompound;
    }

    /**
     * [block-size, block-id..., state-size, block-index&&state-meta...
     */
    private void writeStateTable(DataOutput out, List<BlockState> stateTable) {
        try {
            List<Block> blockTable = new ArrayList<>();
            for (BlockState state : stateTable)
                if (!blockTable.contains(state.getBlock()))
                    blockTable.add(state.getBlock());

            out.writeShort(blockTable.size());
            for (Block block : blockTable)
                out.writeUTF(block.getRegistryID());

            out.writeShort(stateTable.size());
            for (BlockState state : stateTable) {
                out.writeShort(blockTable.indexOf(state.getBlock()));
                out.writeInt(state.statemeta());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write StateTable.");
        }
    }

    private List<BlockState> readStateTable(DataInput in) {
        try {
            short blocks = in.readShort();
            List<Block> blockTable = new ArrayList<>(blocks);
            for (int i = 0;i < blocks;i++) {
                Block block = Block.REGISTRY.get(in.readUTF());
                blockTable.add(block);
            }

            short states = in.readShort();
            List<BlockState> stateTable = new ArrayList<>(states);
            for (int i = 0;i < states;i++) {
                short blockIndex = in.readShort();
                int statemeta = in.readInt();
                BlockState state = blockTable.get(blockIndex).getBlockStateList().findState(statemeta);
                stateTable.add(state);
            }
            return stateTable;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read BlockStateTable.");
        }
    }

    private List<BlockState> generateStateTable() {
        List<BlockState> stateTable = new ArrayList<>();
        for (Octree root : storageArray) {
            Octree.forChildren(root, oct -> {
                if (oct.hasBody() && !stateTable.contains(oct.body()))
                    stateTable.add(oct.body());
            });
        }
        return stateTable;
    }


    public static long posLong(int x, int z) {
        return (x & 0xFFFFFFFFL) << 32 | (z & 0xFFFFFFFFL);
    }

}
