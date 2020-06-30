package outskirts.util.vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    static float angle(float dot, Vector a, Vector b) {
        float dls = dot / (a.length() * b.length());
        // dls = clamp(dls, -1.0F, 1.0F);
        assert dls <= 1.0F && dls >= -1.0F;
        return (float) Math.acos(dls);
    }


    private static final Pattern SIMPLE_NUM_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    public static float[] fromString(CharSequence s, float[] dest) {
        Matcher m = SIMPLE_NUM_PATTERN.matcher(s);
        for (int i = 0;i < dest.length;i++) {
            if (!m.find())
                throw new IllegalArgumentException();
            dest[i] = Float.parseFloat(m.group());
        }
        return dest;
    }
}
