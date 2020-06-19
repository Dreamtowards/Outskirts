package outskirts.util.function;

/**
 * e.g interpolator apply(percent, start, end) return interpolationCurrent
 */

@FunctionalInterface
public interface TriFunction<A, B, C, R> {

    R apply(A a, B b, C c);

}
