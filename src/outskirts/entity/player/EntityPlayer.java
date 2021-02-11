package outskirts.entity.player;

import outskirts.client.Outskirts;
import outskirts.client.gui.screen.GuiScreenChat;
import outskirts.command.CommandSender;
import outskirts.entity.Entity;
import outskirts.item.inventory.Inventory;
import outskirts.item.stack.ItemStack;
import outskirts.network.ChannelHandler;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

public abstract class EntityPlayer extends Entity implements CommandSender {

//    private static float walkspeed = 0.4f;

    public ChannelHandler connection;

    private String name; // should only MP? should alls Entity..?

    private Inventory inventory = new Inventory(8);

    private int hotbarSlot;

    private Gamemode gamemode = Gamemode.SURVIVAL;

    private boolean flymode = false;


    public EntityPlayer() {
        setRegistryID("player");

    }

    public final void walk(float amount, float angrad) {
        walk(amount, Matrix3f.transform(Matrix3f.rotate(angrad+Outskirts.getCamera().getCameraUpdater().getEulerAngles().y, Vector3f.UNIT_Y, null),
                 new Vector3f(0, 0, -1f)).normalize());
    }
    public void walk(float amount, Vector3f dir) {
        //todo: reduce.
//        if (Outskirts.getCamera().getCameraUpdater().getOwnerEntity() == null) {
//            Outskirts.getCamera().getPosition().addScaled(amount*GameTimer.TPS*0.001f, dir);
//            return;
//        }
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

    public Gamemode getGamemode() {
        return gamemode;
    }
    public void setGamemode(Gamemode gamemode) {
        this.gamemode = gamemode;

        if (gamemode == Gamemode.SURVIVAL) {
            rigidbody().getGravity().set(0, -10, 0);
            rigidbody().setLinearDamping(0.95f);
        } else if (gamemode == Gamemode.CREATIVE) {
            rigidbody().getGravity().set(0, 0, 0);
            rigidbody().setLinearDamping(0.0001f);
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


    public boolean isFlymode() {
        return flymode;
    }
    public void setFlymode(boolean flymode) {
        this.flymode = flymode;
    }

    @Override
    public void sendMessage(String msg) {
        GuiScreenChat.INSTANCE.printMessage(msg);
    }
}
