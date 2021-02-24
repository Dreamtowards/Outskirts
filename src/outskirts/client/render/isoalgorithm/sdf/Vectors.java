package outskirts.client.render.isoalgorithm.sdf;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.StringUtils;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.List;

public final class Vectors {


    public static Vector2f vec2(float x, float y) {
        return new Vector2f(x, y);
    }

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
    public static Vector3f vec3(List<Float> v, int begin) {
        return vec3(v.get(begin), v.get(begin+1), v.get(begin+2));
    }
    public static Vector3f vec3(List<Float> v) {
        return vec3(v, 0);
    }
    public static Vector3f vec3(String s) {
        return vec3(StringUtils.readNumbers(s, new float[3]));
    }
    public static Vector3f vec3(String[] s, int begin) {
        return vec3(Float.parseFloat(s[begin]), Float.parseFloat(s[begin+1]), Float.parseFloat(s[begin+2]));
    }
    public static Vector3f vec3(String[] s) {
        return vec3(s, 0);
    }
    public static Vector3f vec3(Vector4f v) {
        return vec3(v.x, v.y, v.z);
    }
    public static Vector3f vec3floor(Vector3f v, float u) {
        return Vector3f.floor(vec3(v), u);
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
    public static Vector4f vec4(Vector3f v, float w) {
        return vec4(v.x, v.y, v.z, w);
    }


    public static Matrix4f mat4(Matrix4f m) {
        return new Matrix4f(m);
    }


    public static AABB aabb(float mnx, float mny, float mnz, float mxx, float mxy, float mxz) {
        return new AABB(mnx, mny, mnz, mxx, mxy, mxz);
    }
    public static AABB aabb(Vector3f mn, Vector3f mx) {
        return aabb(mn.x, mn.y, mn.z, mx.x, mx.y, mx.z);
    }
    public static AABB aabb(Vector3f mn, float sz) {
        return aabb(mn, vec3(mn).add(sz));
    }
    public static AABB aabb(AABB src) {
        return aabb(src.min, src.max);
    }
    public static AABB aabb() {
        return aabb(0, 0, 0, 0, 0, 0);
    }


}
