package outskirts.entity;

import outskirts.client.material.Material;
import outskirts.client.material.Model;
import outskirts.physics.dynamics.RigidBody;
import outskirts.storage.SAVERS;
import outskirts.storage.Savable;
import outskirts.storage.dat.DATObject;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.Map;

public class Entity implements Savable, Registrable {

    public static final Registry<Class<? extends Entity>> REGISTRY = new Registry.ClassRegistry<>();

    private String registryID;

    private RigidBody rigidbody = new RigidBody();

    private Material material = new Material();
    private Model model;

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
    public static Entity createEntity(DATObject mpEntity) {
        Entity entity = createEntity((String)mpEntity.get("registryID"));
        entity.onRead(mpEntity);
        return entity;
    }

    public final Material getMaterial() {
        return material;
    }

    public final Model getModel() {
        return model;
    }
    public void setModel(Model model) {
        this.model = model;
    }

    // directly rigidbody().
    public final RigidBody getRigidBody() {
        return rigidbody;
    }

    // dep.?
    public final Vector3f getPosition() {
        return rigidbody.transform().origin;
    }
    public final Matrix3f getRotation() {
        return rigidbody.transform().basis;
    }

    public World getWorld() {
        return world;
    }

    public final void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void onRead(DATObject mp) {

        SAVERS.RIGIDBODY.read(getRigidBody(), (DATObject)mp.get("rigidbody"));


    }

    @Override
    public Map onWrite(DATObject mp) {
        mp.put("registryID", registryID);

        mp.put("rigidbody", SAVERS.RIGIDBODY.write(getRigidBody(), new DATObject()));

        return mp;
    }

    @Override
    public final String getRegistryID() {
        return registryID;
    }
}
