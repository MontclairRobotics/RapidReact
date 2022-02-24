package frc.robot.model;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Constants;
import frc.robot.framework.CommandManager;
import frc.robot.framework.CommandModel;

public class BallSucker extends CommandModel
{
    private TalonFX motor = new TalonFX(Constants.INTAKE_MOTOR_PORT);

    public BallSucker(CommandManager manager) 
    {
        super(manager);
    }

    public void startSucking() 
    {
        motor.set(ControlMode.PercentOutput, Constants.BALL_SUCKER_MOTOR_SPEED);
    }

    public void stop() 
    {
        motor.set(ControlMode.PercentOutput, 0);
    }
}
