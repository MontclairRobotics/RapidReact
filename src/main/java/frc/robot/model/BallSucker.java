package frc.robot.model;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Constants;

public class BallSucker extends CommandModel
{
    
    private CANSparkMax motor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);

    public void setMotor() 
    {
        motor.set(CONSTANTS.BALL_SUCKER_MOTOR_SPEED);
    }

}
