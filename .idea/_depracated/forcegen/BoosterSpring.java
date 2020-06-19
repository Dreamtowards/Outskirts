package outskirts.physics.dynamic.forcegen;

import outskirts.physics.dynamic.RigidBody;
import outskirts.util.vector.Vector3f;

public class BoosterSpring extends Booster {

    /**
     * opposite side port's position of the Spring
     *
     * this can holds RigidBody.getPosition(), because RigidBody's position's ref
     * is been protected, commonly cant be set another ref directly
     */
    private Vector3f opposite;

    // Spring's hardness
    private float elastic = 100;

    // Spring resting length
    private float originLength = 0;

    public BoosterSpring(Vector3f opposite) {
        this.opposite = opposite;
    }

    //just a tmp transporter, else every onUpdate'll new a Vector3f for vector subtraction
    private final Vector3f TMP_VEC_TRANS = new Vector3f();

    /**
     * Hooke formula of Spring
     * F = -k(l-l')
     * F = -k(|d|-l')d
     */
    @Override
    public void onUpdate(RigidBody rigidBody, float delta) {

        //this is not really ports of swing, the position is center of body and not port intersection
        //direction of: opposite -> body
        Vector3f d = Vector3f.sub(rigidBody.transform().origin, opposite, TMP_VEC_TRANS);

        if (d.equals(Vector3f.ZERO))
            return;

        float offset = d.length() - originLength;

        Vector3f F = d
                .normalize()
                .scale(offset * -elastic);

        rigidBody.applyForce(F);
    }

}
