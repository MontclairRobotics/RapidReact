package frc.robot.model;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.Constants;

public class BallMover 
{

    private TalonFX motor = new TalonFX(Constants.TRANSPORT_MOTOR_PORT);

    public void setMotor(double speed) 
    {
        motor.set(ControlMode.PercentOutput, speed);
    }
    
}
