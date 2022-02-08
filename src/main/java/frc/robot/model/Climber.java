package frc.robot.managers;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.RobotState;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import frc.robot.Constants;

public class Climber {
    private TalonFX talonLeft = new TalonFX(0);
    private TalonFX talonRight = new TalonFX(0);
    
    public Climber(TALON talonLeft, TALON talonRight){
            this.talonLeft = talonLeft;
            this.talongRight = talonRight;
            
            talonLeft.setInverted(Constants.LEFT_INVERTED);
            talonRight.setInverted(Constants.RIGHT_INVERTED);
    }
    
    public void climb(){
        talonLeft.set(CONSTANTS.TALON_MOTOR_SPEED);
        talonRight.set(CONSTANTS.TALON_MOTOR_SPEED);
    }
    public void stop(){
        talonLeft.set(0);
        talonRight.set(0);
    }


}