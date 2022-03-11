package frc.robot.subsystems;
 
import com.revrobotics.CANSparkMax;  
import com.revrobotics.CANSparkMaxLowLevel.MotorType;  
  
import edu.wpi.first.wpilibj2.command.SubsystemBase;  
import frc.robot.Constants;  
  
public class RotationalClimber extends SubsystemBase 
{
    private CANSparkMax leftMotor = new CANSparkMax(Constants.LEFT_ROTATIONAL_CLIMBER_MOTOR_PORT, MotorType.kBrushless);  
    private CANSparkMax rightMotor = new CANSparkMax(Constants.RIGHT_ROTATIONAL_CLIMBER_MOTOR_PORT, MotorType.kBrushless);  
  
    public RotationalClimber() 
    {  
        leftMotor.setInverted(Constants.LEFT_ROTATIONAL_CLIMBER_INVERTED);  
        rightMotor.setInverted(Constants.RIGHT_ROTATIONAL_CLIMBER_INVERTED);  
    }  
  
    public void rotateForward() 
    {  
        leftMotor.set(Constants.ROTATIONAL_CLIMBER_MOTOR_SPEED);  
        rightMotor.set(Constants.ROTATIONAL_CLIMBER_MOTOR_SPEED);  
    }
  
    public void rotateBackward()
    {  
        leftMotor.set(Constants.REVERSE_ROTATIONAL_CLIMBER_MOTOR_SPEED);  
        rightMotor.set(Constants.REVERSE_ROTATIONAL_CLIMBER_MOTOR_SPEED);  
    }  
  
    public void stop() 
    {  
        leftMotor.set(0);  
        rightMotor.set(0);  
    }  
}  
