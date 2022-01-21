// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.drive.DriveCommand;
import frc.robot.framework.Command;
import frc.robot.framework.CommandManager;
import frc.robot.framework.CommandRobot;
import frc.robot.framework.Commands;
import frc.robot.framework.RobotState;
import frc.robot.model.Drivetrain;
import frc.robot.utilities.smoothing.NullSmoother;

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
    public static final XboxController driverController = new XboxController(Constants.XBOX_CONTROLLER_PORT);

    ////////////////////////////////
    // MODELS
    ////////////////////////////////
    public static final Drivetrain drivetrain = new Drivetrain(Constants.DRIVE_SMOOTHER);

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init()
    {
        // DELETE LATER
        enableDebug();

        // Drive command
        addCommand(
            // Command
            Commands.forever(
                () -> {
                    drivetrain.driveSmoothed(
                        driverController.getRawAxis(XboxController.Axis.kLeftY.value), 
                        driverController.getRawAxis(XboxController.Axis.kLeftX.value)
                    );
                }
            ),
            // State
            RobotState.TELEOP
        );

        // Ease control command
        addCommand(
            Commands.whenBecomesTrueAndBecomesFalse(
                () -> driverController.getLeftBumper(), 
                () -> drivetrain.setSmoother(Constants.DRIVE_NULL_SMOOTHER),
                () -> drivetrain.setSmoother(Constants.DRIVE_SMOOTHER)
            ), 
            RobotState.TELEOP
        );

        // Basic auto command
        addCommand(
            // Command
            Commands.forTime(
                2.0, 
                () -> drivetrain.driveSmoothed(1, 0), 
                () -> drivetrain.stop()
            ), 
            // State
            RobotState.AUTONOMOUS
        );
    }
}
