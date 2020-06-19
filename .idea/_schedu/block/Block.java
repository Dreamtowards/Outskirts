package outskirts.block;

import outskirts.block.state.BlockState;
import outskirts.block.state.BlockStateList;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.world.World;
import outskirts.world.chunk.Octree;

import java.util.List;

/**
 * Block's CollisionBox is one CubeBox
 * sometimes a block will not only one CubeBox, like Stairs, FenceDoor, SoulSand(?), PotionPot.
 * but Stairs can be group by little blocks, and others can be impl by Entity
 *
 * how get area-block-AABBs? in Full Octree Construct
 * for octree : world.getOctrees(AABB):
 *     for sub : octree:
 *         if (sub.aabb interest AABB)
 *             result.add(sub.aabb)
 */
public class Block implements Registrable {

    public static final Registry<Block> REGISTRY = new Registry.RegistrableRegistry<>();

    private String registryID;

    private BlockStateList blockStateList;

    private BlockState defaultBlockState;

    public Block() {
        this.blockStateList = createBlocksStateList();
        setDefaultState(blockStateList.getBaseState());
    }

    @SideOnly(Side.CLIENT)
    public void getVertexData(World world, Octree octree, List<Float> positions, List<Float> textureCoords, List<Float> normals) {

//        ChunkModelGenerator.putCube(
//                world,
//                positions, textureCoords, normals,
//                blockPos.x, blockPos.y, blockPos.z,
//                octree.depth(),
//                octree.size() * 16f,
//                GuiRenderer.DATA_RECT_TEXTURECOORDS
//        );
    }

    private BlockStateList createBlocksStateList() {
        return new BlockStateList(this);
    }

    public BlockStateList getBlockStateList() {
        return blockStateList;
    }

    public BlockState getDefaultState() {
        return defaultBlockState;
    }

    private void setDefaultState(BlockState defaultBlockState) {
        this.defaultBlockState = defaultBlockState;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}





