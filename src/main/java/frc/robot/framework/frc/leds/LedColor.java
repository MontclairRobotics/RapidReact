package frc.robot.framework.frc.leds;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public interface LedColor 
{
    public static Color rgb(double r, double g, double b)
    {
        return new Color(r, g, b);
    }
    public static Color hsv(double h, double s, double v)
    {
        return Color.fromHSV((int)(h * 255), (int)(s * 255), (int)(v * 255));
    }
}
