package frc.robot.framework.bases;

import frc.robot.framework.Command;
import frc.robot.framework.CommandManager;

public abstract class TimedCommand extends Command
{
    private double targetTimeLength;
    private long startTime;

    public TimedCommand(double targetTimeLength)
    {
        this.targetTimeLength = targetTimeLength;
    }

    /** Returns the time in seconds elapsed since the command was started */
    public final double timeElapsed()
    {
        return timeElapsedMillis() / 1000.0;
    }
    /** Returns the time in milliseconds elapsed since the command was started */
    public final long timeElapsedMillis()
    {
        return System.currentTimeMillis() - startTime;
    }
    /** Returns percentage of the target time elapsed since the command was started */
    public final double percentElapsed()
    {
        return timeElapsed() / targetTimeLength;
    }

    @Override
    public void onInit() 
    {
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean finished() 
    {
        return percentElapsed() >= 1;
    }
}