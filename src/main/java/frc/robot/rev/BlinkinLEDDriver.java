package frc.robot.rev;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import frc.robot.framework.Status;

public class BlinkinLEDDriver 
{
    private Spark inner;
    private BlinkinLEDMode defaultMode;
    private final Status status;

    public BlinkinLEDDriver(int index, BlinkinLEDMode defaultMode, Status status)
    {
        this.status = status;

        if(status.isDisabled())
        {
            return;
        }

        inner = new Spark(index);
        inner.enableDeadbandElimination(false);

        this.defaultMode = defaultMode;

        set(defaultMode);
    }

    public void set(BlinkinLEDMode mode)
    {
        if(status.isDisabled()) return;
        inner.set(mode.getSparkValue());
    }
    public void returnToDefault()
    {        
        if(status.isDisabled()) return;
        set(defaultMode);
    }
}
