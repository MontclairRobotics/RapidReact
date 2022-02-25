package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.framework.CommandRobot;

public class AHRSTracker extends SubsystemBase 
{
    private AHRS ahrs;

    public AHRSTracker(AHRS ahrs)
    {
        this.ahrs = ahrs;

        prevAngle = ahrs.getAngle();
        angularVelocity = 0;
    }

    private double prevAngle;
    private double angularVelocity;

    public void calibrate() 
    {
        prevAngle = ahrs.getAngle();
    }
    
    public void zeroYaw() 
    {
        prevAngle = 0;
    }

    public double getAngularVelocity()
    {
        return angularVelocity;
    }

    public void update()
    {
        var angle = ahrs.getAngle();

        angularVelocity = (angle - prevAngle) / CommandRobot.deltaTime();
        prevAngle = angle;
    }
}
