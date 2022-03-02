
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.PIDDistanceCommand;
import frc.robot.framework.CommandRobot;
import frc.robot.framework.CommandRobotContainer;
import frc.robot.framework.maths.MathUtils;
import frc.robot.framework.vendors.rev.BlinkinLEDDriver;
import frc.robot.framework.wpilib.controllers.InputController;
import frc.robot.framework.wpilib.controllers.InputController.DPad;
import frc.robot.framework.wpilib.triggers.AnalogTrigger;
import frc.robot.subsystems.TrackedNavx;
import frc.robot.subsystems.BallMover;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.BallSucker;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

import static frc.robot.Constants.*;
import static frc.robot.framework.vendors.rev.BlinkinLEDMode.*;
import static frc.robot.framework.wpilib.controllers.InputController.Axis.*;
import static frc.robot.framework.wpilib.controllers.InputController.Button.*;

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
    public static final Drivetrain drivetrain = new Drivetrain(DRIVE_SMOOTHER, navx);
    public static final BallSucker ballSucker = new BallSucker();
    public static final BallMover ballMover = new BallMover();
    public static final BallShooter ballShooter = new BallShooter();
    public static final Climber climber = new Climber();
    
    /*
    public final BlinkinLEDDriver blinkinLEDDriver = new BlinkinLEDDriver(BLINKIN_LED_DRIVER_PORT, C1_BREATH_SLOW, DISABLED);

    public final Ultrasonic ultrasonicLeft = new Ultrasonic(LEFT_ULTRASONIC_SENSOR_PING_PORT, LEFT_ULTRASONIC_SENSOR_ECHO_PORT);
    public final Ultrasonic ultrasonicRight = new Ultrasonic(RIGHT_ULTRASONIC_SENSOR_PING_PORT, RIGHT_ULTRASONIC_SENSOR_ECHO_PORT);
    */

    public static int speedIndex = 0;

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init() 
    {
        // Calibrate the navx
        navx.init();
        navx.calibrate();

        // PID setup
        drivetrain.setupPID();
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
        CameraServer.startAutomaticCapture("Intake Camera", 0);
        CameraServer.startAutomaticCapture("Shooter Camera", 1);

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
                    runForTime(0.3, block(ballMover, ballShooter)),
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

        // Climb command
        operatorController.getButtonTrigger(X_SQUARE)
            .whenActive(climber::startClimbing)
            .whenInactive(climber::stop)
            .whileActiveContinuous(block(climber));

        // Max speed command
        driverController.getButtonTrigger(A_CROSS)
            .whenActive(() -> {
                // Loop through speeds
                speedIndex++;
                speedIndex %= ROBOT_SPEEDS.length;

                // Set speed
                drivetrain.setMaxOutput(ROBOT_SPEEDS[speedIndex]);
                
                // Display on smart dashboard
                Data.setCurrentMaxSpeed(ROBOT_SPEEDS[speedIndex]);
            });

        // Ease control command
        driverController.getAxisTrigger(LEFT_TRIGGER)
            .when(MathUtils::greaterThan, 0.5)
            .whenActive(() -> {
                // Display on smart dashboard
                Data.setCurrentEasing("None");

                // Set smoother
                drivetrain.setSmoother(DRIVE_NULL_SMOOTHER);
            })
            .whenInactive(() -> {
                // Display on smart dashboard
                Data.setCurrentEasing("Drive");
                
                // Set smoother
                drivetrain.setSmoother(DRIVE_SMOOTHER);
            });

        // Drive command
        drivetrain.setDefaultCommand(
            run(() -> {
                drivetrain.set(
                    -driverController.getAxis(LEFT_Y), 
                    -driverController.getAxis(RIGHT_X)
                );
                //System.out.println(navxTracker.getAngularVelocity());
            }, drivetrain)
        );

        // Turn the thing 90 degrees
        driverController.getDPadTrigger(DPad.LEFT)
            .whenActive(
                sequence(
                    instant(() -> drivetrain.setTargetAngle(90)),
                    print("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
                    runUntil(drivetrain::reachedTargetAngle, block(drivetrain)),
                    print("AAAAAAAAA the second one"),
                    instant(drivetrain::releaseAngleTarget)
                )
            );
        driverController.getDPadTrigger(DPad.RIGHT)
            .whenActive(
                sequence(
                    instant(() -> drivetrain.setTargetAngle(-90)),
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

        // PID straight angle command: TELEOP ONLY
        driverController.getAxisTrigger(RIGHT_X).abs()
            .when(MathUtils::lessThan, ANGLE_PID_DEADBAND)
            .and(
                AnalogTrigger.from(navx::getAngularVelocity)
                    .abs()
                    .when(MathUtils::lessThan, ANGLE_VELOCITY_DEADBAND)
            )
            .and(CommandRobot.whenTeleop())
            .whenActive(() -> drivetrain.setTargetAngle(0))
            .whenInactive(() -> drivetrain.releaseAngleTarget());
    }

    /////////////////////////////////
    /// AUTO
    /////////////////////////////////
    public static enum AutoCommand 
    {
        MAIN, // done!
        DELAY_MAIN, //done!
        NOTHING, // done!
        SHOOT_NO_DRIVE, // done!
        JUST_DRIVE, // done!
        MAIN_BUT_WITHOUT_PID // done!
    }

    private static final double AUTO_DRIVE_DISTANCE = 96.0;
    private static final double AUTO_WAIT_TIME = 5;
    private static final Command AUTO_SHOOT 
        = sequence(
            // stops driving and starts revving up shooter
            instant(
                () -> {
                    ballShooter.startShooting();
                },
                ballShooter
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
            // stop shooter and stop transport
            instant(
                () -> {
                    ballShooter.stop();
                    ballMover.stop();
                    //blinkinLEDDriver.set(HOT_PINK);
                },
                ballShooter
            )
        );
    private static final Command AUTO_MAIN = 
        sequence(
            AUTO_SHOOT,
            // Pid backwards
            PIDDistanceCommand.get(drivetrain, -AUTO_DRIVE_DISTANCE)
        );
    private static final Command AUTO_DELAY_COMMAND =
        sequence(
            waitFor(AUTO_WAIT_TIME),
            AUTO_MAIN
        );

    private static final Command AUTO_DRIVE_BACKWARD =
        PIDDistanceCommand.get(drivetrain, -AUTO_DRIVE_DISTANCE);

    private static final Command NOTHING_COMMAND =
        instant(() -> {});

    @Override
    public Command getAutoCommand()
    {
        var command = Data.getAutoCommand();
        switch (command)
        {
            case JUST_DRIVE:
                return AUTO_DRIVE_BACKWARD;

            case MAIN:
                return sequence(
                    AUTO_SHOOT,
                    // Pid backwards
                    PIDDistanceCommand.get(drivetrain, -AUTO_DRIVE_DISTANCE)
                );

            case MAIN_BUT_WITHOUT_PID:
                return sequence(
                    AUTO_SHOOT,
                    runForTime(
                        2,
                        () -> drivetrain.set(0.5, 0),
                        drivetrain
                    )
                );
            case DELAY_MAIN:
                return AUTO_DELAY_COMMAND;
            case NOTHING:
                return NOTHING_COMMAND;
            
            default:
                throw new RuntimeException("Invalid auto command: " + command);
        }
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
