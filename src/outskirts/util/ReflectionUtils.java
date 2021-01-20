package outskirts.util;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * ReflectionUtils looks more Powerful than ReflectUtils and amateur than Reflects
 */
public final class ReflectionUtils {

    public static sun.misc.Unsafe UNSAFE = ReflectionUtils.getFieldValue(Objects.requireNonNull(getField(Unsafe.class, "theUnsafe")), null);


    public static Field findFieldUpward(Class<?> fromclass, String fieldname, Predicate<Class<?>> predicate) {
        for (Class clazz = fromclass;clazz != Object.class;clazz = clazz.getSuperclass()) {
            if (predicate.test(clazz)) {
                Field f = ReflectionUtils.getField(clazz, fieldname);
                if (f != null)
                    return f;
            }
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String fieldname) {
        try {
            return clazz.getDeclaredField(fieldname);
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }

    public static <T> T getFieldValue(Field field, Object owner) {
        try {
            field.setAccessible(true);
            return (T) field.get(owner);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to get field value.", ex);
        }
    }

    public static void setFieldValue(Field field, Object owner, Object value) {
        try {
            field.setAccessible(true);
            field.set(owner, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to set field value.", ex);
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

    public static <T> T newInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to newInstance.", ex);
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
