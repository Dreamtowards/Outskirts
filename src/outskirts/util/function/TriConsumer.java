package outskirts.util.function;

/**
 * Example is using to accumulator for supplier with MapEntry
 */

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A a, B b, C c);

}
