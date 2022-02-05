package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import frc.robot.framework.bases.OnceCommand;
import frc.robot.managers.RapidReachManager;

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