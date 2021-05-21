package general.lang.reflect;

class Class<T> extends AnnotatedElement {

    public static Class forName(String name);

    public boolean isInstance(Object o);

    public boolean isAssigableFrom(Class subcls);

    // isInterface()
    // isArray()
    // isPrimitive()
    // isAnnotation()
    // isSynthetic()

    // isAnonymousClass()
    // isLocalClass()
    // isMemberClass()


    public String getName();

    public String getSimpleName();

    public Class getSuperclass();

    public Package getPackage();

    public int getModifiers();


    public array<Class> getClasses();

    public array<Field> getFields();

    public array<Method> getMethods();

    public final Field getField(String name);

    public final Method getMethod(String name, Class... parameterTypes);


    // getConstructors();
    // getConstructor(Class.. parameterTypes)

    // public InputStream getResourceAsStream(String name);


}