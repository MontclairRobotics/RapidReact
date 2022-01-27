package frc.robot.model;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Constants;
import frc.robot.utilities.smoothing.Smoother;

public class Drivetrain
{
    private final MotorControllerGroup leftMotors = new MotorControllerGroup(
        new CANSparkMax(Constants.LEFT_MOTOR_1_PORT, MotorType.kBrushless),
        new CANSparkMax(Constants.LEFT_MOTOR_2_PORT, MotorType.kBrushless),
        new CANSparkMax(Constants.LEFT_MOTOR_3_PORT, MotorType.kBrushless)
    );
    private final MotorControllerGroup rightMotors = new MotorControllerGroup(
        new CANSparkMax(Constants.RIGHT_MOTOR_1_PORT, MotorType.kBrushless),
        new CANSparkMax(Constants.RIGHT_MOTOR_2_PORT, MotorType.kBrushless),
        new CANSparkMax(Constants.RIGHT_MOTOR_3_PORT, MotorType.kBrushless)
    );

    private final DifferentialDrive differentialDrive 
        = new DifferentialDrive(leftMotors, rightMotors);

    private Smoother smoother;

    public void setSmoother(Smoother smoother) 
    {
        this.smoother = smoother;
    }
    
    public Drivetrain(Smoother defaultSmoother) 
    {
        smoother = defaultSmoother;
    }

    /**
     * Drive this subsystem's motors with smoothing
     * @param speed The speed to drive at
     * @param turn  The amount to turn
     */
    public void driveSmoothed(double speed, double turn)
    {
        drive(smoother.update(speed).getCurrent(), turn);
    }

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
        driveSmoothed(speed, 0);
    }

}
