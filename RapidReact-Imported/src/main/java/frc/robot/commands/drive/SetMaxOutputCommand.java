package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import frc.robot.RapidReachManager;
import frc.robot.framework.bases.OnceCommand;

public class SetMaxOutputCommand extends OnceCommand
{
    private DoubleSupplier maxSpeedSupplier; 

    public SetMaxOutputCommand(DoubleSupplier maxSpeedSupplier) 
    {   
        this.maxSpeedSupplier = maxSpeedSupplier;
    }

    @Override
    public void execute() 
    {
        RapidReachManager.drivetrain.setMaxOutput(maxSpeedSupplier.getAsDouble());
    }
}