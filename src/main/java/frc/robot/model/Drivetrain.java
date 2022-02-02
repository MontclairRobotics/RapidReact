package frc.robot.model;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
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

    private Smoother speedSmoother;
    private PIDController pid; //pid

    private double targetSpeed = 0, targetTurn = 0;

    public void setSmoother(Smoother speedSmoother) 
    {
        this.speedSmoother = speedSmoother;
    }
    
    public Drivetrain(Smoother defaultSmoother) 
    {
        speedSmoother = defaultSmoother;

        pid = new PIDController(
            Constants.PID.KP,
            Constants.PID.KI,
            Constants.PID.KD
        );
        pid.setTolerance(Constants.PID.KTolerance);
    }

    /**
     * Drive this subsystem's motors
     * @param speed The speed to drive at
     * @param turn  The amount to turn
     */
    public void set(double speed, double turn)
    {
        targetSpeed = speed;
        targetTurn = turn;
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
     * Update this subsystem
     */
    public void update(double deltaTime)
    {
        speedSmoother.update(deltaTime, targetSpeed);
        differentialDrive.arcadeDrive(speedSmoother.getCurrent(), targetTurn);
    }
}
