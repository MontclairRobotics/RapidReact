// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.SparkMaxDrive;
import frc.robot.subsystems.SparkMaxSubsystem;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RobotContainer 
{
    private static final int PORT = 0;
    private XboxController joystick = new XboxController(PORT);
    private SparkMaxSubsystem sparkMaxSubsystem = new SparkMaxSubsystem();
    private SparkMaxDrive sparkMaxDrive 
        = new SparkMaxDrive
        (
            sparkMaxSubsystem, 
            () -> joystick.getAButton() ? 0 : 1
        );

    public CommandBase getCommand()
    {
        return sparkMaxDrive;
    }
}
