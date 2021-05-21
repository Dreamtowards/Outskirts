package general.lang.reflect;


class Field extends AnnotatedElement {

    Class getDeclaringClass();

    String getName();

    int getModifiers();

    Class getType();

    public static Object get(Field field, Object obj);

    public static Object set(Field field, Object obj, Object value);

    array<Annotation> getAnnotations();
}