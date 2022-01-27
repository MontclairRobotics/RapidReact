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

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    private long lastUpdateTime;

    public double getCurrent() 
    {
        return current;
    }

    public Smoother update(double target) 
    {
        // Fix for first update
        if(lastUpdateTime == 0)
        {
            lastUpdateTime = System.currentTimeMillis() - 33;
        }

        updateInternal(target);
        current = Maths.clamp(current, minValue, maxValue);

        lastUpdateTime = System.currentTimeMillis();

        return this;
    }

    public double deltaTime()
    {
        return (System.currentTimeMillis() - lastUpdateTime) / 1000.0;
    }

    protected abstract void updateInternal(double target);
}

