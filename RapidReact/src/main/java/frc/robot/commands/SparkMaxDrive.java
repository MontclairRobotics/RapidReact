// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SparkMaxSubsystem;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class SparkMaxDrive extends CommandBase
{
	public SparkMaxSubsystem subsystem;
    public DoubleSupplier driveSupplier;
    
    public SparkMaxDrive(SparkMaxSubsystem subsystem, DoubleSupplier driveSupplier) 
    {
        addRequirements(subsystem);
        this.subsystem = subsystem;
        this.driveSupplier = driveSupplier;
    }

    @Override
    public void execute() 
    {
        subsystem.driveSpeed(driveSupplier.getAsDouble());
    }
}