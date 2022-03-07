package frc.robot.subsystems;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import frc.robot.Constants;

public class Climber extends SubsystemBase
{
    public static enum ClimberSide
    {
        LEFT,
        RIGHT,
        BOTH
        ;

        public boolean isLeft()
        {
            return this == LEFT || this == BOTH;
        }
        public boolean isRight()
        {
            return this == RIGHT || this == BOTH;
        }
    }

    private TalonFX talonLeft = new TalonFX(Constants.LEFT_CLIMBER_MOTOR_PORT);
    private TalonFX talonRight = new TalonFX(Constants.RIGHT_CLIMBER_MOTOR_PORT);
    
    public Climber()
    {
        talonLeft.setInverted(Constants.LEFT_CLIMBER_INVERTED);
        talonRight.setInverted(Constants.RIGHT_CLIMBER_INVERTED);
    }
    
    public void startClimbing(ClimberSide side)
    {
        if(side.isLeft())
        {
            talonLeft.set(ControlMode.PercentOutput, Constants.CLIMBER_MOTOR_SPEED);
        }
        if(side.isRight())
        {
            talonRight.set(ControlMode.PercentOutput, Constants.CLIMBER_MOTOR_SPEED);
        }
    }

    public void startReverseClimbing(ClimberSide side)
    {
        if(side.isLeft())
        {
            talonLeft.set(ControlMode.PercentOutput, Constants.REVERSE_CLIMBER_MOTOR_SPEED);
        }
        if(side.isRight())
        {
            talonRight.set(ControlMode.PercentOutput, Constants.REVERSE_CLIMBER_MOTOR_SPEED);
        }
    }

    public void stop(ClimberSide side)
    {
        if(side.isLeft())
        {
            talonLeft.set(ControlMode.PercentOutput, 0);
        }
        if(side.isRight())
        {
            talonRight.set(ControlMode.PercentOutput, 0);
        }
    }
}