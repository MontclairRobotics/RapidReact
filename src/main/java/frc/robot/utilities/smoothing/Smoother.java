package frc.robot.utilities.smoothing;

import frc.robot.utilities.Maths;

public abstract class Smoother 
{
    protected double current, target;
    private double minValue, maxValue;

    public Smoother(double startValue, double minValue, double maxValue)
    {
        current = startValue;
        target = startValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public final double getMaxValue() {
        return maxValue;
    }

    public final void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public final double getMinValue() {
        return minValue;
    }

    public final void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public final double getCurrent() 
    {
        return current;
    }

    public final Smoother update(double deltaTime, double target) 
    {
        updateInternal(deltaTime, target);
        current = Maths.clamp(current, minValue, maxValue);

        return this;
    }

    protected abstract void updateInternal(double deltaTime, double target);
}

