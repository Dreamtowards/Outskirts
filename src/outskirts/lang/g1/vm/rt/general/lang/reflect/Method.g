package general.lang.reflect;


class Method extends AnnotatedElement {

    Class getDeclaringClass();

    String getName();

    int getModifiers();

    Class getReturnType();

    array<Class> getPatameterTypes();

    public static Object invoke(Method method, Object obj, Object... args);

    array<Annotation> getAnnotations();

}