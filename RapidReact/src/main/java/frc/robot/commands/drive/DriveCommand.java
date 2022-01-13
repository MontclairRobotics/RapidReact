package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DrivetainSubsystem;

public class DriveCommand extends CommandBase
{
    private DrivetainSubsystem subsystem;

    /** Supplies the speed to drive the drivetain subsystem */
    private DoubleSupplier speedSupplier;
    /** Supplies the turn speed to drive the drivetain subsystem */
    private DoubleSupplier turnSupplier;

    public DriveCommand(DrivetainSubsystem subsystem, DoubleSupplier speedSupplier, DoubleSupplier turnSupplier)
    {
        this.subsystem = subsystem;
        this.speedSupplier = speedSupplier; 
        this.turnSupplier = turnSupplier;

        addRequirements(subsystem);
    }

    @Override
    public void execute() 
    {
        subsystem.drive(speedSupplier.getAsDouble(), turnSupplier.getAsDouble());
    }

    @Override
    public boolean isFinished() 
    {
        return false;
    }
}
