package frc.robot.framework.controllers;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class AnalogTrigger
{
    private final DoubleSupplier axis;

    public AnalogTrigger(DoubleSupplier axis)
    {
        this.axis = axis;
    }

    public Trigger whenGreaterThan(double target)
    {
        return new Trigger(() -> axis.getAsDouble() > target);
    }
    public Trigger whenGreaterOrEqualThan(double target)
    {
        return new Trigger(() -> axis.getAsDouble() >= target);
    }
    public Trigger whenLessThan(double target)
    {
        return new Trigger(() -> axis.getAsDouble() < target);
    }
    public Trigger whenLessOrEqualThan(double target)
    {
        return new Trigger(() -> axis.getAsDouble() <= target);
    }
    public Trigger whenEqualTo(double target)
    {
        return new Trigger(() -> axis.getAsDouble() == target);
    }
    public Trigger whenUnequalTo(double target)
    {
        return new Trigger(() -> axis.getAsDouble() != target);
    }
    public Trigger whenInRange(double min, double max)
    {
        return new Trigger(() -> {
            var a = axis.getAsDouble();
            return (min < a) && (a < max);
        });
    }
    public Trigger whenInRadius(double target, double radius)
    {
        return new Trigger(() -> {
            var a = axis.getAsDouble();
            return (target - radius < a) && (a < target + radius);
        });
    }
}