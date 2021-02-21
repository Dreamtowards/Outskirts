package outskirts.util;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * ReflectionUtils looks more Powerful than ReflectUtils and amateur than Reflects
 */
public final class ReflectionUtils {

    private static sun.misc.Unsafe UNSAFE = ReflectionUtils.getFieldv(Unsafe.class, "theUnsafe");


    public static Class findSuperior(Class subclass, Predicate<Class> until) {
        Class c = subclass;
        while (c != Object.class) {
            if (until.test(c)) {
                return c;
            }
            c = c.getSuperclass();
        }
        throw new NoSuchElementException("Not found such Super class.");
    }

    /**
     * Get Field Value.
     * @param objOrCls non-static-field: Owner Object. static-field: the Class.
     */
    public static <T> T getFieldv(Object objOrCls, String fname) {
        try {
            boolean isStatic = objOrCls instanceof Class;
            Class cls = isStatic ? (Class)objOrCls : objOrCls.getClass();
            Field field = cls.getDeclaredField(fname);
            field.setAccessible(true);
            return (T) field.get(isStatic ? null : objOrCls);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException("Failed to get field value.", ex);
        }
    }



    private static Method getMethod(Class<?> clazz, String methodname, Class<?>... args) {
        try {
            Method method = clazz.getMethod(methodname, args);
            method.setAccessible(true);
            return method;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get method.", ex);
        }
    }

    public static <T> T newInstance(Class<T> cls, Object... initargs) {
        try {
            Class[] inittypes = CollectionUtils.filli(new Class[initargs.length], i -> initargs[i].getClass());
            return cls.getDeclaredConstructor(inittypes).newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            throw new RuntimeException("Failed to newInstance.", ex);
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String clsname, Object... initargs) {
        try {
            return (T)ReflectionUtils.newInstance(Class.forName(clsname), initargs);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Failed find class.", ex);
        }
    }

    /**
     * UNSAFE. just allocate instance without calling the class's constructor
     */
    private static <T> T allocInstance(Class<T> clazz) {
        try {
            return (T)UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException ex) {
            throw new RuntimeException("Failed to allocate this instance.");
        }
    }
//    private static Class<?> defineClass(String name, byte[] bytes) {
//        return UNSAFE.defineClass(name, bytes, 0, bytes.length, ClassLoader.getSystemClassLoader(), null);
//    }
}
