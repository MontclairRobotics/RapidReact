package frc.robot.framework;

public abstract class OnceCommand extends Command
{
    @Override
    public boolean finished() 
    {
        return true;
    }
}
