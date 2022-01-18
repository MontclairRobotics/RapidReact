// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.drive.DriveCommand;
import frc.robot.framework.CommandManager;
import frc.robot.framework.RobotState;
import frc.robot.model.Drivetrain;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class RapidReachManager extends CommandManager<RapidReachManager>
{
    ////////////////////////////////
    // CONTROLLERS
    ////////////////////////////////
    public final XboxController driverController = new XboxController(Constants.XBOX_CONTROLLER_PORT);

    ////////////////////////////////
    // MODELS
    ////////////////////////////////
    public final Drivetrain drivetrain = new Drivetrain();

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init()
    {
        // DELETE LATER
        enableDebug(); 

        addCommand
        (
            new DriveCommand
            (
                () -> driverController.getRawAxis(XboxController.Axis.kLeftY.value),
                () -> driverController.getRawAxis(XboxController.Axis.kLeftX.value)
            ),
            RobotState.TELEOP
        );
    }
}
