package frc.robot.model;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.Constants;
import frc.robot.framework.CommandManager;
import frc.robot.framework.CommandModel;

public class BallMover extends CommandModel
{

    private TalonFX motor = new TalonFX(Constants.TRANSPORT_MOTOR_PORT);

    public BallMover(CommandManager manager)
    {
        super(manager);
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
    
}
