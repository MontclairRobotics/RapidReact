package frc.robot.framework.bases;

import frc.robot.framework.Command;

public abstract class TimedCommand extends Command
{
    private double time;
    private long startTime;

    public TimedCommand(double time)
    {
        this.time = time;
    }

    @Override
    public void onInit() 
    {
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean finished() 
    {
        return (System.currentTimeMillis() - startTime) >= time / 1000;
    }
}