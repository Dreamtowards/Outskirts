package outskirts.client.render;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.CollectionUtils;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

// http://www.cs.otago.ac.nz/postgrads/alexis/planeExtraction.pdf
// https://www.flipcode.com/archives/Frustum_Culling.shtml
public final class Frustum {

    /** [-x, +x, -y, +y, -z, +z] */
    private Vector4f[] ps = CollectionUtils.fill(new Vector4f[6], Vector4f::new);

    // RH -> LH.
    public void set(Matrix4f m) {
//        for (int i = 0;i < 6;i++) {
//            for (int j = 0;j < 4;j++) {
//                ps[i].setv(j, m.get(3,j) + (i%2==0?1:-1)*m.get(i/2, j));
//            }
//        }
        ps[0].set(m.m30 + m.m00, m.m31 + m.m01, m.m32 + m.m02, m.m33 + m.m03);
        ps[1].set(m.m30 - m.m00, m.m31 - m.m01, m.m32 - m.m02, m.m33 - m.m03);

        ps[2].set(m.m30 + m.m10, m.m31 + m.m11, m.m32 + m.m12, m.m33 + m.m13);
        ps[3].set(m.m30 - m.m10, m.m31 - m.m11, m.m32 - m.m12, m.m33 - m.m13);

        ps[4].set(m.m30 + m.m20, m.m31 + m.m21, m.m32 + m.m22, m.m33 + m.m23);
        ps[5].set(m.m30 - m.m20, m.m31 - m.m21, m.m32 - m.m22, m.m33 - m.m23);
    }

    public Vector4f plane(int i) {
        return ps[i];
    }

    public boolean contains(Vector3f p) {
        float x=p.x, y=p.y, z=p.z;
        return ps[0].x * x + ps[0].y * y + ps[0].z * z + ps[0].w >= 0 &&
               ps[1].x * x + ps[1].y * y + ps[1].z * z + ps[1].w >= 0 &&
               ps[2].x * x + ps[2].y * y + ps[2].z * z + ps[2].w >= 0 &&
               ps[3].x * x + ps[3].y * y + ps[3].z * z + ps[3].w >= 0 &&
               ps[4].x * x + ps[4].y * y + ps[4].z * z + ps[4].w >= 0 &&
               ps[5].x * x + ps[5].y * y + ps[5].z * z + ps[5].w >= 0;
    }

    public boolean intersects(AABB aabb) {
        float minX=aabb.min.x, minY=aabb.min.y, minZ=aabb.min.z, maxX=aabb.max.x, maxY=aabb.max.y, maxZ=aabb.max.z;
        return ps[0].x * (ps[0].x < 0 ? minX : maxX) + ps[0].y * (ps[0].y < 0 ? minY : maxY) + ps[0].z * (ps[0].z < 0 ? minZ : maxZ) >= -ps[0].w &&
               ps[1].x * (ps[1].x < 0 ? minX : maxX) + ps[1].y * (ps[1].y < 0 ? minY : maxY) + ps[1].z * (ps[1].z < 0 ? minZ : maxZ) >= -ps[1].w &&
               ps[2].x * (ps[2].x < 0 ? minX : maxX) + ps[2].y * (ps[2].y < 0 ? minY : maxY) + ps[2].z * (ps[2].z < 0 ? minZ : maxZ) >= -ps[2].w &&
               ps[3].x * (ps[3].x < 0 ? minX : maxX) + ps[3].y * (ps[3].y < 0 ? minY : maxY) + ps[3].z * (ps[3].z < 0 ? minZ : maxZ) >= -ps[3].w &&
               ps[4].x * (ps[4].x < 0 ? minX : maxX) + ps[4].y * (ps[4].y < 0 ? minY : maxY) + ps[4].z * (ps[4].z < 0 ? minZ : maxZ) >= -ps[4].w &&
               ps[5].x * (ps[5].x < 0 ? minX : maxX) + ps[5].y * (ps[5].y < 0 ? minY : maxY) + ps[5].z * (ps[5].z < 0 ? minZ : maxZ) >= -ps[5].w;
    }

}
