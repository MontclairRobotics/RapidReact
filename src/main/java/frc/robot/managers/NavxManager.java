package frc.robot.managers;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Data;
import frc.robot.framework.frc.commands.CommandRobot;
import frc.robot.framework.frc.commands.ManagedSubsystemBase;
import frc.robot.framework.frc.commands.ManagerBase;

public class NavxManager extends ManagerBase
{
    private AHRS ahrs;

    public NavxManager(AHRS ahrs)
    {
        this.ahrs = ahrs;

        prevAngle = getAngle();
        angularVelocity = 0;
    }

    public NavxManager()
    {
        this(new AHRS());
    }

    private double angleZero = 0;
    private double prevAngle;
    private double angularVelocity;

    @Override
    public void reset() 
    {
        calibrate();
    }

    public void calibrate() 
    {
        ahrs.calibrate();
        prevAngle = getAngleUnzeroed();
    }    
    
    public double getAngleUnzeroed()
    {
        return ahrs.getAngle();
    }
    public double getAngle()
    {
        return getAngleUnzeroed() - angleZero;
    }

    public void zeroYaw() 
    {
        angleZero = getAngleUnzeroed();
        prevAngle = angleZero;

        System.out.println("!!! RESETTING NAVX YAW !!!");
    }

    public double getAngularVelocity()
    {
        return angularVelocity;
    }

    @Override
    public void always()
    {
        var angle = getAngleUnzeroed();

        angularVelocity = (angle - prevAngle) / CommandRobot.deltaTime();
        prevAngle = angle;
    }
}
