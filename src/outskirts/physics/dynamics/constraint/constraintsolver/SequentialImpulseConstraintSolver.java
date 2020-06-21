package outskirts.physics.dynamics.constraint.constraintsolver;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.dynamics.RigidBody;
import outskirts.physics.dynamics.constraint.Constraint;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import java.util.List;

/**
 * 1. Preparation the Positional Constration Equation.
 *
 * Contact-Constraint: C: ((C_A + r_A) - (C_B + r_B)) · n = 0   // actually is >= 0
 *
 * where C_A, C_B is vec3 vectors of center-of-mass position of body A, B. respectively.
 *       r_A, r_B is vec3 vectors from center-of-mass to Contact-Point. for body A, B. respectively.
 *       n is the Contact-Normal vec3 vector on the "surface of bodyB" (pointing from B to A).
 *
 *
 * 2. Derivate the Positional-Constraint-Equation C, with Respect to Time. then Get the Velocity-Constraint-Equation C'.
 *
 * C': ((v_A + ω_A × r_A) - (v_B + ω_B × r_B)) · n = 0
 *
 * 2.1. let the Velocity-Constraint-Equation C' to the C':JV+b=0 Form.
 *      J is a 1x12(row*col) matrix where the Jacobian. its contains the coefficients corresponding each component on the matrix V.
 *      V is a 12x1 matrix contains velocities of body A,B [v_A^T  ω_A^T  v_B^T  ω_B^T]^T.
 *      the term b just as 0 now - ignored. this term will talk about later.
 *      i.e. its Separating velocity terms.
 *
 * C': n·v_A +n·ω_A×r_A -n·v_B -n·ω_B×r_B = 0
 *
 * C': v_A·n +ω_A·r_A×n -v_B·n -ω_B·r_B×n = 0   // by use of TripleProduct  a·(b×c) == b·(c×a)
 *
 * C': JV+b: [n ^T  r_A×n ^T  -n ^T  -r_B×n ^T] [v_A  ω_A  v_B  ω_B]^T + 0
 *
 *
 * 3. For the CorrectionImpulse denoted p, to Obtain the Lagrangian-Multiplier scalar term λ. known p=J^T*λ
 *
 *    C': JV_corrected+b = 0
 *      = J(V_curr + ΔV_correction)+b
 *      = J(V_curr + M^-1 * J^T * λ)+b
 *
 *      = JV_curr + JM^-1*J^T*λ + b
 *      = JV_curr/(JM^-1*J^T) + λ + b/(JM^-1*J^T)
 *
 *     λ= -JV_curr/(JM^-1*J^T) -b/(JM^-1*J^T)
 *      = -(JV_curr+b)/(JM^-1*J^T)
 *
 * // (there using Contact-Constraint as the constraint example.)
 */
public class SequentialImpulseConstraintSolver extends ConstraintSolver {

    public boolean USE_WARMSTART = false;

    @Override
    public void solveGroup(List<CollisionObject> bodies, List<CollisionManifold> manifolds, List<Constraint> constraints, float delta) {

        // prepare manifolds
        for (CollisionManifold manifold : manifolds)
        {
            manifold.refreshContactPoints();

            for (int i = 0;i < manifold.getNumContactPoints();i++) {
                prepareConstraints(manifold.bodyA(), manifold.bodyB(), manifold.getContactPoint(i), delta);
            }
        }

        // for const: constraint.buildJacobian();

        for (int i = 0;i < 10;i++) { // ITR_NUM

            // for const: constraint.solveConstraint(delta);

            for (CollisionManifold manifold : manifolds) {
                for (int j = 0;j < manifold.getNumContactPoints();j++) {
                    solveContact(manifold.bodyA(), manifold.bodyB(), manifold.getContactPoint(j), delta);
                }
            }

            for (CollisionManifold manifold : manifolds) {
                for (int j = 0;j < manifold.getNumContactPoints();j++) {
                    solveFriction(manifold.bodyA(), manifold.bodyB(), manifold.getContactPoint(j));
                }
            }
        }
    }

