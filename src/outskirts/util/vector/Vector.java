package outskirts.util.vector;


import outskirts.util.Maths;

import static outskirts.util.Maths.clamp;

public abstract class Vector {

    public final float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public abstract float lengthSquared();

    public abstract Vector scale(float scalar);

    public abstract Vector negate();

    public abstract Vector normalize();



    public abstract String toString();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();



    static Vector normalize(Vector vector) {
        float len = vector.length();
        if (len == 0.0F)
            throw new ArithmeticException("Zero length vector.");
        return vector.scale(1.0F / len);
    }

    static float angle(float dot, Vector a, Vector b) {  // tod0: reduce. over highlevel.
        float dls = dot / (a.length() * b.length());
        dls = Maths.clamp(dls, -1.0F, 1.0F);
        return (float) Math.acos(dls);
    }

}
