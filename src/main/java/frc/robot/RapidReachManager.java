// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.Order;
import frc.robot.framework.RobotState;
import frc.robot.model.BallSucker;
import frc.robot.model.Drivetrain;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public final class RapidReachManager extends CommandManager
{
    ////////////////////////////////
    // CONTROLLERS
    ////////////////////////////////
    public static final XboxController driverController = new XboxController(Constants.DRIVER_CONTROLLER_PORT);
    public static final XboxController operatorController = new XboxController(Constants.OPERATOR_CONTROLLER_PORT);

    ////////////////////////////////
    // MODELS
    ////////////////////////////////
    public static final Drivetrain drivetrain = new Drivetrain(Constants.DRIVE_SMOOTHER);
    public static final BallSucker ballSucker = new BallSucker();

    public static int speedIndex = 0;

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
        SmartDashboard.putNumber("Speed", Constants.ROBOT_SPEEDS[speedIndex]);

        ///////////////////////////////////////////////////
        // TELEOP
        ///////////////////////////////////////////////////

        // Intake command
        addCommand(
            // Commands
            Commands.pollToggle(
                () -> operatorController.getBButton(),
                () -> ballSucker.setMotor(Constants.BALL_INTAKE_SPEED), 
                () -> ballSucker.setMotor(0)
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        // Max speed command
        addCommand(
            // Command
            Commands.pollActive(
                () -> driverController.getAButtonPressed(), 
                () -> 
                {
                    // Loop through speeds
                    speedIndex++;
                    speedIndex %= Constants.ROBOT_SPEEDS.length;

                    // Set speed
                    drivetrain.setMaxOutput(Constants.ROBOT_SPEEDS[speedIndex]);
                    
                    // Display on smart dashboard
                    SmartDashboard.putNumber("Speed", Constants.ROBOT_SPEEDS[speedIndex]);
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
                () -> driverController.getLeftTriggerAxis() > 0.5, 
                () -> 
                {
                    // Display on smart dashboard
                    SmartDashboard.putString("Easing", "None");

                    // Set smoother
                    drivetrain.setSmoother(Constants.DRIVE_NULL_SMOOTHER);
                },
                () -> 
                {
                    // Display on smart dashboard
                    SmartDashboard.putString("Easing", "Drive");
                    
                    // Set smoother
                    drivetrain.setSmoother(Constants.DRIVE_SMOOTHER);
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
                        driverController.getRawAxis(-XboxController.Axis.kLeftY.value), 
                        driverController.getRawAxis(XboxController.Axis.kRightX.value)
                    );
                }
            )
            .withOrder(Order.LATE_INPUT),
            // State
            RobotState.TELEOP
        );

        ///////////////////////////////////////////////////
        // AUTONOMOUS
        ///////////////////////////////////////////////////
        
        // Basic auto command
        addCommand(
            // Command
            Commands.series(
                Commands.forTime(
                    2.0, 
                    () -> {
                        drivetrain.setMaxOutput(Constants.AUTO_SPEED);
                        drivetrain.set(1, 0);
                    }, 
                    () -> drivetrain.stop()
                )
            )
            .withOrder(Order.EXECUTION),
            // State
            RobotState.AUTONOMOUS
        );

        ///////////////////////////////////////////////////
        // DEFAULT
        ///////////////////////////////////////////////////

        // Update subsystems
        addAlwaysCommand(
            cmd -> drivetrain.update(cmd.deltaTime()),
            Order.OUTPUT
        );
    }
}
