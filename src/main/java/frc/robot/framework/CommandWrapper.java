package frc.robot.framework;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

@Deprecated
public abstract class CommandWrapper extends CommandBase
{
    private Command command;

    @Override
    public void initialize() 
    {
        command = command();
        CommandScheduler.getInstance().schedule(command);
    }

    @Override
    public boolean isFinished() 
    {
        return command.isFinished();
    }

    @Override
    public void end(boolean interrupted) 
    {
        command.end(interrupted);
    }

    public abstract Command command();
}
