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
import frc.robot.DetectedBall;
import frc.robot.RapidReact;
import frc.robot.framework.CommandRobot;
import frc.robot.framework.RobotState;
import frc.robot.framework.maths.MathDouble;
import frc.robot.framework.profiling.Profiler;
import frc.robot.managers.VisionManager;

public final class Drivetrain extends SubsystemBase
{
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
    private Profiler speedProfiler;
    
    private PIDController distancePid; //pid
    private PIDController anglePid; //angle
    private PIDController ballPid; //ball

    private int currentSpeedIndex = 0;

    private boolean isUsingDistancePID = true;
    private boolean isUsingAnglePID = true;
    private boolean isUsingBallPID = true;

    private double targetSpeed;
    private double targetTurn;

    private TrackedNavx navx;

    private boolean isTargetingADistance = false;
    private double targetDistance = 0.0;
    private boolean isTargetingAnAngle = false;
    private double targetAngle = 0.0;
    private boolean isTargetingABall = false;

    private double maxOutput = 0.0;

    private boolean isStraightPidding = false;

    private boolean isTurnReversed = false;
    
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
    public void reset()
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

        // Setup ball pid
        ballPid = new PIDController(
            Data.getBallKP(),
            Data.getBallKI(),
            Data.getBallKD()
        );
        ballPid.setTolerance(Data.getBallTolerance());

        //System.out.println("akp " + anglePid.getP());

        // Setup max output
        setProfiler(Constants.DRIVE_PROFILER);
        currentSpeedIndex = 0;
        updateDriveSpeedIndex();

        // Release all targets
        releaseAngleTarget();
        releaseDistanceTarget();
        stopStraightPidding();

        // Kill momentum
        killMomentum();
    }

    public void nextDriveSpeed()
    {
        // Loop through speeds
        currentSpeedIndex++;
        currentSpeedIndex %= DRIVE_SPEEDS.length;

        // Set speed
        updateDriveSpeedIndex();
    }

    private void updateDriveSpeedIndex() 
    {
        setMaxOutput(Constants.DRIVE_SPEEDS[currentSpeedIndex]);
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
    public void stopTargetingABall()
    {
        isTargetingABall = false;
    }
    public boolean isStraightPidding()
    {
        return isStraightPidding;
    }

    public void enableAllPID()
    {
        isUsingAnglePID = true;
        isUsingDistancePID = true;
        isUsingBallPID = true;
    }
    public void disableAllPID()
    {
        isUsingAnglePID = false;
        isUsingDistancePID = false;
        isUsingBallPID = false;
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
            sum += /*MathDouble.signFromBoolean(LEFT_DRIVE_INVERSION) */ e.getPosition();
        }
        for (RelativeEncoder e : rightEncoders) 
        {
            sum += /*MathDouble.signFromBoolean(RIGHT_DRIVE_INVERSION) */ e.getPosition();
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
    public void startTargetingABall()
    {
        isTargetingABall = true;
        isStraightPidding = false;
    }

    public void set(double targetSpeed, double targetTurn)
    {
        this.targetSpeed = targetSpeed;
        this.targetTurn = targetTurn;
        //System.out.println("Setting Speed to: " + targetSpeed + "speed");
        //System.out.println("Setting Angle to: " + targetTurn + "percent turning");
    }

    private double modifyAnglePIDOut(double value)
    {
        return MathDouble.clamp(
            value * Constants.ANGLE_PID_SCALE, 
            -Constants.ANGLE_PID_SCALE, 
            Constants.ANGLE_PID_SCALE
        );
    }
    private double calculateAnglePID(double target)
    {
        var angle = navx.getAngle();
        var turn = modifyAnglePIDOut(anglePid.calculate(angle, target));

        //System.out.println("Current angle: " + angle + "*");
        //System.out.println("Current turn: " + turn);
        
        Data.setAngleToTarget(target - angle);

        return turn;
    }

    /**
     * Update this subsystem
     */
    public void onUpdate()
    {
        if(CommandRobot.getState().equals(RobotState.DISABLED))
            return;

        // Locals for speed and turn
        double speed, turn;

        // Pid the speed distance of the input if targetting a distacne
        if(isUsingDistancePID && isTargetingADistance)
        {
            var averageDistance = getAverageDistanceTraveled();
            Data.setDistanceToTarget(targetDistance - averageDistance);

            //System.out.println("Average distance: " + averageDistance);

            var tspeed = distancePid.calculate(averageDistance, targetDistance);

            speedProfiler.update(CommandRobot.deltaTime(), tspeed * maxOutput);
            speed = speedProfiler.current();

            System.out.println("the speed: " + speed);
        }
        else
        {
            // Square input
            var targetSpeedReal = MathDouble.signum(targetSpeed).get() * MathDouble.pow(MathDouble.abs(targetSpeed), 1.1);

            // Update the speed with the smoother
            speedProfiler.update(CommandRobot.deltaTime(), targetSpeedReal * maxOutput);
            speed = speedProfiler.current();

            //System.out.println("Robot driving");
        }

        // Pid the angle if the input turn is within the deadband
        if(isUsingAnglePID && isTargetingAnAngle)
        {
            turn = calculateAnglePID(targetAngle);
        }
        else if (isUsingBallPID && isTargetingABall)
        {
            var ball = RapidReact.vision
                .getBall((a, b) -> Math.abs(a.getAngle()) < Math.abs(b.getAngle()));
            System.out.println("We fuckin ball pidding!");

            if (ball != null)
            {
                turn = modifyAnglePIDOut(
                    ballPid.calculate(-ball.getAngle(), 0.0)
                );
                System.out.println("We fuckin ball pidding with turn: " + turn);
            }
            else
            {
                //isTargetingABall = false;
                turn = 0;
            }
        }
        else if(isUsingAnglePID && isStraightPidding)
        {
            System.out.println("not gay!");
            turn = calculateAnglePID(0);
        }
        else
        {   
            turn = Constants.adjustTurn(speed, targetTurn) * MathDouble.signFromBoolean(!isTurnReversed);
        }

        // Clamp speed
        speed = MathDouble.clamp(speed, -maxOutput, maxOutput);

        //System.out.println("speed: " + speed);
        // Set the drive
        differentialDrive.arcadeDrive(speed, turn, false);
    }

    public void releaseDistanceTarget() 
    {
        isTargetingADistance = false;
        stop();
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
        for(var p : Constants.PROFILERS)
        {
            p.setDirect(0.0);
        }
    }

    public boolean isTargetingAnAngle()
    {
        return isTargetingAnAngle;
    }
    public boolean isTargetingADistance()
    {
        return isTargetingADistance;
    }

    public void startReverseTurning()
    {
        isTurnReversed = true;
    }
    public void stopReverseTurning()
    {
        isTurnReversed = false;
    }
}
