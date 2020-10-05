package outskirts.util;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.logging.Log;
import outskirts.util.vector.*;

import java.util.*;

public final class Maths {

    //float ver PI
    public static final float PI = (float)Math.PI;

    private static final float FLT_EPSILON = 1.19209290e-07f;

    // param force min/max ..? then return Math.max(Math.min(value, max), min)
    public static float clamp(float value, float a, float b) {
        float min = Math.min(a, b);
        float max = Math.max(a, b);
        if (value > max)
            return max;
        if (value < min)
            return min;
        return value;
    }

    public static int clamp(int value, int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        if (value > max)
            return max;
        if (value < min)
            return min;
        return value;
    }

    /**
     * @return a or b who closer to 0
     * rename to absmin(), and deprecated.
     */
    public static float absMin(float a, float b) {
        return Math.abs(a) < Math.abs(b) ? a : b;
    }

    /**
     * ceil/floor's result is not fraction. so int is not precision problem.
     * instead, type has more flexible to choose.
     */
    public static int ceil(float value) {
        return (int)Math.ceil(value);
    }

    // speed similar to
    // int i = (int)value; return (i <= value) ? i : i - 1;
    public static int floor(float value) {
        return (int)Math.floor(value);
    }

    // -1/0/1 is no fraction, so int is fine, and more flexible.
    // this is useful and clear in Comparator::compare
    // private: reduce duplicate...
    private static int signum(float value) {
        return (int)Math.signum(value);
    }

    /**
     * float version. because toRadians/toDegrees method are usually be used.
     * just simply convert Math.toRadians. because
     * 1. those method is high optimized when runtime by modern JVM
     * 2. people a lots trust java.lang.Math
     */
    public static float toRadians(float angdeg) {
        return (float)Math.toRadians(angdeg);
    }

    public static float toDegrees(float angrad) {
        return (float)Math.toDegrees(angrad);
    }

