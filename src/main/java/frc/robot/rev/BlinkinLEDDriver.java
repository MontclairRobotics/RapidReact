package frc.robot.rev;

import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class BlinkinLEDDriver 
{
    private Spark inner;

    public BlinkinLEDDriver(int index, BlinkinLEDMode defaultMode)
    {
        inner = new Spark(index);
        inner.enableDeadbandElimination(false);

        set(defaultMode);
    }

    public void set(BlinkinLEDMode mode)
    {
        inner.set(mode.getSparkValue());
    }
}
