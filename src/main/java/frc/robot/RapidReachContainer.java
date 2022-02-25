
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.PIDDistanceCommand;
import frc.robot.framework.CommandRobotContainer;
import frc.robot.framework.controllers.AnalogTrigger;
import frc.robot.framework.controllers.InputController;
import frc.robot.framework.controllers.InputController.DPad;
import frc.robot.rev.BlinkinLEDDriver;
import frc.robot.subsystems.AHRSTracker;
import frc.robot.subsystems.BallMover;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.BallSucker;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

import static frc.robot.Constants.*;
import static frc.robot.Data.*;

import static frc.robot.framework.controllers.InputController.Button.*;
import static frc.robot.rev.BlinkinLEDMode.*;

import edu.wpi.first.wpilibj2.command.*;
import static edu.wpi.first.wpilibj2.command.CommandBase.*;
import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;
import static frc.robot.framework.Commands.*;

import com.kauailabs.navx.frc.AHRS;

import static frc.robot.framework.controllers.InputController.Axis.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public final class RapidReachContainer extends CommandRobotContainer 
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
    public final AHRS navx = new AHRS();

    public final AHRSTracker navxTracker = new AHRSTracker(navx);
    public final Drivetrain drivetrain = new Drivetrain(DRIVE_SMOOTHER, navx);
    public final BallSucker ballSucker = new BallSucker();
    public final BallMover ballMover = new BallMover();
    public final BallShooter ballShooter = new BallShooter();
    public final Climber climber = new Climber();
    
    /*
    public final BlinkinLEDDriver blinkinLEDDriver = new BlinkinLEDDriver(BLINKIN_LED_DRIVER_PORT, C1_BREATH_SLOW, DISABLED);

    public final Ultrasonic ultrasonicLeft = new Ultrasonic(LEFT_ULTRASONIC_SENSOR_PING_PORT, LEFT_ULTRASONIC_SENSOR_ECHO_PORT);
    public final Ultrasonic ultrasonicRight = new Ultrasonic(RIGHT_ULTRASONIC_SENSOR_PING_PORT, RIGHT_ULTRASONIC_SENSOR_ECHO_PORT);
    */

    public int speedIndex = 0;

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init()
    {
        // Smart dashboard
        SmartDashboard.putString("Easing", "Drive");
        SmartDashboard.putNumber("Speed", ROBOT_SPEEDS[speedIndex]);

        // Calibrate the navx
        navx.calibrate();
        navxTracker.calibrate();
        navxTracker.setDefaultCommand(run(navxTracker::update, navxTracker));

        // Camera servers
        CameraServer.startAutomaticCapture("Intake Camera", 0);

        // PID
        drivetrain.setupPID();
        drivetrain.enableAllPID();

        // TEMP
        //drivetrain.disableAllPID();

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
                    runForTime(0.5, block(ballMover, ballShooter)),
                    instant(() -> {
                        ballMover.stop();
                        ballShooter.startShooting();
                    }),
                    runForTime(1.0, block(ballShooter)),
                    instant(() -> ballMover.startMoving()),
                    runForTime(2.5, block(ballMover, ballShooter)),
                    instant(() -> {
                        ballMover.stop();
                        ballShooter.stop();
                    })
                )
            );

        // Reverse shooter
        operatorController.getButtonTrigger(RIGHT_BUMPER)
            .whenActive(ballShooter::reverseShooting)
            .whenInactive(ballShooter::stop)
            .whileActiveContinuous(block(ballShooter));

        // Climb command
        operatorController.getButtonTrigger(X_SQUARE)
            .whenActive(sequence(
                instant(climber::startClimbing),
                runForTime(3.0, block(climber)),
                instant(climber::stop)
            ))
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
                SmartDashboard.putNumber("Speed", ROBOT_SPEEDS[speedIndex]);
            });

        // Ease control command
        driverController.getAxisTrigger(LEFT_TRIGGER)
            .whenGreaterThan(0.5)
            .whenActive(() -> {
                // Display on smart dashboard
                SmartDashboard.putString("Easing", "None");

                // Set smoother
                drivetrain.setSmoother(DRIVE_NULL_SMOOTHER);
            })
            .whenInactive(() -> {
                // Display on smart dashboard
                SmartDashboard.putString("Easing", "Drive");
                
                // Set smoother
                drivetrain.setSmoother(DRIVE_SMOOTHER);
            });

        // Drive command
        drivetrain.setDefaultCommand(
            run(() -> {
                drivetrain.drive(
                    -driverController.getAxis(LEFT_Y), 
                    -driverController.getAxis(RIGHT_X)
                );
            })
        );

        // PID straight angle command
        driverController.getAxisTrigger(RIGHT_X)
            .whenInRadius(0, ANGLE_PID_DEADBAND)
            .and(
                new AnalogTrigger(navxTracker::getAngularVelocity)
                .whenInRadius(0, ANGLE_VELOCITY_DEADBAND)
            )
            .whenActive(() -> drivetrain.setTargetAngle(0))
            .whenInactive(() -> drivetrain.releaseAngleTarget());
    }

    /////////////////////////////////
    /// AUTO
    /////////////////////////////////
    public static enum AutoCommand 
    {
        DRIVE_TEST,
        MAIN
    }

    @Override
    public Command getAutoCommand()
    {
        var command = Data.getAutoCommand();
        switch (command)
        {
            case DRIVE_TEST:
                return sequence(
                    instant(() -> drivetrain.disableAllPID()),
                    runForTime(
                        2,
                        () -> drivetrain.drive(0.5, 0),
                        drivetrain
                    ),
                    instant(() -> drivetrain.stop())
                );

            case MAIN:
                return sequence(
                    // drives backward for 2 seconds
                    runForTime(
                        2.0,
                        () -> drivetrain.drive(1, 0),
                        drivetrain
                    ),
                    // stops driving and starts revving up shooter
                    instant(
                        () -> {
                            drivetrain.stop();
                            ballShooter.stop();
                        },
                        drivetrain,
                        ballShooter
                    ),
                    // wait for shooter to rev
                    waitFor(2.0),
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
                    )//,
                    // Pid backwards
                    //new PIDDistanceCommand(drivetrain, 5.0)//,
                    // Reset to default state 
                    /*
                    Commands.once(
                        () -> blinkinLEDDriver.returnToDefault()
                    ) */  
                );
            
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
