package frc.robot.managers;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.RobotState;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import frc.robot.Constants;

public class Climber extends CommandModel
{
<<<<<<< Updated upstream
    private TalonFX talonLeft = new TalonFX(Constants.LEFT_CLIMBER_MOTOR_PORT);
    private TalonFX talonRight = new TalonFX(Constants.RIGHT_CLIMBER_MOTOR_PORT);
    
    public Climber(CommandManager manager)
    {       
        super(manager);

        talonLeft.setInverted(Constants.LEFT_INVERTED);
        talonRight.setInverted(Constants.RIGHT_INVERTED);
=======
    
    
    public Climber(TALONFX talonLeft, TALONFX talonRight)
    {
        talonLeft = new TalonFX(0);
        talonRight = new TalonFX(0);

        this.talonLeft = talonLeft;
        this.talonRight = talonRight;
            
        talonLeft.setInverted(Constants.LEFT_CLIMB_INVERTED);
        talonRight.setInverted(Constants.RIGHT_CLIMB_INVERTED);

>>>>>>> Stashed changes
    }
    
    public void startClimbing()
    {
        talonLeft.set(ControlMode.PercentOutput, CONSTANTS.TALON_MOTOR_SPEED);
        talonRight.set(ControlMode.PercentOutput, CONSTANTS.TALON_MOTOR_SPEED);
    }
    public void stop()
    {
        talonLeft.set(ControlMode.PercentOutput, 0);
        talonRight.set(ControlMode.PercentOutput, 0);
    }


}