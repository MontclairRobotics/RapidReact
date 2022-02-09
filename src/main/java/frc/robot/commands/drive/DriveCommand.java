package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import frc.robot.framework.bases.ForeverCommand;
import frc.robot.model.Drivetrain;
import frc.robot.RapidReachManager;


public class DriveCommand extends ForeverCommand
{
    /** Supplies the speed to drive the drivetain subsystem */
    private DoubleSupplier speedSupplier;
    /** Supplies the turn speed to drive the drivetain subsystem */
    private DoubleSupplier turnSupplier;

    private final Drivetrain drivetrain;

    public DriveCommand(DoubleSupplier speedSupplier, DoubleSupplier turnSupplier, Drivetrain drivetrain)
    {
        this.speedSupplier = speedSupplier; 
        this.turnSupplier = turnSupplier;
        this.drivetrain = drivetrain;
    }

    @Override
    public void execute() 
    {
        drivetrain.set(speedSupplier.getAsDouble(), turnSupplier.getAsDouble());
    }
}
