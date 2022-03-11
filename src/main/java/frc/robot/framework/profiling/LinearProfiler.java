package frc.robot.framework.profiling;

import frc.robot.framework.maths.MathDouble;

public class LinearProfiler extends Profiler 
{
    private final double maxAccel;
    private final double maxDecel;

    public LinearProfiler(
        double startValue, 
        double minValue, 
        double maxValue,
        double maxAccel,
        double maxDecel,
        String name) 
    {
        super(startValue, minValue, maxValue, name);
        this.maxAccel = maxAccel;
        this.maxDecel = maxDecel;
    }

    public LinearProfiler(
        double startValue, 
        double minValue, 
        double maxValue,
        double maxAccel,
        String name) 
    {
        this(startValue, minValue, maxValue, maxAccel, maxAccel, name);
    }

    @Override
    protected double update(double deltaTime, double current, double target) 
    {
        // Distance to target
        var delta /*velocity (unscaled)*/ = target - current;
        // Is the target and delta the same sign?
        var isSpeedingUp = MathDouble.signsMatch(current, delta);
        // Select the right bound based on the above.
        var bound /*velocity (unclamped)*/ = (isSpeedingUp ? maxAccel : maxDecel);
        // Find the real bound (accounting for time).
        var realBound /*velocity*/ = bound * MathDouble.clamp(deltaTime, 0, 1);
        // Find the real change (bounded by realBound).
        var realDelta /*velocity*/ = MathDouble.clamp(delta, -realBound, realBound);

        current += realDelta;
        return current;
    }
}
