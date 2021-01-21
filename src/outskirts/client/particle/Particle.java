package outskirts.client.particle;

import outskirts.client.render.Texture;
import outskirts.util.vector.Vector3f;

public class Particle {

    private Vector3f position = new Vector3f();

    private Texture texture;

    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector3f getPosition() {
        return position;
    }

}
