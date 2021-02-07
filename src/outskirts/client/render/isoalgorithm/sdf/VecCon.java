package outskirts.client.render.isoalgorithm.sdf;

import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

public class VecCon {


    public static Vector3f vec3(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }
    public static Vector3f vec3(float f) {
        return vec3(f,f,f);
    }
    public static Vector3f vec3(Vector3f v) {
        return vec3(v.x, v.y, v.z);
    }
    public static Vector3f vec3(float[] v, int begin) {
        return vec3(v[begin], v[begin+1], v[begin+2]);
    }
    public static Vector3f vec3(float[] v) {
        return vec3(v, 0);
    }


    public static Vector4f vec4(float x, float y, float z, float w) {
        return new Vector4f(x, y, z, w);
    }
    public static Vector4f vec4(float f) {
        return vec4(f, f, f, f);
    }
    public static Vector4f vec4(Vector4f v) {
        return vec4(v.x, v.y, v.z, v.w);
    }


    public static Vector2f vec2(float x, float y) {
        return new Vector2f(x, y);
    }

}