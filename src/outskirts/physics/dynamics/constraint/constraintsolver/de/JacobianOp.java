package outskirts.physics.dynamics.constraint.constraintsolver.de;

import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

public final class JacobianOp {

//    public static float diag(
//            Matrix3f invRotA, Matrix3f invRotB,
//            Vector3f relpointA, Vector3f relpointB,
//            Vector3f normOnB, // linearJointAxis
//            Vector3f invInertiaTensorDiagA, float invMassA,
//            Vector3f invInertiaTensorDiagB, float invMassB) { // return_diag > 0;
//
//        Vector3f rA_x_norm = Vector3f.cross(relpointA, normOnB, null);
//        Matrix3f.transform(invRotA, rA_x_norm, rA_x_norm);
//
//        Vector3f rB_x_n$norm = Vector3f.cross(relpointB, new Vector3f(normOnB).negate(), null);
//        Matrix3f.transform(invRotB, rB_x_n$norm, rB_x_n$norm);
//
//        Vector3f m_0MinvJt = mat3diagtransform(invInertiaTensorDiagA, rA_x_norm, new Vector3f()); // invInertiaTensorDiagA scale rA_x_norm
//        Vector3f m_1MinvJt = mat3diagtransform(invInertiaTensorDiagB, rB_x_n$norm, new Vector3f());
//
//        float Adiag = invMassA + Vector3f.dot(m_0MinvJt, rA_x_norm) + invMassB + Vector3f.dot(m_1MinvJt, rB_x_n$norm);
//
//        assert Adiag > 0;
//
//        return Adiag;
//    }

    private static Vector3f mat3diagtransform(Vector3f mat3Diag, Vector3f b, Vector3f dest) {
        dest.x = mat3Diag.x * b.x;
        dest.y = mat3Diag.y * b.y;
        dest.z = mat3Diag.z * b.z;
        return dest;
    }

}
