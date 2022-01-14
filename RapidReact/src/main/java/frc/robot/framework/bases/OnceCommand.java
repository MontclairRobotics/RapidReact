package frc.robot.framework.bases;

import frc.robot.framework.Command;

public abstract class OnceCommand extends Command
{
    @Override
    public final boolean finished() 
    {
        return true;
    }
}
