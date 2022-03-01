package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.Arrays;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import static frc.robot.Constants.*;

import frc.robot.Constants;
import frc.robot.Data;
import frc.robot.framework.CommandRobot;
import frc.robot.framework.profiling.Profiler;

public final class Drivetrain extends SubsystemBase
{
    public static final class Settings
    {
        
    }

    ////////////////////////////////////////////////
    // Final fields
    ////////////////////////////////////////////////
    private final CANSparkMax 
        leftMotor1 = new CANSparkMax(LEFT_MOTOR_1_PORT, MotorType.kBrushless),
        leftMotor2 = new CANSparkMax(LEFT_MOTOR_2_PORT, MotorType.kBrushless),
        leftMotor3 = new CANSparkMax(LEFT_MOTOR_3_PORT, MotorType.kBrushless),
        rightMotor1 = new CANSparkMax(RIGHT_MOTOR_1_PORT, MotorType.kBrushless),
        rightMotor2 = new CANSparkMax(RIGHT_MOTOR_2_PORT, MotorType.kBrushless),
        rightMotor3 = new CANSparkMax(RIGHT_MOTOR_3_PORT, MotorType.kBrushless)
    ;
    
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

    private final RelativeEncoder 
        leftEncoder1 = leftMotor1.getEncoder(),
        leftEncoder2 = leftMotor2.getEncoder(),
        leftEncoder3 = leftMotor3.getEncoder(),
        rightEncoder1 = rightMotor1.getEncoder(),
        rightEncoder2 = rightMotor2.getEncoder(),
        rightEncoder3 = rightMotor3.getEncoder()
    ;


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
    private Profiler speedSmoother;
    private PIDController distancePid; //pid
    private PIDController anglePid; //angle

    private boolean isUsingDistancePID = true;
    private boolean isUsingAnglePID = true;

    private double targetSpeed;
    private double targetTurn;

    private AHRSTracker navx;

    private boolean isTargetingADistance = false;
    private double targetDistance = 0.0;
    private boolean isTargetingAnAngle = false;
    private double targetAngle = 0.0;

    
    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////
    public Drivetrain(Profiler defaultSmoother, AHRSTracker navx) 
    {
        speedSmoother = defaultSmoother;

        this.navx = navx;

        leftMotorGroup.setInverted(LEFT_DRIVE_INVERSION);
        rightMotorGroup.setInverted(RIGHT_DRIVE_INVERSION);

        for(var e : leftEncoders)
        {
            e.setPositionConversionFactor(CONVERSION_RATE);
        }
        for(var e : rightEncoders)
        {
            e.setPositionConversionFactor(CONVERSION_RATE);
        }
    }

    ////////////////////////////////////////////////
    // Methods
    ////////////////////////////////////////////////
    public void setupPID()
    {
        // Setup distance pid
        distancePid = new PIDController(
            Data.getDistanceKP(),
            Data.getDistanceKI(),
            Data.getDistanceKD()
        );
        distancePid.setTolerance(Data.getDistanceTolerance());

        // Setup angle pid
        anglePid = new PIDController(
            Data.getAngleKP(),
            Data.getAngleKI(),
            Data.getAngleKD()
        );
        anglePid.setTolerance(Data.getAngleTolerance());
    }

    public void enableAllPID()
    {
        isUsingAnglePID = true;
        isUsingDistancePID = true;
    }
    public void disableAllPID()
    {
        isUsingAnglePID = false;
        isUsingDistancePID = false;
    }
    
    public void setSmoother(Profiler speedSmoother) 
    {
        this.speedSmoother = speedSmoother;
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
        killMomentum();
    }

    public void resetEncoders()
    {
        for(var e : leftEncoders)
        {
            e.setPosition(0);
        }
        for(var e : rightEncoders)
        {
            e.setPosition(0);
        }
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
    public void setTargetDistance(double td)
    {
        targetDistance = td;
        resetEncoders();
        isTargetingADistance = true;
    }
    public void setTargetAngle(double ta)
    {
        navx.zeroYaw();
        targetAngle = ta;
        isTargetingAnAngle = true;
    }

    public void set(double targetSpeed, double targetTurn)
    {
        this.targetSpeed = targetSpeed;
        this.targetTurn = targetTurn;
    }

    /**
     * Update this subsystem
     */
    @Override
    public void periodic()
    {
        // Locals for speed and turn
        double speed, turn;

        // Pid the speed distance of the input if targetting a distacne
        if(isUsingDistancePID && isTargetingADistance)
        {
            var averageDistance = getAverageDistanceTraveled();

            System.out.println("Average distance: " + averageDistance);

            speed = distancePid.calculate(averageDistance, targetDistance);

            Data.setDistanceToTarget(targetDistance - averageDistance);
        }
        else
        {            
            // Update the speed with the smoother
            speedSmoother.update(CommandRobot.deltaTime(), targetSpeed);
            speed = speedSmoother.getCurrent();
        }

        // Pid the angle if the input turn is within the deadband
        if(isUsingAnglePID && isTargetingAnAngle)
        {
            var angle = navx.getYaw();
            turn = -anglePid.calculate(angle, targetAngle);

            //System.out.println("!!!!!!!!!!!!!!");
            
            Data.setAngleToTarget(targetAngle - angle);
        }
        else
        {   
            turn = Constants.adjustTurn(speed, targetTurn);
        }

        // Set the drive
        differentialDrive.arcadeDrive(speed, turn);
    }

    public void releaseDistanceTarget() 
    {
        isTargetingADistance = false;
    }

    public void releaseAngleTarget() 
    {
        isTargetingAnAngle = false;
    }

    public boolean reachedTargetDistance()
    {
        return distancePid.atSetpoint();
    }
    public boolean reachedTargetAngle()
    {
        return anglePid.atSetpoint();
    }

    public void setUsingAnglePid(boolean value)
    {
        isUsingAnglePID = value;
    }
    public void setUsingDistancePid(boolean value)
    {
        isUsingDistancePID = value;
    }

    public void killMomentum() 
    {
        speedSmoother.setDirect(0);
    }

    public boolean isTargetingAnAngle()
    {
        return isTargetingAnAngle;
    }
    public boolean isTargetingADistance()
    {
        return isTargetingADistance;
    }

    public Object set(double d, int i) {
        return null;
    }
}
