package frc.robot.framework.frc.leds;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public abstract class LedPattern 
{
    public abstract void processInto(AddressableLEDBuffer led, int index, double deltaTime);
}
