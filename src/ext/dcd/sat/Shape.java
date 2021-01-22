package ext.dcd.sat;

import outskirts.util.vector.Vector3f;

public class Shape {

    public Vector3f position;

    public Vector3f[] vertices = {
            new Vector3f()
    };

    public Vector3f[] getSeparatingAxes() {
        Vector3f[] axes = new Vector3f[vertices.length];

        for (int i = 0;i < vertices.length;i+=3) {
            Vector3f p1 = vertices[i];
            Vector3f p2 = vertices[i+1];
            Vector3f p3 = vertices[i+2];
            Vector3f edge1 = Vector3f.sub(p1, p2, null);
            Vector3f edge2 = Vector3f.sub(p1, p3, null);
            Vector3f normal = Vector3f.cross(edge1, edge2, null).normalize();
            axes[i] = normal;
        }
        return axes;
    }

    /**
     * @param axis the axis needs to project to. (not dir), require normalized
     */
    public Projection project(Vector3f axis) {


        return new Projection();
    }


}
