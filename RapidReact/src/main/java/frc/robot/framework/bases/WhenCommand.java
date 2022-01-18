package frc.robot.framework.bases;

public abstract class WhenCommand extends ForeverCommand
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
