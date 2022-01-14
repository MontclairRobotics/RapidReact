package frc.robot.framework;

public abstract class ForeverCommand extends Command 
{
    @Override
    public boolean finished() 
    {
        return false;
    }
}
