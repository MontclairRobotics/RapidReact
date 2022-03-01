package frc.robot.framework.profiling;

import frc.robot.framework.maths.MathUtils;
import frc.robot.framework.maths.Updater;

/**
 * @apiNote was previously named Smoother
 */
public abstract class Profiler extends Updater<Double>
{
    private double minValue, maxValue;

    public Profiler(double startValue, double minValue, double maxValue)
    {
        super(startValue);

        this.minValue = minValue;
        this.maxValue = maxValue;
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

    @Override
    protected final Double update(double deltaTime, Double current, Double target) 
    {
        return update(deltaTime, current, target);
    }
    protected abstract double update(double deltaTime, double current, double target);
}

