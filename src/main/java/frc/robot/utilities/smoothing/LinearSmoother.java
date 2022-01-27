package frc.robot.utilities.smoothing;

import frc.robot.utilities.Maths;

public class LinearSmoother extends Smoother 
{
    private final double lambda;

    public LinearSmoother(double startValue, double minValue, double maxValue, double lambda) {
        super(startValue, minValue, maxValue);
        this.lambda = lambda;
    }

    @Override
    protected void updateInternal(double target) 
    {
        current += Maths.clamp(deltaTime() * (target - current), -lambda, lambda);
    }
    
    // private static double clamp(double num) {
    //     if (num > LAMBDA) return LAMBDA;
    //     else if (num < -LAMBDA) return -LAMBDA; 
    //     else return num;
    // }
}
