package frc.robot.model;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.framework.CommandManager;
import frc.robot.framework.CommandModel;
import frc.robot.framework.Commands;
import frc.robot.framework.RobotState;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import frc.robot.Constants;

public class Climber extends CommandModel
{
    private TalonFX talonLeft = new TalonFX(Constants.LEFT_CLIMBER_MOTOR_PORT);
    private TalonFX talonRight = new TalonFX(Constants.RIGHT_CLIMBER_MOTOR_PORT);
    
    public Climber(CommandManager manager)
    {       
        super(manager);

        talonLeft.setInverted(Constants.LEFT_CLIMBER_INVERTED);
        talonRight.setInverted(Constants.RIGHT_CLIMBER_INVERTED);
    }
    
    public void startClimbing()
    {
        talonLeft.set(ControlMode.PercentOutput, Constants.CLIMBER_MOTOR_SPEED);
        talonRight.set(ControlMode.PercentOutput, Constants.CLIMBER_MOTOR_SPEED);
    }

    //Delete Function?
    public void reverseClimbing()
    {
        talonLeft.set(ControlMode.PercentOutput, Constants.REVERSE_CLIMBER_MOTOR_SPEED);
        talonRight.set(ControlMode.PercentOutput, Constants.REVERSE_CLIMBER_MOTOR_SPEED);
    }

    public void stop()
    {
        talonLeft.set(ControlMode.PercentOutput, 0);
        talonRight.set(ControlMode.PercentOutput, 0);
    }
}