package frc.robot.model;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

import java.util.Arrays;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
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
    private final CANSparkMax leftMotor1 = new CANSparkMax(Constants.LEFT_MOTOR_1_PORT, MotorType.kBrushless);
    private final CANSparkMax leftMotor2 = new CANSparkMax(Constants.LEFT_MOTOR_2_PORT, MotorType.kBrushless);
    private final CANSparkMax leftMotor3 = new CANSparkMax(Constants.LEFT_MOTOR_3_PORT, MotorType.kBrushless);
    private final CANSparkMax rightMotor1 = new CANSparkMax(Constants.RIGHT_MOTOR_1_PORT, MotorType.kBrushless);
    private final CANSparkMax rightMotor2 = new CANSparkMax(Constants.RIGHT_MOTOR_2_PORT, MotorType.kBrushless);
    private final CANSparkMax rightMotor3 = new CANSparkMax(Constants.RIGHT_MOTOR_3_PORT, MotorType.kBrushless);
    
    private final CANSparkMax[]
        leftMotors = {
            leftMotor1,
            leftMotor2,
            leftMotor3
        },
        rightMotors = {
            rightMotor1, 
            rightMotor2,
            rightMotor3
        }
    ;

    private RelativeEncoder leftEncoder1 = leftMotor1.getEncoder();
    private RelativeEncoder leftEncoder2 = leftMotor2.getEncoder();
    private RelativeEncoder leftEncoder3 = leftMotor3.getEncoder();
    private RelativeEncoder rightEncoder1 = rightMotor1.getEncoder();
    private RelativeEncoder rightEncoder2 = rightMotor2.getEncoder();
    private RelativeEncoder rightEncoder3 = rightMotor3.getEncoder();


    private final RelativeEncoder[]
        leftEncoders = {
            leftEncoder1,
            leftEncoder2,
            leftEncoder3
        },
        rightEncoders = {
            rightEncoder1, 
            rightEncoder2,
            rightEncoder3
        }
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

        leftMotorGroup.setInverted(Constants.LEFT_INVERTED);
        rightMotorGroup.setInverted(Constants.RIGHT_INVERTED);
        
        leftEncoder1.setPositionConversionFactor(Constants.CONVERSION_RATE);
        leftEncoder2.setPositionConversionFactor(Constants.CONVERSION_RATE);
        leftEncoder3.setPositionConversionFactor(Constants.CONVERSION_RATE);

        rightEncoder1.setPositionConversionFactor(Constants.CONVERSION_RATE);
        rightEncoder2.setPositionConversionFactor(Constants.CONVERSION_RATE);
        rightEncoder3.setPositionConversionFactor(Constants.CONVERSION_RATE);
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
        // 42 ticks per revolutions
        // gear box ratio is 10.86 : 1
        double sum = 0.0;
        for (RelativeEncoder e : leftEncoders) 
        {
            sum += e.getPosition();
        }
        for (RelativeEncoder e : rightEncoders) 
        {
            sum += e.getPosition();
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

        // Pid the angle if the input turn is within the deadband
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
        leftEncoder1.setPosition(0.0);
    }

    public void releaseAngleTarget() 
    {
        isTargetingAnAngle = false;
    }
}
