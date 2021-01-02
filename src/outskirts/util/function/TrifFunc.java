package outskirts.util.function;

import outskirts.util.vector.Vector3f;

public interface TrifFunc {
    float sample(float x, float y, float z);

    default float sample(Vector3f v) {
        return sample(v.x, v.y, v.z);
    }
}