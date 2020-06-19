package outskirts.entity;

import outskirts.client.material.Material;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.util.nbt.Savable;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

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
    public static Entity createEntity(NBTTagCompound tagEntity) {
        Entity entity = createEntity(tagEntity.getCompoundTag("registryID"));
        entity.readNBT(tagEntity);
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
    public void readNBT(NBTTagCompound tagCompound) {

        tagCompound.getVector3f("position", getPosition());

    }

    @Override
    public NBTTagCompound writeNBT(NBTTagCompound tagCompound) {

        tagCompound.setString("registryID", getRegistryID());

        tagCompound.setVector3f("position", getPosition()); // setCompoundTag("rigidbody", rigidbodt.writeNBT()) ..?

        return tagCompound;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}
