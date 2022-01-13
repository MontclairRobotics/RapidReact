// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.drive.DriveCommand;
import frc.robot.subsystems.DrivetainSubsystem;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer 
{
    private final XboxController driverController = new XboxController(Constants.XBOX_CONTROLLER_PORT);

    // The robot's subsystems and commands are defined here...
    private final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
    private final DrivetainSubsystem drivetrainSubsystem = new DrivetainSubsystem();

    private final ExampleCommand autoCommand = new ExampleCommand(exampleSubsystem);
    private final DriveCommand driveCommand = new DriveCommand(
        drivetrainSubsystem,
        // Speed supplier
        () -> driverController.getRawAxis(XboxController.Axis.kLeftY.value),
        // Turn speed supplier
        () -> driverController.getRawAxis(XboxController.Axis.kLeftX.value)
    );

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() 
    {

    }

    /** Get all of the commands which should run forever */
    public Command[] getForeverCommands()
    {
        return new Command[] 
        {
            driveCommand
        };
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() 
    {
        // An ExampleCommand will run in autonomous
        return autoCommand;
    }
}
