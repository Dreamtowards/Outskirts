package outskirts.world.gen;

import outskirts.util.Maths;

import java.util.Random;

public abstract class NoiseGenerator {

    public static float hash(int i) {
        i = (i << 13) ^ i;
        return (((i * i * 15731 + 789221) * i + 1376312589) & 0x7fffffff) / (float)0x7fffffff;
    }

}
