package outskirts.entity;

import outskirts.client.material.Material;
import outskirts.physics.dynamics.RigidBody;
import outskirts.storage.Savable;
import outskirts.storage.DataMap;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.Map;

public class Entity implements Savable, Registrable {

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
    public static Entity createEntity(DataMap mpEntity) {
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
    public void onRead(DataMap mp) {
        mp.getVector3f("position", getPosition());

    }

    @Override
    public Map onWrite(DataMap mp) {
        mp.put("registryID", registryID);
        mp.putVector3f("position", getPosition());

        return mp;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}
