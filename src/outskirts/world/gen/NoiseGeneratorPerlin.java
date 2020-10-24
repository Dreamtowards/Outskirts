package outskirts.world.gen;

import outskirts.util.CollectionUtils;
import outskirts.util.Maths;

import java.util.Random;

// Lattice-based GradientNoise/type
/*
 Based on original implemention by Ken Perlin. @2002 IMPROVED NOISE.
 https://mrl.nyu.edu/~perlin/noise/
 */
public class NoiseGeneratorPerlin {

    public float noise(float x) {
        int X = Maths.floor(x) & 0xFF;
        x -= Maths.floor(x);
        float u = Maths.fade(x);
        return Maths.lerp(u, grad(perm[X], x), grad(perm[X+1], x-1)) * 2f;
    }

    public float noise(float x, float y) {
        int X = Maths.floor(x) & 0xFF;
        int Y = Maths.floor(y) & 0xFF;
        x -= Maths.floor(x);
        y -= Maths.floor(y);
        float u = Maths.fade(x);
        float v = Maths.fade(y);
        int A = (perm[X  ] + Y) & 0xFF;
        int B = (perm[X+1] + Y) & 0xFF;
        return Maths.lerp(v, Maths.lerp(u, grad(perm[A  ], x, y  ), grad(perm[B  ], x-1, y  )),
                             Maths.lerp(u, grad(perm[A+1], x, y-1), grad(perm[B+1], x-1, y-1)));
    }

    public float noise(float x, float y, float z) {
        int X = Maths.floor(x) & 0xFF;
        int Y = Maths.floor(y) & 0xFF;
        int Z = Maths.floor(z) & 0xFF;
        x -= Maths.floor(x);
        y -= Maths.floor(y);
        z -= Maths.floor(z);
        float u = Maths.fade(x);
        float v = Maths.fade(y);
        float w = Maths.fade(z);
        int A =  (perm[X  ] + Y) & 0xFF, AA = (perm[A  ] + Z) & 0xFF, AB = (perm[A+1] + Z) & 0xFF;
        int B =  (perm[X+1] + Y) & 0xFF, BA = (perm[B  ] + Z) & 0xFF, BB = (perm[B+1] + Z) & 0xFF;
        return Maths.lerp(w, Maths.lerp(v, Maths.lerp(u, grad(perm[AA  ], x, y  ,z  ), grad(perm[BA  ], x-1, y  , z  )),
                                           Maths.lerp(u, grad(perm[AB  ], x, y-1, z  ), grad(perm[BB], x-1, y-1, z))),
                             Maths.lerp(v, Maths.lerp(u, grad(perm[AA+1], x, y  , z-1), grad(perm[BA+1], x-1, y  , z-1)),
                                           Maths.lerp(u, grad(perm[AB+1], x, y-1, z-1), grad(perm[BB+1], x-1, y-1, z-1))));
    }

    // Fractional Brownian Motion.
    // Noise/ Fractial
    public float fbm(float x, int octives) {
        float f = 0;     // sum
        float w = 0.5f;  // wavelength
        for (int i = 0;i < octives;i++) {
            f += w * noise(x);
            x *= 2.0f;
            w *= 0.5f;
        }
        return f;
    }

    public float fbm(float x, float y, int octives) {
        float f = 0;
        float w = 0.5f;
        for (int i = 0;i < octives;i++) {
            f += w * noise(x, y);
            x *= 2.0f; y *= 2.0f;
            w *= 0.5f;
        }
        return f;
    }

    public float fbm(float x, float y, float z, int octives) {
        float f = 0;
        float w = 0.5f;
        for (int i = 0; i < octives;i++) {
            f += w * noise(x, y, z);
            x *= 2.0f; y *= 2.0f; z *= 2.0f;
            w *= 0.5f;
        }
        return f;
    }



    private float grad(int hash, float x) {
        return (hash & 1) == 0 ? x : -x;
    }

    private float grad(int hash, float x, float y) {
        return ((hash & 1) == 0 ? x : -x) + ((hash & 2) == 0 ? y : -y);
    }

    private float grad(int hash, float x, float y, float z) {
        int h = hash & 15;
        float u = h < 8 ? x : y;
        float v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    // Hash lookup table as defined by Ken Perlin.
    // This is a randomly arranged array of all numbers from 0-255 inclusive.
    private static final int[] DEFAULT_PERMUTATION = {151,160,137,91,90,15,
            131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
            190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
            88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
            77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
            102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
            135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
            5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
            223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
            129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
            251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
            49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
            138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    };

    // The Permutaion Lookup Table. (for runtime)
    // onemore(i_256) is actually the first element(i_0). for fast prevents access outbound.
    // i.e. perm[(255+1)%256]=perm[256]=perm[0]
    private final int[] perm = new int[257];

    public NoiseGeneratorPerlin(int[] tperm) {
        initPermutation(tperm);
    }
    public NoiseGeneratorPerlin() {
        initPermutation(DEFAULT_PERMUTATION);
    }
    public NoiseGeneratorPerlin(Random rand) {
        initPermutation(CollectionUtils.shufflei(CollectionUtils.range(256), rand));
    }

    // param tperm: Standard Permutauion Table. len256, random order array [0, 255] distinct.
    private void initPermutation(int[] tperm) {  assert tperm.length == 256;
        for (int i = 0;i < 256;i++) {  assert tperm[i] < 256;
            perm[i] = tperm[i];
        }
        perm[256] = perm[0];
    }

}
