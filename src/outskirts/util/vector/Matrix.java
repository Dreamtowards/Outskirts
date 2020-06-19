package outskirts.util.vector;

/**
 * for the package.
 * from some kind to see, vector objects almost just some Data-Construct
 * it stores data, but also have some closely-connectivity calculation for them.
 * Vector Instance-Part-Class should as much as possible to simple and original, just stores data and basic-simple operation.
 * static high-level operation(like mul, dot, rotate..etc) not only makes instance simpler, also let the operation more
 * clear and Intuitive, its not needs considers the operation-owner-instance, almost just in mathematics external method way
 *
 * In this lib, the related high-level calculation impl use static method in them-local-class. and instanced-part keeping simple.
 * bt calculation func if too dispersion(like in other pkg, Utils cls), the system'll looks like a bunch loose sands and Confusion
 */
public abstract class Matrix {

    public abstract Matrix setIdentity();

    public abstract Matrix setZero();

    public abstract Matrix negate();

    public abstract Matrix transpose();

    public abstract Matrix invert();

    public abstract float determinant();



    public abstract String toString();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();
}