package frc.robot.model;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class BallMover extends SubsystemBase
{

    private TalonFX motor = new TalonFX(Constants.TRANSPORT_MOTOR_PORT);

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
    
}
