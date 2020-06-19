package outskirts.block.state;

import outskirts.block.Block;
import outskirts.util.Maths;

import java.util.List;
import java.util.Map;

public final class BlockState {

    // Runtime Registry / Global BlockState Table, Gen by Block.REGISTRY.
    public static List<BlockState> REGISTRY;

    private final Block block;
    private final Map<Property, Integer> states;

    private final int statemeta;

    BlockState(Block block, Map<Property, Integer> states, Property[] properties) {
        this.block = block;
        this.states = states;

        int meta = 0;
        int off = 0;
        for (Property prop : properties) {
            meta |= states.get(prop) << off;
            off += Maths.ceil(Maths.log2(prop.capacity() - 1)); // cap-1 ..?
        }
        if (off > 32) {
            throw new IllegalStateException("Properties state-meta overload.");
        }
        this.statemeta = meta;
    }

    public Block getBlock() {
        return block;
    }

    public int statemeta() {
        return statemeta;
    }
}
