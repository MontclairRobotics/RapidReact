package frc.robot.framework.bases;

import frc.robot.framework.Command;
import frc.robot.framework.CommandManager;

public abstract class OnceCommand<T extends CommandManager<T>> extends Command<T>
{
    @Override
    public final boolean finished() 
    {
        return true;
    }
}
