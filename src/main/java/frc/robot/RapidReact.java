
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.RobotState;
import frc.robot.framework.frc.AutoCommands;
import frc.robot.framework.commandrobot.CommandRobot;
import frc.robot.framework.commandrobot.RobotContainer;
import frc.robot.framework.frc.commands.triggers.AnalogTrigger;
import frc.robot.framework.frc.controllers.GameController;
import frc.robot.framework.frc.controllers.GameController.DPad;
import frc.robot.framework.frc.vendors.rev.BlinkinLEDDriver;
import frc.robot.framework.math.MathUtils;
import frc.robot.managers.NavxManager;
import frc.robot.managers.VisionManager;
import frc.robot.subsystems.Climber.ClimberSide;
import frc.robot.subsystems.Auto;
import frc.robot.subsystems.BallMover;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.BallSucker;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.RotationalClimber;

import static frc.robot.Constants.*;
import static frc.robot.framework.frc.commands.Commands.*;
import static frc.robot.framework.frc.controllers.GameController.Axis.*;
import static frc.robot.framework.frc.controllers.GameController.Button.*;
import static frc.robot.framework.frc.vendors.rev.BlinkinLEDMode.*;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.*;
import static edu.wpi.first.wpilibj2.command.CommandBase.*;
import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;

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
    public static final GameController driverController = GameController.from(DRIVER_CONTROLLER_TYPE,
            DRIVER_CONTROLLER_PORT);
    public static final GameController operatorController = GameController.from(OPERATOR_CONTROLLER_TYPE,
            OPERATOR_CONTROLLER_PORT);

    ////////////////////////////////
    // SUBSYSTEMS / MANAGERS
    ////////////////////////////////
    public static final Drivetrain drivetrain = new Drivetrain();
    public static final BallSucker ballSucker = new BallSucker();
    public static final BallMover ballMover = new BallMover();
    public static final BallShooter ballShooter = new BallShooter();
    public static final Climber climber = new Climber();
    public static final Field2d field = new Field2d();
    public static Field2d getField() {return field;}
    public static final Auto auto = new Auto();
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
        //*
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
            .whenActive(() -> ballShooter.startShooting())
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
        
        // // Rotational climber forward command
        // operatorController.getDPad(DPad.LEFT)
        //     .whenActive(rotationalClimber::rotateForward)
        //     .whileActiveContinuous(block(rotationalClimber))
        //     .whenInactive(rotationalClimber::stop);
        // // Rotational climber backward command
        // operatorController.getDPad(DPad.RIGHT)
        //     .whenActive(rotationalClimber::rotateBackward)
        //     .whileActiveContinuous(block(rotationalClimber))
        //     .whenInactive(rotationalClimber::stop);

        // Max speed command
        driverController.getButton(A_CROSS)
            .whenActive(drivetrain::nextDriveSpeed);
                 
        // Ease control command
        driverController.getAxis(RIGHT_TRIGGER)
            .whenGreaterThan(0.5)
            .whenActive(() -> {
                drivetrain.setProfiler(NOTHING_PROFILER);
                drivetrain.killMomentum();
            })
            .whenInactive(() -> drivetrain.setProfiler(DRIVE_PROFILER));

        // Drive command
        drivetrain.setDefaultCommand(
            run(() -> 
            {
                if(!DriverStation.isTeleop())
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
        // driverController.getAxis(RIGHT_TRIGGER).whenGreaterThan(0.5)
        //     .whenActive(drivetrain::startReverseTurning)
        //     .whenInactive(drivetrain::stopReverseTurning);

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
        driverController.getAxis(LEFT_TRIGGER).whenGreaterThan(0.5)
            .whenActive(drivetrain::startTargetingABall)
            .whenInactive(drivetrain::stopTargetingABall); 
        
        // CLIMBER BACKUPS
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

        // PID straight angle command: TELEOP ONLY
        driverController.getAxis(RIGHT_X).abs()
            .whenLessThan(ANGLE_PID_DEADBAND)
            .and(
                AnalogTrigger.from(navx::getAngularVelocity).abs()
                    .whenLessThan(ANGLE_VELOCITY_DEADBAND)
            )
            .and(new Trigger(drivetrain::isTargetingAnAngle).negate())
            .and(new Trigger(drivetrain::isStraightPidding).negate())
            .and(new Trigger(DriverStation::isTeleop))
            .whenActive(sequence(
                instant(drivetrain::startStraightPidding),
                waitUntil(() ->
                    Math.abs(driverController.getAxisValue(RIGHT_X)) >= ANGLE_PID_DEADBAND
                    || drivetrain.isTargetingAnAngle()
                ),
                instant(drivetrain::stopStraightPidding)
            ));
        }
        //*/
        
        /////////////////////////////////
        /// AUTO
        /////////////////////////////////
        @Override
        public Command getAuto() {
            return auto.get();
        }

    //     AutoCommands.add(
    //         "Drive (2.5 sec)", 
    //         () -> RapidReactCommands.driveForTime(2.5, 1)
    //     );
    //     AutoCommands.add(
    //         "Drive (6 ft)",
    //         () -> RapidReactCommands.driveDistance(6 * 12)
    //     );
    //     AutoCommands.add(
    //         "Turn (left 90 degrees)",
    //         () -> RapidReactCommands.turn(-90)
    //     );
    //     AutoCommands.add(
    //         "Turn (180 degrees)",
    //         () -> RapidReactCommands.turn(180)
    //     );

    //     AutoCommands.add(
    //         "Shoot Test",
    //         () -> RapidReactCommands.shootSequence()
    //     );
    //     AutoCommands.add(
    //         "Shoot",
    //         () -> RapidReactCommands.shootSequenceShort()
    //     );

    //     final double ballDistance = 92; //in
    //     final double ballPidLeadIn = 50; //in
    //     final double ballStartSpeed = 0.5; 
    //     final double ballPidTime = 1.5; //sec
    //     final double ballPidOutput = 0.4; // (speed while ball pidding)
    //     final double returnTime = 2.0;
    //     final double ballTransportTime = 0.7; //sec
    //     final double taxiTime = 3; //sec
    //     final double preShootTime = 0.7; //sec

    //     AutoCommands.add(
    //         "Taxi",
    //         () -> RapidReactCommands.driveForTime(taxiTime, 1)
    //     );

    //     AutoCommands.add(
    //         "Single Ball",
    //         () -> sequence(
    //             // Shoot ball
    //             AutoCommands.get("Shoot"),
    //             AutoCommands.get("Taxi")
    //         )
    //     );

    //     AutoCommands.add(
    //         "Get 3 Balls",
    //         () -> parallel(
    //             sequence(
    //                 // Shoot ball
    //                 AutoCommands.get("Shoot"),

    //                 // Go to where it needs to go
    //                 RapidReactCommands.driveDistance(40),

    //                 // Angle pid
    //                 //RapidReactCommands.turn(() -> -navx.getAngle()), // undo the ball pidding that was done
    //                 RapidReactCommands.turn(145), // turn the degrees to ball

    //                 // Go to balls 
    //                 instant(navx::zeroYaw),
    //                 instant(ballSucker::startSucking),
    //                 instant(ballMover::startMoving),
    //                 instant(drivetrain::startTargetingABall),

    //                 // undo the ball pidding
    //                 RapidReactCommands.turn(() -> -navx.getAngle()),
    //                 // waitFor(ballTransportTime),
    //                 // instant(ballMover::stop),
    //                 // instant(ballSucker::stop)
    //                 RapidReactCommands.driveDistance(250),
    //                 instant(ballSucker::stop),
    //                 instant(ballMover::stop),
    //                 instant(drivetrain::stopTargetingABall)
    //             )
    //         )
    //     );

    //     AutoCommands.add(
    //         "Return (Shoot 3)",
    //         () -> parallel(
    //             sequence(
    //                 RapidReactCommands.driveDistance(-250),

    //                 RapidReactCommands.turn(-145),

    //                 RapidReactCommands.driveDistance(40),

    //                 AutoCommands.get("Shoot")
    //             ) //later
    //         )
    //     );

    //     AutoCommands.add(
    //         "Main 3 Ball",
    //         () -> sequence(
    //             AutoCommands.get("Get 3 Balls"),
    //             AutoCommands.get("Return (Shoot 3)")
    //         )
    //     );

    //     AutoCommands.add(
    //         "Main (late angle pid)",
    //         () -> sequence(
    //             // Shoot ball
    //             AutoCommands.get("Shoot"),

    //             // Retreive next ball
    //             instant(drivetrain::resetEncoders),
    //             instant(() -> drivetrain.set(ballStartSpeed, 0)),
    //             waitUntil(() -> drivetrain.getAverageDistance() >= ballDistance - ballPidLeadIn),

    //             instant(drivetrain::startTargetingABall),
    //             instant(ballSucker::startSucking),
    //             RapidReactCommands.driveForTime(ballPidTime, ballPidOutput),
    //             instant(drivetrain::stopTargetingABall),
    //             instant(ballSucker::stop),

    //             // Return 
    //             race(
    //                 parallel(
    //                     sequence(
    //                         instant(ballSucker::startSucking),
    //                         instant(ballMover::startMoving),
    //                         waitFor(ballTransportTime),
    //                         instant(ballMover::stop),
    //                         instant(ballSucker::stop)
    //                     ),
    //                     RapidReactCommands.driveForTime(returnTime, -0.75)
    //                 ),
    //                 RapidReactCommands.turn(() -> -navx.getAngle())
    //             ),
    //             waitFor(1),
    //             RapidReactCommands.driveForTime(preShootTime, 0.5),

    //             // Shoot ball (long this time)
    //             RapidReactCommands.shootSequence()
    //         )
    //     );

    //     AutoCommands.add(
    //         "Main",
    //         () -> sequence(
    //             // Shoot ball
    //             AutoCommands.get("Shoot"),

    //             // Retreive next ball
    //             instant(drivetrain::resetEncoders),
    //             instant(() -> drivetrain.set(ballStartSpeed, 0)),
    //             waitUntil(() -> drivetrain.getAverageDistance() >= ballDistance - ballPidLeadIn),

    //             instant(drivetrain::startTargetingABall),
    //             instant(ballSucker::startSucking),
    //             RapidReactCommands.driveForTime(ballPidTime, ballPidOutput),
    //             instant(drivetrain::stopTargetingABall),
    //             instant(ballSucker::stop),

    //             // Angle pid
    //             RapidReactCommands.turn(() -> -navx.getAngle()),

    //             // Return 
    //             parallel(
    //                 sequence(
    //                     instant(ballSucker::startSucking),
    //                     instant(ballMover::startMoving),
    //                     waitFor(ballTransportTime),
    //                     instant(ballMover::stop),
    //                     instant(ballSucker::stop)
    //                 ),
    //                 RapidReactCommands.driveForTime(returnTime, -0.75)
    //             ),
    //             waitFor(1),

    //             // Shoot ball (long this time)
    //             RapidReactCommands.shootSequence()
    //         )
    //     );
        
    //     AutoCommands.add(
    //         "Nothing",
    //         () -> instant(() -> {})
    //     );

    //     // EXPIREMENTAL AUTOS
    //     /*
    //     AutoCommands.add(
    //         "Drive Intake",
    //         () -> sequence(
    //             instant(() -> ballSucker.startSucking()),
    //             AutoCommands.get("Drive"),
    //             instant(() -> ballSucker.stop())
    //         )
    //     );
    //     AutoCommands.add(
    //         "Drive Return",
    //         () -> RapidReactCommands.driveForTime(2.5, -.5)
    //     );
    //     AutoCommands.add(
    //         "Main Taxi Intake",
    //         () -> sequence(
    //             AutoCommands.get("Shoot"),
    //             AutoCommands.get("Drive Intake")
    //         )
    //     );
    //     AutoCommands.add(
    //         "Main Two Ball",
    //         () -> sequence(
    //             AutoCommands.get("Shoot"),
    //             AutoCommands.get("Drive Intake"),
    //             AutoCommands.get("Drive Return"),
    //             AutoCommands.get("Shoot"),
    //             AutoCommands.get("Drive")
    //         )
    //     ); 
    //     */

    //     AutoCommands.setDefaultAutoCommand("Main");
        
    //     // Smart dashboard
    //     // Data.setup();
    // }
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
