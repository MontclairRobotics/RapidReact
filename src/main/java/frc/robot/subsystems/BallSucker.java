package frc.robot.subsystems;

//import com.ctre.phoenix.motorcontrol.ControlMode;
//import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.framework.frc.commands.ManagedSubsystemBase;

public class BallSucker extends ManagedSubsystemBase
{
    private CANSparkMax motor = new CANSparkMax(Constants.INTAKE_MOTOR_PORT, MotorType.kBrushless);

    @Override
    public void reset() 
    {
        stop();
    }

    public void startSucking()
    {
        motor.set(Constants.BALL_SUCKER_MOTOR_SPEED);
    }

    public void startReverseSucking() 
    {
        motor.set(-Constants.BALL_SUCKER_MOTOR_SPEED);
    }

    public void stop() 
    {
        motor.set(0);
    }
    
    @Override
    public void whenInactive() 
    {
        stop();
    }
}
