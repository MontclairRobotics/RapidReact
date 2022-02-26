
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.PIDDistanceCommand;
import frc.robot.framework.Command;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.Order;
import frc.robot.framework.RobotState;
import frc.robot.framework.controllers.InputController;
import frc.robot.framework.controllers.InputController.DPad;
import frc.robot.model.BallMover;
import frc.robot.model.BallShooter;
import frc.robot.model.BallSucker;
import frc.robot.model.Climber;
import frc.robot.model.Drivetrain;
import frc.robot.rev.BlinkinLEDDriver;
import frc.robot.utilities.AHRSExtend;

import static frc.robot.Constants.*;
import static frc.robot.ShuffleboardConstants.*;

import static frc.robot.framework.controllers.InputController.Button.*;
import static frc.robot.rev.BlinkinLEDMode.*;
import static frc.robot.framework.Status.*;

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
public final class RapidReachManager extends CommandManager {
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
    public final AHRSExtend navigator = new AHRSExtend();
    public final Drivetrain drivetrain = new Drivetrain(DRIVE_SMOOTHER, navigator, this);
    public final BallSucker ballSucker = new BallSucker(this);
    public final BallMover ballMover = new BallMover(this);
    public final BallShooter ballShooter = new BallShooter(this);
    //public final Climber climber = new Climber(this);
    
    /*
    public final BlinkinLEDDriver blinkinLEDDriver = new BlinkinLEDDriver(BLINKIN_LED_DRIVER_PORT, C1_BREATH_SLOW, DISABLED);

    public final Ultrasonic ultrasonicLeft = new Ultrasonic(LEFT_ULTRASONIC_SENSOR_PING_PORT, LEFT_ULTRASONIC_SENSOR_ECHO_PORT);
    public final Ultrasonic ultrasonicRight = new Ultrasonic(RIGHT_ULTRASONIC_SENSOR_PING_PORT, RIGHT_ULTRASONIC_SENSOR_ECHO_PORT);
    */

    public int speedIndex = 0;

