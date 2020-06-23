package outskirts.entity;

import outskirts.client.material.Material;
import outskirts.physics.dynamics.RigidBody;
import outskirts.storage.Savable;
import outskirts.storage.SaveUtils;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.Map;

public abstract class Entity implements Savable, Registrable {

    public static final Registry<Class<? extends Entity>> REGISTRY = new Registry.ClassRegistry<>();

    private String registryID;

    private RigidBody rigidBody = new RigidBody();

    private Material material = new Material();

    // ref to the world
    private World world;

    public Vector3f tmp_boxSphere_scale = new Vector3f(1, 1, 1);

    public static Entity createEntity(String registryID) {
        try {
            return REGISTRY.get(registryID).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create Entity.", ex);
        }
    }
    public static Entity createEntity(Map mpEntity) {
        Entity entity = createEntity((String)mpEntity.get("registryID"));
        entity.onRead(mpEntity);
        return entity;
    }

    public final Material getMaterial() {
        return material;
    }

    public final RigidBody getRigidBody() {
        return rigidBody;
    }

    public final Vector3f getPosition() {
        return rigidBody.transform().origin;
    }

    public final Matrix3f getRotation() {
        return rigidBody.transform().basis;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void onRead(Map mp) {

        SaveUtils.vector3f(mp.get("position"), getPosition());

    }

    @Override
    public void onWrite(Map mp) {
        mp.put("registryID", registryID);
        mp.put("position", SaveUtils.vector3f(getPosition()));
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}
