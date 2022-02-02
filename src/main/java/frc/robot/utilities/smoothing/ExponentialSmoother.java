package frc.robot.utilities.smoothing;

public class ExponentialSmoother extends Smoother
{
    private final double factor;

    public ExponentialSmoother(double startValue, double minValue, double maxValue, double factor) 
    {
        super(startValue, minValue, maxValue);
        this.factor = factor;
    }

    protected void updateInternal(double deltaTime, double target) 
    {
        current += (target - current) / factor;
    }
}
