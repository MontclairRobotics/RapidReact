package frc.robot.framework.wpilib.triggers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.functions.DoubleDualPredicate;
import frc.robot.framework.functions.DoublePredicate;
import frc.robot.framework.functions.DoubleTriPredicate;
import frc.robot.framework.maths.MathUtils;

// I AM A TWO
public class AnalogValue
{
    private final DoubleSupplier axis;

    public DoubleSupplier getAxis()
    {
        return axis;
    }

    //////////////////////////////////
    // CONSTRUCTION
    //////////////////////////////////
    public AnalogValue(DoubleSupplier axis)
    {
        this.axis = axis;
    }

    public static AnalogValue from(DoubleSupplier axis)
    {
        return new AnalogValue(axis);
    }

    //////////////////////////////////
    // OPERATORS
    //////////////////////////////////
    public AnalogValue plus(AnalogValue other)
    {
        return against(other, (a, b) -> a + b);
    }
    public AnalogValue plus(DoubleSupplier other)
    {
        return against(other, (a, b) -> a + b);
    }
    public AnalogValue plus(double other)
    {
        return against(other, (a, b) -> a + b);
    }

    public AnalogValue minus(AnalogValue other)
    {
        return against(other, (a, b) -> a - b);
    }
    public AnalogValue minus(DoubleSupplier other)
    {
        return against(other, (a, b) -> a - b);
    }
    public AnalogValue minus(double other)
    {
        return against(other, (a, b) -> a - b);
    }

    public AnalogValue times(AnalogValue other)
    {
        return against(other, (a, b) -> a * b);
    }
    public AnalogValue times(DoubleSupplier other)
    {
        return against(other, (a, b) -> a * b);
    }
    public AnalogValue times(double other)
    {
        return against(other, (a, b) -> a * b);
    }

    public AnalogValue over(AnalogValue other)
    {
        return against(other, (a, b) -> a / b);
    }
    public AnalogValue over(DoubleSupplier other)
    {
        return against(other, (a, b) -> a / b);
    }
    public AnalogValue over(double other)
    {
        return against(other, (a, b) -> a / b);
    }

    public AnalogValue raisedTo(AnalogValue other)
    {
        return against(other, MathUtils::pow);
    }
    public AnalogValue raisedTo(DoubleSupplier other)
    {
        return against(other, MathUtils::pow);
    }
    public AnalogValue raisedTo(double other)
    {
        return against(other, MathUtils::pow);
    }

    public AnalogValue modulo(AnalogValue other)
    {
        return against(other, (a, b) -> a % b);
    }
    public AnalogValue modulo(DoubleSupplier other)
    {
        return against(other, (a, b) -> a % b);
    }
    public AnalogValue modulo(double other)
    {
        return against(other, (a, b) -> a % b);
    }

    public AnalogValue min(AnalogValue other)
    {
        return against(other, MathUtils::min);
    }
    public AnalogValue min(DoubleSupplier other)
    {
        return against(other, MathUtils::min);
    }
    public AnalogValue min(double other)
    {
        return against(other, MathUtils::min);
    }

    public AnalogValue max(AnalogValue other)
    {
        return against(other, MathUtils::max);
    }
    public AnalogValue max(DoubleSupplier other)
    {
        return against(other, MathUtils::max);
    }
    public AnalogValue max(double other)
    {
        return against(other, MathUtils::max);
    }

    public AnalogValue against(AnalogValue other, DoubleBinaryOperator operator)
    {
        return new AnalogValue(() -> operator.applyAsDouble(this.axis.getAsDouble(), other.axis.getAsDouble()));
    }
    public AnalogValue against(DoubleSupplier other, DoubleBinaryOperator operator)
    {
        return new AnalogValue(() -> operator.applyAsDouble(this.axis.getAsDouble(), other.getAsDouble()));
    }
    public AnalogValue against(double other, DoubleBinaryOperator operator)
    {
        return new AnalogValue(() -> operator.applyAsDouble(this.axis.getAsDouble(), other));
    }

    public AnalogValue squared()
    {
        return applied(MathUtils::square);
    }
    public AnalogValue sqrt()
    {
        return applied(MathUtils::sqrt);
    }
    public AnalogValue inversed()
    {
        return applied(a -> 1 / a);
    }
    public AnalogValue negated()
    {
        return applied(a -> -a);
    }
    public AnalogValue abs()
    {
        return applied(MathUtils::abs);
    }

    public AnalogValue applied(DoubleUnaryOperator operator)
    {
        return new AnalogValue(() -> operator.applyAsDouble(this.axis.getAsDouble()));
    }
    //////////////////////////////////
    // TRIGGER GETTERS
    //////////////////////////////////
    public Trigger when(DoublePredicate predicate)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble()));
    }

    public Trigger when(DoubleDualPredicate predicate, double other)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), other));
    }
    public Trigger when(DoubleDualPredicate predicate, DoubleSupplier other)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), other.getAsDouble()));
    }
    public Trigger when(DoubleDualPredicate predicate, AnalogValue other)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), other.axis.getAsDouble()));
    }

    public Trigger when(DoubleTriPredicate predicate, double otherA, double otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA, otherB));
    }
    public Trigger when(DoubleTriPredicate predicate, double otherA, DoubleSupplier otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA, otherB.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, double otherA, AnalogValue otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA, otherB.axis.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, DoubleSupplier otherA, double otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.getAsDouble(), otherB));
    }
    public Trigger when(DoubleTriPredicate predicate, DoubleSupplier otherA, DoubleSupplier otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.getAsDouble(), otherB.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, DoubleSupplier otherA, AnalogValue otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.getAsDouble(), otherB.axis.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogValue otherA, double otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogValue otherA, DoubleSupplier otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogValue otherA, AnalogValue otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB.axis.getAsDouble()));
    }

    public Trigger whenGreaterThan(double value)
    {
        return when((a, b) -> a > b, value);
    }
    public Trigger whenGreaterThanOrEqualTo(double value)
    {
        return when((a, b) -> a >= b, value);
    }
    public Trigger whenLessThan(double value)
    {
        return when((a, b) -> a < b, value);
    }
    public Trigger whenLessThanOrEqualTo(double value)
    {
        return when((a, b) -> a <= b, value);
    }
    public Trigger whenEqualTo(double value)
    {
        return when((a, b) -> a == b, value);
    }
    public Trigger whenUnequalTo(double value)
    {
        return when((a, b) -> a != b, value);
    }
}