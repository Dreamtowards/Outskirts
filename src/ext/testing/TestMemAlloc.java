package ext.testing;

import org.junit.Test;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

public class TestMemAlloc {

    /**
     * -XX:-PrintGCTimeStamps
     */
    @Test
    public void stack() {
        double d = 0;

        AABB aabb = new AABB(0, 0, 0,10, 10, 10);
        long s = System.currentTimeMillis();

        for (int i = 0;i < 1_000_000;i++) {

            d += AABB.volume(aabb);
        }
        System.out.println(System.currentTimeMillis() - s);
        System.out.println("d:"+d);
    }

}
