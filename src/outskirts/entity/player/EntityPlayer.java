package outskirts.entity.player;

import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.network.ChannelHandler;
import outskirts.util.GameTimer;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

public abstract class EntityPlayer extends Entity {

    public static float walkSpeed = 0.4f;

//    // a EulerAngles, just means head look at (for walk, look, pick ..etc), its not effort entity body rotation.
//    // the name lookAt likes a direction, but its a EulerAngles... this is a problem..
//    private Vector3f eulerAngles = new Vector3f(); // change-name from lookAt

    public ChannelHandler connection;

    private String name; // should only MP? should alls Entity..?

    public EntityPlayer() {
        setRegistryID("player");

    }

    public void walkStep(float lv, float angrad) {
        walkStep(lv, Matrix3f.transform(
                Matrix3f.rotate(angrad+Outskirts.getCamera().getCameraUpdater().getEulerAngles().y, Vector3f.UNIT_Y, null),
                new Vector3f(0, 0, -1f)).normalize());
    }
    public void walkStep(float lv, Vector3f dir) {
        if (Outskirts.getCamera().getCameraUpdater().getOwnerEntity() == null) {
            Outskirts.getCamera().getPosition().addScaled(walkSpeed*lv*GameTimer.TPS*0.01f, dir);
            return;
        }
        getRigidBody().applyForce(dir.scale(walkSpeed*lv*getRigidBody().getMass()*GameTimer.TPS));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
