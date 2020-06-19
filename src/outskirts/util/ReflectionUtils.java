package outskirts.util;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * ReflectionUtils looks more Powerful than ReflectUtils and amateur than Reflects
 */
public final class ReflectionUtils {

    public static Field getField(Class<?> sourceClass, String fieldname, boolean findUpward, Predicate<Class<?>> predicate) {
        Field field = null;
        for (Class clazz = sourceClass;clazz != Object.class;clazz = clazz.getSuperclass()) {
            if (predicate.test(clazz)) {
                try {
                    field = clazz.getDeclaredField(fieldname);
                } catch (NoSuchFieldException ex) { }
            }
            if (!findUpward || field != null) {
                break;
            }
        }
        if (field != null) {
            field.setAccessible(true);
        }
        return field;
    }

    public static Field getField(Class<?> sourceClass, String fieldname) {
        return getField(sourceClass, fieldname, false, c -> true);
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


    // speed too low...
    /**
     * @param clazz the class type be instanced
     * @param parent "parent"'s default constructor will be calls for creating the "clazz"'s instance
     */
    private static <T> T allocInstance(Class<T> clazz, Class<? super T> parent) {
        try {
            Constructor parentCon = parent.getDeclaredConstructor();

            Constructor actuallyCon = ReflectionFactory.getReflectionFactory()
                    .newConstructorForSerialization(clazz, parentCon);

            return clazz.cast(actuallyCon.newInstance());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            throw new RuntimeException("Failed to create instance.", ex);
        }
    }

    /**
     * just new instance without calling the type(class/object)'s constructor
     */
    private static <T> T allocInstance(Class<T> clazz) {
        return allocInstance(clazz, Object.class);
    }
}
