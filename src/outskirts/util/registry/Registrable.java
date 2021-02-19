package outskirts.util.registry;

import outskirts.util.*;

import java.lang.reflect.Field;

public interface Registrable {

    String getRegistryID();

    /**
     * this default impl required the impl-class has a String type 'registryID' field.
     *
     * default impl for setRegistryID(). because setRegistryID is rarely be call(normally only one time one object),
     * so even use dynamic-reflection, that is not a big efficiency problem.
     * Instead, this Registrable interface is always been impl,
     * and setRegistryID() needs to check does registryID has already been set, this'll make a lots of duplicate loose codes.
     * so have this default reflection impl.
     */
    default <T extends Registrable> T setRegistryID(String registryID) {
        try {
            //impl class's 'registryID' field.
            Class c = ReflectionUtils.findSuperior(getClass(), supc -> CollectionUtils.contains(supc.getInterfaces(), Registrable.class));
            Field f = c.getDeclaredField("registryID");
            Validate.notNull(f, "'registryID' field is not found.");
            f.setAccessible(true);

            Validate.validState(f.get(this)==null, "registryID already been initialized, can't be change again");

            f.set(this, new Identifier(registryID).toString());

            return (T) this;
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException("A exception occurred in Registrable::setRegistry() default impl.", ex);
        }
    }
}