    private static void prepareConstraints(RigidBody bodyA, RigidBody bodyB, CollisionManifold.ContactPoint cp, float delta) {
        CollisionManifold.ContactPoint.ConstraintSolverPresistentData cpd = cp.cpd;

        // Contact-Constraint  /Normal

        cpd.rest_combined_restitution = cp.combined_restitution * relvel_dot_n(bodyA, bodyB, cp, cp.normOnB);

        cpd.normalEffectiveMass = jac_EffectiveMass(cp.rA, cp.rB, cp.normOnB,
                bodyA.getInvMass(), bodyB.getInvMass(), bodyA.getInvInertiaTensorWorld(), bodyB.getInvInertiaTensorWorld());


        // Friction-Constraint  /Tangential

        Maths.calculateTangentPlane(cp.normOnB, cpd.tangent1, cpd.tangent2);  // really needs update every frame.?

        cpd.tangentEffectiveMass1 = jac_EffectiveMass(cp.rA, cp.rB, cpd.tangent1,
                bodyA.getInvMass(), bodyB.getInvMass(), bodyA.getInvInertiaTensorWorld(), bodyB.getInvInertiaTensorWorld());
        cpd.tangentEffectiveMass2 = jac_EffectiveMass(cp.rA, cp.rB, cpd.tangent2,
                bodyA.getInvMass(), bodyB.getInvMass(), bodyA.getInvInertiaTensorWorld(), bodyB.getInvInertiaTensorWorld());



        Vector3f totalImpulse = new Vector3f(cp.normOnB).scale(cpd.normalImpulseSum);
        // apply previous frames impulse on both bodies
        bodyA.applyImpulse(totalImpulse, cp.rA);
        bodyB.applyImpulse(totalImpulse.negate(), cp.rB);
    }


    private static void solveContact(RigidBody bodyA, RigidBody bodyB, CollisionManifold.ContactPoint cp, float delta) {
        CollisionManifold.ContactPoint.ConstraintSolverPresistentData cpd = cp.cpd;

        // J V =          [n ^T  r_A×n ^T  -n ^T  -r_B×n ^T] [v_A  ω_A  v_B  ω_B]^T
        // p = J^T * λ =  [n ^T  r_A×n ^T  -n ^T  -r_B×n ^T] * -(J * V_curr + b)/(J * M^-1 * J^T)

        // Baumgarte Stabilization Method. (-penetration / delta) * beta. the beta factor always [0-1]. always close to 0, but not a "correct" value
        float positionalErr = (-cp.penetration / delta) * 0.2f;
        float restitutionErr = cpd.rest_combined_restitution;

        float bias = positionalErr + restitutionErr; // the bias term
        float JV = relvel_dot_n(bodyA, bodyB, cp, cp.normOnB); // JV term. relvel_dot_n

        float normalImpulse = -(JV + bias) / cpd.normalEffectiveMass; // the LagrangianMultiplier λ

        // clamp the λ.  SUM(λ_i) >= 0.
        float oldNormalImpulse = cpd.normalImpulseSum;
        cpd.normalImpulseSum = Math.max(oldNormalImpulse + normalImpulse, 0);
        normalImpulse = cpd.normalImpulseSum - oldNormalImpulse;

        Vector3f imp = new Vector3f(cp.normOnB).scale(normalImpulse);
        bodyA.applyImpulse(imp,          cp.rA);
        bodyB.applyImpulse(imp.negate(), cp.rB);
    }

    /**
     * a.k.a  == J*V_curr
     * when tends go collision, return negatives. tends separating, return positives. tends touching, return near zero.
     */
    private static float relvel_dot_n(RigidBody bodyA, RigidBody bodyB, CollisionManifold.ContactPoint cp, Vector3f n) {
        Vector3f relvel = Vector3f.sub(bodyA.getVelocity(cp.rA, null), bodyB.getVelocity(cp.rB, null), null);
        return Vector3f.dot(relvel, n);
    }