    public static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }

    /**
     * Linear Interpolation from start to end
     * s+(e-s)*t == s+et-st == s-st+et == (s1-st)+et == s(1-t)+et
     * @param t 0.0-1.0 if you needs return value between start and end
     */
    public static float lerp(float t, float start, float end) {
        return start + (end - start) * t;
    }

    /**
     * @return Percentage of value between start and end.
     */
    public static float inverseLerp(float value, float start, float end) {
        return (value - start) / (end - start);
    }

    /**
     * Cosine Interpolation
     */
    public static float cosp(float t) {
        return (float)(1f - Math.cos(t * Math.PI)) * 0.5f;
    }

    /**
     * Cubic Interpolation
     * Alias. SmoothStep
     * @param t as v1 as start and as v2 as end
     */
    public static float cubep(float t, float v0, float v1, float v2, float v3) {
        float P = (v3 - v2) - (v0 - v1);
        float Q = (v0 - v1) - P;
        float R = v2 - v0;
        float S = v1;
        float t2 = t * t;
        return P * (t2 * t) + Q * t2 + R * t + S;
    }

    /**
     * Hermite Interpolation
     * Tension: 1 is high, 0 normal, -1 is low
     * Bias: 0 is even, positive is towards first segment, negative towards the other
     */
    public static float hermitep(float t, float v0,float v1, float v2,float v3, float tension, float bias) {
        float t2 = t * t;
        float t3 = t2 * t;
        float m0  = (v1-v0)*(1+bias)*(1-tension)/2 +
                    (v2-v1)*(1-bias)*(1-tension)/2;
        float m1  = (v2-v1)*(1+bias)*(1-tension)/2 +
                    (v3-v2)*(1-bias)*(1-tension)/2;
        float a0 =  2*t3 - 3*t2 + 1;
        float a1 =    t3 - 2*t2 + t;
        float a2 =    t3 -   t2;
        float a3 = -2*t3 + 3*t2;
        return (a0*v1 + a1*m0 + a2*m1 + a3*v2);
    }

    public static float backease(float t, float amplitude) {
        return t*t*t - t*amplitude*(float)Math.sin(t*Math.PI);
    }

    public static float circleease(float t) {
        return 1f - (float)Math.sqrt(1f - t*t);
    }

    public static float powerease(float t, float x) {
        return (float)Math.pow(t, x);
    }

    /**
     * @return fraction 0.0-1.0
     */
    public static float frac(float x) {
        return x - Maths.floor(x);
    }

    public static float log(float x, float base) {
        return (float)(Math.log(x) / Math.log(base));
    }

    /**
     * log2 - Number of LeadingZeros
     */
    public static int log2nlz(int x) {
        if (x == 0)
            throw new ArithmeticException();
        return 31 - Integer.numberOfLeadingZeros(x);
    }

    public static float log2(float x) {
        return (float)(Math.log(x) / 0.6931471805599453D);
    }

    public static boolean fuzzyZero(float v) {
        return Math.abs(v) < FLT_EPSILON;
    }

    /**
     * floor by unitsize.
     * usage e.g get ChunkPos by a world position.
     * @return value - (value MOD unitSize)
     */
    public static float floor(float value, float unitsize) {
        return floor(value / unitsize) * unitsize;
    }
    public static int floor(float value, int unitsize) {
        return floor(value / unitsize) * unitsize;
    }

    // e.g get ViewDistanceInChunks by usually viewDistance
    public static float ceil(float value, float unitsize) {
        return ceil(value / unitsize) * unitsize;
    }
    public static int ceil(float value, int unitsize) {
        return ceil(value / unitsize) * unitsize;
    }

    /**
     * Mod calculation is different with Rem(java % operation)
     *  5 Mod 16 = 5
     * -5 Mod 16 = 11
     *  5 %(rem) 16 = 5
     * -5 %      16 =-5
     * (use for negative coordinate chunk-relative position calculation)
     */
    public static float mod(float v, float b) {
        return v - (Maths.floor(v / b) * b);
    }
    public static int mod(int v, int b) {
        return (int)mod((float)v, (float)b);
    }

    // deprecated. use sequence rotations to get the dir
    /**
     * @param pitch,yaw in radians
     */
    private static Vector3f calculateEulerDirection(float pitch, float yaw) {
        float f0 = (float)Math.cos(yaw - Maths.PI);
        float f1 = (float)Math.sin(yaw - Maths.PI);
        float f2 = (float)Math.cos(pitch);
        float f3 = (float)Math.sin(pitch);
        return new Vector3f(f1 * f2, f3, f0 * f2).normalize();
    }

    /**
     * tangent plane
     * @param n normal vector. unit length
     * @param p,q exports dest. tangent/perp of the norm
     */
    public static void computeTangentBasis(Vector3f n, Vector3f p, Vector3f q) {
        if (Math.abs(n.z) > 0.7071067811865475244008443621048490f) { // SIMDSQRT(1/2)
            // choose p in y-z plane
            float a = n.y*n.y + n.z*n.z;
            float k = 1f / (float)Math.sqrt(a);
            p.set(0, -n.z*k, n.y*k);
            // set q = n x p
            q.set(a*k, -n.x*p.z, n.x*p.y);
        } else {
            // choose p in x-y plane
            float a = n.x*n.x + n.y*n.y;
            float k = 1f / (float)Math.sqrt(a);
            p.set(-n.y*k, n.x*k, 0);
            // set q = n x p
            q.set(-n.z*p.y, n.z*p.x, a*k);
        }
    }

    /**
     * calculate a point that on the line segment AND closest to the point.
     * @param P the point which needs be closest to.
     * @param A,B the line segment
     */
    public static Vector3f findClosestPointOnLineSegment(Vector3f P, Vector3f A, Vector3f B, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        float abX=B.x-A.x, abY=B.y-A.y, abZ=B.z-A.z; // vec3 AB. A-B.
        float t = ((P.x-A.x)*abX + (P.y-A.y)*abY + (P.z-A.z)*abZ) / (abX*abX + abY*abY + abZ*abZ); // AP·AB/lenSq(AB).  this t is factor on AB LineSegment, not the actually distance.
        t = Maths.clamp(t, 0.0f, 1.0f);
        return dest.set(A).add(t*abX, t*abY, t*abZ);
    }

    /**
     * @param P the point which needs be closet to
     * @param O Ray Origin.
     * @param R Ray Direction. unit-vector.
     * @return the t term on ray-equation.
     */
    public static float findClosestPointOnRay(Vector3f P, Vector3f O, Vector3f R) {
        return Vector3f.dot(Vector3f.sub(P, O, null), R);
    }

    /**
     * @param planep a point on the plane (plane-pos
     * @param planen plane normal
     */
    public static Vector3f findClosestPointOnPlane(Vector3f P, Vector3f planep, Vector3f planen, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        float d = -(planen.x * planep.x + planen.y * planep.y + planen.z * planep.z);
        float t = planen.x * P.x + planen.y * P.y + planen.z * P.z - d;
        return dest.set(P.x - t * planen.x, P.y - t * planen.y, P.z - t * planen.z);
    }

    /**
     * Reference: Book "Real-Time Collision Detection" chapter 5.1.5 "Closest Point on Triangle to Point"
     */
    public static Vector3f findClosestPointOnTriangle(Vector3f P, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        float abX = v1.x - v0.x, abY = v1.y - v0.y, abZ = v1.z - v0.z;
        float acX = v2.x - v0.x, acY = v2.y - v0.y, acZ = v2.z - v0.z;
        float apX = P.x - v0.x, apY = P.y - v0.y, apZ = P.z - v0.z;
        float d1 = abX * apX + abY * apY + abZ * apZ;
        float d2 = acX * apX + acY * apY + acZ * apZ;
        if (d1 <= 0.0f && d2 <= 0.0f)
            return dest.set(v0); // POINT_ON_TRIANGLE_VERTEX_0
        float bpX = P.x - v1.x, bpY = P.y - v1.y, bpZ = P.z - v1.z;
        float d3 = abX * bpX + abY * bpY + abZ * bpZ;
        float d4 = acX * bpX + acY * bpY + acZ * bpZ;
        if (d3 >= 0.0f && d4 <= d3)
            return dest.set(v1); // POINT_ON_TRIANGLE_VERTEX_1
        float vc = d1 * d4 - d3 * d2;
        if (vc <= 0.0f && d1 >= 0.0f && d3 <= 0.0f) {
            float v = d1 / (d1 - d3);
            return dest.set(v0).add(v*abX, v*abY, v*abZ); //POINT_ON_TRIANGLE_EDGE_01
        }
        float cpX = P.x - v2.x, cpY = P.y - v2.y, cpZ = P.z - v2.z;
        float d5 = abX * cpX + abY * cpY + abZ * cpZ;
        float d6 = acX * cpX + acY * cpY + acZ * cpZ;
        if (d6 >= 0.0f && d5 <= d6)
            return dest.set(v2); // POINT_ON_TRIANGLE_VERTEX_2
        float vb = d5 * d2 - d1 * d6;
        if (vb <= 0.0f && d2 >= 0.0f && d6 <= 0.0f) {
            float w = d2 / (d2 - d6);
            return dest.set(v0).add(w*acX, w*acY, w*acZ); // POINT_ON_TRIANGLE_EDGE_20
        }
        float va = d3 * d6 - d5 * d4;
        if (va <= 0.0f && d4 - d3 >= 0.0f && d5 - d6 >= 0.0f) {
            float w = (d4 - d3) / (d4 - d3 + d5 - d6);
            return dest.set(v1).add(w*(v2.x-v1.x), w*(v2.y-v1.y), w*(v2.z-v1.z)); // POINT_ON_TRIANGLE_EDGE_12
        }
        float denom = 1.0f / (va + vb + vc);
        float v = vb * denom;
        float w = vc * denom;
        return dest.set(v0).add(abX*v, abY*v, abZ*v).add(acX*w, acY*w, acZ*w); // POINT_ON_TRIANGLE_FACE
    }

    /**
     * @param O RayOrigin.
     * @param R RayDirection
     * @param A a point on the Plane
     * @param N PlaneNormal.
     * @return term t in the ray-equation P=O+tR.
     *         NaN when parallels to the Plane.
     *         >0 intersects. ==NaN non-intersects.
     */
    public static float intersectRayPlanef(Vector3f O, Vector3f R, Vector3f A, Vector3f N) {
        Vector3f OA = Vector3f.sub(A, O, null); //todo opts
        return Vector3f.dot(OA, N) / Vector3f.dot(R, N);
    }
    public static boolean intersectRayPlane(Vector3f raypos, Vector3f raydir, Vector3f planepos, Vector3f planenorm, Ref<Float> t) {
        float f = Maths.intersectRayPlanef(raypos, raydir, planepos, planenorm);
        if (Float.isNaN(f)) return false;
        t.value=f;
        return true;
    }

    /**
     * rfrom JOML.
     * Reference: <a href="https://dl.acm.org/citation.cfm?id=1198748">An Efficient and Robust Ray–Box Intersection</a>
     */
    public static boolean intersectRayAabb(float originX, float originY, float originZ, float dirX, float dirY, float dirZ,
                                          float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vector2f result) {
        float invDirX = 1.0f / dirX, invDirY = 1.0f / dirY, invDirZ = 1.0f / dirZ;
        float tNear, tFar, tymin, tymax, tzmin, tzmax;
        if (invDirX >= 0.0f) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar)
            return false;
        if (invDirZ >= 0.0f) {
            tzmin = (minZ - originZ) * invDirZ;
            tzmax = (maxZ - originZ) * invDirZ;
        } else {
            tzmin = (maxZ - originZ) * invDirZ;
            tzmax = (minZ - originZ) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar)
            return false;
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        tNear = Math.max(tzmin, tNear);
        tFar = Math.min(tzmax, tFar);
        if (tNear <= tFar && tFar >= 0.0f) {
            result.x = tNear;
            result.y = tFar;
            return true;
        }
        return false;
    }
    public static boolean intersectRayAabb(Vector3f rayOrigin, Vector3f rayDir, AABB aabb, Vector2f result) {
        return intersectRayAabb(rayOrigin.x, rayOrigin.y, rayOrigin.z, rayDir.x, rayDir.y, rayDir.z,
                aabb.min.x, aabb.min.y, aabb.min.z, aabb.max.x, aabb.max.y, aabb.max.z, result);
    }

    public static boolean intersectRayAabb2d(Vector2f raypos, Vector2f raydir, Vector2f aabbmin, Vector2f aabbmax, Vector2f result) {
        float invDirX = 1f / raydir.x, invDirY = 1f / raydir.y;  // opti for div.
        float txmin, txmax, tymin, tymax;
        if (invDirX >= 0) {
            txmin = (aabbmin.x - raypos.x) * invDirX;  // positive x-distan * 1f/(d·UNIT_X)
            txmax = (aabbmax.x - raypos.x) * invDirX;
        } else {
            txmin = (aabbmax.x - raypos.x) * invDirX;
            txmax = (aabbmin.x - raypos.x) * invDirX;
        }
        if (invDirY >= 0) {
            tymin = (aabbmin.y - raypos.y) * invDirY;
            tymax = (aabbmax.y - raypos.y) * invDirY;
        } else {
            tymin = (aabbmax.y - raypos.y) * invDirY;
            tymax = (aabbmin.y - raypos.y) * invDirY;
        }
        if (tymax < txmin || txmax < tymin)
            return false;
        float near = Math.max(txmin, tymin);
        float far  = Math.min(txmax, tymax);
        if (near <= far && far >= 0.0f) {
            result.set(near, far);
            return true;
        }
        return false;
    }

    public static boolean intersectRayTriangle(Vector3f raypos, Vector3f raydir, Vector3f A, Vector3f B, Vector3f C, Ref<Float> t) {
        Vector3f N = Vector3f.trinorm(A, B, C, null, null);
        if (!intersectRayPlane(raypos, raydir, A, N, t))
            return false;
        Vector3f P = new Vector3f(raypos).addScaled(t.value, raydir);
        Vector3f AB = Vector3f.sub(B, A, null);
        Vector3f AC = Vector3f.sub(C, A, null);
        Vector3f AP = Vector3f.sub(P, A, null);
        Vector3f ABNorm = Vector3f.cross(AB, N, null);
        if (Vector3f.dot(ABNorm, AC) > 0) ABNorm.negate();
        if (Vector3f.dot(ABNorm, AP) > 0) return false;

        Vector3f BC = Vector3f.sub(C, B, null);
        Vector3f BA = Vector3f.sub(A, B, null);
        Vector3f BP = Vector3f.sub(P, B, null);
        Vector3f BCNorm = Vector3f.cross(BC, N, null);
        if (Vector3f.dot(BCNorm, BA) > 0) BCNorm.negate();
        if (Vector3f.dot(BCNorm, BP) > 0) return false;

        Vector3f CA = Vector3f.sub(A, C, null);
        Vector3f CB = Vector3f.sub(B, C, null);
        Vector3f CP = Vector3f.sub(P, C, null);
        Vector3f CANorm = Vector3f.cross(CA, N, null);
        if (Vector3f.dot(CANorm, CB) > 0) CANorm.negate();
        if (Vector3f.dot(CANorm, CP) > 0) return false;
        return true;
    }


    /**
     * "Real-Time Collision Detection" chapter 3.4 "Barycentric Coordinates"
     */
