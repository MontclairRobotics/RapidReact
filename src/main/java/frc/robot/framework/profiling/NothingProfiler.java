package frc.robot.framework.profiling;

public class NothingProfiler extends Profiler
{
    public NothingProfiler(double startValue, double minValue, double maxValue) 
    {
        super(startValue, minValue, maxValue);
    }

    @Override
    protected double update(double deltaTime, double current, double target) 
    {
        return target;
    }
}
