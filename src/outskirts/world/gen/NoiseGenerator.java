package outskirts.world.gen;

import outskirts.util.Maths;

import java.util.Random;

public class NoiseGenerator {

    private long seed = "ok".hashCode();

    /**
     * @return 0-1 float
     */
    public float noise(int i) {
        i = (int)(i * seed);
        i = (i << 13) ^ i;
        return (((i * i * 15731 + 789221) * i + 1376312589) & 0x7fffffff) / (float)0x7fffffff;
    }

    public float noise(int x, int z) {
        return noise(x * z);
    }

    public float smoothedNoise(int x, int z) {
        float corners = (noise(x-1, z-1) +
                         noise(x+1, z-1) +
                         noise(x-1, z+1) +
                         noise(x+1, z+1)) / 16f;
        float sides = (noise(x-1, z) +
                       noise(x+1, z) +
                       noise(x, z-1) +
                       noise(x, z+1)) / 8f;
        float center = noise(x, z) / 4f;
        return corners + sides + center;
    }

    public float interpolatedNoise(float x, float z) {
        int unitX = Maths.floor(x);
        float fracX = x - unitX;

        int unitZ = Maths.floor(z);
        float fracZ = z - unitZ;

        float v1 = smoothedNoise(unitX, unitZ);
        float v2 = smoothedNoise(unitX+1, unitZ);
        float v3 = smoothedNoise(unitX, unitZ+1);
        float v4 = smoothedNoise(unitX+1, unitZ+1);

        float i1 = Maths.lerp(fracX, v1, v2);
        float i2 = Maths.lerp(fracX, v3, v4);

        return Maths.lerp(fracZ, i1, i2);
    }

    public float octavesNoise(float x, float z, int octaves) {
        float total = 0;
        float persistence = 1;
        for (int i = 0;i < octaves;i++) {
            float frequency = (float) Math.pow(2, i);
            float amplitude = (float) Math.pow(persistence, i);
            total += interpolatedNoise(x * frequency, z * frequency) * amplitude;
        }
        return total;
    }










}
