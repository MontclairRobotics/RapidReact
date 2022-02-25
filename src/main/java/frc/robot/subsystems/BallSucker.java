package frc.robot.model;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class BallSucker extends SubsystemBase
{
    private TalonFX motor = new TalonFX(Constants.INTAKE_MOTOR_PORT);

    public void startSucking()
    {
        motor.set(ControlMode.PercentOutput, Constants.BALL_SUCKER_MOTOR_SPEED);
    }

    public void stop() 
    {
        motor.set(ControlMode.PercentOutput, 0);
    }
}
