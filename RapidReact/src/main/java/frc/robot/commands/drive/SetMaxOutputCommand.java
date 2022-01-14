package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import frc.robot.Robot;
import frc.robot.framework.Command;
import frc.robot.model.Drivetrain;

public class SetMaxOutputCommand extends Command
{
    private DoubleSupplier maxSpeedSupplier; 

    public SetMaxOutputCommand (DoubleSupplier maxSpeedSupplier) 
    {   
        this.maxSpeedSupplier = maxSpeedSupplier;
    }

    @Override
    public void execute() {
        Robot.drivetrain.setMaxOutput(maxSpeedSupplier.getAsDouble());
    }

    @Override
    public boolean finished() {
        return true;
    }
}