package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.framework.frc.commands.ManagedSubsystemBase;

public class BallMover extends ManagedSubsystemBase
{
    private TalonFX motor = new TalonFX(Constants.TRANSPORT_MOTOR_PORT);

    @Override
    public void reset() 
    {
        stop();
    }

    public void startMoving() 
    {
        motor.set(ControlMode.PercentOutput, Constants.BALL_TRANSPORT_SPEED);
    }
    public void startMovingBackwards() 
    {
        motor.set(ControlMode.PercentOutput, -Constants.BALL_TRANSPORT_SPEED);
    }

    public void stop() 
    {
        motor.set(ControlMode.PercentOutput, 0);
    }

    @Override
    public void whenInactive() 
    {
        stop();
    }
}
