package outskirts.block.state;

import outskirts.block.Block;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;

import java.util.*;

public class BlockStateList {

    private Property[] properties;
    private List<BlockState> blockStates = new ArrayList<>();

    /**
     * @param properties properties order is constant, its effects state-meta
     */
    public BlockStateList(Block block, Property... properties) {
        this.properties = properties;

        for (List<Integer> values : Maths.cartesianProduct(getPropertiesAllowedValues(properties))) {
            Map<Property, Integer> states = CollectionUtils.asMap(new HashMap<>(), Arrays.asList(properties), values);

            blockStates.add(new BlockState(block, Collections.unmodifiableMap(states), properties));
        }
    }

    public static List<List<Integer>> getPropertiesAllowedValues(Property[] properties) {
        List<List<Integer>> result = new ArrayList<>();
        for (Property property : properties) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0;i < property.capacity();i++) {
                list.add(i);
            }
            result.add(list);
        }
        return result;
    }

    public BlockState findState(int statemeta) {
        for (BlockState state : blockStates) {
            if (state.statemeta() == statemeta) {
                return state;
            }
        }
        return null;
    }

    public BlockState getBaseState() {
        return blockStates.get(0);
    }

}
