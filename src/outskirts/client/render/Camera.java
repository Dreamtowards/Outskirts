package outskirts.client.render;

import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.entity.player.EntityPlayer;
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

    private CamUpdater camUpdater = new CamUpdater(this);
    public final CamUpdater getCameraUpdater() {
        return camUpdater;
    }


    public static class CamUpdater {

        private float cameraDistance = -10;
        private Vector3f eulerAngles = new Vector3f(); // Camera EulerAngles, x:Pitch, y:Yaw z:Roll. needs persistent sum and adjust then needs eulerAngles.

        private Vector3f direction = new Vector3f(Vector3f.UNIT_X); // pointing front of the camera

        private EntityPlayer ownerEntity;

        private Camera camera;

        private CamUpdater(Camera camera) {
            this.camera = camera;
        }

        public void update() {
            // KEY_VIEW
            if (Outskirts.isIngame()) {
//                if (Outskirts.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
//                    eulerAngles.z += -Math.toRadians(Outskirts.getMouseDX() * GameSettings.MOUSE_SENSITIVITY);
//                } else {
                    eulerAngles.y += -Math.toRadians(Outskirts.getMouseDX() * ClientSettings.MOUSE_SENSITIVITY);
                    eulerAngles.x += -Math.toRadians(Outskirts.getMouseDY() * ClientSettings.MOUSE_SENSITIVITY);
                    eulerAngles.x = Maths.clamp(eulerAngles.x, -Maths.PI/2f, Maths.PI/2f);
//                }

                cameraDistance += Math.signum(Outskirts.getDScroll());
                cameraDistance = Maths.clamp(cameraDistance, -2000, 0);
            }

            // Camera Rotation Matrix
            Matrix3f tmp = new Matrix3f();
            camera.rotation.setIdentity();
            Matrix3f.mul(camera.rotation, Matrix3f.rotate(eulerAngles.y, Vector3f.UNIT_Y, tmp), camera.rotation);
            Matrix3f.mul(camera.rotation, Matrix3f.rotate(eulerAngles.x, Vector3f.UNIT_X, tmp), camera.rotation);
            Matrix3f.mul(camera.rotation, Matrix3f.rotate(eulerAngles.z, Vector3f.UNIT_Z, tmp), camera.rotation);

            if (ownerEntity != null) {
                // direction of cam
                direction.set(0, 0, 1);
                Matrix3f.transform(camera.rotation, direction);
                direction.negate();

                // Camera Position
                camera.position.set(0, 0, 0).addScaled(cameraDistance, direction);
                camera.position.add(ownerEntity.position()).add(0, 0.8f, 0); // applies owner.pos offset
            }

        }

        public Vector3f getEulerAngles() {
            return eulerAngles;
        }

        public Vector3f getDirection() {
            return direction;
        }

        // vari name...
        public float getCameraDistance() {
            return cameraDistance;
        }

        public void setOwnerEntity(EntityPlayer ownerEntity) {
            this.ownerEntity = ownerEntity;
        }

        public EntityPlayer getOwnerEntity() {
            return ownerEntity;
        }
    }
}
