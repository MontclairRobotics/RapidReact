package frc.robot.utilities;

import com.kauailabs.navx.frc.AHRS;

public class AHRSExtend extends AHRS 
{
    public AHRSExtend()
    {
        super();
        prevAngle = getAngle();
        angularVelocity = 0;
    }

    private double prevAngle;
    private double angularVelocity;

    @Override
    public void calibrate() {
        super.calibrate();
        prevAngle = getAngle();
    }
    
    @Override
    public void zeroYaw() {
        super.zeroYaw();
        prevAngle = 0;
    }

    public double getAngularVelocity()
    {
        return angularVelocity;
    }

    public void update(double deltaTime)
    {
        var angle = getAngle();

        angularVelocity = (angle - prevAngle) * deltaTime;
        prevAngle = angle;
    }
}
