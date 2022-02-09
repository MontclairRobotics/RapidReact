package frc.robot.model;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Constants;
import frc.robot.framework.CommandManager;
import frc.robot.framework.CommandModel;

public class BallSucker extends CommandModel
{
    
    private CANSparkMax motor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);

    public BallSucker(CommandManager manager) 
    {
        super(manager);
    }

    public void startSucking() 
    {
        motor.set(Constants.BALL_SUCKER_MOTOR_SPEED);
    }

    public void stop() 
    {
        motor.set(0);
    }
}
