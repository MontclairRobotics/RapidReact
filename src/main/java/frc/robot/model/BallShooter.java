package frc.robot.model;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Constants;
import frc.robot.framework.CommandManager;
import frc.robot.framework.CommandModel;

public class BallShooter extends CommandModel
{
    
    private CANSparkMax rightMotor = new CANSparkMax(Constants.RIGHT_SHOOTER_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax leftMotor = new CANSparkMax(Constants.LEFT_SHOOTER_MOTOR_PORT, MotorType.kBrushless);

    public BallShooter(CommandManager manager) 
    {
        super(manager);

        rightMotor.setInverted(Constants.SHOOTER_RIGHT_INVERSION);
        leftMotor.setInverted(Constants.SHOOTER_LEFT_INVERSION);
    }

    public void startShooting() 
    {
        leftMotor.set(Constants.SHOOTER_SPEED);
        rightMotor.set(Constants.SHOOTER_SPEED);
    }

    public void reverseShooting()
    {
        leftMotor.set(Constants.REVERSE_SHOOTER_SPEED);
        rightMotor.set(Constants.REVERSE_SHOOTER_SPEED);
    }
    
    public void stop() 
    {
        leftMotor.set(0);
        rightMotor.set(0);
    }

}
