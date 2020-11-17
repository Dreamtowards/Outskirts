package outskirts.entity.player;

import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.item.inventory.Inventory;
import outskirts.item.stack.ItemStack;
import outskirts.network.ChannelHandler;
import outskirts.util.GameTimer;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

public abstract class EntityPlayer extends Entity {

    private static float walkspeed = 0.4f;

    public GameMode gamemode = GameMode.CREATIVE;

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
            Outskirts.getCamera().getPosition().addScaled(walkspeed*amount*GameTimer.TPS*0.01f, dir);
            return;
        }
        getRigidBody().getLinearVelocity().add(dir.scale(walkspeed*amount));  // *getRigidBody().getMass()*GameTimer.TPS
    }

    // todo: Nameable.?
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

    public GameMode getGameMode() {
        return gamemode;
    }
}
