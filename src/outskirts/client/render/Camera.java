package outskirts.client.render;

import org.lwjgl.input.Mouse;
import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.entity.player.EntityPlayer;
import outskirts.event.EventHandler;
import outskirts.event.client.input.MouseMoveEvent;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

/**
 * Camera just an Entity-Struc. its stores the position:vec3 and rotation:mat4
 * for high-level futrues like binding-entity-owner, thirdpersion/firstperson-camera,
 * camera-rotation-interpolation(limited axis) ..etc, just update the camera by an external updater.
 * the Camera is tends low level, wide layer.
 */
public class Camera {

    // common transform. isn't negated
    private Vector3f position = new Vector3f();
    private Matrix3f rotation = new Matrix3f();

    public Vector3f getPosition() {
        return position;
    }
    public Matrix3f getRotation() {
        return rotation;
    }



    private float cameraDistance = -0;

    /** Camera EulerAngles. x:Pitch, y:Yaw z:Roll. */
    private Vector3f eulerAngles = new Vector3f();

    private Vector3f direction = new Vector3f(Vector3f.UNIT_X); // pointing camera-front.

    private EntityPlayer ownerEntity;

    public void update() {
        // KEY_VIEW
        if (Outskirts.isIngame()) {
            // actually this mouse-move looks wrong: not sampled full-frame all move records, just sampled frame-tail event move.
            // but this gets better experience effect.
            eulerAngles.y += -Math.toRadians(Outskirts.getMouseDX() * ClientSettings.MOUSE_SENSITIVITY);
            eulerAngles.x += -Math.toRadians(Outskirts.getMouseDY() * ClientSettings.MOUSE_SENSITIVITY);
            eulerAngles.x = Maths.clamp(eulerAngles.x, -Maths.PI/2f, Maths.PI/2f);

            cameraDistance += Math.signum(Outskirts.getDWheel());
            cameraDistance = Maths.clamp(cameraDistance, -2000, 0);
        }

        // Camera Rotation Matrix
        Matrix3f tmp = new Matrix3f();
        rotation.setIdentity();
        Matrix3f.mul(rotation, Matrix3f.rotate(eulerAngles.y, Vector3f.UNIT_Y, tmp), rotation);
        Matrix3f.mul(rotation, Matrix3f.rotate(eulerAngles.x, Vector3f.UNIT_X, tmp), rotation);
        Matrix3f.mul(rotation, Matrix3f.rotate(eulerAngles.z, Vector3f.UNIT_Z, tmp), rotation);

        direction.set(0, 0, -1);
        Matrix3f.transform(rotation, direction);

        if (ownerEntity != null) {
            // Camera Position
            position.set(ownerEntity.position()).add(0, 0.8f, 0).addScaled(cameraDistance, direction);
        }
    }

    public Vector3f getEulerAngles() {
        return eulerAngles;
    }
    public Vector3f getDirection() {
        return direction;
    }

    public float getCameraDistance() {
        return cameraDistance;
    }

    public EntityPlayer getOwnerEntity() {
        return ownerEntity;
    }
    public void setOwnerEntity(EntityPlayer ownerEntity) {
        this.ownerEntity = ownerEntity;
    }


}
