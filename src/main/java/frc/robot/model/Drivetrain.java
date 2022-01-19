package frc.robot.model;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Constants;

public class Drivetrain
{
    private final MotorControllerGroup leftMotors = new MotorControllerGroup(
        new Spark(Constants.LEFT_MOTOR_1_PORT),
        new Spark(Constants.LEFT_MOTOR_2_PORT),
        new Spark(Constants.LEFT_MOTOR_3_PORT)
    );
    private final MotorControllerGroup rightMotors = new MotorControllerGroup(
        new Spark(Constants.RIGHT_MOTOR_1_PORT),
        new Spark(Constants.RIGHT_MOTOR_2_PORT),
        new Spark(Constants.RIGHT_MOTOR_3_PORT)
    );

    private final DifferentialDrive differentialDrive 
        = new DifferentialDrive(leftMotors, rightMotors);

    
    public Drivetrain() {}

    /**
     * Drive this subsystem's motors
     * @param speed The speed to drive at
     * @param turn  The amount to turn
     */
    public void drive(double speed, double turn)
    {
        differentialDrive.arcadeDrive(speed, turn);
    }

    /**
     * Set the maximum output of this subsystem's motors
     * @param maxOutput The new maximum
     */
    public void setMaxOutput(double maxOutput) 
    {
        differentialDrive.setMaxOutput(maxOutput);
    }

    /**
     * Stop the motors of this subsystem
     */
    public void stop() 
    {
        differentialDrive.stopMotor();
    }

    /**
     * Drive this subsystem's motors straight
     * @param speed The speed to drive at
     */
    public void driveStraight(double speed) 
    {
        drive(speed, 0);
    }

}