    /**
     * Calculate the Denominator of the λ LagrangianMultiplier Equation
     *
     *      -(JV_curr+b)
     * λ = --------------
     *        JM^-1J^T    <<< this been return.
     *
     * JM^-1J^T ==
     *                                    [m_A^-1  0       0       0     ] [n     ]
     * [n ^T  r_A×n ^T  -n ^T  -r_B×n ^T] [0       I_A^-1  0       0     ] [r_A×n ]
     *                                    [0       0       m_B^-1  0     ] [-n    ]
     *                                    [0       0       0       I_B^-1] [-r_B×n]
     * == m_A^-1 +
     *    r_A×n ^T * I_A^-1 * r_A×n +
     *    m_B^-1 +
     *    -r_B×n ^T * I_B^-1 * -r_B×n
     *
     * note that there the n (Contact-Nornal)'s coordinatespace needs as same as InertiaTensor's coordinatespace.
     */
    private static float jac_EffectiveMass(Vector3f rA, Vector3f rB, Vector3f n,
                                           float invMassA, float invMassB, Matrix3f invInertiaTensorWorldA, Matrix3f invInertiaTensorWorldB) {
        Vector3f  rA_x_n = Vector3f.cross(rA, n, null);
        Vector3f nrB_x_n = Vector3f.cross(rB, n, null).negate();

        Vector3f J_invM__1 = _matmul_1x3_3x3( rA_x_n, invInertiaTensorWorldA);
        Vector3f J_invM__2 = _matmul_1x3_3x3(nrB_x_n, invInertiaTensorWorldB);

        return invMassA + Vector3f.dot(J_invM__1, rA_x_n) + invMassB + Vector3f.dot(J_invM__2, nrB_x_n);
    }
    // return [1x3] mat
    private static Vector3f _matmul_1x3_3x3(Vector3f l, Matrix3f r) {
        return new Vector3f(
                l.x*r.m00 +l.y*r.m10 +l.z*r.m20, l.x*r.m01 +l.y*r.m11 +l.z*r.m21, l.x*r.m02 +l.y*r.m12 +l.z*r.m22
        );
    }




    private static void solveFriction(RigidBody bodyA, RigidBody bodyB, CollisionManifold.ContactPoint cp) {
        CollisionManifold.ContactPoint.ConstraintSolverPresistentData cpd = cp.cpd;

        float lim = cp.combined_friction * cpd.normalImpulseSum;

        if (lim <= 0) return;

        // friction1
        float tangentImpulse1 = -relvel_dot_n(bodyA, bodyB, cp, cpd.tangent1) / cpd.tangentEffectiveMass1;

        float oldTangentImpulseSum1 = cpd.tangentImpulseSum1;
        cpd.tangentImpulseSum1 = Maths.clamp(oldTangentImpulseSum1 + tangentImpulse1, -lim, lim);
        tangentImpulse1 = cpd.tangentImpulseSum1 - oldTangentImpulseSum1;


        // friction2
        float tangentImpulse2 = -relvel_dot_n(bodyA, bodyB, cp, cpd.tangent2) / cpd.tangentEffectiveMass2;

        float oldTangentImpulseSum2 = cpd.tangentImpulseSum2;
        cpd.tangentImpulseSum2 = Maths.clamp(oldTangentImpulseSum2 + tangentImpulse2, -lim, lim);
        tangentImpulse2 = cpd.tangentImpulseSum2 - oldTangentImpulseSum2;


        // applies.
        Vector3f imp = new Vector3f().addScaled(tangentImpulse1, cpd.tangent1).addScaled(tangentImpulse2, cpd.tangent2);
        bodyA.applyImpulse(imp,          cp.rA);
        bodyB.applyImpulse(imp.negate(), cp.rB);
    }


}
