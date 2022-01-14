package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import frc.robot.Robot;
import frc.robot.framework.bases.ForeverCommand;


public class DriveCommand extends ForeverCommand
{
    /** Supplies the speed to drive the drivetain subsystem */
    private DoubleSupplier speedSupplier;
    /** Supplies the turn speed to drive the drivetain subsystem */
    private DoubleSupplier turnSupplier;

    public DriveCommand(DoubleSupplier speedSupplier, DoubleSupplier turnSupplier)
    {
        this.speedSupplier = speedSupplier; 
        this.turnSupplier = turnSupplier;
    }

    @Override
    public void execute() 
    {
        Robot.drivetrain.drive(speedSupplier.getAsDouble(), turnSupplier.getAsDouble());
    }
}
