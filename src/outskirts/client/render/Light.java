package outskirts.client.render;

import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

public final class Light {

    private Vector3f position = new Vector3f();

    /**
     * Diffuse Color
     * alpha is not join lighting calculation. cause that stuff will be transparent which be illuminated darkness
     */
    private Vector3f color = new Vector3f(1, 1, 1);

    /**
     * Coefficients of Attenuation.
     * x is constant term, always 1.0f.
     * y is linear term.
     * z is quadratic terms.
     * which: d = FragmentToLightDistance, a = this the AttenuationCoefficients.
     * lightAttenStrength = 1f / (a.x + a.y*d + a.z*d^2)
     */
    private Vector3f attenuation = new Vector3f(1, 0, 0);

    /**
     * SpotLight
     */
    private Vector3f direction = new Vector3f(Vector3f.UNIT_X); // Direction (unit vector
    // name as SpotConeInner ..?
    private float coneAngleInner = 3.1415f; // Spot Cone Angle. in radians. default value PI, INNER == OUTER == PI means disable Spot-Light
    private float coneAngleOuter = 3.1415f;

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getConeAngleInner() {
        return coneAngleInner;
    }
    public void setConeAngleInner(float coneAngleInner) {
        this.coneAngleInner = coneAngleInner;
    }

    public float getConeAngleOuter() {
        return coneAngleOuter;
    }
    public void setConeAngleOuter(float coneAngleOuter) {
        this.coneAngleOuter = coneAngleOuter;
    }




    // http://www.ogre3d.org/tikiwiki/tiki-index.php?page=-Point+Light+Attenuation
    private static float[][] ATTE_VTABLE = {
            {7,    0.7f,    1.8f},
            {13,   0.3f,    0.44f},
            {20,   0.22f,   0.20f},
            {32,   0.14f,   0.07f},
            {50,   0.09f,   0.032f},
            {65,   0.07f,   0.017f},
            {100,  0.045f,  0.0075f},
            {160,  0.027f,  0.0028f},
            {200,  0.022f,  0.0019f},
            {325,  0.014f,  0.0007f},
            {600,  0.007f,  0.0002f},
            {3250, 0.0014f, 0.000007f}
    };

    public static Vector3f calculateApproximateAttenuation(float distance, Vector3f dest) {
        distance = Maths.clamp(distance, ATTE_VTABLE[0][0], ATTE_VTABLE[ATTE_VTABLE.length-1][0]-0.001f);
        int section = -1;
        for (int i = 0;i < ATTE_VTABLE.length-1;i++) {
            if (distance >= ATTE_VTABLE[i][0] && distance < ATTE_VTABLE[i+1][0]) { // must had one been pass. cuz had a clamp.
                section = i;
                break;
            }
        }
        float t = Maths.inverseLerp(distance, ATTE_VTABLE[section][0], ATTE_VTABLE[section+1][0]);
        return dest.set(1f,
                Maths.lerp(t, ATTE_VTABLE[section][1], ATTE_VTABLE[section+1][1]),
                Maths.lerp(t, ATTE_VTABLE[section][2], ATTE_VTABLE[section+1][2]));
    }
}
