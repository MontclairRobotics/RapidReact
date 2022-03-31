
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.CommandRobot;
import frc.robot.framework.RobotContainer;
import frc.robot.framework.RobotState;
import frc.robot.framework.maths.MathUtils;
import frc.robot.framework.vendors.rev.BlinkinLEDDriver;
import frc.robot.framework.wpilib.AutoCommands;
import frc.robot.framework.wpilib.controllers.InputController;
import frc.robot.framework.wpilib.controllers.InputController.DPad;
import frc.robot.framework.wpilib.triggers.AnalogValue;
import frc.robot.managers.NavxManager;
import frc.robot.managers.VisionManager;
import frc.robot.subsystems.Climber.ClimberSide;
import frc.robot.subsystems.BallMover;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.BallSucker;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.RotationalClimber;

import static frc.robot.Constants.*;
import static frc.robot.framework.vendors.rev.BlinkinLEDMode.*;
import static frc.robot.framework.wpilib.controllers.InputController.Axis.*;
import static frc.robot.framework.wpilib.controllers.InputController.Button.*;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.*;
import static edu.wpi.first.wpilibj2.command.CommandBase.*;
import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;
import static frc.robot.framework.Commands.*;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
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
public final class RapidReact extends RobotContainer 
{
    ////////////////////////////////
    // CONTROLLERS
    ////////////////////////////////
    public static final InputController driverController = InputController.from(DRIVER_CONTROLLER_TYPE,
            DRIVER_CONTROLLER_PORT);
    public static final InputController operatorController = InputController.from(OPERATOR_CONTROLLER_TYPE,
            OPERATOR_CONTROLLER_PORT);

    ////////////////////////////////
    // SUBSYSTEMS / MANAGERS
    ////////////////////////////////
    public static final Drivetrain drivetrain = new Drivetrain();
    public static final BallSucker ballSucker = new BallSucker();
    public static final BallMover ballMover = new BallMover();
    public static final BallShooter ballShooter = new BallShooter();
    public static final Climber climber = new Climber();
    //public static final RotationalClimber rotationalClimber = new RotationalClimber();

    public static final VisionManager vision = new VisionManager();
    public static final NavxManager navx = new NavxManager(new AHRS());
    
