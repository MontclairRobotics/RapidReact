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
import frc.robot.framework.RobotState;
import frc.robot.framework.maths.MathUtils;
import frc.robot.framework.profiling.Profiler;

public final class Drivetrain extends SubsystemBase
{
    ////////////////////////////////////////////////
    // Final fields
    ////////////////////////////////////////////////
    private final CANSparkMax 
        leftMotor1 = new CANSparkMax(LEFT_MOTOR_1_PORT, MotorType.kBrushless),
        leftMotor2 = new CANSparkMax(LEFT_MOTOR_2_PORT, MotorType.kBrushless),
        //leftMotor3 = new CANSparkMax(LEFT_MOTOR_3_PORT, MotorType.kBrushless),
        rightMotor1 = new CANSparkMax(RIGHT_MOTOR_1_PORT, MotorType.kBrushless),
        rightMotor2 = new CANSparkMax(RIGHT_MOTOR_2_PORT, MotorType.kBrushless)//,
        //rightMotor3 = new CANSparkMax(RIGHT_MOTOR_3_PORT, MotorType.kBrushless)
    ;
    
    private final CANSparkMax[]
        leftMotors = {
            leftMotor1,
            leftMotor2,
            //leftMotor3
        },
        rightMotors = {
            rightMotor1, 
            rightMotor2,
            //rightMotor3
        }
    ;

    private final RelativeEncoder 
        leftEncoder1 = leftMotor1.getEncoder(),
        leftEncoder2 = leftMotor2.getEncoder(),
        //leftEncoder3 = leftMotor3.getEncoder(),
        rightEncoder1 = rightMotor1.getEncoder(),
        rightEncoder2 = rightMotor2.getEncoder()//,
        //rightEncoder3 = rightMotor3.getEncoder()
    ;


    private final RelativeEncoder[]
        leftEncoders = {
            leftEncoder1,
            leftEncoder2,
            //leftEncoder3
        },
        rightEncoders = {
            rightEncoder1, 
            rightEncoder2,
            //rightEncoder3
        }
    ;


    private final MotorControllerGroup leftMotorGroup = new MotorControllerGroup(leftMotors);
    private final MotorControllerGroup rightMotorGroup = new MotorControllerGroup(rightMotors);

    private final DifferentialDrive differentialDrive 
        = new DifferentialDrive(leftMotorGroup, rightMotorGroup);

    ////////////////////////////////////////////////
    // Other fields
    ////////////////////////////////////////////////
    private Profiler speedProfiler;
    
    private PIDController distancePid; //pid
    private PIDController anglePid; //angle

    private boolean isUsingDistancePID = true;
    private boolean isUsingAnglePID = true;

    private double targetSpeed;
    private double targetTurn;

    private TrackedNavx navx;

    private boolean isTargetingADistance = false;
    private double targetDistance = 0.0;
    private boolean isTargetingAnAngle = false;
    private double targetAngle = 0.0;

    private double maxOutput = 0.0;

    private boolean isStraightPidding = false;
    
    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////
    public Drivetrain(TrackedNavx navx) 
    {
        this.speedProfiler = Constants.DRIVE_PROFILER;

        this.navx = navx;

        for(var m : leftMotors)
        {
            m.setInverted(LEFT_DRIVE_INVERSION);
        }
        for(var m : rightMotors)
        {
            m.setInverted(RIGHT_DRIVE_INVERSION);
        }

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
    public void setup()
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

        //System.out.println("akp " + anglePid.getP());

        // Setup max output
        setProfiler(Constants.DRIVE_PROFILER);
        setMaxOutput(Constants.ROBOT_SPEEDS[0]);

        // Release all targets
        releaseAngleTarget();
        releaseDistanceTarget();
        stopStraightPidding();
    }

    public void startStraightPidding()
    {
        isStraightPidding = true;
        navx.zeroYaw();
    }
    public void stopStraightPidding()
    {
        isStraightPidding = false;
    }
    public boolean isStraightPidding()
    {
        return isStraightPidding;
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
    
    public void setProfiler(Profiler speedProfiler) 
    {
        this.speedProfiler = speedProfiler;
        Data.setCurrentEasing(speedProfiler.getName());
    }

    /**
     * Set the maximum output of this subsystem's motors
     * @param maxOutput The new maximum
     */
    public void setMaxOutput(double maxOutput) 
    {
        this.maxOutput = maxOutput;
        
        speedProfiler.setMinValue(-maxOutput);
        speedProfiler.setMaxValue(maxOutput);

        Data.setCurrentMaxSpeed(maxOutput);
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

    private double calculateAnglePID(double target)
    {
        var angle = navx.getAngle();
        var turn = MathUtils.clamp(
            anglePid.calculate(angle, target) * Constants.ANGLE_PID_SCALE, 
            -Constants.ANGLE_PID_SCALE, 
            Constants.ANGLE_PID_SCALE
        );

        System.out.println("Current angle: " + angle + "*");
        //System.out.println("Current turn: " + turn);
        
        Data.setAngleToTarget(target - angle);

        return turn;
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

            speed = -distancePid.calculate(averageDistance, targetDistance) * maxOutput;

            Data.setDistanceToTarget(targetDistance - averageDistance);
        }
        else
        {
            // Square input
            targetSpeed = MathUtils.signum(targetSpeed).get() * targetSpeed * targetSpeed;

            // Update the speed with the smoother
            speedProfiler.update(CommandRobot.deltaTime(), targetSpeed * maxOutput);
            speed = speedProfiler.current();

            //System.out.println("im fucking driving");
        }

        // Pid the angle if the input turn is within the deadband
        if(isUsingAnglePID && isTargetingAnAngle)
        {
            turn = calculateAnglePID(targetAngle);
        }
        else if(isUsingAnglePID && isStraightPidding)
        {
            turn = calculateAnglePID(0);
        }
        else
        {   
            turn = Constants.adjustTurn(speed, targetTurn);
        }

        // Clamp speed
        speed = MathUtils.clamp(speed, -maxOutput, maxOutput);

        // Set the drive
        differentialDrive.arcadeDrive(speed, turn, false);
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
        speedProfiler.setDirect(0.0);
    }

    public boolean isTargetingAnAngle()
    {
        return isTargetingAnAngle;
    }
    public boolean isTargetingADistance()
    {
        return isTargetingADistance;
    }
}
