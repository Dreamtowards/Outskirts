package outskirts.util.vector;

/**
 * this integer-type vector almost just stores data like pixel-coordinate,
 * so always dont needs some maths calculation functions, else'll have some problem:
 * second same-name calculation function in a system and in a similar data construction, people will confuse
 * which is correct? what different? they needs to study new way and when they do something, they always needs considers those
 * but they almost can cast vec-i to vec-f and use standard calculation in vec-f then back cast to vec-i, that makes Clear
 * and integer is not having fraction.., the precision always not enough to reach standard calculation result so have a lots unsafe
 *
 * the vec-i vec-math calculation functions just like some hidden Bombs in the system and makes more Needs-Unnecessary-"Study", Complexity, Confuse, Worries, Displeasure AND Avoid to the System
 */
public class Vector2i extends Vector {

    public static final Vector2i ZERO = new Vector2i(0, 0);

    public int x;
    public int y;

    public Vector2i() {}

    public Vector2i(Vector2i src) {
        set(src);
    }

    public Vector2i(int x, int y) {
        set(x, y);
    }

    public Vector2i set(Vector2i src) {
        return set(src.x, src.y);
    }

    public Vector2i set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public float lengthSquared() {
        return x * x + y * y;
    }

    @Override
    public Vector2i scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    @Override
    public Vector2i negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    @Override
    public Vector2i normalize() {
        return (Vector2i) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Vector2i[" + this.x + ", " + this.y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector2i && ((Vector2i) obj).x == this.x && ((Vector2i) obj).y == this.y;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
        return hash ^ hash >> 16;
    }

    public Vector2i setX(int x) {
        this.x = x;
        return this;
    }

    public Vector2i setY(int y) {
        this.y = y;
        return this;
    }

    public Vector2i add(Vector2i right) {
        return add(right.x, right.y);
    }

    public Vector2i add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2i sub(Vector2i right) {
        return sub(right.x, right.y);
    }

    public Vector2i sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
}
