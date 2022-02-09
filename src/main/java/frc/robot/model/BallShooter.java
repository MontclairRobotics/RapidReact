package frc.robot.model;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Constants;

public class BallShooter
{
    
    private CANSparkMax rightMotor = new CANSparkMax(Constants.RIGHT_SHOOTER_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax leftMotor = new CANSparkMax(Constants.LEFT_SHOOTER_MOTOR_PORT, MotorType.kBrushless);

    public BallShooter() {
        rightMotor.setInverted(Constants.SHOOTER_RIGHT_INVERSION);
        leftMotor.setInverted(Constants.SHOOTER_LEFT_INVERSION);
    }

    public void shoot() {
        leftMotor.set(Constants.SHOOTER_SPEED);
        rightMotor.set(Constants.SHOOTER_SPEED);
    }
    public void stop() {
        leftMotor.set(0);
        rightMotor.set(0);
    }

}