    /*
    public final BlinkinLEDDriver blinkinLEDDriver = new BlinkinLEDDriver(BLINKIN_LED_DRIVER_PORT, C1_BREATH_SLOW, DISABLED);

    public final Ultrasonic ultrasonicLeft = new Ultrasonic(LEFT_ULTRASONIC_SENSOR_PING_PORT, LEFT_ULTRASONIC_SENSOR_ECHO_PORT);
    public final Ultrasonic ultrasonicRight = new Ultrasonic(RIGHT_ULTRASONIC_SENSOR_PING_PORT, RIGHT_ULTRASONIC_SENSOR_ECHO_PORT);
    //*/

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void initialize()
    {
        // Smart dashboard
        Data.setup();
        
        // Intake
        operatorController.getButton(X_SQUARE)
            .whenActive(ballSucker::startSucking)
            .whenInactive(ballSucker::stop)
            .whileActiveContinuous(block(ballSucker));
        // Reverse Intake
        operatorController.getButton(B_CIRCLE)
            .whenActive(ballSucker::startSucking)
            .whenInactive(ballSucker::stop)
            .whileActiveContinuous(block(ballSucker));

        // Transport up
        operatorController.getDPad(DPad.UP)
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
        operatorController.getDPad(DPad.DOWN)
            .whenActive(ballMover::startMovingBackwards)
            .whenInactive(ballMover::stop)
            .whileActiveContinuous(block(ballMover));
        
        // Shooter command
        operatorController.getButton(LEFT_BUMPER)
            .toggleWhenActive(RapidReactCommands.shootSequence());

        // Direct shooter
        operatorController.getAxis(LEFT_TRIGGER)
            .whenGreaterThan(0.5)
            .whenActive(ballShooter::startShooting)
            .whenInactive(ballShooter::stop)
            .whileActiveContinuous(block(ballShooter));

        // Reverse shooter
        operatorController.getButton(RIGHT_BUMPER)
            .whenActive(ballShooter::reverseShooting)
            .whenInactive(ballShooter::stop)
            .whileActiveContinuous(block(ballShooter));

        // Climb up command
        operatorController.getButton(Y_TRIANGLE)
            .whenActive(() -> climber.startClimbing(ClimberSide.BOTH))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.BOTH));
        // Climb down command
        operatorController.getButton(A_CROSS)
            .whenActive(() -> climber.startReverseClimbing(ClimberSide.BOTH))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.BOTH));
        /*
        // Rotational climber forward command
        operatorController.getDPad(DPad.LEFT)
            .whenActive(rotationalClimber::rotateForward)
            .whileActiveContinuous(block(rotationalClimber))
            .whenInactive(rotationalClimber::stop);
        // Rotational climber backward command
        operatorController.getDPad(DPad.RIGHT)
            .whenActive(rotationalClimber::rotateBackward)
            .whileActiveContinuous(block(rotationalClimber))
            .whenInactive(rotationalClimber::stop);
        */

        // Max speed command
        driverController.getButton(A_CROSS)
            .whenActive(drivetrain::nextDriveSpeed);

        // Ease control command
        driverController.getButton(Y_TRIANGLE)
            .whenActive(() -> drivetrain.setProfiler(NOTHING_PROFILER))
            .whenInactive(() -> drivetrain.setProfiler(DRIVE_PROFILER));

        // Drive command
        drivetrain.setDefaultCommand(
            run(() -> 
            {
                if(!CommandRobot.isOperated())
                {
                    return;
                }

                drivetrain.set(
                    -driverController.getAxisValue(LEFT_Y), 
                    driverController.getAxisValue(RIGHT_X)
                );
                //System.out.println(navxTracker.getAngularVelocity());
            }, drivetrain)
        );

        // Turn reverse
        driverController.getAxis(RIGHT_TRIGGER).whenGreaterThan(0.5)
            .whenActive(drivetrain::startReverseTurning)
            .whenInactive(drivetrain::stopReverseTurning);

        // Turn commands
        driverController.getDPad(DPad.RIGHT)
            .toggleWhenActive(RapidReactCommands.turn(90));
        driverController.getDPad(DPad.LEFT)
            .toggleWhenActive(RapidReactCommands.turn(-90));
        driverController.getDPad(DPad.UP)
            .toggleWhenActive(RapidReactCommands.turn(-180));
        driverController.getDPad(DPad.DOWN)
            .toggleWhenActive(RapidReactCommands.turn(180));

        // turn to ball
        driverController.getButton(B_CIRCLE)
            .whenActive(drivetrain::startTargetingABall)
            .whenInactive(drivetrain::stopTargetingABall); 
        
        // CLIMBER BACKUPS
        ///*
        operatorController.getAxis(LEFT_Y).whenGreaterThan(0.5)
            .whenActive(() -> climber.startClimbing(ClimberSide.LEFT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.LEFT));
        operatorController.getAxis(LEFT_Y).whenLessThan(-0.5)
            .whenActive(() -> climber.startReverseClimbing(ClimberSide.LEFT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.LEFT));
        
        operatorController.getAxis(RIGHT_Y).whenGreaterThan(0.5)
            .whenActive(() -> climber.startClimbing(ClimberSide.RIGHT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.RIGHT));
        operatorController.getAxis(RIGHT_Y).whenLessThan(-0.5)
            .whenActive(() -> climber.startReverseClimbing(ClimberSide.RIGHT))
            .whileActiveContinuous(block(climber))
            .whenInactive(() -> climber.stop(ClimberSide.RIGHT));
        //*/    

        // PID straight angle command: TELEOP ONLY
        driverController.getAxis(RIGHT_X).abs()
            .whenLessThan(ANGLE_PID_DEADBAND)
            .and(
                AnalogValue.from(navx::getAngularVelocity).abs()
                    .whenLessThan(ANGLE_VELOCITY_DEADBAND)
            )
            .and(new Trigger(drivetrain::isTargetingAnAngle).negate())
            .and(new Trigger(drivetrain::isStraightPidding).negate())
            .and(CommandRobot.whenTeleop())
            .whenActive(sequence(
                instant(drivetrain::startStraightPidding),
                waitUntil(() ->
                    Math.abs(driverController.getAxisValue(RIGHT_X)) >= ANGLE_PID_DEADBAND
                    || drivetrain.isTargetingAnAngle()
                ),
                instant(drivetrain::stopStraightPidding)
            ));
        
        /////////////////////////////////
        /// AUTO
        /////////////////////////////////
        AutoCommands.setAutoCommandInitializer((name, s) -> {
            Data.mainTab().add(name, s);
        });

        AutoCommands.add(
            "Drive", 
            () -> RapidReactCommands.driveForTime(2.5, 1)
        );
        AutoCommands.add(
            "Drive (6 ft)",
            () -> RapidReactCommands.driveDistance(6 * 12)
        );
        AutoCommands.add(
            "Turn (left 90 degrees)",
            () -> RapidReactCommands.turn(-90)
        );
        AutoCommands.add(
            "Turn (180 degrees)",
            () -> RapidReactCommands.turn(180)
        );

        AutoCommands.add(
            "Shoot",
            () -> RapidReactCommands.shootSequence()
        );

        final double ballDistance = 92; //in
        final double ballPidLeadIn = 20; //in
        final double ballPidTime = 2; //sec
        final double ballPidOutput = 0.1;
        final double ballTransportTime = 0.5; //sec
        final double returnTime = 2;
        final double taxiTime = 5; //sec

        AutoCommands.add(
            "Main",
            () -> sequence(
                // Shoot ball
                RapidReactCommands.shootSequenceShort(),

                // Retreive next ball
                RapidReactCommands.driveDistance(ballDistance - ballPidLeadIn),
                instant(drivetrain::startTargetingABall),
                instant(ballSucker::startSucking),
                RapidReactCommands.driveForTime(ballPidTime, ballPidOutput),
                instant(drivetrain::stopTargetingABall),

                // Return 
                parallel(
                    sequence(
                        instant(ballMover::startMoving),
                        waitFor(ballTransportTime),
                        instant(ballMover::stop),
                        instant(ballSucker::stop)
                    ),
                    RapidReactCommands.driveForTime(returnTime, -1)
                ),

                // Shoot ball (long this time)
                RapidReactCommands.shootSequence(),

                // Taxi
                RapidReactCommands.driveForTime(taxiTime, 1)
            )
        );
        AutoCommands.add(
            "Delay Main",
            () -> sequence(waitFor(AUTO_WAIT_TIME), AutoCommands.get("Main"))
        );
        AutoCommands.add(
            "Nothing",
            () -> instant(() -> {})
        );

        // EXPIREMENTAL AUTOS
        /*
        AutoCommands.add(
            "Drive Intake",
            () -> sequence(
                instant(() -> ballSucker.startSucking()),
                AutoCommands.get("Drive"),
                instant(() -> ballSucker.stop())
            )
        );
        AutoCommands.add(
            "Drive Return",
            () -> RapidReactCommands.driveForTime(2.5, -.5)
        );
        AutoCommands.add(
            "Main Taxi Intake",
            () -> sequence(
                AutoCommands.get("Shoot"),
                AutoCommands.get("Drive Intake")
            )
        );
        AutoCommands.add(
            "Main Two Ball",
            () -> sequence(
                AutoCommands.get("Shoot"),
                AutoCommands.get("Drive Intake"),
                AutoCommands.get("Drive Return"),
                AutoCommands.get("Shoot"),
                AutoCommands.get("Drive")
            )
        ); 
        */

        AutoCommands.setDefaultAutoCommand("Main");
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
