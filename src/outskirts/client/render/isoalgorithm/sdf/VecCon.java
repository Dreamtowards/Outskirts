package outskirts.client.render.isoalgorithm.sdf;

import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

public class VecCon {


    public static Vector3f vec3(Vector3f v) {
        return vec3(v.x, v.y, v.z);
    }
    public static Vector3f vec3(float f) {
        return vec3(f,f,f);
    }
    public static Vector3f vec3(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    public static Vector4f vec4(float x, float y, float z, float w) {
        return new Vector4f(x, y, z, w);
    }


    public static Vector2f vec2(float x, float y) {
        return new Vector2f(x, y);
    }

}
