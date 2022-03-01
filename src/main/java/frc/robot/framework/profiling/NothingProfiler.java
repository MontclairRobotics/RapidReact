package frc.robot.framework.profiling;

public class NothingProfiler extends Profiler
{
    public NothingProfiler(double startValue, double minValue, double maxValue) 
    {
        super(startValue, minValue, maxValue);
    }

    @Override
    protected void updateInternal(double deltaTime, double target) 
    {
        current = target;
    }
}
