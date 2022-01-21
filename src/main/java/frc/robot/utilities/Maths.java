package frc.robot.utilities;

public final class Maths 
{
    public static <T extends Comparable<T>> T clamp(T value, T minValue, T maxValue)
    {
        if(value.compareTo(minValue) < 0)
        {
            return minValue;
        }

        if(value.compareTo(maxValue) > 0)
        {
            return maxValue;
        }

        return value;
    }
}
