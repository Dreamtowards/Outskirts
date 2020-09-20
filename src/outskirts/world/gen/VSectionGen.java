package outskirts.world.gen;

import outskirts.world.Section;

public class VSectionGen {

    private NoiseGenerator noiseGenerator = new NoiseGenerator();

    public Section generate(int xBase, int zBase) {
        assert xBase%16==0 && zBase%16==0;

        Section vsection = new Section(xBase, zBase);

        for (int x = xBase;x < xBase+16;x++) {
            for (int z = zBase;z < zBase+16;z++) {
                float f = noiseGenerator.octavesNoise(x/32f, z/32f, 4);

                int y = 40+(int)(f * 4);

                for (int i = 0;i <= y;i++) {
                    vsection.setAt(x, i, z, (byte)1);
                }
            }
        }

        return vsection;
    }

}
