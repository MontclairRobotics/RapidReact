package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.Arrays;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import static frc.robot.Constants.*;

import frc.robot.Constants;
import frc.robot.Data;
import frc.robot.RapidReact;
import frc.robot.framework.RobotState;
import frc.robot.framework.frc.commands.CommandRobot;
import frc.robot.framework.frc.commands.ManagedSubsystemBase;
import frc.robot.framework.math.MathUtils;
import frc.robot.framework.profiling.Profiler;
import frc.robot.managers.NavxManager;
import frc.robot.managers.VisionManager;
import frc.robot.structure.DetectedBall;

public final class Drivetrain extends ManagedSubsystemBase
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

    DifferentialDrivePoseEstimator poseEstimator;
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

    private boolean isTargetingADistance = false;
    private double targetDistance = 0.0;
    private boolean isTargetingAnAngle = false;
    private double targetAngle = 0.0;
    private boolean isTargetingABall = false;

    private double averageDistance = 0.0;

    private double maxOutput = 0.0;

    private boolean isStraightPidding = false;

    private boolean isTurnReversed = false;
    
    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////
    public Drivetrain() 
    {
        this.speedProfiler = Constants.DRIVE_PROFILER;

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
         //Setup Odometry
         poseEstimator = new DifferentialDrivePoseEstimator( //TODO poseEstimator.update()
         Constants.KINEMATICS,
         getRobotRotation(),
         getAverageDistanceLeftTraveled(),//TODO check correct unit
         getAverageDistanceLeftTraveled(), //TODO check correct unit
         new Pose2d()
     );
    }

    ////////////////////////////////////////////////
    // Methods
    ////////////////////////////////////////////////
    @Override
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
        anglePid.setIntegratorRange(
            -Data.getAngleIntMax(), 
            Data.getAngleIntMax()
        );

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
        stopTargetingABall();

        // Stop driving
        stop();

        // Reset encoders
        resetEncoders();
    }

    public double getAverageDistance()
    {
        return averageDistance;
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
        RapidReact.navx.zeroYaw();
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

    private double calculateAverageDistanceTraveled()
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
        RapidReact.navx.zeroYaw();
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
        return MathUtil.clamp(
            value * Constants.ANGLE_PID_SCALE, 
            -Constants.ANGLE_PID_SCALE, 
            Constants.ANGLE_PID_SCALE
        );
    }
    private double calculateAnglePID(double target)
    {
        var angle = RapidReact.navx.getAngle();
        var turn = modifyAnglePIDOut(anglePid.calculate(angle, target));

        //System.out.println("Current angle: " + angle + "*");
        //System.out.println("Current turn: " + turn);
        
        Data.setAngleToTarget(target - angle);

        return turn;
    }

    /**
     * Update this subsystem
     */
    @Override
    public void always()
    {
        if(DriverStation.isDisabled())
        {
            return;
        }

        averageDistance = calculateAverageDistanceTraveled();

        // Locals for speed and turn
        double speed, turn;

        // Pid the speed distance of the input if targetting a distacne
        var preEaseSpeed = 0.0;
        if(isUsingDistancePID && isTargetingADistance)
        {
            Data.setDriveMode("[pid: " + targetDistance + "]");
            Data.setDistanceToTarget(targetDistance - averageDistance);

            //System.out.println("Average distance: " + averageDistance);

            preEaseSpeed = distancePid.calculate(averageDistance, targetDistance);
        }
        else
        {
            Data.setDriveMode("[simple]");

            // Square input
            preEaseSpeed = MathUtils.powsign(targetSpeed, 1.1);

            //System.out.println("Robot driving");
        }

        speedProfiler.update(CommandRobot.deltaTime(), preEaseSpeed * maxOutput);
        speed = speedProfiler.current();

        // Clamp speed
        speed = MathUtil.clamp(speed, -maxOutput, maxOutput);

        Data.setDriveSpeed(speed);

        // Pid the angle if the input turn is within the deadband
        if(isUsingAnglePID && isTargetingAnAngle)
        {
            Data.setTurnMode("[angle pid]");

            turn = calculateAnglePID(targetAngle);
        }
        else if (isUsingBallPID && isTargetingABall)
        {
            Data.setTurnMode("[ball pid]");
            
            var ball = RapidReact.vision
                .getBall((a, b) -> Math.abs(a.getAngle()) < Math.abs(b.getAngle()));

            if (ball != null && Math.abs(ball.getAngle()) > Data.getBallTolerance())
            {
                Data.setAngleToBall(ball.getAngle());
                Data.setBallArea(ball.getArea());

                turn = modifyAnglePIDOut(
                    ballPid.calculate(-ball.getAngle(), 0.0)
                );
            }
            else
            {
                //isTargetingABall = false;
                turn = 0;
            }
        }
        else if(isUsingAnglePID && isStraightPidding)
        {
            Data.setTurnMode("[straight pid]");

            turn = calculateAnglePID(0);
            if(Math.abs(turn) < Constants.MIN_PID_TURN)
            {
                turn = 0;
            }
        }
        else
        {   
            Data.setTurnMode("[simple]");

            var realTargetTurn = MathUtils.powsign(targetTurn, 4);
            turn = Constants.adjustTurn(speed, realTargetTurn) * MathUtils.signFromBoolean(!isTurnReversed);
        }

        
        Data.setTurnSpeed(turn);

        //System.out.println("speed: " + speed);
        // Set the drive
        differentialDrive.arcadeDrive(speed, turn, false);
    }

    public double getMaxOutput()
    {
        return maxOutput;
    }
    public Profiler getProfiler()
    {
        return speedProfiler;
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

    public Rotation2d getRobotRotation() {
        return Rotation2d.fromDegrees(RapidReact.navx.getAngle()); //TODO check if this is correct w/ whoever wrote TrackedNavx class
    }

    public Pose2d getRobotPose() {
        return poseEstimator.getEstimatedPosition();
    }

    public void setRobotPose(Pose2d pose) {
        poseEstimator.resetPosition(
            getRobotRotation(),
            getAverageDistanceLeftTraveled(),
            getAverageDistanceRightTraveled(),
            pose
        );
    }

    public double getAverageDistanceLeftTraveled() {
        double sum = 0.0;
        for (RelativeEncoder e : leftEncoders) {
            sum += -MathUtils.signFromBoolean(LEFT_DRIVE_INVERSION) * e.getPosition();
        }
        return sum / leftEncoders.length;
    }

    public double getAverageDistanceRightTraveled() {
        double sum = 0.0;
        for (RelativeEncoder e : rightEncoders) {
            sum += -MathUtils.signFromBoolean(RIGHT_DRIVE_INVERSION) * e.getPosition();
        }
        return sum / rightEncoders.length;
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
