package frc.robot.utilities.smoothing;

public class NullSmoother extends Smoother
{
    public NullSmoother(double startValue, double minValue, double maxValue) 
    {
        super(startValue, minValue, maxValue);
    }

    @Override
    protected void updateInternal(double target) 
    {
        current = target;
    }
}
