package outskirts.entity.player;

import outskirts.client.Outskirts;
import outskirts.client.gui.screen.GuiScreenChat;
import outskirts.command.CommandSender;
import outskirts.entity.EntityCreature;
import outskirts.init.ex.Models;
import outskirts.item.inventory.Inventory;
import outskirts.item.stack.ItemStack;
import outskirts.network.ChannelHandler;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.shapes.GhostShape;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

public abstract class EntityPlayer extends EntityCreature implements CommandSender {

    public ChannelHandler connection;

    private Inventory inventory = new Inventory(8);
    private int hotbarSlot;

    private Gamemode gamemode = Gamemode.SURVIVAL;
    private boolean flymode = false;


    public EntityPlayer() {
        setRegistryID("player");
        setMaxHealth(20);
        setHealth(20);

        RigidBody rb = getRigidBody();
        rb.setInertiaTensorLocal(0,0,0);
        rb.setMass(60);
        rb.setFriction(0.2f);
        rb.setRestitution(0f);
    }

    public final void walk(float amount, float angrad) {
        walk(amount, Matrix3f.transform(Matrix3f.rotate(angrad+Outskirts.getCamera().getEulerAngles().y, Vector3f.UNIT_Y, null), vec3(0, 0, -1f)).normalize());
    }
    public void walk(float amount, Vector3f dir) {
        getRigidBody().getLinearVelocity().add(dir.scale(amount));
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
        if (gamemode == Gamemode.SPECTATOR) {
            getRigidBody().setCollisionShape(new GhostShape());
            setModel(Models.EMPTY);
        } else {
            getRigidBody().setCollisionShape(new SphereShape(1));
            setModel(Models.GEO_SPHERE);
        }
    }

    public boolean isOnGround() {
        for (CollisionManifold mf : getWorld().dynamicsWorld.getCollisionManifolds()) {
            if (mf.containsBody(getRigidBody())) {
                for (int i = 0; i < mf.getNumContactPoints(); i++) {
                    CollisionManifold.ContactPoint cp = mf.getContactPoint(i);
                    if (cp.pointOnB.y < position().y && Math.abs(cp.pointOnB.x - position().x) < 0.3f && Math.abs(cp.pointOnB.z - position().z) < 0.3f) {
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
        RigidBody rb = getRigidBody();
        if (flymode) {
            rb.getGravity().set(0, 0, 0);
            rb.setLinearDamping(0.001f);
        } else {
            rb.getGravity().set(0, -10, 0);
            rb.setLinearDamping(0.95f);
        }
    }

    @Override
    public void sendMessage(String msg) {
        GuiScreenChat.INSTANCE.printMessage(msg);
    }
}
