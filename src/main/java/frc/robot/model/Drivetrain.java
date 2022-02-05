package frc.robot.model;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

import java.util.Arrays;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Constants;
import frc.robot.utilities.smoothing.Smoother;

public final class Drivetrain
{
    ////////////////////////////////////////////////
    // Final fields
    ////////////////////////////////////////////////
    private final CANSparkMax[]
        leftMotors = {
            new CANSparkMax(Constants.LEFT_MOTOR_1_PORT, MotorType.kBrushless),
            new CANSparkMax(Constants.LEFT_MOTOR_2_PORT, MotorType.kBrushless),
            new CANSparkMax(Constants.LEFT_MOTOR_3_PORT, MotorType.kBrushless),
        },
        rightMotors = {
            new CANSparkMax(Constants.RIGHT_MOTOR_1_PORT, MotorType.kBrushless),
            new CANSparkMax(Constants.RIGHT_MOTOR_2_PORT, MotorType.kBrushless),
            new CANSparkMax(Constants.RIGHT_MOTOR_3_PORT, MotorType.kBrushless)
        }
    ;

    private final Encoder[]
        leftEncoders  = Arrays.stream(leftMotors) .map(CANSparkMax::getEncoder).toArray(Encoder[]::new),
        rightEncoders = Arrays.stream(rightMotors).map(CANSparkMax::getEncoder).toArray(Encoder[]::new)
    ;

    private final MotorControllerGroup leftMotorGroup = new MotorControllerGroup(leftMotors);
    private final MotorControllerGroup rightMotorGroup = new MotorControllerGroup(rightMotors);

    private final DifferentialDrive differentialDrive 
        = new DifferentialDrive(leftMotorGroup, rightMotorGroup);

    ////////////////////////////////////////////////
    // Other fields
    ////////////////////////////////////////////////
    private Smoother speedSmoother;
    private PIDController distancePid; //pid
    private PIDController anglePid; //angle

    private double targetSpeed = 0.0;
    private double targetTurn  = 0.0;

    private AHRS navx;

    private boolean isTargetingADistance = false;
    private double targetDistance = 0.0;
    private boolean isTargetingAnAngle = false;
    
    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////
    public Drivetrain(Smoother defaultSmoother, AHRS navx) 
    {
        speedSmoother = defaultSmoother;

        // Setup distance pid
        distancePid = new PIDController(
            Constants.PID.KP,
            Constants.PID.KI,
            Constants.PID.KD
        );
        distancePid.setTolerance(Constants.PID.TOLERANCE);

        // Setup angle pid
        anglePid = new PIDController(
            Constants.AnglePID.KP,
            Constants.AnglePID.KI,
            Constants.AnglePID.KD
        );
        anglePid.setTolerance(Constants.AnglePID.TOLERANCE);

        this.navx = navx;
    }

    ////////////////////////////////////////////////
    // Methods
    ////////////////////////////////////////////////
    public void startStraightPid()
    {
        navx.zeroYaw();
    }

    public void setSmoother(Smoother speedSmoother) 
    {
        this.speedSmoother = speedSmoother;
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
     * Get the average distance traveled by the encoders
     */
    public double getAverageDistanceTraveled()
    {
        double sum = 0.0;
        for (Encoder e : leftEncoders) 
        {
            sum += e.getDistance();
        }
        for (Encoder e : rightEncoders) 
        {
            sum += e.getDistance();
        }
        return sum / (leftEncoders.length + rightEncoders.length);
    }
    
    /** 
     * Updates the target distance from what is inputted
     */
    public void setTargetDistance(double td){
        targetDistance = td;
        isTargetingADistance = true;
    }
    public void setTargetAngle(double ta){
        targetTurn = ta;
        isTargetingAnAngle = true;
    }

    /**
     * Update this subsystem
     */
    public void update(double deltaTime)
    {
        // Locals for speed and turn
        double speed, turn;

        // Pid the angle of the input turn is within the deadband
        if(isTargetingAnAngle)
        {
            turn = anglePid.calculate(navx.getYaw(), targetTurn);
        }
        else
        {
            turn = targetTurn;
        }

        // Pid the speed distance of the input if targetting a distacne
        if(isTargetingADistance)
        {
            speed = distancePid.calculate(getAverageDistanceTraveled(), targetDistance);
        }
        else
        {            
            // Update the speed with the smoother
            speedSmoother.update(deltaTime, targetSpeed);
            speed = speedSmoother.getCurrent();
        }

        // Set the drive
        differentialDrive.arcadeDrive(speed, turn);
    }


    public void activateDistanceTarget() 
    {
        isTargetingADistance = true;
    }

    public void activateAngleTarget() 
    {
        isTargetingAnAngle = true;;
    }

    public void releaseDistanceTarget() 
    {
        isTargetingADistance = false;
    }

    public void releaseAngleTarget() 
    {
        isTargetingAnAngle = false;
    }
}
