// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.PIDDistanceCommand;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.Order;
import frc.robot.framework.RobotState;
import frc.robot.framework.controllers.InputController;
import frc.robot.model.BallMover;
import frc.robot.model.BallShooter;
import frc.robot.model.BallSucker;
import frc.robot.model.Drivetrain;

import static frc.robot.Constants.*;
import static frc.robot.framework.controllers.InputController.Button.*;

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
    public AHRS navigator = new AHRS();
    public Drivetrain drivetrain = new Drivetrain(DRIVE_SMOOTHER, navigator, this);
    public BallSucker ballSucker = new BallSucker(this);
    public BallMover ballMover = new BallMover(this);
    public BallShooter ballShooter = new BallShooter(this);

    public static int speedIndex = 0;

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init()
    {
        System.out.println("this guy");

        // Debug
        enableDebug();

        // Smart dashboard
        SmartDashboard.putString("Easing", "Drive");
        SmartDashboard.putNumber("Speed", ROBOT_SPEEDS[speedIndex]);

        ///////////////////////////////////////////////////
        // TELEOP
        ///////////////////////////////////////////////////

        // Calibrate the navx
        addStartupCommand(
            () -> navigator.calibrate()
        );

        /*
        // Ball Suck command
        addCommand(
            // Commands
            Commands.pollToggle(
                () -> operatorController.getButton(B_CIRCLE),
                () -> ballSucker.startSucking(BALL_INTAKE_SPEED), 
                () -> ballSucker.stop(0.0)
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        // Transport command
        addCommand(
            // Commands
            Commands.pollToggle(
                () -> operatorController.getButton(Y_TRIANGLE),
                () -> ballMover.startMoving(BALL_TRANSPORT_SPEED), 
                () -> ballMover.stop(0.0)
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        //Shooter Command
        addCommand(
            // Shooter Button
            Commands.pollToggle(
                () -> operatorController.getButton(X_SQUARE),
                () -> ballShooter.startShooting(), 
                () -> ballShooter.stop()
            )
            .withOrder(Order.OUTPUT),
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
            RobotState.TESTING
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
                    drivetrain.setSmoother(DRIVE_NULL_SMOOTHER);
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
                        driverController.getAxis(RIGHT_X)
                    );
                }
            )
            .withOrder(Order.LATE_INPUT),
            // State
            RobotState.TELEOP
        );

        //PID straught angle command
        addCommand(
            // Command
            Commands.pollToggle (
                // If joystick angle is within the deadband, PID angle
                () -> Math.abs(driverController.getAxis(RIGHT_X)) <= AnglePID.DEADBAND,
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
            .withOrder(Order.OUTPUT),
            // State
            RobotState.AUTONOMOUS
        );
        */

        // Auto Command that goes backward, shoots, and goes forward :)
        addCommand(
            // Command
            Commands.series(
                // drives backward for 2 seconds
                Commands.forTime(
                    2.0,
                    () -> drivetrain.set(-1, 0)
                ),
                // stops driving and starts revving up shooter
                Commands.once(
                    () -> {
                        drivetrain.stop();
                        ballShooter.startShooting();
                    }
                ),
                // wait for shooter to rev
                Commands.wait(
                    2.0
                ),
                // start transport
                Commands.once(
                    () -> ballMover.startMoving() // fucking shit hell bitcoins
                ),
                // wait for ball to shoot
                Commands.wait(
                    2.0
                ),
                // stop shooter and stop transport
                Commands.once(
                    () -> {
                        ballShooter.stop();
                        ballMover.stop();
                    }
                ),
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
                new PIDDistanceCommand(drivetrain, 5.0)
            )
            //ballsucker.a      
            .withOrder(Order.EXECUTION),
            RobotState.AUTONOMOUS
        );

        ///////////////////////////////////////////////////
        // DEFAULT
        ///////////////////////////////////////////////////

        // Update subsystems
        addAlwaysCommand(
            cmd -> drivetrain.update(cmd.deltaTime()),
            Order.END
        );
    }
}
