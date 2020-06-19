package outskirts.util;

import outskirts.util.vector.Vector4f;

public final class Colors {

    private Colors() {}

    public static final Vector4f WHITE = new Vector4f(1, 1, 1, 1);
    public static final Vector4f WHITE40 = new Vector4f(1, 1, 1, 0.4f);
    public static final Vector4f WHITE30 = new Vector4f(1, 1, 1, 0.3f);
    public static final Vector4f WHITE20 = new Vector4f(1, 1, 1, 0.2f);
    public static final Vector4f WHITE10 = new Vector4f(1, 1, 1, 0.1f);
    public static final Vector4f WHITE05 = new Vector4f(1, 1, 1, 0.05f);

    public static final Vector4f BLACK = new Vector4f(0, 0, 0, 1);
    public static final Vector4f BLACK40 = new Vector4f(0, 0, 0, 0.4f);
    public static final Vector4f BLACK10 = new Vector4f(0, 0, 0, 0.1f);

    public static Vector4f DARK_BLUE = fromRGB(0, 0, 170);    //&1
    public static Vector4f DARK_GREEN = fromRGB(0, 170, 0);   //&2
    public static Vector4f DARK_AQUA = fromRGB(0, 170, 170);  //&3
    public static Vector4f DARK_RED = fromRGB(170, 0, 0);     //&4
    public static Vector4f DARK_PURPLE = fromRGB(170, 0, 170);//&5
    public static Vector4f GOLD = fromRGB(255, 170, 0);       //&6
    public static Vector4f GRAY = fromRGB(170, 170, 170);     //&7
    public static Vector4f DARK_GRAY = fromRGB(85, 85, 85);   //&8
    public static Vector4f BLUE = fromRGB(85, 85, 255);       //&9
    public static Vector4f GREEN = fromRGB(85, 255, 85);      //&a
    public static Vector4f AQUA = fromRGB(85, 255, 255);      //&b
    public static Vector4f RED = fromRGB(255, 85, 85);        //&c
    public static Vector4f PURPLE = fromRGB(288, 85, 255);    //&d
    public static Vector4f YELLOW = fromRGB(255, 255, 85);    //&e

    public static Vector4f FULL_R = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    public static Vector4f FULL_G = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
    public static Vector4f FULL_B = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);

    // GL format
    public static Vector4f fromRGBA(int rgba, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();
        return dest.set(
                ((rgba >> 24) & 0xFF) / 255f,
                ((rgba >> 16) & 0xFF) / 255f,
                ((rgba >> 8)  & 0xFF) / 255f,
                (rgba         & 0xFF) / 255f
        );
    }

    // jSwing/awt format
    public static Vector4f fromARGB(int argb, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();
        return dest.set(
                ((argb >> 16) & 0xFF) / 255f,
                ((argb >> 8)  & 0xFF) / 255f,
                (argb         & 0xFF) / 255f,
                ((argb >> 24) & 0xFF) / 255f
        );
    }

    private static Vector4f fromRGB(int r, int g, int b) {
        return new Vector4f(r / 255f, g / 255f, b / 255f, 1);
    }

    public static int toARGB(Vector4f color) {
        return  (((int)(color.w*255) & 0xFF) << 24) |
                (((int)(color.x*255) & 0xFF) << 16) |
                (((int)(color.y*255) & 0xFF) << 8 ) |
                  (int)(color.z*255) & 0xFF;
    }
}
