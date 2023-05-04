package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import frc.robot.Constants;
import frc.robot.framework.frc.commands.ManagedSubsystemBase;

public class Climber extends ManagedSubsystemBase
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

    private DigitalInput leftLimitSwitch = new DigitalInput(Constants.LEFT_LOWER_CLIMBER_LIMIT_PORT);
    private DigitalInput rightLimitSwitch = new DigitalInput(Constants.RIGHT_LOWER_CLIMBER_LIMIT_PORT);

    private double left;
    private double right;
    
    public Climber()
    {
        talonLeft.setInverted(Constants.LEFT_CLIMBER_INVERTED);
        talonRight.setInverted(Constants.RIGHT_CLIMBER_INVERTED);

        talonLeft.setSelectedSensorPosition(0);
        talonRight.setSelectedSensorPosition(0);

        
        left = 0;
        right = 0;
        
    }
    
    public void startClimbing(ClimberSide side)
    {

        if(side.isLeft())
        {
            left = Constants.CLIMBER_MOTOR_SPEED;
        }
        if(side.isRight())
        {
            right = Constants.CLIMBER_MOTOR_SPEED;
        }
    }

    public void startReverseClimbing(ClimberSide side)
    {
        if(side.isLeft())
        {
            left = Constants.REVERSE_CLIMBER_MOTOR_SPEED;
        }
        if(side.isRight())
        {
            right = Constants.REVERSE_CLIMBER_MOTOR_SPEED;
        }
    }

    public void stop(ClimberSide side)
    {
        if(side.isLeft())
        {
            left = 0;
        }
        if(side.isRight())
        {
            right = 0;
        }
    }
    
    @Override
    public void reset() 
    {
        stop(ClimberSide.BOTH);
    }

    @Override
    public void always()
    {
        // Limit switches using the encoder values
        /*
        if(left > 0)
        {
            if(talonLeft.getSelectedSensorPosition() >= Constants.CLIMBER_UPPER_LIMIT)
            {
                left = 0;
            }
        
        }
        if(right>0)
        {
            if(talonRight.getSelectedSensorPosition() >= Constants.CLIMBER_UPPER_LIMIT)
            {
                right = 0;
            }
        }
        

        if(left<0)
        {
            if(talonLeft.getSelectedSensorPosition() <= 0)
            {
                left = 0;
            }
        }

        if(right<0)
        {
            if(talonRight.getSelectedSensorPosition() <= 0)
            {
                right = 0;
            } 
        }
        */

        // code for the hard limit switches
        /*
        if (left < 0 && leftLimitSwitch.get()) 
        {
            left = 0;
        }
        if (right < 0 && rightLimitSwitch.get())
        {
            right = 0;
        }
        */

        talonLeft.set(ControlMode.PercentOutput, left);
        talonRight.set(ControlMode.PercentOutput, right);
    }

    @Override
    public void whenInactive() 
    {
        stop(ClimberSide.BOTH);
    }
}