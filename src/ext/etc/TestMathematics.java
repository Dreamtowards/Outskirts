package ext.etc;

import org.junit.Test;

public class TestMathematics {

//    @Test
//    public void test_crossmultiplier() {
//
//        // mat3x3 * (vec3 x vec3) * scalar ==? mat3x3 * (vec3 x (vec3 * scalar))
//
//        Matrix3f mat = Matrix3f.rotate(Maths.PI/3f, Vector3f.UNIT_X, null).invert();
//        Vector3f vec1 = new Vector3f(1f, 1.3f, 0.4f);
//        Vector3f vec2 = new Vector3f(0f, -1f, 0f).normalize();
//        float scalar = 19f;
//
//        Vector3f result1 = Matrix3f.transform(mat, Vector3f.cross(vec1, vec2, null), null).scale(scalar);
//
//        Vector3f result2 = Matrix3f.transform(mat, Vector3f.cross(vec1, new Vector3f(vec2).scale(scalar), null), null);
//
//        Assert.assertEquals(result1, result2);
//        // 2 output should same
//
//        Vector3f torqueAxis = new Vector3f();
//
//        Vector3f.cross(vec1, vec2.scale(scalar), torqueAxis);
//        Matrix3f.transform(mat, torqueAxis, torqueAxis);
//
//        Log.info(torqueAxis);
//    }
//
//    @Test
//    public void test_rotmat_axislen() {
//
//        Vector3f rand_axis = new Vector3f(21f, 412f, 211f);
//        float rand_ang = 1.3173f;
//
//        Matrix3f mat1 = Matrix3f.rotate(rand_ang, rand_axis, null);
//
//        Matrix3f mat2 = Matrix3f.rotate(rand_ang, new Vector3f(rand_axis).normalize(), null);
//
//        Log.info(mat1);
//        Log.info(mat2);
//
//        Vector3f v = new Vector3f(10, 230f, 91f);
//        Log.info(Matrix3f.transform(mat1, new Vector3f(v), null));
//        Log.info(Matrix3f.transform(mat2, new Vector3f(v), null));
//
//    }
//
//    @Test
//    public void test_quat_rot() {
//
//        Vector3f axis = new Vector3f(1, 2,4).normalize();
//        Matrix3f mat = Matrix3f.rotate(1.314f, axis, null);
//
//        Log.info("orimat: \n"+mat);
//
//        Matrix3f transedMat = Quaternion.toMatrix(Quaternion.fromMatrix(mat, null), new Matrix3f());
//        Log.info("mat->quat->mat: \n" + transedMat);
//
//        Vector3f v = new Vector3f(231, 32, 932);
//        Log.info(Matrix3f.transform(mat, v, null));
//        Log.info(Matrix3f.transform(transedMat, v, null));
//
//    }


    @Test
    public void tmp_test_floatop() {


    }

}
