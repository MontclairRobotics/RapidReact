package frc.robot.framework.bases;

import frc.robot.framework.Command;

public abstract class ForeverCommand extends Command 
{
    @Override
    public final boolean finished() 
    {
        return false;
    }
}