//    public static Vector3f calculateBarycentric(Vector3f P, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f dest) {
//        if (dest == null) dest = new Vector3f();
//        float _v0x = v1.x-v0.x, _v0y = v1.y-v0.y, _v0z = v1.z-v0.z,
//              _v1x = v2.x-v0.x, _v1y = v2.y-v0.y, _v1z = v2.z-v0.z,
//              _v2x =  P.x-v0.x, _v2y =  P.y-v0.y, _v2z =  P.z-v0.z;
//        float d00 = _v0x*_v0x + _v0y*_v0y + _v0z*_v0z;
//        float d01 = _v0x*_v1x + _v0y*_v1y + _v0z*_v1z;
//        float d11 = _v1x*_v1x + _v1y*_v1y + _v1z*_v1z;
//        float d20 = _v2x*_v0x + _v2y*_v0y + _v2z*_v0z;
//        float d21 = _v2x*_v1x + _v2y*_v1y + _v2z*_v1z;
//        float denom = d00 * d11 - d01 * d01; // maybe zero!
//        if (denom == 0) {
//            GuiScreen3DVertices._TMP_DEF_INST.vertices.clear();
//            GuiScreen3DVertices._TMP_DEF_INST.vertices.add(new GuiScreen3DVertices.Vert("A", v0));
//            GuiScreen3DVertices._TMP_DEF_INST.vertices.add(new GuiScreen3DVertices.Vert("B", v1));
//            GuiScreen3DVertices._TMP_DEF_INST.vertices.add(new GuiScreen3DVertices.Vert("C", v2));
//            GuiScreen3DVertices._TMP_DEF_INST.vertices.add(new GuiScreen3DVertices.Vert("P", P));
//            Log.LOGGER.info("ERR.Na"+" .barErr, A:"+v0+", B:"+v1+", C:"+v2+", P:"+P+ " (how about (0.5f, 0.5f, 0.0f)");
////            throw new RuntimeException("barErr, A:"+v0+", B:"+v1+", C:"+v2+", P:"+P); // todo: zero problem
//        }
//        float v = (d11 * d20 - d01 * d21) / denom;
//        float w = (d00 * d21 - d01 * d20) / denom;
//        return dest.set(1.0f - v - w, v, w);
//    }
    public static Vector3f calculateBarycentric(Vector3f P, Vector3f A, Vector3f B, Vector3f C, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        float ABx=B.x-A.x, ABy=B.y-A.y, ABz=B.z-A.z, // B-A, AB
              ACx=C.x-A.x, ACy=C.y-A.y, ACz=C.z-A.z, // C-A, AC
              APx=P.x-A.x, APy=P.y-A.y, APz=P.z-A.z; // P-A, AP
        float dABAB = ABx*ABx + ABy*ABy + ABz*ABz; // AB·AB
        float dABAC = ABx*ACx + ABy*ACy + ABz*ACz;
        float dACAC = ACx*ACx + ACy*ACy + ACz*ACz;
        float dAPAB = APx*ABx + APy*ABy + APz*ABz;
        float dAPAC = APx*ACx + APy*ACy + APz*ACz;
        float denom = dABAB * dACAC - dABAC * dABAC; // maybe zero!
        if (denom == 0) {
            Log.LOGGER.info("barErr. denome==0");
            return dest.set(0.5F, 0.5F, 0.0F);
        }
        float v = (dACAC * dAPAB - dABAC * dAPAC) / denom;
        float w = (dABAB * dAPAC - dABAC * dAPAB) / denom;
        return dest.set(1.0f - v - w, v, w);
    }
    /**
     * @param x,y the point, oriented from left-top
     * @param dest the Vector2f is stories in Heap.. dest for cache supports. cause this method be used too much times
     */
    public static Vector2f calculateNormalDeviceCoords(float x, float y, float width, float height, Vector2f dest) {
        if (dest == null)
            dest = new Vector2f();
        return dest.set(
                    (x /  width * 2f) - 1f,
                1 - (y / height * 2f)
        );
    }

    /**
     * @return xy are oriented left-top 2d rate-coordinate and always in 0-1, z if bigger than 0, that the point is
     *         front in head(should be visible), if lesser than 0, that is behind in head(should't be visible)
     */
    public static Vector4f calculateDisplayPosition(Vector3f worldpoint, Matrix4f projectionMatrix, Matrix4f viewMatrix, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();
        dest.set(worldpoint.x, worldpoint.y, worldpoint.z, 1.0f);

        Matrix4f.transform(viewMatrix, dest);
        Matrix4f.transform(projectionMatrix, dest);

        dest.x /= dest.w;
        dest.y /= dest.w;

        dest.x =      (dest.x + 1f) / 2f;
        dest.y = 1 - ((dest.y + 1f) / 2f);

        return dest;
    }

    // space optimi
    /**
     * note that there are some expensive operations like mat4::inverse mat4::new whether time or space
     */
    public static Vector3f calculateWorldRay(float vx, float vy, float vwidth, float vheight, Matrix4f projectionMatrix, Matrix4f viewMatrix) {

        //to Viewport Coordinates: xy = (p.x, height-p.y)

        //to NDC Coordinates
        Vector2f ndcCoords = calculateNormalDeviceCoords(vx, vy, vwidth, vheight, null);

        //to Clip (Projection) Coordinates Ray
        Vector4f clipCoords = new Vector4f(ndcCoords.x, ndcCoords.y, -1f, 1f);

        //to Eye (View) Coordinates Ray
        Matrix4f inverseProjectionMatrix = new Matrix4f(projectionMatrix).invert();
        Vector4f eyeCoords = Matrix4f.transform(inverseProjectionMatrix, clipCoords);
        eyeCoords = new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);

        //to World Coordinates Ray
        Matrix4f inverseViewMatrix = new Matrix4f(viewMatrix).invert();
        Vector4f worldRay = Matrix4f.transform(inverseViewMatrix, eyeCoords);
        Vector3f normalizedWorldRay = new Vector3f(worldRay.x, worldRay.y, worldRay.z).normalize();

        return normalizedWorldRay;
    }

    private static Matrix4f lookAtViewMatrix(Vector3f eye, Vector3f center, Vector3f up, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();
        Vector3f F = Vector3f.sub(center, eye, null).normalize();
        return Maths.createViewMatrix(eye, lookAt(F, up, null), dest);
    }

    // RH. f=[0, 0, -1], up=[0, 1, 0] == Identity Matrix.
    /**
     * @param f Forward. LookingAt direction. unit-vector.
     * @param up the Roll Up-Localspace-Dir. unit-vector.
     */
    public static Matrix3f lookAt(Vector3f f, Vector3f up, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();
        Vector3f s = Vector3f.cross(f, up, null).normalize();  // Side (Right
        Vector3f u = Vector3f.cross(s, f, null);               // actual Up. (the UP perp to the Forward.
        return dest.set(
                s.x, u.x, -f.x,
                s.y, u.y, -f.y,
                s.z, u.z, -f.z
        );
    }

    // RH_ZO -> LH_ZO
    public static Matrix4f createPerspectiveProjectionMatrix(float fov, float width, float height, float near, float far, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();
        float aspectRatio = width / height;
        float scaleY = (float)(1f / Math.tan(fov / 2f));
        float scaleX = scaleY / aspectRatio;
        float frustumLength = far - near;

        return dest.set(scaleX, 0, 0, 0,
                0,  scaleY, 0, 0,
                0, 0, -((near + far) / frustumLength), -((2f * near * far) / frustumLength),
                0, 0, -1f, 0);
    }

    public static Matrix4f createOrthographicProjectionMatrix(float width, float height, float length, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();
        return dest.set(
                2f / width, 0, 0, 0,
                0, 2f / height, 0, 0,
                0, 0, -2f / length, 0,
                0, 0, 0, 1
        );
    }

    public static Matrix4f createModelMatrix(Vector3f position, Vector3f scale, Matrix3f rotation, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();
        dest.setIdentity();

        Matrix4f.translate(position, dest);

        Matrix4f.mul3x3(dest, rotation, dest); // rotate

        Matrix4f.scale(scale, dest);

        return dest;
    }

    public static Matrix4f createViewMatrix(Vector3f position, Matrix3f rotation, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();
        dest.setIdentity();

        // negated rotation. because when you look left, actually is scene rotate right.
//        Quaternion.toMatrix(rotation.negate(), dest);
        Matrix4f.mul3x3(dest, rotation.transpose(), dest);
        rotation.transpose(); //recover back, use transpose instead of invert() because there is otho matrix

        // negate position. because when you walk forward, actually is scene translate backward.
        Matrix4f.translate(position.negate(), dest);
        position.negate(); //recover back

        return dest;
    }

    /** NormalMatrix will right to Scale and Rotate a NormalVector, but Matrix.invert() very expensive */
    public static Matrix3f createNormalMatrix(Matrix4f modelMatrix) {
        Matrix3f normalMatrix = new Matrix3f();

        Matrix4f normMat4f = new Matrix4f(modelMatrix).invert();
        normMat4f.transpose();

        normalMatrix.m00 = normMat4f.m00;
        normalMatrix.m01 = normMat4f.m01;
        normalMatrix.m02 = normMat4f.m02;

        normalMatrix.m10 = normMat4f.m10;
        normalMatrix.m11 = normMat4f.m11;
        normalMatrix.m12 = normMat4f.m12;

        normalMatrix.m20 = normMat4f.m20;
        normalMatrix.m21 = normMat4f.m21;
        normalMatrix.m22 = normMat4f.m22;

        return normalMatrix;
    }

    // requires triangle faces. positions:vec3, textureCoords:vec2.
    public static void computeTangentCoordinates(int[] indices, float[] positions, float[] textureCoords, float[] out_tangents) {
        Vector3f P1 = new Vector3f(), P2 = new Vector3f(), P3 = new Vector3f();
        Vector2f T1 = new Vector2f(), T2 = new Vector2f(), T3 = new Vector2f();
        for (int i = 0;i < indices.length;i+=3) {
            // init related verts.
            int ele1 = indices[i], ele2 = indices[i+1], ele3 = indices[i+2];
            Vector3f.set(P1, positions, ele1*3);
            Vector3f.set(P2, positions, ele2*3);
            Vector3f.set(P3, positions, ele3*3);
            Vector2f.set(T1, textureCoords, ele1*2);
            Vector2f.set(T2, textureCoords, ele2*2);
            Vector2f.set(T3, textureCoords, ele3*2);

            Vector3f E1 = Vector3f.sub(P2, P1, null);
            Vector3f E2 = Vector3f.sub(P3, P1, null);
            Vector2f D1 = Vector2f.sub(T2, T1, null);
            Vector2f D2 = Vector2f.sub(T3, T1, null);

//            Matrix2f ML = new Matrix2f(
//                    D1.x, D1.y,
//                    D2.x, D2.y
//            );
//
//            try {
//                ML.invert();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                Log.LOGGER.info("failed inv. i:{}. ML:{}", i, ML);
//            }

//            Vector3f T = new Vector3f(
//                    ML.m00*E1.x+ML.m01*E2.x, ML.m00*E1.y+ML.m01*E2.y, ML.m00*E1.z+ML.m01*E2.z
//            ).normalize();
//            Vector3f B = new Vector3f(
//                    ML.m10*E1.x+ML.m11*E2.x, ML.m10*E1.y+ML.m11*E2.y, ML.m10*E1.z+ML.m11*E2.z
//            ).normalize();

            float f = 1.0f / (D1.x*D2.y - D2.x*D1.y);

            Vector3f T = new Vector3f(
                    f*(D2.y*E1.x-D1.y*E2.x),
                    f*(D2.y*E1.y-D1.y*E2.y),
                    f*(D2.y*E1.z-D1.y*E2.z)
            );//.normalize();
            Vector3f B = new Vector3f(
                    f*(-D2.x*E1.x+D1.x*E2.x),
                    f*(-D2.x*E1.y+D1.x*E2.y),
                    f*(-D2.x*E1.z+D1.x*E2.z)
            );//.normalize();
            out_tangents[ele1*3]   = T.x;
            out_tangents[ele1*3+1] = T.y;
            out_tangents[ele1*3+2] = T.z;

            out_tangents[ele2*3]   = T.x;
            out_tangents[ele2*3+1] = T.y;
            out_tangents[ele2*3+2] = T.z;

            out_tangents[ele3*3]   = T.x;
            out_tangents[ele3*3+1] = T.y;
            out_tangents[ele3*3+2] = T.z;
//            Log.LOGGER.info("i:{}, T:{}, B:{}", i, T, B);
        }
    }

    public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> out = new ArrayList<>();
        CartesianProduct.product(lists, out, new LinkedList<>(), 0);
        return out;
    }

    private static final class CartesianProduct {
        /**
         * @param lists [[1, 2, 3, 4], [a, b], [x, y, z]]
         * @param out [[1, a, x], [1, a, y], [1, a, z], [1, b, x], ...]  total lists[i].size
         * @param stack presents full/part of a combination (TMP_STACK_TRANS
         * @param i (current stack depth
         */
        private static <T> void product(List<List<T>> lists, List<List<T>> out, List<T> stack, int i) {
            if (i == lists.size()) {
                out.add(new ArrayList<>(stack));
                return;
            }
            for (T e : lists.get(i)) {

                stack.add(e); // push(e)

                product(lists, out, stack,i+1);

                stack.remove(stack.size()-1); // pop()
            }
        }

    }

}
