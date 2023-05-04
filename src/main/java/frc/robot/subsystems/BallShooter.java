package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.framework.ScoreHeights;
import frc.robot.framework.maths.MathUtils;

public class BallShooter extends SubsystemBase
{
    private double shooterSpeed = Constants.SHOOTER_SPEED;
    private CANSparkMax rightMotor = new CANSparkMax(Constants.RIGHT_SHOOTER_MOTOR_PORT, MotorType.kBrushless);
    private CANSparkMax leftMotor = new CANSparkMax(Constants.LEFT_SHOOTER_MOTOR_PORT, MotorType.kBrushless);

    public BallShooter() 
    {
        rightMotor.setInverted(Constants.SHOOTER_RIGHT_INVERSION);
        leftMotor.setInverted(Constants.SHOOTER_LEFT_INVERSION);
    }

    public void startShooting() 
    {
        leftMotor.set(shooterSpeed);
        rightMotor.set(shooterSpeed);
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

    public void setHeight(ScoreHeights height) {
        shooterSpeed = MathUtils.clamp(height.getSpeed(), 0, 1);
    }

}
