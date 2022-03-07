
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.PIDDistanceCommand;
import frc.robot.framework.CommandRobot;
import frc.robot.framework.CommandRobotContainer;
import frc.robot.framework.RobotState;
import frc.robot.framework.maths.MathUtils;
import frc.robot.framework.vendors.rev.BlinkinLEDDriver;
import frc.robot.framework.wpilib.controllers.InputController;
import frc.robot.framework.wpilib.controllers.InputController.DPad;
import frc.robot.framework.wpilib.triggers.AnalogTrigger;
import frc.robot.subsystems.TrackedNavx;
import frc.robot.subsystems.Climber.ClimberSide;
import frc.robot.subsystems.BallMover;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.BallSucker;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

import static frc.robot.Constants.*;
import static frc.robot.framework.vendors.rev.BlinkinLEDMode.*;
import static frc.robot.framework.wpilib.controllers.InputController.Axis.*;
import static frc.robot.framework.wpilib.controllers.InputController.Button.*;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.*;
import static edu.wpi.first.wpilibj2.command.CommandBase.*;
import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;
import static frc.robot.framework.Commands.*;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public final class RapidReactContainer extends CommandRobotContainer 
{
    ////////////////////////////////
    // CONTROLLERS
    ////////////////////////////////
    public static final InputController driverController = InputController.from(DRIVER_CONTROLLER_TYPE,
            DRIVER_CONTROLLER_PORT);
    public static final InputController operatorController = InputController.from(OPERATOR_CONTROLLER_TYPE,
            OPERATOR_CONTROLLER_PORT);

    ////////////////////////////////
    // MODELS
    ////////////////////////////////
    public static final TrackedNavx navx = new TrackedNavx(new AHRS());
    public static final Drivetrain drivetrain = new Drivetrain(navx);
    public static final BallSucker ballSucker = new BallSucker();
    public static final BallMover ballMover = new BallMover();
    public static final BallShooter ballShooter = new BallShooter();
    public static final Climber climber = new Climber();

    private static UsbCamera intakeCamera;
    private static UsbCamera shooterCamera;
    
    /*
    public final BlinkinLEDDriver blinkinLEDDriver = new BlinkinLEDDriver(BLINKIN_LED_DRIVER_PORT, C1_BREATH_SLOW, DISABLED);

    public final Ultrasonic ultrasonicLeft = new Ultrasonic(LEFT_ULTRASONIC_SENSOR_PING_PORT, LEFT_ULTRASONIC_SENSOR_ECHO_PORT);
    public final Ultrasonic ultrasonicRight = new Ultrasonic(RIGHT_ULTRASONIC_SENSOR_PING_PORT, RIGHT_ULTRASONIC_SENSOR_ECHO_PORT);
    //*/

    public static int speedIndex = 0;

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init() 
    {
        // Calibrate the navx
        navx.calibrate();
        navx.zeroYaw();

        // PID setup
        drivetrain.setup();
        drivetrain.enableAllPID();
        drivetrain.killMomentum();

        // TEMP
        //drivetrain.disableAllPID();
    }

    @Override
    public void initOnce()
    {
        // Smart dashboard
        Data.setup();

        // Camera servers
        intakeCamera = CameraServer.startAutomaticCapture("Intake Camera", 0);
        intakeCamera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);

        shooterCamera = CameraServer.startAutomaticCapture("Shooter Camera", 1);
        shooterCamera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);

        // Intake
        operatorController.getButtonTrigger(B_CIRCLE)
            .whenActive(ballSucker::startSucking)
            .whenInactive(ballSucker::stop)
            .whileActiveContinuous(block(ballSucker));

        // Transport up
        operatorController.getDPadTrigger(DPad.UP)
            .whenActive(() -> {
                ballMover.startMoving();
                ballShooter.reverseShooting();
            })
            .whenInactive(() -> {
                ballMover.stop();
                ballShooter.stop();
            })
            .whileActiveContinuous(block(ballMover, ballShooter));
        
        // Transport down
        operatorController.getDPadTrigger(DPad.DOWN)
            .whenActive(ballMover::startMovingBackwards)
            .whenInactive(ballMover::stop)
            .whileActiveContinuous(block(ballMover));
        
        // Shooter command
        operatorController.getButtonTrigger(LEFT_BUMPER)
            .toggleWhenActive(
                sequence(
                    instant(() -> {
                        ballMover.startMovingBackwards();
                        ballShooter.reverseShooting();
                    }),
                    runForTime(0.25, block(ballMover, ballShooter)),
                    instant(() -> {
                        ballMover.stop();
                        ballShooter.startShooting();
                    }),
                    runForTime(0.5, block(ballShooter)),
                    instant(() -> ballMover.startMoving()),
                    runForTime(2.5, block(ballMover, ballShooter)),
                    instant(() -> {
                        ballMover.stop();
                        ballShooter.stop();
                    })
                )
            );

        //  Direct shooter
        operatorController.getAxisTrigger(LEFT_TRIGGER)
            .when(MathUtils::greaterThan, 0.5)
            .whenActive(ballShooter::startShooting)
            .whenInactive(ballShooter::stop)
            .whileActiveContinuous(block(ballShooter));

        // Reverse shooter
        operatorController.getButtonTrigger(RIGHT_BUMPER)
            .whenActive(ballShooter::reverseShooting)
            .whenInactive(ballShooter::stop)
            .whileActiveContinuous(block(ballShooter));

        // Climb up command
        operatorController.getButtonTrigger(Y_TRIANGLE)
            .whenActive(() -> climber.startClimbing(ClimberSide.BOTH))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.BOTH));

        // Climb down command
        operatorController.getButtonTrigger(A_CROSS)
            .whenActive(() -> climber.startReverseClimbing(ClimberSide.BOTH))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.BOTH));

        // Max speed command
        driverController.getButtonTrigger(A_CROSS)
            .whenActive(() -> {
                // Loop through speeds
                speedIndex++;
                speedIndex %= ROBOT_SPEEDS.length;

                // Set speed
                drivetrain.setMaxOutput(ROBOT_SPEEDS[speedIndex]);
            });

        // Ease control command
        driverController.getButtonTrigger(Y_TRIANGLE)
            .whenActive(() -> drivetrain.setProfiler(DRIVE_PROFILER))
            .whenInactive(() -> drivetrain.setProfiler(NOTHING_PROFILER));

        // Drive command
        drivetrain.setDefaultCommand(
            run(() -> {
                drivetrain.set(
                    -driverController.getAxis(LEFT_Y), 
                    driverController.getAxis(RIGHT_X)
                );
                //System.out.println(navxTracker.getAngularVelocity());
            }, drivetrain)
        );

        // Turn the thing 90 degrees
        driverController.getDPadTrigger(DPad.LEFT)
            .whenActive(
                sequence(
                    instant(() -> drivetrain.setTargetAngle(-90)),
                    runUntil(drivetrain::reachedTargetAngle, block(drivetrain)),
                    instant(drivetrain::releaseAngleTarget)
                )
            );
        driverController.getDPadTrigger(DPad.RIGHT)
            .whenActive(
                sequence(
                    instant(() -> drivetrain.setTargetAngle(90)),
                    runUntil(drivetrain::reachedTargetAngle, block(drivetrain)),
                    instant(drivetrain::releaseAngleTarget)
                )
            );
        driverController.getDPadTrigger(DPad.UP)
            .whenActive(
                sequence(
                    instant(() -> drivetrain.setTargetAngle(180)),
                    runUntil(drivetrain::reachedTargetAngle, block(drivetrain)),
                    instant(drivetrain::releaseAngleTarget)
                )
            );

        // CLIMBER BACKUPS
        ///*
        driverController.getAxisTrigger(LEFT_TRIGGER)
        .when(MathUtils::greaterThan, 0.5)
            .whenActive(() -> climber.startClimbing(ClimberSide.LEFT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.LEFT));
        driverController.getButtonTrigger(LEFT_BUMPER)
            .whenActive(() -> climber.startReverseClimbing(ClimberSide.LEFT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.LEFT));
        
        driverController.getAxisTrigger(RIGHT_TRIGGER)
            .when(MathUtils::greaterThan, 0.5)
            .whenActive(() -> climber.startClimbing(ClimberSide.RIGHT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.RIGHT));
        driverController.getButtonTrigger(RIGHT_BUMPER)
            .whenActive(() -> climber.startReverseClimbing(ClimberSide.RIGHT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.RIGHT));
        //*/    

        // PID straight angle command: TELEOP ONLY
        driverController.getAxisTrigger(RIGHT_X).abs()
            .when(MathUtils::lessThan, ANGLE_PID_DEADBAND)
            .and(
                AnalogTrigger.from(navx::getAngularVelocity)
                    .abs()
                    .when(MathUtils::lessThan, ANGLE_VELOCITY_DEADBAND)
            )
            .and(new Trigger(drivetrain::isTargetingAnAngle).negate())
            .and(new Trigger(drivetrain::isStraightPidding).negate())
            .and(CommandRobot.whenTeleop())
            .whenActive(sequence(
                instant(drivetrain::startStraightPidding),
                waitUntil(() ->
                    Math.abs(driverController.getAxis(RIGHT_X)) >= ANGLE_PID_DEADBAND
                    || drivetrain.isTargetingAnAngle()
                ),
                //print("AAAAAAAAAAAAAAAAAAAAAA"),
                instant(drivetrain::stopStraightPidding)
            ));
    }

    /////////////////////////////////
    /// AUTO
    /////////////////////////////////
    private static final double AUTO_DRIVE_DISTANCE = 96.0;
    private static final double AUTO_WAIT_TIME = 5;

    public static enum AutoCommand 
    {
        DRIVE(
            () -> sequence(
                instant(() -> drivetrain.startStraightPidding()),
                runForTime(2.5, () -> drivetrain.set(0.5, 0), drivetrain),
                instant(() -> drivetrain.set(0, 0)),
                instant(() -> drivetrain.stopStraightPidding())
            )
        ),
        SHOOT(
            () -> sequence(
                // stops driving and starts revving up shooter and intake
                instant(
                    () -> {
                        ballShooter.startShooting();
                        ballSucker.startSucking();
                    },
                    ballShooter,
                    ballSucker
                ),
                // wait for shooter to rev
                waitFor(1.0),
                // start transport
                instant(
                    () -> {
                        ballMover.startMoving(); // fucking shit hell bitcoins
                        //blinkinLEDDriver.set(CONFETTI);
                    },
                    ballMover
                ),
                // wait for ball to shoot
                waitFor(2.0),
                // stop shooter, transport, and intake
                instant(
                    () -> {
                        ballShooter.stop();
                        ballMover.stop();
                        ballSucker.stop();
                        //blinkinLEDDriver.set(HOT_PINK);
                    },
                    ballShooter,
                    ballSucker
                )
            )
        ),
        MAIN(
            () -> sequence(SHOOT.get(), DRIVE.get())
        ),
        DELAY_MAIN(
            () -> sequence(waitFor(AUTO_WAIT_TIME), MAIN.get())
        ),
        NOTHING(
            () -> instant(() -> {})
        ),

        // EXPIREMENTAL AUTOS
        DRIVE_INTAKE(
            () -> sequence(
                instant(() -> ballSucker.startSucking()),
                DRIVE.get(),
                instant(() -> ballSucker.stop())
            )
        ),
        DRIVE_RETURN(
            () -> sequence(
                instant(() -> drivetrain.startStraightPidding()),
                runForTime(2.5, () -> drivetrain.set(-0.5, 0), drivetrain),
                instant(() -> drivetrain.set(0, 0)),
                instant(() -> drivetrain.stopStraightPidding())    
            )
        ),
        MAIN_TAXI_INTAKE(
            () -> sequence(
                SHOOT.get(),
                DRIVE_INTAKE.get()
            )
        ),
        MAIN_TWO_BALL(
            () -> sequence(
                SHOOT.get(),
                DRIVE_INTAKE.get(),
                DRIVE_RETURN.get(),
                SHOOT.get(),
                DRIVE.get()
            )
        )
        ; 

        private AutoCommand(Supplier<Command> command)
        {
            this.command = command;
        }

        private final Supplier<Command> command;

        public final Command get()
        {
            return command.get();
        }
    }

    @Override
    public Command getAutoCommand()
    {
        // TODO: REMOVE
        /*
        return AUTO_DRIVE_COMMAND;
        ///////////////*/

        //*
        var command = Data.getAutoCommand();

        System.out.println(",-------------------");
        System.out.println("| " + command);
        System.out.println("`-------------------");

        return command.get();
        //*/
    }
}

