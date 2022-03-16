package frc.robot.framework.profiling;

import frc.robot.framework.maths.MathDouble;
import frc.robot.framework.maths.Updater;

/**
 * @apiNote was previously named Smoother
 */
public abstract class Profiler extends Updater<Double>
{
    private double minValue, maxValue;
    private String name;

    public Profiler(double startValue, double minValue, double maxValue, String name)
    {
        super(startValue);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.name = name;
    }

    public final double getMaxValue() {
        return maxValue;
    }

    public final void setMaxValue(double maxValue) 
    {
        this.maxValue = maxValue;
    }

    public final double getMinValue() 
    {
        return minValue;
    }

    public final void setMinValue(double minValue) 
    {
        this.minValue = minValue;
    }

    public final String getName()
    {
        return name;
    }

    @Override
    protected final Double update(double deltaTime, Double current, Double target) 
    {
        return update(deltaTime, current.doubleValue(), target.doubleValue());
    }
    protected abstract double update(double deltaTime, double current, double target);
}

