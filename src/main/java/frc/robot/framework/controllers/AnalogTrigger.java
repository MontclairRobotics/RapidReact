package frc.robot.framework.controllers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.utilities.Ops;

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
        return against(other, Ops::add);
    }
    public AnalogTrigger plus(DoubleSupplier other)
    {
        return against(other, Ops::add);
    }
    public AnalogTrigger plus(double other)
    {
        return against(other, Ops::add);
    }

    public AnalogTrigger minus(AnalogTrigger other)
    {
        return against(other, Ops::subtract);
    }
    public AnalogTrigger minus(DoubleSupplier other)
    {
        return against(other, Ops::subtract);
    }
    public AnalogTrigger minus(double other)
    {
        return against(other, Ops::subtract);
    }

    public AnalogTrigger times(AnalogTrigger other)
    {
        return against(other, Ops::multiply);
    }
    public AnalogTrigger times(DoubleSupplier other)
    {
        return against(other, Ops::multiply);
    }
    public AnalogTrigger times(double other)
    {
        return against(other, Ops::multiply);
    }

    public AnalogTrigger over(AnalogTrigger other)
    {
        return against(other, Ops::divide);
    }
    public AnalogTrigger over(DoubleSupplier other)
    {
        return against(other, Ops::divide);
    }
    public AnalogTrigger over(double other)
    {
        return against(other, Ops::divide);
    }

    public AnalogTrigger raisedTo(AnalogTrigger other)
    {
        return against(other, Ops::pow);
    }
    public AnalogTrigger raisedTo(DoubleSupplier other)
    {
        return against(other, Ops::pow);
    }
    public AnalogTrigger raisedTo(double other)
    {
        return against(other, Ops::pow);
    }

    public AnalogTrigger modulo(AnalogTrigger other)
    {
        return against(other, Ops::modulo);
    }
    public AnalogTrigger modulo(DoubleSupplier other)
    {
        return against(other, Ops::modulo);
    }
    public AnalogTrigger modulo(double other)
    {
        return against(other, Ops::modulo);
    }

    public AnalogTrigger min(AnalogTrigger other)
    {
        return against(other, Ops::min);
    }
    public AnalogTrigger min(DoubleSupplier other)
    {
        return against(other, Ops::min);
    }
    public AnalogTrigger min(double other)
    {
        return against(other, Ops::min);
    }

    public AnalogTrigger max(AnalogTrigger other)
    {
        return against(other, Ops::max);
    }
    public AnalogTrigger max(DoubleSupplier other)
    {
        return against(other, Ops::max);
    }
    public AnalogTrigger max(double other)
    {
        return against(other, Ops::max);
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
        return applied(Ops::square);
    }
    public AnalogTrigger sqrt()
    {
        return applied(Ops::sqrt);
    }
    public AnalogTrigger inversed()
    {
        return applied(Ops::inverse);
    }
    public AnalogTrigger negated()
    {
        return applied(Ops::negative);
    }
    public AnalogTrigger abs()
    {
        return applied(Ops::abs);
    }

    public AnalogTrigger applied(DoubleUnaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble()));
    }
    //////////////////////////////////
    // TRIGGER GETTERS
    //////////////////////////////////
    public Trigger greaterThan(double target)
    {
        return new Trigger(() -> axis.getAsDouble() > target);
    }
    public Trigger greaterThanOrEqualTo(double target)
    {
        return new Trigger(() -> axis.getAsDouble() >= target);
    }
    public Trigger lessThan(double target)
    {
        return new Trigger(() -> axis.getAsDouble() < target);
    }
    public Trigger lessThanOrEqualTo(double target)
    {
        return new Trigger(() -> axis.getAsDouble() <= target);
    }
    public Trigger equalTo(double target)
    {
        return new Trigger(() -> axis.getAsDouble() == target);
    }
    public Trigger approximately(double target)
    {
        return new Trigger(() -> Math.abs(axis.getAsDouble() - target) <= Ops.epsilon);
    }
    public Trigger unequalTo(double target)
    {
        return new Trigger(() -> axis.getAsDouble() != target);
    }
    public Trigger notApproximately(double target)
    {
        return new Trigger(() -> Math.abs(axis.getAsDouble() - target) > Ops.epsilon);
    }

    public Trigger inRange(double min, double max)
    {
        return new Trigger(() -> {
            var a = axis.getAsDouble();
            return (min < a) && (a < max);
        });
    }
    public Trigger inRadius(double target, double radius)
    {
        return new Trigger(() -> {
            var a = axis.getAsDouble();
            return (target - radius < a) && (a < target + radius);
        });
    }

    public Trigger isInteger()
    {
        return new Trigger(() -> {
            var a = axis.getAsDouble();
            return (int)a == a;
        });
    }
    public Trigger isEven()
    {
        return new Trigger(() -> {
            var a = axis.getAsDouble();
            return (int)a == a && (int)a % 2 == 0;
        });
    }
    public Trigger isOdd()
    {
        return new Trigger(() -> {
            var a = axis.getAsDouble();
            return (int)a == a && (int)a % 2 == 1;
        });
    }

    public Trigger isPositive()
    {
        return greaterThan(0);
    }
    public Trigger isNegative()
    {
        return lessThan(0);
    }
    public Trigger isNonPositive()
    {
        return lessThanOrEqualTo(0);
    }
    public Trigger isNonNegative()
    {
        return greaterThanOrEqualTo(0);
    }
}