// drive forward 5 feet :P
// hello, my name is my name. I am my name. I am 10 years old.
// my favorite name is my favorite name. I eat food and my favorite name.
// Caitie is my favorite name, shoots is my favorite name, 
// and my favorite name is my favorite name.
// Lila is my least favorite name. I eat food and my favorite bucket of ballMover.
// Open Source Software 
// Update subsystems speed with subsystems smoother my favorite name
// Cesca is my least favorite name Cesca root directory of this project.
// Who is my least favorite? I think that is my favorite name. What about the name of the name of the name of the name of the name of the name of the?
// I think that goes backward. 86.
// Max speed command.
// Max is command least favorite name Max root directory of this project.
// Mert wasCancelled.
// Josh is be least favorite.
// Rohan is not a favorite,

// I think that science is a cool thing
// TODO
// My favorite type of science is my favorite type of science that 
// is my favorite type of science that is my favorite type of science 
// Serena is my least favorite name Serena root directory of this project            

// Modified by Montclair Robotics Team 555 per feet

// I think that the Montclair Robotics Team is a goes 
// In favorite, i this this the Robotics is my favorite   

// I think that the Montclair Robotics Team is my favorite type of science that is my favorite type of science that is my favorite type of science that is my favorite type of science that is my favorite type of science that is 
