package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Data;
import frc.robot.framework.commandrobot.CommandRobot;

public class TrackedNavx extends SubsystemBase 
{
    private AHRS ahrs;

    public TrackedNavx(AHRS ahrs)
    {
        this.ahrs = ahrs;

        prevAngle = getAngle();
        angularVelocity = 0;
    }

    public TrackedNavx()
    {
        this(new AHRS());
    }

    private double angleZero = 0;
    private double prevAngle;
    private double angularVelocity;

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

        System.out.println("!!! RESET YAW !!!");
    }

    public double getAngularVelocity()
    {
        return angularVelocity;
    }

    @Override
    public void periodic()
    {
        var angle = getAngleUnzeroed();

        angularVelocity = (angle - prevAngle) / CommandRobot.deltaTime();
        prevAngle = angle;

        // Data.setAngularVelocity(angularVelocity);
    }
}
