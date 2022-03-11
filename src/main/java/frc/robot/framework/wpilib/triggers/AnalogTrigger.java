package frc.robot.framework.wpilib.triggers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.functions.DoubleDualPredicate;
import frc.robot.framework.functions.DoublePredicate;
import frc.robot.framework.functions.DoubleTriPredicate;
import frc.robot.framework.maths.MathUtils;

public class AnalogTrigger
{
    private final DoubleSupplier axis;

    //////////////////////////////////
    // CONSTRUCTION
    //////////////////////////////////
    public AnalogTrigger(DoubleSupplier axis)
    {
        this.axis = axis;
    }

    public static AnalogTrigger from(DoubleSupplier axis)
    {
        return new AnalogTrigger(axis);
    }

    //////////////////////////////////
    // OPERATORS
    //////////////////////////////////
    public AnalogTrigger plus(AnalogTrigger other)
    {
        return against(other, MathUtils::add);
    }
    public AnalogTrigger plus(DoubleSupplier other)
    {
        return against(other, MathUtils::add);
    }
    public AnalogTrigger plus(double other)
    {
        return against(other, MathUtils::add);
    }

    public AnalogTrigger minus(AnalogTrigger other)
    {
        return against(other, MathUtils::subtract);
    }
    public AnalogTrigger minus(DoubleSupplier other)
    {
        return against(other, MathUtils::subtract);
    }
    public AnalogTrigger minus(double other)
    {
        return against(other, MathUtils::subtract);
    }

    public AnalogTrigger times(AnalogTrigger other)
    {
        return against(other, MathUtils::multiply);
    }
    public AnalogTrigger times(DoubleSupplier other)
    {
        return against(other, MathUtils::multiply);
    }
    public AnalogTrigger times(double other)
    {
        return against(other, MathUtils::multiply);
    }

    public AnalogTrigger over(AnalogTrigger other)
    {
        return against(other, MathUtils::divide);
    }
    public AnalogTrigger over(DoubleSupplier other)
    {
        return against(other, MathUtils::divide);
    }
    public AnalogTrigger over(double other)
    {
        return against(other, MathUtils::divide);
    }

    public AnalogTrigger raisedTo(AnalogTrigger other)
    {
        return against(other, MathUtils::pow);
    }
    public AnalogTrigger raisedTo(DoubleSupplier other)
    {
        return against(other, MathUtils::pow);
    }
    public AnalogTrigger raisedTo(double other)
    {
        return against(other, MathUtils::pow);
    }

    public AnalogTrigger modulo(AnalogTrigger other)
    {
        return against(other, MathUtils::modulo);
    }
    public AnalogTrigger modulo(DoubleSupplier other)
    {
        return against(other, MathUtils::modulo);
    }
    public AnalogTrigger modulo(double other)
    {
        return against(other, MathUtils::modulo);
    }

    public AnalogTrigger min(AnalogTrigger other)
    {
        return against(other, MathUtils::min);
    }
    public AnalogTrigger min(DoubleSupplier other)
    {
        return against(other, MathUtils::min);
    }
    public AnalogTrigger min(double other)
    {
        return against(other, MathUtils::min);
    }

    public AnalogTrigger max(AnalogTrigger other)
    {
        return against(other, MathUtils::max);
    }
    public AnalogTrigger max(DoubleSupplier other)
    {
        return against(other, MathUtils::max);
    }
    public AnalogTrigger max(double other)
    {
        return against(other, MathUtils::max);
    }

    public AnalogTrigger against(AnalogTrigger other, DoubleBinaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble(), other.axis.getAsDouble()));
    }
    public AnalogTrigger against(DoubleSupplier other, DoubleBinaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble(), other.getAsDouble()));
    }
    public AnalogTrigger against(double other, DoubleBinaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble(), other));
    }

    public AnalogTrigger squared()
    {
        return applied(MathUtils::square);
    }
    public AnalogTrigger sqrt()
    {
        return applied(MathUtils::sqrt);
    }
    public AnalogTrigger inversed()
    {
        return applied(MathUtils::inverse);
    }
    public AnalogTrigger negated()
    {
        return applied(MathUtils::negative);
    }
    public AnalogTrigger abs()
    {
        return applied(MathUtils::abs);
    }

    public AnalogTrigger applied(DoubleUnaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble()));
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
    public Trigger when(DoubleDualPredicate predicate, AnalogTrigger other)
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
    public Trigger when(DoubleTriPredicate predicate, double otherA, AnalogTrigger otherB)
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
    public Trigger when(DoubleTriPredicate predicate, DoubleSupplier otherA, AnalogTrigger otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.getAsDouble(), otherB.axis.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogTrigger otherA, double otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogTrigger otherA, DoubleSupplier otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogTrigger otherA, AnalogTrigger otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB.axis.getAsDouble()));
    }

    public Trigger whenGreaterThan(double value)
    {
        return when(MathUtils::greaterThan, value);
    }
    public Trigger whenGreaterThanOrEqualTo(double value)
    {
        return when(MathUtils::greaterThanOrEqualTo, value);
    }
    public Trigger whenLessThan(double value)
    {
        return when(MathUtils::lessThan, value);
    }
    public Trigger whenLessThanOrEqualTo(double value)
    {
        return when(MathUtils::lessThanOrEqualTo, value);
    }
    public Trigger whenEqualTo(double value)
    {
        return when(MathUtils::equalTo, value);
    }
    public Trigger whenUnequalTo(double value)
    {
        return when(MathUtils::unequalTo, value);
    }
}