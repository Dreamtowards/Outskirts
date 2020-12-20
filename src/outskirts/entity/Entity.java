package outskirts.entity;

import outskirts.client.material.Material;
import outskirts.client.material.Model;
import outskirts.physics.dynamics.RigidBody;
import outskirts.storage.SAVERS;
import outskirts.storage.Savable;
import outskirts.storage.dst.DObject;
import outskirts.util.Tickable;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

public abstract class Entity implements Registrable, Savable, Tickable {

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
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create Entity. ("+registryID, ex);
        }
    }
    public static Entity loadEntity(DObject mpEntity) {
        Entity entity = createEntity((String)mpEntity.get("registryID"));
        entity.onRead(mpEntity);
        return entity;
    }

    public final Material getMaterial() {
        return material;
    }
    public final Material material() {
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
    public final RigidBody rigidbody() {
        return rigidbody;
    }

    // dep.?
    public final Vector3f getPosition() {
        return rigidbody.transform().origin;
    }
    public final Vector3f position() {
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
    public void onTick() {



    }

    @Override
    public void onRead(DObject mp) {

        SAVERS.RIGIDBODY.read(getRigidBody(), (DObject)mp.get("rigidbody"));


    }

    @Override
    public DObject onWrite(DObject mp) {
        mp.put("registryID", registryID);

        mp.put("rigidbody", SAVERS.RIGIDBODY.write(getRigidBody(), new DObject()));

        return mp;
    }

    @Override
    public final String getRegistryID() {
        return registryID;
    }
}