    public double prevAngle = 0;

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init()
    {
        // Debug
        enableDebug();

        // Smart dashboard
        SmartDashboard.putString("Easing", "Drive");
        SmartDashboard.putNumber("Speed", ROBOT_SPEEDS[speedIndex]);

        ///////////////////////////////////////////////////
        // TELEOP
        ///////////////////////////////////////////////////

        CameraServer.startAutomaticCapture("Intake Camera", 0);
        CameraServer.startAutomaticCapture("Shooter Camera", 1);
        
        // Calibrate the navx
        addStartupCommand(
            () -> navigator.calibrate()
        );

        // Setup the drivetrain
        addStartupCommand(
            () -> {
                // PID constants
                drivetrain.setupPID(
                    // DISTANCE
                    SmartDashboard.getNumber(DISTANCE_KP, 0.1), 
                    SmartDashboard.getNumber(DISTANCE_KI, 0),
                    SmartDashboard.getNumber(DISTANCE_KD, 0), 
                    SmartDashboard.getNumber(DISTANCE_TOLERANCE, 0.1), 
                    // ANGLE
                    SmartDashboard.getNumber(ANGLE_KP, 0.01), 
                    SmartDashboard.getNumber(ANGLE_KI, 0),
                    SmartDashboard.getNumber(ANGLE_KD, 0), 
                    SmartDashboard.getNumber(ANGLE_TOLERANCE, 1)
                );

                // PIDS
                drivetrain.enableAllPID();

                // TEMP
                //drivetrain.disableAllPID();
            }
        );
        
        // Ball Suck command
        addCommand(
            // Commands
            Commands.pollToggle(
                () -> operatorController.getButton(B_CIRCLE),
                () -> ballSucker.startSucking(), 
                () -> ballSucker.stop()
            )
            .withOrder(Order.OUTPUT),
            // State
            RobotState.TELEOP
        );

        /*
        addCommand(
            Commands.pollToggle(
                () -> driverController.getButton(B_CIRCLE),
                () -> debug("CIRCLE PRESSED!")
            )
            .withOrder(Order.INPUT),
            RobotState.TELEOP
        );
        addCommand(
            Commands.pollToggle(
                () -> driverController.getButton(Y_TRIANGLE),
                () -> debug("TRIANGLE PRESSED!")
            )
            .withOrder(Order.INPUT),
            RobotState.TELEOP
        );
        */

        // Transport commands
        addCommand(
            // Commands
            Commands.group(
                Commands.pollToggle(
                    () -> operatorController.getDPad(DPad.UP),
                    () -> {
                        ballMover.startMoving();
                        ballShooter.startMovingBackwards();
                    }, 
                    () -> {
                        ballMover.stop();
                        ballShooter.stop();
                    }
                ),
                Commands.pollToggle(
                    () -> operatorController.getDPad(DPad.DOWN),
                    () -> ballMover.startMovingBackwards(), 
                    () -> ballMover.stop()
                )
            )
            .withOrder(Order.OUTPUT),
            // State
            RobotState.TELEOP
        );

        //Shooter Command
        final var shooterCommand = 
            Commands.series(
                Commands.doWaitDo(
                    () -> {
                        ballMover.startMovingBackwards();
                        ballShooter.startMovingBackwards();
                    }, 
                    0.5, 
                    () -> ballMover.stop()
                ),
                Commands.doWait(() -> ballShooter.startShooting(), 1.0),
                Commands.doWaitDo(
                    () -> ballMover.startMoving(), 
                    2.5, 
                    () -> {
                        ballMover.stop();
                        ballShooter.stop();
                    }
                )
            )
            .withOrder(Order.OUTPUT);

        addCommand(
            Commands.pollToggle(
                () -> operatorController.getButton(LEFT_BUMPER), 
                cmd -> {
                    // if the shooter command is running:
                    if(shooterCommand.isRunning())
                    {
                        // stop 
                        cmd.getManager().stop(shooterCommand);
                    }
                    // else
                    else
                    {
                        // start it again
                        cmd.getManager().start(shooterCommand);
                    }
                }
            )
            .withOrder(Order.OUTPUT),
            RobotState.TELEOP
        );

        //Reverse Shooter
        addCommand(
            // Commands
            Commands.pollToggle(
                () -> operatorController.getButton(RIGHT_BUMPER),
                () -> ballShooter.startMovingBackwards(), 
                () -> ballShooter.stop()
            )
            .withOrder(Order.OUTPUT),
            // State
            RobotState.TELEOP
        );
        
        /*
        //Climb Command
        addCommand(
            // Command
            Commands.pollToggle(
                () -> driverController.getButtonPressed(X_SQUARE), 
                cmd -> 
                {
                    cmd.getManager().start(
                        Commands.doWaitDo(
                            () -> climber.startClimbing(),
                            3, 
                            () -> climber.stop()
                        )
                        .withOrder(Order.OUTPUT)
                    );
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );
        */

        // Max speed command
        addCommand(
            // Command
            Commands.pollActive(
                () -> driverController.getButtonPressed(A_CROSS), 
                () -> 
                {
                    // Loop through speeds
                    speedIndex++;
                    speedIndex %= ROBOT_SPEEDS.length;

                    // Set speed
                    drivetrain.setMaxOutput(ROBOT_SPEEDS[speedIndex]);
                    
                    // Display on smart dashboard
                    SmartDashboard.putNumber("Speed", ROBOT_SPEEDS[speedIndex]);
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        // Ease control command
        addCommand(
            // Command
            Commands.pollToggle(
                () -> driverController.getAxis(LEFT_TRIGGER) > 0.5, 
                () -> 
                {
                    // Display on smart dashboard
                    SmartDashboard.putString("Easing", "None");

                    // Set smoother
                    //drivetrain.setSmoother(DRIVE_NULL_SMOOTHER);
                },
                () -> 
                {
                    // Display on smart dashboard
                    SmartDashboard.putString("Easing", "Drive");
                    
                    // Set smoother
                    drivetrain.setSmoother(DRIVE_SMOOTHER);
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        // Drive command
        addCommand(
            // Command
            Commands.forever(
                () -> {
                    drivetrain.set(
                        -driverController.getAxis(LEFT_Y), 
                        -driverController.getAxis(RIGHT_X)
                    );
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        //PID straught angle command
        addCommand(
            // Command
            Commands.pollToggle (
                // If joystick angle is within the deadband, PID angle
                () -> Math.abs(driverController.getAxis(RIGHT_X)) <= ANGLE_PID_DEADBAND
                   && Math.abs(navigator.getAngularVelocity()) <= ANGLE_VELOCITY_DEADBAND,
                () -> 
                {
                    // PID the angle to 0
                    drivetrain.setTargetAngle(0.0);
                },
                () -> 
                {
                    // If not in deadband, stop PIDing angle
                    drivetrain.releaseAngleTarget();
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        ///////////////////////////////////////////////////
        // AUTONOMOUS
        ///////////////////////////////////////////////////
        
        // Basic auto command 
        /*
        addCommand(
            // Command
            Commands.series(
                Commands.forTime(
                    2.0, 
                    () -> 
                    {
                        drivetrain.setMaxOutput(AUTO_SPEED);
                        drivetrain.set(1, 0);
                    }, 
                    () -> drivetrain.stop()
                )
            )
            .withOrder(Order.END),
            // State
            RobotState.AUTONOMOUS
        );
        */
        
        
        // Auto command with PID
        /*
        addCommand(
            // Command
            Commands.series(
                Commands.once(() -> {
                    drivetrain.setTargetDistance(4);
                    drivetrain.setTargetAngle(0);
                }),
                Commands.untilSome(
                    () -> drivetrain.reachedTargetDistance(),
                    () -> {
                        
                    }
                ),
                Commands.once(() -> {
                    drivetrain.releaseDistanceTarget();
                    drivetrain.releaseAngleTarget();
                })
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.AUTONOMOUS
        );
        */

        // Auto Command that goes backward, shoots, and goes forward :)
        addCommand(
            // Command
            Commands.select(
                this::getAutoCommand
            )
            .withOrder(Order.INPUT),
            RobotState.AUTONOMOUS
        );

        ///////////////////////////////////////////////////
        // DEFAULT
        ///////////////////////////////////////////////////

        // Reset the leds
        /*
        addDefaultCommand(
            Commands.once(() -> blinkinLEDDriver.returnToDefault())
        );
        */

        // Update subsystems
        addAlwaysCommand(
            cmd -> {
                drivetrain.update(cmd.deltaTime());
            },
            Order.OUTPUT
        );
        addAlwaysCommand(
            cmd -> {
                navigator.update(cmd.deltaTime());
                SmartDashboard.putNumber(ANGULAR_VELOCITY, navigator.getAngularVelocity());
            },
            Order.OUTPUT
        );
    }

    public Command getAutoCommand()
    {
        var command = SmartDashboard.getString(AUTO_COMMAND, AUTO_MAIN);
        switch (command)
        {
            case AUTO_DRIVE_TEST:
                return Commands.series(
                    Commands.once(() -> drivetrain.disableAllPID()),
                    Commands.once(() -> drivetrain.set(0.5, 0)),
                    Commands.waitForTime(2),
                    Commands.once(() -> drivetrain.stop())
                );

            case AUTO_MAIN:
                return Commands.series(
                    // stops driving and starts revving up shooter
                    Commands.once(
                        () -> {
                            ballShooter.startShooting();
                            //blinkinLEDDriver.set(BPM_LAVA);
                        }
                    ),
                    // wait for shooter to rev
                    Commands.waitForTime(
                        0.555
                    ),
                    // start transport
                    Commands.once(
                        () -> {
                            ballMover.startMoving(); // fucking shit hell bitcoins
                            //blinkinLEDDriver.set(CONFETTI);
                        }
                    ),
                    // wait for ball to shoot
                    Commands.waitForTime(
                        2.0
                    ),
                    // stop shooter and stop transport
                    Commands.once(
                        () -> {
                            ballShooter.stop();
                            ballMover.stop();
                            //blinkinLEDDriver.set(HOT_PINK);
                        }
                    ),
                    Commands.doWaitDo(
                        () -> drivetrain.set(.5, 0),
                        0.1,
                        () -> drivetrain.stop()
                    ),
                    Commands.debug("hey i'm done stupid")

                    // Pid backwards
                   // new PIDDistanceCommand(drivetrain, 5.0)//,
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
