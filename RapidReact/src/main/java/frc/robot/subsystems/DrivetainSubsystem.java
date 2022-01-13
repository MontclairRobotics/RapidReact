package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PWMSparkMax;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DrivetainSubsystem extends SubsystemBase
{
    private final SpeedControllerGroup leftMotors = new SpeedControllerGroup(
        new Spark(Constants.LEFT_MOTOR_1_PORT),
        new Spark(Constants.LEFT_MOTOR_2_PORT),
        new Spark(Constants.LEFT_MOTOR_3_PORT)
    );
    private final SpeedControllerGroup rightMotors = new SpeedControllerGroup(
        new Spark(Constants.RIGHT_MOTOR_1_PORT),
        new Spark(Constants.RIGHT_MOTOR_2_PORT),
        new Spark(Constants.RIGHT_MOTOR_3_PORT)
    );

    private final DifferentialDrive differentialDrive 
        = new DifferentialDrive(leftMotors, rightMotors);

    
    public DrivetainSubsystem() {}

    /**
     * Drive this subsystem's motors
     * @param speed The speed to drive at
     * @param turn  The amount to turn
     */
    public void drive(double speed, double turn)
    {
        differentialDrive.arcadeDrive(speed, turn);
    }

    // TODO: IMPLEMENT 'setMaximumOutput'

    // TODO: IMPLEMENT 'stop'
}
