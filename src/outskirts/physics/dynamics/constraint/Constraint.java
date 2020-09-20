package outskirts.physics.dynamics.constraint;

/**
 * SpringConstarint : Point2Point{Rod(no-rot)/String(rot)}Constraint,
 * HingeConstraint
 * SliderConstraint
 *
 */
public abstract class Constraint {

    public abstract void solveConstraint(float delta);

}
