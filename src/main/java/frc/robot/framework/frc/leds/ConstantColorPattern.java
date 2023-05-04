package frc.robot.framework.frc.leds;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class ConstantColorPattern extends LedPattern
{
    private final Color color;
    public ConstantColorPattern(Color color)
    {
        this.color = color;
    }

    @Override
    public void processInto(AddressableLEDBuffer led, int index, double deltaTime) 
    {
        led.setLED(index, color);
    }
}
