package outskirts.storage;

import outskirts.util.vector.Vector3f;

import java.util.Arrays;
import java.util.List;

public class SaveUtils {

    public static Vector3f vector3f(Object fls, Vector3f dest) {
        List<Float> ls = (List<Float>) fls;
        return dest.set(ls.get(0), ls.get(1), ls.get(2));
    }

    public static List<Float> vector3f(Vector3f v) {
        return Arrays.asList(v.x, v.y, v.z);
    }

}
