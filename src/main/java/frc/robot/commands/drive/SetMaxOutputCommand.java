package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import frc.robot.framework.bases.OnceCommand;
import frc.robot.model.Drivetrain;
import frc.robot.RapidReachManager;

public class SetMaxOutputCommand extends OnceCommand
{
    private DoubleSupplier maxSpeedSupplier; 

    private final Drivetrain drivetrain;

    public SetMaxOutputCommand(DoubleSupplier maxSpeedSupplier, Drivetrain drivetrain) 
    {   
        this.maxSpeedSupplier = maxSpeedSupplier;
        this.drivetrain = drivetrain;
    }

    @Override
    public void execute() 
    {
        drivetrain.setMaxOutput(maxSpeedSupplier.getAsDouble());
    }
}