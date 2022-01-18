package frc.robot.framework.bases;

import frc.robot.framework.CommandManager;

public abstract class WhenCommand<T extends CommandManager<T>> extends ForeverCommand<T>
{
    private boolean previousEval;
    private boolean currentEval;

    @Override
    public final void execute() 
    {
        previousEval = currentEval;
        currentEval = predicate();

        if(currentEval)
        {
            onTrue();
        }
        else
        {
            onFalse();
        }

        if(currentEval && !previousEval)
        {
            onBecomeTrue();
        }
        
        if(!currentEval && previousEval)
        {
            onBecomeFalse();
        }
    }

    public abstract boolean predicate();

    public void onTrue() {}
    public void onFalse() {}

    public void onBecomeTrue() {}
    public void onBecomeFalse() {}
}
