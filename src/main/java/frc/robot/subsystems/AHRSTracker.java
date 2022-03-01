package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Data;
import frc.robot.framework.CommandRobot;

public class AHRSTracker extends SubsystemBase 
{
    private AHRS ahrs;

    public AHRSTracker(AHRS ahrs)
    {
        this.ahrs = ahrs;

        prevAngle = getYaw();
        angularVelocity = 0;
    }

    private double yawZero = 0;
    private double navxTiltFactor = 0;

    private double prevAngle;
    private double angularVelocity;

    public void init()
    {
        navxTiltFactor = Math.sin(Math.toRadians(Data.getNAVXTilt()));
    }

    public void calibrate() 
    {
        ahrs.calibrate();
        prevAngle = getYaw();
    }
    
    public double getYawUnzeroed()
    {
        var yaw = ahrs.getAngle();
        return yaw + navxTiltFactor * Math.sin(Math.toRadians(yaw));
    }
    public double getYaw()
    {
        return getYawUnzeroed() - yawZero;
    }

    public void zeroYaw() 
    {
        yawZero = ahrs.getAngle();
        prevAngle = 0;
    }

    public double getAngularVelocity()
    {
        return angularVelocity;
    }

    @Override
    public void periodic()
    {
        var angle = getYawUnzeroed();

        angularVelocity = (angle - prevAngle) / CommandRobot.deltaTime();
        prevAngle = angle;

        Data.setAngularVelocity(angularVelocity);
    }
}
