package frc.robot.utils;

public final class ArrayUtils 
{
    public static <T> void appendFront(T value, T[] array, T[] targetArray)
    {
        targetArray[0] = value;
        for (int i = 0; i < array.length; i++)
        {
            targetArray[i+1] = array[i];
        }
    }
}
