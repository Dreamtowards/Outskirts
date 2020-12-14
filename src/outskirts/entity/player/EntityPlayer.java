package outskirts.entity.player;

import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.item.inventory.Inventory;
import outskirts.item.stack.ItemStack;
import outskirts.network.ChannelHandler;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.util.GameTimer;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

public abstract class EntityPlayer extends Entity {

//    private static float walkspeed = 0.4f;

    public GameMode gamemode = GameMode.SURVIVAL;

    public ChannelHandler connection;

    private String name; // should only MP? should alls Entity..?

    private Inventory inventory = new Inventory(32);

    private int hotbarSlot;

    public EntityPlayer() {
        setRegistryID("player");

    }

    public final void walk(float amount, float angrad) {
        walk(amount, Matrix3f.transform(Matrix3f.rotate(angrad+Outskirts.getCamera().getCameraUpdater().getEulerAngles().y, Vector3f.UNIT_Y, null),
                 new Vector3f(0, 0, -1f)).normalize());
    }
    public void walk(float amount, Vector3f dir) {
        //todo: reduce.
        if (Outskirts.getCamera().getCameraUpdater().getOwnerEntity() == null) {
            Outskirts.getCamera().getPosition().addScaled(amount*GameTimer.TPS*0.01f, dir);
            return;
        }
        getRigidBody().getLinearVelocity().add(dir.scale(amount));  // *getRigidBody().getMass()*GameTimer.TPS
    }

    // todo: Nameable.?  may not cuz all Entity can have name or null.
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Inventory getBackpackInventory() {
        return inventory;
    }

    public void setHotbarSlot(int hotbarSlot) {
        this.hotbarSlot = hotbarSlot;
    }
    public int getHotbarSlot() {
        return hotbarSlot;
    }
    public ItemStack getHotbarItem() {
        return getBackpackInventory().get(getHotbarSlot());
    }

    public GameMode getGamemode() {
        return gamemode;
    }
    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;

        if (gamemode == GameMode.SURVIVAL) {
            rigidbody().getGravity().set(0, -10, 0);
            rigidbody().setLinearDamping(0.95f);
        } else if (gamemode == GameMode.CREATIVE) {
            rigidbody().getGravity().set(0, 0, 0);
            rigidbody().setLinearDamping(0.02f);
        }
    }

    public boolean isOnGround() {
        for (CollisionManifold mf : getWorld().dynamicsWorld.getCollisionManifolds()) {
            if (mf.containsBody(rigidbody())) {
                for (int i = 0; i < mf.getNumContactPoints(); i++) {
                    CollisionManifold.ContactPoint cp = mf.getContactPoint(i);
                    if (cp.pointOnB.y < position().y && Math.abs(cp.pointOnB.x - position().x) < 0.1f && Math.abs(cp.pointOnB.z - position().z) < 0.1f) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
